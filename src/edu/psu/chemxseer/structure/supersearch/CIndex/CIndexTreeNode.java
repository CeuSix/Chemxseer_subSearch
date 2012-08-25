package edu.psu.chemxseer.structure.supersearch.CIndex;

import java.util.Map;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.preprocess.MyFactory;

/**
 * The structure of the CIndexTopDown uses a BinaryTree
 * @author dayuyuan
 *
 */
public class CIndexTreeNode {
	private String featureLabel;
	
	private CIndexTreeNode left;
	private CIndexTreeNode right;
	
	private int fID;// feature ID: not unique, for indexing 
	private int nID;// nodeID: unique ID
	
	public CIndexTreeNode(String aLine){
		String[] tokens = aLine.split(",");
		this.nID = Integer.parseInt(tokens[0]);
		this.fID = Integer.parseInt(tokens[1]);
		this.featureLabel =  tokens[2];
	}
	public CIndexTreeNode(int nID, String featureLabel, int fID){
		this.nID = nID;
		this.featureLabel = featureLabel;
		this.fID = fID;
	}

	/**
	 * @return the featureLabel
	 */
	public String getFeatureLabel() {
		return featureLabel;
	}


	/**
	 * @return the left
	 */
	public CIndexTreeNode getLeft() {
		return left;
	}

	/**
	 * @return the right
	 */
	public CIndexTreeNode getRight() {
		return right;
	}

	/**
	 * @param featureLabel the featureLabel to set
	 */
	public void setFeatureLabel(String featureLabel) {
		this.featureLabel = featureLabel;
	}

	/**
	 * @param left the left to set
	 */
	public void setLeft(CIndexTreeNode left) {
		this.left = left;
	}

	/**
	 * @param right the right to set
	 */
	public void setRight(CIndexTreeNode right) {
		this.right = right;
	}
	
	public String toNodeString(){
		return this.nID + "," + this.fID + "," + this.featureLabel;
	}
	
	public Graph getGraph() {
		return MyFactory.getDFSCoder().parse(this.featureLabel, MyFactory.getGraphFactory());
	}
	public int getFID() {
		return fID;
	}
	
	public void setFID(int id){
		this.fID = id;
	}
	
	/**
	 * Insert <FID, This>  to the input HashMap
	 * @param distinctFeatures
	 */
	public void insertToHash(Map<Integer, CIndexTreeNode> distinctFeatures) {
		if(this.left!=null)
			this.left.insertToHash(distinctFeatures);
		if(this.right!=null)
			this.right.insertToHash(distinctFeatures);
		distinctFeatures.put(this.fID, this);
	}
	
}
