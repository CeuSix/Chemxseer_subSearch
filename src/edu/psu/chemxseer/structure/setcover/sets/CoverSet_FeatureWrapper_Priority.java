package edu.psu.chemxseer.structure.setcover.sets;

import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.OneFeatureMultiClass;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IOneFeature;
import edu.psu.chemxseer.structure.util.HashID;

/**
 * The Cover Set for the In-Memory Input:
 * has one ID, one key, record to show whether it is deleted
 * Basically for the priority queue
 * The CoverSet_FeatureWrapper_Priority can be put into an priority queue
 * @author dayuyuan
 *
 */
public class CoverSet_FeatureWrapper_Priority implements HashID, 
		Comparable<CoverSet_FeatureWrapper_Priority>, ICoverSet_FeatureWrapper{
	private ICoverSet_FeatureWrapper coverSet;
	private int key;
	private int id;
	private boolean deleted;

	/**
	 * Given a feature & initial score of the feature and a un stable ID, 
	 * construct a CoverSet_FeatureWrapper_Priority Object
	 * @param set
	 * @param intialKey
	 * @param ID: changes over time
	 */
	public CoverSet_FeatureWrapper_Priority(ICoverSet_FeatureWrapper set, int initialKey, int ID){
		this.coverSet = set;
		this.key = initialKey;
		this.id = ID;
		this.deleted = false;
	}
	@Override
	public int compareTo(CoverSet_FeatureWrapper_Priority o) {
		if(this.key < o.key)
			return -1;
		else if(this.key == o.key)
			return 0;
		else return 1;
	}

	@Override
	// unstabled ID, changes over time
	public int getID() {
		return this.id;
	}

	@Override
	// unstabled ID, changes over time
	public void setID(int id) {
		this.id = id;
	}
	/**
	 * Return the score of this feature
	 * @return
	 */
	public int getValue() {
		return this.key;
	}
	/**
	 * decrease value -1
	 */
	public void decraseValue() {
		if(this.key == 0)
			System.out.println("Error: can not decrase value, already 0");
		else
			--this.key;
	}
	/**
	 * Return true if this set is deleted, or false if not
	 * @return
	 */
	public boolean isDeleted() {
		return this.deleted;
	}
	/**
	 * delte this feature
	 */
	public void delete(){
		this.deleted = true;
	}
	
	
	@Override
	public int[] containedDatabaseGraphs() {
		return this.coverSet.containedDatabaseGraphs();
	}
	@Override
	public int[] containedQueryGraphs() {
		return this.coverSet.containedQueryGraphs();
	}
	@Override
	public int[] notContainedDatabaseGraphs(int totalDBGraphs) {
		return this.coverSet.notContainedDatabaseGraphs(totalDBGraphs);
	}
	@Override
	public int[] notContainedQueryGraphs(int totalQueryGraphs) {
		return this.coverSet.notContainedQueryGraphs(totalQueryGraphs);
	}
	@Override
	public int[] getEquavalentDatabaseGraphs() {
		return this.coverSet.getEquavalentDatabaseGraphs();
	}
	@Override
	public int[] getEquavalentQueryGraphs() {
		return this.coverSet.getEquavalentQueryGraphs();
	}
	@Override
	public OneFeatureMultiClass getFeature() {
		return this.coverSet.getFeature();
	}
	@Override
	public int getFetureID() {
		return this.coverSet.getFetureID();
	}
}
