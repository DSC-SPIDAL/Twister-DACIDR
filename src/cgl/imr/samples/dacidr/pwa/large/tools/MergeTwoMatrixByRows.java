package cgl.imr.samples.dacidr.pwa.large.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MergeTwoMatrixByRows {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if (args.length != 6) {
			System.out.println("Input: [input upper matrix path] [input lower matrix path] [upper row]" +
					"[lower row] [col] [output matrix path]");
			System.exit(1);
		}
		String inputUpperPath = args[0];
		String inputLowerPath = args[1];
		int upperRow = Integer.parseInt(args[2]);
		int lowerRow = Integer.parseInt(args[3]);
		int col = Integer.parseInt(args[4]);
		String outputPath = args[5];

		DataOutputStream dout = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(outputPath)));
		DataInputStream dinUpper =
				new DataInputStream(
						new BufferedInputStream(
								new FileInputStream(inputUpperPath)));
		DataInputStream dinLower =
				new DataInputStream(
						new BufferedInputStream(
								new FileInputStream(inputLowerPath)));
		for (int i = 0; i < upperRow; ++i) {
			for (int j = 0; j < col; ++j) {
				dout.writeShort(dinUpper.readShort());
			}
		}
		for (int i = 0; i < lowerRow; ++i) {
			for (int j = 0; j < col; ++j) {
				dout.writeShort(dinLower.readShort());
			}
		}

		dinUpper.close();
		dinLower.close();
		dout.close();
	}
}
