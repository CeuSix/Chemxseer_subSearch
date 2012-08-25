package edu.psu.chemxseer.structure.newISO.newFastSU;

import edu.psu.chemxseer.structure.util.HashID;

public class Embedding implements Comparable<Embedding>, HashID{
	private int[] map; // the mapping from the small graph to the big graph
	private int embID;
	private Pattern subgraph;
	private boolean visited; // denote whether this embedding has been visited or not
	private float score;
	
	private int priorityQueueID;
	
	private Embedding(int[] map, int embID, Pattern subgraph){
		this.map = map;
		this.embID = embID;
		this.subgraph = subgraph;
		this.visited = false;
		this.score = subgraph.getScore();
		this.priorityQueueID = -1;
	}
	
	public Embedding[] getInstances(int[][] mappings, Pattern pattern){
		this.subgraph = pattern;
		Embedding[] results = new Embedding[mappings.length];
		for(int i = 0; i< results.length; i++)
			results[i] = new Embedding (mappings[i], i, pattern);
		return results;
	}

	/**
	 * @return the map
	 */
	public int[] getMap() {
		return map;
	}

	/**
	 * @return the embID
	 */
	public int getEmbID() {
		return embID;
	}

	/**
	 * @return the subgraph
	 */
	public Pattern getSubgraph() {
		return subgraph;
	}

	/**
	 * @param map the map to set
	 */
	public void setMap(int[] map) {
		this.map = map;
	}

	/**
	 * @param embID the embID to set
	 */
	public void setEmbID(int embID) {
		this.embID = embID;
	}

	/**
	 * @param subgraph the subgraph to set
	 */
	public void setSubgraph(Pattern subgraph) {
		this.subgraph = subgraph;
	}
	
	

	/**
	 * @return the visited
	 */
	public boolean isVisited() {
		return visited;
	}

	/**
	 * @param visited the visited to set
	 */
	public void setVisited() {
		this.visited = true;
	}
	
	public void udpateScore(){
		this.score = this.subgraph.getScore();
	}

	@Override
	/**
	 * Compare the Embeeding according to their frequency & size
	 * For now, I just consider the "underpinining patterns"
	 * For advance algorithm, I may consider the "# of uncovered nodes" in the mebeddings
	 */
	public int compareTo(Embedding other) {
		if(this.score < other.score)
			return -1;
		else if(this.score == other.score){
			if(this.map.length < other.map.length)
				return -1;
			else if (this.map.length == other.map.length)
				return 0;
			else return 1;
		}
		else return 1;
	}

	@Override
	public int getID() {
		return this.priorityQueueID;
	}

	@Override
	public void setID(int id) {
		this.priorityQueueID = id;
	}

	public int getPatternID() {
		return this.subgraph.getfID();
	}
	
	
	
}
