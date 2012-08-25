package edu.psu.chemxseer.structure.supersearch.Impl;

import java.text.ParseException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.iso.FastSU;
import edu.psu.chemxseer.structure.iso.FastSUCompleteEmbedding;
import edu.psu.chemxseer.structure.iso.FastSUStateLabelling;
import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IndexSearcherLucene;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IndexSearcherPrefix;
import edu.psu.chemxseer.structure.subsearch.Interfaces.PostingBuilderLuceneVectorizer;
/**
 * Basically the same as PostingBuilderLuceneVectorizerSingle, the only difference in on 
 * one field (for prefix) or 2 field (one for prefix & one for support)
 * @author dayuyuan
 *
 */
public class PostingBuilderLuceneVectorizerMultiField implements PostingBuilderLuceneVectorizer{
	private IndexSearcherPrefix prefixSearcher;
	private IndexSearcherLucene luceneSearcher;

	
	public PostingBuilderLuceneVectorizerMultiField(IndexSearcherLucene luceneSearcher, IndexSearcherPrefix prefixSearcher){
		this.luceneSearcher = luceneSearcher;
		this.prefixSearcher = prefixSearcher;
	}
	
	public Document vectorize(int gID, Graph g,  Map<Integer, String> gHash) throws ParseException {
		Document gDoc = new Document();
		
		if(g.getEdgeCount() == 0)
			return gDoc;
		//1. First Field: Prefix Field, given the graph "g" find the prefix feature of the graph "g"
		int prefixID = this.prefixSearcher.getPrefixID(g);
		if(gHash==null){
			String byteString = new Integer(prefixID).toString();
			gDoc.add(new Field("prefix", byteString, Field.Store.YES, Field.Index.NOT_ANALYZED));
		}
		else if(prefixID!=-1){
			gDoc.add(new Field("prefix", gHash.get(prefixID), Field.Store.YES, Field.Index.NOT_ANALYZED));
		}
		else // prefixID = -1
			gDoc.add(new Field("prefix", new Integer(-1).toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
		
		//2.
		List<Integer> allIDs = luceneSearcher.subgraphs(g, new long[4]);
		//2.1 Add -1 as the subgraphs
		gDoc.add(new Field("subGraphs", (new Integer(-1)).toString(), Field.Store.NO, Field.Index.NOT_ANALYZED));
		//2.1 Add other subgraphs
		if(allIDs != null || allIDs.size() > 0){
			Collections.sort(allIDs);
			for(int i = 0; i< allIDs.size(); i++){
				if(gHash==null){
					String byteString = allIDs.get(i).toString();
					gDoc.add(new Field("subGraphs", byteString, Field.Store.NO, Field.Index.NOT_ANALYZED));
				}
				else{
					gDoc.add(new Field("subGraphs", gHash.get(allIDs.get(i)), Field.Store.NO, Field.Index.NOT_ANALYZED));
				}
			}
		}
		//3. Second Field: the Graph String with the feature as prefix
		String graphString = null;
		if(prefixID == -1){
			graphString = MyFactory.getDFSCoder().serialize(g);
		}
		else{
			FastSUCompleteEmbedding emb = prefixSearcher.getEmbedding(prefixID, g);
			FastSU iso = new FastSU();
			FastSUStateLabelling labelISO = iso.graphExtensionLabeling(emb.getState());
			graphString = MyFactory.getDFSCoder().writeArrayToText(labelISO.getExtension());
		}
		Field stringField = new Field("gString", graphString, Field.Store.YES, Field.Index.NO);
		gDoc.add(stringField);
		//4. Third Field: the Graph ID field
		Field IDField = new Field("gID", new Integer(gID).toString(), Field.Store.YES, Field.Index.NO);	
		gDoc.add(IDField);
		return gDoc;
	}
}
