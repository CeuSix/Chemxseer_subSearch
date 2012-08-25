package edu.psu.chemxseer.structure.subsearch.Interfaces;

import java.text.ParseException;

import de.parmol.graph.Graph;

/**
 * A simplified Interface (fronza)
 * @author dayuyuan
 *
 */
public interface IGraphs {
	
	public boolean createGraphs() throws ParseException;
	
	public Graph getGraph(int gID);
	
	public String getLabel(int gID);

	public int getGraphNum();
	//FOR TEST ONLY
	public int getSupport(int j);

}
