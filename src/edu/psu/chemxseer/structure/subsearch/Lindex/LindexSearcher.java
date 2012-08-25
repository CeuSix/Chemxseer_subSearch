package edu.psu.chemxseer.structure.subsearch.Lindex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.iso.CanonicalDFS;
import edu.psu.chemxseer.structure.iso.FastSU;
import edu.psu.chemxseer.structure.iso.FastSUCompleteEmbedding;
import edu.psu.chemxseer.structure.iso.FastSUStateExpandable;
import edu.psu.chemxseer.structure.iso.GraphComparator;
import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IndexSearcher2;
import edu.psu.chemxseer.structure.util.IntersectionSet;

public class LindexSearcher implements IndexSearcher2{

	protected LindexTerm[] indexTerms; 
	// Array of Index Terms, their id is consistent with their index on This array 
	protected LindexTerm dummyHead; 
	protected GraphComparator gComparator;
	
	public LindexSearcher(LindexTerm[] indexTerms, LindexTerm dummyHead){
		this.indexTerms = indexTerms;
		this.dummyHead = dummyHead;
		gComparator = new GraphComparator();
	}
	/**
	 * Conpy constructor
	 * @param searcher
	 */
	protected LindexSearcher(LindexSearcher searcher) {
		this.indexTerms = searcher.indexTerms;
		this.dummyHead = searcher.dummyHead;
		this.gComparator = searcher.gComparator;
	}
	/**
	 * For on-disk index only
	 */
	public List<Integer> maxSubgraphs(FastSUCompleteEmbedding fastSu, long[] TimeComponent) {
		long start = System.currentTimeMillis();
		List<Integer> maxSubTermIds = new ArrayList<Integer>();
		LindexTerm[] seeds = this.dummyHead.getChildren();
		boolean[] preciseLocate = new boolean[1];
		preciseLocate[0] = false;
		for(int i = 0; i< seeds.length; i++){
			//THE ONLY Place Difference
			FastSUCompleteEmbedding fastSuExt = new FastSUCompleteEmbedding(fastSu, seeds[i].getExtension());
			//END OF DIfference
			if(fastSuExt.isIsomorphic()){
				//preciseLocate[0] = true;
				List<Integer> result = new ArrayList<Integer>(2);
				result.add( -1);
				result.add(seeds[i].getId());    
				TimeComponent[2] += System.currentTimeMillis()-start;
				return result; // find the precise maxSubTerms
			}	
			else if(fastSuExt.issubIsomorphic()) {// is extendable
				// grows the seed state
				boolean findPrecise = maximumSubgraphSearch(fastSuExt, maxSubTermIds, seeds[i]);
				if(findPrecise) {
					preciseLocate[0] = true;
					break;
				}
			}
			else continue;
		}

		TimeComponent[2] += System.currentTimeMillis()-start;
		// Return Process
		if(preciseLocate[0]){
			List<Integer> results = new ArrayList<Integer>(2);
			results.add( -1);
			results.add(maxSubTermIds.get(0));    
			return results;
		}
		else{
			return maxSubTermIds;
		}
	}
	
	public List<Integer> maxSubgraphs(Graph query, long[] TimeComponent) {
		long start = System.currentTimeMillis();
		List<Integer> maxSubTermIds = new ArrayList<Integer>();
		LindexTerm[] seeds = this.dummyHead.getChildren();
		CanonicalDFS dfsParser = MyFactory.getDFSCoder();
		boolean[] preciseLocate = new boolean[1];
		preciseLocate[0] = false;
		for(int i = 0; i< seeds.length; i++){
			Graph seedGraph = dfsParser.parse(seeds[i].getLabel(), MyFactory.getGraphFactory());
			if(gComparator.compare(seedGraph, query) >0)
				continue;
			FastSUCompleteEmbedding fastSuExt = new FastSUCompleteEmbedding(seedGraph, query);
			if(fastSuExt.isIsomorphic()){
				//preciseLocate[0] = true;
				List<Integer> result = new ArrayList<Integer>(2);
				result.add( -1);
				result.add(seeds[i].getId());    
				TimeComponent[2] += System.currentTimeMillis()-start;
				return result; // find the precise maxSubTerms
			}	
			else if(fastSuExt.issubIsomorphic()) {// is extendable
				// grows the seed state
				boolean findPrecise = maximumSubgraphSearch(fastSuExt, maxSubTermIds, seeds[i]);
				if(findPrecise) {
					preciseLocate[0] = true;
					break;
				}
			}
			else continue;
		}
		TimeComponent[2] += System.currentTimeMillis()-start;
		// Return Process
		if(maxSubTermIds.size() == 0)
			return null;
		if(preciseLocate[0]){
			List<Integer> results = new ArrayList<Integer>(2);
			results.add( -1);
			results.add(maxSubTermIds.get(0));    
			return results;
		}
		else{
			return maxSubTermIds;
		}
	}
	
	private boolean maximumSubgraphSearch(FastSUCompleteEmbedding fastSu, Collection<Integer> maxSubTerms, LindexTerm oriTerm){
		LindexTerm[] children = oriTerm.getChildren();
		boolean extendable = false;
		// No further node to grow
		if(children == null || children.length == 0){
			extendable = false;
		}
		else {
			for(int i = 0;i< children.length; i++){
				if(children[i].getParent()!=oriTerm)
					continue;
				LindexTerm childTerm = children[i];
				FastSUCompleteEmbedding next = new FastSUCompleteEmbedding(fastSu,childTerm.getExtension());
				if(next.isIsomorphic()){
					maxSubTerms.clear();
					maxSubTerms.add(childTerm.getId());
					return true; // find the precise maxSubTerms
				}
				else if(next.issubIsomorphic()){ 
					extendable = true;
					// Further growing to test node children[i], success means precise locate
					boolean success = maximumSubgraphSearch(next,maxSubTerms, children[i]);
					if(success)
						return true;
				}
			}
		}
		FastSUStateExpandable oriState = fastSu.getState();
		if(extendable == false){
			if(oriState.getNodeCountB() == oriState.getNodeCountS()&&
					oriState.getEdgeCountB() == oriState.getEdgeCountS()){
				maxSubTerms.clear();
				maxSubTerms.add(oriTerm.getId());
				return true; // find the precise maxSubTerms
			}
			else maxSubTerms.add(oriTerm.getId());
		}
		return false;
	}
	
	public List<Integer> subgraphs(Graph query, long[] TimeComponent) {
		long start = System.currentTimeMillis();
		List<Integer> subGraphIds = new ArrayList<Integer>();
		LindexTerm[] seeds = this.dummyHead.getChildren();
		CanonicalDFS dfsParser = MyFactory.getDFSCoder();
		for(int i = 0; i< seeds.length; i++){
			Graph seedGraph = dfsParser.parse(seeds[i].getLabel(), MyFactory.getGraphFactory());
			if(seedGraph.getNodeCount() > query.getNodeCount() || seedGraph.getEdgeCount() > query.getEdgeCount())
				continue;
			FastSUCompleteEmbedding fastSuExt = new FastSUCompleteEmbedding(seedGraph, query);
			if(fastSuExt.isIsomorphic()){                            
				subGraphIds.add(seeds[i].getId());
				break;
			}	
			else if(fastSuExt.issubIsomorphic()) {// is expendable
				// grows the seed state
				subGraphIds.add(seeds[i].getId());
				subgraphSearch(fastSuExt, subGraphIds, seeds[i]);
			}
			else continue;
		}
		
		TimeComponent[2] += System.currentTimeMillis()-start;
		return subGraphIds;
	}
	private boolean subgraphSearch(FastSUCompleteEmbedding fastSu, Collection<Integer> maxSubTerms, LindexTerm oriTerm){
		LindexTerm[] children = oriTerm.getChildren();
		// No further node to grow
		if(children == null || children.length == 0){
			return false;
		}
		else {
			for(int i = 0;i< children.length; i++){
				if(children[i].getParent()!=oriTerm)
					continue;
				LindexTerm childTerm = children[i];			

				FastSUCompleteEmbedding next = new FastSUCompleteEmbedding(fastSu,childTerm.getExtension());
				if(next.isIsomorphic()){
					maxSubTerms.add(childTerm.getId());// Keep on Searching
					//return true; // find the precise maxSubTerms
				}
				else if(next.issubIsomorphic()){ 
					maxSubTerms.add(childTerm.getId());
					// Further growing to test node children[i]
					subgraphSearch(next,maxSubTerms, children[i]);
				}
			}
		}
		return false;
	}
	/****
	 * Not Implemented
	 */
	public int designedSubgraph(Graph query, boolean[] exactMatch,
			long[] TimeComponent) {
		return 0;
	}
	
	
	/**
	 * Get the minimal supergraph of the query, the maxSubs[] may be reorganized, and some of them will be assigned to -1, 
	 * since we further detect that they are not real maximal subgraphs
	 */
	public List<Integer> minimalSupergraphs(Graph query, long[] TimeComponent, List<Integer> maxSubs){
		LindexTerm[] terms = this.getTerms(maxSubs);
		List<Integer> result = this.minimalSupergraphs(query, TimeComponent, terms);
		maxSubs.clear();
		for(LindexTerm oneTerm: terms){
			if(oneTerm == null)
				continue;
			else
				maxSubs.add(oneTerm.getId());
		}
		return result;
	}
	
	protected List<Integer> minimalSupergraphs(Graph query, long[] TimeComponent, LindexTerm[] maxSubTerms){
		long start = System.currentTimeMillis();
		//1st: sort maximum subgraphs according to their node number
		Comparator<LindexTerm> compare = new LindexTermComparator();
		//QuickSort.quicksort(maxSubGraphs, compare);
		Arrays.sort(maxSubTerms, compare);
		
		Set<LindexTerm> subgraphsHash = new HashSet<LindexTerm>(maxSubTerms.length);
		for(int i = 0; i< maxSubTerms.length; i++)
			subgraphsHash.add(maxSubTerms[i]);
		
		//2nd: Finding all candidates super graphs as the intersection of offspring of each maximum subgraph
		IntersectionSet allSuperCandidates = new IntersectionSet();
		boolean firstTime = true;
		
		for(int i = 0; i< maxSubTerms.length; i++){
			if(maxSubTerms[i]==null)
				continue;
			else{
				// Start finding the set of maxSubGraphs[i]'s whole set of children
				// In this process if we find a offspring of this term equals to one other maxSubgraph, this subgraph is set to be null
				boolean[] notMaximum = new boolean[1];
				notMaximum[0] = false; // Assume this is the maximum
				int[] offSpringI = getSuperTermsIndex(maxSubTerms[i], subgraphsHash, notMaximum);
				
				if(notMaximum[0]){
					maxSubTerms[i] = null;
					continue; // there is no need of further intersection, since maxSubgraph[i] is not maximum subgraph
				}
				else {
					Arrays.sort(offSpringI);
					if(firstTime){
						allSuperCandidates.addAll(offSpringI);
						firstTime = false;
					}
					else allSuperCandidates.retainAll(offSpringI);			
				}
			}
		}
		// 3rd: Isomorphism test each of those features in allSuperCandidates
		List<Integer> superGraphs = new ArrayList<Integer>();
		FastSU isoTest = new FastSU();
		
		// 3a: first sort all superGraphs candidate in order of growing edge number and node number
		// and save them in order in candidateTerms
		LindexTerm[] candidateSuperGraphTerms = new LindexTerm[allSuperCandidates.size()];
		int[] allSuperCandidatesIds =  allSuperCandidates.getItems();
		for(int i = 0 ; i< candidateSuperGraphTerms.length; i++)
			candidateSuperGraphTerms[i]= indexTerms[allSuperCandidatesIds[i]];
		Arrays.sort(candidateSuperGraphTerms, compare);
		
		// 3b: test each of those candidateSuperGraphTerms
		// If a candidate is tested isomorphism, then all super terms of this candidate
		// are super graphs of the query without need of verification, but they are not
		// minimum super graphs of the query, thus have to be pruned out
		Set<LindexTerm> unMinimumSuperTerms = new HashSet<LindexTerm>();
		for(int i = 0; i<candidateSuperGraphTerms.length; i++){
			LindexTerm candidateI = candidateSuperGraphTerms[i];
			if(unMinimumSuperTerms.contains(candidateI))
				continue;
			//subgraph isomorphism test
			int[][] label = getTermFullLabel(candidateI);
			Graph termGraph = MyFactory.getDFSCoder().parse(label, MyFactory.getGraphFactory());
			if(isoTest.isIsomorphic(query, termGraph)){
				superGraphs.add(candidateI.getId());
				unMinimumSuperTerms.addAll(getSuperTerms(candidateI));
			}
		}
		TimeComponent[2] += System.currentTimeMillis()-start;
		return superGraphs;
	}
	
	/**
	 * get the set of offsprings of LindexTermNew
	 * if an offspring index term of theTerm is in subgraphsHahs, then return null;
	 * @return
	 */
	protected int[] getSuperTermsIndex(LindexTerm term, Set<LindexTerm> subgraphsHash, boolean[] notMaximum){
		// Breadth first search
		Queue<LindexTerm> queue = new LinkedList<LindexTerm>();
		HashSet<LindexTerm> queueSet = new HashSet<LindexTerm>();
		queue.offer(term);
		while(!queue.isEmpty()){
			LindexTerm aterm = queue.poll();
			LindexTerm[] children = aterm.getChildren();
			if(children == null || children.length == 0)
				continue;
			
			for(int i = 0; i< children.length; i++){
				if(!subgraphsHash.contains(children[i])){
					if(!queueSet.contains(children[i])){
						queue.offer(children[i]);
						queueSet.add(children[i]);
					}
					else continue;
				}
				else{
					notMaximum[0] = true;
					return null;
				}
			}
		}
		int[] finalResults = new int[queueSet.size()];
		int iter = 0;
		for(LindexTerm oneTerm: queueSet)
			finalResults[iter++] = oneTerm.getId();
		return finalResults;
	}
	private Collection<LindexTerm> getSuperTerms(LindexTerm term){
		// Breadth first search
		Queue<LindexTerm> queue = new LinkedList<LindexTerm>();
		HashSet<LindexTerm> queueSet = new HashSet<LindexTerm>();
		queue.offer(term);
		while(!queue.isEmpty()){
			LindexTerm aterm = queue.poll();
			LindexTerm[] children = aterm.getChildren();
			if(children == null || children.length == 0)
				continue;
			for(int i = 0; i< children.length; i++){
				if(!queueSet.contains(children[i])){
					queue.offer(children[i]);
					queueSet.add(children[i]);
				}
				else continue;
			}
		}
		
		return queueSet;
	}
	
	protected int[][] getTermFullLabel(LindexTerm theTerm){
		Stack<LindexTerm> terms = new Stack<LindexTerm>();
		int labelLength = 0;
		terms.push(theTerm);
		labelLength +=theTerm.getLabel().length;
		while(terms.peek()!=this.dummyHead){
			LindexTerm currentTerm = terms.peek();
			LindexTerm theParentTerm = currentTerm.getParent();
			if(theParentTerm!=this.dummyHead){
				labelLength +=theParentTerm.getLabel().length;
				terms.push(theParentTerm);
			}
			else break;
		}
		// Link all these labels together
		int[][] fullLabels = new int[labelLength][];
		int fullLabelIndex = 0;
		while(!terms.isEmpty()){
			int[][] partialLabel = terms.pop().getLabel();
			for(int i = 0; i < partialLabel.length;i++){
				fullLabels[fullLabelIndex]=partialLabel[i];
				++fullLabelIndex;
			}
		}
		return fullLabels;
	}
	
	public FastSUCompleteEmbedding designedSubgraph(Graph query, List<Integer> maxSubs, int[] maximumSubgraph, long[] TimeComponent) {
		maximumSubgraph[0]=this.designedSubgraph(maxSubs, TimeComponent);
		long start = System.currentTimeMillis();
		if(maximumSubgraph[0] == -1)
			return null;
		int[][] labels = this.getTermFullLabel(this.indexTerms[maximumSubgraph[0]]);
		FastSUCompleteEmbedding result = new FastSUCompleteEmbedding(labels, query);
		TimeComponent[2] += System.currentTimeMillis()-start;
		return result;
	}
	@Override
	public int designedSubgraph(List<Integer> maxSubs, long[] TimeComponent) {
		LindexTerm[] terms = this.getTerms(maxSubs);
		return this.designedSubgraph(terms, TimeComponent);
	}
	
	protected int designedSubgraph(LindexTerm[] maxSubTerms, long[] TimeComponent){
		long start = System.currentTimeMillis();
		int minimumFrequency = Integer.MAX_VALUE;
		int theMaximumDepth = 0;
		int theMaximumID = 0;
		// find the maximum subgraphs among those maxSubTerms
		for(int w = 0; w< maxSubTerms.length;w++){
			if(maxSubTerms[w] == null)
				continue;
			LindexTerm theTermW = maxSubTerms[w];
			int[][] labels = this.getTermFullLabel(theTermW);
			if(labels == null || labels.length==0){
				System.out.println("It is so Wired in LindexCompleteAdvance: constructOnDisk: illegal label");
				TimeComponent[2] += System.currentTimeMillis()-start;
				return -1;
			}
			//1. First compare the frequency
			if(theTermW.getFrequency() <  minimumFrequency){
				minimumFrequency = theTermW.getFrequency();
				theMaximumDepth = labels.length;
				theMaximumID = theTermW.getId();
			}
			else if(theTermW.getFrequency() > minimumFrequency)
				continue;
			// equal
			else{
				//2. Compare the depth of labels
				if(labels.length > theMaximumDepth){
					theMaximumDepth = labels.length;
					theMaximumID = theTermW.getId();
				}
				else if(labels.length < theMaximumDepth)
					continue;
				else{
					//3. Compare the ids directly
					if(theTermW.getId() > theMaximumID){
						theMaximumID = theTermW.getId();
					}
				}
			}
		}
		TimeComponent[2] += System.currentTimeMillis()-start;
		return theMaximumID;
	}
	
	protected LindexTerm[] getTerms(List<Integer> termIDs){
		LindexTerm[] terms = new LindexTerm[termIDs.size()];
		for(int i = 0; i< terms.length; i++){
			if(termIDs.get(i) == -1)
				System.out.println("Exception in LindexSearch:getTerm(List<Integer> termIDs)");
			else
				terms[i] = this.indexTerms[termIDs.get(i)];
		}
		return terms;
		
	}
	public int getFeatureCount() {
		return this.indexTerms.length;
	}
	@Override
	public int[] getAllFeatureIDs() {
		int[] rest = new int[this.indexTerms.length];
		for(int i = 0; i< rest.length; i++)
			rest[i] = indexTerms[i].getId();
		Arrays.sort(rest);
		return rest;
	}

}
