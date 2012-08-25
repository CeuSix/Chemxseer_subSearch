package edu.psu.chemxseer.structure.setcover.maxCoverStatus;

import edu.psu.chemxseer.structure.setcover.IO.IInputSequential;
import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter;
import edu.psu.chemxseer.structure.setcover.interfaces.IMaxCoverInvertedIndex;
import edu.psu.chemxseer.structure.setcover.sets.CoverSet_FeatureWrapper;
import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;
import edu.psu.chemxseer.structure.util.IntersectionSet;

public class Stream_LinkedList implements ICoverStatusStream_Set, IMaxCoverInvertedIndex{
	private short[][][] invertedIndex; // invertedIndex[item][] = sets containing the item
	private short[][] invertedIndexSize;
	private ICoverSet_FeatureWrapper[] selectedFeatures; // the K selected Features
	private int numOfSets;
	private int[] gain; // the gain of each feature
	private IFeatureSetConverter converter;
	
	// For the Upp bound calculation
	private int[] yCovered;
	private int[] yCoveredMinSet;
	
	
	public Stream_LinkedList(int K, IFeatureSetConverter converter){
		this.construct(K, converter);
		this.constructUpperBound(K, converter);
	}
	
	private void construct(int K, IFeatureSetConverter converter){
		this.converter = converter;
		this.selectedFeatures = new ICoverSet_FeatureWrapper[K];
		this.numOfSets = 0;
		this.invertedIndex = new short[converter.getCountTwoNumber()][converter.getCountOneNumber()][];
		this.invertedIndexSize = new short[converter.getCountTwoNumber()][converter.getCountOneNumber()];
		for(int i = 0; i< invertedIndexSize.length; i++)
			for(int j = 0; j < invertedIndexSize[i].length; j++)
				invertedIndexSize[i][j] = 0;
		this.gain = new int[K];
	}
	
	private void constructUpperBound(int K, IFeatureSetConverter converter){
		//Construct the Upperbound calculator
		yCovered = new int[converter.getCountTwoNumber()];
		yCoveredMinSet = new int[converter.getCountTwoNumber()];
		for(int i = 0; i< yCoveredMinSet.length; i++)
			yCoveredMinSet[i] = -2;
	}
	
	@Override
	public int getGain(ICoverSet_FeatureWrapper oneSet, short exceptSetID) {
		int count = 0;
		int[][] items = this.converter.featureToSet_Matrix(oneSet);
		for(int i = 0;i< items.length; i++){
			int yID = items[i][0];
			for(int j = 1; j< items[i].length; j++){
				int xID = items[i][j];
				if(invertedIndexSize[yID][xID] == 0)
					count++;
				else if(invertedIndexSize[yID][xID] == 1 && invertedIndex[yID][xID][0] == exceptSetID)
					count++;
			}
		}
		return count;
	}
	@Override
	public int[] getCoveredCountExceptMin(short minSetID, int[] yIDs) {
		int[] result = new int[yIDs.length];
		int[][] minCovered = converter.featureToSet_Matrix(this.selectedFeatures[minSetID]);
		for(int minIndex = 0, yIDsIndex = 0; yIDsIndex < yIDs.length; yIDsIndex++){
			int yID = yIDs[yIDsIndex];
			if(this.yCoveredMinSet[yID] == this.selectedFeatures[minSetID].getFetureID())
				result[yIDsIndex] = yCoveredMinSet[yID]; //  no need to re-calculated
			else{
				// Need to recalculated:
				// 2.1 Calculate the total score
				int totalScore = 0;
				for(int w = 0; w< this.invertedIndexSize[yID].length; w++)
					if(invertedIndexSize[yID][w] > 0)
						totalScore ++;
				// 2.2 get from the converter the minSet covers in yID
				while(minIndex < minCovered.length && minCovered[minIndex][0] < yID)
					minIndex++;
				
				if(minIndex >= minCovered.length || yID < minCovered[minIndex][0]){
					yCovered[yID] = totalScore; // use the total score directly
				}
				else if (yID == minCovered[minIndex][0]){
					for(int w = 1; w < minCovered[minIndex].length; w++){
						int item = minCovered[minIndex][w];
						if(invertedIndexSize[yID][item] == 1)
							totalScore--;
					}
					yCovered[yID] = totalScore;
				}
				yCoveredMinSet[yID] = this.selectedFeatures[minSetID].getFetureID();
				result[yIDsIndex] = yCoveredMinSet[yID]; 
			}
			
		}
		return yCovered;
	}

	@Override
	public boolean swap(short oldSetID, ICoverSet_FeatureWrapper newSet) {
		ICoverSet_FeatureWrapper oldSet = this.selectedFeatures[oldSetID];
		if(oldSet == null)
			return false;
		// Do the swap
		this.selectedFeatures[oldSetID] = newSet;
		// Update the structure
		int[][] newItems = this.converter.featureToSet_Matrix(newSet);
		int[][] oldItems = this.converter.featureToSet_Matrix(oldSet);
		IntersectionSet set = new IntersectionSet();
		
		int newIter = 0, oldIter = 0;
		while(newIter < newItems.length && oldIter < oldItems.length){
			int yID = newItems[newIter][0];
			if(yID < oldItems[oldIter][0]){
				// insert new yID
				for(int j = 1; j< newItems[newIter].length; j++){
					int xID = newItems[newIter][j];
					this.insertValue(yID, xID, oldSetID);
				}
				newIter++;
			}
			else if(yID == oldItems[oldIter][0]){
				// insert new
				set.clear();
				set.addAll(newItems[newIter], 1, newItems[newIter].length);
				set.removeAll(oldItems[oldIter], 1, oldItems[oldIter].length);
				int[] newlyCovered = set.getItems();
				for(int xID : newlyCovered)
					insertValue(yID, xID, oldSetID);
				// remove old
				set.clear();
				set.addAll(oldItems[oldIter], 1,  oldItems[oldIter].length);
				set.removeAll(newItems[newIter], 1, newItems[newIter].length);
				int[] oldCovered = set.getItems();
				for(int xID : oldCovered)
					removeValue(yID, xID, oldSetID);
				newIter++;
				oldIter++;
			}
			else{
				yID = oldItems[oldIter][0];
				for(int j = 1; j< oldItems[oldIter].length; j++){
					int xID = oldItems[oldIter][j];
					this.removeValue(yID, xID, oldSetID);
				}
				oldIter++;
			}
		}
		return true;
	}

	@Override
	public boolean removeSet(short sID) {
		ICoverSet_FeatureWrapper oldSet = this.selectedFeatures[sID];
		if(oldSet == null){
			return false;
		}
		else{
			int[][] items = this.converter.featureToSet_Matrix(oldSet);
			for(int i = 0; i< items.length; i++){
				int yID = items[i][0];
				for(int j = 1; j< items[i].length; j++){
					int xID = items[i][j];
					this.removeValue(yID, xID, sID);
				}
			}
			this.gain[sID] =0;
			return true;
		}
	}

	@Override
	public boolean addSet(ICoverSet_FeatureWrapper oneSet, short sID) {
		if(this.selectedFeatures[sID] == null){
			int[][] items = this.converter.featureToSet_Matrix(oneSet);
			gain[sID] = 0;
			for(int i = 0; i< items.length; i++){
				int yID = items[i][0];
				for(int j = 1; j < items[i].length; j++){
					int xID = items[i][j];
					this.insertValue(yID,xID, sID);
				}
			}
			return true;
		}
		else return false;
	}

	@Override
	public short leastCoverSet(int[] minSize) {
		minSize[0] = Integer.MAX_VALUE;
		short result = (short)-1;
		for(short i = 0; i< gain.length; i++)
			if(this.selectedFeatures[i]!=null && gain[i] < minSize[0]){
				minSize[0] = gain[i];
				result = i;
			}
		return result;
	}

	@Override
	public ICoverSet_FeatureWrapper[] getSelectedSets() {
		return this.selectedFeatures;
	}

	@Override
	public ICoverSet_FeatureWrapper getSelectedSet(short minSetID) {
		return this.selectedFeatures[minSetID];
	}

	@Override
	public int getGain(ICoverSet_FeatureWrapper newSet) {
		return this.getGain(newSet, (short)-1);
	}

	@Override
	public boolean addNewSet(ICoverSet_FeatureWrapper newSet) {
		if(this.numOfSets < this.selectedFeatures.length){
			this.selectedFeatures[numOfSets] = newSet;
			this.gain[numOfSets] = 0;
			int[][] items = this.converter.featureToSet_Matrix(newSet);
			for(int i = 0; i< items.length; i++){
				int yID = items[i][0];
				for(int j =1; j< items[i].length; j++){
					int xID = items[i][j];
					this.insertValue(yID, xID, numOfSets);
				}
			}
			numOfSets++;
			return true;
		}
		else {
			System.out.println("Error: not enough sapce: use swap instead");
			return false;
		}
	}
	@Override
	public int[][] addNewSetWithReturn(ICoverSet_FeatureWrapper newSet) {
		if(this.numOfSets < this.selectedFeatures.length){
			this.selectedFeatures[numOfSets] = newSet;
			this.gain[numOfSets] = 0;
			int[][] items = this.converter.featureToSet_Matrix(newSet);
			int[][] result = new int[items.length][];
			int iter = 0;
			
			for(int i = 0; i< items.length; i++){
				int yID = items[i][0];
				result[i] = new int[items[i].length];
				result[i][0] = yID;
				iter = 1;
				
				for(int j =1; j< items[i].length; j++){
					int xID = items[i][j];
					if(this.invertedIndexSize[yID][xID] == 0)
						result[i][iter++] = xID;
					this.insertValue(yID, xID, numOfSets);
				}
				// save space
				if(iter < result[i].length){
					int[] temp = new int[iter];
					for(int w = 0; w < iter; w++)
						temp[w] = result[i][w];
					result[i] = temp;
				}
			}
			numOfSets++;
			return result;
		}
		else {
			System.out.println("Error: not enough sapce: use swap instead");
			return null;
		}
	}


	@Override
	public int getCoveredCount() {
		int counter = 0;
		for(int i = 0; i < this.invertedIndexSize.length; i++)
			for(int j = 0; j< this.invertedIndexSize[i].length; j++)
				if(invertedIndexSize[i][j] >0)
					counter++;
		return counter;
	}

	@Override
	public IFeatureSetConverter getConverter() {
		return this.converter;
	}
	
	
	
	/****************** Private Member******************************/
	/**
	 * add the fID to the itemID entry 
	 * update the inverted index & gain function
	 * @param itemID
	 * @param fID
	 */
	private void insertValue(int yID, int xID, int fID){
		if(this.invertedIndex[yID][xID] == null){
			invertedIndex[yID][xID] = new short[2];
			invertedIndexSize[yID][xID] = 0;
		}
		else if(this.invertedIndex[yID][xID].length == invertedIndexSize[yID][xID]){
			short[] temp = new short[invertedIndexSize[yID][xID]*2];
			for(int i = 0; i< invertedIndexSize[yID][xID]; i++){
				temp[i] = invertedIndex[yID][xID][i];
			}
			invertedIndex[yID][xID] = temp;
		}
		// update the score
		if(invertedIndexSize[yID][xID] == 1){
			// currently covered by one feature f, after adding fID, f's score decrease
			this.gain[this.invertedIndex[yID][xID][0]] --;
		}
		else if(invertedIndexSize[yID][xID] == 0)
			this.gain[fID]++;
		// update the inverted index by appending the fID
		invertedIndex[yID][xID][invertedIndexSize[yID][xID]++] = (short) fID;
	}
	
	/**
	 * remove the fID from the itemID entry 
	 * update the inverted index & gain function
	 * since it is not required that the invered index is ordered, there fore an linear
	 * search is needed.
	 * @param itemID
	 * @param fID
	 */
	private void removeValue(int yID, int xID, int fID){
		// Shrink the size of the array
		if(2 * invertedIndexSize[yID][xID] == invertedIndex[yID][xID].length 
				&& invertedIndex[yID][xID].length > 2){
			short[] temp = new short[invertedIndexSize[yID][xID]];
			int iter = 0;
			for(int i = 0; i< invertedIndexSize[yID][xID]; i++)
				if(invertedIndex[yID][xID][i]!=fID)
					temp[iter++] = invertedIndex[yID][xID][i];
			this.invertedIndex[yID][xID] = temp;
			invertedIndexSize[yID][xID]--;
		}
		else{
			// do the deletion
			int pos = linearSearch(this.invertedIndex[yID][xID], this.invertedIndexSize[yID][xID], fID);
			if(pos == 0)
				System.out.println("error in removevalue, not such value");
			if(pos != invertedIndexSize[yID][xID]-1){
				// fID is not the last: swap the fID with the last value
				this.invertedIndex[yID][xID][pos] = invertedIndex[yID][xID][invertedIndexSize[yID][xID]-1];
			}
			invertedIndexSize[yID][xID]--;;
		}
		// update the score
		if(invertedIndexSize[yID][xID] == 1){
			gain[invertedIndex[yID][xID][0]]--;
		}
		else if(invertedIndexSize[yID][xID] == 0)
			gain[fID]--;
		
	}
	
	private int linearSearch(short[] array, int boundary, int value){
		if(array == null)
			return -1;
		else{
			for(int i = 0; i< boundary; i++)
				if(array[i] == value)
					return i;
			return -1;
		}
	}
	
	/************For the implementation of MaxSolver_InvertedIndex************/
	@Override
	public boolean create(IInputSequential input, IFeatureSetConverter converter) {
		//1. Construct: 
		int K = input.getSetCount();
		this.construct(K, converter);
		//2. Initialize the inverted index
		CoverSet_FeatureWrapper oneSet = input.nextSet();
		while(oneSet!=null){
			this.addNewSet(oneSet);
			oneSet = input.nextSet();
		}
		//3. save space
		this.saveSpace();
		return true;
	}
	
	private void saveSpace(){
		for(int i = 0; i< this.invertedIndexSize.length; i++){
			for(int j = 0; j< this.invertedIndexSize[i].length; j++){
				if(this.invertedIndexSize[i][j] < this.invertedIndex[i][j].length){
					short[] temp = new short[invertedIndexSize[i][j]];
					for(int w =0; w< temp.length; w++)
						temp[w] = invertedIndex[i][j][w];
					invertedIndex[i][j] = temp;
				}
			}
		}
	}
	
	@Override
	public short[] getCoveredSets(int qID, int gID) {
		return this.invertedIndex[qID][gID];
	}

}
