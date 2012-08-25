package edu.psu.chemxseer.structure.subsearch.Interfaces;

import java.text.ParseException;
import java.util.Map;

import org.apache.lucene.document.Document;

import de.parmol.graph.Graph;

/**
 * Interface for the LuceneVectorizer
 * @author dayuyuan
 *
 */
public interface PostingBuilderLuceneVectorizer {
	/**
	 * @param gID
	 * @param g
	 * @param gHash
	 * @return
	 * @throws ParseException
	 */
	public Document vectorize(int gID, Graph g, Map<Integer, String> gHash) throws ParseException;
}
