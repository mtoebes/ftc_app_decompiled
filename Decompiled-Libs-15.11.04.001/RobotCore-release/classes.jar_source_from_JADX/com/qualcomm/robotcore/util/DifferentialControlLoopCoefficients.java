package com.qualcomm.robotcore.util;

public class DifferentialControlLoopCoefficients {
    public double f364d;
    public double f365i;
    public double f366p;

    public DifferentialControlLoopCoefficients() {
        this.f366p = 0.0d;
        this.f365i = 0.0d;
        this.f364d = 0.0d;
    }

    public DifferentialControlLoopCoefficients(double p, double i, double d) {
        this.f366p = 0.0d;
        this.f365i = 0.0d;
        this.f364d = 0.0d;
        this.f366p = p;
        this.f365i = i;
        this.f364d = d;
    }
}
