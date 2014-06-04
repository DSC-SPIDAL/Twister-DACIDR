package cgl.imr.samples.dacidr.inter.util;

import java.util.List;

import org.biojava3.core.sequence.ProteinSequence;

import cgl.imr.samples.dacidr.inter.type.*;

import edu.indiana.salsahpc.AlignedData;
import edu.indiana.salsahpc.AlignmentData;
import edu.indiana.salsahpc.BioJavaWrapper;
import edu.indiana.salsahpc.DistanceType;
import edu.indiana.salsahpc.MatrixUtil;
import edu.indiana.salsahpc.Sequence;
import edu.indiana.salsahpc.SimilarityMatrix;
import edu.indiana.salsahpc.SmithWatermanAligner;

public class DistanceCalculation {
	public static double Euclidean(Point a, Point b){
		return Math.sqrt((a.getPosition().getX() - b.getPosition().getX())
		* (a.getPosition().getX() - b.getPosition().getX())
		+ (a.getPosition().getY() - b.getPosition().getY())
		* (a.getPosition().getY() - b.getPosition().getY())
		+ (a.getPosition().getZ() - b.getPosition().getZ())
		* (a.getPosition().getZ() - b.getPosition().getZ()));
	}
	
	public static double Euclidean(Vec3D a, Point b){
		return Math.sqrt((a.getX() - b.getPosition().getX())
		* (a.getX() - b.getPosition().getX())
		+ (a.getY() - b.getPosition().getY())
		* (a.getY() - b.getPosition().getY())
		+ (a.getZ() - b.getPosition().getZ())
		* (a.getZ() - b.getPosition().getZ()));
	}
	
	public static double Euclidean(Vec3D a, Vec3D b){
		return Math.sqrt((a.getX() - b.getX())
		* (a.getX() - b.getX())
		+ (a.getY() - b.getY())
		* (a.getY() - b.getY())
		+ (a.getZ() - b.getZ())
		* (a.getZ() - b.getZ()));
	}
		
	public static double getOriginalDistance(SequencePoint p1, SequencePoint p2, String type) 
			throws Exception{
		double d;
		if(type.equals("NW"))
			d = DistanceCalculation.NWDistance
				(p1.getSequence().toString(), 
						p2.getSequence().toString());
		else if(type.equals(("SWG"))){
			try{
				d = DistanceCalculation.SWGDistance(
						p1.getSequence(), 
						p2.getSequence());
			}
			catch(Exception e){
				System.out.println(p1.getSequence());
				System.out.println("outSample: " + p2.getSequence());
				throw e;
			}
		}
		else{
			System.err.println("Type is not right: " + type);
			throw new Exception();
		}
		return d;
	}

	public static double Euclidean(short[] oneRow, short[] oneColumn) throws Exception{
		int sum = 0;
		for(int i = 0; i < oneRow.length; i++){
			sum += (oneRow[i] - oneColumn[i]) * (oneRow[i] - oneColumn[i]);
		}
		
		int distance = (int)Math.sqrt(sum);
		if(distance > Short.MAX_VALUE){
			System.out.println("Distance is larger than Short Max Value");
			//throw new Exception();
		}
		
		return (double)distance/(double)Short.MAX_VALUE;
	}
	
	public static double NWDistance(String sequenceA, String sequenceB){
		@SuppressWarnings("rawtypes")
		AlignmentData ad = BioJavaWrapper.
				calculateAlignment(
						new ProteinSequence(sequenceA), 
						new ProteinSequence(sequenceB), 
						(short)9, (short)1, 
						MatrixUtil.getBlosum62(), 
						MatrixUtil.getBlosum62(), DistanceType.PercentIdentity);
		double pid 
		= 1.0 - (double)ad.getNumIdenticals() / (double)ad.getAlignmentLengthExcludingEndGaps();
		pid = Math.min(0.975, pid);
		pid = Math.log(1 - Math.pow(pid, 6))/Math.log(1 - Math.pow(0.975, 6));
		return pid;
	}
	
	public static double SWGDistance(Sequence a, Sequence b) throws Exception{
		SimilarityMatrix ednafull = SimilarityMatrix.getEDNAFULL();
		SmithWatermanAligner aligner = new SmithWatermanAligner();
		List<AlignedData> ads = aligner.align(
				ednafull, -16, -4, a, b);

		// We will just take the first alignment
		AlignedData ad = ads.get(0);
		//short distance = DistanceUtil.computePercentIdentityDistanceAsShort(ad);
		return 1.0 - (double)ad.getNumberOfIdenticalBasePairs(false)
				/(double)ad.getAlignmentLengthExcludingEndGaps();
	}
	
	public static DistancePoint SWGDistanceTest(Sequence a, Sequence b) throws Exception{
		SimilarityMatrix ednafull = SimilarityMatrix.getEDNAFULL();
		SmithWatermanAligner aligner = new SmithWatermanAligner();
		List<AlignedData> ads = aligner.align(
				ednafull, -16, -4, a, b);

		// We will just take the first alignment
		AlignedData ad = ads.get(0);
		//short distance = DistanceUtil.computePercentIdentityDistanceAsShort(ad);
		DistancePoint p = new DistancePoint();
		p.setIdenticalPairs(ad.getNumberOfIdenticalBasePairs(false));
		p.setAlignedLength(ad.getAlignmentLengthExcludingEndGaps());
		if(p.getAlignedLength() > 10){
			p.setDistance(1.0 - (double)ad.getNumberOfIdenticalBasePairs(false)
				/(double)ad.getAlignmentLengthExcludingEndGaps());
			return p;
		}
		else
			//p.setDistance(1.0);
			return null;
		
		//return p;
	}
}
