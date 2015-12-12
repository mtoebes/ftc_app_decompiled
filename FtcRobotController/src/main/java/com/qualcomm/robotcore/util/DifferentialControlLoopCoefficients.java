package com.qualcomm.robotcore.util;

public class DifferentialControlLoopCoefficients {
    public double d;
    public double i;
    public double p;

    public DifferentialControlLoopCoefficients() {
    }

    public DifferentialControlLoopCoefficients(double p, double i, double d) {
        this.p = p;
        this.i = i;
        this.d = d;
    }
}
