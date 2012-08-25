package edu.psu.chemxseer.structure.supersearch.LWTree;

import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphDatabase;
import edu.psu.chemxseer.structure.supersearch.LWTree.SetCover.CoverSet_Weighted;

/**
 * Given all the mined frequent subgraph features
 * For each feature, construct its representative set
 * @author dayuyuan
 *
 */
public class LWIndexSetConverter {
	/**
	 * Given the input features, construc the representative set of that feature: 
	 * The set is constructed according to the LWindex score function
	 * @param oneFeature 
	 * @param gDB: the graph database, because we need to know the size of each database graph
	 * @return
	 */
	public static CoverSet_Weighted featureToSet_Weight(
			ICoverSet_FeatureWrapper oneFeature, GraphDatabase gDB, int querySize){
		
		int[] items = oneFeature.containedDatabaseGraphs();
		CoverSet_Weighted set = new CoverSet_Weighted(items);
		int containQSize = oneFeature.getFeature().getAllFrequency()[2];
		for(int i = 0; i< items.length; i++){
			int gID = items[i];
			// for each of the database graph, calculate the gain function f(p, gID)
			float score = (float)(containQSize*oneFeature.getFeature().getFeatureGraph().getEdgeCount()
					+ (querySize-containQSize) * gDB.findGraph(gID).getEdgeCount())/(float)querySize;
			set.assignScore(i, score);
		}
		return set;
	}
}
