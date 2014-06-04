package cgl.imr.samples.dacidr.pwa.large.sym;

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

        String fnameLength
        = dataDir + File.separator + outputPrefix + "length_" + String.valueOf(rowBlockNumber);
        String fnameIdenticalPairs
        = dataDir + File.separator + outputPrefix + "identicalPairs_" + String.valueOf(rowBlockNumber);
        String fnamePID
        = dataDir + File.separator + outputPrefix + "pid_" + String.valueOf(rowBlockNumber);

        try {
            DataOutputStream dosLength = new DataOutputStream(
            		new BufferedOutputStream(new FileOutputStream(fnameLength)));
            DataOutputStream dosIdenticalPairs = new DataOutputStream(
            		new BufferedOutputStream(new FileOutputStream(fnameIdenticalPairs)));
            
            short [][] length;
            short [][] identicalPairs;
            //int count = 0;
            for (int i = 0; i < rowSize; i++) {
                for (Block block : blocks) {
                    length = block.getLength();
                    identicalPairs = block.getIdenticalPairs();

                    // Won't push "if" inside loop for the sake of performance
                    if (block.isTranspose()) {
                        for (int k = 0; k < block.getColSize(); k++) {
                            dosLength.writeShort(length[k][i]);
                            dosIdenticalPairs.writeShort(identicalPairs[k][i]);                         
                        }
                    } else {
                        for (int k = 0; k < block.getColSize(); k++) {
                        	dosLength.writeShort(length[i][k]);
                        	dosIdenticalPairs.writeShort(identicalPairs[i][k]);
                    
                        }
                    }
                }
            }
            dosLength.close();
            dosIdenticalPairs.close();
         
            collector.collect(key, new StringValue(fnameLength));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
}
