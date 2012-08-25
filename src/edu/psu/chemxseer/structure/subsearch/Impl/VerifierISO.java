package edu.psu.chemxseer.structure.subsearch.Impl;


import java.util.ArrayList;
import java.util.List;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.iso.FastSU;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;

/**
 * Verification using the fastSu verifier.
 * @author dayuyuan
 *
 */
public class VerifierISO{
	private FastSU fastSu;
	
	
	public VerifierISO(){
		fastSu = new FastSU();
	}
	/**
	 * If order == true, query subgraph isomorphic to answer [subgraph search]
	 * If order == false, query supergraph isomorphic to answer [supergraph search]
	 * @param query
	 * @param candidateFetcher
	 * @param order
	 * @param TimeComponent
	 * @return
	 */
	public  List<GraphResult> verify(Graph query,  GraphFetcher candidateFetcher, boolean order,
			long[] TimeComponent) {
		if(candidateFetcher == null || candidateFetcher.size() == 0)
			return new ArrayList<GraphResult>();
		else {
			List<GraphResult> answerSet = new ArrayList<GraphResult>();
			List<GraphResult> candidates = candidateFetcher.getGraphs(TimeComponent);
			long start = System.currentTimeMillis();
			while(candidates!=null){
				for(int i = 0; i< candidates.size(); i++){
					if(order && fastSu.isIsomorphic(query, candidates.get(i).getG()))
						answerSet.add(candidates.get(i));
					
					else if(!order && fastSu.isIsomorphic(candidates.get(i).getG(),query))
						answerSet.add(candidates.get(i));
				}
				TimeComponent[3] += System.currentTimeMillis()-start;
				candidates = candidateFetcher.getGraphs(TimeComponent);
				start = System.currentTimeMillis();
			}
			return answerSet;
		}
	}
	/**
	 * If order == true, query subgraph not isomorphic to answer [subgraph search]
	 * If order == false, query supergraph not isomorphic to answer [supergraph search]
	 * @param query
	 * @param candidateFetcher
	 * @param order
	 * @param TimeComponent
	 * @return
	 */
	public List<GraphResult> verifyFalse(Graph query,
			GraphFetcher candidateFetcher, boolean order, long[] TimeComponent) {
		if(candidateFetcher == null || candidateFetcher.size() == 0)
			return new ArrayList<GraphResult>();
		else {
			 List<GraphResult> answerSet = new ArrayList<GraphResult>();
			 List<GraphResult> candidates = candidateFetcher.getGraphs(TimeComponent);
			 long start = System.currentTimeMillis();
			 while(candidates!=null){
					for(int i = 0; i< candidates.size(); i++){
						if(order && !fastSu.isIsomorphic(query, candidates.get(i).getG()))
							answerSet.add(candidates.get(i));
						
						else if(!order && !fastSu.isIsomorphic(candidates.get(i).getG(),query))
							answerSet.add(candidates.get(i));
					}
					TimeComponent[3] += System.currentTimeMillis()-start;
					candidates = candidateFetcher.getGraphs(TimeComponent);
					start = System.currentTimeMillis();
				}
			return answerSet;
		}
	}
}
