package cgl.imr.samples.dacidr.pwa.util;

import java.util.List;

import org.biojava3.alignment.template.SubstitutionMatrix;
import org.biojava3.core.sequence.ProteinSequence;

import edu.indiana.salsahpc.AlignedData;
import edu.indiana.salsahpc.AlignmentData;
import edu.indiana.salsahpc.BioJavaWrapper;
import edu.indiana.salsahpc.DistanceType;
import edu.indiana.salsahpc.Sequence;
import edu.indiana.salsahpc.SimilarityMatrix;
import edu.indiana.salsahpc.SmithWatermanAligner;

public class SequenceAlignment {
	@SuppressWarnings("rawtypes")
	public static AlignmentData getNWAlignedData(String sequenceA, String sequenceB, 
			short gapOpen, short gapExt, SubstitutionMatrix scoringMatrix){
		@SuppressWarnings("unchecked")
		AlignmentData ad = BioJavaWrapper.
				calculateAlignment(
						new ProteinSequence(sequenceA), 
						new ProteinSequence(sequenceB), 
						gapOpen, gapExt, scoringMatrix, DistanceType.PercentIdentity);
		return ad;
	}
	
	public static List<AlignedData> getSWGAlignedData(Sequence sequenceA, Sequence sequenceB, 
			short gapOpen, short gapExt, SimilarityMatrix scoringMatrix) throws Exception{
		SmithWatermanAligner aligner = new SmithWatermanAligner();
		List<AlignedData> ads = aligner.align(scoringMatrix, -gapOpen, -gapExt, 
				sequenceA, sequenceB);
		return ads;
	}
}
