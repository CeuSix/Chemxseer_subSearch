package edu.psu.chemxseer.structure.supersearch.CIndex;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.NoPostingFeatures;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.OneFeatureMultiClass;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.PostingFeaturesMultiClass;
import edu.psu.chemxseer.structure.util.IntersectionSet;

public class CIndexTreeFeatureSelectorBK {
//	private NoPostingFeatures<OneFeatureMultiClass> features;
//	// Instead of using matrix as suggested in the paper, I choose to use inverted index, because the matrix is really sparse
//	// 1. Linked-List representation of the containment relationship between features and DB graphs
//	private int[][] FGMap;	// FGMap[i] = posting[fi]
//	private int[][] GFMap; //  GFMap[i] = all features contained in graph G
//	// 2. Use an array of list to store the filtering information between queries and DB graphs
//	private int [][] QGFilterMap; // QGFilterMap[q] denotes all the graphs that are filtered given query q
//	// 3. Use an two dimension array to denote the compact contrast graph matrix
//	private int[][] FQMatrix;	 
//	// FQMatrix[i][j] = 0 if f can not filter Q, FGMatrix[i][j] = how many database graphs can feature i filter
//	// for query j
//	private boolean[][] FQCover; // For Look-up, FQ[i][j] = true if feature i is contained in query q
//	
//	private int featureCount; // total number of features
//	private int graphCount; // total number of database graphs
//	private int queryCount; // total number of the query in the query logs
//	
//	// The minimum number of queries required for each feature
//	private int minimumQueryThreshold;
//	//4. Results: 
//	BinaryTreeFeatureNode root = null;
//	
//	/**
//	 * Constructor of the Feature Selector for CIndexTree: basically the same as FeatureSelector1
//	 * @param postingFeatures
//	 * @param minQueryCount
//	 * @throws IOException
//	 */
//	public CIndexTreeFeatureSelector(PostingFeaturesMultiClass postingFeatures, int minQueryCount) 
//		throws IOException{
//		this.minimumQueryThreshold = minQueryCount;
//		//1. Step One: load all the features into memory
//		this.features = postingFeatures.getFeatures();
//		//2. Step Two: initialize the data member
//		this.featureCount = this.features.getfeatureNum();;
//		this.graphCount = postingFeatures.getClassGraphsCount()[0];
//		this.queryCount = postingFeatures.getClassGraphsCount()[1];
//		
//		this.FGMap = new int[featureCount][];
//		for(int i = 0; i< featureCount; i++){
//			this.FGMap[i] = postingFeatures.getPosting(i,0); // FGMap[i] = all DB Graphs containing f
//		}
//		// Initialize GFMap, Assuming that FGMap are well sorted
//		this.GFMap = new int[graphCount][]; // inverted index
//		int[] indices = new int[featureCount];
//		for(int i = 0; i< indices.length; i++)
//			indices[i] = 0;
//		for(int gID = 0; gID< graphCount; gID++){
//			List<Integer> allFeatures = new ArrayList<Integer>(); // all features that are contained in graph gID
//			for(int fID = 0; fID < featureCount; fID++){
//				if(indices[fID] < FGMap[fID].length && FGMap[fID][indices[fID]] == gID){
//					allFeatures.add(fID);
//					++indices[fID];
//				}
//				// else continue;	
//			}
//			this.GFMap[gID] = new int[allFeatures.size()];
//			for(int w = 0; w< GFMap[gID].length; w++)
//				GFMap[gID][w] = allFeatures.get(w); 
//		}
//		// For Each query, which set of database graphs has been filtered out:
//		// This can be computed based on the features selected
//		this.QGFilterMap = new int [queryCount][]; 
//		for(int i = 0; i< queryCount; i++)
//			this.QGFilterMap = new int[i][0]; // Initialize to be none graph has been filtered
//		
//		
//		this.FQMatrix = new int[featureCount][queryCount];
//		this.FQCover = new boolean[featureCount][queryCount];
//		for(int i = 0; i < featureCount; i++){
//			// if f is contained in q, then FGMap[f][q] = 0, f does not have filtering power
//			// else if f is not contained in q, then FQMatrix[f][q] is calculated as |FGMap[f].length|
//			int[] containedQueries = postingFeatures.getPosting(i, 1);
//			int queryIndex = 0;
//			for(int j = 0; j< queryCount; j++){
//				if(containedQueries[queryIndex]==j){
//					queryIndex++;
//					FQCover[i][j] = true;
//					FQMatrix[i][j] = 0;
//				}
//				else if(j < containedQueries[queryIndex]){
//					FQMatrix[i][j] = FGMap[i].length;
//					FQCover[i][j] = false;
//				}
//				else
//					System.out.println("ERROR: in constructor of Feature Selector");
//					
//			}
//		}	
//		//4. Results:
//		int[] queries = new int[postingFeatures.getClassGraphsCount()[1]];
//		for(int i = 0; i< queries.length; i++)
//			queries[i] = i;
//		this.root= new BinaryTreeFeatureNode(queries, new int[0]);
//	}
//	
//	public BinaryTreeFeatureNode findFeatures(int treeDepth){
//		findFeatures(treeDepth, this.root);
//		return this.root;
//	}
//	/**
//	 * The depth of the tree is "K"
//	 * @param k
//	 * @return
//	 * @throws IOException 
//	 */
//	protected void findFeatures(int k, BinaryTreeFeatureNode currentNode){
//		// need to stop or not
//		if( k==0 || currentNode.getQueries().length < this.minimumQueryThreshold)
//			return; // No need to further split
//		// for all the features that are not covered yet, find the one with the maximum gain
//		int[] visitedFeatures = currentNode.getAncestorFeatures();
//		int[] queries = currentNode.getQueries();
//		int maxFeatureIndex = -1;
//		int maxGain = 0;
//		for(int i =0, index = 0; i< this.featureCount; i++){
//			if(i == visitedFeatures[index]){
//				index ++;
//				continue;
//			}
//			else{
//				int gain = 0;
//				for(int w = 0; w< queries.length; w++)
//					gain += this.FQMatrix[i][queries[w]];
//				if(gain > maxGain){
//					maxGain = gain;
//					maxFeatureIndex = i;
//				}
//			}
//		}
//		// split
//		if(maxGain!=0){
//			// decide to select feature: maxFeatureIndex
//			currentNode.setTheFeature(this.features.getFeature(maxFeatureIndex));
//			// split the query brunches into two: left (contain maxFeatureIndex), right (not contain)
//			// 1. count number of queries containing "f", split the queries array
//			int containedCount =0;
//			for(int i = 0; i< queries.length; i++){
//				if(this.FQCover[maxFeatureIndex][queries[i]])
//					containedCount++;
//			}
//			int[] containedQ = new int[containedCount];
//			int[] unContainedQ = new int[queries.length - containedCount];
//			for(int i = 0, j = 0, m = 0; i < queries.length; i++)
//				if(this.FQCover[maxFeatureIndex][queries[i]])
//					containedQ[j ++]  = queries[i];
//				else containedQ[m ++] = queries[i];
//			// *1. Very important, update related data structures
//			updateScore(maxFeatureIndex, unContainedQ);
//			
//			// 2. create the new feature array
//			int[] newVistedFeatures = new int[visitedFeatures.length+1];
//			for(int i = 0; i< visitedFeatures.length; i++)
//				newVistedFeatures[i] = visitedFeatures[i];
//			newVistedFeatures[visitedFeatures.length] = maxFeatureIndex;
//			// 3. update the left & right children
//			currentNode.setLeftChild(new BinaryTreeFeatureNode( 
//					containedQ, newVistedFeatures));
//			currentNode.setLeftChild(new BinaryTreeFeatureNode(
//					unContainedQ, newVistedFeatures));
//			// 4. Keep on searching
//			findFeatures(k-1, currentNode.getLeftChild());
//			findFeatures(k-1, currentNode.getRightChild());
//		}
//		
//	}
//	/**
//	 * Feature "featureIndex" is selected, we need to update the FQMap and QGMap
//	 * @param featureIndex
//	 * @param summation
//	 */
//	private void updateScore(int featureIndex, int[] unContainedQ){
//		// 1. Find all the graphs that the new feature are covering
//		int[] maxCoverage = this.FGMap[featureIndex];
//		// 2a. Need to update the FQMatrix [Which set of queries need to be updated, only query not containing "f"]
//		//	   For query containing "f" there is no need for updating, since "f" will not be effective in filtering
//		
//		// 2b. For each of the other feature, it need to update contrast matrix FQMatrix
//		// How to update FQMatrix? For each feature f, calculate the new number of graphs that it can filtered out
//		// Which kind of "f" does not have to be updated? 
//		// Answer: Only feature "f" contained in DB graphs that are newly covered by feature "f".
//		// The new value of FQ[f][q] = D(f) - QG(q): all graph can be filtered by f - all graphs already been filtered
//		// It is time consuming to update for each <f, q> pair
//		// (1) Some feature f, just does not contain graphs in QG(q)
//		// (2) Some feature f, just does not contain graphs in newly covered QG(q)
//		//boolean[] allFeatures = new boolean[this.featureCount];
//		
//		for(int i = 0 ; i < unContainedQ.length; i++){
//			int qID = unContainedQ[i];
//			IntersectionSet set = new IntersectionSet();
//			set.addAll(maxCoverage);
//			set.removeAll(this.QGFilterMap[qID]);
//			int[] newCovered = set.getItems();
//			set.clear();
//			set.addAll(maxCoverage);
//			set.addAll(this.QGFilterMap[qID]);
//			this.QGFilterMap[qID] = set.getItems(); 
//			
//			// 2b. (1) First find all the newly covered graphs for query q
//			// maxCoverage - alreadyCovered = newly Covered
//			// update the already covered graphs records in QGFilterMap
//			
//			// 2b. (2) Given all the newly covered graphs, find what kind of features are affected
//			// feature contained by those newly covered graphs
//			for(int gIndex = 0; gIndex < newCovered.length; gIndex++){
//				int gID = newCovered[gIndex];
//				int[] containingFeatures = this.GFMap[gID];
//				for(int fIndex = 0; fIndex < containingFeatures.length; fIndex++){
//					int fID = containingFeatures[fIndex];
//					if(this.FQCover[fID][qID])
//						continue; // FQMap[fID][qID] == 0 means that fID is contained in qID
//					// feature f, contain gID which is newly covered, f is not contained in q. 
//					this.FQMatrix[fID][qID]--;
//				}
//			}
//		}	
//	}
}
