package edu.psu.chemxseer.structure.supersearch.PrefIndex;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import de.parmol.graph.Graph;

import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SubgraphSearch;
import edu.psu.chemxseer.structure.supersearch.Impl.PostingBuilderMem;

/**
 * Hierarchical Prefix Index
 * @author dayuyuan
 *
 */
public class SupSearch_PrefixIndexHi implements SubgraphSearch{
	private List<SupSearch_PrefixIndex> indexes;
	
	/**
	 * The upper-most index is built & added in to the hierarchy first
	 */
	public SupSearch_PrefixIndexHi(){
		// dummy index constructor
		this.indexes = new ArrayList<SupSearch_PrefixIndex>();
	}
	/**
	 * Add on Index into the Indexes Array
	 * Pay attention: the new SupSearch_PrefixIndex should be consistent with the upper-level indexes
	 * @param index
	 * @return
	 */
	public boolean addIndexLow(SupSearch_PrefixIndex index){
		if(indexes.size() == 0){
			this.indexes.add(index);
			return true;
		}
		else{
			if(indexes.get(indexes.size()-1).lowerLevelSearcher.equals(index.searcher)){
				this.indexes.add(index);
				return true;
			}
			else {
				System.out.println("Exception: Wrong Insertion of the Index");
			}
		}
		return false;
	}
	
	@Override
	public List<GraphResult> getAnswer(Graph query, long[] TimeComponent,
			int[] Number) throws IOException, ParseException {
		return this.indexes.get(indexes.size()-1).getAnswer(query, TimeComponent, Number);
	}
	@Override
	public PostingBuilderMem getInMemPosting() {
		return indexes.get(indexes.size()-1).getInMemPosting();
	}
	
	
}
