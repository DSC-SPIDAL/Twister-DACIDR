package cgl.imr.samples.dacidr.wdasmacof.vary;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Test {

	public static double N = 50000;
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if (args.length != 3) {
			System.out.println("Usage: ");
			System.out.println("[1. Input Matrix]");
			System.out.println("[2. row]");
			System.out.println("[3. col]");
			System.exit(0);
		}
		
		String inputMatrix = args[0];
		int row = Integer.parseInt(args[1]);
		int col = Integer.parseInt(args[2]);
		
		double[][] delta = new double[row][col];
		DataInputStream din = new DataInputStream(
				new BufferedInputStream(new FileInputStream(inputMatrix)));
		for (int i = 0; i < row; ++i) {
			for (int j = 0; j < col; ++j) {
				delta[i][j] = din.readShort() / (double) Short.MAX_VALUE;
			}
		}

		double[][] X = DAMDS2.generateInitMapping(col, 3);
		long startTime1 = System.currentTimeMillis();
		for (int i = 0; i < row; ++i) {
			for (int j = 0; j < col; ++j) {
				if (i != j) {
					double dist = CalcBCMapTask.calculateDistance(X, 3, i, j);
					
					delta[i][j] = dist == 0 ? 0 : -delta[i][j] / dist;
					delta[i][i] += -delta[i][j];
				}
			}
		}
		System.out.println("BofZ time: " + (System.currentTimeMillis() - startTime1));
		long startTime = System.currentTimeMillis();
		double[][] result = 
				MatrixUtils.matrixMultiply(delta, X, row, 3, col, 64);
		//MatrixUtils.naiveMM(delta, X, row, 3, col);
		long endTime = System.currentTimeMillis();
		System.out.println((endTime - startTime) + "milli seconds");
		din.close();
	}
}
