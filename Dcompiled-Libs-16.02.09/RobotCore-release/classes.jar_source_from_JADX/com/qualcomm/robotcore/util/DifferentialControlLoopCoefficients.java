package com.qualcomm.robotcore.util;

public class DifferentialControlLoopCoefficients {
    public double f360d;
    public double f361i;
    public double f362p;

    public DifferentialControlLoopCoefficients() {
        this.f362p = 0.0d;
        this.f361i = 0.0d;
        this.f360d = 0.0d;
    }

    public DifferentialControlLoopCoefficients(double p, double i, double d) {
        this.f362p = 0.0d;
        this.f361i = 0.0d;
        this.f360d = 0.0d;
        this.f362p = p;
        this.f361i = i;
        this.f360d = d;
    }
}
