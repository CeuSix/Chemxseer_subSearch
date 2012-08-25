package edu.psu.chemxseer.structure.supersearch.Impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import edu.psu.chemxseer.structure.subsearch.Impl.GraphFetcherDB;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphResultNormalPrefix;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphFetcherPrefix;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResultPref;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IndexSearcherPrefix;

public class GraphFetcherDBPrefix implements GraphFetcherPrefix{
	private IndexSearcherPrefix gDB;
	private GraphFetcherDB gFetcher;
	/**
	 * Constructor the GraphFetcherDBPrefix. 
	 * @param gFetcher
	 * @param gDB: the database containing all the features
	 */
	public GraphFetcherDBPrefix( GraphFetcherDB gFetcher, IndexSearcherPrefix gDB){
		this.gFetcher = gFetcher;
		this.gDB = gDB;
	}
	/**
	 * The Only Different is that here the getGraphs return the "GraphREsultNormalPrefix"
	 * which implements the GraphResultPrefix interface
	 */
	public List<GraphResultPref> getGraphs(long[] TimeComponent) {
		if(gFetcher.start == gFetcher.gIDs.length)
			return null;
		else{
			long startTime = System.currentTimeMillis();
			int end = Math.min(gFetcher.start+batchCount, gFetcher.gIDs.length);
			List<GraphResultPref> results = new ArrayList<GraphResultPref>();
			for(int i = gFetcher.start; i < end; i++){
				int prefixID = this.gDB.getPrefixID(gFetcher.gIDs[i]);
				GraphResultPref temp = new GraphResultNormalPrefix(gFetcher.gIDs[i],prefixID, gDB.getExtension(gFetcher.gIDs[i]), gDB);
				results.add(temp);
			}
			gFetcher.start = end;
			TimeComponent[1] += System.currentTimeMillis() - startTime;
			return results;
		}
	}
	@Override
	public List<GraphResultPref> getAllGraphs(long[] TimeComponent){
		List<GraphResultPref> answer = new ArrayList<GraphResultPref>();
		List<GraphResultPref> temp = this.getGraphs(TimeComponent);
		while(temp!=null){
			answer.addAll(temp);
			temp = this.getGraphs(TimeComponent);
		}
		Collections.sort(answer);
		return answer;
	}
	@Override
	public int[] getIDs() {
		return gFetcher.getIDs();
	}
	@Override
	public GraphFetcherPrefix join(GraphFetcher fetcher) {
		this.gFetcher.join(fetcher);
		return this;
	}
	@Override
	public GraphFetcherPrefix remove(GraphFetcher fetcher) {
		gFetcher.remove(fetcher);
		return this;
	}
	@Override
	public int size() {
		return gFetcher.size();
	}
	
}
