package edu.psu.chemxseer.structure.subsearch.Interfaces;

public interface GraphResultPref extends GraphResult{
	
	/**
	 * Return the Feature ID of the Prefix Feature
	 * The Prefix FeatureID may be -1
	 * @return
	 */
	public int getPrefixFeatureID();
	
	/**
	 * The Suffix String is represented in DFS code format
	 * Return null if the database graph is exactly the same as its prefix
	 * @return
	 */
	public int[][] getSuffix();
}
