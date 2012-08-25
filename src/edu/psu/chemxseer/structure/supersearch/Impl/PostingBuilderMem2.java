package edu.psu.chemxseer.structure.supersearch.Impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Only Different, Support Insert new graph (ID) to a certain posting
 * @author dayuyuan
 *
 */
public class PostingBuilderMem2 {
	protected ArrayList<ArrayList<Integer>> postings;
	protected Map<Integer, Integer> nameConverter;
	
	//Dummy Constructor
	public PostingBuilderMem2(){
		this.postings = new ArrayList<ArrayList<Integer>>();
		this.nameConverter = new HashMap<Integer, Integer>();
	}

	/**
	 * Given the Current Space Expensive PostingBuilderMem2
	 * Construct and Return one PostingBuilderMem
	 * @return
	 */
	public PostingBuilderMem saveSpace(){
		PostingBuilderMem result = new PostingBuilderMem();
		result.nameConverter = nameConverter;
		for(ArrayList<Integer> onePost : postings){
			int[] newPost = new int[onePost.size()];
			for(int i = 0; i< newPost.length; i++)
				newPost[i] = onePost.get(i);
			Arrays.sort(newPost);
			result.postings.add(newPost);
		}
		return result;
	}
	
	/**
	 * Insert the postings for the feature: featureID
	 * @param featureID
	 * @param postings
	 */
	public void insertPosting(Integer featureID, int onePost){
		if(this.nameConverter.containsKey(featureID)){
			int index = nameConverter.get(featureID);
			this.postings.get(index).add(onePost);
		}
		else{
			this.nameConverter.put(featureID, this.postings.size());
			ArrayList<Integer> fPostings = new ArrayList<Integer>();
			fPostings.add(onePost);
			this.postings.add(fPostings);
		}
	}
}
