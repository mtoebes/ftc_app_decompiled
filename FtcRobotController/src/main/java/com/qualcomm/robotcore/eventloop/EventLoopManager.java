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
    private Thread f184b;
    private Thread f185c;
    private final RobocolDatagramSocket socket;
    private boolean isRunning;
    private ElapsedTime elapsedTime;
    private EventLoop eventLoop;
    private final Gamepad[] gamepads;
    private Heartbeat heartbeat;
    private EventLoopMonitor eventLoopMonitor;
    private final Set<SyncdDevice> syncDevices;
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
                    for (SyncdDevice syncdDevice : syncDevices) {
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
                        for (SyncdDevice syncDevice : syncDevices) {
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

    /* renamed from: com.qualcomm.robotcore.eventloop.EventLoopManager.c */
    private class ProcessEventRunnable implements Runnable {

        public void run() {
            while(true) {
                RobocolDatagram message = socket.recv();
                if(!isRunning || socket.isClosed()) {
                    return;
                }

                if(message == null) {
                    Thread.yield();
                } else {
                    if(RobotLog.hasGlobalErrorMsg()) {
                        buildAndSendTelemetry("SYSTEM_TELEMETRY", RobotLog.getGlobalErrorMsg());
                    }

                    try {
                        switch(message.getMsgType()) {
                            case EMPTY:
                                processEmptyEvent();
                                break;
                            case HEARTBEAT:
                                processHeartbeatEvent(message);
                                break;
                            case GAMEPAD:
                                processGamepadEvent(message);
                                break;
                            case PEER_DISCOVERY:
                                processPeerDiscoveryEvent(message);
                                break;
                            case COMMAND:
                                processCommandEvent(message);
                                break;
                            case TELEMETRY:
                                processEmptyEvent();
                                break;
                            default:
                                processUnknownEvent(message);
                        }
                    } catch (RobotCoreException var3) {
                        RobotLog.w("RobotCore event loop cannot process event: " + var3.toString());
                    }
                }
            }
        }
    }

    private class SendCommandRunnable implements Runnable {
        private Set<Command> sentCommands = new HashSet<Command>();

        public void run() {
            while (!Thread.interrupted()) {
                for (Command command : sendCommandCache) {
                    if (command.getAttempts() > 10) {
                        RobotLog.w("Failed to send command, too many attempts: " + command.toString());
                        sentCommands.add(command);
                    } else if (command.isAcknowledged()) {
                        RobotLog.v("Command " + command.getName() + " has been acknowledged by remote device");
                        sentCommands.add(command);
                    } else {
                        try {
                            RobotLog.v("Sending command: " + command.getName() + ", attempt " + command.getAttempts());
                            socket.send(new RobocolDatagram(command.toByteArray()));
                        } catch (RobotCoreException e) {
                            RobotLog.w("Failed to send command " + command.getName());
                            RobotLog.logStacktrace(e);
                        }
                    }
                }
                sendCommandCache.removeAll(sentCommands);
                sentCommands.clear();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException interruptException) {
                    return;
                }
            }
        }
    }

    static {
        EVENT_LOOP = new InactiveEventLoop();
    }

    public void handleDroppedConnection() {
        OpModeManager opModeManager = eventLoop.getOpModeManager();
        String str = "Lost connection while running op mode: " + opModeManager.getActiveOpModeName();
        resetGamepads();
        opModeManager.initActiveOpMode(OpModeManager.DEFAULT_OP_MODE_NAME);
        setState(RobotState.DROPPED_CONNECTION);
        RobotLog.i(str);
    }

    public EventLoopManager(RobocolDatagramSocket socket) {
        state = RobotState.NOT_STARTED;
        f184b = new Thread();
        f185c = new Thread();
        elapsedTime = new ElapsedTime();
        eventLoop = EVENT_LOOP;
        gamepads = new Gamepad[]{new Gamepad(), new Gamepad()};
        heartbeat = new Heartbeat(Token.EMPTY);
        eventLoopMonitor = null;
        syncDevices = new CopyOnWriteArraySet<SyncdDevice>();
        recvCommandCache = new Command[8];
        recvCommandCachePosition = 0;
        sendCommandCache = new CopyOnWriteArraySet<Command>();
        this.socket = socket;
        setState(RobotState.NOT_STARTED);
    }

    public void setMonitor(EventLoopMonitor monitor) {
        eventLoopMonitor = monitor;
    }

    public void start(EventLoop eventLoop) throws RobotCoreException {
        isRunning = true;
        setEventLoop(eventLoop);
        f185c = new Thread(new SendCommandRunnable(), "Scheduled Sends");
        f185c.start();
        new Thread(new ProcessEventRunnable()).start();
    }

    public void shutdown() {
        isRunning = false;
        socket.close();
        f185c.interrupt();
        stopRobot();
    }

    public void registerSyncdDevice(SyncdDevice device) {
        syncDevices.add(device);
    }

    public void unregisterSyncdDevice(SyncdDevice device) {
        syncDevices.remove(device);
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
        return eventLoop;
    }

    public Gamepad getGamepad() {
        return getGamepad(0);
    }

    public Gamepad getGamepad(int port) {
        Range.throwIfRangeIsInvalid((double) port, 0.0d, Servo.MAX_POSITION);
        return gamepads[port];
    }

    public Gamepad[] getGamepads() {
        return gamepads;
    }

    public void resetGamepads() {
        for (Gamepad reset : gamepads) {
            reset.reset();
        }
    }

    public Heartbeat getHeartbeat() {
        return heartbeat;
    }

    public void sendTelemetryData(Telemetry telemetry) {
        try {
            socket.send(new RobocolDatagram(telemetry.toByteArray()));
        } catch (RobotCoreException e) {
            RobotLog.w("Failed to send telemetry data");
            RobotLog.logStacktrace(e);
        }
        telemetry.clearData();
    }

    public void sendCommand(Command command) {
        sendCommandCache.add(command);
    }

    private void startRobot() throws RobotCoreException {
        try {
            setState(RobotState.INIT);
            eventLoop.init(this);
            for (SyncdDevice startBlockingWork : syncDevices) {
                startBlockingWork.startBlockingWork();
            }
            elapsedTime = new ElapsedTime(0);
            setState(RobotState.RUNNING);
            f184b = new Thread(new EventLoopRunnable(), "Event Loop");
            f184b.start();
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
        f184b.interrupt();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            // Do nothing
        }
        setState(RobotState.STOPPED);
        eventLoop = EVENT_LOOP;
        syncDevices.clear();
    }

    private void setState(RobotState robotState) {
        state = robotState;
        RobotLog.v("EventLoopManager state is " + robotState.toString());
        if (eventLoopMonitor != null) {
            eventLoopMonitor.onStateChange(robotState);
        }
    }

    private void processGamepadEvent(RobocolDatagram message) throws RobotCoreException {
        Gamepad gamepad = new Gamepad();
        gamepad.fromByteArray(message.getData());
        if (gamepad.user < (byte) 1 || gamepad.user > 2) {
            RobotLog.d("Gamepad with user %d received. Only users 1 and 2 are valid");
            return;
        }
        int i = gamepad.user - 1;
        gamepads[i].copy(gamepad);
        if (gamepads[0].id == gamepads[1].id) {
            RobotLog.v("Gamepad moved position, removing stale gamepad");
            if (i == 0) {
                gamepads[1].copy(new Gamepad());
            }
            if (i == 1) {
                gamepads[0].copy(new Gamepad());
            }
        }
    }

    private void processHeartbeatEvent(RobocolDatagram message) throws RobotCoreException {
        Heartbeat heartbeat = new Heartbeat(Token.EMPTY);
        heartbeat.fromByteArray(message.getData());
        heartbeat.setRobotState(state);
        message.setData(heartbeat.toByteArray());
        socket.send(message);
        elapsedTime.reset();
        this.heartbeat = heartbeat;
    }

    private void processPeerDiscoveryEvent(RobocolDatagram message) throws RobotCoreException {
        if (!message.getAddress().equals(inetAddress)) {
            if (state == RobotState.DROPPED_CONNECTION) {
                setState(RobotState.RUNNING);
            }
            if (eventLoop != EVENT_LOOP) {
                inetAddress = message.getAddress();
                RobotLog.i("new remote peer discovered: " + inetAddress.getHostAddress());
                try {
                    socket.connect(inetAddress);
                } catch (SocketException socketException) {
                    RobotLog.e("Unable to connect to peer:" + socketException.toString());
                }
                RobocolParsable peerDiscovery = new PeerDiscovery(PeerType.PEER);
                RobotLog.v("Sending peer discovery packet");
                RobocolDatagram robocolDatagram2 = new RobocolDatagram(peerDiscovery);
                if (socket.getInetAddress() == null) {
                    robocolDatagram2.setAddress(inetAddress);
                }
                socket.send(robocolDatagram2);
            }
        }
    }

    private void processCommandEvent(RobocolDatagram message) throws RobotCoreException {
        Command command = new Command(message.getData());

        if (command.isAcknowledged()) {
            sendCommandCache.remove(command);
            return;
        }

        command.acknowledge();
        socket.send(new RobocolDatagram(command));

        // Check if command is already cached
        for(Command commandCached : recvCommandCache) {
            if (commandCached != null && commandCached.equals(command)) {
                return;
            }
        }

        recvCommandCachePosition++;
        recvCommandCache[recvCommandCachePosition % recvCommandCache.length] = command;

        try {
            eventLoop.processCommand(command);
        } catch (Exception e) {
            RobotLog.e("Event loop threw an exception while processing a command");
            RobotLog.logStacktrace(e);
        }
    }

    private void processEmptyEvent() {
    }

    private void processUnknownEvent(RobocolDatagram message) {
        RobotLog.w("RobotCore event loop received unknown event type: " + message.getMsgType().name());
    }

    public void buildAndSendTelemetry(String tag, String msg) {
        Telemetry telemetry = new Telemetry();
        telemetry.setTag(tag);
        telemetry.addData(tag, msg);
        sendTelemetryData(telemetry);
    }
}
