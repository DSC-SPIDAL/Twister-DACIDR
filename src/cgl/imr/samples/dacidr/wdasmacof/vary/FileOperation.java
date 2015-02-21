package cgl.imr.samples.dacidr.wdasmacof.vary;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

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
		short[][] weights = new short[row][col];
		try (FileChannel fc = (FileChannel) Files.newByteChannel(Paths.get(fileName), StandardOpenOption.READ)) {
			long pos = 0;
			MappedByteBuffer mappedBytes = fc.map(FileChannel.MapMode.READ_ONLY, pos,
					row * col * 2); // 2 for short data
			mappedBytes.order(ByteOrder.BIG_ENDIAN);

			for (int i = 0; i < row; i++) {
				for (int j = 0; j < col; j++) {
					int element = i * col + j; // element position - not the byte position
					// We assume that Matrix values in binary files are stored in short value.
					weights[i][j] = mappedBytes.getShort(element*2);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return weights;
	}
}
