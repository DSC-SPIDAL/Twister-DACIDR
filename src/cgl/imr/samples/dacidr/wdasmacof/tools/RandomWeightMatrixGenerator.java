package cgl.imr.samples.dacidr.wdasmacof.tools;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Random;

public class RandomWeightMatrixGenerator {

	public static int BLOCK_SIZE = 5000;
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if(args.length != 6){
			System.out.println("Input: " +
					"[output weighted matrix] [row] [col] [percentage][symmetric (0:no; 1:yes)][weight value]");
			System.exit(1);
		}
		
		String outputWeightMatrix = args[0];
		int row = Integer.parseInt(args[1]);
		int col = Integer.parseInt(args[2]);
		double cut = Double.parseDouble(args[3]);
		int sym = Integer.parseInt(args[4]);
		short weightValue = Short.parseShort(args[5]);
		
		DataOutputStream dout = new DataOutputStream(
				new BufferedOutputStream(
						new FileOutputStream(outputWeightMatrix)));
		double count = 0;
		double rowCount = 0;
		if(sym == 0){
			for(int i = 0; i < row; ++i){
				boolean flag = true;
				for(int j = 0; j < col; ++j){
					Random r = new Random();
					if(i == j)
						dout.writeShort(1);
					else{
						double ran = r.nextDouble();

						if(ran > cut){
							dout.writeShort(weightValue);
							flag = false;
						}
						else{
							dout.writeShort(0);
							++count;
						}
					}
				}
				if(flag)
					++rowCount;
			}
		}
		else{
			short[][] W = new short[row][col];
			for(int i = 0; i < row; ++i){

				for(int j = i; j < col; ++j){
					Random r = new Random();
					if(i == j){
						W[i][j] = 1;
					}
					else{
						double ran = r.nextDouble();

						if(ran > cut){
							W[i][j] = weightValue;
							W[j][i] = weightValue;
						}
						else{
							W[i][j] = 0;
							W[j][i] = 0;
						}
					}
				}
			}

			for(int i = 0; i < row; ++i)
				for(int j = 0; j < col; ++j)
					dout.writeShort(W[i][j]);
		}
		dout.close();
		DecimalFormat twoDForm = new DecimalFormat("#.####");
		System.out.println("There are totally " + twoDForm.format(count / (double)row /(double) col * 100.0) + "% is 0");
		System.out.println("There are totally " + rowCount +" rows are all 0");	
	}
}
