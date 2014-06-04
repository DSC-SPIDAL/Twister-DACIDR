package cgl.imr.samples.dacidr.pwa.large.partial;

import org.safehaus.uuid.UUIDGenerator;

import cgl.imr.base.TwisterException;
import cgl.imr.base.TwisterMonitor;
import cgl.imr.base.impl.JobConf;
import cgl.imr.client.TwisterDriver;

/**
 * @author Yang Ruan (yangruan at cs dot indiana dot edu)
 */

public class PairWiseAlignment {

	private static UUIDGenerator uuidGen = UUIDGenerator.getInstance();

	public static double drivePWAMapReduce(int numOfMapTasks,
			int numOfSequences, int numOfPartitions, String dataDir,
			String geneBlockPrefix, String outputPrefix, String fullFastaFile, String type, String matrixType, String seqType)
	throws TwisterException {

		// Total computable blocks (n * (n + 1)) / 2 , i.e. lower or upper triangle blocks including diagonal blocks
		int numOfBlocks = (numOfPartitions * (numOfPartitions + 1)) / 2;

		System.out.println("Number of partitions: " + numOfPartitions);
		System.out.println("Number of computable blocks: " + numOfBlocks);

		// JobConfigurations
		JobConf jobConf = new JobConf("Pairwise Alignment-" + uuidGen.generateTimeBasedUUID());
		jobConf.setMapperClass(PWAMapTask.class);

		jobConf.setNumMapTasks(numOfMapTasks);
		jobConf.setNumReduceTasks(0);

		jobConf.addProperty("dataDir", dataDir);
		jobConf.addProperty("geneBlockPrefix", geneBlockPrefix);
		jobConf.addProperty("outputPrefix", outputPrefix);
		jobConf.addProperty("FullFastaFile", fullFastaFile);
		jobConf.addProperty("numOfSequences", String.valueOf(numOfSequences));
		jobConf.addProperty("numOfPartitions", String.valueOf(numOfPartitions));
		jobConf.addProperty("Type", type);
		jobConf.addProperty("MatrixType", matrixType);
		jobConf.addProperty("SeqType", seqType);

		TwisterDriver driver = null;
		TwisterMonitor monitor;
		try {
			driver = new TwisterDriver(jobConf);
			driver.configureMaps();
			//System.out.println("Into mapreduce");
			monitor = driver.runMapReduce();
			monitor.monitorTillCompletion();
			driver.close();

		} catch (Exception e) {
			if (driver != null) {
				driver.close();
			}
			throw new TwisterException(e);
		}
		return monitor.getTotalSequentialTimeSeconds();
	}

	public static void main(String args[]) {

		if (args.length != 10) {
			System.err
			.println("args:  [num_of_map_tasks] [col_sequence_count]" +
			"[num_of_partitions] [row_fasta_dir] [gene_block_prefix] [tmp_output_prefix] [col_fasta_file] [aligner type]" +
			"[score matrix type] [sequence type]");
			System.exit(2);
		}

		//String partitionFile = args[0];
		int numOfMapTasks = Integer.parseInt(args[0]);
		int numOfSeqs = Integer.parseInt(args[1]);
		int numOfPartitions = Integer.parseInt(args[2]);
		String dataDir = args[3];
		String geneBlockPrefix = args[4];
		String outputPrefix = args[5];
		String fullFastaFile = args[6];
		String type = args[7];
		String matrixType = args[8];
		String seqType = args[9];

		if ( !outputPrefix.endsWith("_") ) {
			System.err.println("ERROR: The output file prefix must end with an underscore (\"_\").");
			System.exit(2);
		}
		if( !type.equals("NW") && !type.equals("SWG")){
			System.err.println("The type must be NW or SWG");
			System.exit(2);
		}
		if(!matrixType.equals("blo") && !matrixType.equals("edn")){
			System.err.println("The matrix type must be blo or end");
			System.exit(2);
		}
		if(!seqType.equals("DNA") && !seqType.equals("RNA")){
			System.err.println("The matrix type must be DNA or RNA");
			System.exit(2);
		}
		
		long beforeTime = System.currentTimeMillis();
		double sequentialTime = 0;
		try {
			sequentialTime = drivePWAMapReduce(numOfMapTasks, numOfSeqs,
					numOfPartitions, dataDir, geneBlockPrefix, outputPrefix, fullFastaFile, type, matrixType, seqType);

		} catch (TwisterException e) {
			e.printStackTrace();
		}

		double timeInSeconds = ((double) (System.currentTimeMillis() - beforeTime)) / 1000;
		System.out.println("Total Time for Pairwise Alignment: " + timeInSeconds + " Seconds");
		System.out.println("Sequential Time = (Sigma mapTime + Sigma reduce time: " + sequentialTime + " Seconds");
		System.exit(0);
	}
}