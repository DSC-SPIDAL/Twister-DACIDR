package cgl.imr.samples.dacidr.pwa.norm;

import java.io.*;
import java.util.List;

import cgl.imr.base.Key;
import cgl.imr.base.ReduceOutputCollector;
import cgl.imr.base.ReduceTask;
import cgl.imr.base.SerializationException;
import cgl.imr.base.TwisterException;
import cgl.imr.base.Value;
import cgl.imr.base.impl.JobConf;
import cgl.imr.base.impl.ReducerConf;
import cgl.imr.types.*;

/**
 * @author Yang Ruan (yangruan at cs dot indiana dot edu)
 * @author Saliya Ekanayake (sekanaya at cs dot indiana dot edu)
 */
public class PWAReduceTask implements ReduceTask {

	private String outputPrefix;

	private int numOfSequences;
    private int numOfPartitions;
    private String dataDir;
    private String seqType;
    //private String idxFile;

	@Override
	public void close() throws TwisterException {
	}

	@Override
	public void configure(JobConf jobConf, ReducerConf mapConf)
			throws TwisterException {
        dataDir = jobConf.getProperty("dataDir");
		outputPrefix = jobConf.getProperty("outputPrefix");

		numOfSequences = Integer.parseInt(jobConf.getProperty("numOfSequences"));
        numOfPartitions = Integer.parseInt(jobConf.getProperty("numOfPartitions"));
        seqType = jobConf.getProperty("SeqType");
	}

	@Override
	public void reduce(ReduceOutputCollector collector, Key key,
                       List<Value> values) throws TwisterException {

		
        int rowBlockNumber = Integer.parseInt(((StringKey) key).getString());
        int rowSize = numOfSequences / numOfPartitions;
        int remainder;
        if ((remainder = numOfSequences % numOfPartitions) > 0 && rowBlockNumber < remainder) {
            rowSize++;
        }

        Block[] blocks = new Block[values.size()];
        Block b;
        for (Value value : values) {
            try {
                b = new Block(value.getBytes());
                System.out.println(b.getColumnBlockNumber());
                blocks[b.getColumnBlockNumber()] = b;
            } catch (SerializationException e) {
                e.printStackTrace();
            }
        }

        String fnameScore
        = dataDir + File.separator + outputPrefix + "score_" + String.valueOf(rowBlockNumber);
        String fnameLength
        = dataDir + File.separator + outputPrefix + "length_" + String.valueOf(rowBlockNumber);
        String fnameIdenticalPairs
        = dataDir + File.separator + outputPrefix + "identicalPairs_" + String.valueOf(rowBlockNumber);
        String fnamePID
        = dataDir + File.separator + outputPrefix + "pid_" + String.valueOf(rowBlockNumber);
        
        String fnameScoreReverse
        = dataDir + File.separator + outputPrefix + "scoreReverse_" + String.valueOf(rowBlockNumber);
        String fnameLengthReverse
        = dataDir + File.separator + outputPrefix + "lengthReverse_" + String.valueOf(rowBlockNumber);
        String fnameIdenticalPairsReverse
        = dataDir + File.separator + outputPrefix + "identicalPairsReverse_" + String.valueOf(rowBlockNumber);
        
        String fnameScoreA
        = dataDir + File.separator + outputPrefix + "scoreA_" + String.valueOf(rowBlockNumber);
        String fnameScoreAReverse
        = dataDir + File.separator + outputPrefix + "scoreAReverse_" + String.valueOf(rowBlockNumber);
        String fnameScoreB
        = dataDir + File.separator + outputPrefix + "scoreB_" + String.valueOf(rowBlockNumber);
        String fnameScoreBReverse
        = dataDir + File.separator + outputPrefix + "scoreBReverse_" + String.valueOf(rowBlockNumber);
        
        try {
            DataOutputStream dosScore = new DataOutputStream(
            		new BufferedOutputStream(new FileOutputStream(fnameScore)));
            DataOutputStream dosLength = new DataOutputStream(
            		new BufferedOutputStream(new FileOutputStream(fnameLength)));
            DataOutputStream dosIdenticalPairs = new DataOutputStream(
            		new BufferedOutputStream(new FileOutputStream(fnameIdenticalPairs)));
            DataOutputStream dosScoreA = new DataOutputStream(
            		new BufferedOutputStream(new FileOutputStream(fnameScoreA)));
            DataOutputStream dosScoreB = new DataOutputStream(
            		new BufferedOutputStream(new FileOutputStream(fnameScoreB)));
            DataOutputStream dosPID = new DataOutputStream(
            		new BufferedOutputStream(new FileOutputStream(fnamePID)));
            
            DataOutputStream dosScoreReverse = null;
            DataOutputStream dosLengthReverse = null;
            DataOutputStream dosIdenticalPairsReverse = null;
            DataOutputStream dosScoreAReverse = null;
            DataOutputStream dosScoreBReverse = null;
            if(seqType.equals("DNA")){
            	dosScoreReverse = new DataOutputStream(
                		new BufferedOutputStream(new FileOutputStream(fnameScoreReverse)));
                dosLengthReverse = new DataOutputStream(
                		new BufferedOutputStream(new FileOutputStream(fnameLengthReverse)));
                dosIdenticalPairsReverse = new DataOutputStream(
                		new BufferedOutputStream(new FileOutputStream(fnameIdenticalPairsReverse)));
                dosScoreAReverse = new DataOutputStream(
                		new BufferedOutputStream(new FileOutputStream(fnameScoreAReverse)));
                dosScoreBReverse = new DataOutputStream(
                		new BufferedOutputStream(new FileOutputStream(fnameScoreBReverse)));
            }
            
            short [][] score;
            short [][] length;
            short [][] identicalPairs;
            short [][] scoreReverse;
            short [][] lengthReverse;
            short [][] identicalPairsReverse;
            short [][] scoreA;
            short [][] scoreAReverse;
            short [][] scoreB;
            short [][] scoreBReverse;
            //int count = 0;
            for (int i = 0; i < rowSize; i++) {
                for (Block block : blocks) {
                	//System.out.println("This count: " + count);
                    score = block.getScore();
                    length = block.getLength();
                    identicalPairs = block.getIdenticalPairs();
                    scoreReverse = block.getScoreReverse();
                    lengthReverse = block.getLengthReverse();
                    identicalPairsReverse = block.getIdenticalPairsReverse();
                    scoreA = block.getScoreA();
                    scoreAReverse = block.getScoreAReverse();
                    scoreB = block.getScoreB();
                    scoreBReverse = block.getScoreBReverse();

                    // Won't push "if" inside loop for the sake of performance
                    if (block.isTranspose()) {
                        for (int k = 0; k < block.getColSize(); k++) {
                            dosScore.writeShort(score[k][i]);
                            dosLength.writeShort(length[k][i]);
                            dosIdenticalPairs.writeShort(identicalPairs[k][i]);
                            dosScoreA.writeShort(scoreA[k][i]);
                            dosScoreB.writeShort(scoreB[k][i]);
                            dosPID.writeShort((short) ((1 - identicalPairs[k][i] / (double) length[k][i]) * Short.MAX_VALUE));
                            if(seqType.equals("DNA")){
                            	dosScoreReverse.writeShort(scoreReverse[k][i]);
                                dosLengthReverse.writeShort(lengthReverse[k][i]);
                                dosIdenticalPairsReverse.writeShort(identicalPairsReverse[k][i]);
                                dosScoreAReverse.writeShort(scoreAReverse[k][i]);
                                dosScoreBReverse.writeShort(scoreBReverse[k][i]);
                            }
                        }
                    } else {
                        for (int k = 0; k < block.getColSize(); k++) {
                        	dosScore.writeShort(score[i][k]);
                        	dosLength.writeShort(length[i][k]);
                        	dosIdenticalPairs.writeShort(identicalPairs[i][k]);
                        	dosScoreA.writeShort(scoreA[i][k]);
                            dosScoreB.writeShort(scoreB[i][k]);
                            dosPID.writeShort((short) ((1 - identicalPairs[i][k] / (double) length[i][k]) * Short.MAX_VALUE));
                            
                        	if(seqType.equals("DNA")){
                            	dosScoreReverse.writeShort(scoreReverse[i][k]);
                            	dosLengthReverse.writeShort(lengthReverse[i][k]);
                            	dosIdenticalPairsReverse.writeShort(identicalPairsReverse[i][k]);
                            	dosScoreAReverse.writeShort(scoreAReverse[i][k]);
                                dosScoreBReverse.writeShort(scoreBReverse[i][k]);
                        	}
                        }
                    }
                }
            }
            dosScore.close();
            dosLength.close();
            dosIdenticalPairs.close();
            dosScoreA.close();
            dosScoreB.close();
            dosPID.close();
            if(seqType.equals("DNA")){
            	dosScoreReverse.close();
                dosLengthReverse.close();
                dosIdenticalPairsReverse.close();
                dosScoreAReverse.close();
                dosScoreBReverse.close();
            }
            collector.collect(key, new StringValue(fnameScore));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
