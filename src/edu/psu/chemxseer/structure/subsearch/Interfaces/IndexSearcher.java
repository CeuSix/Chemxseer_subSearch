package edu.psu.chemxseer.structure.subsearch.Interfaces;

import java.util.List;

import de.parmol.graph.Graph;

/**
 * In charge of searching the Index
 * @author duy113
 *
 */
public interface IndexSearcher extends IndexSearcherLucene {
	/**
	 * Given the query graph "query", return all the maximum subgraphs (for filtering)
	 * feature IDs.
	 * @param query
	 * @return TimeComponent[2], index lookup time
	 */
	public List<Integer> maxSubgraphs(Graph query, long TimeComponent[]);

	
	/**
	 * Given the query graph "query", return the designed subgraphs
	 * For example, FGindex return TCFG feature
	 * Gindex/Lindex return the exact matching feature
	 * 
	 * @param query
	 * @param TimeComponent[2], index lookup time
	 * @return
	 */
	public int designedSubgraph(Graph query, boolean[] exactMatch, long[] TimeComponent);

}
