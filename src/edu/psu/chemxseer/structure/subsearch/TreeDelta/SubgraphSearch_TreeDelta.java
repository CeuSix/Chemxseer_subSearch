package edu.psu.chemxseer.structure.subsearch.TreeDelta;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;


import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.subsearch.Impl.VerifierISO;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IndexSearcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.PostingFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SubgraphSearch;
import edu.psu.chemxseer.structure.supersearch.Impl.PostingBuilderMem;
import edu.psu.chemxseer.structure.util.SelfImplementSet;

public class SubgraphSearch_TreeDelta implements SubgraphSearch{
	private IndexSearcher treeSearcher;
	private IndexSearcher deltaSearcher;
	private VerifierISO verifier;
	private PostingFetcher treeFetcher;
	private PostingFetcher deltaFetcher;
	
	public SubgraphSearch_TreeDelta(IndexSearcher treeSearcher, IndexSearcher deltaSearcher, 
			PostingFetcher posting1, PostingFetcher posting2, VerifierISO verif){
		this.treeSearcher = treeSearcher;
		this.deltaSearcher = deltaSearcher;
		this.verifier = verif;
		this.treeFetcher = posting1;
		this.deltaFetcher = posting2;
	}

	public List<GraphResult> getAnswer(Graph query, long[] TimeComponent, int[] Number)
			throws IOException, ParseException {
		TimeComponent[0] = TimeComponent[1] =TimeComponent[2] =TimeComponent[3]= 0;
		Number[0] = Number [1] = 0;
		List<GraphResult> answer = null;
		// 0. Step one: hit & return
		boolean[] exactMatch = new boolean[1];
		int fID = treeSearcher.designedSubgraph(query, exactMatch, TimeComponent);
		if(fID > 0){
			GraphFetcher answerFetcher = treeFetcher.getPosting(fID, TimeComponent);
			answer = answerFetcher.getAllGraphs(TimeComponent);
			Number[1] = answer.size();
			return answer;
		}
		if(query.getEdgeCount() >= query.getNodeCount()){
			fID = deltaSearcher.designedSubgraph(query, exactMatch, TimeComponent);
			if(fID > 0){
				GraphFetcher answerFetcher = deltaFetcher.getPosting(fID, TimeComponent);
				answer = answerFetcher.getAllGraphs(TimeComponent);
				Number[1] = answer.size();
				return answer;
			}
		}
		
		// 1. first step: find the sudo maxSubgraphs of graph g
		List<Integer> maxSubs1 = treeSearcher.maxSubgraphs(query, TimeComponent);
		List<Integer> maxSubs2 = null;
		if(query.getEdgeCount()>= query.getNodeCount()){
			maxSubs2=deltaSearcher.maxSubgraphs(query, TimeComponent);
		}
		// 2. get the candidate set 
		GraphFetcher candidateFetcher = null;
		GraphFetcher candidateFetcher1 = null, candidateFetcher2 = null;
		if(maxSubs1!=null && maxSubs1.size()==0){
			candidateFetcher1 = treeFetcher.getJoin(maxSubs1, TimeComponent);
		}
		if(maxSubs2!=null && maxSubs2.size()!=0){
			candidateFetcher2 = deltaFetcher.getJoin(maxSubs2, TimeComponent);
		}
		SelfImplementSet<GraphResult> set = new SelfImplementSet<GraphResult>();
		if(candidateFetcher1!=null && candidateFetcher2!=null){
			candidateFetcher = candidateFetcher1.join(candidateFetcher2);
		}
		else if(candidateFetcher1!=null)
			candidateFetcher = candidateFetcher1;
		else if(candidateFetcher2!=null)
			candidateFetcher = candidateFetcher2;
		else{
			return null;
		}
		
		Number[0] = candidateFetcher.size();
		// 3. verification
		answer = this.verifier.verify(query, candidateFetcher, true,TimeComponent);
		Number[1] = answer.size();
		return answer;
	}

	public static String getLuceneName() {
		// TODO Auto-generated method stub
		return "lucene/";
	}

	public static String getDeltaIndexName() {
		return "dIndex";
	}

	public static Object getDeltaFeature() {
		return "dFeatures";
	}

	@Override
	public PostingBuilderMem getInMemPosting() {
		// TODO Auto-generated method stub
		return null;
	}

}
