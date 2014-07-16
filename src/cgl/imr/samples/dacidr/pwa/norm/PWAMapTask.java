package cgl.imr.samples.dacidr.pwa.norm;

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

import org.biojava3.alignment.template.SubstitutionMatrix;
import org.biojava3.core.sequence.ProteinSequence;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

/**
 * @author Yang Ruan (yangruan at cs dot indiana dot edu)
 * @author Saliya Ekanayake (sekanaya at cs dot indiana dot edu)
 */

public class PWAMapTask implements MapTask {

	private String dataDir;
	private String geneBlockPrefix;

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

		dataDir = jobConf.getProperty("dataDir");
		geneBlockPrefix = jobConf.getProperty("geneBlockPrefix");

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

			String rowBlockFileName = String.format("%s%s%s%d", dataDir, File.separator, geneBlockPrefix, rowBlockNumber);

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

			if (rowBlockNumber == columnBlockNumber) {
				colBlockSequences = rowBlockSequences;
			} else {
				String colBlockFileName = String.format("%s%s%s%d", dataDir, File.separator, geneBlockPrefix, columnBlockNumber);
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
			}

			// get the number of sequences of row block and column block
			int rowSize = rowBlockSequences.length;
			int columnSize = colBlockSequences.length;

			//System.out.println("Before the choice!");
			// calculate distance, sequence by sequence
			//short[][] distances = new short[rowSize][columnSize];
			
//			short[][] score = new short[rowSize][columnSize];
			short[][] length = new short[rowSize][columnSize];
			short[][] identicalPairs = new short[rowSize][columnSize];
//			short[][] scoreReverse = new short[rowSize][columnSize];
			short[][] lengthReverse = new short[rowSize][columnSize];
			short[][] identicalPairsReverse = new short[rowSize][columnSize];
//			short[][] scoreA = new short[rowSize][columnSize];
//			short[][] scoreB = new short[rowSize][columnSize];
//			short[][] scoreAReverse = new short[rowSize][columnSize];
//			short[][] scoreBReverse = new short[rowSize][columnSize];
			
			// calculate the alignment length size
			if(type.equals("NW")){

				SubstitutionMatrix nwScoringMatrix = null;
				if(matrixType.equals("edn")){
					nwScoringMatrix = MatrixUtil.getEDNAFULL();
				}
				else if(matrixType.equals("blo")){
					nwScoringMatrix = MatrixUtil.getBlosum62();
					gapOpen = (short) 9;
					gapExt = (short) 1;
				}
				
				if (rowBlockNumber != columnBlockNumber) {
					// Not a diagonal block. So have to do pairwise distance calculation for each pair
					for (int j = 0; j < rowSize; j++) {
						for (int k = 0; k < columnSize; k++) {
							@SuppressWarnings({ "rawtypes", "unchecked" })
							AlignmentData ad = BioJavaWrapper.
							calculateAlignment(new ProteinSequence(rowBlockSequences[j].toString()), 
									new ProteinSequence(colBlockSequences[k].toString()), 
									gapOpen, gapExt, nwScoringMatrix, DistanceType.PercentIdentity);
//							score[j][k] = (short)ad.getScore();
							length[j][k] = (short)ad.getAlignmentLengthExcludingEndGaps();
							identicalPairs[j][k] = (short)ad.getNumIdenticals();

//							int startA = ad.getFirstAlignedSequenceStartOffset();
//							int endA = ad.getFirstAlignedSequenceEndOffset();
//							int startB = ad.getSecondAlignedSequenceStartOffset();
//							int endB = ad.getSecondAlignedSequenceEndOffset();

//							ProteinSequence partialA 
//							= new ProteinSequence(rowBlockSequences[j].toString().substring(startA, endA + 1));
//							ProteinSequence partialB 
//							= new ProteinSequence(colBlockSequences[k].toString().substring(startB, endB + 1));

//							scoreA[j][k] = (short)AlignmentData.getSelfAlignedScore(partialA, nwScoringMatrix);
//							scoreB[j][k] = (short)AlignmentData.getSelfAlignedScore(partialB, nwScoringMatrix);
						}
					}
				} else {
					// Diagonal block. Perform pairwise distance calculation only for one triangle
					for (int j = 0; j < rowSize; j++) {
						for (int k = 0; k < j; k++) {
							@SuppressWarnings({ "rawtypes", "unchecked" })
							AlignmentData ad = BioJavaWrapper.
							calculateAlignment(new ProteinSequence(rowBlockSequences[j].toString()), 
									new ProteinSequence(colBlockSequences[k].toString()), 
									gapOpen, gapExt, nwScoringMatrix, DistanceType.PercentIdentity);
//							score[j][k] = (short)ad.getScore();
							length[j][k] = (short)ad.getAlignmentLengthExcludingEndGaps();
							identicalPairs[j][k] = (short)ad.getNumIdenticals();

							int startA = ad.getFirstAlignedSequenceStartOffset();
							int endA = ad.getFirstAlignedSequenceEndOffset();
							int startB = ad.getSecondAlignedSequenceStartOffset();
							int endB = ad.getSecondAlignedSequenceEndOffset();

							ProteinSequence partialA 
							= new ProteinSequence(rowBlockSequences[j].toString().substring(startA, endA + 1));
							ProteinSequence partialB 
							= new ProteinSequence(colBlockSequences[k].toString().substring(startB, endB + 1));

//							scoreA[j][k] = (short)AlignmentData.getSelfAlignedScore(partialA, nwScoringMatrix);
//							scoreB[j][k] = (short)AlignmentData.getSelfAlignedScore(partialB, nwScoringMatrix);
//							score[k][j] = score[j][k];
//							scoreA[k][j] = scoreA[j][k];
//							scoreB[k][j] = scoreB[j][k];
						}
						// Pairwise distance for diagonal elements
						AlignmentData ad = BioJavaWrapper.
								calculateAlignment(new ProteinSequence(rowBlockSequences[j].toString()), 
										new ProteinSequence(rowBlockSequences[j].toString()), 
										gapOpen, gapExt, nwScoringMatrix, DistanceType.PercentIdentity);
//						score[j][j] = (short) ad.getScore();
//						scoreA[j][j] = (short) ad.getScore();
//						scoreB[j][j] = (short) ad.getScore();
						length[j][j] = (short) ad.getAlignmentLengthExcludingEndGaps();
						identicalPairs[j][j] = (short) ad.getNumIdenticals();
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
					//SmithWatermanAligner aligner = new SmithWatermanAligner();
						
					
					if (rowBlockNumber != columnBlockNumber) {
						// Not a diagonal block. So have to do pairwise distance calculation for each pair
						for (int j = 0; j < rowSize; j++) {
							for (int k = 0; k < columnSize; k++) {
								//System.out.println("row: " + j + " col: " + k);
								Sequence sequenceA = rowBlockSequences[j];
								Sequence sequenceB = colBlockSequences[k];
								List<AlignedData> ads = SequenceAlignment.
										getSWGAlignedData(sequenceA, 
												sequenceB, 
												gapOpen, gapExt, swgScoringMatrix);
								AlignedData ad = null;
								try{
									ad = ads.get(0);
								}
								catch(Exception e){
									System.out.println("Exception:");
									System.out.println("SequenceA: " + sequenceA.toString() + 
											"####SequenceB:" + sequenceB.toString());
								}
//								score[j][k] = ad.getScore();
								length[j][k] = ad.getAlignmentLength();
								identicalPairs[j][k] = ad.getNumberOfIdenticalBasePairs(false);
								int startA = ad.getFirstAlignedSequenceStartOffset();
								int endA = ad.getFirstAlignedSequenceEndOffset();
								int startB = ad.getSecondAlignedSeqeunceStartOffset();
								int endB = ad.getSecondAlignedSeqeunceEndOffset();
								
								
//								Sequence partialA =	getPartialSequence(startA, endA, sequenceA);
//								scoreA[j][k] = (short) partialA.getSelfAlignedScore(swgScoringMatrix);
//								Sequence partialB =	getPartialSequence(startB, endB, sequenceB); 
//								scoreB[j][k] = (short) partialB.getSelfAlignedScore(swgScoringMatrix);
								
								if(seqType.equals("DNA")){
									try{
									ads = SequenceAlignment.
											getSWGAlignedData(sequenceA, 
													sequenceB.getReverseComplementedSequence(), 
													gapOpen, gapExt, swgScoringMatrix);
									}
									catch(Exception e){
										System.out.println("Reverse Exception:");
										System.out.print("sequenceB: ");
										for(int x = 0; x < sequenceB.toString().length(); x++){
											char c = sequenceB.toString().charAt(x);
											if(c != 'A' && c != 'C' && c != 'T' && c!= 'G')
												System.out.print(c);
										}
									}
									ad = ads.get(0);
//									scoreReverse[j][k] = ad.getScore();
									lengthReverse[j][k] = ad.getAlignmentLength();
									identicalPairsReverse[j][k] = ad.getNumberOfIdenticalBasePairs(false);
									
//									startA = ad.getFirstAlignedSequenceStartOffset();
//									endA = ad.getFirstAlignedSequenceEndOffset();
//									startB = ad.getSecondAlignedSeqeunceStartOffset();
//									endB = ad.getSecondAlignedSeqeunceEndOffset();
//									
//									Sequence partialAReverse =	getPartialSequence(startA, endA, sequenceA);
//									scoreAReverse[j][k] = (short) partialA.getSelfAlignedScore(swgScoringMatrix);
//									Sequence partialBReverse =getPartialSequence(startB, endB, sequenceB.getReverseComplementedSequence()); 
//									scoreBReverse[j][k] = (short) partialB.getSelfAlignedScore(swgScoringMatrix);
								}					
							}
						}
					} else {
						// Diagonal block. Perform pairwise distance calculation only for one triangle
						for (int j = 0; j < rowSize; j++) {
							for (int k = 0; k < j; k++) {
								//System.out.println("row: " + j + " col: " + k);
								Sequence sequenceA = rowBlockSequences[j];
								Sequence sequenceB = colBlockSequences[k];
								//Sequence sequenceBReverse = sequenceB.getReverseComplementedSequence();
								List<AlignedData> ads = SequenceAlignment.
										getSWGAlignedData(sequenceA, 
												sequenceB, 
												gapOpen, gapExt, swgScoringMatrix);
								// We will just take the first alignment
								AlignedData ad = ads.get(0);
//								score[j][k] = ad.getScore();
//								score[k][j] = score[j][k];					          
								length[j][k] = ad.getAlignmentLength();
								length[k][j] = length[j][k];
								identicalPairs[j][k] = ad.getNumberOfIdenticalBasePairs(false);
								identicalPairs[k][j] = identicalPairs[j][k];
								
								int startA = ad.getFirstAlignedSequenceStartOffset();
								int endA = ad.getFirstAlignedSequenceEndOffset();
								int startB = ad.getSecondAlignedSeqeunceStartOffset();
								int endB = ad.getSecondAlignedSeqeunceEndOffset();
//								
//								System.out.println("Total A:" + sequenceA.toString().length());
//								System.out.println("startA: " + startA);
//
//								System.out.println("endA: " + endA);
//								System.out.println("Total B:" + sequenceB.toString().length());
//
//								System.out.println("startB: " + startB);
//
//								System.out.println("endB: " + endB);
//								Sequence partialA =	getPartialSequence(startA, endA, sequenceA);
//								scoreA[j][k] = (short) partialA.getSelfAlignedScore(swgScoringMatrix);
//								Sequence partialB =	getPartialSequence(startB, endB, sequenceB); 
//								scoreB[j][k] = (short) partialB.getSelfAlignedScore(swgScoringMatrix);
//								scoreA[k][j] = scoreA[j][k];
//								scoreB[k][j] = scoreB[j][k];
								
								if(seqType.equals("DNA")){
									ads = SequenceAlignment.
											getSWGAlignedData(sequenceA, 
													sequenceB.getReverseComplementedSequence(), 
													gapOpen, gapExt, swgScoringMatrix);
									ad = ads.get(0);
//									scoreReverse[j][k] = ad.getScore();
//									scoreReverse[k][j] = scoreReverse[j][k];
         
									lengthReverse[j][k] = ad.getAlignmentLength();
									lengthReverse[k][j] = lengthReverse[j][k];
									identicalPairsReverse[j][k] = ad.getNumberOfIdenticalBasePairs(false);
									identicalPairsReverse[k][j] = identicalPairsReverse[j][k];
									
//									startA = ad.getFirstAlignedSequenceStartOffset();
//									endA = ad.getFirstAlignedSequenceEndOffset();
//									startB = ad.getSecondAlignedSeqeunceStartOffset();
//									endB = ad.getSecondAlignedSeqeunceEndOffset();
//									
//									Sequence partialAReverse =	getPartialSequence(startA, endA, sequenceA);
//									scoreAReverse[j][k] = (short) partialA.getSelfAlignedScore(swgScoringMatrix);
//									Sequence partialBReverse =	getPartialSequence(startB, endB, sequenceB.getReverseComplementedSequence()); 
//									scoreBReverse[j][k] = (short) partialB.getSelfAlignedScore(swgScoringMatrix);
//									scoreAReverse[k][j] = scoreAReverse[j][k];
//									scoreBReverse[k][j] = scoreBReverse[j][k];
								}
							}
							// Pairwise distance for diagonal elements
							List<AlignedData> ads = SequenceAlignment.
									getSWGAlignedData(rowBlockSequences[j], 
											rowBlockSequences[j], 
											gapOpen, gapExt, swgScoringMatrix);
							AlignedData ad = ads.get(0);
//							score[j][j] = ad.getScore();
							length[j][j] = ad.getAlignmentLength();
							identicalPairs[j][j] = ad.getNumberOfIdenticalBasePairs(false);
//							scoreA[j][j] = ad.getScore();
//							scoreB[j][j] = ad.getScore();
							if(seqType.equals("DNA")){
								ads = SequenceAlignment.
										getSWGAlignedData(rowBlockSequences[j], 
												rowBlockSequences[j].getReverseComplementedSequence(), 
												gapOpen, gapExt, swgScoringMatrix);ad = ads.get(0);
//								scoreReverse[j][j] = ad.getScore();
								lengthReverse[j][j] = ad.getAlignmentLength();
								identicalPairsReverse[j][j] = ad.getNumberOfIdenticalBasePairs(false);
//								scoreAReverse[j][j] = ad.getScore();
//								scoreBReverse[j][j] = ad.getScore();
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
//			block.setScore(score, false);
			block.setLength(length, false);
			block.setIdenticalPairs(identicalPairs);
//			block.setScoreReverse(scoreReverse);
			block.setLengthReverse(lengthReverse);
			block.setIdenticalPairsReverse(identicalPairsReverse);
//			block.setScoreA(scoreA);
//			block.setScoreAReverse(scoreAReverse);
//			block.setScoreB(scoreB);
//			block.setScoreBReverse(scoreBReverse);

			try {
				collector.collect(new StringKey("" + block.getRowBlockNumber()),
						new BytesValue(block.getBytes()));
			} catch (SerializationException e) {
				throw new TwisterException(e);
			}

			if (rowBlockNumber != columnBlockNumber) {
				// Creates a transpose block. Note. distances array should be treated as transpose when reading.
				Block transBlock = new Block(block.getColumnBlockNumber(), block.getRowBlockNumber());
//				transBlock.setScore(block.getScore(), true);
				transBlock.setLength(block.getLength(), true);
				transBlock.setIdenticalPairs(block.getIdenticalPairs());
//				transBlock.setScoreReverse(block.getScoreReverse());
				transBlock.setLengthReverse(block.getLengthReverse());
				transBlock.setIdenticalPairsReverse(block.getIdenticalPairsReverse());
//				transBlock.setScoreA(block.getScoreA());
//				transBlock.setScoreAReverse(block.getScoreAReverse());
//				transBlock.setScoreB(block.getScoreB());
//				transBlock.setScoreBReverse(block.getScoreBReverse());
				try {
					collector.collect(new StringKey("" + transBlock.getRowBlockNumber()),
							new BytesValue(transBlock.getBytes()));
				} catch (SerializationException e) {
					throw new TwisterException(e);
				}
			}
		}
	}
	
	private Sequence getPartialSequence(int startIndex, int endIndex, Sequence sequence){
		Sequence partialSequence = null;
		if(seqType.equals("DNA"))
			partialSequence = new Sequence(sequence.toString().substring(startIndex, endIndex+1), 
					sequence.getId(), Alphabet.DNA);
		else if(seqType.equals("RNA"))
			partialSequence = new Sequence(sequence.toString().substring(startIndex, endIndex+1), 
					sequence.getId(), Alphabet.Protein);
		return partialSequence;
	}
}