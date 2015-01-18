package cgl.imr.samples.dacidr.wdasmacof.tools;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.stream.IntStream;

public class RandomWeightMatrixGenerator {

	public static int BLOCK_SIZE = 5000;
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if(args.length != 6 && args.length != 4){
			System.out.println("Input: " +
					"[output weighted matrix] [row] [col] [percentage][symmetric (0:no; 1:yes)][weight value]");
			System.out.println("\nor\n");
			System.out.println("Input: [output dir] [size] [splits] [weight value]");
			System.exit(1);
		}

		if (args.length == 4){
			generateFastSimpleWeights(args);
			return;
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

	private static void generateFastSimpleWeights(String[] args) {
		String outDir = args[0];
		int size = Integer.parseInt(args[1]);
		int splits = Integer.parseInt(args[2]);
		short weight = Short.parseShort(args[3]);

		int q = size / splits;
		int r = size % splits;

		byte [] bytes =  new byte[]{(byte)((weight>>8)&0xFF),(byte)(weight&0xFF)};
		final byte [] weightRow = new byte[size*Short.BYTES];
		IntStream.range(0,size).parallel().forEach(i -> {
			weightRow[2*i] = bytes[0];
			weightRow[2*i+1] = bytes[1];
		});

		Path file = Paths.get(outDir, "w" + weight + "_" + 0);
		String fat = file.toString();
		try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(file, StandardOpenOption.CREATE_NEW))){
			int rows = q + (r > 0 ? 1 : 0);
			for (int row = 0; row < rows; ++row){
				bos.write(weightRow);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		String thin = "";
		if (r > 0){
			file = Paths.get(outDir, "w" + weight + "_" + r);
			thin = file.toString();
			try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(file, StandardOpenOption.CREATE_NEW))){
				for (int row = 0; row < q; ++row){
					bos.write(weightRow);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		Path script = Paths.get(outDir, "gen.sh");
		try (PrintWriter writer = new PrintWriter(Files.newBufferedWriter(script, Charset.defaultCharset(), StandardOpenOption.CREATE_NEW),true)) {
			StringBuilder sb = new StringBuilder("#!/bin/bas\n");
			int end = r > 0 ? r : splits;
			for (int i = 1; i < end; ++i){
				String to = Paths.get(outDir, "w" + weight + "_" + i).toString();
				sb.append("cp ").append(fat).append(" ").append(to).append("\n");
			}
			if (r > 0){
				for (int i = r+1; i < splits; ++i){
					String to = Paths.get(outDir, "w" + weight + "_" + i).toString();
					sb.append("cp ").append(thin).append(" ").append(to).append("\n");
				}
			}
			writer.println(sb.toString());


		} catch (IOException e) {
			e.printStackTrace();
		}

		/*IntStream.range(0,splits).parallel().forEach(i->{
			Path file = Paths.get(outDir, "w" + weight + "_" + i);
			try (BufferedOutputStream bos = new BufferedOutputStream(Files.newOutputStream(file, StandardOpenOption.CREATE_NEW))){
				int rows = (i+1)*q + (i < r ? (i+1) : r);
				for (int row = 0; row < rows; ++row){
					bos.write(weightRow);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		});*/


	}
}
