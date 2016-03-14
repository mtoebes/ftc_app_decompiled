package com.qualcomm.robotcore.eventloop;

import android.hardware.usb.UsbDevice;
import com.qualcomm.robotcore.BuildConfig;
import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.usb.RobotUsbModule;
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
import com.qualcomm.robotcore.util.Util;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class EventLoopManager {
    public static final String RC_BATTERY_LEVEL_KEY = "$RobotController$Battery$Level$";
    public static final String ROBOT_BATTERY_LEVEL_KEY = "$Robot$Battery$Level$";
    public static final String SYSTEM_ERROR_KEY = "$System$Error$";
    public static final String SYSTEM_NONE_KEY = "$System$None$";
    public static final String SYSTEM_WARNING_KEY = "$System$Warning$";
    private static final EventLoop f186a;
    private Thread f187b;
    private ExecutorService f188c;
    private final RobocolDatagramSocket f189d;
    private ElapsedTime f190e;
    private EventLoop f191f;
    private final Gamepad[] f192g;
    private Heartbeat f193h;
    private EventLoopMonitor f194i;
    private final Set<SyncdDevice> f195j;
    private final Command[] f196k;
    private int f197l;
    private final Set<Command> f198m;
    private InetAddress f199n;
    private final Object f200o;
    private String f201p;
    private String f202q;
    private long f203r;
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
        void onErrorOrWarning();

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

        public void onUsbDeviceAttached(UsbDevice usbDevice) {
        }

        public void processedRecentlyAttachedUsbDevices() throws RobotCoreException, InterruptedException {
        }

        public void handleUsbModuleDetach(RobotUsbModule module) throws RobotCoreException, InterruptedException {
        }

        public void processCommand(Command command) {
            RobotLog.m256w("Dropping command " + command.getName() + ", no active event loop");
        }

        public OpModeManager getOpModeManager() {
            return null;
        }
    }

    /* renamed from: com.qualcomm.robotcore.eventloop.EventLoopManager.b */
    private class C0029b implements Runnable {
        final /* synthetic */ EventLoopManager f179a;

        /* renamed from: com.qualcomm.robotcore.eventloop.EventLoopManager.b.1 */
        class C00281 implements Runnable {
            final /* synthetic */ C0029b f178a;

            C00281(C0029b c0029b) {
                this.f178a = c0029b;
            }

            public void run() {
                try {
                    ElapsedTime elapsedTime = new ElapsedTime();
                    while (!Thread.interrupted()) {
                        while (elapsedTime.time() < 0.001d) {
                            Thread.sleep(5);
                        }
                        elapsedTime.reset();
                        this.f178a.f179a.refreshSystemTelemetry();
                        if (this.f178a.f179a.f190e.startTime() == 0.0d) {
                            Thread.sleep(500);
                        } else if (this.f178a.f179a.f190e.time() > 2.0d) {
                            this.f178a.f179a.handleDroppedConnection();
                            this.f178a.f179a.f199n = null;
                            this.f178a.f179a.f190e = new ElapsedTime(0);
                        }
                        for (SyncdDevice blockUntilReady : this.f178a.f179a.f195j) {
                            blockUntilReady.blockUntilReady();
                        }
                        for (SyncdDevice blockUntilReady2 : this.f178a.f179a.f195j) {
                            if (blockUntilReady2.hasShutdownAbnormally()) {
                                RobotUsbModule owner = blockUntilReady2.getOwner();
                                if (owner != null) {
                                    this.f178a.f179a.f191f.handleUsbModuleDetach(owner);
                                }
                            }
                        }
                        this.f178a.f179a.f191f.processedRecentlyAttachedUsbDevices();
                        try {
                            this.f178a.f179a.f191f.loop();
                            for (SyncdDevice blockUntilReady22 : this.f178a.f179a.f195j) {
                                blockUntilReady22.startBlockingWork();
                            }
                        } catch (Exception e) {
                            RobotLog.m250e("Event loop threw an exception");
                            RobotLog.logStacktrace(e);
                            RobotLog.setGlobalErrorMsg("User code threw an uncaught exception: " + (e.getClass().getSimpleName() + (e.getMessage() != null ? " - " + e.getMessage() : BuildConfig.VERSION_NAME)));
                            this.f178a.f179a.refreshSystemTelemetry();
                            throw new RobotCoreException("EventLoop Exception in loop(): %s", e.getClass().getSimpleName() + (e.getMessage() != null ? " - " + e.getMessage() : BuildConfig.VERSION_NAME));
                        } catch (Throwable th) {
                            Throwable th2 = th;
                            for (SyncdDevice blockUntilReady222 : this.f178a.f179a.f195j) {
                                blockUntilReady222.startBlockingWork();
                            }
                        }
                    }
                } catch (InterruptedException e2) {
                    RobotLog.m254v("EventLoopRunnable interrupted");
                    this.f178a.f179a.m172a(RobotState.STOPPED);
                } catch (RobotCoreException e3) {
                    RobotLog.m254v("RobotCoreException in EventLoopManager: " + e3.getMessage());
                    this.f178a.f179a.m172a(RobotState.EMERGENCY_STOP);
                    this.f178a.f179a.refreshSystemTelemetry();
                }
                try {
                    this.f178a.f179a.f191f.teardown();
                } catch (Exception e4) {
                    RobotLog.m256w("Caught exception during looper teardown: " + e4.toString());
                    RobotLog.logStacktrace(e4);
                    this.f178a.f179a.refreshSystemTelemetry();
                }
            }
        }

        private C0029b(EventLoopManager eventLoopManager) {
            this.f179a = eventLoopManager;
        }

        public void run() {
            Util.logThreadLifeCycle("EventLoopRunnable.run()", new C00281(this));
        }
    }

    /* renamed from: com.qualcomm.robotcore.eventloop.EventLoopManager.c */
    private class C0031c implements Runnable {
        ElapsedTime f181a;
        final /* synthetic */ EventLoopManager f182b;

        /* renamed from: com.qualcomm.robotcore.eventloop.EventLoopManager.c.1 */
        class C00301 implements Runnable {
            final /* synthetic */ C0031c f180a;

            C00301(C0031c c0031c) {
                this.f180a = c0031c;
            }

            /* JADX WARNING: inconsistent code. */
            /* Code decompiled incorrectly, please refer to instructions dump. */
            public void run() {
                /*
                r4 = this;
            L_0x0000:
                r0 = java.lang.Thread.interrupted();
                if (r0 != 0) goto L_0x0026;
            L_0x0006:
                r0 = r4.f180a;
                r0 = r0.f182b;
                r0 = r0.f189d;
                r1 = r0.recv();
                r0 = java.lang.Thread.interrupted();
                if (r0 != 0) goto L_0x0026;
            L_0x0018:
                r0 = r4.f180a;
                r0 = r0.f182b;
                r0 = r0.f189d;
                r0 = r0.isClosed();
                if (r0 == 0) goto L_0x0027;
            L_0x0026:
                return;
            L_0x0027:
                if (r1 != 0) goto L_0x002d;
            L_0x0029:
                java.lang.Thread.yield();
                goto L_0x0000;
            L_0x002d:
                r0 = r4.f180a;
                r0 = r0.f182b;
                r0.refreshSystemTelemetry();
                r0 = com.qualcomm.robotcore.eventloop.EventLoopManager.C00261.f176a;	 Catch:{ RobotCoreException -> 0x0056 }
                r2 = r1.getMsgType();	 Catch:{ RobotCoreException -> 0x0056 }
                r2 = r2.ordinal();	 Catch:{ RobotCoreException -> 0x0056 }
                r0 = r0[r2];	 Catch:{ RobotCoreException -> 0x0056 }
                switch(r0) {
                    case 1: goto L_0x004e;
                    case 2: goto L_0x007c;
                    case 3: goto L_0x0089;
                    case 4: goto L_0x0091;
                    case 5: goto L_0x0099;
                    default: goto L_0x0043;
                };	 Catch:{ RobotCoreException -> 0x0056 }
            L_0x0043:
                r0 = r4.f180a;	 Catch:{ RobotCoreException -> 0x0056 }
                r0 = r0.f182b;	 Catch:{ RobotCoreException -> 0x0056 }
                r0.m186e(r1);	 Catch:{ RobotCoreException -> 0x0056 }
            L_0x004a:
                r1.close();
                goto L_0x0000;
            L_0x004e:
                r0 = r4.f180a;	 Catch:{ RobotCoreException -> 0x0056 }
                r0 = r0.f182b;	 Catch:{ RobotCoreException -> 0x0056 }
                r0.m171a(r1);	 Catch:{ RobotCoreException -> 0x0056 }
                goto L_0x004a;
            L_0x0056:
                r0 = move-exception;
                r2 = new java.lang.StringBuilder;	 Catch:{ all -> 0x0084 }
                r2.<init>();	 Catch:{ all -> 0x0084 }
                r3 = "RobotCore event loop cannot process event: ";
                r2 = r2.append(r3);	 Catch:{ all -> 0x0084 }
                r3 = r0.toString();	 Catch:{ all -> 0x0084 }
                r2 = r2.append(r3);	 Catch:{ all -> 0x0084 }
                r2 = r2.toString();	 Catch:{ all -> 0x0084 }
                com.qualcomm.robotcore.util.RobotLog.m256w(r2);	 Catch:{ all -> 0x0084 }
                r0 = r0.getMessage();	 Catch:{ all -> 0x0084 }
                com.qualcomm.robotcore.util.RobotLog.setGlobalErrorMsg(r0);	 Catch:{ all -> 0x0084 }
                r1.close();
                goto L_0x0000;
            L_0x007c:
                r0 = r4.f180a;	 Catch:{ RobotCoreException -> 0x0056 }
                r0 = r0.f182b;	 Catch:{ RobotCoreException -> 0x0056 }
                r0.m176b(r1);	 Catch:{ RobotCoreException -> 0x0056 }
                goto L_0x004a;
            L_0x0084:
                r0 = move-exception;
                r1.close();
                throw r0;
            L_0x0089:
                r0 = r4.f180a;	 Catch:{ RobotCoreException -> 0x0056 }
                r0 = r0.f182b;	 Catch:{ RobotCoreException -> 0x0056 }
                r0.m180c(r1);	 Catch:{ RobotCoreException -> 0x0056 }
                goto L_0x004a;
            L_0x0091:
                r0 = r4.f180a;	 Catch:{ RobotCoreException -> 0x0056 }
                r0 = r0.f182b;	 Catch:{ RobotCoreException -> 0x0056 }
                r0.m183d(r1);	 Catch:{ RobotCoreException -> 0x0056 }
                goto L_0x004a;
            L_0x0099:
                r0 = r4.f180a;	 Catch:{ RobotCoreException -> 0x0056 }
                r0 = r0.f182b;	 Catch:{ RobotCoreException -> 0x0056 }
                r0.m177c();	 Catch:{ RobotCoreException -> 0x0056 }
                goto L_0x004a;
                */
                throw new UnsupportedOperationException("Method not decompiled: com.qualcomm.robotcore.eventloop.EventLoopManager.c.1.run():void");
            }
        }

        private C0031c(EventLoopManager eventLoopManager) {
            this.f182b = eventLoopManager;
            this.f181a = new ElapsedTime();
        }

        public void run() {
            Util.logThreadLifeCycle("RecvRunnable.run()", new C00301(this));
        }
    }

    /* renamed from: com.qualcomm.robotcore.eventloop.EventLoopManager.d */
    private class C0033d implements Runnable {
        final /* synthetic */ EventLoopManager f184a;
        private Set<Command> f185b;

        /* renamed from: com.qualcomm.robotcore.eventloop.EventLoopManager.d.1 */
        class C00321 implements Runnable {
            final /* synthetic */ C0033d f183a;

            C00321(C0033d c0033d) {
                this.f183a = c0033d;
            }

            public void run() {
                while (!Thread.interrupted()) {
                    long nanoTime = System.nanoTime();
                    for (Command command : this.f183a.f184a.f198m) {
                        if (command.getAttempts() > 10) {
                            RobotLog.m257w("giving up on command %s(%d) after %d attempts", command.getName(), Integer.valueOf(command.getSequenceNumber()), Byte.valueOf(command.getAttempts()));
                            this.f183a.f185b.add(command);
                        } else if (command.isAcknowledged()) {
                            RobotLog.m255v("command %s(%d) has been acknowledged by remote device", command.getName(), Integer.valueOf(command.getSequenceNumber()));
                            this.f183a.f185b.add(command);
                        } else if (command.shouldTransmit(nanoTime)) {
                            try {
                                RobotLog.m255v("sending %s(%d), attempt %d", command.getName(), Integer.valueOf(command.getSequenceNumber()), Byte.valueOf(command.getAttempts()));
                                this.f183a.f184a.f189d.send(new RobocolDatagram(command.toByteArrayForTransmission()));
                            } catch (RobotCoreException e) {
                                RobotLog.m257w("failed to send %s(%d) ", command.getName(), Integer.valueOf(command.getSequenceNumber()));
                                RobotLog.logStacktrace(e);
                            }
                        }
                    }
                    this.f183a.f184a.f198m.removeAll(this.f183a.f185b);
                    this.f183a.f185b.clear();
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e2) {
                        return;
                    }
                }
            }
        }

        private C0033d(EventLoopManager eventLoopManager) {
            this.f184a = eventLoopManager;
            this.f185b = new HashSet();
        }

        public void run() {
            Util.logThreadLifeCycle("ScheduledSendRunnable.run()", new C00321(this));
        }
    }

    static {
        f186a = new C0027a();
    }

    public EventLoopManager(RobocolDatagramSocket socket) {
        this.state = RobotState.NOT_STARTED;
        this.f187b = new Thread();
        this.f188c = Executors.newFixedThreadPool(2);
        this.f190e = new ElapsedTime();
        this.f191f = f186a;
        this.f192g = new Gamepad[]{new Gamepad(), new Gamepad()};
        this.f193h = new Heartbeat(Token.EMPTY);
        this.f194i = null;
        this.f195j = new CopyOnWriteArraySet();
        this.f196k = new Command[8];
        this.f197l = 0;
        this.f198m = new CopyOnWriteArraySet();
        this.f200o = new Object();
        this.f201p = null;
        this.f202q = null;
        this.f203r = 0;
        this.f189d = socket;
        m172a(RobotState.NOT_STARTED);
    }

    public void setMonitor(EventLoopMonitor monitor) {
        this.f194i = monitor;
    }

    public EventLoopMonitor getMonitor() {
        return this.f194i;
    }

    public EventLoop getEventLoop() {
        return this.f191f;
    }

    public Gamepad getGamepad() {
        return getGamepad(0);
    }

    public Gamepad getGamepad(int port) {
        Range.throwIfRangeIsInvalid((double) port, 0.0d, Servo.MAX_POSITION);
        return this.f192g[port];
    }

    public Gamepad[] getGamepads() {
        return this.f192g;
    }

    public Heartbeat getHeartbeat() {
        return this.f193h;
    }

    public void refreshSystemTelemetryNow() {
        this.f203r = 0;
        refreshSystemTelemetry();
    }

    public void refreshSystemTelemetry() {
        synchronized (this.f200o) {
            long nanoTime = System.nanoTime();
            String globalErrorMsg = RobotLog.getGlobalErrorMsg();
            String globalWarningMessage = RobotLog.getGlobalWarningMessage();
            if (!globalErrorMsg.isEmpty()) {
                globalWarningMessage = globalErrorMsg;
                globalErrorMsg = SYSTEM_ERROR_KEY;
            } else if (globalWarningMessage.isEmpty()) {
                globalWarningMessage = BuildConfig.VERSION_NAME;
                globalErrorMsg = SYSTEM_NONE_KEY;
            } else {
                globalErrorMsg = SYSTEM_WARNING_KEY;
            }
            Object obj = (globalWarningMessage.equals(this.f201p) && globalErrorMsg.equals(this.f202q) && nanoTime - this.f203r <= 5000000000L) ? null : 1;
            if (obj != null) {
                this.f201p = globalWarningMessage;
                this.f202q = globalErrorMsg;
                this.f203r = nanoTime;
                buildAndSendTelemetry(globalErrorMsg, globalWarningMessage);
                if (this.f194i != null) {
                    this.f194i.onErrorOrWarning();
                }
            }
        }
    }

    public void handleDroppedConnection() {
        OpModeManager opModeManager = this.f191f.getOpModeManager();
        String str = "Lost connection while running op mode: " + opModeManager.getActiveOpModeName();
        opModeManager.initActiveOpMode(OpModeManager.DEFAULT_OP_MODE_NAME);
        m172a(RobotState.DROPPED_CONNECTION);
        RobotLog.m252i(str);
    }

    public void start(EventLoop eventLoop) throws RobotCoreException {
        this.f188c = Executors.newFixedThreadPool(2);
        this.f188c.submit(new C0033d());
        this.f188c.submit(new C0031c());
        setEventLoop(eventLoop);
    }

    public void shutdown() {
        this.f189d.close();
        this.f188c.shutdownNow();
        while (!this.f188c.awaitTermination(5, TimeUnit.SECONDS)) {
            try {
                RobotLog.m254v("waiting for eventLoop send/receive shutdown");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        m174b();
    }

    public void registerSyncdDevice(SyncdDevice device) {
        this.f195j.add(device);
    }

    public void unregisterSyncdDevice(SyncdDevice device) {
        this.f195j.remove(device);
    }

    public void setEventLoop(EventLoop eventLoop) throws RobotCoreException {
        if (eventLoop == null) {
            eventLoop = f186a;
            RobotLog.m248d("Event loop cannot be null, using empty event loop");
        }
        m174b();
        this.f191f = eventLoop;
        m168a();
    }

    public void sendTelemetryData(Telemetry telemetry) {
        try {
            this.f189d.send(new RobocolDatagram(telemetry.toByteArrayForTransmission()));
        } catch (RobotCoreException e) {
            RobotLog.m256w("Failed to send telemetry data");
            RobotLog.logStacktrace(e);
        }
        telemetry.clearData();
    }

    public void sendCommand(Command command) {
        this.f198m.add(command);
    }

    private void m168a() throws RobotCoreException {
        try {
            m172a(RobotState.INIT);
            this.f191f.init(this);
            for (SyncdDevice startBlockingWork : this.f195j) {
                startBlockingWork.startBlockingWork();
            }
            this.f190e = new ElapsedTime(0);
            m172a(RobotState.RUNNING);
            this.f187b = new Thread(new C0029b(), "Event Loop");
            this.f187b.start();
        } catch (Exception e) {
            RobotLog.m256w("Caught exception during looper init: " + e.toString());
            RobotLog.logStacktrace(e);
            m172a(RobotState.EMERGENCY_STOP);
            refreshSystemTelemetry();
            throw new RobotCoreException("Robot failed to start: " + e.getMessage());
        }
    }

    private void m174b() {
        if (this.f187b.isAlive()) {
            while (true) {
                try {
                    this.f187b.interrupt();
                    this.f187b.join(5000);
                    if (!this.f187b.isAlive()) {
                        break;
                    }
                    RobotLog.m254v("waiting for eventLoop shutdown");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
        m172a(RobotState.STOPPED);
        this.f191f = f186a;
        this.f195j.clear();
    }

    private void m172a(RobotState robotState) {
        this.state = robotState;
        RobotLog.m254v("EventLoopManager state is " + robotState.toString());
        if (this.f194i != null) {
            this.f194i.onStateChange(robotState);
        }
    }

    private void m171a(RobocolDatagram robocolDatagram) throws RobotCoreException {
        Gamepad gamepad = new Gamepad();
        gamepad.fromByteArray(robocolDatagram.getData());
        if (gamepad.user < (byte) 1 || gamepad.user > 2) {
            RobotLog.m248d("Gamepad with user %d received. Only users 1 and 2 are valid");
            return;
        }
        int i = gamepad.user - 1;
        this.f192g[i].copy(gamepad);
        if (this.f192g[0].id == this.f192g[1].id) {
            RobotLog.m254v("Gamepad moved position, removing stale gamepad");
            if (i == 0) {
                this.f192g[1].copy(new Gamepad());
            }
            if (i == 1) {
                this.f192g[0].copy(new Gamepad());
            }
        }
    }

    private void m176b(RobocolDatagram robocolDatagram) throws RobotCoreException {
        Heartbeat heartbeat = new Heartbeat(Token.EMPTY);
        heartbeat.fromByteArray(robocolDatagram.getData());
        heartbeat.setRobotState(this.state);
        robocolDatagram.setData(heartbeat.toByteArrayForTransmission());
        this.f189d.send(robocolDatagram);
        this.f190e.reset();
        this.f193h = heartbeat;
    }

    private void m180c(RobocolDatagram robocolDatagram) throws RobotCoreException {
        if (!robocolDatagram.getAddress().equals(this.f199n)) {
            PeerDiscovery.forReceive().fromByteArray(robocolDatagram.getData());
            if (this.state == RobotState.DROPPED_CONNECTION) {
                m172a(RobotState.RUNNING);
            }
            if (this.f191f != f186a) {
                this.f199n = robocolDatagram.getAddress();
                RobotLog.m252i("new remote peer discovered: " + this.f199n.getHostAddress());
                try {
                    this.f189d.connect(this.f199n);
                } catch (SocketException e) {
                    RobotLog.m250e("Unable to connect to peer:" + e.toString());
                }
                RobotLog.m255v("sending peer discovery packet(%d)", Integer.valueOf(new PeerDiscovery(PeerType.PEER).getSequenceNumber()));
                RobocolDatagram robocolDatagram2 = new RobocolDatagram(r0);
                if (this.f189d.getInetAddress() == null) {
                    robocolDatagram2.setAddress(this.f199n);
                }
                this.f189d.send(robocolDatagram2);
            }
        }
    }

    private void m183d(RobocolDatagram robocolDatagram) throws RobotCoreException {
        RobocolParsable command = new Command(robocolDatagram.getData());
        if (command.isAcknowledged()) {
            this.f198m.remove(command);
            return;
        }
        command.acknowledge();
        this.f189d.send(new RobocolDatagram(command));
        Command[] commandArr = this.f196k;
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
        Command[] commandArr2 = this.f196k;
        int i2 = this.f197l;
        this.f197l = i2 + 1;
        commandArr2[i2 % this.f196k.length] = command;
        try {
            this.f191f.processCommand(command);
        } catch (Exception e) {
            RobotLog.m250e("Event loop threw an exception while processing a command");
            RobotLog.logStacktrace(e);
        }
    }

    private void m177c() {
    }

    private void m186e(RobocolDatagram robocolDatagram) {
        RobotLog.m256w("RobotCore event loop received unknown event type: " + robocolDatagram.getMsgType().name());
    }

    public void buildAndSendTelemetry(String tag, String msg) {
        Telemetry telemetry = new Telemetry();
        telemetry.setTag(tag);
        telemetry.addData(tag, msg);
        sendTelemetryData(telemetry);
    }
}
