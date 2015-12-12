package com.qualcomm.robotcore.util;

import android.util.Log;
import com.qualcomm.robotcore.hardware.Servo;
import java.lang.reflect.Array;

public class MatrixD {
    protected int mCols;
    protected double[][] mData;
    protected int mRows;

    public MatrixD(int rows, int cols) {
        this((double[][]) Array.newInstance(Double.TYPE, new int[]{rows, cols}));
    }

    public MatrixD(double[] init, int rows, int cols) {
        this(rows, cols);
        if (init == null) {
            throw new IllegalArgumentException("Attempted to initialize MatrixF with null array");
        } else if (init.length != rows * cols) {
            throw new IllegalArgumentException("Attempted to initialize MatrixF with rows/cols not matching init data");
        } else {
            for (int i = 0; i < rows; i++) {
                for (int i2 = 0; i2 < cols; i2++) {
                    this.mData[i][i2] = init[(cols * i) + i2];
                }
            }
        }
    }

    public MatrixD(float[] init, int rows, int cols) {
        this(rows, cols);
        if (init == null) {
            throw new IllegalArgumentException("Attempted to initialize MatrixF with null array");
        } else if (init.length != rows * cols) {
            throw new IllegalArgumentException("Attempted to initialize MatrixF with rows/cols not matching init data");
        } else {
            for (int i = 0; i < rows; i++) {
                for (int i2 = 0; i2 < cols; i2++) {
                    this.mData[i][i2] = (double) init[(cols * i) + i2];
                }
            }
        }
    }

    public MatrixD(double[][] init) {
        int i = 0;
        this.mData = init;
        if (this.mData == null) {
            throw new IllegalArgumentException("Attempted to initialize MatrixF with null array");
        }
        this.mRows = this.mData.length;
        if (this.mRows <= 0) {
            throw new IllegalArgumentException("Attempted to initialize MatrixF with 0 rows");
        }
        this.mCols = this.mData[0].length;
        while (i < this.mRows) {
            if (this.mData[i].length != this.mCols) {
                throw new IllegalArgumentException("Attempted to initialize MatrixF with rows of unequal length");
            }
            i++;
        }
    }

    public int numRows() {
        return this.mRows;
    }

    public int numCols() {
        return this.mCols;
    }

    public double[][] data() {
        return this.mData;
    }

    public MatrixD submatrix(int rows, int cols, int rowOffset, int colOffset) {
        if (rows > numRows() || cols > numCols()) {
            throw new IllegalArgumentException("Attempted to get submatrix with size larger than original");
        } else if (rowOffset + rows > numRows() || colOffset + cols > numCols()) {
            throw new IllegalArgumentException("Attempted to access out of bounds data with row or col offset out of range");
        } else {
            double[][] dArr = (double[][]) Array.newInstance(Double.TYPE, new int[]{rows, cols});
            for (int i = 0; i < rows; i++) {
                for (int i2 = 0; i2 < cols; i2++) {
                    dArr[i][i2] = data()[rowOffset + i][colOffset + i2];
                }
            }
            return new MatrixD(dArr);
        }
    }

    public boolean setSubmatrix(MatrixD inData, int rows, int cols, int rowOffset, int colOffset) {
        if (inData == null) {
            throw new IllegalArgumentException("Input data to setSubMatrix null");
        } else if (rows > numRows() || cols > numCols()) {
            throw new IllegalArgumentException("Attempted to get submatrix with size larger than original");
        } else if (rowOffset + rows > numRows() || colOffset + cols > numCols()) {
            throw new IllegalArgumentException("Attempted to access out of bounds data with row or col offset out of range");
        } else if (rows > inData.numRows() || cols > inData.numCols()) {
            throw new IllegalArgumentException("Input matrix small for setSubMatrix");
        } else if (rowOffset + rows > inData.numRows() || colOffset + cols > numCols()) {
            throw new IllegalArgumentException("Input matrix Attempted to access out of bounds data with row or col offset out of range");
        } else {
            for (int i = 0; i < rows; i++) {
                for (int i2 = 0; i2 < cols; i2++) {
                    data()[rowOffset + i][colOffset + i2] = inData.data()[i][i2];
                }
            }
            return true;
        }
    }

    public MatrixD transpose() {
        int i = this.mRows;
        int i2 = this.mCols;
        double[][] dArr = (double[][]) Array.newInstance(Double.TYPE, new int[]{i2, i});
        for (int i3 = 0; i3 < i2; i3++) {
            for (int i4 = 0; i4 < i; i4++) {
                dArr[i3][i4] = this.mData[i4][i3];
            }
        }
        return new MatrixD(dArr);
    }

    public MatrixD add(MatrixD other) {
        double[][] dArr = (double[][]) Array.newInstance(Double.TYPE, new int[]{numRows(), numCols()});
        int numRows = numRows();
        int numCols = numCols();
        for (int i = 0; i < numRows; i++) {
            for (int i2 = 0; i2 < numCols; i2++) {
                dArr[i][i2] = data()[i][i2] + other.data()[i][i2];
            }
        }
        return new MatrixD(dArr);
    }

    public MatrixD add(double val) {
        double[][] dArr = (double[][]) Array.newInstance(Double.TYPE, new int[]{numRows(), numCols()});
        int numRows = numRows();
        int numCols = numCols();
        for (int i = 0; i < numRows; i++) {
            for (int i2 = 0; i2 < numCols; i2++) {
                dArr[i][i2] = data()[i][i2] + val;
            }
        }
        return new MatrixD(dArr);
    }

    public MatrixD subtract(MatrixD other) {
        double[][] dArr = (double[][]) Array.newInstance(Double.TYPE, new int[]{numRows(), numCols()});
        int numRows = numRows();
        int numCols = numCols();
        for (int i = 0; i < numRows; i++) {
            for (int i2 = 0; i2 < numCols; i2++) {
                dArr[i][i2] = data()[i][i2] - other.data()[i][i2];
            }
        }
        return new MatrixD(dArr);
    }

    public MatrixD subtract(double val) {
        double[][] dArr = (double[][]) Array.newInstance(Double.TYPE, new int[]{numRows(), numCols()});
        int numRows = numRows();
        int numCols = numCols();
        for (int i = 0; i < numRows; i++) {
            for (int i2 = 0; i2 < numCols; i2++) {
                dArr[i][i2] = data()[i][i2] - val;
            }
        }
        return new MatrixD(dArr);
    }

    public MatrixD times(MatrixD other) {
        if (numCols() != other.numRows()) {
            throw new IllegalArgumentException("Attempted to multiply matrices of invalid dimensions (AB) where A is " + numRows() + "x" + numCols() + ", B is " + other.numRows() + "x" + other.numCols());
        }
        int numCols = numCols();
        int numRows = numRows();
        int numCols2 = other.numCols();
        double[][] dArr = (double[][]) Array.newInstance(Double.TYPE, new int[]{numRows, numCols2});
        for (int i = 0; i < numRows; i++) {
            for (int i2 = 0; i2 < numCols2; i2++) {
                for (int i3 = 0; i3 < numCols; i3++) {
                    double[] dArr2 = dArr[i];
                    dArr2[i2] = dArr2[i2] + (data()[i][i3] * other.data()[i3][i2]);
                }
            }
        }
        return new MatrixD(dArr);
    }

    public MatrixD times(double f) {
        double[][] dArr = (double[][]) Array.newInstance(Double.TYPE, new int[]{numRows(), numCols()});
        for (int i = 0; i < numRows(); i++) {
            for (int i2 = 0; i2 < numCols(); i2++) {
                dArr[i][i2] = data()[i][i2] * f;
            }
        }
        return new MatrixD(dArr);
    }

    public double length() {
        if (numRows() == 1 || numCols() == 1) {
            double d = 0.0d;
            for (int i = 0; i < numRows(); i++) {
                int i2 = 0;
                while (i2 < numCols()) {
                    double d2 = (this.mData[i][i2] * this.mData[i][i2]) + d;
                    i2++;
                    d = d2;
                }
            }
            return Math.sqrt(d);
        }
        throw new IndexOutOfBoundsException("Not a 1D matrix ( " + numRows() + ", " + numCols() + " )");
    }

    public String toString() {
        String str = new String();
        for (int i = 0; i < numRows(); i++) {
            String str2 = new String();
            for (int i2 = 0; i2 < numCols(); i2++) {
                str2 = str2 + String.format("%.4f", new Object[]{Double.valueOf(data()[i][i2])});
                if (i2 < numCols() - 1) {
                    str2 = str2 + ", ";
                }
            }
            str = str + str2;
            if (i < numRows() - 1) {
                str = str + "\n";
            }
        }
        return str + "\n";
    }

    public static void test() {
        Log.e("MatrixD", "Hello2 matrix");
        MatrixD matrixD = new MatrixD(new double[][]{new double[]{Servo.MAX_POSITION, 0.0d, -2.0d}, new double[]{0.0d, 3.0d, -1.0d}});
        Log.e("MatrixD", "Hello3 matrix");
        Log.e("MatrixD", "A = \n" + matrixD.toString());
        MatrixD matrixD2 = new MatrixD(new double[][]{new double[]{0.0d, 3.0d}, new double[]{-2.0d, -1.0d}, new double[]{0.0d, 4.0d}});
        Log.e("MatrixD", "B = \n" + matrixD2.toString());
        Log.e("MatrixD", "A transpose = " + matrixD.transpose().toString());
        Log.e("MatrixD", "B transpose = " + matrixD2.transpose().toString());
        Log.e("MatrixD", "AB = \n" + matrixD.times(matrixD2).toString());
        matrixD = matrixD2.times(matrixD);
        Log.e("MatrixD", "BA = \n" + matrixD.toString());
        Log.e("MatrixD", "BA*2 = " + matrixD.times(2.0d).toString());
        Log.e("MatrixD", "BA submatrix 3,2,0,1 = " + matrixD.submatrix(3, 2, 0, 1));
        Log.e("MatrixD", "BA submatrix 2,1,1,2 = " + matrixD.submatrix(2, 1, 1, 2));
    }
}
