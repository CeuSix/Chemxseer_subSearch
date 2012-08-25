package edu.psu.chemxseer.structure.subsearch.Impl;

import java.util.List;

import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphDatabase;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IndexSearcherLucene;
import edu.psu.chemxseer.structure.subsearch.Interfaces.PostingFetcher;
import edu.psu.chemxseer.structure.supersearch.Impl.PostingBuilderMem;
import edu.psu.chemxseer.structure.util.IntersectionSet;

/**
 * The in-memory posting fetcher: all the postings are represented as arrays
 * @author dayuyuan
 *
 */
public class PostingFetcherMem extends PostingBuilderMem implements PostingFetcher{
	private GraphDatabase gDB;
	
	public PostingFetcherMem(GraphDatabase gDB){
		super();
		this.gDB = gDB;
	}
	public PostingFetcherMem(GraphDatabase gDB, String fileName){
		super(fileName);
		this.gDB = gDB;
	}
	
	public PostingFetcherMem(GraphDatabase newGDB, PostingBuilderMem postingBuilder){
		super(postingBuilder);
		this.gDB = newGDB;
	}
	
	@Override
	public GraphFetcher getPosting(int featureID, long[] TimeComponent) {
		Integer id = this.nameConverter.get(featureID);
		long start = System.currentTimeMillis();
		if(id == null){
			System.out.println("Error in getPosting: illiegal input featureID");
			return null;
		}
		else{
			int[] temp = postings.get(id);
			TimeComponent[0] += System.currentTimeMillis()-start;
			GraphFetcherDB result = new GraphFetcherDB(gDB, temp, false);
			if(result.size() == 0)
				System.out.println("Empty Return Result in PostingFetcherMem: getPosting");
			return result;
		}
	}

	@Override
	public GraphFetcher getPosting(String featureString, long[] TimeComponent) {
		System.out.println("The PostingFetcherMem: getPosting(FeatureString) is not implemented");
		return null;
	}

	@Override
	public GraphFetcher getUnion(List<Integer> featureIDs, long[] TimeComponent) {
		long start = System.currentTimeMillis();
		IntersectionSet set = new IntersectionSet();
		for(int i = 0; i< featureIDs.size(); i++){
			Integer it = this.nameConverter.get(featureIDs.get(i));
			if(it == null){
				System.out.println("Error in getUnion: illigale input featureID");
				return null;
			}
			else set.addAll(this.postings.get(it));
		}
		int[] temp = set.getItems();
		TimeComponent[0] += System.currentTimeMillis()-start;
		GraphFetcherDB result = new GraphFetcherDB(gDB, temp,false);
		if(result.size() == 0)
			System.out.println("Empty Return Result in PostingFetcherMem: getUnion");
		return result;
	}

	@Override
	public GraphFetcher getJoin(List<Integer> featureIDs, long[] TimeComponent) {
		long start = System.currentTimeMillis();
		IntersectionSet set = new IntersectionSet();
		for(int i = 0; i< featureIDs.size(); i++){
			Integer it = this.nameConverter.get(featureIDs.get(i));
			if(it == null){
				System.out.println("Error in getJoin: illigale input featureID");
				return null;
			}
			else if(i==0)
				set.addAll(this.postings.get(it));
			else set.retainAll(this.postings.get(it));
		}
		int[] temp = set.getItems();
		TimeComponent[0] += System.currentTimeMillis()-start;
		GraphFetcherDB result = new GraphFetcherDB(gDB, temp, false);
		if(result.size() == 0)
			System.out.println("Empty Return Result in PostingFetcherMem: getJoin");
		return result;
	}

	@Override
	public GraphFetcher getJoin(String[] featureStrings, long[] TimeComponent) {
		System.out.println("The PostingFetcherMem: getJoin(FeatureString) is not implemented");
		return null;
	}
	

	@Override
	public GraphFetcher getComplement(List<Integer> featureIDs,
			long[] TimeComponent) {
		long start = System.currentTimeMillis();
		IntersectionSet set = new IntersectionSet();
		for(int i = 0; i< featureIDs.size(); i++){
			Integer it = this.nameConverter.get(featureIDs.get(i));
			if(it == null){
				System.out.println("Error in getUnion: illigale input featureID");
				return null;
			}
			else set.addAll(this.postings.get(it));
		}
		int[] temp = set.getItems();
		TimeComponent[0] += System.currentTimeMillis()-start;
		GraphFetcherDB result =  new GraphFetcherDB(gDB, temp, true); // reverse
		if(result.size() == 0)
			System.out.println("Empty Return Result in PostingFetcherMem: getComplete");
		return result;
	}
	@Override
	public PostingBuilderMem loadPostingIntoMemory(
			IndexSearcherLucene indexSearcher) {
		return this;
	}

}
