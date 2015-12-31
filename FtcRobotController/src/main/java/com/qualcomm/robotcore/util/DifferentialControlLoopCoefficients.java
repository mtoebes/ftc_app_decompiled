package com.qualcomm.robotcore.util;

/**
 * Contains p, i, and d coefficients for control loops
 */
public class DifferentialControlLoopCoefficients {
    /**
     * d coefficient
     */
    public double d;
    /**
     * i coefficient
     */
    public double i;
    /**
     * p coefficient
     */
    public double p;

    /**
     * Constructor with coefficients set to 0.0
     */
    public DifferentialControlLoopCoefficients() {
    }

    /**
     * Constructor with coefficients supplied
     *
     * @param p p coefficient
     * @param i i coefficient
     * @param d d coefficient
     */
    public DifferentialControlLoopCoefficients(double p, double i, double d) {
        this.p = p;
        this.i = i;
        this.d = d;
    }
}
