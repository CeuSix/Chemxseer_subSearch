package edu.psu.chemxseer.structure.setcover.experiments;

import java.io.IOException;

import edu.psu.chemxseer.structure.parmolExtension.GSpanMiner_MultiClass_Iterative;
import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.setcover.IO.Input_DFSStream;
import edu.psu.chemxseer.structure.setcover.IO.Input_FileBucket_Writter;
import edu.psu.chemxseer.structure.setcover.featureGenerator.BranchBoundCalculator;
import edu.psu.chemxseer.structure.setcover.featureGenerator.FeatureConverter_SubSearch;
import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter;
import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter.FeatureSetType;
import edu.psu.chemxseer.structure.setcover.impl.MaxCoverSolver_Stream;
import edu.psu.chemxseer.structure.setcover.interfaces.IBranchBoundCalculator;
import edu.psu.chemxseer.structure.setcover.maxCoverStatus.ICoverStatusStream;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureProcessorDuralClass;

/**
 * For the same minimum support settings: 
 * using the batch model (without branch & bound) to mine the features
 * Measure:
 * (1) Time complexity
 * (2) Memory Consumption
 * (3) # of covered items. 
 * (4) How the performance will be improved if the branch & bound algorithm was used. 
 * 
 * The streaming model is used with:
 * A: shortStatus / featureWrapper2 status
 * B: Streaming mining model
 * @author dayuyuan
 *
 */
public class StreamingBenefit {
	
//	public static void main(String args[]){
//		double[] minimumFrequency = {0.1, 0.05, 0.03, 0.01, 0.008, 0.006};
//		for(int i = 0; i< 6; i++){
//			try {
//				runStreamingFeatureMining(100, minimumFrequency[i], i);
//			} catch (IOException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
//	}
//	public static void runStreamingFeatureMining(int featureCount, double minSupport, int ID) throws IOException {
//		System.out.println("Stremaining Model, No BranchBound, ShortArray Status, " 
//				+ minSupport + " min Support");
//		
//		String gDBFileName = SubgraphSearchExp.gDBFileName;
//		
//		FeatureProcessorDuralClass processor = new FeatureProcessorDuralClass();
//		GSpanMiner_MultiClass_Iterative patternGen = processor
//				.getPatternEnumerator(gDBFileName + "_merged", gDBFileName
//						+ "_class", MyFactory.getSmilesParser(), minSupport, minSupport, 10);
//		int[] counts = patternGen.getClassGraphCount();
//		
//		Input_DFSStream input = new Input_DFSStream(patternGen);
//		IFeatureSetConverter converter = new FeatureConverter_SubSearch(counts[0], counts[1]);
//		ICoverStatusStream status = new Status_ShortMatrix(featureCount, converter);
////		IMaxCoverStatusStream status = 
////			new SetCoverStatus_FeatureWrapper2(featureCount, FeatureSetType.subSearch, 
////					counts[0], counts[1]);
//		IBranchBoundCalculator cal = new BranchBoundCalculator
//					(status, FeatureSetType.subSearch, counts[0], counts[1]);
////		NoPostingFeatures queries = new NoPostingFeatures(trainingQueries,
////				MyFactory.getFeatureFactory(FeatureFactoryType.OneFeature));
////		IBranchBoundCalculator cal = new BranchBoundCalculator2(status, FeatureSetType.subSearch,
////				counts[0], counts[1], queries);
//		MaxCoverSolver_Stream streamGreedy = 
//			new MaxCoverSolver_Stream(input, status, 1, null, 1);
//		streamGreedy.runGreedy(featureCount);
//		System.out.println("Total # of Covered Items: "
//				+ streamGreedy.totalCoveredItems());
//		// Sorting selected Features
//		String batchBase = SubgraphSearchExp.baseName + "batch_"  + ID + "/";
//		String selectedFeatureFile = batchBase + "Selected_Streaming_T";
//		Input_FileBucket_Writter out = new Input_FileBucket_Writter(selectedFeatureFile, false);
//		out.writeSelectedSets(status);
//	}
}
