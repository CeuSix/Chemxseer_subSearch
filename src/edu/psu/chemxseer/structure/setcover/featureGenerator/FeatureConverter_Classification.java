package edu.psu.chemxseer.structure.setcover.featureGenerator;

import javaewah.EWAHCompressedBitmap;
import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;

public class FeatureConverter_Classification implements IFeatureSetConverter{
	protected int classOneNumber;
	protected int classTwoNumber;
	
	public FeatureConverter_Classification(int classOneCount, int classTwoCount){
		this.classOneNumber = classOneCount;
		this.classTwoNumber = classTwoCount;
	}

	@Override
	public int[] featureToSet_Array(ICoverSet_FeatureWrapper oneFeature) {
		int[] dbSupport = oneFeature.containedDatabaseGraphs();
		int[] dbNoSupport = oneFeature.notContainedDatabaseGraphs(this.classOneNumber);
		int[] querySupport = oneFeature.containedQueryGraphs();
	
		int resultCount = dbSupport.length * (classTwoNumber - querySupport.length) + 
		querySupport.length * dbNoSupport.length;
		int[] results =new int[resultCount];
		int counter = 0;
		
		for(int qID = 0, containedQueryIndex=0; qID < classTwoNumber; qID++){
			boolean isSupportQuery = false;
			int base = qID * this.classOneNumber;
			if(containedQueryIndex < querySupport.length){
				if(qID == querySupport[containedQueryIndex]){
					isSupportQuery = true;
					containedQueryIndex++;
				}
			}
			if(isSupportQuery){
				// Add all not-support database graphs
				for(int i =0; i< dbNoSupport.length; i++)
					results[counter++] = base + dbNoSupport[i];
			}
			else{
				// Add all support database graphs
				for(int i = 0; i< dbSupport.length; i++)
					results[counter++] = base + dbSupport[i];
			}
		}
		return results;
	}
	
	@Override
	public int[][] featureToSet_Matrix(ICoverSet_FeatureWrapper oneFeature) {
		int[] dbSupport = oneFeature.containedDatabaseGraphs();
		int[] dbNoSupport = oneFeature.notContainedDatabaseGraphs(this.classOneNumber);
		int[] querySupport = oneFeature.containedQueryGraphs();
	
		int[][] results =new int[this.classTwoNumber][];
		
		for(int qID = 0, containedQueryIndex=0; qID < classTwoNumber; qID++){
			boolean isSupportQuery = false;
			if(containedQueryIndex < querySupport.length){
				if(qID == querySupport[containedQueryIndex]){
					isSupportQuery = true;
					containedQueryIndex++;
				}
			}
			if(isSupportQuery){
				// Add all not-support database graphs
				results[qID]  = new int[dbNoSupport.length+1];
				results[qID][0] = qID;
				for(int i =0; i< dbNoSupport.length; i++)
					results[qID][i=1] = dbNoSupport[i];
			}
			else{
				// Add all support database graphs
				results[qID] = new int[dbSupport.length+1];
				results[qID][0] = qID;
				for(int i = 0; i< dbSupport.length; i++)
					results[qID][i+1] = dbSupport[i];
			}
		}
		return results;
	}
	
	/**
	 * Return a compressed BitMap
	 * @param oneFeature
	 * @return
	 */
	public EWAHCompressedBitmap FeatureToSet_EWAH(ICoverSet_FeatureWrapper oneFeature){
		int[] dbSupport = oneFeature.containedDatabaseGraphs();
		int[] dbNoSupport = oneFeature.notContainedDatabaseGraphs(this.classOneNumber);
		int[] querySupport = oneFeature.containedQueryGraphs();
	
		int resultCount = dbSupport.length * (classTwoNumber - querySupport.length) + 
		querySupport.length * dbNoSupport.length;
		EWAHCompressedBitmap result = new EWAHCompressedBitmap(resultCount >> 6);
		
		for(int qID = 0, containedQueryIndex=0; qID < classTwoNumber; qID++){
			boolean isSupportQuery = false;
			int base = qID * this.classOneNumber;
			if(containedQueryIndex < querySupport.length){
				if(qID == querySupport[containedQueryIndex]){
					isSupportQuery = true;
					containedQueryIndex++;
				}
			}
			if(isSupportQuery){
				// Add all not-support database graphs
				for(int i =0; i< dbNoSupport.length; i++)
					result.set(base + dbNoSupport[i]);
			}
			else{
				// Add all support database graphs
				for(int i = 0; i< dbSupport.length; i++)
					result.set(base + dbSupport[i]);
			}
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
		int[] dbSupport = oneFeature.containedDatabaseGraphs();
		int[] querySupport = oneFeature.containedQueryGraphs();
	
		
		int resultCount = dbSupport.length * (classTwoNumber - querySupport.length) + 
					querySupport.length * (classOneNumber - dbSupport.length);
		return resultCount;
	}
}
