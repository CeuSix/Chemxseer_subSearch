package edu.psu.chemxseer.structure.setcover.impl;

import java.util.HashSet;
import java.util.List;

import edu.psu.chemxseer.structure.setcover.IO.IInputSequential;
import edu.psu.chemxseer.structure.setcover.IO.Input_FileStream;
import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter.FeatureSetType;
import edu.psu.chemxseer.structure.setcover.interfaces.IMaxCoverSolver;
import edu.psu.chemxseer.structure.setcover.maxCoverStatus.ICoverStatus;
import edu.psu.chemxseer.structure.setcover.sets.CoverSet_FeatureWrapper;
import edu.psu.chemxseer.structure.util.MemoryConsumptionCal;

/**
 * A implementation of the greedy algorithm for max_k coverage problem
 * (1) No random access of the input
 * (2) Only Sequential scan 
 * If "K" features need to be selected, then "K" sequential scan need to be applied
 * @author dayuyuan
 *
 */
public class MaxCoverSolver_Sequential implements IMaxCoverSolver{
	private ICoverStatus status;
	private IInputSequential input;
	private int batchNum = 50;
	
	public MaxCoverSolver_Sequential(IInputSequential input, ICoverStatus status){
		this.input = input;
		this.status = status;
	}
	public int totalCoveredItems(){
		return this.status.getCoveredCount();
	}
	/**
	 * Top K maximum Coverage
	 * @param K
	 * @return: the K sets covering the maximum number of items
	 */
	public int[]  runGreedy(int K){
		long start = System.currentTimeMillis();
		int[] results = new int[K];
		HashSet<Integer> selectedSets = new HashSet<Integer>();
		double avgMem = 0;
		int i = 0;
		for(; i< K; i++){
			int nextSetID = this.findNextSet(selectedSets);
			if(nextSetID!=-1){
				results[i] = nextSetID;
				selectedSets.add(nextSetID);
			}
			else{
				System.out.println("All the sets are already covered or no sets available");
				break;
			}
			double mem =MemoryConsumptionCal.usedMemoryinMB();
			if(i == 0)
				avgMem = mem;
			else{
				avgMem = (i*avgMem + mem)/i+1;
			}
		}
		System.out.println("OnDisk Average Space Complexity (MB): " + avgMem);
		System.out.println("OnDisk Total Time for onDisk Mining: " + (System.currentTimeMillis()-start));
		if(i == K)
			return results;
		else{
			int[] realResults = new int[i];
			for(int w = 0; w < i; w++)
				realResults[w] = results[w];
			return realResults;
		}
	}
	
	/**
	 * Find the next available sets (unselected) covering the maximum number of 
	 * uncovered items
	 * @param selectedSets
	 * @return
	 */
	private int findNextSet(HashSet<Integer> selectedSets){
		if(!selectedSets.isEmpty())
			this.input.reWind();
		
		List<CoverSet_FeatureWrapper> batchInput = this.input.nextSets(batchNum);
		int setID = 0;
		int maxScore = 0;
		CoverSet_FeatureWrapper maxSet = null;
		int maxSetID = -1;
		
		while(batchInput.size() > 0){
			for(int i = 0; i< batchInput.size(); i++){
				if(!selectedSets.contains(setID)){
					// this set not selected yet
					int score = status.getGain(batchInput.get(i));
					if(score > maxScore){
						maxScore = score;
						maxSet = batchInput.get(i);
						maxSetID = setID;
					}
				}
				setID ++;
			}
			batchInput = this.input.nextSets(batchNum);
		}
		// update
		if(maxScore > 0){
			status.addNewSet(maxSet); // updating
			return maxSetID;
		}
		else return -1; // not need for further selection
	}
	
}
