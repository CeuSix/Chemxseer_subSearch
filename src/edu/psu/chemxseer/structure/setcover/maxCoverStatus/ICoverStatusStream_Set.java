package edu.psu.chemxseer.structure.setcover.maxCoverStatus;

import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter;

/**
 * The IMaxCoverStatusStream interface when the universe is stored.
 * @author dayuyuan
 *
 */
public interface ICoverStatusStream_Set extends ICoverStatusStream {

	/**
	 * Get the feature converter used in the coverset status
	 * @return
	 */
	public IFeatureSetConverter getConverter();
}
