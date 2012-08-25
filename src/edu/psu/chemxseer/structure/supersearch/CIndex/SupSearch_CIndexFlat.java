package edu.psu.chemxseer.structure.supersearch.CIndex;

import java.util.ArrayList;
import java.util.List;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.subsearch.Impl.VerifierISO;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.PostingFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SubgraphSearch;
import edu.psu.chemxseer.structure.supersearch.Impl.PostingBuilderMem;

/**
 * The Supergraph Searcher Corresponding to the CIndexFlat
 * @author dayuyuan
 *
 */
public class SupSearch_CIndexFlat implements SubgraphSearch{
	private CIndexFlat searcher;
	private PostingFetcher postingFetcher;
	private VerifierISO verifier;
	
	public SupSearch_CIndexFlat(CIndexFlat searcher, PostingFetcher postingFetcher,
			VerifierISO verifier){
		this.searcher = searcher;
		this.postingFetcher = postingFetcher;
		this.verifier = verifier;
	}
	/**
	 * Filter + Verification Approach finding the answer for supergraph search
	 * @param query
	 * @param TimeComponent
	 * @param Number
	 * @return
	 */
	public List<GraphResult> getAnswer(Graph query, long[] TimeComponent, int[] Number){
		ArrayList<Integer> noContainedSubs = searcher.getNoSubgraphs(query, TimeComponent);
		GraphFetcher candidateFetcher = postingFetcher.getComplement(noContainedSubs, TimeComponent);
		Number[0] = candidateFetcher.size();
		List<GraphResult> answer =verifier.verify(query, candidateFetcher, false,TimeComponent);
		Number[1] = answer.size();
		return answer;
	}
	/**
	 * 
	 * @param query
	 * @param subsID: subgraph features that are contained in the query
	 * @param TimeComponent
	 * @param Number
	 * @return
	 */
	public List<GraphResult> getAnswer(Graph query, int[] subsID,long[] TimeComponent, int[] Number){
		ArrayList<Integer> noContainedSubs = searcher.getNoSubgraphs(subsID, TimeComponent);
		GraphFetcher candidateFetcher = postingFetcher.getComplement(noContainedSubs, TimeComponent);
		Number[0] = candidateFetcher.size();
		List<GraphResult> answer =verifier.verify(query, candidateFetcher, false,TimeComponent);
		Number[1] = answer.size();
		return answer;
	}
	public static String getIndexName() {
		return "index";
	}
	public static String getLuceneName() {
		return "lucene";
	}
	public String[] getIndexFeatures() {
		return this.searcher.indexingGraphs;
	}
	@Override
	public PostingBuilderMem getInMemPosting() {
		return this.postingFetcher.loadPostingIntoMemory(this.searcher);
	}
}
