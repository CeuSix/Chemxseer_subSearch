package edu.psu.chemxseer.structure.setcover.maxCoverStatus;

import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter;
import edu.psu.chemxseer.structure.setcover.sets.CoverSet_FeatureWrapper_Priority;
import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;

/**
 * One Implementation of ICoverStatus_Set, where the status is recorded by matrix of boolean
 * @author dayuyuan
 *
 */
public class Status_BooleanMatrix implements ICoverStatus_Set{
	// Data member denoting the coverage status of each items, given the total number of 
	// possible items.
	protected boolean[][] coverStatus;
	protected IFeatureSetConverter converter;
	
	public Status_BooleanMatrix(IFeatureSetConverter converter){
		this.coverStatus = new boolean[converter.getCountTwoNumber()][converter.getCountOneNumber()];
		for(int i = 0; i< coverStatus.length; i++)
			for(int j =0; j< coverStatus[i].length; j++)
				coverStatus[i][j] = false;
		this.converter = converter;
	}
	
	@Override
	public int getGain(ICoverSet_FeatureWrapper newSet) {
		int gain = 0;// Number of uncovered items covered by aSet
		int[][] items = converter.featureToSet_Matrix(newSet);
		for(int i = 0; i< items.length; i++){
			int yID = items[i][0];
			for(int j = 0; j< items[i].length; j++)
				if(coverStatus[yID][items[i][j]])
					continue;
				else gain++;
		}
		return gain;
	}

	@Override
	public boolean addNewSet(ICoverSet_FeatureWrapper newSet) {
		int[][] items = converter.featureToSet_Matrix(newSet);
		for(int i = 0; i< items.length; i++){
			int yID = items[i][0];
			for(int j = 0; j< items[i].length; j++)
				coverStatus[yID][items[i][j]] = true;
		}
		return true;
	}
	
	@Override
	public int[][] addNewSetWithReturn(ICoverSet_FeatureWrapper newSet) {
		int[][] items = converter.featureToSet_Matrix(newSet);
		int[][] result = new int[items.length][];
		int iter = 0;
		for(int i = 0; i< items.length; i++){
			int yID = items[i][0];
			result[i] = new int[items[i].length];
			result[i][0] = yID;
			iter = 1;
			for(int j = 0; j< items[i].length; j++){
				int xID = items[i][j];
				if(coverStatus[yID][xID] == true)
					continue;
				else {
					coverStatus[yID][xID] = true;
					result[i][iter++] = xID;
				}
			}
			if(iter < result[i].length){
				int[] temp = new int[iter];
				for(int w = 0; w < iter; w++)
					temp[w] = result[i][w];
				result[i] = temp; // save space
			}
		}
		return result;
	}

	@Override
	public int getCoveredCount() {
		int count= 0; 
		for(int i = 0; i< this.coverStatus.length; i++)
			for(int j = 0; j< this.coverStatus[i].length; j++)
				if(coverStatus[i][j])
					count++;
		return count;
	}

	@Override
	public IFeatureSetConverter getConverter() {
		return this.converter;
	}



}
