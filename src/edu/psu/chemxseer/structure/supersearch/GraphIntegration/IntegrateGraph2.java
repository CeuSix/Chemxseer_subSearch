package edu.psu.chemxseer.structure.supersearch.GraphIntegration;

//NOT Finished: the graph constructed my be too dense
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.parmol.graph.Graph;
import de.parmol.graph.MutableGraph;
import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.util.IntersectionSet;

/**
 * The inner representation of the integrated graphs
 * Each edge is associated with a list of gIDs, containing those edge
 * The graph will change dynamically, therefore, an matrix representaiton my not be appropriate, 
 * We use a linked list representation only
 * @author dayuyuan
 *
 */
public class IntegrateGraph2 {
//	private int[][] nodes; //nodes[i] is an array containing all the surrounding nodes of i
//	private int[] nodeDegree;
//	private int[][] edges; //given node "i", return the edgeID of the "j" adjacent edge
//	
//	private int[] nodeLabels; 
//	private int[] edgeLabels;
//	
//	private int[] edgeFreq;
//	private int[][] edgeContainedGIds; // all the database graphs contained certain edge eID
//	
//	private int nodeCount; //total number of nodes
//	private int edgeCount; //total number of edges
//	
//	//Certain Edge (Label) maps with the edgeID (most frequent edgeID). 
//	private HashMap<OneEdge, OneEdge> headTable; 
//	
//	public IntegrateGraph2(){
//		// dummy constructor
//	}
//	
//	/**
//	 * Add a new graph into the integrated graph
//	 * @param g
//	 */
//	public void addGraph(Graph g){
//		if(nodes == null)
//			initialize(g);
//		else this.insertGraph(g);
//	}
//	
//	/**
//	 * Add the first graph
//	 * @param g
//	 */
//	private void initialize(Graph g){
//		// initialize data member
//		this.nodeCount = g.getNodeCount();
//		this.edgeCount = g.getEdgeCount();
//		
//		this.nodes = new int[nodeCount*2][];
//		this.nodeDegree = new int[nodeCount*2];
//		for(int i = 0; i< nodeDegree.length; i++)
//			nodeDegree[i] = 0;
//		this.edges = new int[nodeCount*2][];
//		
//		this.nodeLabels = new int[nodeCount*2];
//		this.edgeLabels = new int[edgeCount*2];
//		
//		this.edgeFreq = new int[edgeCount*2];
//		for(int i = 0; i< edgeFreq.length; i++)
//			edgeFreq[i] =0;
//		this.edgeContainedGIds = new int[edgeCount*2][];
//		
//		this.headTable = new HashMap<OneEdge, OneEdge>();
//		
//		// add the new graph g
//		for(int i = 0; i< nodeCount; i++){
//			for(int jIndex = 0; jIndex < g.getDegree(i); jIndex++){
//				int edge = g.getNodeEdge(i, jIndex);
//				int j = g.getOtherNode(edge, i);
//				this.addNode(i, j, edge, g.getNodeLabel(i), g.getNodeLabel(j));
//				if( i < j){
//					this.addEdge(edge, g.getEdgeLabel(edge), g.getID());
//					OneEdge aEdge = new OneEdge(i, j, edge, nodeLabels[i], nodeLabels[j], edgeLabels[edge]);
//					if(!this.headTable.containsKey(aEdge))
//						headTable.put(aEdge, aEdge); // The frequency equals to one
//				}
//			}
//		}
//	}
//	
//	/**
//	 * Add the node "j", and attached it to node "i". 
//	 * NodeLabel for both node i and j are assigned
//	 * @param i
//	 * @param j
//	 * @param edgeID
//	 * @param nodeILabel
//	 * @param nodeJLabel
//	 */
//	private void addNode(int i, int j, int edgeID, int nodeILabel, int nodeJLabel){
//		int count = Math.max(i, j);
//		if(this.nodes.length <= count){
//			int[][] tempNodes = new int[count* 2][];
//			int[] tempNodeDegree = new int[tempNodes.length];
//			int[][] tempEdges = new int[tempNodes.length][];
//			int[] tempNodeLabels = new int[tempNodes.length];
//			for(int w= 0; w < nodes.length; w++){
//				tempNodes[w] = nodes[w];
//				tempNodeDegree[w] = nodeDegree[w];
//				tempEdges[w] = edges[w];
//				tempNodeLabels[w] = nodeLabels[w];
//			}
//			for(int w =nodes.length; w < tempNodeDegree.length; w++)
//				tempNodeDegree[w] = 0;
//			nodes = tempNodes;
//			nodeDegree = tempNodeDegree;
//			edges = tempEdges;
//			nodeLabels = tempNodeLabels;
//		}
//		if(this.nodeDegree[i] == 0){
//			this.nodes[i] = new int[4];
//			this.edges[i] = new int[4];
//		}
//		else if(this.nodeDegree[i] == nodes[i].length){
//			int[] temp = new int[nodeDegree[i] * 2];
//			int[] temp2 = new int[nodeDegree[i]*2];
//			for(int w = 0; w< nodeDegree[i]; w++){
//				temp[w] = nodes[i][w];
//				temp2[w] = edges[i][w];
//			}
//			nodes[i] = temp;
//			edges[i] = temp2;
//		}
//		
//		int index = nodeDegree[i];
//		nodes[i][index] = j;
//		edges[i][index] = edgeID;
//		nodeDegree[i]++;
//		this.nodeLabels[i] = nodeILabel;
//		this.nodeLabels[j] = nodeJLabel;
//	}
//	
//	/**
//	 * Edge info: 
//	 * Edge label is also assigned
//	 * @param eID
//	 * @param edgeLabel
//	 * @param gID
//	 */
//	private void addEdge(int eID, int edgeLabel, int gID){
//		if(eID >= edgeLabels.length){
//			int[] tempEdgeLabels = new int[edgeLabels.length*2];
//			int[][] tempEdgeContainedGIds = new int[tempEdgeLabels.length][];
//			int[] tempEdgeFreq = new int[tempEdgeLabels.length];
//			for(int w = 0; w < edgeLabels.length; w++){
//				tempEdgeLabels[w] = edgeLabels[w];
//				tempEdgeContainedGIds[w] = edgeContainedGIds[w];
//				tempEdgeFreq[w] = edgeFreq[w];
//			}
//			for(int w = edgeFreq.length; w < tempEdgeFreq.length;w++)
//				tempEdgeFreq[w] = 0;
//			
//			edgeLabels = tempEdgeLabels;
//			edgeContainedGIds = tempEdgeContainedGIds;
//			edgeFreq = tempEdgeFreq;
//		}
//		
//		this.edgeLabels[eID] = edgeLabel;
//		if(this.edgeFreq[eID] == 0){
//			edgeContainedGIds[eID] = new int[4];
//		}
//		else if(this.edgeFreq[eID] == edgeContainedGIds[eID].length){
//			int[] temp = new int[edgeContainedGIds[eID].length*2];
//			for(int i = 0; i< edgeContainedGIds[eID].length; i++)
//				temp[i] = edgeContainedGIds[eID][i];
//			this.edgeContainedGIds[eID] = temp;
//		}
//		edgeContainedGIds[eID][edgeFreq[eID]++] = gID;
//	}
//	
//	/**
//	 * Integrate another graph into the Integrated Graph
//	 * @param g
//	 */
////	public void insertGraph(Graph g){
////		//1. First find the search order according to the edge frequency
////		OneEdge[] edgeSequence = this.getEdgeSequence(g);
////		//2.Given the node sequence, start the insertion and matching
////		boolean[] mappedIntegratedNode = new boolean[this.nodeCount + g.getNodeCount()];
////		for(int i = 0; i< mappedIntegratedNode.length; i++)
////			mappedIntegratedNode[i] = false;
////		int[] mapGraphNode = new int[g.getNodeCount()];
////		for(int i = 0; i< mapGraphNode.length; i++)
////			mapGraphNode[i] = -1;
////		
////		for(int i = 0; i< edgeSequence.length; i++){	
////			if( i == 0 ){
////				OneEdge firstEdge = identifyFirstEdge(edgeSequence[0]);
////				if(firstEdge == null)
////					return; // do nothing
////				mappedIntegratedNode[firstEdge.getNodeAID()] = mappedIntegratedNode[firstEdge.getNodeBID()] = true;
////				mapGraphNode[nodeSequence[0]] = firstEdge.getNodeAID();
////				mapGraphNode[nodeSequence[1]] = firstEdge.getNodeBID();
////				// Integrate the edge
////				int edge = firstEdge.getEdgeID();
////				this.addEdge(edge,firstEdge.getEdgeLabel() ,g.getID());
////				nodeIndex = 1;
////			}
////			else{
////				int bigNodeB = identifyNode(nodeSequence[nodeIndex], g, mapGraphNode, mappedIntegratedNode);
////				if(bigNodeB == -1)
////					break;
////				mappedIntegratedNode[bigNodeB] = true;
////				mapGraphNode[nodeSequence[nodeIndex]] = bigNodeB;
////				for(int w = 0; w < this.nodeDegree[bigNodeB]; w++){
////					int edge = edges[bigNodeB][w];
////					this.addEdge(edge,edgeLabels[edge], g.getID());
////				}
////			}
////		}
////		for(; nodeIndex < nodeSequence.length; nodeIndex++){
////			int nodeB = nodeSequence[nodeIndex];
////			for(int w = 0; w < g.getDegree(nodeB); w++){
////				int edgeG = g.getNodeEdge(nodeB, w);
////				int nodeA = g.getOtherNode(edgeG, nodeB);
////				int nodeABig = mapGraphNode[nodeA];
////				if(nodeABig == -1)
////					continue;
////				this.addNode(nodeABig, this.nodeCount, this.edgeCount, g.getNodeLabel(nodeA), g.getNodeLabel(nodeB));
////				this.addNode(this.nodeCount, nodeABig, this.edgeCount, g.getNodeLabel(nodeB), g.getNodeLabel(nodeA));
////				this.addEdge(this.edgeCount, g.getEdgeLabel(edgeG) , g.getID());
////				edgeCount++;
////			}
////			mappedIntegratedNode[nodeCount] = true;
////			mapGraphNode[nodeB] = nodeCount;
////			nodeCount++;
////		}
////	}
//	
//	/**
//	 * Given a Edge from the input graph, find the mapping Edge on the integrated graph
//	 * @param aEdge
//	 * @param integratedNodesMapped
//	 * @param newNodesMapped
//	 * @return
//	 */
//	private OneEdge findCandidateEdge(OneEdge aEdge, boolean[] integratedNodesMapped, int[] newNodesMapped){
//		int nodeA = aEdge.getNodeAID();
//		int nodeB = aEdge.getNodeBID();
//		int bigNodeA = newNodesMapped[nodeA];
//		int bigNodeB = newNodesMapped[nodeB];
//		if(bigNodeA >0 && bigNodeB > 0){
//			//both the nodeA and nodeB of the aEdge is mapped. 
//			for(int w = 0; w < this.nodeDegree[bigNodeA]; w++){
//				if(nodes[bigNodeA][w] == bigNodeB){
//					int bigEdge = edges[bigNodeA][w];
//					//1.1 Find the edge connecting the mapped nodes on the integrated graph
//					if(this.edgeLabels[bigEdge] == aEdge.getEdgeLabel())
//						return new OneEdge(bigNodeA, bigNodeB, bigEdge, 
//								nodeLabels[bigNodeA], nodeLabels[bigNodeB], edgeLabels[bigEdge]);
//					//1.2 Exception: we generally does not multiple edge connecting two nodes
//					else {
//						System.out.println("Exception in findCandidateEdge: we generally does not connecting" +
//						"to nodes with multiple edges");
//						return null;
//					}
//				}
//			}
//		}
//		else if (bigNodeA == -1 && bigNodeB == -1){
//			//both the nodeA and nodeB of the aEdge is not mapped, in which case, we search for the headertable
//			// and return: first edge
//			OneEdge edge = this.headTable.get(aEdge);
//			if(edge!=null)
//				return edge;
//			else{
//				//Find matchable node for nodeA
//				for(int node = 0; node < this.nodeCount; node++)
//					if(nodeLabels[node] == aEdge.getNodeALabel()){
//						bigNodeA = node;
//						return new OneEdge(bigNodeA, nodeCount+1, edgeCount+1, );
//					}
//				if(bigNodeA == -1)
//					for(int node = 0; node < this.nodeCount; node++)
//						if(nodeLabels[node] == aEdge.getNodeBLabel()){
//							
//						}
//			}
//		}
//		else{
//			int mappedNode = nodeA;
//			int unmappedNode = nodeB;
//			int bigMappedNode = bigNodeA;
//			int bigUnMappedNode = bigNodeB;
//			if(bigNodeA < 0){ //This may not happen
//				mappedNode = nodeB;
//				unmappedNode = nodeA;
//				bigMappedNode = bigNodeB;
//				bigUnMappedNode = bigNodeA;
//			}
//			
//			
//		}
//	}
//	
//	/**
//	 * Given the input "g", find the sequence of visited nodes
//	 * TODO: slight change afterwards
//	 * @param g
//	 * @return
//	 */
//	public OneEdge[] getEdgeSequence(Graph g){
//		OneEdge[] results = new OneEdge[g.getEdgeCount()];
//		//Status tracking: edgeVisited[edge] = 0, not visited; =1, in the surroudingEdge candidate; = 2, visited & in the results already
//		int[] edgeVisited = new int[g.getEdgeCount()];
//		List<OneEdge> surroudingEdge = new ArrayList<OneEdge>();
//		for(int i = 0; i< results.length; i++){
//			if(i == 0){
//				// find the edge with maximum frequency to start
//				int maxFreq = -1;
//				OneEdge maxEdge = null;
//				for(int edge = 0; edge < g.getEdgeCount(); edge++){
//					int nodeA = g.getNodeA(edge);
//					int nodeB = g.getNodeB(edge);
//					OneEdge aEdge = new OneEdge(g.getNodeLabel(nodeA), g.getNodeLabel(nodeB), g.getEdgeLabel(edge));
//					OneEdge bigEdge = this.headTable.get(aEdge);
//					int freq = 0;
//					if(bigEdge != null)
//						freq = this.edgeFreq[bigEdge.getEdgeID()];
//					if(freq > maxFreq){
//						maxFreq = freq;
//						maxEdge = aEdge;
//					}
//				}
//				results[i] = maxEdge;
//				// update the status
//				edgeVisited[maxEdge.getEdgeID()] = 2;
//				this.addSurroudings(g, maxEdge.getNodeAID(), surroudingEdge, edgeVisited);
//				this.addSurroudings(g, maxEdge.getNodeBID(), surroudingEdge, edgeVisited);
//			}
//			else{
//				// find the edge with maximum surrounding scores
//				int maxFreq = -1;
//				OneEdge maxEdge = null;
//				int maxEdgeIDInSurroudingEdge = -1;
//				
//				for(int w = 0; w< surroudingEdge.size(); w++){
//					OneEdge aEdge = surroudingEdge.get(w);
//					OneEdge bigEdge = this.headTable.get(aEdge);
//					int freq = 0;
//					if(bigEdge != null)
//						freq = this.edgeFreq[bigEdge.getEdgeID()];
//					if(freq > maxFreq){
//						maxEdge = aEdge;
//						maxFreq = freq;
//						maxEdgeIDInSurroudingEdge = w;
//					}
//				}
//				results[i] = maxEdge;
//				//update the status
//				edgeVisited[maxEdge.getEdgeID()] = 2;
//				surroudingEdge.remove(maxEdgeIDInSurroudingEdge);
//				this.addSurroudings(g, maxEdge.getNodeBID(), surroudingEdge, edgeVisited);
//			}
//		}
//		return results;
//	}
//	
//	/**
//	 * edgeVisited[edge] = 0, not visited; 
//	 * =1, in the surroudingEdge candidate; 
//	 * = 2, visited & in the results already
//	 * @param g
//	 * @param node1
//	 * @param surroudingEdge
//	 * @param edgeVisited
//	 */
//	private void addSurroudings(Graph g, int node1, List<OneEdge> surroudingEdge, int[] edgeVisited){
//		//for all the edges surrounding node 1 in the graph g, 
//		// if the edge is not visited, add its into the surrounding Edge
//		for(int i = 0; i< g.getDegree(node1); i++){
//			int edge = g.getNodeEdge(node1, i);
//			if(edgeVisited[edge] == 0){
//				int node2 = g.getOtherNode(edge, node1);
//				OneEdge aEdge = new OneEdge(node1, node2, edge, g.getNodeLabel(node1), 
//						g.getNodeLabel(node2), g.getEdgeLabel(edge));
//				surroudingEdge.add(aEdge);
//				edgeVisited[edge] = 1;
//			}
//			else continue;
//		}
//		
//	}
//	
//	public Graph toGraph(String gID){
//		MutableGraph g = MyFactory.getGraphFactory().createGraph(gID);
//		for(int i = 0; i< nodeCount; i++){
//			g.addNode(nodeLabels[i]);
//		}
//		for(int i = 0; i<nodeCount; i++){
//			for(int jIndex = 0; jIndex < nodes[i].length; jIndex++){
//				int j = nodes[i][jIndex];
//				if(i < j)
//					g.addEdge(i, j, edgeLabels[edges[i][jIndex]]);
//				else continue;
//			}
//		}
//		g.saveMemory();
//		return g;
//	}
}
