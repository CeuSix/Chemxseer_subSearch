package edu.psu.chemxseer.structure.setcover.maxCoverStatus.Decomp;

import edu.psu.chemxseer.structure.setcover.maxCoverStatus.ICoverStatus;
import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;

public abstract class CoverStatus_Decomp_NoIndex implements ICoverStatus{
	
	protected ICoverSet_FeatureWrapper[] selectedFeatures;
	protected int numOfSets;
	protected int classOneNumber;
	protected int classTwoNumber;
	
	public CoverStatus_Decomp_NoIndex(int K, 
			int classOneNumber, int classTwoNumber){
		this.selectedFeatures = new ICoverSet_FeatureWrapper[K];
		this.numOfSets = 0;
		this.classOneNumber = classOneNumber;
		this.classTwoNumber = classTwoNumber;
		
	}
	@Override
	public boolean addNewSet(ICoverSet_FeatureWrapper newSet) {
		if(this.numOfSets < this.selectedFeatures.length){
			this.selectedFeatures[this.numOfSets++] = newSet;
			return true;
		}
		else{
			System.out.println("Not Enough Space, need to swap instead of add new");
			return false;
		}
	}


}
