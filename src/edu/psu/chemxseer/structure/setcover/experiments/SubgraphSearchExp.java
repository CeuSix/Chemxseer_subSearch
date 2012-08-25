package edu.psu.chemxseer.structure.setcover.experiments;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.commons.math.MathException;

import edu.psu.chemxseer.structure.parmolExtension.GSpanMiner_MultiClass_Iterative;
import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.preprocess.RandomChoseDBGraph;
import edu.psu.chemxseer.structure.query.InFrequentQueryGenerater2;
import edu.psu.chemxseer.structure.setcover.IO.FeatureFileGenerater;
import edu.psu.chemxseer.structure.setcover.IO.Input_DFSStream;
import edu.psu.chemxseer.structure.setcover.IO.Input_FileStream;
import edu.psu.chemxseer.structure.setcover.IO.Input_Mem;
import edu.psu.chemxseer.structure.setcover.IO.Input_FileBucket_Writter;
import edu.psu.chemxseer.structure.setcover.featureGenerator.BranchBoundCalculator;
import edu.psu.chemxseer.structure.setcover.featureGenerator.BranchBoundCalculator2;
import edu.psu.chemxseer.structure.setcover.featureGenerator.FeatureConverter_SubSearch;
import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter;
import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter.FeatureSetType;
import edu.psu.chemxseer.structure.setcover.impl.MaxCoverSolver_InvertedIndex;
import edu.psu.chemxseer.structure.setcover.impl.MaxCoverSolver_Sequential;
import edu.psu.chemxseer.structure.setcover.impl.MaxCoverSolver_Stream;
import edu.psu.chemxseer.structure.setcover.interfaces.IBranchBoundCalculator;
import edu.psu.chemxseer.structure.setcover.maxCoverStatus.ICoverStatus;
import edu.psu.chemxseer.structure.setcover.maxCoverStatus.ICoverStatusStream;
import edu.psu.chemxseer.structure.subsearch.FGindex.SubgraphSearch_FGindexBuilder;
import edu.psu.chemxseer.structure.subsearch.Gindex.SubgraphSearch_GindexBuilder;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphDatabase_InMem;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphDatabase_OnDisk;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureProcessorDuralClass;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureProcessorFG;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureProcessorG;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureProcessorL;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.NoPostingFeatures;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureFactory.FeatureFactoryType;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.NoPostingFeatures_Ext;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.PostingFeatures;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.PostingFeaturesMultiClass;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphDatabase;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IOneFeature;
import edu.psu.chemxseer.structure.subsearch.Lindex.SubgraphSearch_Lindex;
import edu.psu.chemxseer.structure.subsearch.Lindex.SubgraphSearch_LindexBuilder;
import edu.psu.chemxseer.structure.subsearch.Lindex.SubgraphSearch_LindexPlus;
import edu.psu.chemxseer.structure.subsearch.Lindex.SubgraphSearch_LindexPlusBuilder;
import edu.psu.chemxseer.structure.subsearch.Lindex.SubgraphSearch_LindexSimple;
import edu.psu.chemxseer.structure.subsearch.Lindex.SubgraphSearch_LindexSimpleBuilder;
import edu.psu.chemxseer.structure.subsearch.Lindex.SubgraphSearch_LindexSimplePlus;
import edu.psu.chemxseer.structure.subsearch.Lindex.SubgraphSearch_LindexSimplePlusBuilder;
import edu.psu.chemxseer.structure.util.MemoryConsumptionCal;

/**
 * The Experiment For Subgraph Search
 * 
 * @author dayuyuan
 * 
 */
public class SubgraphSearchExp {
//	//private static String baseName = "/Users/dayuyuan/Documents/workspace/Experiment2/";
//	protected static String baseName = "/home/duy113/setcover/exp/AIDS/";
//	//protected static String baseName = "/home/duy113/setcover/exp/NCI/";
//	protected static String gDBFileRaw = baseName + "DBFile_Raw";
//	protected static String gDBFileName = baseName + "DBFile";
//	protected static String trainingQueries = baseName + "TrainingQueries";
//	protected static String testingQueries = baseName + "TestingQueries";
//	protected static String edgeFileName = baseName + "FGindex_0.1/edge";
//
//	public static void main(String[] args) throws IOException, ParseException,
//			MathException {
//		SubgraphSearchExp search = new SubgraphSearchExp();
////		search.preProcess();
////		
////		System.out.println("Exp0: Run Gindex");
////		search.runGindexMining();
////		System.out.println("Exp0: Run FGindex");
////		search.runFGindexMininer();
////		
////		
//		System.out.println("Exp1: Run in Memory Sequential Scan, min sup 0.1, boolean_array status, 600 features");
//		search.runInMemGreedy(500, 0);
//		System.out.println("Exp2: Run in Memory Sequential Scan, min sup 0.05, boolean_array status, 600 features");
//		search.runInMemGreedy(500, 1);
//		System.out.println("Exp3: Run in Streaming Mining, min sup 0.05, Feature Wrapper Status, Naive BB, Type 1");
////		search.runStreamingFeatureMining(600, 1, baseName + "Stream_1_FW_N_0.05/");
////		search.runStreamingFeatureMining(600, 1, baseName + "TestStream/");
////		System.out.println("Exp4: Run in Streaming Mining, min sup 0.05, Feature Wrapper Status, Naive BB, Type 0");
////		search.runStreamingFeatureMining(600, 0, baseName + "Stream_0_FW_N_0.05/");
////		System.out.println("Exp5: Run in Streaming Mining, min sup 0.05, Feature Wrapper Status, Adv BB, Type 1");
////		search.runStreamingFeatureMining2(600, 1, baseName + "Stream_1_FW_A_0.05/");
////		System.out.println("Exp6: Run in Streaming Mining, min sup 0.05, Feature Wrapper Status, Adv BB, Type 0");
////		search.runStreamingFeatureMining2(600, 0, baseName + "Stream_0_FW_A_0.05/");
//		
////		SubgraphSearchTest.main(args);
//	}
//
//	/**
//	 * (1) Sample Graph Database: 10,000 (2) Generate Training Queries: 4, 30,
//	 * 10,000, 0.05, sampleed with Uniform Distribution (3) Merge Sampled Graph
//	 * Database + Generated Training Queries
//	 * 
//	 * @throws IOException
//	 * @throws ParseException
//	 * @throws MathException
//	 */
//	public void preProcess() throws IOException, ParseException, MathException {
//		// 0. Sample the Graph Database to 200
//		GraphDatabase_OnDisk rawDB = new GraphDatabase_OnDisk(gDBFileRaw,
//				MyFactory.getSmilesParser());
//		RandomChoseDBGraph.randomlyChooseDBGraph(rawDB, 100000, gDBFileName);
//		GraphDatabase_OnDisk gDB = new GraphDatabase_OnDisk(gDBFileName,
//				MyFactory.getSmilesParser());
//		// 1. Step One: Generate Training Queries: mine frequent subgraphs &
//		// random sampling as queries
//		InFrequentQueryGenerater2 queryGen = new InFrequentQueryGenerater2();
//		queryGen.generateInFrequentQueries2(4, 30, 1000, 0.01, gDB, 0,
//				trainingQueries);
//		// TODO: Test Query Generation
//		// 2. Step Two: Merge the Database File with the Query File
//		NoPostingFeatures features = new NoPostingFeatures(trainingQueries,
//				MyFactory.getFeatureFactory(FeatureFactoryType.OneFeature));
//		GraphDatabase queries = new GraphDatabase_InMem(features);
//		FeatureProcessorDuralClass processor = new FeatureProcessorDuralClass();
//		processor.mergeGraphFile(gDB, queries, MyFactory.getSmilesParser(),
//				gDBFileName + "_merged", gDBFileName + "_class");
//	}
//	
//	/**
//	 * (1) Mine Frequent Features (Multi-Class) with minSupport: 0.1 & 0.05,
//	 * (2) Convert the Frequent Features to Set-Format 
//	 * (3) Run the greedy on-Disk max_coverage solver: batchNum = 50
//	 * 
//	 * @throws IOException
//	 */
//	public void runBatchFeatureMining(int featureCount, int freqID) throws IOException {
//		String batchBase = baseName + "batchBase_0.1/";
//		if(freqID ==1)
//			batchBase = baseName + "batchBase_0.05/";
//		double minSupport = 0.1;
//		if(freqID == 1)
//			minSupport = 0.05;
//			
//		File temp = new File(batchBase);
//		if(!temp.exists())
//			temp.mkdirs();
//		// 3. Mine Frequent Subgraph Features & Convert to File Input
//
//		System.out.println("Memory Consumption Before Frequent Feature Mining" + MemoryConsumptionCal.usedMemory());
//		String featureFileName = batchBase + "MultiClassFeatures";
//		String postingFileName = batchBase + "MultiClassPostings";
//		String setFeatures = batchBase + "MultiClassSets";
//		String selectedFeatures = batchBase + "SelectedFeatures";
//		String[] postingFileNames = new String[4];
//		for (int i = 0; i < 4; i++)
//			postingFileNames[i] = postingFileName + i;
//
//		FeatureProcessorDuralClass processor = new FeatureProcessorDuralClass();
//		PostingFeaturesMultiClass multiFeatures = processor
//				.frequentSubgraphMining2(gDBFileName + "_merged", gDBFileName
//						+ "_class", featureFileName, postingFileNames,
//						MyFactory.getSmilesParser(), minSupport, -1, 10);
//		MemoryConsumptionCal.runGC();
//		System.out.println("Memory Consumption After Frequent Feature Mining" + MemoryConsumptionCal.usedMemory());
//		IFeatureSetConverter converter = new FeatureConverter_SubSearch(100000, 1000);
//		FeatureFileGenerater.FeatureToSetFile(multiFeatures, setFeatures, converter);
//		// 4. Run the on-disk Set-Cover Feature Selection
//		Input_FileStream input = Input_FileStream.newInstance(setFeatures, true);
//		
//		ICoverStatus status = new MaxCoverStatus_BooleanArray(input, converter); 
//		//	new SetCoverStatus_FeatureWrapper2(featureCount, FeatureSetType.subSearch, 10000, 10000);
//		MaxCoverSolver_Sequential onDiskGreedy = new MaxCoverSolver_Sequential(input, status);
//		int[] result = onDiskGreedy.runGreedy(featureCount);
//		input.storeSelected(result, selectedFeatures);
//		
//		System.out.println("Total # of Covered Items: "
//				+ onDiskGreedy.totalCoveredItems());
//	}
//	/**
//	 * (1) Mine Frequent Features (Multi-Class) with minSupport: 0.1 & 0.05,
//	 * (2) Convert the Frequent Features to Set-Format 
//	 * (3) Run the greedy in-memory solver: batchNum = 50
//	 * 
//	 * @param featureCount
//	 * @param freqID
//	 * @throws IOException
//	 * @throws ParseException 
//	 */
//	public void runInMemGreedy(int featureCount, int freqID) throws IOException, ParseException{
//		String batchBase = baseName + "inMem_0.1/";
//		if(freqID ==1)
//			batchBase = baseName + "inMem_0.05/";
//		double minSupport = 0.1;
//		if(freqID == 1)
//			minSupport = 0.05;
//			
//		File temp = new File(batchBase);
//		if(!temp.exists())
//			temp.mkdirs();
//		// 3. Mine Frequent Subgraph Features & Convert to File Input
//		System.out.println("Memory Consumption Before Frequent Feature Mining" + MemoryConsumptionCal.usedMemory());
//		String featureFileName = batchBase + "MultiClassFeatures";
//		String postingFileName = batchBase + "MultiClassPostings";
//		String setFeatures = batchBase + "MultiClassSets";
//		String selectedFeatures = batchBase + "SelectedFeatures";
//		String[] postingFileNames = new String[4];
//		for (int i = 0; i < 4; i++)
//			postingFileNames[i] = postingFileName + i;
//
//		FeatureProcessorDuralClass processor = new FeatureProcessorDuralClass();
//		PostingFeaturesMultiClass multiFeatures = processor
//				.frequentSubgraphMining2(gDBFileName + "_merged", gDBFileName
//						+ "_class", featureFileName, postingFileNames,
//						MyFactory.getSmilesParser(), minSupport, -1, 10);
//		MemoryConsumptionCal.runGC();
//		
//		
//		
//		IFeatureSetConverter converter = new FeatureConverter_SubSearch(100000,1000);
//		FeatureFileGenerater.FeatureToSetFile(multiFeatures, setFeatures, converter);
//		// 4. Run the on-disk Set-Cover Feature Selection
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
//	
//	/**
//	 * Run the Stream mining:Several Parameters;
//	 * (1) K in swap criterion, default = 1
//	 * (2) swap criterion type: 0 or 1
//	 * (3) branch & bound: naive & adv
//	 * (4) status: FW (featureWraper), Short(short)
//	 * @param featureCount
//	 * @param streamingBase
//	 * @throws IOException
//	 * @throws ParseException 
//	 */
//	public void runStreamingFeatureMining(int featureCount, int swapType, String streamingBase) throws IOException, ParseException {
//		File temp = new File(streamingBase);
//		if(!temp.exists())
//			temp.mkdirs();
//		// 3. Run the Streaming Set-Cover Feature Selection
//		FeatureProcessorDuralClass processor = new FeatureProcessorDuralClass();
//		GSpanMiner_MultiClass_Iterative patternGen = processor
//				.getPatternEnumerator(gDBFileName + "_merged", gDBFileName
//						+ "_class", MyFactory.getSmilesParser(), 0.1, -1, 10);
//		int[] counts = patternGen.getClassGraphCount();
//		
//		Input_DFSStream input = new Input_DFSStream(patternGen);
////		IFeatureSetConverter converter = new FeatureConverter_SubSearch(counts[0], counts[1]);
////		IMaxCoverStatusStream status = new SetCoverStatus_Short(featureCount, converter);
//		ICoverStatusStream status = 
//			new MaxCoverStatus_FeatureWrapperPartialIndex(featureCount, FeatureSetType.subSearch, 
//					counts[0], counts[1]);
////		IMaxCoverStatusStream status = new SetCoverStatus_EWAH_Count(input, converter, featureCount);
//		IBranchBoundCalculator cal = new BranchBoundCalculator(
//				status, FeatureSetType.subSearch, counts[0], counts[1]);
//		MaxCoverSolver_Stream streamGreedy = 
//			new MaxCoverSolver_Stream(input, status, 1, cal, swapType);
//		streamGreedy.runGreedy(featureCount);
//		//Store the selected Features
//		String selectedFeatureFile = streamingBase + "SelectedFeatures";
//		Input_FileBucket_Writter out = new Input_FileBucket_Writter(selectedFeatureFile, false);
//		out.writeSelectedSets(status);
//		System.out.println("Total # of Covered Items: "
//				+ streamGreedy.totalCoveredItems());
//		this.bildLindexforSetCover(selectedFeatureFile, edgeFileName, streamingBase);
//		
//	}
//
//	public void runStreamingFeatureMining2(int featureCount, int swapType, String streamingBase) throws IOException, ParseException {
//		File temp = new File(streamingBase);
//		if(!temp.exists())
//			temp.mkdirs();
//		// 3. Run the Streaming Set-Cover Feature Selection
//		FeatureProcessorDuralClass processor = new FeatureProcessorDuralClass();
//		GSpanMiner_MultiClass_Iterative patternGen = processor
//				.getPatternEnumerator(gDBFileName + "_merged", gDBFileName
//						+ "_class", MyFactory.getSmilesParser(), 0.01, -1, 10);
//		int[] counts = patternGen.getClassGraphCount();
//		
//		Input_DFSStream input = new Input_DFSStream(patternGen);
//		ICoverStatusStream status = 
//			new MaxCoverStatus_FeatureWrapperPartialIndex(featureCount, FeatureSetType.subSearch, 
//					counts[0], counts[1]);
//		NoPostingFeatures queries = new NoPostingFeatures(trainingQueries,
//				MyFactory.getFeatureFactory(FeatureFactoryType.OneFeature));
//		IBranchBoundCalculator cal = new BranchBoundCalculator2(status, FeatureSetType.subSearch,
//				counts[0], counts[1], queries);
//		
//		MaxCoverSolver_Stream streamGreedy = 
//			new MaxCoverSolver_Stream(input, status, 1, cal, swapType);
//		streamGreedy.runGreedy(featureCount);
//		//Store the selected Features
//		String selectedFeatureFile = streamingBase + "SelectedFeatures";
//		Input_FileBucket_Writter out = new Input_FileBucket_Writter(selectedFeatureFile, false);
//		out.writeSelectedSets(status);
//		System.out.println("Total # of Covered Items: "
//				+ streamGreedy.totalCoveredItems());
//		this.bildLindexforSetCover(selectedFeatureFile, edgeFileName, streamingBase);
//		
//	}
//	/**
//	 * Build the Gindex 
//	 * (1) Frequent Feature Mining 
//	 * (2) Gindex Feature Mining
//	 * (3) Lindex Construction
//	 * 
//	 * @throws IOException
//	 * @throws ParseException
//	 */
//	public void runGindexMining() throws IOException, ParseException {
//		String gIndexBaseName = baseName + "Gindex_0.1/";
//		File temp = new File(gIndexBaseName);
//		if(!temp.exists())
//			temp.mkdirs();
//		// 3. Mine Frequent Subgraph Features
//		String featureFileName = gIndexBaseName + "GFrequentFeatures";
//		String postingFileName = gIndexBaseName + "GPostingFiles_0.1";
//		PostingFeatures freqFeatures = FeatureProcessorG
//				.frequentSubgraphMining(gDBFileName, featureFileName,
//						postingFileName, 0.1, 3, 10, MyFactory
//								.getSmilesParser());
//		
////		NoPostingFeatures noPostingFreq = new NoPostingFeatures(featureFileName, 
////				MyFactory.getFeatureFactory(FeatureFactoryType.OneFeature));
////		PostingFeatures freqFeatures = new PostingFeatures(postingFileName, noPostingFreq);
//		// 4. Run Gindex-Feature Selection
//		GraphDatabase_OnDisk gDB = new GraphDatabase_OnDisk(gDBFileName,
//				MyFactory.getSmilesParser());
//		String selectedFeatureFile = gIndexBaseName + "GSelectedFeatures_0.1";
//		String selectedPostingFile = gIndexBaseName + "GSelectedPostings_0.1";
//		
//
//		SubgraphSearch_GindexBuilder indexBuilder = new SubgraphSearch_GindexBuilder();
//		indexBuilder.buildIndex(freqFeatures, gDB, false, gIndexBaseName,
//				selectedFeatureFile, selectedPostingFile, MyFactory
//						.getDFSCoder());
//		// Build the corresponding Lindex
//		NoPostingFeatures<IOneFeature> features = 
//			new NoPostingFeatures<IOneFeature>(selectedFeatureFile, 
//					MyFactory.getFeatureFactory(FeatureFactoryType.OneFeature));
//		NoPostingFeatures_Ext<IOneFeature> extFeatures = new NoPostingFeatures_Ext<IOneFeature>(features);
//		SubgraphSearch_LindexSimpleBuilder builder = new SubgraphSearch_LindexSimpleBuilder();
//		SubgraphSearch_LindexSimple lindex = builder.buildIndex(extFeatures, 
//				new GraphDatabase_OnDisk(gDBFileName, MyFactory.getSmilesParser()), gIndexBaseName,
//				MyFactory.getSmilesParser());
//	}
//
//	/**
//	 * Build the FGindex 
//	 * (1) Frequent Feature Mining 
//	 * (2) FGindex Feature Mining(Delta-TCFG) 
//	 * (3) Lindex_Adv Construction
//	 * 
//	 * @throws IOException
//	 * @throws ParseException 
//	 */
//	public void runFGindexMininer() throws IOException, ParseException {
//		String fgIndexBaseName = baseName + "FGindex_0.1/";
//		File temp = new File(fgIndexBaseName);
//		if(!temp.exists())
//			temp.mkdirs();
//		long beforeMemUsed = MemoryConsumptionCal.usedMemory();
//		// 3. Mine Frequent Subgraph Features
//		String featureFileName = fgIndexBaseName + "FGFrequentFeatures_0.1";
//		String postingFileName = fgIndexBaseName + "FGPostingFiles_0.1";
//		PostingFeatures freqFeatures = FeatureProcessorFG
//				.frequentSubgraphMining(gDBFileName, featureFileName,
//						postingFileName, 0.1, 10, MyFactory.getSmilesParser());
//
//		System.out.println("Mining Frequent subgraphs consumption in B" + (MemoryConsumptionCal.usedMemory()-beforeMemUsed));
//		// 4. Run FGindex-Feature Selection
//		
//		GraphDatabase_OnDisk gDB = new GraphDatabase_OnDisk(gDBFileName,
//				MyFactory.getSmilesParser());
//		SubgraphSearch_FGindexBuilder indexBuilder = new SubgraphSearch_FGindexBuilder();
//		indexBuilder.buildIndex(freqFeatures.getFeatures(), gDB,
//				fgIndexBaseName, MyFactory.getDFSCoder());
//		//5. Build the Lindex_Adv
//		//1. Mine-edge features & load previous mined FG features
//		PostingFeatures edgeFeatures = 
//			FeatureProcessorL.findEdgeOneFeatures(gDBFileName, fgIndexBaseName+"edge", fgIndexBaseName+"edgePosting", MyFactory.getSmilesParser());
//		NoPostingFeatures selectedFeature = 
//			new NoPostingFeatures(fgIndexBaseName +"StatusRecordedFeatures" , 
//					MyFactory.getFeatureFactory(FeatureFactoryType.OneFeature));
//	
//		//2. Combine this two features
//		NoPostingFeatures lindexFeatures = FeatureProcessorL.mergeFeatures(freqFeatures.getFeatures(), edgeFeatures.getFeatures());
//		NoPostingFeatures_Ext selectedFeatures 
//			= new NoPostingFeatures_Ext(new NoPostingFeatures(null, lindexFeatures.getSelectedFeatures(), false));
//		NoPostingFeatures_Ext onDiskFeatures 
//			= new NoPostingFeatures_Ext(new NoPostingFeatures(null, lindexFeatures.getUnSelectedFeatures(), false));
//		//3. Build the Lindex-plus index with all those features
//		SubgraphSearch_LindexSimplePlusBuilder builder = new SubgraphSearch_LindexSimplePlusBuilder();
//		SubgraphSearch_LindexSimplePlus lindex = builder.buildIndex(selectedFeatures, onDiskFeatures, 
//				new GraphDatabase_OnDisk(gDBFileName, MyFactory.getSmilesParser()), fgIndexBaseName, MyFactory.getSmilesParser());
//	}
//	
//	/**
//	 * Given the selecteFeatures (in set cover selection model)
//	 * (1) Merge them with the edgeFeatures
//	 * (2) build the Lindex in the lindexBaseName folder
//	 * @param selectedFeatures
//	 * @param edgeFeatures
//	 * @param lindexBaseName
//	 * @throws IOException 
//	 * @throws ParseException 
//	 */
//	public void bildLindexforSetCover(String selectedFeatureFile, String edgeFeatureFile, String lindexBaseName) throws IOException, ParseException{
//		NoPostingFeatures edgeFeatures = new NoPostingFeatures(edgeFeatureFile, 
//				MyFactory.getFeatureFactory(FeatureFactoryType.OneFeature));
//		
//		Input_Mem input = Input_Mem.newInstance(selectedFeatureFile, true);
//		NoPostingFeatures selectedFeatures = input.toNoPostingFeatures(FeatureFactoryType.OneFeature);
//		//1. Merge the edgeFeatures & selectedFeatures
//		NoPostingFeatures_Ext lindexFeatures = new NoPostingFeatures_Ext(
//				FeatureProcessorL.mergeFeatures(selectedFeatures, edgeFeatures));
//		//2. Build the Lindex
//		SubgraphSearch_LindexSimpleBuilder builder = new SubgraphSearch_LindexSimpleBuilder();
//		SubgraphSearch_LindexSimple lindex
//			= builder.buildIndex(lindexFeatures, 
//					new GraphDatabase_OnDisk(gDBFileName, MyFactory.getSmilesParser()), 
//					lindexBaseName, MyFactory.getSmilesParser());
//	}
//	
//	public void runTest(int featureCount) throws IOException{
//		String batchBase = baseName + "test/";
//			
//		File temp = new File(batchBase);
//		if(!temp.exists())
//			temp.mkdirs();
//		// 3. Mine Frequent Subgraph Features & Convert to File Input
//		MemoryConsumptionCal.runGC();
//		System.out.println("Memory Consumption Before Frequent Feature Mining" + MemoryConsumptionCal.usedMemory());
//		String featureFileName = batchBase + "MultiClassFeatures";
//		String postingFileName = batchBase + "MultiClassPostings";
//		String setFeatures = batchBase + "MultiClassSets";
//		String selectedFeatures = batchBase + "SelectedFeatures";
//		String[] postingFileNames = new String[4];
//		for (int i = 0; i < 4; i++)
//			postingFileNames[i] = postingFileName + i;
//
//		FeatureProcessorDuralClass processor = new FeatureProcessorDuralClass();
//		GSpanMiner_MultiClass_Iterative patternGen = processor
//		.getPatternEnumerator(gDBFileName + "_merged", gDBFileName
//				+ "_class", MyFactory.getSmilesParser(), 0.01, -1, 10);
//
//		Input_DFSStream input = new Input_DFSStream(patternGen);
//		IFeatureSetConverter converter = new FeatureConverter_SubSearch(200, 200);
//		//Input_inMem input= Input_inMem.newInstance(setFeatures, converter);
//		ICoverStatusStream status = new Status_ShortMatrix(featureCount, converter);
////		IMaxCoverStatusStream status = 
////			new SetCoverStatus_FeatureWrapper2(featureCount, FeatureSetType.subSearch, 200, 200);
//		IBranchBoundCalculator cal = new BranchBoundCalculator(
//				status, FeatureSetType.subSearch, 200, 200);
//		
//		MaxCoverSolver_Stream greedy = new MaxCoverSolver_Stream(input, status, 1, cal, 0);
//		//MaxCoverSolver_InMem greedy = new MaxCoverSolver_InMem(input, status);
//		int[] result = greedy.runGreedy(featureCount);
//		
//		System.out.println("Total # of Covered Items: "
//				+ greedy.totalCoveredItems());
//	}
}
