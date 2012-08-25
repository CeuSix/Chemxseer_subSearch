package edu.psu.chemxseer.structure.setcover.IO;

import de.parmol.util.FrequentFragment;
import edu.psu.chemxseer.structure.parmolExtension.GSpanMiner_MultiClass_Iterative;
import edu.psu.chemxseer.structure.setcover.sets.CoverSet_FeatureWrapper;

/**
 * A Wrapper for the FeatureSetGenerator Class
 * Generate features one after another as in a stream
 * @author dayuyuan
 *
 */
public class Input_DFSStream implements IInputStream{
	private GSpanMiner_MultiClass_Iterative patternGenerator;
	private CoverSet_FeatureWrapper currentFeature;
	
	public Input_DFSStream(GSpanMiner_MultiClass_Iterative gen){
		this.patternGenerator = gen;
	}
	/**
	 * Return the next CoverSet
	 * @return
	 */
	public CoverSet_FeatureWrapper nextSet(){
		long start = System.currentTimeMillis();
		FrequentFragment frag = patternGenerator.nextPattern();
		if(frag == null)
			return null;
		currentFeature = patternGenerator.getFeature(frag);
		System.out.println("Enumerate one Feature: " + (System.currentTimeMillis()-start));
		return currentFeature;
	}
	
	/**
	 * Prune the branches starting from current_fragment
	 * @return
	 */
	public boolean pruneBranch() {
		return this.patternGenerator.prune();
	}
	
}
