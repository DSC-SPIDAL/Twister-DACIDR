package cgl.imr.samples.dacidr.pwa.large.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MergeTwoMatrixByCols {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if (args.length != 7) {
			System.out.println("Input: [input left matrix path] [input right matrix path] [row]" +
					"[buffer size] [left col] [right col] [output matrix path]");
			System.exit(1);
		}
		String inputLeftPath = args[0];
		String inputRightPath = args[1];
		int row = Integer.parseInt(args[2]);
		int bufferSize = Integer.parseInt(args[3]);
		int leftCol = Integer.parseInt(args[4]);
		int rightCol = Integer.parseInt(args[5]);
		String outputPath = args[6];
		
		DataOutputStream dout = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(outputPath)));
		DataInputStream dinLeft =
				new DataInputStream(
						new BufferedInputStream(
								new FileInputStream(inputLeftPath)));
		DataInputStream dinRight =
				new DataInputStream(
						new BufferedInputStream(
								new FileInputStream(inputRightPath)));
		int iterNum = row / bufferSize;
		int totalSize = leftCol + rightCol;
		for (int iter = 0; iter < iterNum; ++iter) {
			System.out.println("##### iter num : " + iter);
			short[][] matrix = new short[bufferSize][totalSize];
			for (int i = 0; i < bufferSize; ++i) {
				for (int j = 0; j < totalSize; ++j) {
					if (j < leftCol) {
						matrix[i][j] = dinLeft.readShort();
					}
					else {
						matrix[i][j] = dinRight.readShort();
					}
				}

			}

			for (int k = 0; k < bufferSize; ++k) {
				for (int l = 0; l < totalSize; ++l) {
					dout.writeShort(matrix[k][l]);
				}
			}
		}
		dinLeft.close();
		dinRight.close();
		dout.close();
	}
}
