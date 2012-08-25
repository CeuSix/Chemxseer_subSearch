package edu.psu.chemxseer.structure.setcover.IO;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter;
import edu.psu.chemxseer.structure.setcover.sets.CoverSet_FeatureWrapper;
import edu.psu.chemxseer.structure.setcover.sets.CoverSet_FeatureWrapper2;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.PostingFeaturesMultiClass;

/**
 * In-memory implementation of the buckets: 
 * @author dayuyuan
 *
 */
public class Input_MemBucket implements IInputBucket{
	private Input_Mem[] buckets; // the files saving the input of the sets
	private double logP; // logP is for segmentation
	
	
	public static Input_MemBucket newInstance (PostingFeaturesMultiClass postingFeatures, 
			IFeatureSetConverter converter, String filePrefix, double p){
		//1. Pre-process: sort the features according to its size
		CoverSet_FeatureWrapper2[] features = postingFeatures.toWrapper(converter);
		Arrays.sort(features);
		//2. Doing the Segmentation
		//2.1 find the min * max of K
		double logP = Math.log(p);
		int smallestK = (int) (Math.log(features[0].getGain())/logP);
		int largestK = (int)(Math.log(features[0].getGain())/logP);
		List<CoverSet_FeatureWrapper>[] inputs = new ArrayList[largestK+1];
		for(int i = 0; i< inputs.length; i++){
			inputs[i] = new ArrayList<CoverSet_FeatureWrapper>();
		}
		
		int power = (int)(Math.exp(logP*(smallestK+1)));
		for(int fID = 0, i = smallestK; i< inputs.length; ){
			if(features[fID].getGain() > power){
				power = (int) (power * p); 
				i++;
				continue; // continue the iteration
			}
			else {
				inputs[i].add(new CoverSet_FeatureWrapper(features[fID]));
				fID++;
			}
		}
		
		return new Input_MemBucket(logP, inputs);
	}
	
	public Input_MemBucket(double logP, List<CoverSet_FeatureWrapper>[] inputs){
		this.logP = logP;
		this.buckets = new Input_Mem[inputs.length];
		for(int i = 0; i< buckets.length; i++){
			if(inputs[i]!=null)
				buckets[i] = Input_Mem.newInstance(inputs[i]);
			else continue;
		}
	}
	
	@Override
	public int getBucketCount() {
		return this.buckets.length;
	}

	@Override
	public boolean append(CoverSet_FeatureWrapper feature, int gain) {
		// First find what is the "level" the "gain" function should be in
		int bucketID = this.getBucket(gain);
		if(buckets[bucketID]!=null){
			buckets[bucketID].appendFeature(feature);
			return true;
		}
		else{
			buckets[bucketID] = Input_Mem.newEmptyInstance();
			buckets[bucketID].appendFeature(feature);
			return true;
		}
	}
	
	/**
	 * Given the gain function, calculate the bucket the set
	 * should be assigned to
	 * @param gain
	 * @return
	 */
	private int getBucket(int gain){
		int result = (int)( Math.log(gain)/ logP);
		return result;
	}

	@Override
	public int getLowerBound(int bID) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public IInputSequential getBucketInput(int bID) {
		// TODO Auto-generated method stub
		return null;
	}

}
