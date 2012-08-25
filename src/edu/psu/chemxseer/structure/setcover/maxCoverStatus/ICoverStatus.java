package edu.psu.chemxseer.structure.setcover.maxCoverStatus;

import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter;
import edu.psu.chemxseer.structure.setcover.sets.CoverSet_FeatureWrapper_Priority;
import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;


/**
 * The Interface for the Status of the max-cover status
 * For In-memory & On-disk Model
 * @author dayuyuan
 *
 */
public interface ICoverStatus {
	/**
	 * Given a new max-cover set, calculate the # of currently uncovered items
	 * that can be covered by the newSet
	 * @param newSet
	 * @return: the count
	 */
	public int getGain(ICoverSet_FeatureWrapper newSet);
	/**
	 * Given a new max-cover set, add the new set as selected
	 * @param newSet
	 * @return
	 */
	public boolean addNewSet(ICoverSet_FeatureWrapper newSet);
	/**
	 * Given a new max-cover set, add the new set as selected, 
	 * and return the newly covered items in "matrix" format
	 * @param nextSet
	 * @return
	 */
	public int[][] addNewSetWithReturn(ICoverSet_FeatureWrapper nextSet);
	/**
	 * Get the total Number of covered items
	 * @return
	 */
	public int getCoveredCount();
	
	
}
