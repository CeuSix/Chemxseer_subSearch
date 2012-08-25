package edu.psu.chemxseer.structure.setcover.experiments;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import org.apache.commons.math.MathException;

import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.setcover.IO.FeatureFileGenerater;
import edu.psu.chemxseer.structure.setcover.IO.Input_FileStream;
import edu.psu.chemxseer.structure.setcover.featureGenerator.FeatureConverter_SubSearch;
import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter;
import edu.psu.chemxseer.structure.setcover.impl.MaxCoverSolver_Sequential;
import edu.psu.chemxseer.structure.setcover.maxCoverStatus.ICoverStatus;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureFactory.FeatureFactoryType;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureProcessorDuralClass;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.NoPostingFeatures;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.PostingFeaturesMultiClass;

/**
 * In this experiment, I want to show the bottleneck for the batch mode
 * (1) First mine frequent features with minimum support 0.1, 0.05, 0.01, 0.005, 0.0001
 * A: Measure the Memory Consumption 
 * B: Time complexity of Mining those features
 * (2) Store all the features-on-disk. Tune the disk batch to read 200 features at one time. 
 * Use the on-disk greedy to select the top 100 or 500 features
 * A: Time complexity of Greedy Set-cover Mining
 * B: The quality of the Features
 * C: Measure Memory consumption
 * 
 * Status: boolean-array
 * Mining model: on-disk
 * 
 * Want to show:
 * (1) Performance of frequent features mining
 * (2) Performance of on-disk feature mining
 * (3) Performance of "# of covered items" when the minimum support decreases

 * @author dayuyuan
 *
 */
public class DecreasingFrequency {
	
//	public static void main(String[] args) throws IOException, ParseException, MathException{
//		//1. Pre-processing
//		SubgraphSearchExp.main(args);
//		//2. Mine Frequent Subgraphs
//		double[] minimumFrequency = {0.1, 0.05, 0.03, 0.01, 0.008, 0.006};
//		for(int i = 0; i< minimumFrequency.length; i++){
//			mineFrequentFeatures(i, minimumFrequency[i]);
//			storeFeatures(i);
//			batchMine(i, 100);
//		}
//	}
//	
//	public static void mineFrequentFeatures(int ID, double minSupport){
//		String batchBase = SubgraphSearchExp.baseName + "batch_"  + ID + "/";
//		
//		File temp = new File(batchBase);
//		if(!temp.exists())
//			temp.mkdirs();
//		// 3. Mine Frequent Subgraph Features & Convert to File Input
//		String featureFileName = batchBase + "MultiClassFeatures";
//		String postingFileName = batchBase + "MultiClassPostings";
//		String setFeatures = batchBase + "MultiClassSets";
//		String[] postingFileNames = new String[4];
//		for (int i = 0; i < 4; i++)
//			postingFileNames[i] = postingFileName + i;
//
//		FeatureProcessorDuralClass processor = new FeatureProcessorDuralClass();
//		String gDBFileName = SubgraphSearchExp.gDBFileName;
//		
//		PostingFeaturesMultiClass multiFeatures = processor
//				.frequentSubgraphMining2(gDBFileName + "_merged", gDBFileName
//						+ "_class", featureFileName, postingFileNames,
//						MyFactory.getSmilesParser(), minSupport, -1, 30); 
//					//  Here the frequency for the queries is set to be -1, which in the real mining, will be replaced
//					// by 1. 
//	}
//	
//	public static void storeFeatures(int ID) throws IOException{
//		String batchBase = SubgraphSearchExp.baseName + "batch_"  + ID + "/";
//		//1. First Step: For each of the frequent features, construct the set file
//		String featureFileName = batchBase + "MultiClassFeatures";
//		String postingFileName = batchBase + "MultiClassPostings";
//		String[] postingFileNames = new String[4];
//		for (int i = 0; i < 4; i++)
//			postingFileNames[i] = postingFileName + i;
//
//		String setFeatures = batchBase + "MultiClassSets";
//		NoPostingFeatures features= 
//			new NoPostingFeatures(featureFileName, MyFactory.getFeatureFactory(FeatureFactoryType.MultiFeature));
//		int[] classCount = new int[2];
//		classCount[0] = classCount[1] = 10000;
//		PostingFeaturesMultiClass multiFeatures = 
//			new PostingFeaturesMultiClass(postingFileNames, features, classCount);
//		IFeatureSetConverter converter = new FeatureConverter_SubSearch(classCount[0], classCount[1]);
//		FeatureFileGenerater.FeatureToSetFile(multiFeatures, setFeatures, converter);
//	}
//	
//	public static void batchMine(int ID, int K){
//		String batchBase = SubgraphSearchExp.baseName + "batch_"  + ID + "/";
//		String setFeatures = batchBase + "MultiClassSets";
//		String selectedFeatureFile = batchBase +  "Selected";
//		// 4. Run the on-disk Set-Cover Feature Selection
//		Input_FileStream input = Input_FileStream.newInstance(setFeatures, true);
//		//IMaxCoverStatus status = new SetCoverStatus_EWAH(input, converter);
//		//IMaxCoverStatus status = new SetCoverStatus_FeatureWrapper2(K, 
//		//		FeatureSetType.subSearch, 10000, 10000);
//		IFeatureSetConverter converter = new FeatureConverter_SubSearch(10000, 10000);
//		ICoverStatus status = new MaxCoverStatus_BooleanArray(input, converter);
//		MaxCoverSolver_Sequential onDiskGreedy = new MaxCoverSolver_Sequential(input, status);
//		int[] result = onDiskGreedy.runGreedy(K);
//		try {
//			input.storeSelected(result, selectedFeatureFile);
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		System.out.println("Total # of Covered Items: "
//				+ onDiskGreedy.totalCoveredItems());
//	}
}
