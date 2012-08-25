package edu.psu.chemxseer.structure.subsearch.Interfaces;

import java.util.List;

import de.parmol.graph.Graph;

public interface IndexSearcherLucene {
	/**
	 * Given the query graph "query", return all the subgraphs feature IDs contained in 
	 * "query"
	 * @param query
	 * @return TimeComponent[2], index lookup time
	 */
	public List<Integer> subgraphs(Graph query, long TimeComponent[]);
	
	/**
	 * Return the index feature IDs of all the index features
	 * @return
	 */
	public int[] getAllFeatureIDs();
}
