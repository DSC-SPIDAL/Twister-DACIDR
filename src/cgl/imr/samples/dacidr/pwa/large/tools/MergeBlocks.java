package cgl.imr.samples.dacidr.pwa.large.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class MergeBlocks {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if (args.length != 6) {
			System.out.println("Input: [Input Matrix Folder] [Input Matrix Prefix (ends with _)] [row block number]" +
					"[block size] [buffer size (exact divison to block size)] [output matrix pat" +
					"h]");
			System.exit(1);
		}
		String inputPrefix = (args[0] + "/" + args[1]).replaceAll("//", "/");
		int blockNum = Integer.parseInt(args[2]);
		int blockSize = Integer.parseInt(args[3]);
		int bufferSize = Integer.parseInt(args[4]);
		String outputFile = args[5];
		
		int iterNum = blockSize / bufferSize;
		int totalSize = blockNum * blockSize;
		DataOutputStream dout = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(outputFile)));
		for (int i = 0; i < blockNum; ++i) {
			System.out.println("########### Writing Block Row " + i +  "#############");
			DataInputStream[] dins = new DataInputStream[blockNum];
			for (int j = 0; j < blockNum; ++j) {
				dins[j] = new DataInputStream(
					new BufferedInputStream(new FileInputStream(inputPrefix + i + "_" + j + "_pid.bin")));
			}
			for (int iter = 0; iter < iterNum; ++iter) {
				System.out.println("##### iter num : " + iter);
				short[][] matrix = new short[bufferSize][totalSize];
				for (int j = 0; j < blockNum; ++j) {
					System.out.println("### Writing Block Col " + j);
					for (int k = 0; k < bufferSize; ++k) {
						for (int l = 0; l < blockSize; ++l) {
							matrix[k][j * blockSize + l] = dins[j].readShort();
						}
					}
				}
				
				for (int k = 0; k < bufferSize; ++k) {
					for (int l = 0; l < totalSize; ++l) {
						dout.writeShort(matrix[k][l]);
					}
				}
			}
			for (int j = 0; j < blockNum; ++j) {
				dins[j].close();
			}
		}
		dout.close();
	}
}
