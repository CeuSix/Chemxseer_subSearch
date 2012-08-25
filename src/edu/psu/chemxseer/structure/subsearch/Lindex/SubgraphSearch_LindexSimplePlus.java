package edu.psu.chemxseer.structure.subsearch.Lindex;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.iso.FastSUCompleteEmbedding;
import edu.psu.chemxseer.structure.subsearch.Impl.VerifierISO;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IndexSearcher2;
import edu.psu.chemxseer.structure.subsearch.Interfaces.PostingFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SubgraphSearch;
import edu.psu.chemxseer.structure.supersearch.Impl.PostingBuilderMem;

public class SubgraphSearch_LindexSimplePlus implements SubgraphSearch{
	private IndexSearcher2 indexSearcher;
	private PostingFetcher in_memFetcher;
	private VerifierISO verifier;
	
	private PostingFetcher on_diskFetcher;
	private String baseName;
	
	public SubgraphSearch_LindexSimplePlus(IndexSearcher2 indexSearcher, PostingFetcher in_memFetcher, PostingFetcher
			on_diskFetcher, VerifierISO verifier, String baseName){
		this.indexSearcher = indexSearcher;
		this.in_memFetcher = in_memFetcher;
		this.on_diskFetcher = on_diskFetcher;
		this.verifier = verifier;
		this.baseName = baseName;
	}

	public List<GraphResult> getAnswer(Graph query, long[] TimeComponent, int[] Number)
			throws IOException, ParseException {
		TimeComponent[0] = TimeComponent[1] = TimeComponent[2] = TimeComponent[3] = 0;
		Number[0] = Number[1] = 0;
		
		List<GraphResult> answer = null;
		List<Integer> maxSubgraphs = this.indexSearcher.maxSubgraphs(query, TimeComponent); 
		// In Memory Hit
		if(maxSubgraphs!=null && maxSubgraphs.get(0)==-1){
			 GraphFetcher answerFetcher = this.in_memFetcher.getPosting(maxSubgraphs.get(1),TimeComponent);
			 answer = answerFetcher.getAllGraphs(TimeComponent);
			 Number[0] = 0;
		}
		else{
			// Decide whether to load on-disk Lindex
			//TODO: This is For FGindex Only
			boolean loadOnDisk = true;
			boolean onDisk = false;
			List<Integer> onDiskMaxSubs = null;
			int[] maximumSub = new int[1];
			maximumSub[0] = -1;
			
			if(loadOnDisk){
				// Find the maximum subgraph
				FastSUCompleteEmbedding fastSu = this.indexSearcher.designedSubgraph(query, maxSubgraphs, maximumSub, TimeComponent);
				// Load the on-disk index
				LindexSearcher on_diskIndex = this.loadOndiskIndex(maximumSub[0], TimeComponent);
				if(on_diskIndex != null){
					onDiskMaxSubs = on_diskIndex.maxSubgraphs(fastSu, TimeComponent);
					if(onDiskMaxSubs!=null &onDiskMaxSubs.size() > 0 && onDiskMaxSubs.get(0)==-1){
						onDisk = true;
						GraphFetcher answerFetcher = this.on_diskFetcher.getPosting(maximumSub[0] + "_" + onDiskMaxSubs.get(1), TimeComponent);
						answer = answerFetcher.getAllGraphs(TimeComponent);
						Number[0] = 0;
					}
				}
			}
			if(!loadOnDisk || !onDisk){
				GraphFetcher candidateFetcher = this.in_memFetcher.getJoin(maxSubgraphs, TimeComponent);
				Number[0] = candidateFetcher.size();
				answer = this.verifier.verify(query, candidateFetcher, true,TimeComponent);
				
			}
		}
		Number[1] = answer.size();
		 return answer;
	}
	/**
	 * Load the on disk index, TimeComponent[2], index loopup time
	 * @param in_memoryFeatureID
	 * @param TimeComponent
	 * @return
	 */
	private LindexSearcher loadOndiskIndex(int in_memoryFeatureID, long[] TimeComponent){
		long start = System.currentTimeMillis();
		LindexSearcher on_diskIndex = null;
		try {
			on_diskIndex = LindexConstructor.loadSearcher(baseName, getOnDiskIndexName(in_memoryFeatureID));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		TimeComponent[2] += System.currentTimeMillis()-start;
		return on_diskIndex;
	}
	/**********This part will be replace to configuration file latter***************/
	private static String onDiskBase = "onDiskIndex/";
	public static String getOnDiskIndexName(int id){
		return onDiskBase + id;
	}
	public static String getLuceneName(){
		return "lucene";
	}
	public static String getIn_MemoryIndexName(){
		return "in_memory_index";
	}
	public static String getOnDiskLuceneName(){
		return "onDiskLucene";
	}
	public static String getOnDiskFolderName(){
		return onDiskBase;
	}

	@Override
	public PostingBuilderMem getInMemPosting() {
		return this.in_memFetcher.loadPostingIntoMemory(this.indexSearcher);
	}
	
	
}
