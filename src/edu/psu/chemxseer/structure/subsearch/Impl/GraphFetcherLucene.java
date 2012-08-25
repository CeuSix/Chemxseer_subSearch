package edu.psu.chemxseer.structure.subsearch.Impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.FieldSelector;
import org.apache.lucene.document.LoadFirstFieldSelector;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;

import de.parmol.parsers.GraphParser;

import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphFetcher;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphFetcherPrefix;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IndexSearcherPrefix;
import edu.psu.chemxseer.structure.util.SelfImplementSet;
import edu.psu.chemxseer.structure.util.SelfImplementSet2;

/**
 * Lucene Lazy Fetcher all The Documents that need to be returned
 * @author dayuyuan
 *
 */
public class GraphFetcherLucene implements GraphFetcher{
	protected IndexSearcher searcher;
	protected ScoreDoc[] scoreDocs;
	protected int start;
	protected GraphParser gParser;
	
	public GraphFetcherLucene(IndexSearcher searcher, TopDocs hits, GraphParser gParser){
		this.searcher = searcher;
		this.scoreDocs = hits.scoreDocs;
		this.start = 0;
		this.gParser = gParser;
		Arrays.sort(scoreDocs, new DocComparator());
	}

	public GraphFetcherLucene(GraphFetcherLucene lucene) {
		this.searcher = lucene.searcher;
		this.scoreDocs = lucene.scoreDocs;
		this.start = 0;
		this.gParser = null;
		Arrays.sort(scoreDocs, new DocComparator());
	}

	@Override
	public List<GraphResult> getGraphs(long[] TimeComponent){
		if(start == scoreDocs.length)
			return null; // no graphs need to return
		else{
			long startTime = System.currentTimeMillis();
			int end = Math.min(start+batchCount, scoreDocs.length);
			List<GraphResult> results = new ArrayList<GraphResult>();
			for(int i = start; i < end; i++){
				int docID = scoreDocs[i].doc;
				Document graphDoc = null;
				try {
					graphDoc = searcher.doc(docID);
				} catch (CorruptIndexException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				if(graphDoc!=null)
					results.add(new GraphResultLucene(gParser, graphDoc, docID));
			}
			start = end;
			TimeComponent[1] += System.currentTimeMillis()-startTime;
			return results;
		}
	}

	@Override
	public int size() {
		return this.scoreDocs.length;
	}

	@Override
	public int[] getIDs() {
		int[] results = new int[this.scoreDocs.length];
		for(int i = 0; i< results.length; i++)
			results[i] = scoreDocs[i].doc;
		return results;
	}

	@Override
	public GraphFetcher join(GraphFetcher fetcher) {
		// A copy of the retain operation of the "SelfImplemntSet or IntersectionSet"
		int[] otherIDs = fetcher.getIDs();
		if(otherIDs == null || otherIDs.length == 0)
			return this; // no need for intersection at all
		int iter = 0, i = 0, j = 0;
		// i is index on item, j is index on c
		while(i < scoreDocs.length && j < otherIDs.length){
			if(scoreDocs[i].doc > otherIDs[j])
				j++;
			else if(scoreDocs[i].doc == otherIDs[j]){
				scoreDocs[iter++] = scoreDocs[i];
				j++;
				i++;
				continue;
			}
			else {// items[i] < c[j]
				i++;
				continue;
			}
		}
		ScoreDoc[] newS = new ScoreDoc[iter];
		for(int w = 0; w< iter; w++)
			newS[w] = this.scoreDocs[w];
		this.scoreDocs = newS;
		return this;
	}

	@Override
	public GraphFetcher remove(GraphFetcher fetcher) {
		// A copy of the retain operation of the "SelfImplemntSet or IntersectionSet"
		int[] otherIDs = fetcher.getIDs();
		if(otherIDs == null || otherIDs.length == 0)
			return this; // no need for intersection at all
		int iter = 0, i = 0, j = 0;
		// i is index on item, j is index on c
		while(i < scoreDocs.length && j < otherIDs.length){
			if(scoreDocs[i].doc > otherIDs[j])
				j++;
			else if(scoreDocs[i].doc == otherIDs[j]){
				j++; // skip this item
				i++;
				continue;
			}
			else {// items[i] < c[j]
				scoreDocs[iter++] = scoreDocs[i];
				i++;
				continue;
			}
		}
		while(i < scoreDocs.length)
			scoreDocs[iter++] = scoreDocs[i++];
		
		
		ScoreDoc[] newS = new ScoreDoc[iter];
		for(int w = 0; w< iter; w++)
			newS[w] = this.scoreDocs[w];
		this.scoreDocs = newS;
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

	class DocComparator implements Comparator<ScoreDoc>{
		@Override
		public int compare(ScoreDoc arg0, ScoreDoc arg1) {
			Integer one = arg0.doc;
			Integer two = arg1.doc;
			return one.compareTo(two);
		}
	}
	
