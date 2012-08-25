package edu.psu.chemxseer.structure.supersearch.PrefIndex;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphFetcherDB;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphFetcherLucene;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphFetcherLucenePrefix;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphFetcherPrefix;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.PostingFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SubgraphSearch;
import edu.psu.chemxseer.structure.supersearch.Impl.GraphFetcherDBPrefix;
import edu.psu.chemxseer.structure.supersearch.Impl.PostingBuilderMem;
import edu.psu.chemxseer.structure.supersearch.Impl.VerifierISOPrefix;

public class SupSearch_PrefixIndex implements SubgraphSearch{
	protected SupSearch_PrefixIndex upperLevelIndex;
	protected PrefixIndexSearcher searcher;
	protected PostingFetcher postingFetcher;
	protected VerifierISOPrefix verifier;
	
	protected PrefixIndexSearcher lowerLevelSearcher;
	
	/**
	 * Build One Flat SupSearch_PrefixIndex: Not upper Level Index, Not Lower Level Index
	 * @param indexSearcher
	 * @param postingFetcher
	 * @param verifier
	 */
	public SupSearch_PrefixIndex(PrefixIndexSearcher indexSearcher, PostingFetcher postingFetcher,
			VerifierISOPrefix verifier){
		this.searcher = indexSearcher;
		this.postingFetcher = postingFetcher;
		this.verifier = verifier;
		this.upperLevelIndex = null;
		this.lowerLevelSearcher = null;
	}
	/**
	 * Build One SupSearch_PrefIndex with Both UpperLevelIndex & LowerLevelIndex
	 * @param indexSearcher
	 * @param postingFetcher
	 * @param verifier
	 * @param upperLevelIndex
	 * @param lowerSearcher
	 */
	public SupSearch_PrefixIndex(PrefixIndexSearcher indexSearcher, PostingFetcher postingFetcher, 
			VerifierISOPrefix verifier, SupSearch_PrefixIndex upperLevelIndex, PrefixIndexSearcher lowerSearcher){
		this.searcher = indexSearcher;
		this.postingFetcher = postingFetcher;
		this.verifier = verifier;
		this.upperLevelIndex = upperLevelIndex;
		this.lowerLevelSearcher = lowerSearcher;
	}
	
	public void addLowerIndexSearcher(PrefixIndexSearcher lowerIndexSearcher){
		this.lowerLevelSearcher = lowerIndexSearcher;
	}
	
	@Override
	public List<GraphResult> getAnswer(Graph query, long[] TimeComponent,
			int[] Number) throws IOException, ParseException {
		if(this.upperLevelIndex == null)
			return this.getAnswerNormal(query, TimeComponent, Number);
		else return this.getAnswerRecursive(query, TimeComponent, Number);
	}
	/**
	 * Recursively call the UpperLevelIndex
	 * @param query
	 * @param TimeComponent
	 * @param Number
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	private List<GraphResult> getAnswerRecursive(Graph query,
			long[] TimeComponent, int[] Number) throws IOException, ParseException {
		//1. First, get Answer of the upperLevelIndex
		long startTime = System.currentTimeMillis();
		long[] temp = new long[4];
		int[] tempNum = new int[2];
		// The prefix subgraph isomrophism test is calculated as filtering cost
		List<GraphResult> containedFeatures = this.upperLevelIndex.getAnswer(query, temp, tempNum);
		int[] containedFeatureIDs = new int[containedFeatures.size()];
		for(int i = 0; i< containedFeatureIDs.length; i++){
			containedFeatureIDs[i] = containedFeatures.get(i).getDocID();
		}
		TimeComponent[2] += System.currentTimeMillis()-startTime;
		List<Integer> nonSubgraphs = this.searcher.nonSubgraphs(containedFeatureIDs, TimeComponent);
		//2. Filtering
		GraphFetcher candidateFetcher = postingFetcher.getComplement(nonSubgraphs, TimeComponent);
		GraphFetcherPrefix candidateFetcherPrefix =null;
		if(this.lowerLevelSearcher == null)
			candidateFetcherPrefix = 
				new GraphFetcherLucenePrefix((GraphFetcherLucene)candidateFetcher, this.searcher);
		else candidateFetcherPrefix = 
				new GraphFetcherDBPrefix((GraphFetcherDB) candidateFetcher, this.lowerLevelSearcher);
		
		//3. Verify
		Number[0] = candidateFetcherPrefix.size();
		List<GraphResult> answer = this.verifier.verify(candidateFetcherPrefix, query,TimeComponent);
		Number[1] = answer.size();
		return answer;
	}
	
	
	private List<GraphResult> getAnswerNormal(Graph query, long[] TimeComponent, int[] Number){
		//1. Get All the Subgraphs not contained in the query
		List<Integer> nonSubgraphs = searcher.nonSubgraphs(query, TimeComponent);
		//2. Filtering: the DISK Lucene Posting Fetcher
		GraphFetcher candidateFetcher = postingFetcher.getComplement(nonSubgraphs, TimeComponent);
		GraphFetcherPrefix candidateFetcherPrefix =null;
		if(this.lowerLevelSearcher == null)
			candidateFetcherPrefix = 
				new GraphFetcherLucenePrefix((GraphFetcherLucene)candidateFetcher, this.searcher);
		else candidateFetcherPrefix = 
				new GraphFetcherDBPrefix((GraphFetcherDB) candidateFetcher, this.lowerLevelSearcher);
		//3. Verify
		Number[0] = candidateFetcherPrefix.size();
		List<GraphResult> answer = this.verifier.verify(candidateFetcherPrefix, query, TimeComponent);
		Number[1] = answer.size();
		return answer;
	}
	
	public static String getLucene() {
		return "lucene/";
	}
	public static String getIndexName() {
		return "index";
	}
	public PrefixIndexSearcher getSearcher() {
		return this.searcher;
	}
//	/**
//	 * Build One SupSearch_PrefxIndex with LowerLevelIndex
//	 * @param indexSearcher
//	 * @param postingFetcher
//	 * @param verifier
//	 * @param lowerLevelSearcher
//	 */
//	public SupSearch_PrefixIndex(PrefixIndexSearcher indexSearcher, PostingFetcher postingFetcher, 
//			VerifierISOPrefix verifier, PrefixIndexSearcher lowerLevelSearcher){
//		this.searcher = indexSearcher;
//		this.postingFetcher = postingFetcher;
//		this.verifier = verifier;
//		this.upperLevelIndex = null;
//		this.lowerLevelSearcher = lowerLevelSearcher;
//		
//	}
//	/**
//	 * Build One SupSearch_PrefixIndex with UpperLevelIndex
//	 * @param indexSearcher
//	 * @param postingFetcher
//	 * @param verifier
//	 * @param uppderLevelIndex
//	 */
//	public SupSearch_PrefixIndex(PrefixIndexSearcher indexSearcher, PostingFetcher postingFetcher,
//			VerifierISOPrefix verifier, SupSearch_PrefixIndex upperLevelIndex){
//		this.searcher = indexSearcher;
//		this.postingFetcher = postingFetcher;
//		this.verifier = verifier;
//		this.upperLevelIndex = upperLevelIndex;
//	}
	@Override
	public PostingBuilderMem getInMemPosting() {
		return this.postingFetcher.loadPostingIntoMemory(this.searcher);
	}
	
}
