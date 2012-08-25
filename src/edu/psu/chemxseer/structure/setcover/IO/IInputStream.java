package edu.psu.chemxseer.structure.setcover.IO;

import edu.psu.chemxseer.structure.setcover.sets.CoverSet_FeatureWrapper;

/**
 * Interface for Streaming Input
 * @author dayuyuan
 *
 */
public interface IInputStream extends IInput{
	public CoverSet_FeatureWrapper nextSet();
	public boolean pruneBranch();
}
