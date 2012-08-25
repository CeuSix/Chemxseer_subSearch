package edu.psu.chemxseer.structure.setcover.maxCoverStatus.Decomp;

import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;
import edu.psu.chemxseer.structure.util.IntersectionSet;

public class CoverStatus_Decomp_NoIndex_SupSearch extends CoverStatus_Decomp_NoIndex{
	
	
	public CoverStatus_Decomp_NoIndex_SupSearch(int K,
			int classOneNumber, int classTwoNumber) {
		super(K, classOneNumber, classTwoNumber);
		// TODO Auto-generated constructor stub
	}

	/**
	 * For a query "q", current feature can filter "_"
	 * if a is not containing aFeature, then "aFeature"
	 * can help to filter more
	 * @param aFeature
	 * @return
	 */
	@Override
	public int getGain(ICoverSet_FeatureWrapper aFeature){
		int[] containedQueries = aFeature.containedQueryGraphs();
		int containedQueryIndex = 0;
		int[][] otherContainedQueries = new int[numOfSets][];
		int[] otherContainedQueriesIndex = new int[numOfSets];
		for(int i = 0; i< otherContainedQueries.length; i++){
			otherContainedQueriesIndex[i] = 0;
			otherContainedQueries[i] = this.selectedFeatures[i].containedQueryGraphs();
		}
		int gain = 0;
		
		for(int qID = 0; qID < this.classTwoNumber;  qID++){
			if(containedQueryIndex < containedQueries.length && qID == containedQueries[containedQueryIndex]){
				// aFeature will not help to filter qID
				containedQueryIndex++;
				continue;
			}
			// ELSE: Can help to do filtering
			IntersectionSet filteredGraphs = new IntersectionSet();
			filteredGraphs.addAll(aFeature.containedDatabaseGraphs());
			for(int i = 0; i< numOfSets; i++){
				for(; otherContainedQueriesIndex[i] < otherContainedQueries[i].length; otherContainedQueriesIndex[i]++){
					if(otherContainedQueries[i][otherContainedQueriesIndex[i]] < qID)
						continue; // increase the otherCountainedQueriesIndex[i]
					else if(otherContainedQueries[i][otherContainedQueriesIndex[i]] == qID){
						otherContainedQueriesIndex[i]++;
						break; // feature "i" can not be used to filter query qID
					}
					else{
						// feature "i" can be used to filter query qID
						filteredGraphs.removeAll(this.selectedFeatures[i].containedDatabaseGraphs());
						break;
					}
				}
			}
			gain += aFeature.containedDatabaseGraphs().length-filteredGraphs.size();
		}
		return gain;
	}
	
	@Override
	public int getCoveredCount(){
		int[][] containedQueries = new int[numOfSets][];
		int[] containedQueriesIndex = new int[numOfSets];
		for(int i = 0; i< containedQueries.length; i++){
			containedQueriesIndex[i] = 0;
			containedQueries[i] = this.selectedFeatures[i].containedQueryGraphs();
		}
		int count = 0;
		
		for(int qID = 0; qID < this.classTwoNumber;  qID++){
			// find all the features not containing this query
			IntersectionSet filteredGraphs = new IntersectionSet();
			for(int f = 0; f<this.numOfSets; f++){
				for(;containedQueriesIndex[f] < containedQueries.length; containedQueriesIndex[f]++){
					if(containedQueries[f][containedQueriesIndex[f]] < qID)
						continue;
					else if(containedQueries[f][containedQueriesIndex[f]] == qID)
						break;
					else{
						// f can be used to filter qID
						filteredGraphs.addAll(this.selectedFeatures[f].containedDatabaseGraphs());
					}
				}
			}
			count += filteredGraphs.size();
		}
		return count;
	}
	
	@Override
	public int[][] addNewSetWithReturn(ICoverSet_FeatureWrapper nextSet) {
		//TODO: not implemented, leave for further implementation
		return null;
	}

}
