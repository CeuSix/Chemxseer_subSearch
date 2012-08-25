package edu.psu.chemxseer.structure.subsearch.Lindex;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.subsearch.Impl.VerifierISO;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.PostingFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SubgraphSearch;
import edu.psu.chemxseer.structure.supersearch.Impl.PostingBuilderMem;
import edu.psu.chemxseer.structure.util.SelfImplementSet;

public class SubgraphSearch_Lindex implements SubgraphSearch{
	public LindexSearcher indexSearcher;
	private PostingFetcher postingFetcher;
	private VerifierISO verifier;

	public SubgraphSearch_Lindex(LindexSearcher indexSearcher, PostingFetcher postingFetcher, VerifierISO verifier){
		this.indexSearcher = indexSearcher;
		this.postingFetcher = postingFetcher;
		this.verifier = verifier;
	}

	
	public  List<GraphResult> getAnswer(Graph query, long[] TimeComponent, int[] Number)
	throws IOException, ParseException {
		// First look for g's subgraphs
		TimeComponent[0] = TimeComponent[1] = TimeComponent[2]= TimeComponent[3]=0;
		Number[0] = Number[1] = 0;

		List<GraphResult> answer = null;
		GraphFetcher trueFetcher = null;
		List<Integer> maxSubgraphs = indexSearcher.maxSubgraphs(query, TimeComponent);

		if(maxSubgraphs!=null && maxSubgraphs.get(0) == -1){// graph g hits on one of the index term
			GraphFetcher answerFetcher= this.postingFetcher.getPosting(maxSubgraphs.get(1), TimeComponent);
			answer = answerFetcher.getAllGraphs(TimeComponent);
			Number[0] = 0;
		}
		else{
			List<Integer> superGraphs = indexSearcher.minimalSupergraphs(query, TimeComponent, maxSubgraphs);
			GraphFetcher candidateFetcher = this.postingFetcher.getJoin(maxSubgraphs, TimeComponent);

			SelfImplementSet<GraphResult> set = new SelfImplementSet<GraphResult>();
			if(superGraphs!=null && superGraphs.size()>0){
				trueFetcher = this.postingFetcher.getUnion(superGraphs, TimeComponent);
				candidateFetcher  = candidateFetcher.remove(trueFetcher);
			}
			Number[0] = candidateFetcher.size();
			answer = this.verifier.verify(query, candidateFetcher,true, TimeComponent);
			if(trueFetcher!=null && trueFetcher.size()>0){
				set.clear();
				set.addAll(answer);
				set.addAll(trueFetcher.getAllGraphs(TimeComponent));
				answer = set.getItems();
			}
		}
		Number[1] = answer.size();
		return answer;
	}

	public static String getLuceneName() {
		return "lucene/";
	}

	public static String getIndexName() {
		return "index";
	}


	public PostingFetcher getPostingFetcher() {
		return this.postingFetcher;
	}


	public VerifierISO getVerifier() {
		return this.verifier;
	}


	@Override
	public PostingBuilderMem getInMemPosting() {
		return postingFetcher.loadPostingIntoMemory(this.indexSearcher);	
	}



}
