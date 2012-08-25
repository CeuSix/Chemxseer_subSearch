package edu.psu.chemxseer.structure.setcover.maxCoverStatus;

import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;

/**
 * The Interface for the stream max-coverage status
 * With a record of "K" features
 * @author dayuyuan
 *
 */
public interface ICoverStatusStream extends ICoverStatus{
	
	/**
	 * Calculate the Gain Function of "oneSet", given all selected set except for "exceptFeature"
	 * @param oneSet
	 * @param exceptSetID
	 * @return
	 */
	public int getGain(ICoverSet_FeatureWrapper oneSet, short exceptSetID);
	
	/**
	 * Return the number of pairs that has been covered for each row, except for minSetID
	 * Using all the selected features, except for the minimum features
	 * @param minSetID
	 * @yIDs: asking about yIDs
	 * @return
	 */
	public int[] getCoveredCountExceptMin(short minSetID, int[] yIDs);
	
	
	
	/**
	 * Swap the old set with ID (oldSetID), with a new set "newSet"
	 * @param oldSetID
	 * @param newSet
	 * @return
	 */
	public boolean swap(short oldSetID, ICoverSet_FeatureWrapper newSet);
	

	/**
	 * Remove the Feature
	 * @param sID
	 * @return
	 */
	public boolean removeSet(short sID);
	
	/**
	 * Add a new Feature with "fID"
	 * @param oneSet
	 * @param sID
	 * @return 
	 */
	public boolean addSet(ICoverSet_FeatureWrapper oneSet, short sID);
	
	
	/**
	 * Among the current selected K features
	 * Return the one, covering the minimum number of items
	 * @param minSize
	 * @return
	 */
	public short leastCoverSet( int[] minSize);
	
	
	/**
	 * Return the K selected Features
	 * @return
	 */
	public ICoverSet_FeatureWrapper[] getSelectedSets();
	
	/**
	 * Get the Kth selected sets
	 * @param minSetID
	 * @return
	 */
	public ICoverSet_FeatureWrapper getSelectedSet(short minSetID);
	
}
