package edu.psu.chemxseer.structure.subsearch.Interfaces;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.index.CorruptIndexException;

public interface GraphFetcher {
	public static int batchCount = 1000; // at most 1000 graphs are returned in a batch
	/**
	 * Return a list of Graphs with maximum number "maxNum" (depends on the implementation)
	 * Return "null" if no graphs left to return
	 * TimeComponent[1] = DB Loading Time 
	 * @param TimeComponent[1] = DB Loading Time 
	 * @return
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 */
	public List<GraphResult> getGraphs(long[] TimeComponent);
	
	/**
	 * Return the whold list of Graphs from the fetcher
	 * @param TimeComponent
	 * @return
	 */
	public List<GraphResult> getAllGraphs(long[] TimeComponent);
	/**
	 * Get the document IDs of the Graph Fetcher
	 * @return
	 */
	public int[] getIDs();
	/**
	 * Join with another graph fetcher
	 * @param fetcher
	 * @return 
	 */
	public GraphFetcher join(GraphFetcher fetcher);
	
	/**
	 * Remove the Graph Fetcher
	 * @param fetcher
	 * @return
	 */
	public GraphFetcher remove(GraphFetcher fetcher);
	/**
	 * return the number of graphs that will be fetched;
	 * @return
	 */
	public int size();
	
}

