package com.qualcomm.robotcore.eventloop.opmode;

/**
 * Register Op Modes
 */
public interface OpModeRegister {
    /**
     * The Op Mode Manager will call this method when it wants a list of all available op modes. Add you op mode to the list to enable it.
     *
     * @param manager op mode manager
     */
    void register(OpModeManager manager);
}
