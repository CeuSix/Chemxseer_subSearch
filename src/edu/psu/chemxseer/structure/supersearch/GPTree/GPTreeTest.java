package edu.psu.chemxseer.structure.supersearch.GPTree;

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
import edu.psu.chemxseer.structure.supersearch.Experiment.AIDSExp;

/**
 * The Testing Class for the GPTree
 * @author dayuyuan
 *
 */
public class GPTreeTest {
	
	private static String base = "/Users/dayuyuan/Documents/workspace/Experiment1/";
	private static String queryFile = "/Users/dayuyuan/Documents/workspace/Experiment1/DBFile";
	private static String dbFile = "/Users/dayuyuan/Documents/workspace/Experiment1/SupSearchDB";
	
	public void buildIndex() throws ParseException, IOException{
		String baseName = base + "GPTree/";
		File temp = new File(baseName);
		if(!temp.exists())
			temp.mkdirs();
		
		NoPostingFeatures dbFeatures = new NoPostingFeatures(dbFile, MyFactory.getFeatureFactory(FeatureFactoryType.OneFeature));
		GraphDatabase gDB = new GraphDatabase_InMem(dbFeatures);
		//1. Mine Features:
		PostingFeatures freqFeatures = FeatureProcessorFG.frequentSubgraphMining(gDB, baseName + "feature",
				baseName + "posting", 0.01, 10, gDB.getParser());
		FeatureMiner miner = new FeatureMiner();
		PostingFeatures sigFeatures = miner.minSignificantFeatures(freqFeatures, 1.25);
		PostingFeatures preSigFeaturesRaw = FeatureProcessorFG.
			frequentSubgraphMining(new GraphDatabase_InMem(sigFeatures.getFeatures()), baseName + "preSigRw", baseName + "preSigRwPosting", 0.01, 10, MyFactory.getDFSCoder());
		NoPostingFeatures preSigFeatures = miner.minPrefixFeatures(preSigFeaturesRaw, sigFeatures.getFeatures().getfeatureNum());
		NoPostingFeatures prefFeatures = miner.minPrefixFeatures(freqFeatures, gDB.getTotalNum());
		//2. Build the Index
		SupSearch_GPTreeBuilder builder = new SupSearch_GPTreeBuilder();
		builder.buildIndex(sigFeatures.getFeatures(), preSigFeatures,prefFeatures , gDB, baseName, false);
		
	}
	public void runQueries() throws NumberFormatException, IOException, ParseException{
		//1. First Step: load the index
		String baseName = base + "GPTree/";
		NoPostingFeatures dbFeatures = new NoPostingFeatures(dbFile, MyFactory.getFeatureFactory(FeatureFactoryType.OneFeature));
		GraphDatabase gDB = new GraphDatabase_InMem(dbFeatures);
		SupSearch_GPTreeBuilder builder = new SupSearch_GPTreeBuilder();
		SupSearch_GPTree index = builder.loadIndex(gDB, baseName, false);
		//2. Run Queries
		GraphDatabase query = new GraphDatabase_OnDisk(queryFile, MyFactory.getSmilesParser());
		AIDSExp.runQueries(query, index);
//		FastSU iso = new FastSU();
//		for(int i = 0; i < query.getTotalNum(); i++){
//			Graph q = query.findGraph(i);
//			long[] TimeComponent = new long[4];
//			int[] Number = new int[2];
//			List<GraphResult> answers = index.getAnswer(q, TimeComponent, Number);
//////			for(GraphResult result : answers)
//////				System.out.println(result.getID());
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
	
	public static void main(String[] args) throws ParseException, NumberFormatException, IOException{
		GPTreeTest test = new GPTreeTest();
		test.buildIndex();
		test.runQueries();
	}
}
