package edu.psu.chemxseer.structure.subsearch.Impl;

import java.text.ParseException;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.NoPostingFeatures;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IGraphs;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IOneFeature;

public class Graphs implements IGraphs{
	private NoPostingFeatures features;
	
	public Graphs (NoPostingFeatures features){
		this.features = features;
	}
	
	public int getSupport(int gID){
		return this.features.getFeature(gID).getFrequency();
	}

	@Override
	public boolean createGraphs() throws ParseException {
		return this.features.createGraphs();
	}

	@Override
	public Graph getGraph(int gID) {
		IOneFeature feature = this.features.getFeature(gID);
		if(feature.getFeatureGraph() == null)
			return MyFactory.getDFSCoder().parse(feature.getDFSCode(), MyFactory.getGraphFactory());
		else return feature.getFeatureGraph();
	}

	@Override
	public int getGraphNum() {
		return this.features.getfeatureNum();
	}

	@Override
	public String getLabel(int gID) {
		return this.features.getFeature(gID).getDFSCode();
	}
}
