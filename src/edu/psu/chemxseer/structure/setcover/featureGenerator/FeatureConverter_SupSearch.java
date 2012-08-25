package edu.psu.chemxseer.structure.setcover.featureGenerator;

import javaewah.EWAHCompressedBitmap;
import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;

public class FeatureConverter_SupSearch implements IFeatureSetConverter{
	protected int classOneNumber;
	protected int classTwoNumber;

	public FeatureConverter_SupSearch(int classOneCount, int classTwoCount) {
		this.classOneNumber = classOneCount;
		this.classTwoNumber = classTwoCount;
	}


	@Override
	public int[] featureToSet_Array(ICoverSet_FeatureWrapper oneFeature) {
		int[] dbSupport = oneFeature.containedDatabaseGraphs();
		int[] noQuerySupport = oneFeature
				.notContainedQueryGraphs(this.classTwoNumber);

		int resultCount = noQuerySupport.length * dbSupport.length;
		int[] results = new int[resultCount];
		int counter = 0;
		for (int i = 0; i < noQuerySupport.length; i++) {
			int qID = noQuerySupport[i];
			int base = qID * this.classOneNumber;
			for (int j = 0; j < dbSupport.length; j++){
				results[counter++] = base + dbSupport[j];
			}
		}
		return results;
	}
	

	@Override
	public int[][] featureToSet_Matrix(ICoverSet_FeatureWrapper oneFeature) {
		int[] dbSupport = oneFeature.containedDatabaseGraphs();
		int[] noQuerySupport = oneFeature
				.notContainedQueryGraphs(this.classTwoNumber);

		int[][] results = new int[noQuerySupport.length][dbSupport.length+1];
		for (int i = 0; i < noQuerySupport.length; i++) {
			int qID = noQuerySupport[i];
			results[i][0] = qID;
			for (int j = 0; j < dbSupport.length; j++){
				results[i][j+1] = dbSupport[j];
			}
		}
		return results;
	}
	
	/**
	 * Return the Compressed Bit Map
	 * Return null if not bit is set
	 * @param oneFeature
	 * @return
	 */
	public EWAHCompressedBitmap FeatureToSet_EWAH(
			ICoverSet_FeatureWrapper oneFeature) {

		int[] dbSupport = oneFeature.containedDatabaseGraphs();
		int[] noQuerySupport = oneFeature
				.notContainedQueryGraphs(this.classTwoNumber);
		
		int resultCount = noQuerySupport.length * dbSupport.length;
		EWAHCompressedBitmap result = new EWAHCompressedBitmap(resultCount >> 6);
		boolean empty = true;
		for (int i = 0; i < noQuerySupport.length; i++) {
			int qID = noQuerySupport[i];
			int base = qID * this.classOneNumber;
			for (int j = 0; j < dbSupport.length; j++){
				result.set(base + dbSupport[j]);
				empty = false;
			}
		}
		if(empty == false)
			return result;
		else return null;
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
		int[] dbSupport = oneFeature.containedDatabaseGraphs();
		int[] noQuerySupport = oneFeature
				.notContainedQueryGraphs(this.classTwoNumber);

		int resultCount = noQuerySupport.length * dbSupport.length;
		return resultCount;
	}
}
