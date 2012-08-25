package edu.psu.chemxseer.structure.supersearch.LWTree;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;

import de.parmol.graph.Graph;

import edu.psu.chemxseer.structure.iso.FastSU;
import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphDatabase_InMem;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphDatabase_OnDisk;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureFactory.FeatureFactoryType;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureProcessorDuralClass;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureProcessorFG;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.NoPostingFeatures;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.NoPostingFeatures_Ext;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.OneFeatureMultiClass;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.PostingFeatures;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.PostingFeaturesMultiClass;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphDatabase;
import edu.psu.chemxseer.structure.supersearch.CIndex.CIndexFeatureMiner;
import edu.psu.chemxseer.structure.supersearch.LWTree.SetCover.MaxCover_FeatureSelector;

public class LWIndexTest {
	private static String base = "/Users/dayuyuan/Documents/workspace/Experiment1/";
	private static String queryFile = "/Users/dayuyuan/Documents/workspace/Experiment1/DBFile";
	private static String dbFile = "/Users/dayuyuan/Documents/workspace/Experiment1/SupSearchDB";
	private static String mergedFile = "/Users/dayuyuan/Documents/workspace/Experiment1/mergedFile";
	private static String classFile = "/Users/dayuyuan/Documents/workspace/Experiment1/classFile";
	
	public PostingFeaturesMultiClass preProcess() throws IOException{
		//1.First step: mine the queryFile to generate the graph database
		PostingFeatures freqFeatures = FeatureProcessorFG.frequentSubgraphMining(queryFile, dbFile, null, 0.05, 50, MyFactory.getSmilesParser());
		//2. Second step: mine the raw features (dual class) from both query database file & query file
		FeatureProcessorDuralClass processor = new FeatureProcessorDuralClass();
		GraphDatabase queryDB = new GraphDatabase_OnDisk(queryFile, MyFactory.getSmilesParser());
		GraphDatabase gDB = new GraphDatabase_InMem(freqFeatures.getFeatures());
		processor.mergeGraphFile(gDB, queryDB, MyFactory.getSmilesParser(), mergedFile, classFile);
		//3. Mine the frequent features
		String featureFile = base + "featureFile";
		String[] postingFiles = new String[4];
		for(int i = 0; i< 4; i++)
			postingFiles[i] = base + "postingFile" + i;
		return processor.frequentSubgraphMining(mergedFile, classFile, 
				featureFile, postingFiles, MyFactory.getSmilesParser(), 
				0.01, -1, 10);	
	}
	
	public void buildIndex(PostingFeaturesMultiClass frequentSubgraphs) throws CorruptIndexException, LockObtainFailedException, IOException, ParseException{
		//1. Mine the Indexing Features: how many, until all covered
		MaxCover_FeatureSelector selector = new MaxCover_FeatureSelector();
		GraphDatabase gDB = new GraphDatabase_InMem(new NoPostingFeatures(dbFile, 
				MyFactory.getFeatureFactory(FeatureFactoryType.OneFeature)));
		NoPostingFeatures<OneFeatureMultiClass> selectedFeatures = selector.minePrefixFeatures(frequentSubgraphs, gDB);
		//2. Build the Index using the selectedFeatures
		File dir = new File(base + "LWIndex/");
		if(!dir.exists())
			dir.mkdirs();
		
		SupSearch_LWIndexBuilder builder = new SupSearch_LWIndexBuilder();
		// build & save the index
		builder.buildIndex(new NoPostingFeatures_Ext<OneFeatureMultiClass>(selectedFeatures), 
				frequentSubgraphs.getClassGraphsCount()[1], gDB, base + "LWIndex/", false);
		
	}
	
	public void runQueries() throws IOException, ParseException{
		//1. Load the Index 
		GraphDatabase gDB = new GraphDatabase_InMem(new NoPostingFeatures(dbFile, 
				MyFactory.getFeatureFactory(FeatureFactoryType.OneFeature)));
		SupSearch_LWIndexBuilder builder = new SupSearch_LWIndexBuilder();
		SupSearch_LWIndex index = builder.loadIndex(gDB, base+ "LWIndex/", false);
		//2. Process Queries:
		GraphDatabase query = new GraphDatabase_OnDisk(queryFile, MyFactory.getSmilesParser());
		FastSU iso = new FastSU();
		int realAnswerCount = 0;
		for(int i = 0; i < query.getTotalNum(); i++){
			Graph q = query.findGraph(i);
//			long[] TimeComponent = new long[4];
//			int[] Number = new int[2];
//			int answerCount = index.getAnswer(q, TimeComponent, Number).size();
			for(int w = 0; w < gDB.getTotalNum(); w++){
				if(iso.isIsomorphic(gDB.findGraph(w), q))
					realAnswerCount++;
			}
//			if(realAnswerCount!= answerCount){
//				System.out.println("This is what I don't want to see");
//			}
		}
		System.out.println(realAnswerCount);
	}
	
	public static void main(String[] args) throws IOException, ParseException{
		LWIndexTest test = new  LWIndexTest();
		GraphDatabase trainingDB = new GraphDatabase_InMem(new NoPostingFeatures(dbFile, MyFactory.getFeatureFactory(FeatureFactoryType.OneFeature)));
		GraphDatabase trainQuery = new GraphDatabase_OnDisk(queryFile, MyFactory.getSmilesParser());
		PostingFeaturesMultiClass frequentSubgraphs = CIndexFeatureMiner.minFreqFeatures(trainingDB, trainQuery, trainQuery.getParser(), base+"LWIndex/", 0.1);
		test.buildIndex(frequentSubgraphs);
//		test.runQueries();
	}
}
