package com.qualcomm.robotcore.util;

/**
 * Utility class for performing range operations
 */
public class Range {
    private Range() {
    }

    /**
     * Scale a number in the range of x1 to x2, to the range of y1 to y2
     *
     * @param n  number to scale
     * @param x1 lower bound range of n
     * @param x2 upper bound range of n
     * @param y1 lower bound of scale
     * @param y2 upper bound of scale
     * @return a double scaled to a value between y1 and y2, inclusive
     */
    public static double scale(double n, double x1, double x2, double y1, double y2) {
        double delta = (y1 - y2) / (x1 - x2);
        return (n * delta) + (y1 - (x1 * delta));
    }

    /**
     * clip 'number' if 'number' is less than 'min' or greater than 'max'
     *
     * @param number number to test
     * @param min    minume value allowed
     * @param max    maximum value allowed
     * @return clipped number
     */
    public static double clip(double number, double min, double max) {
        if (number < min) {
            return min;
        }
        return (number > max) ? max : number;
    }


    /**
     * clip 'number' if 'number' is less than 'min' or greater than 'max'
     *
     * @param number number to test
     * @param min    minume value allowed
     * @param max    maximum value allowed
     * @return clipped number
     */
    public static float clip(float number, float min, float max) {
        if (number < min) {
            return min;
        }
        return (number > max) ? max : number;
    }

    /**
     * Throw an IllegalArgumentException if 'number' is less than 'min' or greater than 'max'
     *
     * @param number number to test
     * @param min    minume value allowed
     * @param max    maximum value allowed
     * @throws IllegalArgumentException if number is outside of range
     */
    public static void throwIfRangeIsInvalid(double number, double min, double max) throws IllegalArgumentException {
        if ((number < min) || (number > max)) {
            throw new IllegalArgumentException(String.format("number %f is invalid; valid ranges are %f..%f", new Object[]{number, min, max}));
        }
    }
}
