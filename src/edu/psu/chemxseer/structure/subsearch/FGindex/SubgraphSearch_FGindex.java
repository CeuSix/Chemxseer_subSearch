package edu.psu.chemxseer.structure.subsearch.FGindex;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;


import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphDatabase_OnDisk;
import edu.psu.chemxseer.structure.subsearch.Impl.VerifierISO;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.PostingFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SubgraphSearch;
import edu.psu.chemxseer.structure.supersearch.Impl.PostingBuilderMem;
import edu.psu.chemxseer.structure.util.SelfImplementSet;

public class SubgraphSearch_FGindex implements SubgraphSearch{
	private FGindex in_memoryIndex;
	private VerifierISO verifier;
	private PostingFetcher onDiskPostingFetcher;
	private String baseName;

	
	public SubgraphSearch_FGindex(FGindex in_memoryIndex, VerifierISO verifier, PostingFetcher onDiskPostings, String baseName){
		this.in_memoryIndex = in_memoryIndex;
		this.verifier = verifier;
		this.onDiskPostingFetcher = onDiskPostings;
		this.baseName = baseName;
	}
	
	public  List<GraphResult> getAnswer(Graph query, long[] TimeComponent, int[] Number)
			throws IOException, ParseException {
		TimeComponent[0] = TimeComponent[1] = TimeComponent[2] = TimeComponent[3] = 0;
		Number[0] = Number[1] = 0;
		 List<GraphResult> answers = null;
		
		int[] hitIndex = new int[1];
		hitIndex[0]=-1;
		// 1. In-memory Index lookup
		answers = in_memoryIndex.hitAndReturn(query, hitIndex, TimeComponent);
		if(answers!=null){
			Number[1] = answers.size();
			return answers; // find a hit and return
		}
		// 2. Load the on-disk index
		if(hitIndex[0] >=0){
			int onDiskIndexID = hitIndex[0];
			FGindex on_diskIndex = loadOndiskIndex(hitIndex[0], TimeComponent);
			if(on_diskIndex != null){
				hitIndex[0]=-1;
				answers = on_diskIndex.hitAndReturn(query, onDiskIndexID,hitIndex, TimeComponent);
				if(hitIndex[0] >= 0){
					Number[1] = answers.size();
					return answers;
				}
			}
		}
		// 3. Filtering + verification
		GraphFetcher candidateFetcher;
		 GraphFetcher r1 = in_memoryIndex.candidateByFeatureJoin(query, TimeComponent);
		 GraphFetcher r2 = in_memoryIndex.candidateByEdgeJoin(query, TimeComponent);
		if(r1 == null || r1.size() == 0)
			candidateFetcher = r2;
		else if(r2 == null || r2.size()==0)
			candidateFetcher = r1;
		else{
			candidateFetcher = r1.join(r2);
		}
		Number[0] = candidateFetcher.size();
		answers =  verifier.verify(query, candidateFetcher, true, TimeComponent);
		Number[1] = answers.size();
		return answers;
 	}
	/**
	 * Load the onDisk index, counted as the index loopup time
	 * @param TCFGId
	 * @param TimeComponent
	 * @return
	 * @throws IOException
	 */
	private FGindex loadOndiskIndex(int TCFGId, long[] TimeComponent) throws IOException {
		long start = System.currentTimeMillis();
		FGindexSearcher searcher = FGindexConstructor.loadSearcher(baseName, getOnDiskIndexName(TCFGId), null); // empty graphdatabase
		FGindex onDiskIGI = new FGindex(searcher, onDiskPostingFetcher);
		TimeComponent[2] += System.currentTimeMillis()-start;
		return onDiskIGI;
	}
	
	/**********This part will be replace to configuration file latter***************/
	private static String onDiskBase = "onDiskIndex/";
	public static String getOnDiskIndexName(int TCFGID){
		return onDiskBase + TCFGID;
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
	
	public EdgeIndex getEdgeIndex(){
		return this.in_memoryIndex.getEdgeIndex();
	}

	@Override
	public PostingBuilderMem getInMemPosting() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
