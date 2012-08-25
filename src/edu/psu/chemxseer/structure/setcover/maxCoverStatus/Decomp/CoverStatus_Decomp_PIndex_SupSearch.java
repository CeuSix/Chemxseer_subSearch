package edu.psu.chemxseer.structure.setcover.maxCoverStatus.Decomp;

import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter.FeatureSetType;
import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;
import edu.psu.chemxseer.structure.util.IntersectionSet;

public class CoverStatus_Decomp_PIndex_SupSearch extends CoverStatus_Decomp_PIndex{

	public CoverStatus_Decomp_PIndex_SupSearch(int K, int classOneNumber,
			int classTwoNumber) {
		super(K, classOneNumber, classTwoNumber);
		
		this.qfIndex = new short[classOneNumber][];
		this.qCovered = new int[classOneNumber];
	}

	@Override
	public int getGain(ICoverSet_FeatureWrapper oneSet, short exceptSetID) {
		int gain = 0;
		int[] containedDB = oneSet.containedDatabaseGraphs();
		for(int gID: containedDB){
			IntersectionSet candidates = supCandidate(gID,exceptSetID);
			if(candidates!=null){
				int currentSize = candidates.size();
//				if(exceptSetID!= -1){ // by product
					this.qCovered[gID] = this.classTwoNumber-currentSize;
//				}
				
				candidates.retainAll(oneSet.containedQueryGraphs());
				gain += currentSize-candidates.size(); // decrease of the candidate set size
			}
			else{
				gain += this.classTwoNumber- oneSet.containedQueryGraphs().length;
	//			if(exceptSetID != -1)
					this.qCovered[gID] = 0;
			}
		}
		this.qCoveredMinFeatureID = this.selectedFeatures[exceptSetID].getFetureID();
		return gain;
	}


	@Override
	public boolean swap(short oldSetID, ICoverSet_FeatureWrapper newSet) {
		long start = System.currentTimeMillis();
		ICoverSet_FeatureWrapper oldSet = this.selectedFeatures[oldSetID];
		if(oldSet == null){
			System.out.println("Wrong input for Swap: invalide oldSetID");
			return false;
		}
		this.selectedFeatures[oldSetID] = newSet;
		// Then, update the query graphs
		int[] oldQ = null, newQ = null;
		oldQ = oldSet.containedDatabaseGraphs();
		newQ = newSet.containedDatabaseGraphs();
		swapQueryIndex(oldQ, newQ, oldSetID);
		System.out.println("Swap Time: " + (System.currentTimeMillis()-start));
		return true;
	}

	@Override
	public boolean removeSet(short sID) {
		if(this.selectedFeatures[sID]!=null){
			// update the qIndex
			int[] removed = this.selectedFeatures[sID].containedDatabaseGraphs();
			for(int qID : removed)
				this.deleteValue(qID, sID);
			this.selectedFeatures[sID] = null;
			return true;
		}
		return false;
	}

	@Override
	public boolean addSet(ICoverSet_FeatureWrapper oneSet, short sID) {
		if(this.selectedFeatures[sID] == null){
			this.selectedFeatures[sID] = oneSet;
			// update the qf index
			int[] containedG = oneSet.containedDatabaseGraphs();
			for(int gID : containedG){
				this.addValue(gID, sID);
			}
			return true;
		}
		else return false;
	}

	@Override
	public short leastCoverSet(int[] minSize) {
		long start = System.currentTimeMillis();
		minSize[0] = Integer.MAX_VALUE;
		short result = 0;
		for(short i = 0; i< this.numOfSets; i++){
			int localMin = this.getGain(this.selectedFeatures[i], i);
			if(minSize[0] > localMin){
				minSize[0] = localMin;
				result = i;
			}
		}
		System.out.println("LeastCovered Set: " + (System.currentTimeMillis()-start));
		return result;
	}

	@Override
	public int getGain(ICoverSet_FeatureWrapper newSet) {
		return this.getGain(newSet, (short)-1);
	}

	@Override
	public boolean addNewSet(ICoverSet_FeatureWrapper newSet) {
		if(this.numOfSets < this.selectedFeatures.length){
			this.selectedFeatures[numOfSets] = newSet;
			// update the qf index
			int[] containedG = newSet.containedDatabaseGraphs();
			for(int gID : containedG){
				this.addValue(gID, numOfSets);
			}
			numOfSets++;
			return true;
		}
		else{
			System.out.println("No Enough Space: try swap");
			return false;
		}
	}

	@Override
	public int getCoveredCount() {
		int count  =0;
		for(int gID = 0; gID < this.classOneNumber; gID++){
			if(this.qfIndex[gID] == null)
				continue; // not feature covering this database graph
			else{
				IntersectionSet set = this.supCandidate(gID, -1); 
				if(set == null) // for the graph "gID", not feature contained in it
					continue;
				else count += this.classTwoNumber-set.size();
			}
		}
		return count;
	}
	
	/*********************Private Member********************************/
	/**
	 * Return null if no feature contained in g
	 * @param g
	 * @return
	 */
	private IntersectionSet supCandidate(int g, int exceptForFeatureF){
		// 1. First Fetch all features contained in query
		IntersectionSet result = new IntersectionSet();
		short[] features = this.qfIndex[g];
		
		// 2. Do intersection to generate the candidate set
		if(features == null)
			return null;
		else{
			boolean firstTime = true;
			for(int i = 0; i< features.length; i++){
				if(features[i] == -1 || features[i] == exceptForFeatureF)
					continue;
				else {
					ICoverSet_FeatureWrapper f = this.selectedFeatures[features[i]];
					if(firstTime){
						result.addAll(f.containedQueryGraphs());
						firstTime = false;
					}
					else 
						result.retainAll(f.containedQueryGraphs());
				}

			}
			if(firstTime)
				return null;
			else return result;
		}
	}
	@Override
	public int[][] addNewSetWithReturn(ICoverSet_FeatureWrapper nextSet) {
		//TODO: not implemented, leave for further implementation
		return null;
	}
}
