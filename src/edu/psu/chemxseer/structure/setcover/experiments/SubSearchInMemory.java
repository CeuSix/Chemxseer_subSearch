package edu.psu.chemxseer.structure.setcover.experiments;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.apache.commons.math.MathException;

import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.preprocess.RandomChoseDBGraph;
import edu.psu.chemxseer.structure.query.InFrequentQueryGenerater2;
import edu.psu.chemxseer.structure.setcover.IO.FeatureFileGenerater;
import edu.psu.chemxseer.structure.setcover.IO.Input_Mem;
import edu.psu.chemxseer.structure.setcover.featureGenerator.FeatureConverter_SubSearch;
import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter;
import edu.psu.chemxseer.structure.setcover.impl.MaxCoverSolver_Sequential;
import edu.psu.chemxseer.structure.setcover.maxCoverStatus.ICoverStatus;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphDatabase_InMem;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphDatabase_OnDisk;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureProcessorDuralClass;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.NoPostingFeatures;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.PostingFeaturesMultiClass;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureFactory.FeatureFactoryType;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphDatabase;
import edu.psu.chemxseer.structure.util.MemoryConsumptionCal;

/**
 * The experiment of subgrpah search feature selection. 
 * Assuming that all the sets can be stored in-memory
 * @author dayuyuan
 *
 */
public class SubSearchInMemory {
	
//	protected static String baseName = "/home/duy113/setcover/exp/AIDS/";
//	protected static String gDBFileRaw = baseName + "DBFile_Raw";
//	protected static String gDBFileName = baseName + "DBFile";
//	protected static String trainingQueries = baseName + "TrainingQueries";
//	protected static String testingQueries = baseName + "TestingQueries";
//	protected static String edgeFileName = baseName + "FGindex_0.1/edge";
//	
//	/**
//	 * Pre-process, Generating 1,000 database graphs & 1,000 queries 
//	 * @throws IOException
//	 * @throws ParseException
//	 * @throws MathException
//	 */
//	public void preProcess() throws IOException, ParseException, MathException {
//		// 0. Sample the Graph Database to 1000
//		GraphDatabase_OnDisk rawDB = new GraphDatabase_OnDisk(gDBFileRaw,
//				MyFactory.getSmilesParser());
//		RandomChoseDBGraph.randomlyChooseDBGraph(rawDB, 1000, gDBFileName);
//		GraphDatabase_OnDisk gDB = new GraphDatabase_OnDisk(gDBFileName,
//				MyFactory.getSmilesParser());
//		
//		// 1. Step One: Generate Training Queries: mine frequent subgraphs &
//		// random sampling as queries
//		InFrequentQueryGenerater2 queryGen = new InFrequentQueryGenerater2();
//		queryGen.generateInFrequentQueries2(4, 30, 1000, 0.01, gDB, 0,
//				trainingQueries);
//		
//		// 2. Step Two: Merge the Database File with the Query File
//		NoPostingFeatures features = new NoPostingFeatures(trainingQueries,
//				MyFactory.getFeatureFactory(FeatureFactoryType.OneFeature));
//		GraphDatabase queries = new GraphDatabase_InMem(features);
//		FeatureProcessorDuralClass processor = new FeatureProcessorDuralClass();
//		processor.mergeGraphFile(gDB, queries, MyFactory.getSmilesParser(),
//				gDBFileName + "_merged", gDBFileName + "_class");
//		//3. Run frequent subgraph minings for batch-mode mining
//		for(int freqID = 0; freqID< 2; freqID++){
//			String batchBase = baseName + "inMem_0.1/";
//			double minSupport = 0.1;
//			if(freqID == 1){
//				batchBase = baseName + "inMem_0.05/";
//				minSupport = 0.05;
//			}
//				
//			File temp = new File(batchBase);
//			if(!temp.exists())
//				temp.mkdirs();
//			// 3. Mine Frequent Subgraph Features & Convert to File Input
//			System.out.println("Memory Consumption Before Frequent Feature Mining" + MemoryConsumptionCal.usedMemory());
//			String featureFileName = batchBase + "MultiClassFeatures";
//			String postingFileName = batchBase + "MultiClassPostings";
//			String[] postingFileNames = new String[4];
//			for (int i = 0; i < 4; i++)
//				postingFileNames[i] = postingFileName + i;
//			
//			PostingFeaturesMultiClass multiFeatures = processor
//					.frequentSubgraphMining2(gDBFileName + "_merged", gDBFileName
//							+ "_class", featureFileName, postingFileNames,
//							MyFactory.getSmilesParser(), minSupport, -1, 10);
//			MemoryConsumptionCal.runGC();
//			System.out.println("Memory Consumption After Frequent Feature Mining" + MemoryConsumptionCal.usedMemory());
//		}
//	}
//	
//	/**
//	 * Run the Batch mode algorithm, assuming that all sets can be stored in-memory
//	 * But no-inverted index is built
//	 * @param featureCount
//	 * @param freqID
//	 * @throws IOException
//	 * @throws ParseException 
//	 */
//	public void runInMemNoIndex(int featureCount, int freqID) throws IOException, ParseException{
//		String batchBase = baseName + "inMem_0.1/";
//		if(freqID ==1)
//			batchBase = baseName + "inMem_0.05/";
//		
//		String selectedFeatures = batchBase + "SelectedFeatures";
//		
//		Input_Mem input = Input_Mem.newInstance(setFeatures, true);
//		
//		ICoverStatus status = new MaxCoverStatus_BooleanArray(input, converter);
//		//	new SetCoverStatus_FeatureWrapper2(featureCount, FeatureSetType.subSearch, 10000, 10000);
//		MaxCoverSolver_Sequential onDiskGreedy = new MaxCoverSolver_Sequential(input, status);
//		int[] result = onDiskGreedy.runGreedy(featureCount);
//		input.storeSelected(result, selectedFeatures);
//		
//		System.out.println("Total # of Covered Items: "
//				+ onDiskGreedy.totalCoveredItems());
//		this.bildLindexforSetCover(selectedFeatures, edgeFileName, batchBase);
//	}
}
