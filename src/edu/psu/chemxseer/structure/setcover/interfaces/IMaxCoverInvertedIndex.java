package edu.psu.chemxseer.structure.setcover.interfaces;

import edu.psu.chemxseer.structure.setcover.IO.IInputSequential;
import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter;

/**
 * The interface for the inverted index for the greedy in-memory algorithm
 * This is pretty similar to the linked-list of the cover status
 * @author dayuyuan
 *
 */
public interface IMaxCoverInvertedIndex {
	
	/**
	 * Given the input, create an maxCoverInvertedIndex
	 * @param input
	 * @param converter
	 * @return
	 */
	public boolean create(IInputSequential input, IFeatureSetConverter converter);

	/**
	 * Given the item (q, g) pair, return the sets covering the <q, g> pair
	 * @return
	 */
	public short[] getCoveredSets(int qID, int gID);

}
