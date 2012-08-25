package edu.psu.chemxseer.structure.setcover.IO;

import edu.psu.chemxseer.structure.setcover.sets.CoverSet_FeatureWrapper;

/**
 * max cover for random input
 * @author dayuyuan
 *
 */
public interface IInputRandom extends IInput {
	public CoverSet_FeatureWrapper getSet(int setID);
}
