package edu.psu.chemxseer.structure.supersearch.LWTree;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphFetcherLucene;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphFetcherLucenePrefix;
import edu.psu.chemxseer.structure.subsearch.Impl.PostingFetcherLucene;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SubgraphSearch;
import edu.psu.chemxseer.structure.supersearch.Impl.PostingBuilderMem;
import edu.psu.chemxseer.structure.supersearch.Impl.VerifierISOPrefix;

public class SupSearch_LWIndex implements SubgraphSearch{
	
	private LWIndexSearcher lwIndex;
	private VerifierISOPrefix verifier;
	private PostingFetcherLucene postingFetcher;
	
	public SupSearch_LWIndex(LWIndexSearcher indexSearcher,  
			PostingFetcherLucene postingFetcher, VerifierISOPrefix verifier){
		this.lwIndex = indexSearcher;
		this.verifier = verifier;
		this.postingFetcher = postingFetcher;
	}
	
	@Override
	/**
	 * TimeComponent[0] = posting fetching 
	 * TimeComponent[1] = DB graph loading time 
	 * TimeComponent[2] = filtering cost
	 * TimeComponent[3] = verification cost
	 * Number[0] = verified graphs number 
	 * Number[1] = True answer size
	 */
	public List<GraphResult> getAnswer(Graph query, long[] TimeComponent,
			int[] Number) throws IOException, ParseException {
		//1. First step is to find all features that are contained in the query graph
		List<Integer> allSubgraphs = lwIndex.subgraphs(query, TimeComponent);
		//2. Get all the candidate graphs: just a summation operation
		GraphFetcherLucene temp = (GraphFetcherLucene) postingFetcher.getUnion(allSubgraphs, TimeComponent);
		GraphFetcherLucenePrefix candidateFetcher = new GraphFetcherLucenePrefix(
				temp, lwIndex);
		Number[0] = candidateFetcher.size(); 
		//3. Verification phase
		List<GraphResult> answer = this.verifier.verify(candidateFetcher, query, TimeComponent);
		Number[1] = answer.size();
		return answer;
	}

	public static String getIndexName() {
		return "index";
	}

	public int getFeatureCount() {
		return this.lwIndex.getFeatureCount();
	}

	@Override
	public PostingBuilderMem getInMemPosting() {
		return this.postingFetcher.loadPostingIntoMemory(this.lwIndex);
	}

}
