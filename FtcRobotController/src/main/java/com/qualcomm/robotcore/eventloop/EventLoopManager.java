package com.qualcomm.robotcore.eventloop;

import com.qualcomm.robotcore.BuildConfig;
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
import com.qualcomm.robotcore.robocol.RobocolParsable.MsgType;
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
    private static final EventLoop f183a;
    private Thread f184b;
    private Thread f185c;
    private final RobocolDatagramSocket f186d;
    private boolean f187e;
    private ElapsedTime f188f;
    private EventLoop f189g;
    private final Gamepad[] f190h;
    private Heartbeat f191i;
    private EventLoopMonitor f192j;
    private final Set<SyncdDevice> f193k;
    private final Command[] f194l;
    private int f195m;
    private final Set<Command> f196n;
    private InetAddress f197o;
    public RobotState state;

    /* renamed from: com.qualcomm.robotcore.eventloop.EventLoopManager.1 */
    static /* synthetic */ class C00261 {
        static final /* synthetic */ int[] f176a;

        static {
            f176a = new int[MsgType.values().length];
            try {
                f176a[MsgType.GAMEPAD.ordinal()] = 1;
            } catch (NoSuchFieldError e) {
            }
            try {
                f176a[MsgType.HEARTBEAT.ordinal()] = 2;
            } catch (NoSuchFieldError e2) {
            }
            try {
                f176a[MsgType.PEER_DISCOVERY.ordinal()] = 3;
            } catch (NoSuchFieldError e3) {
            }
            try {
                f176a[MsgType.COMMAND.ordinal()] = 4;
            } catch (NoSuchFieldError e4) {
            }
            try {
                f176a[MsgType.EMPTY.ordinal()] = 5;
            } catch (NoSuchFieldError e5) {
            }
        }
    }

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
    private static class C0027a implements EventLoop {
        private C0027a() {
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

    /* renamed from: com.qualcomm.robotcore.eventloop.EventLoopManager.b */
    private class C0028b implements Runnable {
        final /* synthetic */ EventLoopManager f178a;

        private C0028b(EventLoopManager eventLoopManager) {
            this.f178a = eventLoopManager;
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
                        this.f178a.buildAndSendTelemetry(EventLoopManager.SYSTEM_TELEMETRY, RobotLog.getGlobalErrorMsg());
                    }
                    if (this.f178a.f188f.startTime() == 0.0d) {
                        Thread.sleep(500);
                    } else if (this.f178a.f188f.time() > 2.0d) {
                        this.f178a.handleDroppedConnection();
                        this.f178a.f197o = null;
                        this.f178a.f188f = new ElapsedTime(0);
                    }
                    for (SyncdDevice blockUntilReady : this.f178a.f193k) {
                        blockUntilReady.blockUntilReady();
                    }
                    try {
                        this.f178a.f189g.loop();
                        for (SyncdDevice blockUntilReady2 : this.f178a.f193k) {
                            blockUntilReady2.startBlockingWork();
                        }
                    } catch (Exception e) {
                        RobotLog.e("Event loop threw an exception");
                        RobotLog.logStacktrace(e);
                        RobotLog.setGlobalErrorMsg("User code threw an uncaught exception: " + (e.getClass().getSimpleName() + (e.getMessage() != null ? " - " + e.getMessage() : BuildConfig.VERSION_NAME)));
                        this.f178a.buildAndSendTelemetry(EventLoopManager.SYSTEM_TELEMETRY, RobotLog.getGlobalErrorMsg());
                        throw new RobotCoreException("EventLoop Exception in loop()");
                    } catch (Throwable th) {
                        Throwable th2 = th;
                        for (SyncdDevice blockUntilReady22 : this.f178a.f193k) {
                            blockUntilReady22.startBlockingWork();
                        }
                    }
                }
            } catch (InterruptedException e2) {
                RobotLog.v("EventLoopRunnable interrupted");
                this.f178a.m164a(RobotState.STOPPED);
            } catch (RobotCoreException e3) {
                RobotLog.v("RobotCoreException in EventLoopManager: " + e3.getMessage());
                this.f178a.m164a(RobotState.EMERGENCY_STOP);
                this.f178a.buildAndSendTelemetry(EventLoopManager.SYSTEM_TELEMETRY, RobotLog.getGlobalErrorMsg());
            }
            try {
                this.f178a.f189g.teardown();
            } catch (Exception e4) {
                RobotLog.w("Caught exception during looper teardown: " + e4.toString());
                RobotLog.logStacktrace(e4);
                if (RobotLog.hasGlobalErrorMsg()) {
                    this.f178a.buildAndSendTelemetry(EventLoopManager.SYSTEM_TELEMETRY, RobotLog.getGlobalErrorMsg());
                }
            }
            RobotLog.v("EventLoopRunnable has exited");
        }
    }

    /* renamed from: com.qualcomm.robotcore.eventloop.EventLoopManager.c */
    private class C0029c implements Runnable {
        ElapsedTime f179a;
        final /* synthetic */ EventLoopManager f180b;

        private C0029c(EventLoopManager eventLoopManager) {
            this.f180b = eventLoopManager;
            this.f179a = new ElapsedTime();
        }

        /* JADX WARNING: inconsistent code. */
        /* Code decompiled incorrectly, please refer to instructions dump. */
        public void run() {
            /*
            r4 = this;
        L_0x0000:
            r0 = r4.f180b;
            r0 = r0.f186d;
            r0 = r0.recv();
            r1 = r4.f180b;
            r1 = r1.f187e;
            if (r1 != 0) goto L_0x001e;
        L_0x0012:
            r1 = r4.f180b;
            r1 = r1.f186d;
            r1 = r1.isClosed();
            if (r1 == 0) goto L_0x001f;
        L_0x001e:
            return;
        L_0x001f:
            if (r0 != 0) goto L_0x0025;
        L_0x0021:
            java.lang.Thread.yield();
            goto L_0x0000;
        L_0x0025:
            r1 = com.qualcomm.robotcore.util.RobotLog.hasGlobalErrorMsg();
            if (r1 == 0) goto L_0x0036;
        L_0x002b:
            r1 = r4.f180b;
            r2 = "SYSTEM_TELEMETRY";
            r3 = com.qualcomm.robotcore.util.RobotLog.getGlobalErrorMsg();
            r1.buildAndSendTelemetry(r2, r3);
        L_0x0036:
            r1 = com.qualcomm.robotcore.eventloop.EventLoopManager.C00261.f176a;	 Catch:{ RobotCoreException -> 0x004b }
            r2 = r0.getMsgType();	 Catch:{ RobotCoreException -> 0x004b }
            r2 = r2.ordinal();	 Catch:{ RobotCoreException -> 0x004b }
            r1 = r1[r2];	 Catch:{ RobotCoreException -> 0x004b }
            switch(r1) {
                case 1: goto L_0x0067;
                case 2: goto L_0x006d;
                case 3: goto L_0x0073;
                case 4: goto L_0x0079;
                case 5: goto L_0x007f;
                default: goto L_0x0045;
            };	 Catch:{ RobotCoreException -> 0x004b }
        L_0x0045:
            r1 = r4.f180b;	 Catch:{ RobotCoreException -> 0x004b }
            r1.m178e(r0);	 Catch:{ RobotCoreException -> 0x004b }
            goto L_0x0000;
        L_0x004b:
            r0 = move-exception;
            r1 = new java.lang.StringBuilder;
            r1.<init>();
            r2 = "RobotCore event loop cannot process event: ";
            r1 = r1.append(r2);
            r0 = r0.toString();
            r0 = r1.append(r0);
            r0 = r0.toString();
            com.qualcomm.robotcore.util.RobotLog.w(r0);
            goto L_0x0000;
        L_0x0067:
            r1 = r4.f180b;	 Catch:{ RobotCoreException -> 0x004b }
            r1.m163a(r0);	 Catch:{ RobotCoreException -> 0x004b }
            goto L_0x0000;
        L_0x006d:
            r1 = r4.f180b;	 Catch:{ RobotCoreException -> 0x004b }
            r1.m168b(r0);	 Catch:{ RobotCoreException -> 0x004b }
            goto L_0x0000;
        L_0x0073:
            r1 = r4.f180b;	 Catch:{ RobotCoreException -> 0x004b }
            r1.m171c(r0);	 Catch:{ RobotCoreException -> 0x004b }
            goto L_0x0000;
        L_0x0079:
            r1 = r4.f180b;	 Catch:{ RobotCoreException -> 0x004b }
            r1.m175d(r0);	 Catch:{ RobotCoreException -> 0x004b }
            goto L_0x0000;
        L_0x007f:
            r0 = r4.f180b;	 Catch:{ RobotCoreException -> 0x004b }
            r0.m169c();	 Catch:{ RobotCoreException -> 0x004b }
            goto L_0x0000;
            */
            throw new UnsupportedOperationException("Method not decompiled: com.qualcomm.robotcore.eventloop.EventLoopManager.c.run():void");
        }
    }

    /* renamed from: com.qualcomm.robotcore.eventloop.EventLoopManager.d */
    private class C0030d implements Runnable {
        final /* synthetic */ EventLoopManager f181a;
        private Set<Command> f182b;

        private C0030d(EventLoopManager eventLoopManager) {
            this.f181a = eventLoopManager;
            this.f182b = new HashSet();
        }

        public void run() {
            while (!Thread.interrupted()) {
                for (Command command : this.f181a.f196n) {
                    if (command.getAttempts() > 10) {
                        RobotLog.w("Failed to send command, too many attempts: " + command.toString());
                        this.f182b.add(command);
                    } else if (command.isAcknowledged()) {
                        RobotLog.v("Command " + command.getName() + " has been acknowledged by remote device");
                        this.f182b.add(command);
                    } else {
                        try {
                            RobotLog.v("Sending command: " + command.getName() + ", attempt " + command.getAttempts());
                            this.f181a.f186d.send(new RobocolDatagram(command.toByteArray()));
                        } catch (RobotCoreException e) {
                            RobotLog.w("Failed to send command " + command.getName());
                            RobotLog.logStacktrace(e);
                        }
                    }
                }
                this.f181a.f196n.removeAll(this.f182b);
                this.f182b.clear();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e2) {
                    return;
                }
            }
        }
    }

    static {
        f183a = new C0027a();
    }

    public void handleDroppedConnection() {
        OpModeManager opModeManager = this.f189g.getOpModeManager();
        String str = "Lost connection while running op mode: " + opModeManager.getActiveOpModeName();
        resetGamepads();
        opModeManager.initActiveOpMode(OpModeManager.DEFAULT_OP_MODE_NAME);
        m164a(RobotState.DROPPED_CONNECTION);
        RobotLog.i(str);
    }

    public EventLoopManager(RobocolDatagramSocket socket) {
        this.state = RobotState.NOT_STARTED;
        this.f184b = new Thread();
        this.f185c = new Thread();
        this.f187e = false;
        this.f188f = new ElapsedTime();
        this.f189g = f183a;
        this.f190h = new Gamepad[]{new Gamepad(), new Gamepad()};
        this.f191i = new Heartbeat(Token.EMPTY);
        this.f192j = null;
        this.f193k = new CopyOnWriteArraySet();
        this.f194l = new Command[8];
        this.f195m = 0;
        this.f196n = new CopyOnWriteArraySet();
        this.f186d = socket;
        m164a(RobotState.NOT_STARTED);
    }

    public void setMonitor(EventLoopMonitor monitor) {
        this.f192j = monitor;
    }

    public void start(EventLoop eventLoop) throws RobotCoreException {
        this.f187e = false;
        setEventLoop(eventLoop);
        this.f185c = new Thread(new C0030d(this), "Scheduled Sends");
        this.f185c.start();
        new Thread(new C0029c(this)).start();
    }

    public void shutdown() {
        this.f186d.close();
        this.f185c.interrupt();
        this.f187e = true;
        m166b();
    }

    public void registerSyncdDevice(SyncdDevice device) {
        this.f193k.add(device);
    }

    public void unregisterSyncdDevice(SyncdDevice device) {
        this.f193k.remove(device);
    }

    public void setEventLoop(EventLoop eventLoop) throws RobotCoreException {
        if (eventLoop == null) {
            eventLoop = f183a;
            RobotLog.d("Event loop cannot be null, using empty event loop");
        }
        m166b();
        this.f189g = eventLoop;
        m160a();
    }

    public EventLoop getEventLoop() {
        return this.f189g;
    }

    public Gamepad getGamepad() {
        return getGamepad(0);
    }

    public Gamepad getGamepad(int port) {
        Range.throwIfRangeIsInvalid((double) port, 0.0d, Servo.MAX_POSITION);
        return this.f190h[port];
    }

    public Gamepad[] getGamepads() {
        return this.f190h;
    }

    public void resetGamepads() {
        for (Gamepad reset : this.f190h) {
            reset.reset();
        }
    }

    public Heartbeat getHeartbeat() {
        return this.f191i;
    }

    public void sendTelemetryData(Telemetry telemetry) {
        try {
            this.f186d.send(new RobocolDatagram(telemetry.toByteArray()));
        } catch (RobotCoreException e) {
            RobotLog.w("Failed to send telemetry data");
            RobotLog.logStacktrace(e);
        }
        telemetry.clearData();
    }

    public void sendCommand(Command command) {
        this.f196n.add(command);
    }

    private void m160a() throws RobotCoreException {
        try {
            m164a(RobotState.INIT);
            this.f189g.init(this);
            for (SyncdDevice startBlockingWork : this.f193k) {
                startBlockingWork.startBlockingWork();
            }
            this.f188f = new ElapsedTime(0);
            m164a(RobotState.RUNNING);
            this.f184b = new Thread(new C0028b(this), "Event Loop");
            this.f184b.start();
        } catch (Exception e) {
            RobotLog.w("Caught exception during looper init: " + e.toString());
            RobotLog.logStacktrace(e);
            m164a(RobotState.EMERGENCY_STOP);
            if (RobotLog.hasGlobalErrorMsg()) {
                buildAndSendTelemetry(SYSTEM_TELEMETRY, RobotLog.getGlobalErrorMsg());
            }
            throw new RobotCoreException("Robot failed to start: " + e.getMessage());
        }
    }

    private void m166b() {
        this.f184b.interrupt();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
        }
        m164a(RobotState.STOPPED);
        this.f189g = f183a;
        this.f193k.clear();
    }

    private void m164a(RobotState robotState) {
        this.state = robotState;
        RobotLog.v("EventLoopManager state is " + robotState.toString());
        if (this.f192j != null) {
            this.f192j.onStateChange(robotState);
        }
    }

    private void m163a(RobocolDatagram robocolDatagram) throws RobotCoreException {
        Gamepad gamepad = new Gamepad();
        gamepad.fromByteArray(robocolDatagram.getData());
        if (gamepad.user < (byte) 1 || gamepad.user > 2) {
            RobotLog.d("Gamepad with user %d received. Only users 1 and 2 are valid");
            return;
        }
        int i = gamepad.user - 1;
        this.f190h[i].copy(gamepad);
        if (this.f190h[0].id == this.f190h[1].id) {
            RobotLog.v("Gamepad moved position, removing stale gamepad");
            if (i == 0) {
                this.f190h[1].copy(new Gamepad());
            }
            if (i == 1) {
                this.f190h[0].copy(new Gamepad());
            }
        }
    }

    private void m168b(RobocolDatagram robocolDatagram) throws RobotCoreException {
        Heartbeat heartbeat = new Heartbeat(Token.EMPTY);
        heartbeat.fromByteArray(robocolDatagram.getData());
        heartbeat.setRobotState(this.state);
        robocolDatagram.setData(heartbeat.toByteArray());
        this.f186d.send(robocolDatagram);
        this.f188f.reset();
        this.f191i = heartbeat;
    }

    private void m171c(RobocolDatagram robocolDatagram) throws RobotCoreException {
        if (!robocolDatagram.getAddress().equals(this.f197o)) {
            if (this.state == RobotState.DROPPED_CONNECTION) {
                m164a(RobotState.RUNNING);
            }
            if (this.f189g != f183a) {
                this.f197o = robocolDatagram.getAddress();
                RobotLog.i("new remote peer discovered: " + this.f197o.getHostAddress());
                try {
                    this.f186d.connect(this.f197o);
                } catch (SocketException e) {
                    RobotLog.e("Unable to connect to peer:" + e.toString());
                }
                RobocolParsable peerDiscovery = new PeerDiscovery(PeerType.PEER);
                RobotLog.v("Sending peer discovery packet");
                RobocolDatagram robocolDatagram2 = new RobocolDatagram(peerDiscovery);
                if (this.f186d.getInetAddress() == null) {
                    robocolDatagram2.setAddress(this.f197o);
                }
                this.f186d.send(robocolDatagram2);
            }
        }
    }

    private void m175d(RobocolDatagram robocolDatagram) throws RobotCoreException {
        Command command = new Command(robocolDatagram.getData());
        if (command.isAcknowledged()) {
            this.f196n.remove(command);
            return;
        }
        command.acknowledge();
        this.f186d.send(new RobocolDatagram(command));
        Command[] commandArr = this.f194l;
        int length = commandArr.length;
        int i = 0;
        while (i < length) {
            Command command2 = commandArr[i];
            if (command2 == null || !command2.equals(command)) {
                i++;
            } else {
                return;
            }
        }
        Command[] commandArr2 = this.f194l;
        int i2 = this.f195m;
        this.f195m = i2 + 1;
        commandArr2[i2 % this.f194l.length] = command;
        try {
            this.f189g.processCommand(command);
        } catch (Exception e) {
            RobotLog.e("Event loop threw an exception while processing a command");
            RobotLog.logStacktrace(e);
        }
    }

    private void m169c() {
    }

    private void m178e(RobocolDatagram robocolDatagram) {
        RobotLog.w("RobotCore event loop received unknown event type: " + robocolDatagram.getMsgType().name());
    }

    public void buildAndSendTelemetry(String tag, String msg) {
        Telemetry telemetry = new Telemetry();
        telemetry.setTag(tag);
        telemetry.addData(tag, msg);
        sendTelemetryData(telemetry);
    }
}
