package com.qualcomm.robotcore.util;

import android.util.Log;
import com.qualcomm.robotcore.hardware.Servo;
import java.lang.reflect.Array;

public class MatrixD {
    protected int mCols;
    protected double[][] mData;
    protected int mRows;

    public MatrixD(int rows, int cols) {
        this((double[][]) Array.newInstance(Double.TYPE, rows, cols));
    }

    public MatrixD(double[] init, int rows, int cols) {
        this(rows, cols);
        if (init == null) {
            throw new IllegalArgumentException("Attempted to initialize MatrixF with null array");
        } else if (init.length != rows * cols) {
            throw new IllegalArgumentException("Attempted to initialize MatrixF with rows/cols not matching init data");
        } else {
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    this.mData[row][col] = init[(cols * row) + col];
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
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    this.mData[row][col] = (double) init[(cols * row) + col];
                }
            }
        }
    }

    public MatrixD(double[][] init) {
        this.mData = init;
        if (this.mData == null) {
            throw new IllegalArgumentException("Attempted to initialize MatrixF with null array");
        }

        this.mRows = this.mData.length;
        if (this.mRows <= 0) {
            throw new IllegalArgumentException("Attempted to initialize MatrixF with 0 rows");
        }

        this.mCols = this.mData[0].length;
        for (int row = 0; row < mRows; row++) {
            if(mData[row].length != mCols) {
                throw new IllegalArgumentException("Attempted to initialize MatrixF with rows of unequal length");
            }
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
            double[][] matrixBuffer = (double[][]) Array.newInstance(Double.TYPE, rows, cols);
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    matrixBuffer[row][col] = data()[rowOffset + row][colOffset + col];
                }
            }
            return new MatrixD(matrixBuffer);
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
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    data()[rowOffset + row][colOffset + col] = inData.data()[row][col];
                }
            }
            return true;
        }
    }

    public MatrixD transpose() {
        double[][] matrixBuffer = (double[][]) Array.newInstance(Double.TYPE, mCols, mRows);
        for (int col = 0; col < mCols; col++) {
            for (int row = 0; row < mRows; row++) {
                matrixBuffer[col][row] = mData[row][col];
            }
        }
        return new MatrixD(matrixBuffer);
    }

    public MatrixD add(MatrixD other) {
        double[][] matrixBuffer = (double[][]) Array.newInstance(Double.TYPE, numRows(), numCols());
        for (int row = 0; row < numRows(); row++) {
            for (int col = 0; col < numCols(); col++) {
                matrixBuffer[row][col] = data()[row][col] + other.data()[row][col];
            }
        }
        return new MatrixD(matrixBuffer);
    }

    public MatrixD add(double val) {
        double[][] matrixBuffer = (double[][]) Array.newInstance(Double.TYPE, numRows(), numCols());
        for (int row = 0; row < numRows(); row++) {
            for (int col = 0; col < numCols(); col++) {
                matrixBuffer[row][col] = data()[row][col] + val;
            }
        }
        return new MatrixD(matrixBuffer);
    }

    public MatrixD subtract(MatrixD other) {
        double[][] matrixBuffer = (double[][]) Array.newInstance(Double.TYPE, numRows(), numCols());
        for (int row = 0; row < numRows(); row++) {
            for (int col = 0; col < numCols(); col++) {
                matrixBuffer[row][col] = data()[row][col] - other.data()[row][col];
            }
        }
        return new MatrixD(matrixBuffer);
    }

    public MatrixD subtract(double val) {
        double[][] matrixBuffer = (double[][]) Array.newInstance(Double.TYPE, numRows(), numCols());
        for (int row = 0; row < numRows(); row++) {
            for (int col = 0; col < numCols(); col++) {
                matrixBuffer[row][col] = data()[row][col] - val;
            }
        }
        return new MatrixD(matrixBuffer);
    }

    public MatrixD times(MatrixD other) {
        if (numCols() != other.numRows()) {
            throw new IllegalArgumentException("Attempted to multiply matrices of invalid dimensions (AB) where A is " + numRows() + "x" + numCols() + ", B is " + other.numRows() + "x" + other.numCols());
        }
        double[][] matrixBuffer = (double[][]) Array.newInstance(Double.TYPE, numRows(), other.numCols());
        for (int row = 0; row < numRows(); row++) {
            for (int otherCol = 0; otherCol < other.numCols(); otherCol++) {
                for (int col = 0; col < numCols(); col++) {
                    double[] matrixBufferRow = matrixBuffer[row];
                    matrixBufferRow[otherCol] = matrixBufferRow[otherCol] + (data()[row][col] * other.data()[col][otherCol]);
                }
            }
        }
        return new MatrixD(matrixBuffer);
    }

    public MatrixD times(double f) {
        double[][] matrixBuffer = (double[][]) Array.newInstance(Double.TYPE, numRows(), numCols());
        for (int row = 0; row < numRows(); row++) {
            for (int col = 0; col < numCols(); col++) {
                matrixBuffer[row][col] = data()[row][col] * f;
            }
        }
        return new MatrixD(matrixBuffer);
    }

    public double length() {
        if (numRows() == 1) {
            return numRows();
        } else if (numCols() == 1) {
            return numRows();
        } else {
            throw new IndexOutOfBoundsException("Not a 1D matrix ( " + numRows() + ", " + numCols() + " )");
        }
    }

    public String toString() {
        String string = "";
        for (int row = 0; row < numRows(); row++) {
            for (int col = 0; col < numCols(); col++) {
                string +=  String.format("%.4f", data()[row][col]);
                if (col < numCols() - 1) {
                    string = string + ", ";
                }
            }
            if (row < numRows() - 1) {
                string = string + "\n";
            }
        }
        return string + "\n";
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
