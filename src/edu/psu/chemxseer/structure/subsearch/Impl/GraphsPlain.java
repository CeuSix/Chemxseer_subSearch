package edu.psu.chemxseer.structure.subsearch.Impl;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IGraphs;

public class GraphsPlain implements IGraphs{
	private String[] graphsString;
	private Graph[] graphs;
	private boolean graphExist;
	
	public GraphsPlain(String[] graphString){
		this.graphsString = graphString;
		this.graphExist = false;
	}
	


	@Override
	public boolean createGraphs() throws ParseException {
		if(graphExist)
			return false;
		else{
			this.graphs = new Graph[graphsString.length];
			int index = 0;
			for(String oneS: graphsString)
				graphs[index++] = (MyFactory.getDFSCoder().parse(oneS, 
						MyFactory.getGraphFactory()));	
			graphExist = true;
			return true;
		}
	}

	@Override
	public Graph getGraph(int gID) {
		if(gID < 0 || gID >= this.graphsString.length)
			return null;
		if(!this.graphExist)
			return MyFactory.getDFSCoder().parse(this.graphsString[gID],
					MyFactory.getGraphFactory());
		else return graphs[gID];
	}

	@Override
	public int getGraphNum() {
		return this.graphsString.length;
	}

	@Override
	public int getSupport(int j) {
		//Not Implemented
		return -1;
	}



	@Override
	public String getLabel(int gID) {
		return this.graphsString[gID];
	}

}
