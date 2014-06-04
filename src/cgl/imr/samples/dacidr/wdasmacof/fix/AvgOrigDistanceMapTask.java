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
import cgl.imr.types.DoubleArray;
import cgl.imr.types.StringKey;

public class AvgOrigDistanceMapTask implements MapTask {

	MDSShortMatrixData rowData;
	MapperConf mapConf;
	short[][] weights;
	int inSampleSize;

	@Override
	public void close() throws TwisterException {
		// TODO Auto-generated method stub
	}

	@Override
	public void configure(JobConf jobConf, MapperConf mapConf)
			throws TwisterException {
		this.mapConf = mapConf;

		String idsFile = jobConf.getProperty("IdsFile");
		String inputFolder = jobConf.getProperty("InputFolder");
		String inputPrefix = jobConf.getProperty("InputPrefix");
		String weightPrefix = jobConf.getProperty("WeightPrefix");
		inSampleSize = Integer.parseInt(jobConf.getProperty("InSampleSize"));
		String fileName = (inputFolder + "/" + inputPrefix + mapConf.getMapTaskNo())
			.replaceAll("//", "/");
		String weightName = (inputFolder + "/" + weightPrefix + mapConf.getMapTaskNo())
				.replaceAll("//", "/");
		try{
			BufferedReader br = new BufferedReader(new FileReader(idsFile));
			String line;
			String[] tokens;
			rowData = new MDSShortMatrixData();
			while((line = br.readLine())!=null){
				tokens = line.split("\t");
				if(Integer.parseInt(tokens[0]) == mapConf.getMapTaskNo()){
					rowData.setHeight(Integer.parseInt(tokens[1]));
					rowData.setWidth(Integer.parseInt(tokens[2]));
					rowData.setRow(Integer.parseInt(tokens[3]));
					rowData.setRowOFfset(Integer.parseInt(tokens[4]));
				}
			}
			br.close();
		}
		catch(IOException e){
			e.printStackTrace();
		}

		try {
			rowData.loadDeltaFromBinFile(fileName);
			weights = FileOperation.loadWeights(weightName, rowData.getHeight(), rowData.getWidth());
		} catch (Exception e) {
			throw new TwisterException(e);
		}
	}

	@Override
	public void map(MapOutputCollector collector, Key key, Value val)
			throws TwisterException {
		short[][] data = rowData.getData();
		int height = rowData.getHeight();
		int width = rowData.getWidth();
		double average = 0;
		double avgSquare = 0;
		double maxDelta = 0.0;

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if(weights[i][j] != 0 && i + rowData.getRowOffset() < inSampleSize){
					double realD = data[i][j] / (double) Short.MAX_VALUE;
					average += realD;
					avgSquare += (realD * realD);
					
					if (maxDelta < realD)
						maxDelta = realD;
				}
			}
		}
		//System.out.println(average);
		double[] avgs = new double[3];
		avgs[0] = average;
		avgs[1] = avgSquare;
		avgs[2] = maxDelta;
		collector
				.collect(new StringKey("stress-key"), new DoubleArray(avgs, 3));
	}

}
