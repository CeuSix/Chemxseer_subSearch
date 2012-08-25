package edu.psu.chemxseer.structure.setcover.impl;

import java.util.List;

import edu.psu.chemxseer.structure.setcover.IO.IInputSequential;
import edu.psu.chemxseer.structure.setcover.IO.Input_Mem;
import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter;
import edu.psu.chemxseer.structure.setcover.interfaces.IMaxCoverInvertedIndex;
import edu.psu.chemxseer.structure.setcover.interfaces.IMaxCoverSolver;
import edu.psu.chemxseer.structure.setcover.maxCoverStatus.ICoverStatus;
import edu.psu.chemxseer.structure.setcover.maxCoverStatus.ICoverStatus_Set;
import edu.psu.chemxseer.structure.setcover.maxCoverStatus.Stream_LinkedList;
import edu.psu.chemxseer.structure.setcover.sets.CoverSet_FeatureWrapper_Priority;
import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;
import edu.psu.chemxseer.structure.util.PriorityQueueSelfImp;

/**
 * For Matrix Sets: the implementation of a inverted-index embedded max-coverage solver
 * @author dayuyuan
 *
 */
public class MaxCoverSolver_InvertedIndexMatrix implements IMaxCoverSolver{
	// Use the priority queue to retrieve the max-gain sets
	private PriorityQueueSelfImp<CoverSet_FeatureWrapper_Priority> queue;
	private CoverSet_FeatureWrapper_Priority[] sets; // for fID retrievel
	// Use the inverted index to update the gain of each sets
	private boolean preprocessed;
	private IMaxCoverInvertedIndex invertedIndex;
	// The status
	private ICoverStatus status;
	
	public MaxCoverSolver_InvertedIndexMatrix(IInputSequential input,  
			ICoverStatus status){
		long start =System.currentTimeMillis();
		this.status = status;
		//1. Build Queue
		this.queue = new PriorityQueueSelfImp<CoverSet_FeatureWrapper_Priority>(false);
		this.sets =  new CoverSet_FeatureWrapper_Priority[input.getSetCount()];
		ICoverSet_FeatureWrapper nextSet = input.nextSet();
		int counter = 0;
		while(nextSet!=null){
			ICoverSet_FeatureWrapper oriSet = nextSet;
			int key = this.status.getGain(oriSet);
			sets[counter++] = new CoverSet_FeatureWrapper_Priority(oriSet, key , counter);
			if(counter!= oriSet.getFetureID()){
				System.out.println("In consistence in counter and fID");
			}
			queue.add(sets[counter]);
			nextSet = input.nextSet();
		}
		//2. Build the inverted Index
		if(status instanceof ICoverStatus_Set){
			ICoverStatus_Set temp = (ICoverStatus_Set)status;
			this.preProcess(input, temp.getConverter());
		}
		else this.preProcess(input, null);
		
		System.out.println("InMem PreProcessing Time: " + (System.currentTimeMillis()-start));
	}
	
	/**
	 * Do the pre-processing of the internal representation, 
	 * After the pre-processing, an inverted index is built
	 */
	private boolean preProcess(IInputSequential input, IFeatureSetConverter converter){
		if(this.preprocessed)
			return false;
		else{
			// build the inverted index
			this.invertedIndex.create(input, converter);
			return true;
		}
	}

	
	/**
	 * Top K maximum Coverage:
	 * (1) In each iteration, the feature with the maximum gain is selected
	 * (2) Then based on the inverted index, the gain of other features is updated
	 * @param K
	 * @return: the K sets covering the maimum number of items
	 */
	public int[] runGreedy(int K){
		long startTime = System.currentTimeMillis();
		int[] results = new int[K];
		for(int i = 0; i< K; i++){
			CoverSet_FeatureWrapper_Priority nextSet = this.coverNextSet();
			nextSet.delete(); // find one set, delete it from the candidate sets
			if(nextSet!=null && nextSet.getValue()!=0){
				results[i] = nextSet.getFetureID();
			}
			else{
				System.out.println("All the sets are already covered or no sets available");
				for(; i < K; i++)
					results[i] = -1;
				break;
			}
		}
		System.out.println("InMem Total Time Complexity: " + (System.currentTimeMillis()-startTime));
		return results;
	}
	
	/**
	 * Return the set with the maximum gain
	 * Also, update the score for other features
	 * @return
	 */
	private CoverSet_FeatureWrapper_Priority coverNextSet(){
		if(queue.isEmpty())
			return null; // no sets left
		else{
			// Fetch the max-gain set
			CoverSet_FeatureWrapper_Priority nextSet = queue.deleteMax(); 
			nextSet.delete();
			
			if(nextSet.getValue()!=0){
				// update the status
				int[][] newlyCoveredMatrix = status.addNewSetWithReturn(nextSet);
				// Use the inverted index, update the other sets
				for(int i = 0; i< newlyCoveredMatrix.length; i++){
					int qID = newlyCoveredMatrix[i][0];
					for(int j = 1; j< newlyCoveredMatrix[i].length; j++){
						int gID = newlyCoveredMatrix[i][j];
						short[] setsNeedUpdate = this.invertedIndex.getCoveredSets(qID, gID);
						for(int eachSetID: setsNeedUpdate){
							CoverSet_FeatureWrapper_Priority updateSet = this.sets[eachSetID];
							//TEST
							assert(eachSetID == updateSet.getFetureID());
							//END of TEST
							if(updateSet.isDeleted()) //  skip already detected sets
								continue;
							updateSet.decraseValue();
							queue.changeKey(updateSet, -1);
						}
					}
				}
			}
			return nextSet;
		}
	}
	
	@Override
	public int totalCoveredItems() {
		return this.status.getCoveredCount();
	}
}
