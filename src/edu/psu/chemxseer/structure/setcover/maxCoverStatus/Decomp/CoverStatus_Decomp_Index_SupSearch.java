package edu.psu.chemxseer.structure.setcover.maxCoverStatus.Decomp;

import edu.psu.chemxseer.structure.setcover.IO.IInputSequential;
import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter;
import edu.psu.chemxseer.structure.setcover.maxCoverStatus.ICoverStatusStream;
import edu.psu.chemxseer.structure.setcover.maxCoverStatus.Util_IntersectionSet;
import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;
import edu.psu.chemxseer.structure.util.IntersectionSet;

public class CoverStatus_Decomp_Index_SupSearch extends CoverStatus_Decomp_Index implements ICoverStatusStream{

	public CoverStatus_Decomp_Index_SupSearch(int K,  int classOneNumber, int classTwoNumber){
		super(K, classOneNumber, classTwoNumber);
		this.qCovered = new int[classOneNumber];
	}

	@Override
	public int getGain(ICoverSet_FeatureWrapper newSet) {
		return this.getGain(newSet, (short)-1);
	}

	@Override
	public boolean addNewSet(ICoverSet_FeatureWrapper newSet) {
		boolean result =  super.addNewSetPrivate(newSet);
		if(result)
			this.updateScoreAdd(this.numOfSets-1);
		return result;
	}
	
	@Override
	public int[][] addNewSetWithReturn(ICoverSet_FeatureWrapper newSet) {
		boolean insert =  super.addNewSetPrivate(newSet);
		if(insert)
			return this.updateScoreAddWithReturn(this.numOfSets-1);
		return null;
	}


	@Override
	public int getCoveredCount() {
		int count  =0;
		for(int gID = 0; gID < this.classOneNumber; gID++){
			if(this.gfIndex[gID] == null)
				continue; // not feature covering this database graph
			else{
				IntersectionSet set = this.supCandidate(gID, (short)-1); 
				if(set == null) // for the graph "gID", not feature contained in it
					continue;
				else count += this.classTwoNumber-set.size();
			}
		}
		return count;
	}

	@Override
	public int getGain(ICoverSet_FeatureWrapper oneSet, short exceptSetID) {
		int gain = 0;
		int[] containedDB = oneSet.containedDatabaseGraphs();
		for(int gID: containedDB){
			IntersectionSet candidates = supCandidate(gID,exceptSetID);
			if(candidates!=null){
				int currentSize = candidates.size();
				this.qCovered[gID] = this.classTwoNumber-currentSize;
				candidates.retainAll(oneSet.containedQueryGraphs());
				gain += currentSize-candidates.size(); // decrease of the candidate set size
			}
			else{
				gain += this.classTwoNumber- oneSet.containedQueryGraphs().length;
				this.qCovered[gID] = 0;
			}
		}
		this.qCoveredMinFeatureID = this.selectedFeatures[exceptSetID].getFetureID();
		return gain;
	}

	@Override
	public boolean swap(short oldSetID, ICoverSet_FeatureWrapper newSet) {
		ICoverSet_FeatureWrapper oldSet = this.selectedFeatures[oldSetID];
		boolean result=  super.swapPrivate(oldSetID, newSet);
		if(result){
			this.updateScoreSwap(oldSet, newSet, oldSetID);
		}
		return result;
	}

	@Override
	public boolean removeSet(short sID) {
		ICoverSet_FeatureWrapper oldSet = this.selectedFeatures[sID];
		boolean result = super.removeFeaturePrivate(sID);
		if(result){
			this.updateScoreDelete(oldSet, sID);
		}
		return result;
	}

	@Override
	public boolean addSet(ICoverSet_FeatureWrapper oneSet, short sID) {
		boolean result  = super.addFeaturePrivate(oneSet, sID);
		if(result){
			this.updateScoreAdd(sID);
		}
		return result;
	}

	/********************Private Member*******************************/
	private IntersectionSet supCandidate(int g, short exceptForFeatureF){
		// 1. First Fetch all features contained in query
		IntersectionSet result = new IntersectionSet();
		short[] features = this.gfIndex[g];
		
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
	
	/******************For Gain &Inverted Index Update**********************/
	/**
	 * (1) After adding the newSet, the score need to be re-assigned
	 * (2) After adding the newSet, other set's score need to be updated
	 * Eg. For another feature f, it is previous the only cover for <q, d> pair
	 * but not after the inclusion of the new set, then f's gain must decrease by one
	 * @param newSet
	 */
	private void updateScoreAdd(int newSetID){
		this.featureGain[newSetID] =0;
		//2.1 Filter database graphs
		int[] qIDs = this.selectedFeatures[newSetID].notContainedQueryGraphs(this.classTwoNumber);
		for(int gID : this.selectedFeatures[newSetID].containedDatabaseGraphs()){
			this.updateScoreAdd(newSetID, gID, qIDs);
		}
	}
	
	private int[][] updateScoreAddWithReturn(int newSetID) {
		this.featureGain[newSetID] =0;
		//2.1 Filter database graphs
		int[] qIDs = this.selectedFeatures[newSetID].notContainedQueryGraphs(this.classTwoNumber);
		int[] gIDs = this.selectedFeatures[newSetID].containedDatabaseGraphs();
		int[][] result = new int[qIDs.length][];
		int[] xIterator = new int[qIDs.length];
		for(int w = 0; w< xIterator.length; w++){
			result[w] = new int[gIDs.length+1];
			result[w][0] = qIDs[w];
			xIterator[w] = 1;
		}
		
		for(int gID : gIDs){
			this.updateScoreAddWithReturn(newSetID, gID, qIDs, result, xIterator);
		}
		//save space
		for(int i = 0; i< result.length; i++){
			if(xIterator[i] < result[i].length){
				int[] temp = new int[xIterator[i]];
				for(int w = 0; w< xIterator[i]; w++)
					temp[w] = result[i][w];
				result[i] = temp;
			}
		}
		return result;
	}
	


	/**
	 * After deleting oldSetID, <q, d> pair it covers
	 * Another feature "f", it is become the only cover of the <q, d> pair, then it grain increase by 1
	 * @param oldSetID
	 */
	private void updateScoreDelete(ICoverSet_FeatureWrapper oldSet, int oldSetID){
		//2.1 Filter database graphs
		int[] qIDs = this.selectedFeatures[oldSetID].notContainedQueryGraphs(this.classTwoNumber);
		for(int gID : this.selectedFeatures[oldSetID].containedDatabaseGraphs()){
			this.updateScoreDelete(oldSetID, gID, qIDs);
		}
	}
	/**
	 * For each of the <gID, qIDs> pair, 
	 * if this pair is covered by one set $fID$ except for the newSetID, then featureGain[fID]--
	 * if this pair is covered by newSetID only, then featureGain[newSetID]++;
	 * @param newSetID
	 * @param gID
	 * @param qIDs
	 */
	private void updateScoreAdd(int newSetID, int gID, int[] qIDs){
		//this.selectedFeatures[newSetID].notContainedQueryGraphs(this.classTwoNumber)
		for(int qID : qIDs){
			int fID = Util_IntersectionSet.
			retain(gfIndex[gID], gfSize[gID], qfIndex[qID], qfSize[qID], newSetID);
			if(fID >=0)
				this.featureGain[fID]--;
			else if(fID == -1)
				this.featureGain[newSetID]++; // newSet is the only coverage
		}
	}
	
	private void updateScoreAddWithReturn(int newSetID, int gID, int[] qIDs,
			int[][] result, int[] xIterator) {
		for(int i = 0; i< qIDs.length; i++){
			int qID = qIDs[i];
			int fID = Util_IntersectionSet.
			retain(gfIndex[gID], gfSize[gID], qfIndex[qID], qfSize[qID], newSetID);
			if(fID >=0)
				this.featureGain[fID]--;
			else if(fID == -1){
				this.featureGain[newSetID]++; // newSet is the only coverage
				result[i][xIterator[i]++] = gID;
			}
		}
	}
	
	/**
	 * For each of the <gID, qID> pair
	 * if this pair is covered by one set $fID$ except for the oldSetID, then featureGain[fID]++;
	 * if this pair is covered by oldSetID only, the featureGain[oldSetID]--;
	 * @param oldSetID
	 * @param gID
	 * @param qIDs
	 */
	private void updateScoreDelete(int oldSetID, int gID, int[] qIDs){
		for(int qID : qIDs){
			int fID = Util_IntersectionSet.
			retain(gfIndex[gID], gfSize[gID], qfIndex[qID], qfSize[qID], oldSetID);
			if(fID >=0)
				this.featureGain[fID]++;
			else if(fID == -1)
				this.featureGain[oldSetID]--; // newSet is the only coverage
		}
	}
	
	/**
	 * Find all the <q, d> pairs covered by the oldset, covered by the newset
	 * 
	 * @param oldSet
	 * @param newSet
	 * @param newSetID
	 */
	private void updateScoreSwap(ICoverSet_FeatureWrapper oldSet, ICoverSet_FeatureWrapper newSet,
			int setID){
		
		IntersectionSet set = new IntersectionSet();
		set.addAll(newSet.containedQueryGraphs());
		set.removeAll(oldSet.containedQueryGraphs());
		int[] oldSetUncoveredQueriesOnly = set.getItems();
		int[] oldUncoveredQueries = oldSet.notContainedQueryGraphs(classTwoNumber);
		set.clear();
		set.addAll(oldSet.containedQueryGraphs());
		set.removeAll(newSet.containedQueryGraphs());
		int[] newSetUncoveredQueriesOnly = set.getItems();
		int[] newUncoveredQueries = newSet.notContainedQueryGraphs(classTwoNumber);
		
		int[] oldSetCoveredG = oldSet.containedDatabaseGraphs();
		int[] newSetCoveredG = newSet.containedDatabaseGraphs();
		
		int i =0, j=0;
		while(i < oldSetCoveredG.length && j < newSetCoveredG.length){
			if(oldSetCoveredG[i] < newSetCoveredG[j]){
				//oldSetCoveredG[i] is removed
				this.updateScoreDelete(setID, oldSetCoveredG[i], oldUncoveredQueries);
				i++;
			}
			else if(oldSetCoveredG[i] == newSetCoveredG[j]){
				// For queries covered by oldSet only, remove them
				this.updateScoreDelete(setID, oldSetCoveredG[i], oldSetUncoveredQueriesOnly);
				// For queries covered by newSet only, add them
				this.updateScoreAdd(setID, newSetCoveredG[j], newSetUncoveredQueriesOnly);
				i++; j++;
			}
			else{
				// newSetCoveredG[j] is newly added
				this.updateScoreAdd(setID, newSetCoveredG[j], newUncoveredQueries);
			}
		}
		
		for(; i< oldSetCoveredG.length; i++){
			this.updateScoreDelete(setID, oldSetCoveredG[i], oldUncoveredQueries);
		}
		for(; j < newSetCoveredG.length; i++){
			this.updateScoreAdd(setID, newSetCoveredG[i], newUncoveredQueries);
		}
	}

	@Override
	public boolean create(IInputSequential input, IFeatureSetConverter converter) {
		super.construct(input.getSetCount(), converter.getCountOneNumber(), converter.getCountTwoNumber());
		this.qCovered = new int[classOneNumber];
		// add each of the input feature to the inverted index
		ICoverSet_FeatureWrapper nextSet = input.nextSet();
		while(nextSet!=null){
			this.addNewSet(nextSet);
			nextSet = input.nextSet();
		}
		return true;
	}

	@Override
	// A feature f convers the <q, g> pair, if it does not contained in q but contained in g
	public short[] getCoveredSets(int qID, int gID) {
		short[] result= Util_IntersectionSet.retain(this.gfIndex[gID], this.qfIndex[qID]);
		if(result == null)
			return new short[0];
		else return result;
	}




}
