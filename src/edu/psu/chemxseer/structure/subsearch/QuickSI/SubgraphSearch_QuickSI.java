package edu.psu.chemxseer.structure.subsearch.QuickSI;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.subsearch.Impl.VerifierISO;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IndexSearcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.PostingFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SubgraphSearch;
import edu.psu.chemxseer.structure.supersearch.Impl.PostingBuilderMem;

public class SubgraphSearch_QuickSI implements SubgraphSearch{
	private IndexSearcher indexSearcher;
	private PostingFetcher postingFetcher;
	private VerifierISO verifier;
	
	public SubgraphSearch_QuickSI(IndexSearcher indexSearcher, PostingFetcher postings, VerifierISO verifier){
		this.indexSearcher = indexSearcher;
		this.postingFetcher = postings;
		this.verifier = verifier;
	}
	
	public List<GraphResult> getAnswer(Graph query, long[] TimeComponent, int[] Number)
			throws IOException, ParseException {
		TimeComponent[0] = TimeComponent[1] = TimeComponent[2] = TimeComponent[3] = 0;
		Number[0] = Number[1] = 0;
		List<GraphResult> answer = null;
		// 1. first step: find the sudo maxSubgraphs of graph g
		List<Integer> maxSubs = indexSearcher.maxSubgraphs(query, TimeComponent);
		if(maxSubs.get(0)==-1){
			Number[0] = 0;
			GraphFetcher answerFetcher = this.postingFetcher.getPosting(maxSubs.get(1), TimeComponent);
			answer = answerFetcher.getAllGraphs(TimeComponent);
		}
		// 2. get the candidate set 
		else{	
			GraphFetcher candidateFetcher = this.postingFetcher.getJoin(maxSubs, TimeComponent);
			Number[0] = candidateFetcher.size();
			// 3. verification
			answer = this.verifier.verify(query, candidateFetcher, true,TimeComponent);
		}
		Number[1] = answer.size();
		return answer;
	}

	public static String getLuceneName() {
		return "lucene/";
	}

	public static String getIndexName() {
		return "index/";
	}

	@Override
	public PostingBuilderMem getInMemPosting() {
		// TODO not implemented
		return null;
	}
	
//	public void test(Graph g, int gID){
//		long[] time = new long[4];
//		int[] subs = this.indexSearcher.subgraphs(g, time);
//		for(int i = 0; i< subs.length;i++){
//			List<GraphResult> postings = this.postingFetcher.getPosting(subs[i], time);
//			boolean contain = false;
//			for(GraphResult onePos : postings){
//				if(onePos.getID() == gID){
//					contain = true;
//					break;
//				}
//			}
//			if(contain==false){
//				System.out.println("Ill build " + gID  + "," + subs.length);
//			}
//		}
//	}

}
