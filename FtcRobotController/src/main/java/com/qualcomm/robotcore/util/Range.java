package com.qualcomm.robotcore.util;

public class Range {
    private Range() {
    }

    public static double scale(double scale, double x1, double x2, double y1, double y2) {
        double delta = (y1 - y2) / (x1 - x2);
        return (scale * delta) + (y1 - (x1 * delta));
    }

    public static double clip(double number, double min, double max) {
        if (number < min) {
            return min;
        }
        return (number > max) ? max : number;
    }

    public static float clip(float number, float min, float max) {
        if (number < min) {
            return min;
        }
        return (number > max) ? max : number;
    }

    public static void throwIfRangeIsInvalid(double number, double min, double max) throws IllegalArgumentException {
        if ((number < min) || (number > max)) {
            throw new IllegalArgumentException(String.format("number %f is invalid; valid ranges are %f..%f", new Object[]{number, min, max}));
        }
    }
}
