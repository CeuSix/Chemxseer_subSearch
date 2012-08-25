package edu.psu.chemxseer.structure.subsearch.Interfaces;

import java.util.List;

import edu.psu.chemxseer.structure.subsearch.Lindex.LindexSearcher;
import edu.psu.chemxseer.structure.supersearch.Impl.PostingBuilderMem;

/**
 * Interface for postings
 * @author dayuyuan
 *
 */
public interface PostingFetcher {
	/**
	 * Given the featureID, retrieve the postings of this feature
	 * TimeComponent[0] = posting retrieval time
	 * @param featureID
	 * @param TimeComponent[0]
	 * @return
	 */
	public GraphFetcher getPosting(int featureID, long[] TimeComponent);
	/**
	 * Given the featureString, retrieve the posting of this featureString
	 * TimeComponent[0] = posting retrieval time
	 * @param featureString
	 * @param TimeComponent
	 * @return
	 */
	public GraphFetcher getPosting(String featureString, long[] TimeComponent);
	/**
	 * Given the featureIDs, retrieve the Union of those features postings
	 * TimeComponent[0] = posting retrieval time
	 * @param featureIDs
	 * @param TimeComponent[0]
	 * @return
	 */
	public GraphFetcher getUnion(List<Integer> featureIDs, long[] TimeComponent);
	
	/**
	 * Given the featureIDs, retrieve the Join of those features postings;
	 * TimeComponent[0] = posting retrieval time
	 * @param featureIDs
	 * @param TimeComponent[0]
	 * @return
	 */
	public GraphFetcher getJoin(List<Integer> featureIDs, long[] TimeComponent);
	/**
	 * TimeComponent[0] = posting retrieval time
	 * @param featureStrings
	 * @param TimeComponent
	 * @return
	 */
	public GraphFetcher getJoin(String[] featureStrings, long[] TimeComponent);
	
	/**
	 * Given the featureIDs, retrieve the compliment of the Union of those features Postings
	 * @param featureIDs
	 * @param TimeComponent
	 * @return
	 */
	public GraphFetcher getComplement(List<Integer> featureIDs, long[] TimeComponent);
	
	public PostingBuilderMem loadPostingIntoMemory(IndexSearcherLucene indexSearcher);
}
