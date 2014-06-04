package cgl.imr.samples.dacidr.pwa.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import edu.indiana.salsahpc.Sequence;

public class FileOperation {
	
	public static void writeMatrixToFile(String filePath, double[][] matrix) throws IOException {
		DataOutputStream dout = new DataOutputStream(
				new BufferedOutputStream(new FileOutputStream(filePath)));
		for (int i = 0; i < matrix.length; ++i)
			for (int j = 0; j < matrix[i].length; ++j) 
				dout.writeShort((short) (matrix[i][j] * Short.MAX_VALUE));
		dout.close();
	}
	
	public static List<Integer> readGroups(String filePath, 
			String separator) throws IOException{
		List<Integer> groups = new ArrayList<Integer>();
		BufferedReader br = new BufferedReader(
				new FileReader(filePath));
		String line;
		String[] tokens;
		
		//int id = 0;
		//br.readLine();
		while((line = br.readLine())!= null){
			tokens = line.split(separator);
			groups.add(Integer.parseInt(tokens[1]));
		}
		br.close();
		//writeToFile(filePath, points);
		return groups;
	}
	
	public static HashSet<String> getStringSet(String fileName) throws Exception{
		HashSet<String> names = new HashSet<String>();
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line = null;
		while((line = br.readLine())!= null){
			if(line.length() > 0)
				names.add(line);
		}
		br.close();
		return names;
	}
	
	public static String[] getLines(String fileName, int dataSize) throws IOException{
		BufferedReader br = new BufferedReader(new FileReader(fileName));
		String line = null;
		String[] lines = new String[dataSize];
		int count = 0;
		while((line = br.readLine()) != null){
			lines[count] = line;
			count++;
		}
		br.close();
		
		return lines;
	}
	
	public static void writeSequenceToFile(List<Sequence> sequences, String filePath) throws IOException{
		BufferedWriter bw = new BufferedWriter(new FileWriter(filePath));
		for(Sequence s: sequences){
			bw.write(s.getId() + "\n");
			bw.write(s.toString() + "\n");
		}
		bw.flush();
		bw.close();}
}
