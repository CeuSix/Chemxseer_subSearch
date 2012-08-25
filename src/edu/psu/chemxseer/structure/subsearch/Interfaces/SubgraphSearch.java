package edu.psu.chemxseer.structure.subsearch.Interfaces;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import de.parmol.graph.Graph;
import edu.psu.chemxseer.structure.supersearch.Impl.PostingBuilderMem;
/**
 * The main interface for subgraph search problem: 
 * There are Gindex, FGindex, Lindex, Lindex+, QuickSI, TreeDelta algorithms
 * implementing the subgraph search problem.
 * Usually, one subgraph search algorithm contains (1) indexSearcher (2) postingFetcher
 * and (3) verifier. But there are exceptions [Especially for some on-Disk index].
 * @author dayuyuan
 *
 */
public interface SubgraphSearch {
	/**
	 * Given the query graph q, search for database graphs containing the query q
	 * The GraphResults is returned in order
	 * TimeComponent[0] = posting fetching
	 * TimeComponent[1] = DB graph loading time
	 * TimeComponent[2] = filtering cost (index lookup: maximum subgraph search & minimal supergraph search)
	 * TimeComponent[3] = verification cost (subgraph isomorphism test in verification step)
	 * Number[0] = verified graphs number
	 * Number[1] = True answer size
	 * @param q
	 * @param TimeComponent
	 * @param Number
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public List<GraphResult> getAnswer(Graph q, long[] TimeComponent, int[] Number) 
		throws IOException, ParseException;

	public PostingBuilderMem getInMemPosting();
}
