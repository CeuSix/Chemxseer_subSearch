package edu.psu.chemxseer.structure.subsearch.Impl;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;

/**
 * The normal implementation of gGraphResults containing 
 * one graph and one graph ID.
 * @author dayuyuan
 *
 */
public class GraphResultNormal implements GraphResult{
	protected int gID;
	protected Graph g;
	
	public GraphResultNormal(int ID, Graph g){
		this.gID = ID;
		this.g = g;
	}

	@Override
	public Graph getG() {
		return g;
	}

	@Override
	public int getID() {
		return gID;
	}
	@Override
	public int getDocID() {
		return this.gID;
	}
	
	public String toString(){
		return (new Integer(gID)).toString();
	}
	
	public int compareTo(GraphResult o) {
		int id1 = this.getID();
		int id2 = o.getID();
		if(id1 < id2)
			return -1;
		else if(id1 == id2)
			return 0;
		else return 1;
	}
	
}
