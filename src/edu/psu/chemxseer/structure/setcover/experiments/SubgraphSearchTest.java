package edu.psu.chemxseer.structure.setcover.experiments;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.commons.math.MathException;

import de.parmol.graph.Graph;

import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.query.InFrequentQueryGenerater2;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphDatabase_OnDisk;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureFactory.FeatureFactoryType;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.NoPostingFeatures;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IOneFeature;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SubgraphSearch;
import edu.psu.chemxseer.structure.subsearch.Lindex.SubgraphSearch_LindexPlusBuilder;
import edu.psu.chemxseer.structure.subsearch.Lindex.SubgraphSearch_LindexSimpleBuilder;

/**
 * Test the feature quliaty of the several subgraph search mining algorithms:
 * @author dayuyuan
 *
 */
public class SubgraphSearchTest {
//	public static void main(String[] args) throws IOException, ParseException, MathException{
//		//0. Generate the queries
//		SubgraphSearchTest.genTestQueries();
//		
//		//1. Run the Experiment:
//		String gIndex = SubgraphSearchExp.baseName + "Gindex_0.1/";
//		String fgIndex = SubgraphSearchExp.baseName + "FGindex_0.1/";
//		String in_mem_1 = SubgraphSearchExp.baseName + "inMem_0.1/";
//		String in_mem_5 = SubgraphSearchExp.baseName + "inMem_0.05/";
//		String streaming1 = SubgraphSearchExp.baseName + "Stream_1_FW_N_0.05/";
//		String streaming2 = SubgraphSearchExp.baseName + "Stream_0_FW_N_0.05/";
//		String streaming3 = SubgraphSearchExp.baseName + "Stream_1_FW_A_0.05/";
//		String streaming4 = SubgraphSearchExp.baseName + "Stream_0_FW_A_0.05/";
//		
//		GraphDatabase_OnDisk gDB = new GraphDatabase_OnDisk(SubgraphSearchExp.gDBFileName, MyFactory.getSmilesParser());
//		//1
//		System.out.println("Run LindexDF: ");
//		testLindex(loadLindex(gDB, gIndex));
//		//2
//		System.out.println("Run LindexTCFG");
//		testLindex(loadLindexAdv(gDB, fgIndex));
//		//3 
//		System.out.println("Run Greedy 0.1");
//		testLindex(loadLindex(gDB, in_mem_1));
//		//4
//		System.out.println("Run Greedy 0.05");
//		testLindex(loadLindex(gDB, in_mem_5));
//		//5
//		System.out.println("Run Streaming 1");
//		testLindex(loadLindex(gDB, streaming1));
//		//6
//		System.out.println("Run Streaming 2");
//		testLindex(loadLindex(gDB, streaming2));
//		//7
//		System.out.println("Run Streaming 3");
//		testLindex(loadLindex(gDB, streaming3));
//		//8
//		System.out.println("Run Streaming 4");
//		testLindex(loadLindex(gDB, streaming4));
//		
//	}
//	
//	public static SubgraphSearch loadLindex(GraphDatabase_OnDisk gDB, String indexBaseName) throws IOException{
//		SubgraphSearch_LindexSimpleBuilder builder = new SubgraphSearch_LindexSimpleBuilder();
//		return builder.loadIndex(gDB, indexBaseName, MyFactory.getSmilesParser());
//	}
//	
//	public static SubgraphSearch loadLindexAdv(GraphDatabase_OnDisk gDB, String indexBaseName) throws IOException{
//		SubgraphSearch_LindexPlusBuilder builder = new SubgraphSearch_LindexPlusBuilder();
//		return builder.loadIndex(gDB, indexBaseName, MyFactory.getSmilesParser());
//	}
//	
//	public static void genTestQueries() throws IOException, ParseException, MathException{
//		String rawQueryFile = SubgraphSearchExp.trainingQueries + "_raw";
//		String testQueryFile = SubgraphSearchExp.testingQueries;
//		
//		NoPostingFeatures<IOneFeature> features = 
//			new NoPostingFeatures<IOneFeature>(rawQueryFile, MyFactory.getFeatureFactory(FeatureFactoryType.OneFeature));
//		
//		InFrequentQueryGenerater2 queryGen = new InFrequentQueryGenerater2();
//		queryGen.generateInFrequentQueries2(4, 30, 1000, features, 0, testQueryFile);
//	}
//	
//	public static void testLindex(SubgraphSearch lindex) throws IOException, ParseException{
//		//1. First Step: load the testing queries
//		InFrequentQueryGenerater2 queryGen = new InFrequentQueryGenerater2();
//		NoPostingFeatures queries = new NoPostingFeatures(SubgraphSearchExp.testingQueries,
//				MyFactory.getFeatureFactory(FeatureFactoryType.OneFeature));
//		long[] TotalTimeComplexity = new long[4];
//		long[] tc = new long[4];
//		for(int i = 0; i< 4; i++)
//			TotalTimeComplexity[i] = tc[i]=0;
//		int[] Number = new int[2];
//		int[] nb = new int[2];
//		for(int i =0; i< 2; i++)
//			Number[i] = nb[i] = 0;
//		
//		for(int i = 0; i< queries.getfeatureNum(); i++){
//			Graph q = queries.getFeature(i).getFeatureGraph();
//			List<GraphResult> result = lindex.getAnswer(q, tc, nb);
//			if(result.size()!=queries.getFeature(i).getFrequency())
//				System.out.println("Some Time is Wrong");
//			for(int j = 0; j< 4; j++)
//				TotalTimeComplexity[j] += tc[j];
//			for(int j =0; j< 2; j++)
//				Number[j] += nb[j];
//		}
//		System.out.println("Time Complexity: " + TotalTimeComplexity[0] + ", " +  TotalTimeComplexity[1] + 
//				", " +  TotalTimeComplexity[2]  + ", " +  TotalTimeComplexity[3]);
//		System.out.println("Number Count: " + Number[0] + ", " + Number[1]);
//	}
}
