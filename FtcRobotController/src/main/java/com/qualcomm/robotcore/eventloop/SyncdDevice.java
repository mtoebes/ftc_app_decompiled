package com.qualcomm.robotcore.eventloop;

import com.qualcomm.robotcore.exception.RobotCoreException;

/**
 * SyncdDevice is for a device that wants to be in sync with the event loop. If there is sync'd device registered with the event loop manager then the event loop manager will run the event loop in this manor:
 * <ol>
 * <li> wait until all sync'd device have returned from blockUtilReady()
 * <li> run EventLoop.loop()
 * <li> call startBlockingWork() on all sync'd device
 * </ol>
 * Sync'd devices need to register themselves with the event loop manager
 */
public interface SyncdDevice {
    /**
     * This method should block until it is ready for the event loop to run, Once this method has returned, subsequent calls should return immediately until startBlockingWork() has been called.
     *
     * @throws RobotCoreException
     * @throws InterruptedException
     */
    void blockUntilReady() throws RobotCoreException, InterruptedException;

    /**
     * This method will be called to let the sync'd device know that it's ok to enter a blocking state.
     * <p/>
     * Before this method returns, the sync'd device should put blockUntilReady() into a blocking state. blockUntilReady() should remain in a blocking state until the device is ready for the event loop to run. Once blockUntilReady() returns, it should not block again until startBlockingWork() has been called.
     */
    void startBlockingWork();
}
