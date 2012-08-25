package edu.psu.chemxseer.structure.supersearch.Experiment;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;

import de.parmol.graph.Graph;
import de.parmol.parsers.GraphParser;
import edu.psu.chemxseer.structure.iso.FastSU;
import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphDatabase_InMem;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphDatabase_OnDisk;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureProcessorDuralClass;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.NoPostingFeatures;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.NoPostingFeatures_Ext;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.OneFeatureMultiClass;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.PostingFeaturesMultiClass;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphDatabase;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SubgraphSearch;
import edu.psu.chemxseer.structure.supersearch.CIndex.CIndexFeatureMiner;
import edu.psu.chemxseer.structure.supersearch.Impl.PostingBuilderMem;
import edu.psu.chemxseer.structure.supersearch.LWTree.LWIndexTest;
import edu.psu.chemxseer.structure.supersearch.LWTree.SupSearch_LWIndex;
import edu.psu.chemxseer.structure.supersearch.LWTree.SupSearch_LWIndexBuilder;
import edu.psu.chemxseer.structure.supersearch.LWTree.SetCover.MaxCover_FeatureSelector;
import edu.psu.chemxseer.structure.supersearch.PrefIndex.SupSearch_PrefixIndexBuilder;
import edu.psu.chemxseer.structure.util.MemoryConsumptionCal;

public class LWIndexExp {
	/**
	 * Train the indexing features with the trainingDB & trainingQuery
	 * Build the graph index with the realDB
	 * @param trainingDB
	 * @param traingingQuery
	 * @param realDB
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 * @throws ParseException
	 */
	public static void buildIndex(GraphDatabase trainingDB, 
			GraphDatabase trainQuery, GraphDatabase realDB, GraphParser gParser, String base, double minSupt, int[] featureCount) 
		throws CorruptIndexException, LockObtainFailedException, IOException, ParseException{
		//1. Mine Indexing Features
		File dir = new File(base + "LWIndex/");
		if(!dir.exists())
			dir.mkdirs();
		PostingFeaturesMultiClass frequentSubgraphs = 
			CIndexFeatureMiner.minFreqFeatures(trainingDB, trainQuery, gParser, base+"LWIndex/", minSupt);
		MaxCover_FeatureSelector selector = new MaxCover_FeatureSelector();
		NoPostingFeatures<OneFeatureMultiClass> selectedFeatures = selector.minePrefixFeatures(frequentSubgraphs, trainingDB);
		//2. Build the Index using the selectedFeatures
		SupSearch_LWIndexBuilder builder = new SupSearch_LWIndexBuilder();
		// build & save the index
		SupSearch_LWIndex index = builder.buildIndex(new NoPostingFeatures_Ext<OneFeatureMultiClass>(selectedFeatures), 
				frequentSubgraphs.getClassGraphsCount()[1], realDB, base + "LWIndex/", false);
		featureCount[0] = index.getFeatureCount();		
	}

	public static void runIndex(GraphDatabase gDB,
			GraphDatabase query, String base, boolean lucene_in_mem) {
		// First Load the index
		SupSearch_LWIndexBuilder builder = new SupSearch_LWIndexBuilder();
		SubgraphSearch searcher = null;
		try {
			searcher  = builder.loadIndex(gDB, base + "LWIndex/", lucene_in_mem);
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
	
	public static void memoryConsumption(GraphDatabase gDB, String base,boolean lucene_in_mem) throws IOException {
		SupSearch_LWIndexBuilder builder = new SupSearch_LWIndexBuilder();
		for(int i = 0; i< 3; i++){
			MemoryConsumptionCal.runGC();
			long start = MemoryConsumptionCal.usedMemory();
			SubgraphSearch searcher  = builder.loadIndex(gDB, base + "LWIndex/", lucene_in_mem);
			MemoryConsumptionCal.runGC();
			long end = MemoryConsumptionCal.usedMemory();
			System.out.print((end-start));
			System.out.print(",");
			searcher = null;
		}
		System.out.println();
	}
	
	public static void pMemoryConsumption(GraphDatabase gDB, String base) throws IOException {
		SupSearch_LWIndexBuilder builder = new SupSearch_LWIndexBuilder();
		SubgraphSearch searcher = null;
		for(int i = 0; i< 3; i++){
			searcher  = builder.loadIndex(gDB, base + "LWIndex/",false);
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
