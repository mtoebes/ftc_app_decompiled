package com.qualcomm.robotcore.util;

public class Statistics {
    int f425a;
    double f426b;
    double f427c;

    public Statistics() {
        clear();
    }

    public int getCount() {
        return this.f425a;
    }

    public double getMean() {
        return this.f426b;
    }

    public double getVariance() {
        return this.f427c / ((double) (this.f425a - 1));
    }

    public double getStandardDeviation() {
        return Math.sqrt(getVariance());
    }

    public void clear() {
        this.f425a = 0;
        this.f426b = 0.0d;
        this.f427c = 0.0d;
    }

    public void add(double x) {
        this.f425a++;
        double d = x - this.f426b;
        this.f426b += d / ((double) this.f425a);
        this.f427c = (d * (x - this.f426b)) + this.f427c;
    }

    public void remove(double x) {
        int i = this.f425a - 1;
        double d = x - this.f426b;
        this.f427c -= d * ((((double) this.f425a) * d) / ((double) i));
        this.f426b = ((this.f426b * ((double) this.f425a)) - x) / ((double) i);
        this.f425a = i;
    }
}
