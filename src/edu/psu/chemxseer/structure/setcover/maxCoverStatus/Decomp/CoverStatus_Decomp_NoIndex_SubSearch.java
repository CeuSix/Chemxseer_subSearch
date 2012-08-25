package edu.psu.chemxseer.structure.setcover.maxCoverStatus.Decomp;

import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;
import edu.psu.chemxseer.structure.util.IntersectionSet;

public class CoverStatus_Decomp_NoIndex_SubSearch extends CoverStatus_Decomp_NoIndex{
	
	
	public CoverStatus_Decomp_NoIndex_SubSearch(int K,
			int classOneNumber, int classTwoNumber) {
		super(K, classOneNumber, classTwoNumber);
	}
	
	/**
	 * 1. Find the Set of Features that aFeature is contained in
	 * 2. For each of the query, see what Other features are contained in it
	 * 3. Remove the nonDatabase graph from aFeature's nonDatabase graph
	 * @param aFeature
	 * @return
	 */
	@Override
	public int getGain(ICoverSet_FeatureWrapper aFeature){
		int[] queries = aFeature.containedQueryGraphs();
		int[] equalQueries = aFeature.getEquavalentQueryGraphs();
		
		int[][] otherQueries = new int[numOfSets][];
		int[] otherQueriesIndex = new int[numOfSets];
		int[][] otherEqualQueries = new int[numOfSets][];
		int[] otherEqualQueriesIndex = new int[numOfSets];
		
		for(int i = 0; i< otherQueries.length; i++){
			otherQueries[i] = selectedFeatures[i].containedQueryGraphs();
			otherEqualQueries[i] = selectedFeatures[i].getEquavalentQueryGraphs();
			otherQueriesIndex[i] = otherEqualQueriesIndex[i] = 0;
		}
		int gain = 0;
		
		for(int qIndex = 0, eqIndex =0; qIndex < queries.length || eqIndex< equalQueries.length; ){
			boolean chooseQ = true;
			if(eqIndex!=equalQueries.length){
				if(qIndex == queries.length)
					chooseQ = false; // only equeries left
				else if(queries[qIndex] > equalQueries[eqIndex])
					chooseQ = false; // equeries have priority
			}
			if(chooseQ){
				// For filtering
				// (1) First detect if there is any feature, equivalent to queries[qIndex]
				boolean foundEqual = false;
				for(int i =0; i< numOfSets; i++){
					for(; otherQueriesIndex[i] < otherEqualQueries[i].length; otherQueriesIndex[i]++){
						if(otherEqualQueries[i][ otherQueriesIndex[i] ] == queries[qIndex]){
							foundEqual = true;
							break;
						}
						else if(otherEqualQueries[i][otherQueriesIndex[i]] > queries[qIndex])
							break;
					}
					if(foundEqual)
						break;
				}
				// (2) Effective if and only if foundEqual == false
				// Find all the Features, covered by the same query
				if(foundEqual==false){
					IntersectionSet candidateGraphs = new IntersectionSet();
					boolean firstTime = true;
					
					for(int i = 0; i< numOfSets; i++){
						for(; otherQueriesIndex[i] < otherQueries[i].length; otherQueriesIndex[i]++){
							if(otherQueries[i][otherQueriesIndex[i]] == queries[qIndex]){
								if(firstTime){
									candidateGraphs.addAll(this.selectedFeatures[i].containedDatabaseGraphs());
									firstTime = false;
								}
								else 
									candidateGraphs.retainAll(this.selectedFeatures[i].containedDatabaseGraphs());
								break;
							}
							else if(otherQueries[i][otherQueriesIndex[i]] == queries[qIndex]){
								break;
							}
						}
					}
					if(firstTime)
						// no other feature processing the query
						gain += this.classOneNumber-aFeature.containedDatabaseGraphs().length;
					else{
						int tempSize = candidateGraphs.size();
						candidateGraphs.retainAll(aFeature.containedDatabaseGraphs());
						gain += tempSize-candidateGraphs.size();
					}
				}
			}
			else{
				// Answers can be directly retrieved. all database graphs filtered
				// Find all features contained in the query, find the candidate set: same as (2) 
				IntersectionSet candidateGraphs = new IntersectionSet();
				boolean firstTime = true;
				
				for(int i = 0; i< numOfSets; i++){
					for(; otherQueriesIndex[i] < otherQueries[i].length; otherQueriesIndex[i]++){
						if(otherQueries[i][otherQueriesIndex[i]] == queries[qIndex]){
							if(firstTime){
								candidateGraphs.addAll(this.selectedFeatures[i].containedDatabaseGraphs());
								firstTime = false;
							}
							else 
								candidateGraphs.retainAll(this.selectedFeatures[i].containedDatabaseGraphs());
							break;
						}
						else if(otherQueries[i][otherQueriesIndex[i]] == queries[qIndex]){
							break;
						}
					}
				}
				if(firstTime)
					gain+= this.classOneNumber;
				else gain = candidateGraphs.size();
			}
		}
		return gain;
	}
	
	@Override
	public int getCoveredCount(){
		int count = 0;
		int[][] containedQueries = new int[numOfSets][];
		int[] containedQueryIndex = new int[numOfSets];
		int[][] equalQueries = new int[numOfSets][];
		int[] equalQueryIndex = new int[numOfSets];
		
		for(int i = 0; i< numOfSets; i++){
			containedQueries[i] = this.selectedFeatures[i].containedQueryGraphs();
			containedQueryIndex[i] = 0;
			equalQueries[i] = this.selectedFeatures[i].getEquavalentQueryGraphs();
			equalQueryIndex[i] = 0;
		}
		for(int qID = 0; qID < this.classTwoNumber; qID++){
			//1 First decide whether there is an equivalent feature
			boolean foundEqual = false;
			for(int f = 0; f< numOfSets; f++){
				for(; equalQueryIndex[f] < equalQueries[f].length; equalQueryIndex[f]++){
					if(equalQueries[f][equalQueryIndex[f]] < qID)
						continue;
					else if(equalQueries[f][equalQueryIndex[f]] == qID){
						foundEqual = true;
						equalQueryIndex[f]++;
						break;
					}
					else break;
				}
				if(foundEqual)
					break;
			}
			if(foundEqual){
				count += this.classOneNumber;
				continue;
			}
			//2. Filtering count
			boolean firstTime = true;
			IntersectionSet candidateSet = new IntersectionSet();
			for(int f = 0; f < numOfSets; f++){
				for(; containedQueryIndex[f] < containedQueries[f].length; containedQueryIndex[f]++){
					if(containedQueries[f][containedQueryIndex[f]] < qID)
						continue;
					else if(containedQueries[f][containedQueryIndex[f]] == qID){
						if(firstTime){
							candidateSet.addAll(this.selectedFeatures[f].containedDatabaseGraphs());
							firstTime =false;
						}
						else candidateSet.retainAll(this.selectedFeatures[f].containedDatabaseGraphs());
						containedQueryIndex[f]++;
						break;
					}
					else break;
				}
			}
			if(!firstTime)// actually filtered
				count += this.classOneNumber-candidateSet.size();
		}
		return count;
	}

	@Override
	public int[][] addNewSetWithReturn(ICoverSet_FeatureWrapper nextSet) {
		//TODO: not implemented, leave for further implementation
		return null;
	}

}
