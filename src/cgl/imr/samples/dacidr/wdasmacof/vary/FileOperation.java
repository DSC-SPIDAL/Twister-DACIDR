package cgl.imr.samples.dacidr.wdasmacof.vary;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;

public class FileOperation {
	public static int[][] loadV(String fileName, int row, int col) throws IOException{
		DataInputStream din = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)));
		int[][] VPlus = new int[row][col];
		for(int i = 0; i < row; ++i)
			for(int j = 0; j < col; ++j)
				VPlus[i][j] = (int) din.readDouble();
		din.close();
		return VPlus;
	}
	
	public static double[][] loadVPlus(String fileName, int row, int col) throws IOException{
		DataInputStream din = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)));
		double[][] VPlus = new double[row][col];
		for(int i = 0; i < row; ++i)
			for(int j = 0; j < col; ++j)
				VPlus[i][j] = din.readDouble();
		din.close();
		return VPlus;
	}
	
	public static short[][] loadWeights(String fileName, int row, int col) throws IOException{
		DataInputStream din = new DataInputStream(new BufferedInputStream(new FileInputStream(fileName)));
		short[][] weights = new short[row][col];
		for(int i = 0; i < row; ++i)
			for(int j = 0; j < col; ++j)
				weights[i][j] = din.readShort();
		din.close();
		return weights;
	}
}
