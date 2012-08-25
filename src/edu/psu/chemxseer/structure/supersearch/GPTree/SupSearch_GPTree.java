package edu.psu.chemxseer.structure.supersearch.GPTree;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphFetcherLucene;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphFetcherLucenePrefix;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.PostingFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SubgraphSearch;
import edu.psu.chemxseer.structure.supersearch.Impl.PostingBuilderMem;
import edu.psu.chemxseer.structure.supersearch.Impl.VerifierISOPrefix;

public class SupSearch_GPTree implements SubgraphSearch{
	protected GPTreeSearcher dbPrefix; // Data base graphs prefix index
	protected VerifierISOPrefix verifier;
	
	protected CRGraphSearcher crGraph; // CRGraph for Filtering
	protected PostingFetcher positngFetcher;
	
	/**
	 * Construct a SupSearch_GPTree
	 * @param crGraph
	 * @param postingFetcher
	 * @param verifier
	 * @param prefixSearcher
	 */
	public SupSearch_GPTree(CRGraphSearcher crGraph, PostingFetcher postingFetcher, 
			VerifierISOPrefix verifier, GPTreeSearcher prefixSearcher){
		this.crGraph = crGraph;
		this.positngFetcher = postingFetcher;
		this.verifier = verifier;
		this.dbPrefix = prefixSearcher;
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
		//1. First Step, Filtering
		List<Integer> noSubgraphs = crGraph.getNonSubgraphs(query, TimeComponent);
		//2. Filtering
		GraphFetcher candidateFetcher = this.positngFetcher.getComplement(noSubgraphs, TimeComponent);
		GraphFetcherLucenePrefix candidateFetcherPrefix = 
			new GraphFetcherLucenePrefix((GraphFetcherLucene)candidateFetcher,dbPrefix);
		//3. Verify
		Number[0] = candidateFetcherPrefix.size();
		List<GraphResult> answer = verifier.verify(candidateFetcherPrefix, query,TimeComponent);
		Number[1] = answer.size();
		return answer;
	}
	
	
	public static String getCRPrefixName() {
		String result = new String("CRGraphPrefixIndex");
		return result;
	}
	
	public static String getCRGraphName(){
		String result = new String("CRGraph");
		return result;
	}
	
	public static String getLucene(){
		return "lucene";
	}
	
	public static String getPrefixIndex(){
		return "prefixIndex";
	}
	@Override
	public PostingBuilderMem getInMemPosting() {
		return this.positngFetcher.loadPostingIntoMemory(this.crGraph);
	}
	
}
