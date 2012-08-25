package edu.psu.chemxseer.structure.setcover.featureGenerator;

import javaewah.EWAHCompressedBitmap;
import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;

/**
 * Generate the input file for the set-cover feature selection methods 
 * (1) Subgraph Search Feature Selection
 * (2) SuperGraph Search Feature Selection
 * (3) Graph Classification Feature Selection
 * @author dayuyuan
 *
 */
public interface IFeatureSetConverter {
	public static enum FeatureSetType{
		subSearch, supSearch, classification
	}
	
	/**
	 * Given the Set Type, return EWAHCompressedBitmap as representation
	 * Return null if not bit is set
	 * @param oneFeature
	 * @param type
	 * @return
	 */
	public EWAHCompressedBitmap FeatureToSet_EWAH(ICoverSet_FeatureWrapper oneFeature);
	
	/**
	 * Given the Set Type, return An array of items contained by the feature
	 * @param aFeature
	 * @return
	 */
	public int[] featureToSet_Array(ICoverSet_FeatureWrapper aFeature);
	
	/**
	 * Return the matrix representation of the set
	 * The row ID need to be sorted
	 * M[i][0] denotes the qID
	 * M[i][j > 0] denotes the gID [ordered]
	 * @param aFeature
	 * @return
	 */
	public int[][] featureToSet_Matrix(ICoverSet_FeatureWrapper aFeature);
	
	
	public int getCountOneNumber();
	
	public int getCountTwoNumber();
	
	public int getUniverseCount();
	
	public int getItemCount(ICoverSet_FeatureWrapper oneFeature);



}
