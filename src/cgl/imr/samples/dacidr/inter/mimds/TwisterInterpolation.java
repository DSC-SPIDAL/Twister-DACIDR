package cgl.imr.samples.dacidr.inter.mimds;


import java.io.IOException;

import org.safehaus.uuid.UUIDGenerator;

import cgl.imr.base.TwisterException;
import cgl.imr.base.TwisterModel;
import cgl.imr.base.TwisterMonitor;
import cgl.imr.base.impl.JobConf;
import cgl.imr.client.TwisterDriver;
import cgl.imr.samples.dacidr.inter.util.Constants;

public class TwisterInterpolation {
	private static UUIDGenerator uuidGen = UUIDGenerator.getInstance();
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		if (args.length != 9) {
	        System.err.println("[map number] [input sample fasta][input sample coordinates]" +
	        		"[input out-sample folder][input out-sample prefix][output out-sample prefix]" +
	        		"[k value][aligner type][idx file]");
	        System.exit(2);
  	    }
		int mapTaskNo = Integer.parseInt(args[0]);
		String sampleFasta = args[1];
		String sampleCoordinates = args[2];
		String outSampleFolder = args[3];
		String outSamplePrefix = args[4];
		String outputPrefix = args[5];
		int kValue = Integer.parseInt(args[6]);
		String type = args[7];
		String idxFile = args[8];
		if(type.equals("SWG") && type.equals("NW")){
			System.out.println("The input type must be SWG or NW!");
			System.exit(2);
		}
		try {	
			double start = System.currentTimeMillis();
			//List<Point> result = 
					driveMapReduce(mapTaskNo, 0, 
					sampleFasta, sampleCoordinates, outSampleFolder, outSamplePrefix, outputPrefix,
					kValue, type, idxFile);
			double end = System.currentTimeMillis();
			//FileOperation.writeToFile(outCoordinates, result);
			System.out.println("Total time:" + (end-start)/1000.0 + "seconds");
			System.exit(0);
		} catch (TwisterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private static void driveMapReduce(int mapNum, int reduceNum,
			String sampleFasta, String sampleCoordinates, String outSampleFolder, 
			String outSamplePrefix, String outputPrefix, int kValue, String type, String idxFile)
			throws TwisterException {
		String jobID = "Twister MI-MDS Interpolation: " + uuidGen.generateRandomBasedUUID();

		JobConf jobConf = new JobConf(jobID);
		jobConf.setMapperClass(InterpolationMapper.class);
		jobConf.setNumMapTasks(mapNum);
		jobConf.setNumReduceTasks(reduceNum);
		
		jobConf.addProperty(Constants.SAMPLE_COORDINATES, sampleCoordinates);
		jobConf.addProperty(Constants.SAMPLE_FASTA, sampleFasta);
		jobConf.addProperty(Constants.KVALUE, String.valueOf(kValue));
		jobConf.addProperty(Constants.TYPE, type);
		jobConf.addProperty(Constants.OUTSAMPLE_FOLDER, outSampleFolder);
		jobConf.addProperty(Constants.OUTSAMPLE_PREFIX, outSamplePrefix);
		jobConf.addProperty(Constants.IDX_FILE, idxFile);
		jobConf.addProperty(Constants.OUTPUT_PREFIX, outputPrefix);

		TwisterModel hmdsDriver = null;
		TwisterMonitor monitor = null;
		try{
			System.out.println("Use Driver");
			hmdsDriver = new TwisterDriver(jobConf);
			System.out.println(hmdsDriver);
			hmdsDriver.configureMaps();
			System.out.println("Driver running start!");
			monitor = hmdsDriver.runMapReduce();
			
			monitor.monitorTillCompletion();
			System.out.println("Running finished!");
			
			hmdsDriver.close();
		}
		catch(Exception e){
			hmdsDriver.close();
			e.printStackTrace();
			throw new TwisterException(e);
		}
	}
}
