package edu.psu.chemxseer.structure.subsearch.Interfaces;

import de.parmol.graph.Graph;

/**
 * The graph results interface: 
 * Can return the graph and also its index
 * @author dayuyuan
 *
 */
public interface GraphResult extends Comparable<GraphResult>{
	/**
	 * The Graph
	 * @return
	 */
	public Graph getG();
	/**
	 * The real graph ID
	 * @return
	 */
	public int getID();
	/**
	 * Internal Used Only
	 * @return
	 */
	public int getDocID();
	
	
}
