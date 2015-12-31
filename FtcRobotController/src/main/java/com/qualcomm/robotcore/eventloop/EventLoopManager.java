/*
 * Copyright (c) 2014, 2015 Qualcomm Technologies Inc
 *
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted
 * (subject to the limitations in the disclaimer below) provided that the following conditions are
 * met:
 *
 * Redistributions of source code must retain the above copyright notice, this list of conditions
 * and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this list of conditions
 * and the following disclaimer in the documentation and/or other materials provided with the
 * distribution.
 *
 * Neither the name of Qualcomm Technologies Inc nor the names of its contributors may be used to
 * endorse or promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS LICENSE. THIS
 * SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS
 * FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA,
 * OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF
 * THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

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

/**
 * Event Loop Manager
 * <p/>
 * Takes RobocolDatagram messages, converts them into the appropriate data type, and then passes it to the current EventLoop.
 */
public class EventLoopManager {
    public static final String RC_BATTERY_LEVEL_KEY = "RobotController Battery Level";
    public static final String ROBOT_BATTERY_LEVEL_KEY = "Robot Battery Level";
    public static final String SYSTEM_TELEMETRY = "SYSTEM_TELEMETRY";
    private static final EventLoop EVENT_LOOP = new InactiveEventLoop();
    private Thread EventLoopThread = new Thread();
    private Thread sendCommandThread = new Thread();
    private final RobocolDatagramSocket socket;
    private boolean isRunning;
    private ElapsedTime elapsedTime = new ElapsedTime();
    private EventLoop eventLoop = EVENT_LOOP;
    private final Gamepad[] gamepads = {new Gamepad(), new Gamepad()};
    private Heartbeat heartbeat = new Heartbeat(Token.EMPTY);
    private EventLoopMonitor monitor;
    private final Set<SyncdDevice> syncdDevices = new CopyOnWriteArraySet<SyncdDevice>();
    private final Command[] recvCommandCache = new Command[8];
    private int recvCommandCachePosition;
    private final Set<Command> sendCommandCache = new CopyOnWriteArraySet<Command>();
    private InetAddress inetAddress;
    public RobotState state = RobotState.NOT_STARTED;

    /**
     * Callback to monitor when event loop changes state
     */
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
        final EventLoopManager eventLoopManager;

        private EventLoopRunnable(EventLoopManager eventLoopManager) {
            this.eventLoopManager = eventLoopManager;
        }

        public void run() {
            RobotLog.v("EventLoopRunnable has started");
            try {
                ElapsedTime elapsedTime = new ElapsedTime();
                while (!Thread.interrupted()) {
                    while (elapsedTime.time() < 0.001) {
                        Thread.sleep(5);
                    }
                    elapsedTime.reset();
                    if (RobotLog.hasGlobalErrorMsg()) {
                        this.eventLoopManager.buildAndSendTelemetry(EventLoopManager.SYSTEM_TELEMETRY, RobotLog.getGlobalErrorMsg());
                    }
                    if (this.eventLoopManager.elapsedTime.startTime() == 0.) {
                        Thread.sleep(500);
                    } else if (this.eventLoopManager.elapsedTime.time() > 2) {
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
                        RobotLog.setGlobalErrorMsg("User code threw an uncaught exception: " +
                                (e.getClass().getSimpleName() + (e.getMessage() != null ? " - " + e.getMessage() : "")));
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
                    if (elapsedTime.startTime() == 0) {
                        Thread.sleep(500);
                    } else if (elapsedTime.time() > 2) {
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

    public void handleDroppedConnection() {
        OpModeManager opModeManager = this.eventLoop.getOpModeManager();
        String message = "Lost connection while running op mode: " + opModeManager.getActiveOpModeName();
        resetGamepads();
        opModeManager.initActiveOpMode(OpModeManager.DEFAULT_OP_MODE_NAME);
        setState(RobotState.DROPPED_CONNECTION);
        RobotLog.i(message);
    }

    /**
     * Constructor
     *
     * @param socket socket for IO with remote device
     */
    public EventLoopManager(RobocolDatagramSocket socket) {
        this.socket = socket;
        setState(RobotState.NOT_STARTED);
    }

    /**
     * Set a monitor for this event loop
     *
     * @param monitor event loop monitor
     */
    public void setMonitor(EventLoopMonitor monitor) {
        this.monitor = monitor;
    }

    /**
     * Start the event processor
     *
     * @param eventLoop set initial event loop
     * @throws RobotCoreException if event loop fails to init
     */
    public void start(EventLoop eventLoop) throws RobotCoreException {
        this.isRunning = false;
        setEventLoop(eventLoop);
        this.sendCommandThread = new Thread(new SendCommandRunnable(this), "Scheduled Sends");
        this.sendCommandThread.start();
        new Thread(new ProcessEventRunnable(this)).start();
    }

    /**
     * Shut down the event processor
     */
    public void shutdown() {
        this.socket.close();
        this.sendCommandThread.interrupt();
        this.isRunning = true;
        stopRobot();
    }

    /**
     * Register a sync'd device
     *
     * @param device sync'd device
     */
    public void registerSyncdDevice(SyncdDevice device) {
        this.syncdDevices.add(device);
    }

    /**
     * Unregister a sync'd device
     *
     * @param device sync'd device
     */
    public void unregisterSyncdDevice(SyncdDevice device) {
        this.syncdDevices.remove(device);
    }

    /**
     * Replace the current event loop with a new event loop
     *
     * @param eventLoop new event loop
     * @throws RobotCoreException if event loop fails to init
     */
    public void setEventLoop(EventLoop eventLoop) throws RobotCoreException {
        if (eventLoop == null) {
            eventLoop = EVENT_LOOP;
            RobotLog.d("Event loop cannot be null, using empty event loop");
        }
        stopRobot();
        this.eventLoop = eventLoop;
        startRobot();
    }

    /**
     * Get the current event loop
     *
     * @return current event loop
     */
    public EventLoop getEventLoop() {
        return this.eventLoop;
    }

    /**
     * Get the current gamepad state
     * <p/>
     * Port 0 is assumed
     *
     * @return gamepad
     */
    public Gamepad getGamepad() {
        return getGamepad(0);
    }

    /**
     * Get the gamepad connected to a particular user
     *
     * @param port port - user 0 and 1 are valid
     * @return gamepad
     */
    public Gamepad getGamepad(int port) {
        Range.throwIfRangeIsInvalid((double) port, 0, Servo.MAX_POSITION);
        return this.gamepads[port];
    }

    /**
     * Get the gamepads
     * <p/>
     * Array index will match the user number
     *
     * @return gamepad
     */
    public Gamepad[] getGamepads() {
        return this.gamepads;
    }

    public void resetGamepads() {
        for (Gamepad reset : this.gamepads) {
            reset.reset();
        }
    }

    /**
     * Get the current heartbeat state
     *
     * @return heartbeat
     */
    public Heartbeat getHeartbeat() {
        return this.heartbeat;
    }

    /**
     * Send telemetry data
     * <p/>
     * Send the telemetry data, and then clear the sent data
     *
     * @param telemetry telemetry data
     */
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
            RobotLog.d("Gamepad with invalid user received. Only users 1 and 2 are valid");
            return;
        }
        int port = gamepad.user - 1;
        this.gamepads[port].copy(gamepad);
        if (this.gamepads[0].id == this.gamepads[1].id) {
            RobotLog.v("Gamepad moved position, removing stale gamepad");
            if (port == 0) {
                this.gamepads[1].copy(new Gamepad());
            }
            if (port == 1) {
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

        for (Command recvCommand : this.recvCommandCache) {
            if ((recvCommand != null) && recvCommand.equals(command)) {
                return;
            }
        }

        int cachePosition = this.recvCommandCachePosition;
        this.recvCommandCache[cachePosition % this.recvCommandCache.length] = command;
        this.recvCommandCachePosition++;
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
