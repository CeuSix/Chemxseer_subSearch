package edu.psu.chemxseer.structure.supersearch.CIndex;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.iso.FastSU;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IndexSearcherLucene;

public class CIndexTree implements IndexSearcherLucene {
	
	protected CIndexTreeNode rootFeature;
	protected int featureCount;
	protected Map<Integer, CIndexTreeNode> distinctFeatures;
	
	public CIndexTree(CIndexTreeNode rootNode, int featureCount){
		this.rootFeature = rootNode;
		this.featureCount = featureCount;
	}
	
	
	public List<Integer> getNoSubgraphs(Graph query, long[] TimeComponent) {
		long startTime = System.currentTimeMillis();
		List<Integer> results = new ArrayList<Integer>();
		CIndexTreeNode theNode  = this.rootFeature;
		FastSU iso = new FastSU();
		while(theNode!=null){
			// test if the node is contained in the query
			boolean temp = iso.isIsomorphic(theNode.getGraph(), query);
			if(temp){
				theNode = theNode.getLeft(); //Contained Features
			}
			else {
				results.add(theNode.getFID());
				theNode = theNode.getRight(); //Not Contained Features
			}
		}
		TimeComponent[2] += System.currentTimeMillis()-startTime;
		return results;
	}


	@Override
	public List<Integer> subgraphs(Graph query, long[] TimeComponent) {
		long startTime = System.currentTimeMillis();
		if(this.distinctFeatures == null)
			createDistinctFeatures();
		List<Integer> results = new ArrayList<Integer>();
		FastSU iso = new FastSU();
		for(CIndexTreeNode oneNode: this.distinctFeatures.values()){
			boolean temp = iso.isIsomorphic(oneNode.getGraph(), query);
			if(temp)
				results.add(oneNode.getFID());
		}
		TimeComponent[2] += System.currentTimeMillis()-startTime;
		return results;
	}

	private void createDistinctFeatures(){
		this.distinctFeatures = new HashMap<Integer, CIndexTreeNode>();
		if(this.rootFeature!=null)
			this.rootFeature.insertToHash(this.distinctFeatures);
		
	}
	public int getFeatureCount() {
		return this.featureCount;
	}
	public int getDistinctFeatureCount(){
		if(this.distinctFeatures == null)
			createDistinctFeatures();
		return this.distinctFeatures.size();
	}


	@Override
	public int[] getAllFeatureIDs() {
		if(this.distinctFeatures ==null)
			createDistinctFeatures();
		int[] rest = new int[distinctFeatures.size()];
		int iter = 0;
		for(Integer key:  this.distinctFeatures.keySet())
			rest[iter++] = key;
		return rest;
	}

}
