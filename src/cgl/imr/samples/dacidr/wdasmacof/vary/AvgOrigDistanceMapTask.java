package cgl.imr.samples.dacidr.wdasmacof.vary;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class AvgOrigDistanceMapTask {

	MDSShortMatrixData rowData;
	MapperConf mapConf;
	short[][] weights;
    boolean sammonMapping = false;
	double distanceTransform = 1.0;
	private boolean bigEndian = true;


	public void configure(JobConf jobConf, MapperConf mapConf) {
		this.mapConf = mapConf;

		sammonMapping = Boolean.parseBoolean(jobConf.getProperty(DAMDS2.PROP_SAMMON));
		distanceTransform = Double.parseDouble(jobConf.getProperty(DAMDS2.PROP_DTRANS));
		bigEndian = Boolean.parseBoolean(jobConf.getProperty(DAMDS2.PROP_BIGENDIAN));
		String idsFile = jobConf.getProperty("IdsFile");
		String inputFolder = jobConf.getProperty("InputFolder");
		String inputPrefix = jobConf.getProperty("InputPrefix");
		String weightPrefix = jobConf.getProperty("WeightPrefix");
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
			rowData.loadDeltaFromBinFile(fileName, bigEndian);
            // The weights are used in this map-reduce stage only to decide if a distance value
            // should be considered (non zero weight) or not (zero weight).
            // In Sammon mode we'll consider all distances,
            // hence the reason not load weights for Sammon.
            if (!sammonMapping){
				weights = FileOperation.loadWeights(weightName, rowData.getHeight(), rowData.getWidth());
			}
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public double [] map(){
		short[][] data = rowData.getData();
		int height = rowData.getHeight();
		int width = rowData.getWidth();
		double average = 0;
		double avgSquare = 0;
		double maxDelta = 0.0;
		long pairCount = 0;
		long missingDistCount = 0;

		for (int i = 0; i < height; i++) {
			for (int j = 0; j < width; j++) {
				if (!sammonMapping && weights[i][j] == 0) {
					continue;
				}
				double realD = data[i][j] / (double) Short.MAX_VALUE;
				if (realD < 0){
					++missingDistCount;
					continue; // ignore missing distances (i.e. dist < 0) irrespective of weight
				}
				realD = distanceTransform != 1.0 ? Math.pow(realD, distanceTransform) : realD;
				average += realD;
				avgSquare += (realD * realD);

				if (maxDelta < realD) {
                    maxDelta = realD;
                }
				++pairCount;
			}
		}
		//System.out.println(average);
		double[] avgs = new double[5];
		avgs[0] = average;
		avgs[1] = avgSquare;
		avgs[2] = maxDelta;
		avgs[3] = pairCount;
		avgs[4] = missingDistCount;
        return  avgs;
	}
}
