package edu.psu.chemxseer.structure.supersearch.CIndex;

import java.util.ArrayList;
import java.util.List;

import de.parmol.graph.Graph;

import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SubgraphSearch;
import edu.psu.chemxseer.structure.supersearch.Impl.PostingBuilderMem;

/**
 * The bottom up indexing structures of the cIndex
 * features on the ith level must be subgraphs of features on the (i-1) ith level. 
 * Given a query graph q, if f not subgraph isomorphic to q, then the tree covered by f1 need not be examed due 
 * to the exclusive logic
 * 
 * Pay attention: there is no computational sharing at all, just hierarchical structures
 * @author dayuyuan
 *
 */
public class SupSearch_CIndexBottomUp implements SubgraphSearch{
	private List<SupSearch_CIndexFlat> hiIndex;
	
	public SupSearch_CIndexBottomUp(SupSearch_CIndexFlat basicIndex){
		this.hiIndex = new ArrayList<SupSearch_CIndexFlat>();
		this.hiIndex.add(basicIndex);
	}
	
	public void addOneLevel(SupSearch_CIndexFlat oneLevelIndex){
		this.hiIndex.add(oneLevelIndex);
	}
	/**
	 * Given a query Graph, q find all the database graphs contained in q
	 * @param query
	 * @param TimeComponent
	 * @param Number
	 * @return
	 */
	public List<GraphResult> getAnswer(Graph query, long[] TimeComponent, int[] Number){
		if(hiIndex.size() == 1)
			return hiIndex.get(0).getAnswer(query, TimeComponent, Number);
		else{
			long startTime = System.currentTimeMillis();
			long[] tempTime = new long[4];
			int[] tempNum = new int[2];
			int[] IDs = null;
			for(int i = hiIndex.size()-1; i > 0; i--){
				List<GraphResult> upperLevelResults= null;
				if(i == hiIndex.size()-1) // first level
					upperLevelResults = hiIndex.get(i).getAnswer(query, tempTime, tempNum);
				else  // other leverl
					upperLevelResults = hiIndex.get(i).getAnswer(query, IDs, tempTime, tempNum);
				IDs = new int[upperLevelResults.size()];
				for(int w =0 ;w < IDs.length; w++){
					IDs[w] = upperLevelResults.get(w).getDocID();
				}
			}
			// bottom level index
			TimeComponent[2] += System.currentTimeMillis()-startTime; //all filtering time
			return hiIndex.get(0).getAnswer(query, IDs, TimeComponent, Number);
		}
	}

	@Override
	public PostingBuilderMem getInMemPosting() {
		return this.hiIndex.get(0).getInMemPosting();
	}
	
	
}
