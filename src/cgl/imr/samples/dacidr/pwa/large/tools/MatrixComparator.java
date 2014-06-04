package cgl.imr.samples.dacidr.pwa.large.tools;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class MatrixComparator {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if (args.length != 8) {
			System.out.println("Input: [input matrix 1 path] [input matrix 2 path]" +
					"[row 1] [col 1] [row 2] [col 2] [starting row] [starting col]");
			System.exit(1);
		}
		
		DataInputStream din1 = new DataInputStream(
				new BufferedInputStream(new FileInputStream(args[0])));

		DataInputStream din2 = new DataInputStream(
				new BufferedInputStream(new FileInputStream(args[1])));
		int row1 = Integer.parseInt(args[2]);
		int col1 = Integer.parseInt(args[3]);
		int row2 = Integer.parseInt(args[4]);
		int col2 = Integer.parseInt(args[5]);
		int startingRow = Integer.parseInt(args[6]);
		int startingCol = Integer.parseInt(args[7]);
		for (int i = 0; i < row1; ++i) {
			for (int j = 0; j < col1; ++j) {
				short val1 = din1.readShort();
				if (i >= startingRow && i < startingRow + row2
						&& j >= startingCol && j < startingCol + col2) {
					short val2 = din2.readShort();
					if (2 * Math.abs(val1 - val2) / (double) (val1 + val2) > 0.20) {
						System.out.println(
								"row :" + i 
								+ "\tcol: " + j 
								+ "###val1: " + val1 
								+ "\tval2: " + val2);
					}
				}
			}
		}
		din1.close();
		din2.close();
	}

}
