package edu.psu.chemxseer.structure.supersearch.CIndex;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import de.parmol.graph.Graph;

import edu.psu.chemxseer.structure.iso.FastSU;
import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.setcover.IO.IInputSequential;
import edu.psu.chemxseer.structure.setcover.IO.Input_Mem;
import edu.psu.chemxseer.structure.setcover.featureGenerator.FeatureConverter_SupSearch;
import edu.psu.chemxseer.structure.setcover.impl.MaxCoverSolver_Sequential;
import edu.psu.chemxseer.structure.setcover.maxCoverStatus.ICoverStatus_Set;
import edu.psu.chemxseer.structure.setcover.maxCoverStatus.Status_BooleanMatrix;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphDatabase_InMem;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphDatabase_OnDisk;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.NoPostingFeatures;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.OneFeatureMultiClass;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureFactory.FeatureFactoryType;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.PostingFeaturesMultiClass;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphDatabase;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IOneFeature;
import edu.psu.chemxseer.structure.supersearch.Experiment.AIDSExp;
import edu.psu.chemxseer.structure.supersearch.LWTree.SupSearch_LWIndex;
import edu.psu.chemxseer.structure.supersearch.LWTree.SupSearch_LWIndexBuilder;

public class CIndexTest {
	private static String base = "/Users/dayuyuan/Documents/workspace/Experiment1/";
	private static String queryFile = "/Users/dayuyuan/Documents/workspace/Experiment1/DBFile";
	private static String dbFile = "/Users/dayuyuan/Documents/workspace/Experiment1/SupSearchDB";
	private static String mergedFile = "/Users/dayuyuan/Documents/workspace/Experiment1/mergedFile";
	private static String classFile = "/Users/dayuyuan/Documents/workspace/Experiment1/classFile";
	
	private static double minSupt = 0.1;
	public void buildIndexTopDown() throws IOException{
		String baseName = base + "TopDown/";
		File temp = new File(baseName);
		if(!temp.exists())
			temp.mkdirs();
		
		//1. Mine Frequent Features
		NoPostingFeatures<IOneFeature> dbFeatures = new NoPostingFeatures<IOneFeature>(dbFile, MyFactory.getFeatureFactory(FeatureFactoryType.OneFeature));
		GraphDatabase gDB = new GraphDatabase_InMem(dbFeatures);
		GraphDatabase gQuery = new GraphDatabase_OnDisk(queryFile, MyFactory.getSmilesParser());
		PostingFeaturesMultiClass firstFeatures = CIndexFeatureMiner.minFreqFeatures(gDB, gQuery, gQuery.getParser(), baseName,minSupt);
		//2. Construc the TopDown cIndex
		SupSearch_CIndexTopDownBuilder builder = new SupSearch_CIndexTopDownBuilder();
		//1. Feature Selection
		
		CIndexTreeFeatureSelector fSelector = new CIndexTreeFeatureSelector(firstFeatures, 10);
		CIndexTreeFeatureNode root = fSelector.constructFeatureTree();
		
		builder.buildCIndexTopDown(gDB, root, MyFactory.getSmilesParser(), baseName, false);
	}
	public void buildIndex() throws IOException{
		String baseName = base + "BottomUp/";
		File temp = new File(baseName);
		if(!temp.exists())
			temp.mkdirs();
		
		//1. Mine First Level Index Features
		NoPostingFeatures<IOneFeature> dbFeatures = new NoPostingFeatures<IOneFeature>(dbFile, MyFactory.getFeatureFactory(FeatureFactoryType.OneFeature));
		GraphDatabase gDB = new GraphDatabase_InMem(dbFeatures);
		GraphDatabase gQuery = new GraphDatabase_OnDisk(queryFile, MyFactory.getSmilesParser());
		PostingFeaturesMultiClass firstFeatures = CIndexFeatureMiner.mineFeatures(300, gDB, gQuery,MyFactory.getSmilesParser(), baseName + "1", minSupt);
		
		//2. Mine Second Level Index Features
		GraphDatabase gDB2 = new GraphDatabase_InMem(firstFeatures.getFeatures());
		PostingFeaturesMultiClass secondFeatures = CIndexFeatureMiner.mineFeatures(50, gDB2, gQuery,MyFactory.getSmilesParser(), baseName+"2", minSupt);
		PostingFeaturesMultiClass[] upperLevelFeatures = new PostingFeaturesMultiClass[1];
		upperLevelFeatures[0] = secondFeatures;
		
		//3. Build the Cindex_bottomup
		SupSearch_CIndexBottomUpBuilder builder = new SupSearch_CIndexBottomUpBuilder();
		builder.buildCIndexBottomUp(gDB, firstFeatures.getFeatures(), upperLevelFeatures, baseName, MyFactory.getSmilesParser(), false);
	}
	
	public void runQueriesTopDown() throws IOException, ParseException{
		//1. Load the Index
		GraphDatabase gDB = new GraphDatabase_InMem(new NoPostingFeatures(dbFile, 
				MyFactory.getFeatureFactory(FeatureFactoryType.OneFeature)));
		SupSearch_CIndexTopDownBuilder builder = new SupSearch_CIndexTopDownBuilder();
		SupSearch_CIndexTopDown index = builder.loadCIndexTopDown(gDB, base + "TopDown/",  MyFactory.getSmilesParser(), false);
		//2. Process Queries:
		GraphDatabase query = new GraphDatabase_OnDisk(queryFile, MyFactory.getSmilesParser());
		AIDSExp.runQueries(query, index);
//		FastSU iso = new FastSU();
//		for(int i = 0; i < query.getTotalNum(); i++){
//			Graph q = query.findGraph(i);
//			long[] TimeComponent = new long[4];
//			int[] Number = new int[2];
//			List<GraphResult> answers = index.getAnswer(q, TimeComponent, Number);
////			for(GraphResult result : answers)
////				System.out.println(result.getID());
//			int realAnswerCount = 0;
//			for(int w = 0; w < gDB.getTotalNum(); w++){
//				if(iso.isIsomorphic(gDB.findGraph(w), q)){
////					System.out.println(w);
//					realAnswerCount++;
//				}
//				
//			}
//			if(realAnswerCount!= answers.size()){
//				System.out.println("This is what I don't want to see");
//			}
//		}
		
	}
	public void runQueries() throws IOException, ParseException{
		//1. Load the Index 
		GraphDatabase gDB = new GraphDatabase_InMem(new NoPostingFeatures(dbFile, 
				MyFactory.getFeatureFactory(FeatureFactoryType.OneFeature)));
		SupSearch_CIndexBottomUpBuilder builder = new SupSearch_CIndexBottomUpBuilder();
		SupSearch_CIndexBottomUp index = builder.loadCIndexBottomUp(gDB, base+"BottomUp/", 2, MyFactory.getSmilesParser(), false);
		//2. Process Queries:
		GraphDatabase query = new GraphDatabase_OnDisk(queryFile, MyFactory.getSmilesParser());
		FastSU iso = new FastSU();
		for(int i = 0; i < query.getTotalNum(); i++){
			Graph q = query.findGraph(i);
			long[] TimeComponent = new long[4];
			int[] Number = new int[2];
			List<GraphResult> answers = index.getAnswer(q, TimeComponent, Number);
//			for(GraphResult result : answers)
//				System.out.println(result.getID());
//			System.out.println(answerCount);
			int realAnswerCount = 0;
			for(int w = 0; w < gDB.getTotalNum(); w++){
				if(iso.isIsomorphic(gDB.findGraph(w), q)){
//					System.out.println(w);
					realAnswerCount++;
				}
				
			}
			if(realAnswerCount!= answers.size()){
				System.out.println("This is what I don't want to see");
			}
		}
	}
	
	public static void main(String[] args) throws IOException, ParseException{
		CIndexTest test = new CIndexTest();
		test.buildIndexTopDown();
		test.runQueriesTopDown();
	}
}
