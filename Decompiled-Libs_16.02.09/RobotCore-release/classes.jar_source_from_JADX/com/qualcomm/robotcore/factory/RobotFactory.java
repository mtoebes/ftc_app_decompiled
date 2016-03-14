package com.qualcomm.robotcore.factory;

import com.qualcomm.robotcore.eventloop.EventLoopManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.RobocolDatagramSocket;
import com.qualcomm.robotcore.robot.Robot;

public class RobotFactory {
    public static Robot createRobot() throws RobotCoreException {
        RobocolDatagramSocket robocolDatagramSocket = new RobocolDatagramSocket();
        EventLoopManager eventLoopManager = new EventLoopManager(robocolDatagramSocket);
        Robot robot = new Robot();
        robot.eventLoopManager = eventLoopManager;
        robot.socket = robocolDatagramSocket;
        return robot;
    }
}
