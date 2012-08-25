package edu.psu.chemxseer.structure.supersearch.Impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.iso.FastSU;
import edu.psu.chemxseer.structure.iso.FastSUCompleteEmbedding;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphFetcherPrefix;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResultPref;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IndexSearcherPrefix;

/**
 * The Implementation of the Verifier with Prefix Embedding available
 * Important: the input candidateFetcher must be prefixFetcher, which can return graphResultPrefix
 * @author dayuyuan
 *
 */
public class VerifierISOPrefix {
	private IndexSearcherPrefix prefSearcher;
	// StoreEmbedding = true, then all the embedding from the "answer" & query is recorded
	private boolean storeEmbedding;
	private Map<GraphResult, FastSUCompleteEmbedding> storedEmbeddings;
	
	/**
	 * Constructor of the VeirifierISO Prefix
	 * @param prefSearcher
	 * @param storeEmbedding
	 */
	public VerifierISOPrefix(IndexSearcherPrefix prefSearcher, boolean storeEmbedding){
		this.prefSearcher = prefSearcher;
		this.storeEmbedding = storeEmbedding;
		if(storeEmbedding)
			this.storedEmbeddings = new HashMap<GraphResult, FastSUCompleteEmbedding>();
	}
	/**
	 * For Supergraph Search Use Only
	 * The Mapping from the "indexing features" to the query graph will be fetched
	 * This mapping will be extended to mappings from the "candidate graph" to the query
	 * @param candidateFetcher
	 * @param TimeComponent
	 * @return
	 */
	public  List<GraphResult> verify(GraphFetcherPrefix candidateFetcher,Graph query,
			long[] TimeComponent) {

		if(candidateFetcher == null || candidateFetcher.size() == 0)
			return new ArrayList<GraphResult>();
		else {
			if(this.storeEmbedding)
				this.storedEmbeddings.clear();
			List<GraphResult> answerSet = new ArrayList<GraphResult>();
			
			List<GraphResultPref> candidates = candidateFetcher.getGraphs(TimeComponent);
			long start = System.currentTimeMillis();
			while(candidates!=null){
				for(int i = 0; i< candidates.size(); i++){
					GraphResultPref oneCandidate = (GraphResultPref) candidates.get(i);
					if(oneCandidate.getPrefixFeatureID()!=-1){
						int[][] suffix = oneCandidate.getSuffix();
						if(suffix!=null){
							FastSUCompleteEmbedding prefixEmbedding = 
								this.prefSearcher.getEmbedding(oneCandidate.getPrefixFeatureID(), query);
							if(prefixEmbedding == null)
								continue; // this is not an answer
							if(this.storeEmbedding){
								FastSUCompleteEmbedding newEmbedding = 
									new FastSUCompleteEmbedding(prefixEmbedding,suffix);
								if(newEmbedding.issubIsomorphic()){
									answerSet.add(oneCandidate);
									this.storedEmbeddings.put(oneCandidate, newEmbedding);
								}
							}
							else{
								FastSU su = new FastSU();
								boolean iso = su.isIsomorphic(prefixEmbedding, suffix);
								if(iso)
									answerSet.add(oneCandidate);
							}
						}
						else // the database graph is the same as the prefix feature
						{
							answerSet.add(candidates.get(i));
							if(this.storeEmbedding)
								this.storedEmbeddings.put(oneCandidate, 
										this.prefSearcher.getEmbedding(oneCandidate.getPrefixFeatureID(), query));
						}
					}
					else{
						// PrefixFeatureID = -1;
						if(this.storeEmbedding){
							FastSUCompleteEmbedding newEmbedding = new FastSUCompleteEmbedding(oneCandidate.getG(), query);
							if(newEmbedding.issubIsomorphic()){
								answerSet.add(oneCandidate);
								this.storedEmbeddings.put(oneCandidate, newEmbedding);
							}
						}
						else{
							FastSU su = new FastSU();
							boolean iso = su.isIsomorphic(oneCandidate.getG(), query); 
							if(iso)
								answerSet.add(candidates.get(i));
						}
					}
					
				}
				TimeComponent[3] += System.currentTimeMillis()-start;
				candidates = candidateFetcher.getGraphs(TimeComponent);
				start = System.currentTimeMillis();
			}
			return answerSet;
		}
	}

	/**
	 * For Supergraph Search Only
	 * The Mapping from the "indexing features" to the query graph will be fetched
	 * This mapping will be extended to mappings from the "candidate graph" to the query
	 * @param candidateFetcher
	 * @param TimeComponent
	 * @return
	 */
	public List<GraphResult> verifyFalse(GraphFetcher candidateFetcher, Graph query, long[] TimeComponent) {

		if(candidateFetcher == null || candidateFetcher.size() == 0)
			return new ArrayList<GraphResult>();
		
		else {
			if(this.storeEmbedding)
				this.storedEmbeddings.clear();
			List<GraphResult> answerSet = new ArrayList<GraphResult>();
			List<GraphResult> candidates = candidateFetcher.getGraphs(TimeComponent);
			long start = System.currentTimeMillis();
			while(candidates!=null){
				for(int i = 0; i< candidates.size(); i++){
					GraphResultPref oneCandidate = (GraphResultPref) candidates.get(i);
					FastSUCompleteEmbedding prefixEmbedding = 
						this.prefSearcher.getEmbedding(oneCandidate.getPrefixFeatureID(), query);
					FastSUCompleteEmbedding newEmbedding = 
						new FastSUCompleteEmbedding(prefixEmbedding,oneCandidate.getG());
					if(!newEmbedding.issubIsomorphic()){
						answerSet.add(candidates.get(i));
					}
					else if(this.storeEmbedding)
						this.storedEmbeddings.put(candidates.get(i), newEmbedding);
				} 
				TimeComponent[3] += System.currentTimeMillis()-start;
				candidates = candidateFetcher.getGraphs(TimeComponent);
				start = System.currentTimeMillis();
			}
			return answerSet;
		}
	}
	/**
	 * If the Verifier stored the embeddings, return the embeddings
	 * else return null;
	 * @return
	 */
	public Map<GraphResult, FastSUCompleteEmbedding> getEmbeddings(){
		if(this.storeEmbedding)
			return this.storedEmbeddings;
		else return null;
	}
}
