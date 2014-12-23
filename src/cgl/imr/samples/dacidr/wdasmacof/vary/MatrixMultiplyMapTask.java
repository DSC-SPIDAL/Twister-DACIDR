package cgl.imr.samples.dacidr.wdasmacof.vary;

import cgl.imr.base.*;
import cgl.imr.base.impl.JobConf;
import cgl.imr.base.impl.MapperConf;
import cgl.imr.types.StringKey;
import cgl.imr.types.StringValue;
import cgl.imr.worker.MemCache;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MatrixMultiplyMapTask implements MapTask{

    private boolean sammonMapping = false;
    private double averageOriginalDistance = 0.0;
	JobConf jobConf;
	private short[][] deltaBlock = null;
	short[][] weights;
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
		
		MDSShortMatrixData deltaMatData = null;
        sammonMapping = Boolean.parseBoolean(jobConf.getProperty(DAMDS2.PROP_SAMMON));
        averageOriginalDistance = Double.parseDouble(jobConf.getProperty(DAMDS2.PROP_AVG_D));
        String inputFolder = jobConf.getProperty("InputFolder");
        String inputPrefix = jobConf.getProperty("InputPrefix");
		String weightPrefix = jobConf.getProperty("WeightPrefix");
        String fileName = (inputFolder + "/" + inputPrefix + mapConf.getMapTaskNo())
                .replaceAll("//", "/");
		String weightName = (inputFolder + "/" + weightPrefix + mapConf.getMapTaskNo())
				.replaceAll("//", "/");
		String idsFile = jobConf.getProperty("IdsFile");

		try {
			BufferedReader br = new BufferedReader(new FileReader(idsFile));
			String line;
			String[] tokens;
			deltaMatData = new MDSShortMatrixData();
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

            deltaBlock = deltaMatData.loadDeltaFromBinFile(fileName);
			// In Sammon mode we'll compute weights when needed
			// hence the reason not load weights for Sammon.
			if (!sammonMapping){
				weights = FileOperation.loadWeights(weightName, deltaMatData.getHeight(),deltaMatData.getWidth());
			}
			V = new double[deltaMatData.getHeight()];
			for (int i = 0; i < deltaMatData.getHeight(); ++i) {
				for (int j = 0; j < deltaMatData.getWidth(); ++j) {
					if (i + deltaMatData.getRowOffset() != j) {
						double origD = deltaBlock[i][j]*1.0/Short.MAX_VALUE;
						double weight = sammonMapping ? 1.0 / Math.max(origD, 0.001 * averageOriginalDistance) : weights[i][j];
						V[i] += weight;
					}
				}
				V[i] += 1;
			}

			rowOffset = deltaMatData.getRowOffset();
			blockHeight = deltaMatData.getHeight();
			N = deltaMatData.getWidth();
			bz = Integer.parseInt(jobConf.getProperty(DAMDS2.PROP_BZ));
		} catch (IOException e) {
			e.printStackTrace();
		}

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

		X = sammonMapping ?
				MatrixUtils.matrixMultiply((i, j) ->
						1.0 / Math.max(deltaBlock[i][j] * 1.0 / Short.MAX_VALUE, 0.001 * averageOriginalDistance),
						V, X, blockHeight, X[0].length, N, bz, rowOffset)
				: MatrixUtils.matrixMultiply(weights, V, X, blockHeight, X[0].length, N, bz, rowOffset);

		// Send C with the map task number to a reduce task. Which will simply
		// combine these parts and form the N x d matrix.
		// We don't need offset here.
		MDSMatrixData newMData = new MDSMatrixData(X, blockHeight, X[0].length,
				mData.getRow(), rowOffset);
		collector.collect(new StringKey("MM-map-to-reduce-key"), newMData);
	}

	
}
