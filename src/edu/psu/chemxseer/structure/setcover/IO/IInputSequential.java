package edu.psu.chemxseer.structure.setcover.IO;

import java.util.List;

import edu.psu.chemxseer.structure.setcover.sets.CoverSet_FeatureWrapper;

/**
 * Interface for sequential input
 * @author dayuyuan
 *
 */
public interface IInputSequential extends IInput
{	
	/**
	 * Return the Next Sets in the Sequence
	 * @return next CoverSet_FeatureWrapper
	 */
	public CoverSet_FeatureWrapper nextSet();
	
	public List<CoverSet_FeatureWrapper> nextSets(int batchNum);
	/**
	 * Get back to the start of the sequence input
	 */
	public void reWind();
	/**
	 * Given the set of selected features,
	 * Store the CoverSet_FeatureWrapper to Disk
	 * @param selectedFeatureIDs
	 * @param selectedFeatureFile
	 */
	public void storeSelected(int[] selectedFeatureIDs, String selectedFeatureFile);

	/**
	 * Return the total number of sets 
	 * @return
	 */
	public int getSetCount();
	
}
