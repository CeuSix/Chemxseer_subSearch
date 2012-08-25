package edu.psu.chemxseer.structure.setcover.maxCoverStatus.Decomp;

import java.util.HashSet;

import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter.FeatureSetType;
import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;
import edu.psu.chemxseer.structure.util.IntersectionSet;

public class CoverStatus_Decomp_PIndex_SubSearch extends CoverStatus_Decomp_PIndex{

	// for each query q, an list of features contained in q, 
	// or for each database graph g, an list of features contained in q, 
	private int[] qfEqual; // for eachq query q, the features equals to it

	public CoverStatus_Decomp_PIndex_SubSearch(int K, int classOneNumber,
			int classTwoNumber) {
		super(K, classOneNumber, classTwoNumber);
		
		this.qfIndex = new short[classTwoNumber][];
		this.qfEqual = new int[classTwoNumber];
		for(int i = 0; i< classTwoNumber; i++)
			this.qfEqual[i] = -1;
		this.qCovered = new int[classTwoNumber];
	}

	@Override
	public int getGain(ICoverSet_FeatureWrapper oneSet, short exceptSetID) {
		long start = System.currentTimeMillis();
		int[] equalQueries = oneSet.getEquavalentQueryGraphs();
		int[] queries = oneSet.containedQueryGraphs();
		
		int gain = 0;
		for(int qID: equalQueries){
			// what is the current candidate set for the equalQueries: do subsearch
			IntersectionSet candidateSet = subCandidate(qID, exceptSetID);
			int candidateSize = this.classOneNumber;
			if(candidateSet!=null)
				candidateSize = candidateSet.size();
			gain += candidateSize; //  those candidates can all be filtered out now
//			if(exceptForFeatureF != -1)// by product, all database graphs are filtered [covered]
				this.qCovered[qID] = this.classOneNumber;
		}
		for(int qID:queries){
			IntersectionSet candidates = subCandidate(qID, exceptSetID);
			
			if(candidates!=null){
				int currentSize = candidates.size();
//				if(exceptForFeatureF != -1) // By Product
					this.qCovered[qID] = this.classOneNumber-currentSize; // all un-candidate graphs are filtered
				candidates.retainAll(oneSet.containedDatabaseGraphs());
				gain += currentSize-candidates.size(); // decrease of the candidate set size
			}
			else {
				gain += this.classOneNumber - oneSet.containedDatabaseGraphs().length;
//				if(exceptForFeatureF != -1) // By Product
					this.qCovered[qID] = 0; // Non of the database graphs are filtered
			}
		}
		System.out.println("SubSearch Get Gain: " + (System.currentTimeMillis()-start));
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
		oldQ = oldSet.containedQueryGraphs();
		newQ = newSet.containedQueryGraphs();
		// Extra work on equal features
		int[] equalQ = oldSet.getEquavalentQueryGraphs();
		for(int qID : equalQ)
			this.qfEqual[qID] = -1;
		equalQ = newSet.getEquavalentQueryGraphs();
		for(int qID : equalQ)
			this.qfEqual[qID] = oldSetID;
		
		swapQueryIndex(oldQ, newQ, oldSetID);
		System.out.println("Swap Time: " + (System.currentTimeMillis()-start));
		return true;
	}

	@Override
	public boolean removeSet(short sID) {
		if(this.selectedFeatures[sID]!=null){
			// update the qIndex
			int[] removed = this.selectedFeatures[sID].containedQueryGraphs();
			//Extra work
			int[] equalQ = this.selectedFeatures[sID].getEquavalentQueryGraphs();
			for(int qID : equalQ)
				this.qfEqual[qID] =-1;
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
			int[] containedQ = oneSet.containedQueryGraphs();
			for(int qID : containedQ){
				this.addValue(qID, sID);
			}
			int[] equalQ = oneSet.getEquavalentQueryGraphs();
			for(int qID : equalQ)
				this.qfEqual[qID] = sID;
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean addNewSet(ICoverSet_FeatureWrapper newSet) {
		if(this.numOfSets < this.selectedFeatures.length){
			this.selectedFeatures[numOfSets] = newSet;
			// update the qf index
			int[] containedQ = newSet.containedQueryGraphs();
			for(int qID : containedQ){
				this.addValue(qID, numOfSets);
			}
			int[] equalQ = newSet.getEquavalentQueryGraphs();
			for(int qID : equalQ)
				this.qfEqual[qID] = numOfSets;
			
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
		//1. Find all the features-equal queries
		int count = 0;
		HashSet<Integer> feature_equal = new HashSet<Integer>();
		for(ICoverSet_FeatureWrapper feature: this.selectedFeatures){
			if(feature == null)
				continue;
			int[] equalQ = feature.getEquavalentQueryGraphs();
			for(int qID:equalQ){
				feature_equal.add(qID);
				count+= this.classOneNumber;
			}
		}
		//2. Process all queries
		for(int qID =0; qID< this.classTwoNumber; qID++){
			if(this.qfIndex[qID] == null || feature_equal.contains(qID))
				continue;
			else{
				IntersectionSet set = this.subCandidate(qID, -1);
				if(set == null) // For the query "qID", no feature contained, therefore, no coverage
					continue; 
				else
					count += this.classOneNumber-set.size();
			}
		}
		return count;
	}
	
	/***************Private Member*********************************/
	/**
	 * Return null if no features contained in q
	 * @param q
	 * @return
	 */
	private IntersectionSet subCandidate(int q, int exceptForFeatureF){
		// First Test if q can be answered directly
		if(this.qfEqual[q]>=0 && this.qfEqual[q]!=exceptForFeatureF)
			return new IntersectionSet(); // no candidates
		
		// 1. First Fetch all features contained in query
		IntersectionSet result = new IntersectionSet();
		short[] features = this.qfIndex[q];
		// 2. Do intersection to generate the candidate set
		if(features == null)
			return null;
		else{
			boolean firstTime = true;
			for(int i = 0; i< features.length; i++){
				if(features[i] == -1 || features[i] == exceptForFeatureF || this.selectedFeatures[features[i]] == null)
					continue;
				else {
					ICoverSet_FeatureWrapper f = this.selectedFeatures[features[i]];
					if(firstTime){
						result.addAll(f.containedDatabaseGraphs());
						firstTime = false;
					}
					else 
						result.retainAll(f.containedDatabaseGraphs());
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
