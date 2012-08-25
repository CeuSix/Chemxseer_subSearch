package edu.psu.chemxseer.structure.setcover.sets;

import java.io.Serializable;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.OneFeatureMultiClass;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IOneFeature;
import edu.psu.chemxseer.structure.util.IntersectionSet;

/**
 * Just an Wrapper for OneFeatureMultiClass(WithPosting)
 * @author dayuyuan
 *
 */
public class CoverSet_FeatureWrapper implements ICoverSet_FeatureWrapper, IOneFeature, Serializable{
	
	private static final long serialVersionUID = 5671593532323406193L;
	
	private OneFeatureMultiClass feature;
	private int[][] postings;
	
	public CoverSet_FeatureWrapper(CoverSet_FeatureWrapper2 oneF) {
			this.feature= (OneFeatureMultiClass) oneF.getFeature();
			this.postings = oneF.getPosting();
	}
	public CoverSet_FeatureWrapper(OneFeatureMultiClass feature2,
			int[][] thePostings) {
		this.feature = feature2;
		this.postings = thePostings;
	}
	/**
	 * Return the posting of the Feature
	 * @return
	 */
	public int[][] getPostings(){
		return this.postings;
	}

	@Override
	public boolean isSelected() {
		return this.feature.isSelected();
	}

	@Override
	public void setSelected() {
		this.feature.setSelected();
	}

	@Override
	public void setUnselected() {
		this.feature.setUnselected();
	}

	@Override
	public Graph getFeatureGraph() {
		return this.feature.getFeatureGraph();
	}

	@Override
	public void creatFeatureGraph(int gID) {
		this.feature.creatFeatureGraph(gID);
	}

	@Override
	public String getDFSCode() {
		return this.feature.getDFSCode();
	}

	@Override
	public int getFrequency() {
		return this.feature.getFrequency(); // This generally does not make much sense
		
	}

	@Override
	public void setFrequency(int frequency) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public long getPostingShift() {
		// DO Nothing
		return 0;
	}

	@Override
	public void setPostingShift(long shift) {
		//do nothing
	}

	@Override
	public int getFeatureId() {
		return this.feature.getFeatureId();
	}

	@Override
	public void setFeatureId(int id) {
		this.feature.setFeatureId(id);
	}

	@Override
	public String toFeatureString() {
		return this.feature.toFeatureString();
	}
	
	/**
	 * Return all the database graphs, containing the feature
	 * @return
	 */
	public int[] containedDatabaseGraphs(){
		return this.postings[0];
	}
	
	/**
	 * Return all the query graphs, containing the feature
	 * @return
	 */
	public int[] containedQueryGraphs(){
		return this.postings[2];
	}
	
	/**
	 * Given the total number of database graphs, return the database graphs
	 * not containing the feature
	 * @param totalDBGraphs
	 * @return
	 */
	public int[] notContainedDatabaseGraphs(int totalDBGraphs){
		int[] result = IntersectionSet.getCompleteSet(this.postings[0], totalDBGraphs);
		return result;
	}
	
	/**
	 * Given the total number of queries, return the query graphs 
	 * not containing the feature
	 * @param totalQueryGraphs
	 * @return
	 */
	public int[] notContainedQueryGraphs(int totalQueryGraphs){
		int[] result = IntersectionSet.getCompleteSet(this.postings[2], totalQueryGraphs);
		return result;
	}
	
	/**
	 * Return all the database graphs, isomorphic to the feature
	 * @return
	 */
	public int[] getEquavalentDatabaseGraphs(){
		return this.postings[1];
	}
	
	/**
	 * Return all the query graphs, isomorphic to the features
	 * @return
	 */
	public int[] getEquavalentQueryGraphs(){
		return this.postings[3];
	}
	
	/**
	 * Return the underlying feature of the coverSet
	 * @return
	 */
	public OneFeatureMultiClass getFeature(){
		return this.feature;
	}

	public int getFetureID() {
		return this.getFeatureId();
	}
}
