package edu.psu.chemxseer.structure.supersearch.Experiment;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import de.parmol.parsers.GraphParser;

import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphDatabase_InMem;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphDatabase_OnDisk;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureFactory;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.NoPostingFeatures;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.OneFeatureMultiClass;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.PostingFeaturesMultiClass;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureFactory.FeatureFactoryType;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphDatabase;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IOneFeature;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SubgraphSearch;
import edu.psu.chemxseer.structure.supersearch.CIndex.CIndexFeatureMiner;
import edu.psu.chemxseer.structure.supersearch.CIndex.CIndexTree;
import edu.psu.chemxseer.structure.supersearch.CIndex.CIndexTreeConstructor;
import edu.psu.chemxseer.structure.supersearch.CIndex.CIndexTreeFeatureNode;
import edu.psu.chemxseer.structure.supersearch.CIndex.CIndexTreeFeatureSelector;
import edu.psu.chemxseer.structure.supersearch.CIndex.SupSearch_CIndexBottomUpBuilder;
import edu.psu.chemxseer.structure.supersearch.CIndex.SupSearch_CIndexFlatBuilder;
import edu.psu.chemxseer.structure.supersearch.CIndex.SupSearch_CIndexTopDown;
import edu.psu.chemxseer.structure.supersearch.CIndex.SupSearch_CIndexTopDownBuilder;
import edu.psu.chemxseer.structure.supersearch.GPTree.SupSearch_GPTreeBuilder;
import edu.psu.chemxseer.structure.supersearch.Impl.PostingBuilderMem;
import edu.psu.chemxseer.structure.supersearch.PrefIndex.SupSearch_PrefixIndexBuilder;
import edu.psu.chemxseer.structure.util.MemoryConsumptionCal;

/**
 * The class related CIndex experiments: both single CIndex & Hierachical CIndex
 * @author dayuyuan
 *
 */
public class CIndexExp {
	public static void buildIndexTopDown(GraphDatabase trainDB, GraphDatabase trainQuery, 
			GraphDatabase realDB,   GraphParser gParser, String base, double minSupt, int minQuerySplit) throws IOException{
		String baseName = base + "CIndexTopDown/";
		File temp = new File(baseName);
		if(!temp.exists())
			temp.mkdirs();
		
		//1. Mine Frequent Features
		PostingFeaturesMultiClass firstFeatures = 
			CIndexFeatureMiner.minFreqFeatures(trainDB, trainQuery, gParser, baseName, minSupt);
		SupSearch_CIndexTopDownBuilder builder = new SupSearch_CIndexTopDownBuilder();
		System.out.println("Select Index Patterns for CIndexTopDown");
		CIndexTreeFeatureSelector fSelector = new CIndexTreeFeatureSelector(firstFeatures, minQuerySplit);
		CIndexTreeFeatureNode root = fSelector.constructFeatureTree();
		//2. Construct the TopDown cIndex
		builder.buildCIndexTopDown(realDB, root, MyFactory.getSmilesParser(), baseName, false);
	}
	
	public static void buildIndexTopDownLuceneOnly(GraphDatabase realDB, String base, double minSupt) throws IOException {
		String baseName = base + "CIndexTopDown/";
		File temp = new File(baseName);
		if(!temp.exists())
			temp.mkdirs();
		
		//1. Mine Frequent Features
		CIndexTree searcher = CIndexTreeConstructor.loadSearcher(baseName, SupSearch_CIndexTopDown.getIndexName());
		//2. Build Lucene Index
		SupSearch_CIndexTopDownBuilder builder = new SupSearch_CIndexTopDownBuilder();
		builder.buildCIndexTopDown(realDB, searcher, MyFactory.getSmilesParser(), baseName, false);
	}
	
	public static void runIndexTopDown(GraphDatabase gDB, GraphDatabase query, String baseName, boolean lucene_in_mem){
		// First Load the index
		SupSearch_CIndexTopDownBuilder builder = new SupSearch_CIndexTopDownBuilder();
		SubgraphSearch searcher = null;
		try {
			searcher  = builder.loadCIndexTopDown(gDB, baseName + "CIndexTopDown/",  MyFactory.getSmilesParser(), lucene_in_mem);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(searcher!=null){
			try {
				AIDSExp.runQueries(query, searcher);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void memoryConsumptionTopDown(GraphDatabase realDB,
			String base, boolean lucene_in_mem) throws NumberFormatException, IOException {
		SupSearch_CIndexTopDownBuilder builder = new SupSearch_CIndexTopDownBuilder();
		for(int i = 0; i< 3; i++){
			MemoryConsumptionCal.runGC();
			long start = MemoryConsumptionCal.usedMemory();
			SubgraphSearch searcher  = builder.loadCIndexTopDown(realDB, base + "CIndexTopDown/",  MyFactory.getSmilesParser(), lucene_in_mem);
			MemoryConsumptionCal.runGC();
			long end = MemoryConsumptionCal.usedMemory();
			System.out.print((end-start));
			System.out.print(",");
			searcher = null;
		}
		System.out.println();
	}
	
	public static void pMemoryConsumptionTopDown(GraphDatabase realDB,
			String base) throws NumberFormatException, IOException {
		SupSearch_CIndexTopDownBuilder builder = new SupSearch_CIndexTopDownBuilder();
		SubgraphSearch searcher = null;
		for(int i = 0; i< 3; i++){
			searcher  = builder.loadCIndexTopDown(realDB, base + "CIndexTopDown/",  MyFactory.getSmilesParser(), false);
			MemoryConsumptionCal.runGC();
			long start = MemoryConsumptionCal.usedMemory();
			PostingBuilderMem InMemPosting = searcher.getInMemPosting();
			MemoryConsumptionCal.runGC();
			long end = MemoryConsumptionCal.usedMemory();
			System.out.print((end-start));
			System.out.print(",");
			InMemPosting  =null;
		}
		System.out.println();
	}
	
	/**
	 * Build A Bottom Up CIndex
	 * TODO: Need to specify the total number of indexing features for each level
	 * Here I study the "2"-level index
	 * @throws IOException
	 */
	public static void buildIndexBottomUp(GraphDatabase trainDB,
			GraphDatabase trainQuery,  GraphParser gParser, GraphDatabase realDB, String base, double minSupt, int baseFeatureCount, int uperFeatureCount) throws IOException{
		String baseName = base + "CIndexBottomUp/";
		File temp = new File(baseName);
		if(!temp.exists())
			temp.mkdirs();
		
		//1. Mine First Level Index Features
		System.out.println("For Leve 0 of cIndex_BottomUp");
		PostingFeaturesMultiClass firstFeatures = CIndexFeatureMiner.mineFeatures(baseFeatureCount, trainDB, trainQuery,gParser,  baseName + "1", minSupt);
		
		//2. Mine Second Level Index Features
		GraphDatabase gDB2 = new GraphDatabase_InMem(firstFeatures.getFeatures());
		System.out.println("For Leve 1 of cIndex_BottomUp");
		PostingFeaturesMultiClass secondFeatures = CIndexFeatureMiner.mineFeatures(uperFeatureCount, gDB2, trainQuery, gParser, baseName+"2", minSupt);
		PostingFeaturesMultiClass[] upperLevelFeatures = new PostingFeaturesMultiClass[1];
		upperLevelFeatures[0] = secondFeatures;
		
		//3. Build the Cindex_bottomup
		SupSearch_CIndexBottomUpBuilder builder = new SupSearch_CIndexBottomUpBuilder();
		builder.buildCIndexBottomUp(realDB, firstFeatures.getFeatures(), upperLevelFeatures, baseName, MyFactory.getSmilesParser(), false);
	}
	
	public static void runIndexBottomUp(GraphDatabase gDB, GraphDatabase query, String baseName, boolean lucene_in_mem){
		// First Load the index
		SupSearch_CIndexBottomUpBuilder builder = new SupSearch_CIndexBottomUpBuilder();
		SubgraphSearch searcher = null;
		try {
			searcher  = builder.loadCIndexBottomUp(gDB, baseName+"CIndexBottomUp/", 2, MyFactory.getSmilesParser(), lucene_in_mem);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(searcher!=null){
			try {
				AIDSExp.runQueries(query, searcher);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Build A Flat CIndex
	 * TODO: Need to specify the total number features selected
	 * @param trainDB
	 * @param trainQuery
	 * @param realDB
	 * @throws IOException
	 */
	public static void buildIndex(GraphDatabase trainDB, GraphDatabase trainQuery,GraphDatabase realDB,  GraphParser gParser,  String base, 
			double minSupt, int patternCount) throws IOException{
		String baseName = base + "CIndex/";
		File temp = new File(baseName);
		if(!temp.exists())
			temp.mkdirs();
		//1. Mine Indexing Features
		PostingFeaturesMultiClass indexingFeatures = CIndexFeatureMiner.mineFeatures(patternCount, trainDB
				, trainQuery,gParser, baseName + "1", minSupt);
		//3. Build the Cindex_bottomup
		SupSearch_CIndexFlatBuilder builder = new SupSearch_CIndexFlatBuilder();
		builder.buildCIndexFlat(realDB, indexingFeatures.getFeatures(), baseName, MyFactory.getSmilesParser(), false);
	}
	
	public static void buildIndex(String selectedFeatureBaseName, GraphDatabase realDB, String base, 
			double minSupt) throws IOException{
		String baseName = base + "CIndex/";
		NoPostingFeatures selectedFeatures = new NoPostingFeatures<OneFeatureMultiClass>(selectedFeatureBaseName + "CIndex/1featuresel" ,
				MyFactory.getFeatureFactory(FeatureFactoryType.MultiFeature));
		
		SupSearch_CIndexFlatBuilder builder = new SupSearch_CIndexFlatBuilder();
		builder.buildCIndexFlat(realDB, selectedFeatures, baseName, MyFactory.getSmilesParser(), false);
	}
	
	public static void runIndex(GraphDatabase gDB, GraphDatabase query, String baseName, boolean lucene_in_mem){
		// First Load the index
		SupSearch_CIndexFlatBuilder builder = new SupSearch_CIndexFlatBuilder();
		SubgraphSearch searcher = null;
		try {
			searcher  = builder.loadCIndexFlat(gDB, baseName + "CIndex/",  MyFactory.getSmilesParser(), lucene_in_mem);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(searcher!=null){
			try {
				AIDSExp.runQueries(query, searcher);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public static void memoryConsumptionFlat(GraphDatabase realDB,
			String base, boolean lucene_in_mem) throws NumberFormatException, IOException {
		SupSearch_CIndexFlatBuilder builder = new SupSearch_CIndexFlatBuilder();
		for(int i = 0; i< 3; i++){
			MemoryConsumptionCal.runGC();
			long start = MemoryConsumptionCal.usedMemory();
			SubgraphSearch searcher  = builder.loadCIndexFlat(realDB, base + "CIndex/",  MyFactory.getSmilesParser(), lucene_in_mem);
			MemoryConsumptionCal.runGC();
			long end = MemoryConsumptionCal.usedMemory();
			System.out.print((end-start));
			System.out.print(",");
			searcher = null;
		}
		System.out.println();
	}
	
	public static void pMemoryConsumptionFlat(GraphDatabase realDB,
			String base) throws NumberFormatException, IOException {
		SupSearch_CIndexFlatBuilder builder = new SupSearch_CIndexFlatBuilder();
		SubgraphSearch searcher = null;
		for(int i = 0; i< 3; i++){
			searcher  = builder.loadCIndexFlat(realDB, base + "CIndex/",  MyFactory.getSmilesParser(), false);
			MemoryConsumptionCal.runGC();
			long start = MemoryConsumptionCal.usedMemory();
			PostingBuilderMem InMemPosting = searcher.getInMemPosting();
			MemoryConsumptionCal.runGC();
			long end = MemoryConsumptionCal.usedMemory();
			System.out.print((end-start));
			System.out.print(",");
			InMemPosting = null;
		}
		System.out.println();
	}

}
