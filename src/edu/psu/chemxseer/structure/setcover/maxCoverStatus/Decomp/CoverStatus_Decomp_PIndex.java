package edu.psu.chemxseer.structure.setcover.maxCoverStatus.Decomp;

import edu.psu.chemxseer.structure.setcover.maxCoverStatus.ICoverStatusStream;
import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;

public abstract class CoverStatus_Decomp_PIndex implements ICoverStatusStream{
	protected short[][] qfIndex; 
	
	protected int[] qCovered; // qCovered[i] records the number of pairs <qCovered>, covered...
	protected int qCoveredMinFeatureID; // the minFeature (except) when qCovered is update
	
	protected ICoverSet_FeatureWrapper[] selectedFeatures;
	protected short numOfSets;
	protected int classOneNumber;
	protected int classTwoNumber;
	
	/**
	 * 
	 * @param K
	 * @param classOneNumber
	 * @param classTwoNumber
	 */
	public CoverStatus_Decomp_PIndex(int K,  int classOneNumber, int classTwoNumber){
		this.classOneNumber = classOneNumber;
		this.classTwoNumber = classTwoNumber;
		this.numOfSets = 0;
		
		this.selectedFeatures = new ICoverSet_FeatureWrapper[K];
	}

	@Override
	public ICoverSet_FeatureWrapper[] getSelectedSets() {
		return this.selectedFeatures;
	}

	@Override
	public ICoverSet_FeatureWrapper getSelectedSet(short minSetID) {
		return this.selectedFeatures[minSetID];
	}
	
	@Override
	public int[] getCoveredCountExceptMin(short minSetID, int[] yIDs) {
		if(this.qCoveredMinFeatureID == this.selectedFeatures[minSetID].getFetureID()){
			int[] result = new int[yIDs.length];
			for(int i = 0; i< result.length; i++)
				result[i] = this.qCovered[yIDs[i]];
			return result;
		}
		else{
			System.out.println("The getCoveredCountExceptMin count can not be answered directly");
			return null;
		}
	}
	/*************Protected Sharing Member************************/
	// Add a positive number to the array this.qfIndex[index]
	// number is positive
	// an negative number means this entry is empty
	protected void addValue(int index, short number) {
		if(this.qfIndex[index] == null){
			this.qfIndex[index] = new short[4];
			for(int i = 0; i< 4; i++)
				qfIndex[index][i] = -1;
		}
		for(int i = 0; i< qfIndex[index].length; i++){
			if(qfIndex[index][i] == -1){
				qfIndex[index][i] = number;
				return;
			}
		}
		// If not -1 is encountered: the array is full
		short[] temp = new short[2*qfIndex[index].length];
		int w = 0;
		for(; w< qfIndex[index].length; w++)
			temp[w] = qfIndex[index][w];
		temp[w++] = number;
		for(; w< temp.length; w++)
			temp[w] = -1;
		this.qfIndex[index] = temp;
	}
	// Delete a positive number from the array this.qfIndex[index]
	// number is positive
	// an negative number means this entry is empty
	protected boolean deleteValue(int index, int number){
		if(qfIndex[index] == null)
			return false;
		for(int i = 0; i< this.qfIndex[index].length; i++)
			if(qfIndex[index][i]==-1)
				continue;
			else if(qfIndex[index][i] == number){
				qfIndex[index][i] = -1;
				return true;
			}
		return false; // the deletion does not happen
	}
	
	protected void swapQueryIndex(int[] oldQ, int[] newQ, short number){
		int oldIndex = 0, newIndex = 0;
		while(true){
			if(oldIndex != oldQ.length && newIndex != newQ.length){
				if(oldQ[oldIndex] < newQ[newIndex]){
					// delete the old
					this.deleteValue(oldQ[oldIndex], number);
					oldIndex++;
				}
				else if(oldQ[oldIndex] == newQ[newIndex]){
					// do nothing
					oldIndex++;
					newIndex++;
				}
				else{
					// add the new query
					this.addValue(newQ[newIndex], number);
					newIndex++;
				}
			}
			else if(oldIndex == oldQ.length)
				if(newIndex == newQ.length)
					break;
				else {
					this.addValue(newQ[newIndex], number);
					newIndex++;
				}
			else {
				this.deleteValue(oldQ[oldIndex], number);
				oldIndex++;
			}
		}
	}
}
