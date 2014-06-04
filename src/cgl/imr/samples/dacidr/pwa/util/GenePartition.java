package cgl.imr.samples.dacidr.pwa.util;

import java.util.List;
import java.io.BufferedWriter;
import java.io.FileWriter;
import edu.indiana.salsahpc.*;

/**
 * Yang Ruan (yangruan at cs dot indiana dot edu)
 * Saliya Ekanayake (sekanaya at cs dot indiana dot edu)
 */

public class GenePartition {

	public static Sequence[] ConvertSequences(List<Sequence> listOfSequences){
		Sequence[] sequences = new Sequence[listOfSequences.size()];
		for(int i = 0; i < listOfSequences.size(); i++){
			
			String trimmedName = listOfSequences.get(i).getId();
			if(trimmedName.startsWith(">"))
					trimmedName = trimmedName.substring(1);
			//System.out.println(trimmedName);
			Sequence sequence = new Sequence(listOfSequences.get(i).toString(), trimmedName,
					listOfSequences.get(i).getAlphabet());
			//System.out.println(listOfSequences.get(i).getId());
			sequences[i] = sequence;
		}
		return sequences;
	}
	
	public static Sequence[] ConvertSequencesWithoutTrime(List<Sequence> listOfSequences){
		Sequence[] sequences = new Sequence[listOfSequences.size()];
		for(int i = 0; i < listOfSequences.size(); i++){
			sequences[i] = listOfSequences.get(i);
		}
		return sequences;
	}
	
	public static void buildGeneBlocks(String fname, int seqCount, int partitions, String outDir,
			String blockPrefix, String idxFile, Alphabet al) throws Exception {

        //SequenceParser parser = new SequenceParser();
        Sequence[] sequences = ConvertSequencesWithoutTrime(SequenceParser.parse(fname, al));
        
        System.out.println("Total Sequences detected: " + sequences.length);
        // Integer division automatically results in the floor of numOfSequences / partitions
        int minSeqsPerPartition = seqCount / partitions;
        int remainder = seqCount % partitions;

        int count = 0; // keep track of how many sequences are read and written back to disk
        int seqsPerPartition;
        //FileOutputStream fos;
        BufferedWriter bw;
        BufferedWriter idxWriter = new BufferedWriter(new FileWriter(idxFile));
		// loop for different blocks
		for (int i = 0; i < partitions; i++) {
            //fos = new FileOutputStream(new File(outDir + blockPrefix + i));
			idxWriter.write(i + "\t");
			bw = new BufferedWriter(new FileWriter(outDir + blockPrefix +i));
            seqsPerPartition = (remainder-- <= 0 ? minSeqsPerPartition : minSeqsPerPartition + 1);
            idxWriter.write(seqsPerPartition + "\t" + seqCount + "\t" + i + "\t" + count + "\n");
			// loop for one block
			for (int j = 0; j < seqsPerPartition; j++){
				bw.write(sequences[count].getId() + "\n");
				bw.write(sequences[count].toString() + "\n");
				//FastaWriterHelper.writeSequence(fos, sequences[count]);
                count++;
			}
            bw.flush();
            bw.close();
		}
		idxWriter.flush();
		idxWriter.close();
	}

	public static void main(String[] args) {

		if (args.length < 7) {
			System.err
					.println("args:  [gene_seq_file] [sequence_count] [num_of_partitions] [out_dir] [gene_block_prefix] [output_idx file] [Alphabet]");
			System.exit(2);
		}

		String seqFileName = args[0];
		int seqCount = Integer.parseInt(args[1]);
		int partitions = Integer.parseInt(args[2]);
		String outDir = args[3];
		String geneBlockPrefix = args[4];
		String idxFile = args[5];
		Alphabet al = null;
		if(args[6].equals("RNA"))
			al = Alphabet.Protein;
		else if(args[6].equals("DNA"))
			al = Alphabet.DNA;
		else{
			System.out.println("Please input RNA or DNA as alphabet");
			System.exit(1);
		}
		

		try {
			buildGeneBlocks(seqFileName, seqCount, partitions, outDir, geneBlockPrefix, idxFile, al);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}