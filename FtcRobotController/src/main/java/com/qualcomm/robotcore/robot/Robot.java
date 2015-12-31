package com.qualcomm.robotcore.robot;

import com.qualcomm.robotcore.eventloop.EventLoop;
import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.RobocolDatagram;
import com.qualcomm.robotcore.robocol.RobocolDatagramSocket;
import com.qualcomm.robotcore.util.RobotLog;

import java.net.InetAddress;
import java.util.concurrent.ArrayBlockingQueue;

public class Robot {
    public EventLoopManager eventLoopManager;
    public ArrayBlockingQueue<RobocolDatagram> eventQueue;
    public ArrayBlockingQueue<RobocolDatagram> sendQueue;
    public RobocolDatagramSocket socket;

    public Robot() {
        this.eventLoopManager = null;
        this.socket = null;
        this.sendQueue = null;
        this.eventQueue = null;
    }

    public void start(InetAddress driverStationAddr, EventLoop eventLoop) throws RobotCoreException {
        try {
            this.socket.listen(driverStationAddr);
            this.eventLoopManager.start(eventLoop);
        } catch (Exception e) {
            RobotLog.logStacktrace(e);
            throw new RobotCoreException("Robot start failed: " + e.toString());
        }
    }

    public void shutdown() {
        if (this.eventLoopManager != null) {
            this.eventLoopManager.shutdown();
        }
        if (this.socket != null) {
            this.socket.close();
        }
    }
}
