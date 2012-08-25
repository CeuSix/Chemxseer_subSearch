package edu.psu.chemxseer.structure.setcover.maxCoverStatus;

import javaewah.EWAHCompressedBitmap;
import edu.psu.chemxseer.structure.setcover.IO.IInput;
import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter;
import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;

/**
 * The bitmap implementation of the ICoverStatus_Set:
 * out of update
 * 
 * @author dayuyuan
 *
 */
public class Status_EWAH {
	protected EWAHCompressedBitmap coverStatus;
	protected IFeatureSetConverter converter;
	
	public Status_EWAH(IFeatureSetConverter converter){
		this.coverStatus = null;
		this.converter = converter;
	}
	
	public int getGain(ICoverSet_FeatureWrapper newSet) {
		EWAHCompressedBitmap inputSet = converter.FeatureToSet_EWAH(newSet);
		if(inputSet == null)
			return 0;
		else if(this.coverStatus == null)
			return inputSet.cardinality();
		else {
			EWAHCompressedBitmap temp = inputSet.andNot(this.coverStatus);
			return temp.cardinality();
		}
	}

	public boolean addNewSet(ICoverSet_FeatureWrapper newSet) {
		EWAHCompressedBitmap inputSet = converter.FeatureToSet_EWAH(newSet);
		if(inputSet == null)
			return false; 
		else if(this.coverStatus == null){
			this.coverStatus = inputSet;
			return true;
		}
		else{
			this.coverStatus = this.coverStatus.or(inputSet);
			return true;
		}
	}

	public int getCoveredCount() {
		if(this.coverStatus == null)
			return 0;
		else return this.coverStatus.cardinality();
	}

	public IFeatureSetConverter getConverter() {
		return this.converter;
	}

}
