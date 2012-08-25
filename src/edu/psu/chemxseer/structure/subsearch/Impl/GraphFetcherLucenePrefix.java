package edu.psu.chemxseer.structure.subsearch.Impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;


import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphFetcherPrefix;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResultPref;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IndexSearcherPrefix;

public class GraphFetcherLucenePrefix implements GraphFetcherPrefix{
	private IndexSearcherPrefix prefixIndex;
	private GraphFetcherLucene lucene;
	/**
	 * Current support the DFS graph parser only
	 * @param lucene
	 * @param prefixIndex: the Index Storing the Prefix Features of the Database Graphs
	 */
	public GraphFetcherLucenePrefix(GraphFetcherLucene lucene, IndexSearcherPrefix prefixIndex) {
		this.lucene = lucene;
		this.prefixIndex = prefixIndex;
	}


	@Override
	/**
	 * The difference is that GraphFetcherLucenePrefix return the GraphResultPrefix
	 */
	public List<GraphResultPref> getGraphs(long[] TimeComponent){
		if(lucene.start == lucene.scoreDocs.length)
			return null; // no graphs need to return
		else{
			long startTime = System.currentTimeMillis();
			int end = Math.min(lucene.start+batchCount, lucene.scoreDocs.length);
			List<GraphResultPref> results = new ArrayList<GraphResultPref>();
			for(int i = lucene.start; i < end; i++){
				int docID = lucene.scoreDocs[i].doc;
				Document graphDoc = null;
				try {
					graphDoc = lucene.searcher.doc(docID);
				} catch (CorruptIndexException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(graphDoc!=null)
					results.add(new GraphResultLucenePrefix(graphDoc, docID, this.prefixIndex));
			}
			lucene.start = end;
			TimeComponent[1] += System.currentTimeMillis()-startTime;
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
		return lucene.getIDs();
	}


	@Override
	public GraphFetcherPrefix join(GraphFetcher fetcher) {
		lucene.join(fetcher);
		return this;
	}


	@Override
	public GraphFetcherPrefix remove(GraphFetcher fetcher) {
		lucene.remove(fetcher);
		return this;
	}


	@Override
	public int size() {
		return lucene.size();
	}
}
