package edu.psu.chemxseer.structure.subsearch.Lindex;

import java.io.IOException;
import java.text.ParseException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;

import de.parmol.parsers.GraphParser;

import edu.psu.chemxseer.structure.subsearch.Impl.GraphDatabase_OnDisk;
import edu.psu.chemxseer.structure.subsearch.Impl.PostingBuilderLucene;
import edu.psu.chemxseer.structure.subsearch.Impl.PostingBuilderLuceneVectorizerPartition;
import edu.psu.chemxseer.structure.subsearch.Impl.PostingFetcherLucene2;
import edu.psu.chemxseer.structure.subsearch.Impl.VerifierISO;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.NoPostingFeatures_Ext;
import edu.psu.chemxseer.structure.subsearch.Interfaces.PostingFetcher;

public class SubgraphSearch_LindexBuilder {
	/**
	 * 
	 * @param features
	 * @param gDB
	 * @param baseName
	 * @param gSerializer
	 * @return
	 * @throws CorruptIndexException
	 * @throws LockObtainFailedException
	 * @throws IOException
	 * @throws ParseException
	 */
	public SubgraphSearch_Lindex buildIndex(NoPostingFeatures_Ext features, GraphDatabase_OnDisk gDB, 
			String baseName, GraphParser gSerializer, boolean lucene_in_mem) throws CorruptIndexException, LockObtainFailedException, IOException, ParseException{
		// 0 step: features are all selected
		// 1st step: build the searcher
		long start = System.currentTimeMillis();
		features.mineSubSuperRelation(); 
		long time1 = System.currentTimeMillis();
		System.out.println("1. Mine super-sub graph relationships: " + (time1-start));
		LindexSearcherAdv in_memoryIndex = new LindexSearcherAdv(LindexConstructor.construct(features));
		long time2 = System.currentTimeMillis();
		System.out.println("2. Building Lindex: " + (time2-time1));
		LindexConstructor.saveSearcher(in_memoryIndex,baseName, SubgraphSearch_Lindex.getIndexName());
		
		// 2nd step: build the postings for the in_memoryIndex
		time2 = System.currentTimeMillis();
		PostingBuilderLucene builder = new PostingBuilderLucene(new PostingBuilderLuceneVectorizerPartition(gSerializer, in_memoryIndex));
		builder.buildLuceneIndex(baseName + SubgraphSearch_Lindex.getLuceneName(), in_memoryIndex.getFeatureCount(), gDB, null);
		long time3 = System.currentTimeMillis();
		System.out.println("3. Buildling Lucene for Lindex: " + (time3-time2));
		// 3rd step: return
		PostingFetcher posting = new PostingFetcherLucene2(baseName + SubgraphSearch_Lindex.getLuceneName(), gDB.getTotalNum(), gSerializer, lucene_in_mem);
		return new SubgraphSearch_Lindex(in_memoryIndex, posting, new VerifierISO());
	}
	
	public SubgraphSearch_Lindex loadIndex(GraphDatabase_OnDisk gDB, String baseName,  GraphParser gParser, boolean lucene_in_mem) throws IOException{
		LindexSearcher in_memoryIndex  = LindexConstructor.loadSearcher(baseName, SubgraphSearch_Lindex.getIndexName());
		PostingFetcher posting = new PostingFetcherLucene2(baseName + SubgraphSearch_Lindex.getLuceneName(), gDB.getTotalNum(), gParser, lucene_in_mem);
		return new SubgraphSearch_Lindex(new LindexSearcherAdv(in_memoryIndex), posting, new VerifierISO());
	}
}
