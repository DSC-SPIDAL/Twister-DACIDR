package cgl.imr.samples.dacidr.pwa.large.nosym;

import cgl.imr.base.*;
import cgl.imr.base.impl.JobConf;
import cgl.imr.base.impl.MapperConf;
import cgl.imr.samples.dacidr.pwa.util.GenePartition;
import cgl.imr.samples.dacidr.pwa.util.SequenceAlignment;
import cgl.imr.types.BytesValue;
import cgl.imr.types.StringKey;
import edu.indiana.salsahpc.AlignedData;
import edu.indiana.salsahpc.AlignmentData;
import edu.indiana.salsahpc.Alphabet;
import edu.indiana.salsahpc.BioJavaWrapper;
import edu.indiana.salsahpc.DistanceType;
import edu.indiana.salsahpc.MatrixUtil;
import edu.indiana.salsahpc.Sequence;
import edu.indiana.salsahpc.SequenceParser;
import edu.indiana.salsahpc.SimilarityMatrix;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.biojava3.alignment.template.SubstitutionMatrix;
import org.biojava3.core.sequence.ProteinSequence;

/**
 * @author Yang Ruan (yangruan at cs dot indiana dot edu)
 * @author Saliya Ekanayake (sekanaya at cs dot indiana dot edu)
 */

public class PWAMapTask implements MapTask {

	private String rowDataDir;
	private String rowGeneBlockPrefix;
	private String colDataDir;
	private String colGeneBlockPrefix;

	private short gapOpen, gapExt;

	private String type;
	private String matrixType;
	private String seqType;
	//FileData fileData;

	@Override
	public void close() throws TwisterException {

	}

	@Override
	public void configure(JobConf jobConf, MapperConf mapConf)
			throws TwisterException {

		rowDataDir = jobConf.getProperty("rowDataDir");
		rowGeneBlockPrefix = jobConf.getProperty("rowGeneBlockPrefix");

		colDataDir = jobConf.getProperty("colDataDir");
		colGeneBlockPrefix = jobConf.getProperty("colGeneBlockPrefix");

		gapOpen = (short) 16;
		gapExt = (short) 4;
		type = jobConf.getProperty("Type");
		matrixType = jobConf.getProperty("MatrixType");
		seqType = jobConf.getProperty("SeqType");
		//fileData = (FileData) mapConf.getDataPartition();

	}

	@Override
	public void map(MapOutputCollector collector, Key key, Value val)
			throws TwisterException {
		Region region;
		//String inputFile = fileData.getFileName();
		try {
			region = new Region(val.getBytes());
		} catch (SerializationException e) {
			throw new TwisterException(e);
		}
		//System.out.println("Just into the Mapper");
		List<Block> blocks = region.getBlocks();

		for (Block block : blocks) {
			int rowBlockNumber = block.getRowBlockNumber();
			int columnBlockNumber = block.getColumnBlockNumber();

			String rowBlockFileName = String
					.format("%s%s%s%d", rowDataDir, File.separator, rowGeneBlockPrefix, rowBlockNumber);

			// for each block, read row block and columnBlockNumber block
			Sequence[] rowBlockSequences = null;
			Sequence[] colBlockSequences = null;
			try {
				if(seqType.equals("DNA"))
					rowBlockSequences = GenePartition.ConvertSequences(SequenceParser.parse(rowBlockFileName, Alphabet.DNA));
				else if(seqType.equals("RNA"))
					rowBlockSequences = GenePartition.ConvertSequences(SequenceParser.parse(rowBlockFileName, Alphabet.Protein));
			} catch (Exception e) {
				InetAddress addr;
				try {
					addr = InetAddress.getLocalHost();
					// Get IP Address
					//byte[] ipAddr = addr.getAddress();

					// Get hostname
					String hostname = addr.getHostName();
					System.out.println(hostname);
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				throw new TwisterException(e);
			}

			String colBlockFileName = String.format("%s%s%s%d", colDataDir, File.separator, colGeneBlockPrefix, columnBlockNumber);
			try {
				if(seqType.equals("DNA"))
					colBlockSequences = GenePartition.ConvertSequences(SequenceParser.parse(colBlockFileName, Alphabet.DNA));
				else if(seqType.equals("RNA"))
					colBlockSequences = GenePartition.ConvertSequences(SequenceParser.parse(colBlockFileName, Alphabet.Protein));
			} catch (Exception e) {
				InetAddress addr;
				try {
					addr = InetAddress.getLocalHost();
					// Get IP Address
					//byte[] ipAddr = addr.getAddress();

					// Get hostname
					String hostname = addr.getHostName();
					System.out.println(hostname);
				} catch (UnknownHostException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				throw new TwisterException(e);
			}


			// get the number of sequences of row block and column block
			int rowSize = rowBlockSequences.length;
			int columnSize = colBlockSequences.length;

			short[][] length = new short[rowSize][columnSize];
			short[][] identicalPairs = new short[rowSize][columnSize];
			if(type.equals("NW")){
				@SuppressWarnings("rawtypes")
				SubstitutionMatrix nwScoringMatrix = null;
				if(matrixType.equals("edn")){
					nwScoringMatrix = MatrixUtil.getEDNAFULL();
				}
				else if(matrixType.equals("blo")){
					nwScoringMatrix = MatrixUtil.getBlosum62();
					gapOpen = (short) 9;
					gapExt = (short) 1;
				}
				// Not a diagonal block. So have to do pairwise distance calculation for each pair
				//System.out.println("rowSize: " + rowSize + "||colSize: " + columnSize);
				for (int j = 0; j < rowSize; j++) {
					for (int k = 0; k < columnSize; k++) {
						@SuppressWarnings({ "rawtypes", "unchecked" })
						AlignmentData ad = BioJavaWrapper.
						calculateAlignment(new ProteinSequence(rowBlockSequences[j].toString()), 
								new ProteinSequence(colBlockSequences[k].toString()), 
								gapOpen, gapExt, nwScoringMatrix, DistanceType.PercentIdentity);
						length[j][k] = (short)ad.getAlignmentLengthExcludingEndGaps();
						identicalPairs[j][k] = (short)ad.getNumIdenticals();	
					}
				}
			}
			else if(type.equals("SWG")){
				SimilarityMatrix swgScoringMatrix = null;
				try {
					//System.out.println("Begine to calculate!");
					if(matrixType.equals("blo")){
						swgScoringMatrix = SimilarityMatrix.getBLOSUM62();
						gapOpen = (short) 9;
						gapExt = (short) 1;
					}
					else if(matrixType.equals("edn"))
						swgScoringMatrix = SimilarityMatrix.getEDNAFULL();


					// Not a diagonal block. So have to do pairwise distance calculation for each pair
					for (int j = 0; j < rowSize; j++) {
						for (int k = 0; k < columnSize; k++) {
							//System.out.println("row: " + j + " col: " + k);
							Sequence sequenceA = rowBlockSequences[j];
							Sequence sequenceB = colBlockSequences[k];
							List<AlignedData> ads = SequenceAlignment.
									getSWGAlignedData(sequenceA, sequenceB, 
											gapOpen, gapExt, swgScoringMatrix);
							AlignedData ad = null;
							if(ads.size() != 0){
								ad = ads.get(0);
								length[j][k] = ad.getAlignmentLength();
								identicalPairs[j][k] = ad.getNumberOfIdenticalBasePairs(false);
							}
							else{
								ad = getAdWithZeroValue(sequenceA, sequenceB);
								length[j][k] = 0;
								identicalPairs[j][k] = 0;
							}
						}
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			block.setLength(length, false);
			block.setIdenticalPairs(identicalPairs);
			try {
				collector.collect(new StringKey("" + block.getRowBlockNumber()),
						new BytesValue(block.getBytes()));
			} catch (SerializationException e) {
				throw new TwisterException(e);
			}
		}
	}
	private AlignedData getAdWithZeroValue(Sequence sequenceA, Sequence sequenceB){
		AlignedData ad = new AlignedData(sequenceA, sequenceB);
		ad.setScore(0);
		ad.setFirstAlignedSequence(null);
		ad.setSecondAlignedSequence(null);
		ad.setFirstAlignedSequenceStartOffset(-1);
		ad.setFirstAlignedSequenceEndOffset(-1);
		ad.setSecondAlignedSeqeunceStartOffset(-1);
		ad.setSecondAlignedSeqeunceEndOffset(-1);
		ad.setFirstAlignedSequenceInsertionCount(-1);
		ad.setSecondAlignedSeqeunceInsertionCount(-1);
		ad.setFirstOffset(-1);
		ad.setSecondOffset(-1);
		return ad;
	}
}
