package edu.psu.chemxseer.structure.setcover.maxCoverStatus.Decomp;

import java.util.HashSet;

import edu.psu.chemxseer.structure.setcover.IO.IInputSequential;
import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter;
import edu.psu.chemxseer.structure.setcover.maxCoverStatus.ICoverStatusStream;
import edu.psu.chemxseer.structure.setcover.maxCoverStatus.Util_IntersectionSet;
import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;
import edu.psu.chemxseer.structure.util.IntersectionSet;

public class CoverStatus_Decomp_Index_SubSearch extends CoverStatus_Decomp_Index implements ICoverStatusStream {
	
	private short[] qfEqual; 
	public CoverStatus_Decomp_Index_SubSearch(int K,  int classOneNumber, int classTwoNumber){
		super(K, classOneNumber, classTwoNumber);
		this.qCovered = new int[classTwoNumber];
		this.qfEqual = new short[classTwoNumber];
	}
	@Override
	public int getGain(ICoverSet_FeatureWrapper newSet) {
		return this.getGain(newSet, (short)-1);
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
			//by product, all database graphs are filtered [covered]
			this.qCovered[qID] = this.classOneNumber;
		}
		for(int qID:queries){
			IntersectionSet candidates = subCandidate(qID, exceptSetID);
			
			if(candidates!=null){
				int currentSize = candidates.size();
				// By Product
				this.qCovered[qID] = this.classOneNumber-currentSize; // all un-candidate graphs are filtered
				candidates.retainAll(oneSet.containedDatabaseGraphs());
				gain += currentSize-candidates.size(); // decrease of the candidate set size
			}
			else {
				gain += this.classOneNumber - oneSet.containedDatabaseGraphs().length;
				// By Product
				this.qCovered[qID] = 0; // Non of the database graphs are filtered
			}
		}
		System.out.println("SubSearch Get Gain: " + (System.currentTimeMillis()-start));
		this.qCoveredMinFeatureID = this.selectedFeatures[exceptSetID].getFetureID();
		return gain;
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


	@Override
	public boolean swap(short oldSetID, ICoverSet_FeatureWrapper newSet) {
		ICoverSet_FeatureWrapper oldSet = this.selectedFeatures[oldSetID];
		boolean result = super.swapPrivate(oldSetID, newSet);;
		// update the qfEqual if subSearch
		if(result){
			for(int qID : oldSet.getEquavalentQueryGraphs())
				this.qfEqual[qID] = -1;
			for(int qID : newSet.getEquavalentQueryGraphs())
				this.qfEqual[qID] = oldSetID;
			this.updateScoreSwap(oldSet, newSet, oldSetID);
			
		}
		return result;
	}

	@Override
	public boolean removeSet(short sID) {
		ICoverSet_FeatureWrapper oneSet = this.selectedFeatures[sID];
		boolean result = super.removeFeaturePrivate(sID);
		if(result){
			for(int qID : oneSet.getEquavalentQueryGraphs())
				this.qfEqual[qID] = -1;
			this.updateScoreDelete(oneSet, sID);
		}
		return result;
	}
	
	@Override
	public boolean addNewSet(ICoverSet_FeatureWrapper newSet) {
		boolean result = super.addNewSetPrivate(newSet);
		if(result){
			for(int qID : newSet.getEquavalentQueryGraphs())
				this.qfEqual[qID] = (short)(numOfSets-1);
			// update the score
			this.updateScoreAdd(numOfSets-1);
		}
		return result;
	}
	
	@Override
	public int[][] addNewSetWithReturn(ICoverSet_FeatureWrapper newSet) {
		boolean insert = super.addNewSetPrivate(newSet);
		if(insert){
			for(int qID : newSet.getEquavalentQueryGraphs())
				this.qfEqual[qID] = (short)(numOfSets-1);
			// update the score
			return this.updateScoreAddWithReturn(numOfSets-1);
		}
		return null;
	}

	@Override
	public boolean addSet(ICoverSet_FeatureWrapper oneSet, short sID) {
		boolean result = super.addFeaturePrivate(oneSet, sID);
		if(result){
			for(int qID : oneSet.getEquavalentQueryGraphs())
				this.qfEqual[qID] = sID;
			// update the score
			this.updateScoreAdd(sID);
		}
		return result;
	}

	
	/****************Private Member***********************************/
	private IntersectionSet subCandidate(int q, int exceptForFeatureF){
		// First Test if q can be answered directly
		// For supSearch, qfEqual == null
		if(this.qfEqual[q]>=0 && this.qfEqual[q]!=exceptForFeatureF)
			return new IntersectionSet(); // no candidates
		
		// 1. First Fetch all features contained in query
		IntersectionSet result = new IntersectionSet();
		short[] features = qfIndex[q];
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
	/**************For Score Update***************************************/
	/**
	 * (1) After adding the newSet, the score need to be re-assigned
	 * (2) After adding the newSet, other set's score need to be updated
	 * Eg. For another feature f, it is previous the only cover for <q, d> pair
	 * but not after the inclusion of the new set, then f's gain must decrease by one
	 * @param newSet
	 */
	private void updateScoreAdd(int newSetID){
		this.featureGain[newSetID] =0;
		//1.1 Equal queries
		for(int qID : this.selectedFeatures[newSetID].getEquavalentQueryGraphs()){
			for(int gID = 0; gID < this.classOneNumber; gID++){
				int fID = Util_IntersectionSet.
				retain(qfIndex[qID], qfSize[qID], gfIndex[gID], gfSize[gID], newSetID);
				if(fID >=0)
					this.featureGain[fID]--;
				else if(fID == -1)
					this.featureGain[newSetID]++; // newSet is the only coverage
			}
		}
		//1.2 Filter queries
		int[] gIDs = this.selectedFeatures[newSetID].notContainedDatabaseGraphs(this.classOneNumber);
		for(int qID : this.selectedFeatures[newSetID].containedQueryGraphs()){
			this.updateScoreAdd(newSetID, qID, gIDs);
		}
	}
	
	private int[][] updateScoreAddWithReturn(int newSetID){
		this.featureGain[newSetID] =0;
		
		int equalCount = this.selectedFeatures[newSetID].getEquavalentQueryGraphs().length;
		int filterCount = this.selectedFeatures[newSetID].containedQueryGraphs().length;
		int[][] result = new int[equalCount+filterCount][];
		int yIter = 0;
		int xIter = 0;
		
		//1.1 Equal queries
		for(int qID : this.selectedFeatures[newSetID].getEquavalentQueryGraphs()){
			result[yIter++] = new int[classOneNumber+1];
			result[yIter][0] = qID;
			xIter = 1;
			for(int gID = 0; gID < this.classOneNumber; gID++){
				int fID = Util_IntersectionSet.
				retain(qfIndex[qID], qfSize[qID], gfIndex[gID], gfSize[gID], newSetID);
				if(fID >=0)
					this.featureGain[fID]--;
				else if(fID == -1){
					result[yIter][xIter++] = gID;
					this.featureGain[newSetID]++; // newSet is the only coverage
				}
			}
			if(xIter < result[yIter].length){
				int[] temp = new int[xIter];
				for(int w = 0; w< xIter; w++)
					temp[w] = result[yIter][w];
				result[yIter] = temp;
			}
		}
		//1.2 Filter queries
		int[] gIDs = this.selectedFeatures[newSetID].notContainedDatabaseGraphs(this.classOneNumber);
		for(int qID : this.selectedFeatures[newSetID].containedQueryGraphs()){
			result[yIter++] = this.updateScoreAdd(newSetID, qID, gIDs);
		}
		return result;
	}
	
	/**
	 * After deleting oldSetID, <q, d> pair it covers
	 * Another feature "f", it is become the only cover of the <q, d> pair, then it grain increase by 1
	 * @param oldSetID
	 */
	private void updateScoreDelete(ICoverSet_FeatureWrapper oldSet, int oldSetID){
		//1.1 Equal queries
		for(int qID : oldSet.getEquavalentQueryGraphs()){
			for(int gID = 0; gID < this.classOneNumber; gID++){
				int fID = Util_IntersectionSet.
				retain(qfIndex[qID], qfSize[qID], gfIndex[gID], gfSize[gID], oldSetID);
				if(fID >=0)
					this.featureGain[fID]++;
//				else if(fID == -1)
//					this.featureGain[oldSetID]--; 
			}
		}
		//1.2 Filter queries
		int[] gIDs = oldSet.notContainedDatabaseGraphs(this.classOneNumber);
		for(int qID : oldSet.containedQueryGraphs()){
			this.updateScoreDelete(oldSetID, qID, gIDs);
		}
	}
	/**
	 * Given the oldSet & newSet, swap it, and update the score
	 * @param oldSet
	 * @param newSet
	 * @param setID
	 */
	private void updateScoreSwap(ICoverSet_FeatureWrapper oldSet, ICoverSet_FeatureWrapper newSet,
			int setID){
		IntersectionSet set = new IntersectionSet();
		set.addAll(newSet.containedDatabaseGraphs());
		set.removeAll(oldSet.containedDatabaseGraphs());
		int[] oldSetUncoveredGraphsOnly = set.getItems();
		int[] oldUncoveredGraphs = oldSet.notContainedDatabaseGraphs(this.classOneNumber);
		set.clear();
		set.addAll(oldSet.containedDatabaseGraphs());
		set.removeAll(newSet.containedDatabaseGraphs());
		int[] newSetUncoveredGraphsOnly = set.getItems();
		int[] newUncoveredGraphs = newSet.notContainedDatabaseGraphs(this.classOneNumber);
		
		int[] oldSetCoveredQ = oldSet.containedQueryGraphs();
		int[] newSetCoveredQ = newSet.containedQueryGraphs();
		
		int oldQIter = 0, newQIter = 0;
		
		//0. Remove equvalent covered queries by the oldQuery
		for(int qID : oldSet.getEquavalentQueryGraphs()){
			for(int gID = 0; gID < this.classOneNumber; gID++){
				int fID = Util_IntersectionSet.
				retain(qfIndex[qID], qfSize[qID], gfIndex[gID], gfSize[gID], setID);
				if(fID >=0)
					this.featureGain[fID]++;
				else if(fID == -1)
					this.featureGain[setID]--; 
			}
		}
		//1. For non-equavalent covered queries
		while(oldQIter < oldSetCoveredQ.length && newQIter < newSetCoveredQ.length){
			int oldQ = oldSetCoveredQ[oldQIter];
			int newQ = newSetCoveredQ[newQIter];
			if(oldQ < newQ){
				//1.1 : the old Q will not be covered by the new set
				if(this.qfEqual[oldQ]!= setID)
					this.updateScoreDelete(setID, oldQ, oldUncoveredGraphs);
				//1.2: the oldQ is covered by the new set [directly] & covered by the old set
				else{
					//1.2.1 Property one: for all queries = newSet f, they are in oldQ, if one of then appears in oldQ
					//1.2.2 Property two: while doing the update, since setID = qfEqual[oldQ]
					int[] newlyCovered = newSet.containedDatabaseGraphs();
					this.updateScoreAdd(setID, oldQ, newlyCovered);
				}
				oldQIter++;
			}
			else if(oldQ == newQ){
				//2.1: remove the old set covered only
				this.updateScoreDelete(setID, oldQ, oldSetUncoveredGraphsOnly);
				//2.2: ad the new set covered only
				this.updateScoreAdd(setID, newQ, newSetUncoveredGraphsOnly);
				oldQIter++;
				newQIter++;
			}
			else{
				// oldQ > newQ, newQ is newly covered
				// 3.1: the newQ is not directly covered by old Set
//				if(!oldEqualQueries.contains(newQ))
//					this.updateScoreAdd(setID, newQ, newUncoveredGraphs);
//				// 3.2: the newQ is directly covered by the oldSet: 
//				// in 0.1, all the items covered by the oldSet are removed, 
//				else{
//					this.updateScoreAdd(setID, newQ, newUncoveredGraphs);
//				}
				// Therefore, in summary
				this.updateScoreAdd(setID, newQ, newUncoveredGraphs);
				newQIter++;
			}
		}
		//2. 
		for(; oldQIter < oldSetCoveredQ.length; oldQIter++){
			int oldQ = oldSetCoveredQ[oldQIter];
			//1.1 : the old Q will not be covered by the new set
			if(this.qfEqual[oldQ]!= setID)
				this.updateScoreDelete(setID, oldQ, oldUncoveredGraphs);
			//1.2: the oldQ is covered by the new set [directly] & covered by the old set
			else{
				//1.2.1 Property one: for all queries = newSet f, they are in oldQ, if one of them appears in oldQ
				//1.2.2 Property two: while doing the update, since setID = qfEqual[oldQ]
				int[] newlyCovered = newSet.containedDatabaseGraphs();
				this.updateScoreAdd(setID, oldQ, newlyCovered);
			}
		}
		//3. 
		for(; newQIter < newSetCoveredQ.length; newQIter++){
			this.updateScoreAdd(setID, newSetCoveredQ[newQIter], newUncoveredGraphs);
		}
	}
	
	/**
	 * If qID is directly answered by a F: then decrease the F score
	 * If qID is not direct answered by a F: then as usual
	 * @param newSetID
	 * @param qID
	 * @param gIDs
	 */
	private int[] updateScoreAdd(int newSetID, int qID, int[] gIDs){
		//this.selectedFeatures[newSetID].notContainedDatabaseGraphs(this.classOneNumber);
		int[] result = new int[gIDs.length + 1];
		result[0] = qID;
		int iter = 1;
		
		for(int gID : gIDs){
			int fID = Util_IntersectionSet.
			retain(qfIndex[qID], qfSize[qID], gfIndex[gID], gfSize[gID], newSetID);
			
			if(qfEqual[qID] == newSetID){
				// the newSetID covered qID directly
				if(fID==-1){ // the <q, g> pair not covered by other features
					this.featureGain[newSetID]++;
					result[iter++] = gID;
				}
				else if(fID >= 0) // the <q, g> pair covered once by other features
					this.featureGain[fID]--;
				//else fID == -1, <q, g> pair covered by multiple features
			}
			else if(qfEqual[qID] >=0){
				// the qID is covered directly, but not by newSetID
				if(fID == -1) // the <q, g> pair not covered by other features, except for the feature qfEauql[qID]
					this.featureGain[qfEqual[qID]]--;
				// else, <q, g> pair covered by multi features, 
			}
			else{
				// qfEqual[qID] <0, qID is not covered directly
				if(fID == -1){ // the <q, g> pair not covered by other features
					this.featureGain[newSetID]++;
					result[iter++] = gID;
				}
				else if(fID > 0) // the <q, g> pair is covered by only by fID
					this.featureGain[fID]--;
				//else <q, g> pair is covered by multiple features
			}		
		}
		if(iter < result.length){
			int[] finalResult = new int[iter];
			for(int w = 0; w< iter; w++)
				finalResult[w] = result[w];
			return finalResult;
		}
		else return result;
	}
	/**
	 * Assumption, qfIndex[qID] != oldSetID 
	 * IF qID is directly answered by a F: then increase the F score
	 * If qID is not directly answered by a F: then as usual
	 * @param oldSetID
	 * @param qID
	 * @param gIDs
	 */
	private void updateScoreDelete(int oldSetID, int qID, int[] gIDs){
		//this.selectedFeatures[oldSetID].notContainedDatabaseGraphs(this.classOneNumber)
		for(int gID : gIDs){
			int fID = Util_IntersectionSet.
			retain(qfIndex[qID], qfSize[qID], gfIndex[gID], gfSize[gID], oldSetID);
			if(qfEqual[qID] > 0 && fID == -1)
				this.featureGain[qfEqual[qID]]++; // previous the <q, d> pair is covered by both direct answer & oldSetID
			if(fID >=0 && this.qfEqual[qID] < 0)  // previous, the <q, d> pair is covered by fID & oldSetID
				this.featureGain[fID]++;
			else if(fID == -1 && this.qfEqual[qID] < 0) // previous, the <q, d> pair is covered by oldSetID only
				this.featureGain[oldSetID]--;
		}
	}
	@Override
	public boolean create(IInputSequential input, IFeatureSetConverter converter) {
		super.construct(input.getSetCount(), converter.getCountOneNumber(), converter.getCountTwoNumber());
		this.qCovered = new int[classTwoNumber];
		this.qfEqual = new short[classTwoNumber];
		// add each of the input feature to the inverted index
		ICoverSet_FeatureWrapper nextSet = input.nextSet();
		while(nextSet!=null){
			this.addNewSet(nextSet);
			nextSet = input.nextSet();
		}
		return true;
	}
	@Override
	// A feature covers the <q, g> pair, if it is contained in q, but not contained in g, or it equals to "q".  
	public short[] getCoveredSets(int qID, int gID) {
		short[] result= Util_IntersectionSet.retain(this.qfIndex[qID], this.gfIndex[gID]);
		if(this.qfEqual[qID] >=0 ){
			short[] finalResult = new short[result.length+1];
			for(int w = 0; w< result.length; w++)
				finalResult[w] = result[w];
			finalResult[result.length] = qfEqual[qID];
			return finalResult;
		}
		else return result;
	}
	

}
