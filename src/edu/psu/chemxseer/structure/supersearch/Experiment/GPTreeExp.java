package edu.psu.chemxseer.structure.supersearch.Experiment;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.iso.FastSU;
import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphDatabase_InMem;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphDatabase_OnDisk;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureProcessorFG;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.NoPostingFeatures;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.PostingFeatures;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureFactory.FeatureFactoryType;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphDatabase;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SubgraphSearch;
import edu.psu.chemxseer.structure.supersearch.CIndex.SupSearch_CIndexFlatBuilder;
import edu.psu.chemxseer.structure.supersearch.GPTree.FeatureMiner;
import edu.psu.chemxseer.structure.supersearch.GPTree.GPTreeTest;
import edu.psu.chemxseer.structure.supersearch.GPTree.SupSearch_GPTree;
import edu.psu.chemxseer.structure.supersearch.GPTree.SupSearch_GPTreeBuilder;
import edu.psu.chemxseer.structure.supersearch.Impl.PostingBuilderMem;
import edu.psu.chemxseer.structure.supersearch.LWTree.SupSearch_LWIndex;
import edu.psu.chemxseer.structure.supersearch.PrefIndex.SupSearch_PrefixIndexHiBuilder;
import edu.psu.chemxseer.structure.util.MemoryConsumptionCal;

/**
 * The Class for GPTree Experiment
 * @author dayuyuan
 *
 */
public class GPTreeExp {
	public static void buildIndex(GraphDatabase trainDB, GraphDatabase realDB, String base, double minSupt) throws ParseException, IOException{
		String baseName = base + "GPTree/";
		File temp = new File(baseName);
		if(!temp.exists())
			temp.mkdirs();
		
		//1. Mine Features:
		PostingFeatures freqFeatures = FeatureProcessorFG.frequentSubgraphMining(trainDB, baseName + "feature", 
				baseName + "posting", minSupt,10, trainDB.getParser());
		FeatureMiner miner = new FeatureMiner();
		//1.1 Mine Significant Features
		PostingFeatures sigFeatures = miner.minSignificantFeatures(freqFeatures, 1.25);
		//1.2 Prefix-Significant Features
		PostingFeatures preSigFeaturesRaw = FeatureProcessorFG.
			frequentSubgraphMining(new GraphDatabase_InMem(sigFeatures.getFeatures()), baseName + "preSigRw", baseName + "preSigRwPosting", minSupt, 10, MyFactory.getDFSCoder());
		NoPostingFeatures preSigFeatures = miner.minPrefixFeatures(preSigFeaturesRaw, sigFeatures.getFeatures().getfeatureNum());
		//1.3 Prefix Sharing Feature Mining
		NoPostingFeatures prefFeatures = miner.minPrefixFeatures(freqFeatures, trainDB.getTotalNum());
		//2. Build the Index
		SupSearch_GPTreeBuilder builder = new SupSearch_GPTreeBuilder();
		builder.buildIndex(sigFeatures.getFeatures(), preSigFeatures,prefFeatures , realDB, baseName, false);	
	}
	
	
	public static void runIndex(GraphDatabase gDB, GraphDatabase query, String baseName, boolean lucene_in_mem){
		// First Load the index
		SupSearch_GPTreeBuilder builder = new SupSearch_GPTreeBuilder();
		SubgraphSearch searcher = null;
		try {
			searcher  = builder.loadIndex(gDB, baseName + "GPTree/", lucene_in_mem);
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


	public static void memoryConsumption(GraphDatabase realDB,
			String base, boolean lucene_in_mem) throws NumberFormatException, IOException {
		SupSearch_GPTreeBuilder builder = new SupSearch_GPTreeBuilder();
		for(int i = 0; i< 3; i++){
			MemoryConsumptionCal.runGC();
			long start = MemoryConsumptionCal.usedMemory();
			SubgraphSearch searcher  = builder.loadIndex(realDB, base + "GPTree/", lucene_in_mem);
			MemoryConsumptionCal.runGC();
			long end = MemoryConsumptionCal.usedMemory();
			System.out.print((end-start));
			System.out.print(",");
			searcher = null;
		}
		System.out.println();
	}
	

	public static void pMemoryConsumption(GraphDatabase realDB,
			String base) throws NumberFormatException, IOException {
		SupSearch_GPTreeBuilder builder = new SupSearch_GPTreeBuilder();
		SubgraphSearch searcher = null;
		for(int i = 0; i< 3; i++){
			searcher  = builder.loadIndex(realDB, base + "GPTree/", false);
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
