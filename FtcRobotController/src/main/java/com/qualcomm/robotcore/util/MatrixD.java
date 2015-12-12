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

    public MatrixD(double[] dataBuffer, int rows, int cols) {
        this(rows, cols);
        if (dataBuffer == null) {
            throw new IllegalArgumentException("Attempted to initialize MatrixF with null array");
        } else if (dataBuffer.length != rows * cols) {
            throw new IllegalArgumentException("Attempted to initialize MatrixF with rows/cols not matching init data");
        } else {
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    this.mData[row][col] = dataBuffer[(cols * row) + col];
                }
            }
        }
    }

    public MatrixD(float[] dataBuffer, int rows, int cols) {
        this(rows, cols);
        if (dataBuffer == null) {
            throw new IllegalArgumentException("Attempted to initialize MatrixF with null array");
        } else if (dataBuffer.length != rows * cols) {
            throw new IllegalArgumentException("Attempted to initialize MatrixF with rows/cols not matching init data");
        } else {
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    this.mData[row][col] = (double) dataBuffer[(cols * row) + col];
                }
            }
        }
    }

    public MatrixD(double[][] dataBuffer) {
        int row = 0;
        this.mData = dataBuffer;
        if (this.mData == null) {
            throw new IllegalArgumentException("Attempted to initialize MatrixF with null array");
        }
        this.mRows = this.mData.length;
        if (this.mRows <= 0) {
            throw new IllegalArgumentException("Attempted to initialize MatrixF with 0 rows");
        }
        this.mCols = this.mData[0].length;
        while (row < this.mRows) {
            if (this.mData[row].length != this.mCols) {
                throw new IllegalArgumentException("Attempted to initialize MatrixF with rows of unequal length");
            }
            row++;
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
            double[][] dataBuffer = (double[][]) Array.newInstance(Double.TYPE, rows, cols);
            for (int row = 0; row < rows; row++) {
                for (int col = 0; col < cols; col++) {
                    dataBuffer[row][col] = data()[rowOffset + row][colOffset + col];
                }
            }
            return new MatrixD(dataBuffer);
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
        int rows = this.mRows;
        int cols = this.mCols;
        double[][] dataBuffer = (double[][]) Array.newInstance(Double.TYPE, cols, rows);
        for (int col = 0; col < cols; col++) {
            for (int row = 0; row < rows; row++) {
                dataBuffer[col][row] = this.mData[row][col];
            }
        }
        return new MatrixD(dataBuffer);
    }

    public MatrixD add(MatrixD other) {
        double[][] dataBuffer = (double[][]) Array.newInstance(Double.TYPE, numRows(), numCols());
        int numRows = numRows();
        int numCols = numCols();
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                dataBuffer[row][col] = data()[row][col] + other.data()[row][col];
            }
        }
        return new MatrixD(dataBuffer);
    }

    public MatrixD add(double val) {
        double[][] dataBuffer = (double[][]) Array.newInstance(Double.TYPE,numRows(), numCols());
        int numRows = numRows();
        int numCols = numCols();
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                dataBuffer[row][col] = data()[row][col] + val;
            }
        }
        return new MatrixD(dataBuffer);
    }

    public MatrixD subtract(MatrixD other) {
        double[][] dataBuffer = (double[][]) Array.newInstance(Double.TYPE, numRows(), numCols());
        int numRows = numRows();
        int numCols = numCols();
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                dataBuffer[row][col] = data()[row][col] - other.data()[row][col];
            }
        }
        return new MatrixD(dataBuffer);
    }

    public MatrixD subtract(double val) {
        double[][] dataBuffer = (double[][]) Array.newInstance(Double.TYPE, numRows(), numCols());
        int numRows = numRows();
        int numCols = numCols();
        for (int row = 0; row < numRows; row++) {
            for (int col = 0; col < numCols; col++) {
                dataBuffer[row][col] = data()[row][col] - val;
            }
        }
        return new MatrixD(dataBuffer);
    }

    public MatrixD times(MatrixD other) {
        if (numCols() != other.numRows()) {
            throw new IllegalArgumentException("Attempted to multiply matrices of invalid dimensions (AB) where A is " + numRows() + "x" + numCols() + ", B is " + other.numRows() + "x" + other.numCols());
        }
        int numCols = numCols();
        int numRows = numRows();
        int numCols2 = other.numCols();
        double[][] dataBuffer = (double[][]) Array.newInstance(Double.TYPE, numRows, numCols2);
        for (int row = 0; row < numRows; row++) {
            for (int col2 = 0; col2 < numCols2; col2++) {
                for (int col = 0; col < numCols; col++) {
                    double[] dataBuffer2 = dataBuffer[row];
                    dataBuffer2[col2] = dataBuffer2[col2] + (data()[row][col] * other.data()[col][col2]);
                }
            }
        }
        return new MatrixD(dataBuffer);
    }

    public MatrixD times(double f) {
        double[][] dataBuffer = (double[][]) Array.newInstance(Double.TYPE, numRows(), numCols());
        for (int row = 0; row < numRows(); row++) {
            for (int col = 0; col < numCols(); col++) {
                dataBuffer[row][col] = data()[row][col] * f;
            }
        }
        return new MatrixD(dataBuffer);
    }

    public double length() {
        if (numRows() == 1 || numCols() == 1) {
            double d = 0.0d;
            for (int row = 0; row < numRows(); row++) {
                for (int col = 0; col < numCols(); col++) {
                    d += (this.mData[row][col] * this.mData[row][col]);
                }
            }
            return Math.sqrt(d);
        }
        throw new IndexOutOfBoundsException("Not a 1D matrix ( " + numRows() + ", " + numCols() + " )");
    }

    public String toString() {
        String matrixString = new String();
        for (int row = 0; row < numRows(); row++) {
            String colString = new String();
            for (int col = 0; col < numCols(); col++) {
                colString = colString + String.format("%.4f", data()[row][col]);
                if (col < numCols() - 1) {
                    colString = colString + ", ";
                }
            }
            matrixString += colString;
            if (row < numRows() - 1) {
                matrixString = matrixString + "\n";
            }
        }
        return matrixString + "\n";
    }

    public static void test() {
        Log.e("MatrixD", "Hello2 matrix");
        MatrixD matrixD = new MatrixD(new double[][]{new double[]{Servo.MAX_POSITION, 0.0d, -2.0d}, new double[]{0.0d, 3.0d, -1.0d}});
        Log.e("MatrixD", "Hello3 matrix");
        Log.e("MatrixD", "A = \n" + matrixD);
        MatrixD matrixD2 = new MatrixD(new double[][]{new double[]{0.0d, 3.0d}, new double[]{-2.0d, -1.0d}, new double[]{0.0d, 4.0d}});
        Log.e("MatrixD", "B = \n" + matrixD2);
        Log.e("MatrixD", "A transpose = " + matrixD.transpose());
        Log.e("MatrixD", "B transpose = " + matrixD2.transpose());
        Log.e("MatrixD", "AB = \n" + matrixD.times(matrixD2));
        matrixD = matrixD2.times(matrixD);
        Log.e("MatrixD", "BA = \n" + matrixD);
        Log.e("MatrixD", "BA*2 = " + matrixD.times(2.0d));
        Log.e("MatrixD", "BA submatrix 3,2,0,1 = " + matrixD.submatrix(3, 2, 0, 1));
        Log.e("MatrixD", "BA submatrix 2,1,1,2 = " + matrixD.submatrix(2, 1, 1, 2));
    }
}
