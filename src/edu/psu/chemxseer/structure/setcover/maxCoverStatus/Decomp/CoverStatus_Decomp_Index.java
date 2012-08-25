package edu.psu.chemxseer.structure.setcover.maxCoverStatus.Decomp;

import java.util.Arrays;

import edu.psu.chemxseer.structure.setcover.interfaces.IMaxCoverInvertedIndex;
import edu.psu.chemxseer.structure.setcover.maxCoverStatus.ICoverStatusStream;
import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;

public abstract class CoverStatus_Decomp_Index implements ICoverStatusStream, 
	IMaxCoverInvertedIndex{
	protected int classOneNumber;
	protected int classTwoNumber;
	protected ICoverSet_FeatureWrapper[] selectedFeatures;
	protected int[] featureGain; // the gain of each feature [# of items that f covers only]
	
	protected short numOfSets; // useful when the selecteFeatures are not full
	// The Index: given a query(database graph), 
	// the value list contains all features contained in it
	protected short[][] qfIndex; 
	protected short[] qfSize; // Denoting the size of the qfIndex
	protected short[][] gfIndex;
	protected short[] gfSize; // Denoting the size of the gfIndex

	
	protected int[] qCovered; // qCovered[i] records the number of pairs <qCovered>, covered...
	protected int qCoveredMinFeatureID;
	/**
	 * Initialize both the qfIndex & gfIndex
	 * @param K
	 * @param type
	 * @param classOneNumber
	 * @param classTwoNumber
	 */
	public CoverStatus_Decomp_Index(int K,  int classOneNumber, int classTwoNumber){
		this.construct(K, classOneNumber, classTwoNumber);
	}
	
	protected void construct(int K,  int classOneNumber, int classTwoNumber){
		this.classOneNumber = classOneNumber;
		this.classTwoNumber = classTwoNumber;
		this.selectedFeatures = new ICoverSet_FeatureWrapper[K];
		this.numOfSets = 0;
		// Construct the Inverted Index
		this.qfIndex = new short[classTwoNumber][];
		this.qfSize = new short[classTwoNumber];
		this.gfIndex = new short[classOneNumber][];
		this.gfSize = new short[classOneNumber];
		
		for(int i = 0; i< classTwoNumber; i++)
			qfSize[i] = 0;
		for(int i = 0; i< classOneNumber; i++)
			gfSize[i] = 0;
		
		this.featureGain = new int[K];
		for(int i = 0; i< K; i++)
			featureGain[i] = 0; // ass the 0 as the initial value
	}
	protected void saveSpace(){
		for(int i = 0; i< classTwoNumber; i++){
			if(qfIndex[i].length > qfSize[i]){
				short[] temp = new short[qfSize[i]];
				for(int w = 0; w < temp.length; w++){
					temp[w] = qfIndex[i][w];
				}
				this.qfIndex[i] = temp;
			}
		}
		for(int i = 0; i< classOneNumber; i++){
			if(gfIndex[i].length > gfSize[i]){
				short[] temp = new short[gfSize[i]];
				for(int w = 0; w < temp.length; w++){
					temp[w] = gfIndex[i][w];
				}
				this.gfIndex[i] = temp;
			}
		}
	}

	@Override
	public int[] getCoveredCountExceptMin(short minSetID, int[] yIDs) {
		if(this.qCoveredMinFeatureID == this.selectedFeatures[minSetID].getFetureID()){
			int[] result = new int[yIDs.length];
			for(int i = 0; i< yIDs.length; i++)
				result[i] = this.qCovered[yIDs[i]];
			return result;
		}
		else{
			System.out.println("The getCoveredCountExceptMin count can not be answered directly");
			return null;
		}
	}
	@Override
	public short leastCoverSet(int[] minSize) {
		//Sequential Scan of all the selected features, find the one with the minimum score
		// Other implementations: with priority queue, can be implemented, however we leave that in TODO:
		minSize[0] = Integer.MAX_VALUE;
		short minFeature = -1;
		for(short i = 0; i< this.selectedFeatures.length; i++){
			if(selectedFeatures[i] == null)
				continue;
			else if(this.featureGain[i] < minSize[0]){
				minSize[0] = featureGain[i];
				minFeature = i;
			}
		}
		return minFeature;
	}

	@Override
	public ICoverSet_FeatureWrapper[] getSelectedSets() {
		return this.selectedFeatures;
	}


	@Override
	public ICoverSet_FeatureWrapper getSelectedSet(short minSetID) {
		return this.selectedFeatures[minSetID];
	}

	
	/**
	 * private "partial" operation for addNewSet
	 * @param newSet
	 * @return
	 */
	protected boolean addNewSetPrivate(ICoverSet_FeatureWrapper newSet) {
		if(this.numOfSets < this.selectedFeatures.length){
			this.selectedFeatures[numOfSets] = newSet;
			// update the index
			for(int qID : newSet.containedQueryGraphs())
				this.insertValueToQindex(numOfSets, qID);
			for(int gID : newSet.containedDatabaseGraphs())
				this.insertValueToGindex(numOfSets, gID);
			numOfSets++;
			return true;
		}
		else{
			System.out.println("No Enough Space: try swap");
			return false;
		}
	}
	/**
	 * private "partial" operation for set one set to position id
	 * @param oneSet
	 * @param sID
	 * @return
	 */
	protected boolean addFeaturePrivate(ICoverSet_FeatureWrapper oneSet, short sID) {
		if(this.selectedFeatures[sID] == null){
			this.selectedFeatures[sID] = oneSet;
			// update the index
			for(int qID : oneSet.containedQueryGraphs())
				this.insertValueToQindex(sID, qID);
			for(int gID : oneSet.containedDatabaseGraphs())
				this.insertValueToGindex(sID, gID);
			return true;
		}
		else return false;
	}
	
	/**
	 * private "partial" operation for removing one set with sID
	 * @param sID
	 * @return
	 */
	protected boolean removeFeaturePrivate(short sID) {
		if(this.selectedFeatures[sID] == null){
			return false;
		}
		else{
			ICoverSet_FeatureWrapper oneSet = selectedFeatures[sID];
			selectedFeatures[sID] = null;
			// update the index
			for(int qID : oneSet.containedQueryGraphs())
				this.removeValueFromQindex(sID, qID);
			for(int gID : oneSet.containedDatabaseGraphs())
				this.removeValueFromGindex(sID, gID);
			return true;
		}
	}

	/**
	 * private "partial" operation for swap two sets
	 * @param oldSetID
	 * @param newSet
	 * @return
	 */
	protected boolean swapPrivate(short oldSetID, ICoverSet_FeatureWrapper newSet) {
		long start = System.currentTimeMillis();
		ICoverSet_FeatureWrapper oldSet = this.selectedFeatures[oldSetID];
		if(oldSet == null){
			System.out.println("Wrong input for Swap: invalide oldSetID");
			return false;
		}
		this.selectedFeatures[oldSetID] = newSet;
		// update the Qindex
		int[] oldQueries = oldSet.containedQueryGraphs();
		int[] newQueries = newSet.containedQueryGraphs();
		int i = 0, j = 0;
		for(; i< oldQueries.length && j<newQueries.length; ){
			if(oldQueries[i] == newQueries[j]){
				i++; j++;
			}
			else if(oldQueries[i] < newQueries[j]){
				this.removeValueFromQindex(oldSetID, oldQueries[i]);
				i++;
			}
			else{ // oldQueries[i] > newQueries[j]
				this.insertValueToQindex(oldSetID, newQueries[j]);
				j++;
			}
		}
		for(; i< oldQueries.length;i++)
			this.removeValueFromQindex(oldSetID, oldQueries[i]);
		for(; j < newQueries.length; j++)
			this.insertValueToQindex(oldSetID, newQueries[j]);
		
		// update the Gindex
		int[] oldGraphs = oldSet.containedDatabaseGraphs();
		int[] newGraphs = newSet.containedDatabaseGraphs();
		for(i = 0, j = 0; i< oldGraphs.length && j<newGraphs.length; ){
			if(oldGraphs[i] == newGraphs[j]){
				i++; j++;
			}
			else if(oldGraphs[i] < newGraphs[j]){
				this.removeValueFromGindex(oldSetID, oldGraphs[i]);
				i++;
			}
			else{ // oldQueries[i] > newQueries[j]
				this.insertValueToGindex(oldSetID, newGraphs[j]);
				j++;
			}
		}
		for(; i< oldGraphs.length; i++)
			this.removeValueFromGindex(oldSetID, oldGraphs[i]);
		for(; j < newGraphs.length; j++)
			this.removeValueFromGindex(oldSetID, newGraphs[j]);
		
		System.out.println("Swap Time: " + (System.currentTimeMillis()-start));
		return true;
	}
	
	
	/****************For Inverted Index Maintenance  ***************/
	/**
	 * add a value, maintain the order
	 * @param value
	 * @param qID
	 */
	private void insertValueToQindex(short value, int qID){
		if(this.qfIndex[qID] == null){
			this.qfIndex[qID] = new short[4];
			qfIndex[qID][qfSize[qID]++] = value;
		}
		else if(this.qfSize[qID] == this.qfIndex[qID].length){
			short[] newEntry = new short[qfSize[qID] * 2];
			int i = 0; 
			for(; i < qfSize[qID] && qfIndex[qID][i]<value; i++)
				newEntry[i] = qfIndex[qID][i];
			
			newEntry[i] = value;
			for(; i< qfSize[qID]; i++)
				newEntry[i+1] = qfIndex[qID][i];
			qfIndex[qID] = newEntry;
			qfSize[qID]++;
		}
		else{
			//1. Binary Search the position of "value"
			int pos = Arrays.binarySearch(qfIndex[qID], 0, qfSize[qID], value);
			pos = -pos-1;
			assert pos >=0;
			//2. Insert the new value
			for(int i = qfSize[qID]-1; i >=pos; i--)
				qfIndex[qID][i+1] = qfIndex[qID][i];
			qfIndex[qID][pos] = value;
			qfSize[qID]++;
		}
	}
	private void insertValueToGindex(short value, int gID){
		if(this.gfIndex[gID] == null){
			this.gfIndex[gID] = new short[4];
			gfIndex[gID][gfSize[gID]++] = value;
		}
		else if(this.gfSize[gID] == this.gfIndex[gID].length){
			short[] newEntry = new short[gfSize[gID] * 2];
			int i = 0; 
			for(; i < gfSize[gID] && gfIndex[gID][i]<value; i++)
				newEntry[i] = gfIndex[gID][i];
			
			newEntry[i] = value;
			for(; i< gfSize[gID]; i++)
				newEntry[i+1] = gfIndex[gID][i];
			gfIndex[gID] = newEntry;
			gfSize[gID]++;
		}
		else{
			//1. Binary Search the position of "value"
			int pos = Arrays.binarySearch(gfIndex[gID], 0, gfSize[gID], value);
			pos = -pos-1;
			assert pos >=0;
			//2. Insert the new value
			for(int i = gfSize[gID]-1; i >=pos; i--)
				gfIndex[gID][i+1] = gfIndex[gID][i];
			gfIndex[gID][pos] = value;
			gfSize[gID]++;
		}
	}
	
	/**
	 * remove the value, maintain the order
	 * @param value
	 * @param qID
	 */
	private void removeValueFromQindex(short value, int qID){
		if(qfSize[qID] == 1)
			qfSize[qID] = 0;
		
		else if(2 * this.qfSize[qID] ==  this.qfIndex[qID].length){
			short[] newEntry = new short[qfSize[qID]];
			int iter = 0;
			for(int i = 0; i< qfSize[qID]; i++){
				if(qfIndex[qID][i] == value)
					continue;
				else newEntry[iter++] = qfIndex[qID][i];
			}
			qfIndex[qID] = newEntry;
			qfSize[qID]--;
			assert(iter == qfSize[qID]);
		}
		else{
			//1. Binary Search the position of "value"
			int pos = Arrays.binarySearch(qfIndex[qID], 0, qfSize[qID], value);
			assert (pos >= 0);
			//2. Remove the Value: maintain the order
			for(int i = pos+1; i < qfSize[qID]; i++){
				qfIndex[i-1] = qfIndex[i];
			}
			qfSize[qID]--;
		}
	}
	/**
	 * Remove the value, maitain the order
	 * @param value
	 * @param gID
	 */
	private void removeValueFromGindex(short value, int gID){
		if(this.gfSize[gID] == 1)
			gfSize[gID] = 0;
		else if(2 * this.gfSize[gID] ==  this.gfIndex[gID].length){
			short[] newEntry = new short[gfSize[gID]];
			int iter = 0;
			for(int i = 0; i< gfSize[gID]; i++){
				if(gfIndex[gID][i] == value)
					continue;
				else newEntry[iter++] = gfIndex[gID][i];
			}
			gfIndex[gID] = newEntry;
			gfSize[gID]--;
			assert(iter == gfSize[gID]);
		}
		else{
			//1. Binary Search the position of "value"
			int pos = Arrays.binarySearch(gfIndex[gID], 0, gfSize[gID], value);
			assert (pos >= 0);
			//2. Remove the Value: maintain the order
			for(int i = pos+1; i < gfSize[gID]; i++){
				gfIndex[i-1] = gfIndex[i];
			}
			gfSize[gID]--;
		}
	}


}
