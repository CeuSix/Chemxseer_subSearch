package edu.psu.chemxseer.structure.setcover.maxCoverStatus;

import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter;
import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;
import edu.psu.chemxseer.structure.util.IntersectionSet;

/**
 * A streaming status implementation
 * Record the Status of the Item coverage
 * Counter is used for each items
 * Support "counter" check
 * This implementation uses the integer array to store all the counters
 * (1) For each of the new set: calculate the "score" of the new set by looking up the table. 
 * The score can be calculated linear to the size of the new set
 * (2) Given all the selected (old) sets, return the set with minimum score. 
 * Sequential scan all the selected sets and check their scores, find the min score
 * Time complexity = K * |S|, where K is the # of selected features and |S| is the average size of those
 * selected features.
 * (3) After each swap operation (or insertion), the update of the status is linear to the size of 
 * swapped in set + swapped out set
 * @author dayuyuan
 *
 */
public class Stream_ShortMatrix implements ICoverStatusStream_Set{
	// In order to save space, here I use short instead of integer.
	// Since, the total number of selected set "K" will not exceed the boundary of short
	private short[][] coverStatus;
	private IFeatureSetConverter converter;
	private ICoverSet_FeatureWrapper[] selectedFeatures;
	private int numOfSets;
	
	// For the Upp bound calculation
	private int[] yCovered;
	private int[] yCoveredMinSet;
	
	/**
	 * Total number of selected features K
	 * FeatureSetConverter converter
	 * @param K
	 * @param converter
	 */
	public Stream_ShortMatrix(int K,  IFeatureSetConverter converter){
		this.converter = converter;
		this.selectedFeatures = new ICoverSet_FeatureWrapper[K];
		this.numOfSets = 0;
		this.coverStatus = new short[converter.getCountTwoNumber()][converter.getCountOneNumber()];
		for(int i = 0; i< coverStatus.length; i++)
			for(int j = 0; j< coverStatus.length; j++)
				coverStatus[i][j] = 0;
		
		//For the Upp bound Calculation
		yCovered = new int[converter.getCountTwoNumber()];
		yCoveredMinSet = new int[converter.getCountTwoNumber()];
		for(int i = 0; i< yCoveredMinSet.length; i++)
			yCoveredMinSet[i] = -2;
	}
	@Override
	public int getGain(ICoverSet_FeatureWrapper oneSet, short exceptSetID) {
		int[][] exceptItems =null;
		if(exceptSetID >=0 && exceptSetID < this.selectedFeatures.length && selectedFeatures[exceptSetID]!=null){
			exceptItems = this.converter.featureToSet_Matrix(selectedFeatures[exceptSetID]);
		}
		else exceptItems = new int[0][0];
		
		int[][] newItems = this.converter.featureToSet_Matrix(oneSet);
		return this.getGain(newItems, exceptItems);
	}
	
	/**
	 * Get the number items covered by the newItems, but not the exceptItems.
	 * @param newItems
	 * @param exceptItems
	 * @return
	 */
	private int getGain(int[][] newItems, int[][] exceptItems){
		int gain = 0;
		int j = 0;
		IntersectionSet interTool = new IntersectionSet();
		for(int i = 0;  i < exceptItems.length && j< newItems.length; ){
			int yIDNew = newItems[j][0];
			int yIDExp = exceptItems[i][0];
			if(yIDExp < yIDNew)
				i++;
			else if(yIDExp == yIDNew){
				interTool.clear();
				interTool.addAll(newItems[j], 1, newItems[j].length);
				interTool.removeAll(exceptItems[i], 1, exceptItems[i].length);
				int[] newSetCoveredOnly = interTool.getItems();
				for(int item:newSetCoveredOnly){
					if(this.coverStatus[yIDExp][item] == 1)
						gain++;
				}
				i++;
				j++;
			}
			else{
				// yIDExp < yIDNew, all yIDNew items are tested
				for(int w = 1; w < newItems[j].length; w++){
					if(this.coverStatus[yIDNew][w] == 0)
						gain++;
				}
				j++;
			}
		}
		for(; j< newItems.length; j++){
			int yIDNew = newItems[j][0];
			for(int w = 1; w < newItems[j].length; w++){
				if(this.coverStatus[yIDNew][w] == 0)
					gain++;
			}
		}
		return gain;
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
				for(int w = 0; w< this.coverStatus[yID].length; w++)
					if(coverStatus[yID][w] > 0)
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
						if(coverStatus[yID][item] == 1)
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
		boolean result = removeSet(oldSetID);
		boolean result2 = addSet(newSet, oldSetID);
		return result&result2;
	}
	@Override
	public boolean removeSet(short sID) {
		if(this.selectedFeatures[sID]!=null){
			this.removeOldSet(this.converter.featureToSet_Matrix(this.selectedFeatures[sID]));
			this.selectedFeatures[sID] = null;
			return true;
		}
		return false;
	}
	private boolean removeOldSet(int[][] matrix){
		for(int i = 0; i< matrix.length; i++){
			int qID = matrix[i][0];
			for(int j = 1; j< matrix[i].length; j++){
				int item = matrix[qID][j];
				if(coverStatus[qID][item] >= 1)
					this.coverStatus[qID][item]--;
				else {
					System.out.println("Error in Removing Old Set");
					return false;
				}
			}
		}
		return true;
	}
	@Override
	public boolean addSet(ICoverSet_FeatureWrapper oneSet, short sID) {
		if(this.selectedFeatures[sID]!=null)
			return false;
		else {
			this.selectedFeatures[sID] = oneSet;
			return this.addNewSet(this.converter.featureToSet_Matrix(oneSet));
		}
	}
	private boolean addNewSet(int[][] matrix) {
		for(int i = 0; i< matrix.length; i++){
			int qID = matrix[i][0];
			for(int j = 1; j < matrix[i].length; j++){
				int item = matrix[i][j];
				this.coverStatus[qID][item]++; // increase the coverage of <qID, item> pair
			}
		}
		return true;
	}
	
	private int[][] addNewSetWithReturn(int[][] matrix) {
		int[][] result= new int[matrix.length][];
		int iter = 0;
		
		for(int i = 0; i< matrix.length; i++){
			int qID = matrix[i][0];
			result[i] = new int[matrix[i].length];
			iter = 1;
			
			for(int j = 1; j < matrix[i].length; j++){
				int item = matrix[i][j];
				if(coverStatus[qID][item] == 0)
					result[i][iter++] = item;
				
				this.coverStatus[qID][item]++; // increase the coverage of <qID, item> pair
			}
			if(iter < result[i].length){
				int[] temp = new int[iter];
				for(int w= 0 ; w < iter; w++)
					temp[w] = result[i][w];
				result[i] = temp;
			}
		}
		return result;
	}
	@Override
	public short leastCoverSet(int[] minSize) {
		minSize[0] = Integer.MAX_VALUE;
		short minSetID = (short)-1;
		for(short i = 0; i< this.selectedFeatures.length; i++){
			int[][] items = this.converter.featureToSet_Matrix(selectedFeatures[i]);
			int localCount = getGain(items);
			if(localCount < minSize[0]){
				minSetID = i;
				minSize[0] = localCount;
			}
		}
		return minSetID;
	}
	/**
	 * Get the # of items that matrix covers only 
	 * The input matrix is already selected
	 * @param items
	 * @return
	 */
	private int getGain(int[][] matrix){
		int gain = 0;
		for(int i = 0; i< matrix.length; i++){
			int qID = matrix[i][0];
			for(int j = 1; j< matrix[i].length; j++){
				int item = matrix[i][j];
				// The only cover set of the <qID, item> pair is by the input matrix
				if(this.coverStatus[qID][item] == 1) 
					gain++;
			}
		}
		return gain;
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
		return getGain(newSet, (short)-1);
	}
	@Override
	public boolean addNewSet(ICoverSet_FeatureWrapper newSet) {
		if(this.numOfSets < this.selectedFeatures.length){
			this.selectedFeatures[numOfSets++] = newSet;
			int[][] matrix = this.converter.featureToSet_Matrix(newSet);
			return this.addNewSet(matrix);
		}
		else {
			System.out.println("Error: not enough sapce: use swap instead");
			return false;
		}
	}
	
	@Override
	public int[][] addNewSetWithReturn(ICoverSet_FeatureWrapper newSet) {
		if(this.numOfSets < this.selectedFeatures.length){
			this.selectedFeatures[numOfSets++] = newSet;
			int[][] matrix = this.converter.featureToSet_Matrix(newSet);
			return this.addNewSetWithReturn(matrix);
		}
		else {
			System.out.println("Error: not enough sapce: use swap instead");
			return null;
		}
	}
	
	@Override
	public int getCoveredCount() {
		int totalNum = 0;
		for(int i = 0; i< coverStatus.length; i++)
			for(int j = 0; j< coverStatus[i].length; j++)
				if(coverStatus[i][j] >0) // The entry is covered
					totalNum++;
		return totalNum;
	}
	@Override
	public IFeatureSetConverter getConverter() {
		return this.converter;
	}

	

}
