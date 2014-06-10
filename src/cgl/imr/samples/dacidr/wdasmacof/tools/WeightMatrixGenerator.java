package cgl.imr.samples.dacidr.wdasmacof.tools;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.List;

import edu.indiana.salsahpc.Alphabet;
import edu.indiana.salsahpc.Sequence;
import edu.indiana.salsahpc.SequenceParser;

public class WeightMatrixGenerator {

	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if(args.length != 7){
			System.out.println("Input: [Input alignment length matrix] [Input Row Fasta File]" +
					"[Input Col Fasta File]" +
					"[output weighted matrix] [row] [col] [length cut]");
			System.exit(1);
		}
		
		String lengthMatrix = args[0];
		String fastaFile1 = args[1];
		String fastaFile2 = args[2];
		String outputWeightMatrix = args[3];
		int row = Integer.parseInt(args[4]);
		int col = Integer.parseInt(args[5]);
		double cut = Double.parseDouble(args[6]);
		
		List<Sequence> seqs1 = SequenceParser.parse(fastaFile1, Alphabet.Protein);
		List<Sequence> seqs2 = SequenceParser.parse(fastaFile2, Alphabet.Protein);
		DataInputStream din = new DataInputStream(
				new BufferedInputStream(
						new FileInputStream(lengthMatrix)));
		DataOutputStream dout = new DataOutputStream(
				new BufferedOutputStream(
						new FileOutputStream(outputWeightMatrix)));
		double count = 0;
		double rowCount = 0;
		for(int i = 0; i < row; ++i){
			boolean flag = true;
			for(int j = 0; j < col; ++j){
				double len = Math.min(seqs1.get(i).toString().length(),
						seqs2.get(j).toString().length());
				double per = (double)din.readShort() / len;
				if(i == j)
					dout.writeShort(1);
				else{
					if(per > cut){
						dout.writeShort(1);
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
		din.close();
		dout.close();
		DecimalFormat twoDForm = new DecimalFormat("#.####");
		System.out.println("There are totally " + twoDForm.format(count / (double)row /(double) col * 100.0) + "% is 0");
		System.out.println("There are totally " + rowCount +" rows are all 0");
		
	}

}
