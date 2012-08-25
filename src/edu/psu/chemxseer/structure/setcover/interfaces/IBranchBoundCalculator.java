package edu.psu.chemxseer.structure.setcover.interfaces;

import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;


public interface IBranchBoundCalculator {
	public int getUpperBound(ICoverSet_FeatureWrapper oneFeature, short minFeatureID);
}
