package edu.psu.chemxseer.structure.subsearch.Interfaces;

import java.text.ParseException;

import de.parmol.graph.Graph;
import de.parmol.parsers.GraphParser;
import edu.psu.chemxseer.structure.preprocess.MyFactory;

public abstract class GraphDatabase {
	protected GraphParser gParser;
	
	public GraphDatabase(GraphParser gParser){
		this.gParser = gParser;
	}
	/**
	 * Excluding end, incuding start
	 * @param graphIDs
	 * @param start
	 * @param end
	 * @return
	 */
	public Graph[] loadGraphs(int[] graphIDs, int start, int end){
		if(start < 0 || end > graphIDs.length)
			return null;
		Graph[] results = new Graph[end-start];
		for(int i = 0; i< end-start; i++){
			String graphString = this.findGraphString(graphIDs[i+start]);
			try {
				results[i] = gParser.parse(graphString, MyFactory.getGraphFactory());
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			if(results[i] == null)
				System.out.println("Excpetion in loadGraphs: graphDB");
		}
		return results;
	}
	public Graph[] loadAllGraphs(){
		Graph[] graphs = new Graph[this.getTotalNum()];
		for(int i = 0; i< graphs.length; i++){
			try {
				graphs[i] = gParser.parse(findGraphString(i), MyFactory.getGraphFactory());
			} catch (ParseException e) {
				System.out.println("Error in Load All Graphs");
				e.printStackTrace();
				return null;
			}
		}
		return graphs;
	}
	/**
	 * get graphs from the graph database with ID starting from startNum
	 * end with endNum, including the first one but not the last one
	 * @param startNum
	 * @param endNum
	 * @return
	 */
	public Graph[] loadGraphs(int start, int end){
		if(start < 0 || end > this.getTotalNum())
			return null;
		Graph[] results = new Graph[end-start];
		for(int i = 0; i< end-start; i++){
			String graphString = this.findGraphString(i+start);
			try {
				results[i] = gParser.parse(graphString, MyFactory.getGraphFactory());
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			if(results[i] == null)
				System.out.println("Excpetion in loadGraphs: graphDB");
		}
		return results;
		
	}
	
	/**
	 * Given the graph ID, load the Smiles (String) from of this graph from the graph database
	 * file
	 * @param id
	 * @return
	 */
	public Graph findGraph(int id){
		String gString = this.findGraphString(id);
		if(gString == null)
			return null;
		else{
			try {
				return gParser.parse(gString, MyFactory.getGraphFactory());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public GraphParser getParser(){
		return gParser;
	}
	
	public abstract String findGraphString(int id);
	public abstract int getTotalNum();
}
