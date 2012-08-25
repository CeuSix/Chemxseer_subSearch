package edu.psu.chemxseer.structure.supersearch.Experiment;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;

import de.parmol.graph.Graph;

import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphDatabase_InMem;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureProcessorFG;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.NoPostingFeatures;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.PostingFeatures;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureFactory.FeatureFactoryType;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphDatabase;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SubgraphSearch;
import edu.psu.chemxseer.structure.supersearch.GPTree.SupSearch_GPTree;
import edu.psu.chemxseer.structure.supersearch.Impl.PostingBuilderMem;
import edu.psu.chemxseer.structure.supersearch.LWTree.SupSearch_LWIndexBuilder;
import edu.psu.chemxseer.structure.supersearch.PrefIndex.FeatureMiner;
import edu.psu.chemxseer.structure.supersearch.PrefIndex.SupSearch_PrefixIndex;
import edu.psu.chemxseer.structure.supersearch.PrefIndex.SupSearch_PrefixIndexBuilder;
import edu.psu.chemxseer.structure.supersearch.PrefIndex.SupSearch_PrefixIndexHiBuilder;
import edu.psu.chemxseer.structure.util.MemoryConsumptionCal;

/**
 * This is the class related to the PrefixIndex
 * @author dayuyuan
 *
 */
public class PrefixIndexExp {
	
	/**
	 * Build the PrefixIndex
	 * @param trainDB
	 * @param realDB
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 */
	public static void buildIndex(GraphDatabase trainDB, GraphDatabase realDB, String base, double minSupt) throws CorruptIndexException, LockObtainFailedException, IOException{
		String baseName = base + "PrefIndex/";
		File temp = new File(baseName);
		if(!temp.exists())
			temp.mkdirs();
		//1. Mine Frequent Subgraphs:
		PostingFeatures freqFeatures = FeatureProcessorFG.frequentSubgraphMining(trainDB, 
				baseName + "feature", baseName + "posting", 
				minSupt, 10, trainDB.getParser()) ;
		//2. Mine Prefix Features
		FeatureMiner miner = new FeatureMiner();
		NoPostingFeatures selectedFeatures = miner.minPrefixFeatures(freqFeatures, trainDB.getTotalNum()).getFeatures();
		//3. Build the Index
		SupSearch_PrefixIndexBuilder builder = new SupSearch_PrefixIndexBuilder();
		builder.buildIndex(selectedFeatures, realDB, baseName, false);
	}
	
	
	/**
	 * Build a Hierachical PrefixIndex
	 * @param trainDB
	 * @param realDB
	 * @throws IOException
	 */
	public static void buildHiIndex(GraphDatabase trainDB, GraphDatabase realDB, int level, String base, double minSupt) throws IOException{
		String baseName = base + "PrefIndexHi/";
		File temp = new File(baseName);
		if(!temp.exists())
			temp.mkdirs();
		//1. Mine Index Features: multi level indexing features: this involve multiple times frequent subgrph mining
		// and also feature selection. 
		PostingFeatures[] selectedFeatures = new PostingFeatures[level];
		FeatureMiner miner = new FeatureMiner();
		for(int i = 0; i< level; i++){
			PostingFeatures freqFeatures = FeatureProcessorFG.frequentSubgraphMining(trainDB, baseName + "feature" +i, 
					baseName + "posting" + i, minSupt, 10, trainDB.getParser()) ;
			System.out.println("Level: " + i);
			selectedFeatures[i] = miner.minPrefixFeatures(freqFeatures, trainDB.getTotalNum());
			trainDB = new GraphDatabase_InMem(selectedFeatures[i].getFeatures());
		}
		
		//2. Build the Hierarchy index
		SupSearch_PrefixIndexHiBuilder builder = new SupSearch_PrefixIndexHiBuilder();
		builder.buildIndex(selectedFeatures, baseName, realDB, false);
	}
	
	public static void runIndex(GraphDatabase gDB,
			GraphDatabase fakeQuery, String base, boolean lucene_in_mem) {
		// First Load the index
		SupSearch_PrefixIndexBuilder builder = new SupSearch_PrefixIndexBuilder();
		SubgraphSearch searcher = null;
		try {
			searcher  = builder.loadIndex(gDB, base + "PrefIndex/", lucene_in_mem);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(searcher!=null){
			try {
				AIDSExp.runQueries(fakeQuery, searcher);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	
	public static void runHiIndex(GraphDatabase gDB,
			GraphDatabase fakeQuery, String base, boolean lucene_in_mem) {
		// First Load the index
		SupSearch_PrefixIndexHiBuilder builder = new SupSearch_PrefixIndexHiBuilder();
		SubgraphSearch searcher = null;
		try {
			searcher  = builder.loadIndex(base + "PrefIndexHi/", 2, gDB, lucene_in_mem);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(searcher!=null){
			try {
				AIDSExp.runQueries(fakeQuery, searcher);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}


	public static void memoryConsumptionHi(GraphDatabase realDB,
			String base, boolean lucene_in_mem) throws NumberFormatException, IOException {
		SupSearch_PrefixIndexHiBuilder builder = new SupSearch_PrefixIndexHiBuilder();
		for(int i = 0; i< 3; i++){
			MemoryConsumptionCal.runGC();
			long start = MemoryConsumptionCal.usedMemory();
			SubgraphSearch searcher  = builder.loadIndex(base + "PrefIndexHi/", 2, realDB, lucene_in_mem);
			MemoryConsumptionCal.runGC();
			long end = MemoryConsumptionCal.usedMemory();
			System.out.print((end-start));
			System.out.print(",");
			searcher = null;
		}
		System.out.println();
	}
	
	public static void pMemoryConsumptionHi(GraphDatabase realDB,
			String base) throws NumberFormatException, IOException {
		SupSearch_PrefixIndexHiBuilder builder = new SupSearch_PrefixIndexHiBuilder();
		SubgraphSearch searcher = null;
		for(int i = 0; i< 3; i++){
			searcher  = builder.loadIndex(base + "PrefIndexHi/", 2, realDB, false);
			MemoryConsumptionCal.runGC();
			long start = MemoryConsumptionCal.usedMemory();
			PostingBuilderMem temp = searcher.getInMemPosting();
			MemoryConsumptionCal.runGC();
			long end = MemoryConsumptionCal.usedMemory();
			System.out.print((end-start));
			System.out.print(",");
			temp = null;
		}
		System.out.println();
	}
}

