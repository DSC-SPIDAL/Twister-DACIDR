package cgl.imr.samples.dacidr.pwa.large.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MatrixTranspose {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if (args.length != 4) {
			System.out.println("Input: [input matrix path] [row]" +
					"[col] [output matrix path]");
			System.exit(1);
		}
		String inputPath = args[0];
		int row = Integer.parseInt(args[1]);
		int col = Integer.parseInt(args[2]);
		String outputPath = args[3];
		
		short[][] matrix = new short[row][col];
		DataInputStream din = new DataInputStream(
				new BufferedInputStream(new FileInputStream(inputPath)));
		for (int i = 0; i < row; ++i) {
			for (int j = 0; j < col; ++j) {
				matrix[i][j] = din.readShort();
			}
		}
		din.close();
		DataOutputStream dout = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(outputPath)));
		for (int i = 0; i < col; ++i) {
			for (int j = 0; j < row; ++j) {
				dout.writeShort(matrix[j][i]);
			}
		}
		dout.close();
	}

}
