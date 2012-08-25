package edu.psu.chemxseer.structure.supersearch.CIndex;

import java.util.List;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.subsearch.Impl.VerifierISO;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.PostingFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SubgraphSearch;
import edu.psu.chemxseer.structure.supersearch.Impl.PostingBuilderMem;

public class SupSearch_CIndexTopDown implements SubgraphSearch {
	private CIndexTree indexTreeSearcher;
	private VerifierISO verifier;
	private PostingFetcher postingFetcher;

	public SupSearch_CIndexTopDown(CIndexTree searcher, PostingFetcher postingFetcher,
			VerifierISO verifier){
		this.indexTreeSearcher = searcher;
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
		List<Integer> noContainedSubs = indexTreeSearcher.getNoSubgraphs(query, TimeComponent);
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

	@Override
	public PostingBuilderMem getInMemPosting() {
		return this.postingFetcher.loadPostingIntoMemory(this.indexTreeSearcher);
	}

}
