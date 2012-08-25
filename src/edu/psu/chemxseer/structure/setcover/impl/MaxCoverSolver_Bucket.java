package edu.psu.chemxseer.structure.setcover.impl;

import java.io.IOException;

import edu.psu.chemxseer.structure.setcover.IO.Input_FileBucket;
import edu.psu.chemxseer.structure.setcover.IO.Input_FileStream;
import edu.psu.chemxseer.structure.setcover.interfaces.IMaxCoverSolver;
import edu.psu.chemxseer.structure.setcover.maxCoverStatus.ICoverStatus;
import edu.psu.chemxseer.structure.setcover.sets.CoverSet_FeatureWrapper;

public class MaxCoverSolver_Bucket implements IMaxCoverSolver {
	private ICoverStatus status;
	private Input_FileBucket input;
	
	public MaxCoverSolver_Bucket(Input_FileBucket input, ICoverStatus status){
		this.input = input;
		this.status = status;
	}
	/**
	 * Find the Top-K maximum coverage sets
	 * @param K
	 * @return
	 * @throws IOException 
	 */
	public int[] runGreedy(int K){
		int[] result = new int[K];
		int resultIndex = 0;
		
		
		int totalNumofSegments = input.getBucketCount();
		for(int i = 0; i< totalNumofSegments && resultIndex< K; i++){
			Input_FileStream segInput = this.input.getBucketInput(i);
			int threshold = this.input.getLowerBound(i);
			
			CoverSet_FeatureWrapper feature = null;
			while((feature = segInput.nextSet())!=null && resultIndex < K){
				int gain = status.getGain(feature);
				if(gain > threshold){// select
					status.addNewSet(feature);
					result[resultIndex++] = feature.getFetureID();
				}
				else this.input.append(feature, gain);
			}
		}
		return result;
	}
	
	@Override
	public int totalCoveredItems() {
		return this.status.getCoveredCount();
	}
	
}
