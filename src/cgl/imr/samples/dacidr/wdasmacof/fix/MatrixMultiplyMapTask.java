package cgl.imr.samples.dacidr.wdasmacof.fix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import cgl.imr.base.Key;
import cgl.imr.base.MapOutputCollector;
import cgl.imr.base.MapTask;
import cgl.imr.base.TwisterException;
import cgl.imr.base.Value;
import cgl.imr.base.impl.JobConf;
import cgl.imr.base.impl.MapperConf;
import cgl.imr.types.StringKey;
import cgl.imr.types.StringValue;
import cgl.imr.worker.MemCache;

public class MatrixMultiplyMapTask implements MapTask{

	JobConf jobConf;
	short[][] weight;
	int rowOffset;
	int blockHeight;
	int N;
	int bz;
	double[] V;
	
	@Override
	public void close() throws TwisterException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void configure(JobConf jobConf, MapperConf mapConf)
			throws TwisterException {
		// TODO Auto-generated method stub
		this.jobConf = jobConf;
		
		MDSMatrixData deltaMatData = null;
		String inputFolder = jobConf.getProperty("InputFolder");
		String inputPrefix = jobConf.getProperty("WeightPrefix");
		String fileName = (inputFolder + "/" + inputPrefix + mapConf.getMapTaskNo())
				.replaceAll("//", "/");
		String idsFile = jobConf.getProperty("IdsFile");

		try {
			BufferedReader br = new BufferedReader(new FileReader(idsFile));
			String line;
			String[] tokens;
			deltaMatData = new MDSMatrixData();
			while((line = br.readLine())!=null){
				tokens = line.split("\t");
				if(Integer.parseInt(tokens[0]) == mapConf.getMapTaskNo()){
					deltaMatData.setHeight(Integer.parseInt(tokens[1]));
					deltaMatData.setWidth(Integer.parseInt(tokens[2]));
					deltaMatData.setRow(Integer.parseInt(tokens[3]));
					deltaMatData.setRowOFfset(Integer.parseInt(tokens[4]));
				}
			}
			br.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}
		
		try {
			weight = FileOperation.loadWeights(fileName, 
					deltaMatData.getHeight(), deltaMatData.getWidth());
			V = new double[deltaMatData.getHeight()];
			for (int i = 0; i < deltaMatData.getHeight(); ++i) {
				for (int j = 0; j < deltaMatData.getWidth(); ++j) {
					if (i + deltaMatData.getRowOffset() != j)
						V[i] += weight[i][j];
				}
				V[i] += 1;

			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rowOffset = deltaMatData.getRowOffset();
		blockHeight = deltaMatData.getHeight();
		N = deltaMatData.getWidth();
		bz = Integer.parseInt(jobConf.getProperty(DAMDS2.PROP_BZ));
	}

	@Override
	public void map(MapOutputCollector collector, Key key, Value val)
			throws TwisterException {
		// TODO Auto-generated method stub
		StringValue memCacheKey = (StringValue) val;
		MDSMatrixData mData = (MDSMatrixData) (MemCache.getInstance().get(
				jobConf.getJobId(), memCacheKey.toString()));
		double[][] X = mData.getData();

		// Next we can calculate the BofZ * preX.
		X = MatrixUtils.matrixMultiply(weight, V, X, blockHeight,
				X[0].length, N, bz, rowOffset);

		// Send C with the map task number to a reduce task. Which will simply
		// combine these parts and form the N x d matrix.
		// We don't need offset here.
		MDSMatrixData newMData = new MDSMatrixData(X, blockHeight, X[0].length,
				mData.getRow(), rowOffset);
		collector.collect(new StringKey("MM-map-to-reduce-key"), newMData);
	}

	
}
