package edu.psu.chemxseer.structure.setcover.IO;

import java.util.Arrays;

import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter;
import edu.psu.chemxseer.structure.setcover.sets.CoverSet_FeatureWrapper;
import edu.psu.chemxseer.structure.setcover.sets.CoverSet_FeatureWrapper2;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.PostingFeaturesMultiClass;

/**
 * The hard-disk sorted implementation of the standard input
 * (1) All the sets are partitioned into buckets
 * (2) Sets are returned according to the buckets they are in
 */
public class Input_FileBucket implements IInputBucket{
	private String[] inputFiles; // the files saving the input of the sets
	private double p; // logP is for segmentation
	private int minK;
	private Input_FileBucket_Writter[] streams;
//	private int universeSize;
	
	
	public static Input_FileBucket newInstance (PostingFeaturesMultiClass postingFeatures, 
			IFeatureSetConverter converter, String filePrefix, double p){
//		int[] graphClassCount = postingFeatures.getClassGraphsCount();
//		int universeSize = graphClassCount[0] * graphClassCount[1];
		
		//1. Pre-process: sort the features according to its size
		CoverSet_FeatureWrapper2[] features = postingFeatures.toWrapper(converter);
		Arrays.sort(features);
		//2. Doing the Segmentation
		//2.1 find the min * max of K
		int k = -1;
		double power = 1;
		while(power < features[0].getGain() ){
			power = power * p; // power = p^(k+1)
			k ++;
		}
		Input_FileBucket_Writter output = new Input_FileBucket_Writter(filePrefix + k, false);
		int smallestK = k;
		
		for(CoverSet_FeatureWrapper2 oneF: features){
			while(oneF.getGain() > power){
				power = power * p;
				k++;
				output.closeWriter();
				output = new Input_FileBucket_Writter(filePrefix+k, false);
			}
			output.writerSet(new CoverSet_FeatureWrapper(oneF));
		}
		
		output.closeWriter();
		String[] fileStrings = new String[k-smallestK+1];
		for(int i = smallestK; i<= k; i++)
			fileStrings[i-smallestK] = filePrefix + k;
		return new Input_FileBucket(p, fileStrings, smallestK);
	}
	
	private Input_FileBucket(double p, String[] fileStrings, int minK){
		this.p = p;
		this.inputFiles = fileStrings;
		this.minK = minK;
	}
	
	
	public void finalize (){
		for(int i = 0; i< streams.length; i++)
			this.closeStream(i);
	}
	private void closeStream(int sigID){
		if(this.streams[sigID]!=null)
			streams[sigID].closeWriter();
		streams[sigID] = null;
	}
	private Input_FileBucket_Writter openStream(int sigID){
		if(sigID > minK + this.inputFiles.length)
			return null;
		
		if(this.streams[sigID] == null){
			streams[sigID] = new Input_FileBucket_Writter(this.inputFiles[sigID], true);
		}
		return this.streams[sigID];
	}
	@Override
	public Input_FileStream getBucketInput(int sigID){
		// First close the output_stream
		this.closeStream(sigID);
		return Input_FileStream.newInstance(this.inputFiles[sigID], false);
	}
	@Override
	public int getLowerBound(int sigID){
		return (int) Math.pow(p, minK+sigID);
	}
	@Override
	/**
	 * Based on the "gain" value, append the feature to different postings
	 * @param feature
	 * @param gain
	 * @return
	 */
	public boolean append(CoverSet_FeatureWrapper feature, int gain){
		// First find what is the "level" the "gain" function should be in
		int sigID = this.getLevel(gain)-minK;
		Input_FileBucket_Writter newStream = this.openStream(sigID);
		if(newStream!=null){
			newStream.writerSet(feature);
			return true;
		}
		else return false;
	}
	
	private int getLevel(int featureSize){
		double power = Math.pow(p, minK+1);
		int k = minK;
		while(featureSize > power){
			power = power * p;
			k++;
		}
		return k;
	}
	@Override
	public int getBucketCount(){
		return this.inputFiles.length;
	}

//	@Override
//	public int getUniverseCount() {
//		return this.universeSize;
//	}

}

