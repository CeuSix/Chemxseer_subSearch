package edu.psu.chemxseer.structure.setcover.interfaces;


/**
 * The interface for the max-coverage solver
 * @author dayuyuan
 *
 */
public interface IMaxCoverSolver {
	public abstract int[] runGreedy(int K);
	
	public abstract int totalCoveredItems();
	
}
