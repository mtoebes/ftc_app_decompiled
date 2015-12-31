package com.qualcomm.robotcore.eventloop;

import com.qualcomm.robotcore.eventloop.opmode.OpModeManager;
import com.qualcomm.robotcore.exception.RobotCoreException;
import com.qualcomm.robotcore.robocol.Command;

/**
 * Event loop interface
 * <p/>
 * Event loops need to implement this interface. Contains methods for managing the life cycle of your robot.
 */
public interface EventLoop {
    OpModeManager getOpModeManager();

    /**
     * Init method, this will be called before the first call to loop. You should set up your hardware in this method.
     *
     * @param eventLoopManager event loop manager that is responsible for this event loop
     * @throws RobotCoreException   if a RobotCoreException is thrown, it will be handled by the event loop manager. The manager will report that the robot failed to start.
     * @throws InterruptedException
     */
    void init(EventLoopManager eventLoopManager) throws RobotCoreException, InterruptedException;

    /**
     * This method will be repeatedly called by the event loop manager.
     *
     * @throws RobotCoreException   if a RobotCoreException is thrown, it will be handled by the event loop manager. The manager may decide to either stop processing this iteration of the loop, or it may decide to shut down the robot.
     * @throws InterruptedException
     */
    void loop() throws RobotCoreException, InterruptedException;

    /**
     * Process command method, this will be called if the event loop manager receives a user defined command. How this command is handled is up to the event loop implementation.
     *
     * @param command command to process
     */
    void processCommand(Command command);

    /**
     * Teardown method, this will be called after the last call to loop. You should place your robot into a safe state before this method exits, since there will be no more changes to communicate with your robot.
     *
     * @throws RobotCoreException   if a RobotCoreException is thrown, it will be handled by the event loop manager. The manager will then attempt to shut down the robot without the benefit of the teardown method.
     * @throws InterruptedException
     */
    void teardown() throws RobotCoreException, InterruptedException;
}
