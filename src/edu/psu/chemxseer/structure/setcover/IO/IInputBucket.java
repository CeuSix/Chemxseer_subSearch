package edu.psu.chemxseer.structure.setcover.IO;

import edu.psu.chemxseer.structure.setcover.sets.CoverSet_FeatureWrapper;

public interface IInputBucket extends IInput{
	/**
	 * Return the total number of buckets
	 * @return
	 */
	public int getBucketCount();
	/**
	 * Given the gain function, append the feature to the end of gain list
	 * @param feature
	 * @param gain
	 * @return
	 */
	public boolean append(CoverSet_FeatureWrapper feature, int gain);
	
	/**
	 * Get the lower bound of "gain" of the bucket with bID
	 * @param bID
	 * @return
	 */
	public int getLowerBound(int bID);
	/**
	 * The input of the "bID"'s bucket
	 * @param bID
	 * @return
	 */
	public IInputSequential getBucketInput(int bID);
}
