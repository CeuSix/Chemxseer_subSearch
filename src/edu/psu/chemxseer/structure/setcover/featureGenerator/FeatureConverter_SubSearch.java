package edu.psu.chemxseer.structure.setcover.featureGenerator;

import javaewah.EWAHCompressedBitmap;
import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;

public class FeatureConverter_SubSearch implements IFeatureSetConverter{
	protected int classOneNumber;
	protected int classTwoNumber;
	
	/**
	 * Construct a SubSearch Feature Converter
	 * @param classOneCount
	 * @param classTwoCount
	 */
	public FeatureConverter_SubSearch(int classOneCount, int classTwoCount){
		this.classOneNumber = classOneCount;
		this.classTwoNumber = classTwoCount;
	}
	
	/**
	 * Return compressed bit map
	 * Return null if the no bit is set during the procedure
	 * @param oneFeature
	 * @return
	 */
	public EWAHCompressedBitmap FeatureToSet_EWAH(ICoverSet_FeatureWrapper oneFeature) {
		int[] dbNoSupport = oneFeature.notContainedDatabaseGraphs(this.classOneNumber);
		int[] querySupport = oneFeature.containedQueryGraphs();
		int[] queryEqual = oneFeature.getEquavalentQueryGraphs();

		int resultCount = querySupport.length * dbNoSupport.length + this.classOneNumber * queryEqual.length;
		EWAHCompressedBitmap result = new EWAHCompressedBitmap(resultCount >>6);
		boolean empty = true;
		
		// Add Items
		int containIndex = 0;
		int equalIndex = 0;
		while(containIndex < querySupport.length && equalIndex < queryEqual.length){
			// choose the smaller item of containedQ and equalQ
			int containedQ = querySupport[containIndex];
			int equalQ = queryEqual[equalIndex];
			if(containedQ < equalQ){
				// Add the filtered db graphs
				int base = classOneNumber * containedQ ;
				for(int j =0; j< dbNoSupport.length; j++){
					int gID = dbNoSupport[j];
					result.set(base + gID);
					empty = false;
				}
				containIndex++;
			}
			else{
				// all can be filtered, no need for verification
				int base = classOneNumber * equalQ ;
				for(int gID = 0; gID < classOneNumber; gID++){
					result.set(base + gID); 
					empty = false;
				}
				equalIndex++;
			}
		}
		// Filtered Graphs:
		while(containIndex<querySupport.length){
			int base = classOneNumber * querySupport[containIndex] ;
			for(int j =0; j< dbNoSupport.length; j++){
				int gID = dbNoSupport[j];
				empty = false;
				result.set(base + gID);
			}
			containIndex++;
		}
		// Direct Answer Graphs:
		while(equalIndex < queryEqual.length){
			int base = classOneNumber * queryEqual[equalIndex] ;
			for(int gID = 0; gID < classOneNumber; gID++){
				result.set(base + gID); 
				empty = false;
			}
			equalIndex++;
		}
		if(empty == false)
			return result;
		else return null;
	}
	
	@Override
	public int[][] featureToSet_Matrix(ICoverSet_FeatureWrapper oneFeature) {
		int[] dbNoSupport = oneFeature.notContainedDatabaseGraphs(this.classOneNumber);
		int[] querySupport = oneFeature.containedQueryGraphs();
		int[] queryEqual = oneFeature.getEquavalentQueryGraphs();

		int[][] result = new int[querySupport.length + queryEqual.length][];
		// Add Items
		int containIndex = 0;
		int equalIndex = 0;
		int iter = 0;
		while(containIndex < querySupport.length && equalIndex < queryEqual.length){
			// choose the smaller item of containedQ and equalQ
			int containedQ = querySupport[containIndex];
			int equalQ = queryEqual[equalIndex];
			if(containedQ < equalQ){
				// Add the filtered db graphs
				result[iter] = new int[dbNoSupport.length+1];
				result[iter][0] = containedQ ;
				for(int j =0; j< dbNoSupport.length; j++){
					int gID = dbNoSupport[j];
					result[iter][j+1] = gID;
				}
				containIndex++;
				iter++;
			}
			else{
				// all can be filtered, no need for verification
				result[iter] = new int[classOneNumber+1];
				result[iter][0] = equalQ;
				for(int gID = 0; gID < classOneNumber; gID++){
					result[iter][gID+1] = gID; 
				}
				equalIndex++;
				iter++;
			}
		}
		// Filtered Graphs:
		while(containIndex<querySupport.length){
			result[iter] = new int[dbNoSupport.length+1];
			result[iter][0] = querySupport[containIndex] ;
			for(int j =0; j< dbNoSupport.length; j++){
				int gID = dbNoSupport[j];
				result[iter][j+1] = gID;
			}
			containIndex++;
			iter++;
		}
		// Direct Answer Graphs:
		while(equalIndex < queryEqual.length){
			result[iter] = new int[classOneNumber+1];
			result[iter][0] = queryEqual[equalIndex];
			for(int gID = 0; gID < classOneNumber; gID++){
				result[iter][gID+1] = gID; 
			}
			equalIndex++;
			iter++;
		}
		return result;
	}

	@Override
	public int[] featureToSet_Array(ICoverSet_FeatureWrapper oneFeature) {
		int[] dbNoSupport = oneFeature.notContainedDatabaseGraphs(this.classOneNumber);
		int[] querySupport = oneFeature.containedQueryGraphs();
		int[] queryEqual = oneFeature.getEquavalentQueryGraphs();

		int resultCount = querySupport.length * dbNoSupport.length + 
		this.classOneNumber * queryEqual.length;
		int[] result = new int[resultCount];
		int counter = 0;
		// Add Items
		int containIndex = 0;
		int equalIndex = 0;
		while(containIndex < querySupport.length && equalIndex < queryEqual.length){
			// choose the smaller item of containedQ and equalQ
			int containedQ = querySupport[containIndex];
			int equalQ = queryEqual[equalIndex];
			if(containedQ < equalQ){
				// Add the filtered db graphs
				int base = classOneNumber * containedQ ;
				for(int j =0; j< dbNoSupport.length; j++){
					int gID = dbNoSupport[j];
					result[counter++] = base + gID;
				}
				containIndex++;
			}
			else{
				// all can be filtered, no need for verification
				int base = classOneNumber * equalQ ;
				for(int gID = 0; gID < classOneNumber; gID++){
					result[counter++] = base + gID; 
				}
				equalIndex++;
			}
		}
		// Filtered Graphs:
		while(containIndex<querySupport.length){
			int base = classOneNumber * querySupport[containIndex] ;
			for(int j =0; j< dbNoSupport.length; j++){
				int gID = dbNoSupport[j];
				result[counter++] = base + gID;
			}
			containIndex++;
		}
		// Direct Answer Graphs:
		while(equalIndex < queryEqual.length){
			int base = classOneNumber * queryEqual[equalIndex] ;
			for(int gID = 0; gID < classOneNumber; gID++){
				result[counter++] = base + gID; 
			}
			equalIndex++;
		}
		return result;
	}
	
	@Override
	public int getCountOneNumber() {
		return this.classOneNumber;
	}

	@Override
	public int getCountTwoNumber() {
		return this.classTwoNumber;
	}
	@Override
	public int getUniverseCount() {
		return this.classOneNumber * this.classTwoNumber;
	}
	@Override
	public int getItemCount(ICoverSet_FeatureWrapper oneFeature) {
		int[] dbNoSupport = oneFeature.notContainedDatabaseGraphs(this.classOneNumber);
		int[] querySupport = oneFeature.containedQueryGraphs();
		int[] queryEqual = oneFeature.getEquavalentQueryGraphs();

		int resultCount = querySupport.length * dbNoSupport.length + 
		this.classOneNumber * queryEqual.length;
		return resultCount;
	}

	
}
