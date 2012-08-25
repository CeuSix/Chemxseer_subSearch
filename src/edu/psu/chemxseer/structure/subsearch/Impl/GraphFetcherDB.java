package edu.psu.chemxseer.structure.subsearch.Impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphDatabase;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphFetcherPrefix;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IndexSearcherPrefix;
import edu.psu.chemxseer.structure.util.IntersectionSet;

public class GraphFetcherDB implements GraphFetcher{
	public GraphDatabase gDB;
	public int[] gIDs;
	public int start;
	
	/**
	 * Assume that the inputGIDs is well sorted
	 * @param gDB
	 * @param inputGIDs
	 * @param reverse
	 */
	public GraphFetcherDB(GraphDatabase gDB, int[] inputGIDs, boolean reverse){
		this.gDB = gDB;
		if(reverse){
			int totalNum = gDB.getTotalNum();
			this.gIDs =  IntersectionSet.getCompleteSet(inputGIDs, totalNum);
		}
		else {
			this.gIDs = inputGIDs;
		}
	}
	/**
	 * Copy Constructor
	 * @param gFetcher
	 */
	public GraphFetcherDB(GraphFetcherDB gFetcher) {
		this.gDB = gFetcher.gDB;
		this.gIDs = gFetcher.gIDs;
	}

	@Override
	public int[] getIDs(){
		return gIDs;
	}
	@Override
	public List<GraphResult> getGraphs(long[] TimeComponent) {
		if(start == gIDs.length)
			return null;
		else{
			long startTime = System.currentTimeMillis();
			int end = Math.min(start+batchCount, gIDs.length);
			List<GraphResult> results = new ArrayList<GraphResult>();
			for(int i = start; i < end; i++){
				GraphResult temp = new GraphResultNormal(gIDs[i], gDB.findGraph(gIDs[i]));
				results.add(temp);
			}
			start = end;
			TimeComponent[1] += System.currentTimeMillis() - startTime;
			return results;
		}
	}
	@Override
	public int size() {
		return gIDs.length;
	}

	@Override
	public GraphFetcher join(GraphFetcher fetcher) {
		int[] otherIDs = fetcher.getIDs();
		IntersectionSet set = new IntersectionSet();
		set.addAll(gIDs);
		set.retainAll(otherIDs);
		this.gIDs = set.getItems();
		return this;
	}

	@Override
	public GraphFetcher remove(GraphFetcher fetcher) {
		int[] otherIDs = fetcher.getIDs();
		IntersectionSet set = new IntersectionSet();
		set.addAll(gIDs);
		set.removeAll(otherIDs);
		this.gIDs = set.getItems();
		return this;
	}

	
	@Override
	public List<GraphResult> getAllGraphs(long[] TimeComponent){
		List<GraphResult> answer = new ArrayList<GraphResult>();
		List<GraphResult> temp = this.getGraphs(TimeComponent);
		while(temp!=null){
			answer.addAll(temp);
			temp = this.getGraphs(TimeComponent);
		}
		Collections.sort(answer);
		return answer;
	}
	
	

}
