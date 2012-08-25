package edu.psu.chemxseer.structure.subsearch.QuickSI;

import java.io.IOException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;

import de.parmol.parsers.GraphParser;

import edu.psu.chemxseer.structure.subsearch.Impl.GraphDatabase_OnDisk;
import edu.psu.chemxseer.structure.subsearch.Impl.PostingBuilderLucene;
import edu.psu.chemxseer.structure.subsearch.Impl.PostingBuilderLuceneVectorizerNormal;
import edu.psu.chemxseer.structure.subsearch.Impl.PostingFetcherLucene;
import edu.psu.chemxseer.structure.subsearch.Impl.VerifierISO;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.NoPostingFeatures;
import edu.psu.chemxseer.structure.subsearch.Interfaces.PostingFetcher;

public class SubgraphSearch_QuickSIBuilder {
	/***
	 * No need for feature selection (selected by other methods already)
	 * @param features
	 * @param gDB
	 * @param baseName
	 * @return 
	 * @throws IOException 
	 * @throws LockObtainFailedException 
	 * @throws CorruptIndexException 
	 */
	public SubgraphSearch_QuickSI buildIndex(NoPostingFeatures features, 
			GraphDatabase_OnDisk gDB, String baseName, GraphParser gSerializer, boolean lucene_in_mem) 
			throws CorruptIndexException, LockObtainFailedException, IOException{
		long time1 = System.currentTimeMillis();
		SwiftIndexSearcher swiftIndex = SwiftIndexConstructor.construct(features);
		long time2 = System.currentTimeMillis();
		System.out.println("1. Build SwfitIndex: " +(time2 - time1));
		
		SwiftIndexConstructor.saveSearcher(swiftIndex,baseName, SubgraphSearch_QuickSI.getIndexName());
		time2 = System.currentTimeMillis();
		
		String lucenePath = baseName + SubgraphSearch_QuickSI.getLuceneName();
		PostingBuilderLucene postingBuilder = new PostingBuilderLucene(new PostingBuilderLuceneVectorizerNormal(gSerializer, swiftIndex));
		postingBuilder.buildLuceneIndex(lucenePath, swiftIndex.getFeatureCount(), gDB, null);
		long time3 = System.currentTimeMillis();
		System.out.println("2. Building Lucene for SwiftIndex: " + (time3-time2));
		
		PostingFetcher posting = new PostingFetcherLucene(lucenePath, gDB.getTotalNum(), gSerializer, lucene_in_mem);
		
		return new SubgraphSearch_QuickSI(swiftIndex, posting, new VerifierISO());
	}
	
	public SubgraphSearch_QuickSI loadIndex(GraphDatabase_OnDisk gDB, String baseName, GraphParser gParser, boolean lucene_in_mem) throws IOException{
		SwiftIndexSearcher swiftIndex = SwiftIndexConstructor.loadSearcher(baseName, SubgraphSearch_QuickSI.getIndexName());
		PostingFetcher posting = new PostingFetcherLucene(baseName + SubgraphSearch_QuickSI.getLuceneName(), gDB.getTotalNum(), gParser, lucene_in_mem);
		return new SubgraphSearch_QuickSI(swiftIndex, posting, new VerifierISO());
	}
}
