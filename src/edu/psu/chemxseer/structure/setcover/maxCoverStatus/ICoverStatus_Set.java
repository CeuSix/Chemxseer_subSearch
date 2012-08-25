package edu.psu.chemxseer.structure.setcover.maxCoverStatus;

import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter;

/**
 * The interface for the max cover status that record the whole universe information
 * @author dayuyuan
 *
 */
public interface ICoverStatus_Set extends ICoverStatus {
	
	/**
	 * Get the feature converter used in the coverset status
	 * @return
	 */
	public IFeatureSetConverter getConverter();
}
