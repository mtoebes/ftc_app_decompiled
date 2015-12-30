package com.qualcomm.robotcore.eventloop;

import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robocol.Command;
import com.qualcomm.robotcore.robocol.Heartbeat;
import com.qualcomm.robotcore.robocol.Heartbeat.Token;
import com.qualcomm.robotcore.robocol.PeerDiscovery;
import com.qualcomm.robotcore.robocol.PeerDiscovery.PeerType;
import com.qualcomm.robotcore.robocol.RobocolDatagram;
import com.qualcomm.robotcore.robocol.RobocolDatagramSocket;
import com.qualcomm.robotcore.robocol.RobocolParsable;
import com.qualcomm.robotcore.robocol.Telemetry;
import com.qualcomm.robotcore.robot.RobotState;
import com.qualcomm.robotcore.util.ElapsedTime;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.util.RobotLog;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class EventLoopManager {
    public static final String RC_BATTERY_LEVEL_KEY = "RobotController Battery Level";
    public static final String ROBOT_BATTERY_LEVEL_KEY = "Robot Battery Level";
    public static final String SYSTEM_TELEMETRY = "SYSTEM_TELEMETRY";
    private static final EventLoop EVENT_LOOP;
    private Thread EventLoopThread;
    private Thread sendCommandThread;
    private final RobocolDatagramSocket socket;
    private boolean isRunning;
    private ElapsedTime elapsedTime;
    private EventLoop eventLoop;
    private final Gamepad[] gamepads;
    private Heartbeat heartbeat;
    private EventLoopMonitor monitor;
    private final Set<SyncdDevice> syncdDevices;
    private final Command[] recvCommandCache;
    private int recvCommandCachePosition;
    private final Set<Command> sendCommandCache;
    private InetAddress inetAddress;
    public RobotState state;

    public interface EventLoopMonitor {
        void onStateChange(RobotState robotState);
    }

    public enum State {
        NOT_STARTED,
        INIT,
        RUNNING,
        STOPPED,
        EMERGENCY_STOP,
        DROPPED_CONNECTION
    }

    /* renamed from: com.qualcomm.robotcore.eventloop.EventLoopManager.a */
    private static class InactiveEventLoop implements EventLoop {
        private InactiveEventLoop() {
        }

        public void init(EventLoopManager eventProcessor) {
        }

        public void loop() {
        }

        public void teardown() {
        }

        public void processCommand(Command command) {
            RobotLog.w("Dropping command " + command.getName() + ", no active event loop");
        }

        public OpModeManager getOpModeManager() {
            return null;
        }
    }

    private class EventLoopRunnable implements Runnable {
        final EventLoopManager eventLoopManager;

        private EventLoopRunnable(EventLoopManager eventLoopManager) {
            this.eventLoopManager = eventLoopManager;
        }

        public void run() {
            RobotLog.v("EventLoopRunnable has started");
            try {
                ElapsedTime elapsedTime = new ElapsedTime();
                while (!Thread.interrupted()) {
                    while (elapsedTime.time() < 0.001d) {
                        Thread.sleep(5);
                    }
                    elapsedTime.reset();
                    if (RobotLog.hasGlobalErrorMsg()) {
                        this.eventLoopManager.buildAndSendTelemetry(EventLoopManager.SYSTEM_TELEMETRY, RobotLog.getGlobalErrorMsg());
                    }
                    if (this.eventLoopManager.elapsedTime.startTime() == 0.0d) {
                        Thread.sleep(500);
                    } else if (this.eventLoopManager.elapsedTime.time() > 2.0d) {
                        this.eventLoopManager.handleDroppedConnection();
                        this.eventLoopManager.inetAddress = null;
                        this.eventLoopManager.elapsedTime = new ElapsedTime(0);
                    }
                    for (SyncdDevice blockUntilReady : this.eventLoopManager.syncdDevices) {
                        blockUntilReady.blockUntilReady();
                    }
                    try {
                        this.eventLoopManager.eventLoop.loop();
                        for (SyncdDevice blockUntilReady : this.eventLoopManager.syncdDevices) {
                            blockUntilReady.startBlockingWork();
                        }
                    } catch (Exception e) {
                        RobotLog.e("Event loop threw an exception");
                        RobotLog.logStacktrace(e);
                        RobotLog.setGlobalErrorMsg("User code threw an uncaught exception: " + (e.getClass().getSimpleName() + (e.getMessage() != null ? " - " + e.getMessage() : "")));
                        this.eventLoopManager.buildAndSendTelemetry(EventLoopManager.SYSTEM_TELEMETRY, RobotLog.getGlobalErrorMsg());
                        throw new RobotCoreException("EventLoop Exception in loop()");
                    } catch (Throwable th) {
                        for (SyncdDevice blockUntilReady : this.eventLoopManager.syncdDevices) {
                            blockUntilReady.startBlockingWork();
                        }
                    }
                }
            } catch (InterruptedException e2) {
                RobotLog.v("EventLoopRunnable interrupted");
                this.eventLoopManager.setState(RobotState.STOPPED);
            } catch (RobotCoreException e3) {
                RobotLog.v("RobotCoreException in EventLoopManager: " + e3.getMessage());
                this.eventLoopManager.setState(RobotState.EMERGENCY_STOP);
                this.eventLoopManager.buildAndSendTelemetry(EventLoopManager.SYSTEM_TELEMETRY, RobotLog.getGlobalErrorMsg());
            }
            try {
                this.eventLoopManager.eventLoop.teardown();
            } catch (Exception e4) {
                RobotLog.w("Caught exception during looper teardown: " + e4.toString());
                RobotLog.logStacktrace(e4);
                if (RobotLog.hasGlobalErrorMsg()) {
                    this.eventLoopManager.buildAndSendTelemetry(EventLoopManager.SYSTEM_TELEMETRY, RobotLog.getGlobalErrorMsg());
                }
            }
            RobotLog.v("EventLoopRunnable has exited");
        }
    }

    private class ProcessEventRunnable implements Runnable {
        ElapsedTime time;
        final EventLoopManager eventLoopManager;

        private ProcessEventRunnable(EventLoopManager eventLoopManager) {
            this.eventLoopManager = eventLoopManager;
            this.time = new ElapsedTime();
        }

        public void run() {
            RobotLog.v("EventLoopRunnable has started");
            try {
                ElapsedTime runnableElapseTime = new ElapsedTime();

                while (!Thread.interrupted()) {
                    while (runnableElapseTime.time() < 0.001) {
                        Thread.sleep(5);
                    }
                    runnableElapseTime.reset();
                    if (RobotLog.hasGlobalErrorMsg()) {
                        buildAndSendTelemetry(EventLoopManager.SYSTEM_TELEMETRY, RobotLog.getGlobalErrorMsg());
                    }
                    if (elapsedTime.startTime() == 0.0) {
                        Thread.sleep(500);
                    } else if (elapsedTime.time() > 2.0) {
                        handleDroppedConnection();
                        elapsedTime = new ElapsedTime(0);
                    }
                    for (SyncdDevice syncdDevice : syncdDevices) {
                        syncdDevice.blockUntilReady();
                    }

                    try {
                        eventLoop.loop();
                    } catch (Exception e) {
                        RobotLog.e("Event loop threw an exception");
                        RobotLog.logStacktrace(e);
                        RobotLog.setGlobalErrorMsg("User code threw an uncaught exception: " + (e.getClass().getSimpleName() + (e.getMessage() != null ? " - " + e.getMessage() : "")));
                        buildAndSendTelemetry(EventLoopManager.SYSTEM_TELEMETRY, RobotLog.getGlobalErrorMsg());
                        throw new RobotCoreException("EventLoop Exception in loop()");
                    } finally {
                        for (SyncdDevice syncDevice : syncdDevices) {
                            syncDevice.startBlockingWork();
                        }
                    }
                }

            } catch (InterruptedException e2) {
                RobotLog.v("EventLoopRunnable interrupted");
                setState(RobotState.STOPPED);
            } catch (RobotCoreException e3) {
                RobotLog.v("RobotCoreException in EventLoopManager: " + e3.getMessage());
                setState(RobotState.EMERGENCY_STOP);
                buildAndSendTelemetry(EventLoopManager.SYSTEM_TELEMETRY, RobotLog.getGlobalErrorMsg());
            }
            try {
                eventLoop.teardown();
            } catch (Exception e4) {
                RobotLog.w("Caught exception during looper teardown: " + e4.toString());
                RobotLog.logStacktrace(e4);
                if (RobotLog.hasGlobalErrorMsg()) {
                    buildAndSendTelemetry(EventLoopManager.SYSTEM_TELEMETRY, RobotLog.getGlobalErrorMsg());
                }
            }
            RobotLog.v("EventLoopRunnable has exited");
        }
    }

    private class SendCommandRunnable implements Runnable {
        final EventLoopManager eventLoopManager;
        private Set<Command> sentCommands;

        private SendCommandRunnable(EventLoopManager eventLoopManager) {
            this.eventLoopManager = eventLoopManager;
            this.sentCommands = new HashSet<Command>();
        }

        public void run() {
            while (!Thread.interrupted()) {
                for (Command command : this.eventLoopManager.sendCommandCache) {
                    if (command.getAttempts() > 10) {
                        RobotLog.w("Failed to send command, too many attempts: " + command.toString());
                        this.sentCommands.add(command);
                    } else if (command.isAcknowledged()) {
                        RobotLog.v("Command " + command.getName() + " has been acknowledged by remote device");
                        this.sentCommands.add(command);
                    } else {
                        try {
                            RobotLog.v("Sending command: " + command.getName() + ", attempt " + command.getAttempts());
                            this.eventLoopManager.socket.send(new RobocolDatagram(command.toByteArray()));
                        } catch (RobotCoreException e) {
                            RobotLog.w("Failed to send command " + command.getName());
                            RobotLog.logStacktrace(e);
                        }
                    }
                }
                this.eventLoopManager.sendCommandCache.removeAll(this.sentCommands);
                this.sentCommands.clear();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e2) {
                    return;
                }
            }
        }
    }

    static {
        EVENT_LOOP = new InactiveEventLoop();
    }

    public void handleDroppedConnection() {
        OpModeManager opModeManager = this.eventLoop.getOpModeManager();
        String message = "Lost connection while running op mode: " + opModeManager.getActiveOpModeName();
        resetGamepads();
        opModeManager.initActiveOpMode(OpModeManager.DEFAULT_OP_MODE_NAME);
        setState(RobotState.DROPPED_CONNECTION);
        RobotLog.i(message);
    }

    public EventLoopManager(RobocolDatagramSocket socket) {
        this.state = RobotState.NOT_STARTED;
        this.EventLoopThread = new Thread();
        this.sendCommandThread = new Thread();
        this.isRunning = false;
        this.elapsedTime = new ElapsedTime();
        this.eventLoop = EVENT_LOOP;
        this.gamepads = new Gamepad[]{new Gamepad(), new Gamepad()};
        this.heartbeat = new Heartbeat(Token.EMPTY);
        this.monitor = null;
        this.syncdDevices = new CopyOnWriteArraySet<SyncdDevice>();
        this.recvCommandCache = new Command[8];
        this.recvCommandCachePosition = 0;
        this.sendCommandCache = new CopyOnWriteArraySet<Command>();
        this.socket = socket;
        setState(RobotState.NOT_STARTED);
    }

    public void setMonitor(EventLoopMonitor monitor) {
        this.monitor = monitor;
    }

    public void start(EventLoop eventLoop) throws RobotCoreException {
        this.isRunning = false;
        setEventLoop(eventLoop);
        this.sendCommandThread = new Thread(new SendCommandRunnable(this), "Scheduled Sends");
        this.sendCommandThread.start();
        new Thread(new ProcessEventRunnable(this)).start();
    }

    public void shutdown() {
        this.socket.close();
        this.sendCommandThread.interrupt();
        this.isRunning = true;
        stopRobot();
    }

    public void registerSyncdDevice(SyncdDevice device) {
        this.syncdDevices.add(device);
    }

    public void unregisterSyncdDevice(SyncdDevice device) {
        this.syncdDevices.remove(device);
    }

    public void setEventLoop(EventLoop eventLoop) throws RobotCoreException {
        if (eventLoop == null) {
            eventLoop = EVENT_LOOP;
            RobotLog.d("Event loop cannot be null, using empty event loop");
        }
        stopRobot();
        this.eventLoop = eventLoop;
        startRobot();
    }

    public EventLoop getEventLoop() {
        return this.eventLoop;
    }

    public Gamepad getGamepad() {
        return getGamepad(0);
    }

    public Gamepad getGamepad(int port) {
        Range.throwIfRangeIsInvalid((double) port, 0, Servo.MAX_POSITION);
        return this.gamepads[port];
    }

    public Gamepad[] getGamepads() {
        return this.gamepads;
    }

    public void resetGamepads() {
        for (Gamepad reset : this.gamepads) {
            reset.reset();
        }
    }

    public Heartbeat getHeartbeat() {
        return this.heartbeat;
    }

    public void sendTelemetryData(Telemetry telemetry) {
        try {
            this.socket.send(new RobocolDatagram(telemetry.toByteArray()));
        } catch (RobotCoreException e) {
            RobotLog.w("Failed to send telemetry data");
            RobotLog.logStacktrace(e);
        }
        telemetry.clearData();
    }

    public void sendCommand(Command command) {
        this.sendCommandCache.add(command);
    }

    private void startRobot() throws RobotCoreException {
        try {
            setState(RobotState.INIT);
            this.eventLoop.init(this);
            for (SyncdDevice startBlockingWork : this.syncdDevices) {
                startBlockingWork.startBlockingWork();
            }
            this.elapsedTime = new ElapsedTime(0);
            setState(RobotState.RUNNING);
            this.EventLoopThread = new Thread(new EventLoopRunnable(this), "Event Loop");
            this.EventLoopThread.start();
        } catch (Exception e) {
            RobotLog.w("Caught exception during looper init: " + e.toString());
            RobotLog.logStacktrace(e);
            setState(RobotState.EMERGENCY_STOP);
            if (RobotLog.hasGlobalErrorMsg()) {
                buildAndSendTelemetry(SYSTEM_TELEMETRY, RobotLog.getGlobalErrorMsg());
            }
            throw new RobotCoreException("Robot failed to start: " + e.getMessage());
        }
    }

    private void stopRobot() {
        this.EventLoopThread.interrupt();
        try {
            Thread.sleep(200);
        } catch (InterruptedException ignored) {
        }
        setState(RobotState.STOPPED);
        this.eventLoop = EVENT_LOOP;
        this.syncdDevices.clear();
    }

    private void setState(RobotState robotState) {
        this.state = robotState;
        RobotLog.v("EventLoopManager state is " + robotState.toString());
        if (this.monitor != null) {
            this.monitor.onStateChange(robotState);
        }
    }

    private void processGamepadEvent(RobocolDatagram robocolDatagram) throws RobotCoreException {
        Gamepad gamepad = new Gamepad();
        gamepad.fromByteArray(robocolDatagram.getData());
        if ((gamepad.user < (byte) 1) || (gamepad.user > 2)) {
            RobotLog.d("Gamepad with user %d received. Only users 1 and 2 are valid");
            return;
        }
        int userIndex = gamepad.user - 1;
        this.gamepads[userIndex].copy(gamepad);
        if (this.gamepads[0].id == this.gamepads[1].id) {
            RobotLog.v("Gamepad moved position, removing stale gamepad");
            if (userIndex == 0) {
                this.gamepads[1].copy(new Gamepad());
            }
            if (userIndex == 1) {
                this.gamepads[0].copy(new Gamepad());
            }
        }
    }

    private void processHeartbeatEvent(RobocolDatagram robocolDatagram) throws RobotCoreException {
        Heartbeat heartbeat = new Heartbeat(Token.EMPTY);
        heartbeat.fromByteArray(robocolDatagram.getData());
        heartbeat.setRobotState(this.state);
        robocolDatagram.setData(heartbeat.toByteArray());
        this.socket.send(robocolDatagram);
        this.elapsedTime.reset();
        this.heartbeat = heartbeat;
    }

    private void processPeerDiscoveryEvent(RobocolDatagram robocolDatagram) throws RobotCoreException {
        if (!robocolDatagram.getAddress().equals(this.inetAddress)) {
            if (this.state == RobotState.DROPPED_CONNECTION) {
                setState(RobotState.RUNNING);
            }
            if (this.eventLoop != EVENT_LOOP) {
                this.inetAddress = robocolDatagram.getAddress();
                RobotLog.i("new remote peer discovered: " + this.inetAddress.getHostAddress());
                try {
                    this.socket.connect(this.inetAddress);
                } catch (SocketException e) {
                    RobotLog.e("Unable to connect to peer:" + e.toString());
                }
                RobocolParsable peerDiscovery = new PeerDiscovery(PeerType.PEER);
                RobotLog.v("Sending peer discovery packet");
                RobocolDatagram peerRobocolDatagram = new RobocolDatagram(peerDiscovery);
                if (this.socket.getInetAddress() == null) {
                    peerRobocolDatagram.setAddress(this.inetAddress);
                }
                this.socket.send(peerRobocolDatagram);
            }
        }
    }

    private void processCommandEvent(RobocolDatagram robocolDatagram) throws RobotCoreException {
        Command command = new Command(robocolDatagram.getData());
        if (command.isAcknowledged()) {
            this.sendCommandCache.remove(command);
            return;
        }
        command.acknowledge();
        this.socket.send(new RobocolDatagram(command));
        Command[] commandArr = this.recvCommandCache;
        int length = commandArr.length;
        int i = 0;
        while (i < length) {
            Command command2 = commandArr[i];
            if ((command2 == null) || !command2.equals(command)) {
                i++;
            } else {
                return;
            }
        }
        Command[] commandArr2 = this.recvCommandCache;
        int i2 = this.recvCommandCachePosition;
        this.recvCommandCachePosition = i2 + 1;
        commandArr2[i2 % this.recvCommandCache.length] = command;
        try {
            this.eventLoop.processCommand(command);
        } catch (Exception e) {
            RobotLog.e("Event loop threw an exception while processing a command");
            RobotLog.logStacktrace(e);
        }
    }

    private void processUnknownEvent(RobocolDatagram robocolDatagram) {
        RobotLog.w("RobotCore event loop received unknown event type: " + robocolDatagram.getMsgType().name());
    }

    public void buildAndSendTelemetry(String tag, String msg) {
        Telemetry telemetry = new Telemetry();
        telemetry.setTag(tag);
        telemetry.addData(tag, msg);
        sendTelemetryData(telemetry);
    }
}
