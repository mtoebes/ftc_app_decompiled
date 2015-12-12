package com.qualcomm.robotcore.eventloop;

import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.Command;

public interface EventLoop {
    OpModeManager getOpModeManager();

    void init(EventLoopManager eventLoopManager) throws RobotCoreException, InterruptedException;

    void loop() throws RobotCoreException, InterruptedException;

    void processCommand(Command command);

    void teardown() throws RobotCoreException, InterruptedException;
}
