package edu.psu.chemxseer.structure.setcover.impl;

import java.util.ArrayList;
import java.util.List;

import edu.psu.chemxseer.structure.setcover.IO.Input_Mem;
import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter;
import edu.psu.chemxseer.structure.setcover.interfaces.IMaxCoverSolver;
import edu.psu.chemxseer.structure.setcover.maxCoverStatus.ICoverStatus;
import edu.psu.chemxseer.structure.setcover.sets.CoverSet_FeatureWrapper_Priority;
import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;
import edu.psu.chemxseer.structure.util.PriorityQueueSelfImp;

/**
 * A Implementation of the Greedy algorithm for Max-K coverage problem.
 * Assuming memory in big enough to hold the input and the inverted index
 * (1) Inverted-index is used for fast process of the update
 * (2) Min-max heap is used for fast process of the update
 * So that the gain of each set is up to date
 * Pre-requestion: All the Sets & Items are Known
 * @author dayuyuan
 */
public class MaxCoverSolver_InvertedIndex  {
//	
//	private PriorityQueueSelfImp<CoverSet_FeatureWrapper_Priority> sets;
//	// Given a item, find what's set of sets actually contain this item
//	private boolean preprocessed;
//	private int[][] invertedIndex;
//	// The status
//	private ICoverStatus status;
//	
//	public MaxCoverSolver_InvertedIndex(Input_Mem in_memInput,  
//			ICoverStatus status){
//		long start =System.currentTimeMillis();
//		this.status = status;
//		this.sets = new PriorityQueueSelfImp<CoverSet_FeatureWrapper_Priority>(false);
//		this.buildQueue(in_memInput);
//		this.preProcess(in_memInput);
//		System.out.println("InMem PreProcessing Time: " + (System.currentTimeMillis()-start));
//	}
//	/**
//	 * Build the priority queue to fast process the lookup of the maximum sets
//	 */
//	private void buildQueue(Input_Mem input){
//		for(int i  = 0; i< input.getSetCount(); i++){
//			ICoverSet_FeatureWrapper oriSet = input.getSet(i);
//			int key = this.status.getGain(oriSet);
//			this.sets.add(new CoverSet_FeatureWrapper_Priority(oriSet, key , i));
//		}
//	}
//	
//	/**
//	 * Do the pre-processing of the internal representation, 
//	 * After the pre-processing, an inverted index is built
//	 */
//	private boolean preProcess(Input_Mem input){
//		if(this.preprocessed)
//			return false;
//		else{
//			// build the inverted index
//			this.invertedIndex = new int[input.getUniverseCount()][];
//			int[] count = new int[input.getUniverseCount()];
//			for(int i = 0; i < count.length; i++)
//				count[i] = 0;
//			for(int i = 0; i< input.getSetCount(); i++){
//				int[] items = this.converter.featureToSet_Array(input.getSet(i));
//				for(int item:items){
//					addSetToIndex(item, count, i);
//				}
//			}
//			// save space for the inverted index
//			for(int item = 0; item < invertedIndex.length; item++){
//				if(invertedIndex[item].length == count[item])
//					continue;
//				else{
//					int[] temp = new int[count[item]];
//					for(int i = 0; i < temp.length; i++)
//						temp[i] = invertedIndex[item][i];
//					invertedIndex[item] = temp;
//				}
//			}
//			return true;
//		}
//	}
//	
//	private void addSetToIndex(int item, int[] count, int sID){
//		if(this.invertedIndex[item] == null)
//			invertedIndex[item] = new int[4];
//		if(this.invertedIndex[item].length == count[item]){
//			int[] temp = new int[count[item]<<1];
//			for(int i = 0; i< count[item]; i++)
//				temp[i] = invertedIndex[item][i];
//			invertedIndex[item] = temp;
//		}
//		//append the sID
//		invertedIndex[item][count[item]++] = sID;
//	}
//
//	
//	/**
//	 * Top K maximum Coverage:
//	 * (1) In each iteration, the feature with the maximum gain is selected
//	 * (2) Then based on the inverted index, the gain of other features is updated
//	 * @param K
//	 * @return: the K sets covering the maimum number of items
//	 */
//	public int[] runGreedy(int K){
//		long startTime = System.currentTimeMillis();
//		int[] results = new int[K];
//		for(int i = 0; i< K; i++){
//			CoverSet_FeatureWrapper_Priority nextSet = this.coverNextSet();
//			nextSet.delete(); // find one set, delete it from the candidate sets
//			if(nextSet!=null && nextSet.getValue()!=0){
//				results[i] = nextSet.getFetureID();
//			}
//			else{
//				System.out.println("All the sets are already covered or no sets available");
//				for(; i < K; i++)
//					results[i] = -1;
//				break;
//			}
//		}
//		System.out.println("InMem Total Time Complexity: " + (System.currentTimeMillis()-startTime));
//		return results;
//	}
//	
//	/**
//	 * Return the set with the maximum gain
//	 * Also, update the score for other features
//	 * @return
//	 */
//	private CoverSet_FeatureWrapper_Priority coverNextSet(){
//		if(this.sets.isEmpty())
//			return null; // no sets left
//		else{
//			CoverSet_FeatureWrapper_Priority nextSet = this.sets.deleteMax(); // the queue is a max queue
//			nextSet.delete();
//			
//			if(nextSet.getValue()!=0){
//				// update the status
//				int[] newlyCovered = this.status.addNewSetWithReturn(nextSet.getCoverSet());
//				// Use the inverted index, update the other sets
//				for(int item: newlyCovered){
//					List<Integer> setsNeedUpdate = this.invertedIndex[item];
//					for(int eachSetID: setsNeedUpdate){
//						if(input.getOneSet(eachSetID).deleted()) //  skip already detected sets
//							continue;
//						this.input.getOneSet(eachSetID).decraseValue();
//						this.sets.changeKey(input.getOneSet(eachSetID), -1);
//					}
//				}
//			}
//			return nextSet;
//		}
//	}
//	@Override
//	public int totalCoveredItems() {
//		return this.status.getCoveredCount();
//	}
//	
}
