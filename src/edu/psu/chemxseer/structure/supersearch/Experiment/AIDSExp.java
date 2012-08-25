package edu.psu.chemxseer.structure.supersearch.Experiment;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;

import de.parmol.graph.Graph;

import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphDatabase_OnDisk;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphDatabase;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphResult;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SubgraphSearch;
import edu.psu.chemxseer.structure.supersearch.LWTree.SupSearch_LWIndexBuilder;
import edu.psu.chemxseer.structure.util.MemoryConsumptionCal;

/**
 * The experiment for the AIDS Dataset
 * @author dayuyuan
 *
 */
public class AIDSExp {
	//private static double minSupt = 0.05;
	
	public static void main(String[] args) throws CorruptIndexException, LockObtainFailedException, IOException, ParseException{
		boolean lucene_im_mem = false;
		//1. Build the Index with varying "database size"
		String dirName ="/data/home/duy113/SupSearchExp/AIDSNew/";
//		String dirName = "/Users/dayuyuan/Documents/workspace/Experiment/";
		String dbFileName = dirName + "DBFile";
		String trainQueryName= dirName + "TrainQuery";
//		String testQuery15 = dirName + "TestQuery15";
		String testQuery25 = dirName + "TestQuery25";
//		String testQuery35 = dirName + "TestQuery35";
		GraphDatabase query = new GraphDatabase_OnDisk(testQuery25, MyFactory.getSmilesParser());
		double[] minSupts = new double[4];
		minSupts[0] = 0.05; minSupts[1] = 0.03; minSupts[2] =0.02; minSupts[3] = 0.01;
 		int lwIndexCount[] = new int[1];
		lwIndexCount[0] = 479;	
//		System.out.println("Build CIndexFlat Left-over: ");
//		for(int j = 3; j< 4; j++){
//			double minSupt = minSupts[j];
//			for(int i = 4; i<=10; i = i+2){
//				String baseName = dirName + "G_" + i + "MinSup_" + minSupt + "/";
//				GraphDatabase trainingDB = new GraphDatabase_OnDisk(dbFileName + i, MyFactory.getDFSCoder());
//				GraphDatabase trainQuery = new GraphDatabase_OnDisk(trainQueryName, MyFactory.getSmilesParser());		
//				if(i == 2){
//					System.out.println(baseName + "CIndexFlat");
//					CIndexExp.buildIndex(trainingDB, trainQuery, trainingDB, baseName, minSupt, lwIndexCount[0]);
//				}
//				else{
//					String featureBaseName = dirName + "G_2" + "MinSup_" + minSupt + "/";
//					System.out.println(baseName + "CIndexFlat with Features " + featureBaseName);
//					CIndexExp.buildIndex(featureBaseName, trainingDB, baseName, minSupt);
//				}
//				System.gc();
//			}
//		}
		System.out.println("Run Query Processing: ");
		for(int j = 0; j< 4; j++){
			double minSupt = minSupts[j];
			for(int i = 2; i<=10; i = i+2){
				String baseName = dirName + "G_" + i + "MinSup_" + minSupt + "/";
				GraphDatabase trainingDB = new GraphDatabase_OnDisk(dbFileName + i, MyFactory.getDFSCoder());
				GraphDatabase trainQuery = new GraphDatabase_OnDisk(trainQueryName, MyFactory.getSmilesParser());		
				if(j!=0 || i!=2){
					System.out.println(baseName + "LWindex");
					//LWIndexExp.buildIndex(trainingDB, trainQuery, trainingDB, trainQuery.getParser(),baseName, minSupt, lwIndexCount);
					//System.gc();
					LWIndexExp.runIndex(trainingDB, trainQuery, baseName, lucene_im_mem);
					LWIndexExp.runIndex(trainingDB, query, baseName, lucene_im_mem);
					System.gc();		
					System.out.println(baseName + "PrefixIndex");
					//PrefixIndexExp.buildIndex(trainingDB, trainingDB, baseName, minSupt);
					//System.gc();
					PrefixIndexExp.runIndex(trainingDB, trainQuery, baseName, lucene_im_mem);
					PrefixIndexExp.runIndex(trainingDB, query, baseName, lucene_im_mem);
					System.gc();		
					System.out.println(baseName + "PrefixIndexHi");
					//PrefixIndexExp.buildHiIndex(trainingDB, trainingDB, 2, baseName, minSupt);
					//System.gc();
					PrefixIndexExp.runHiIndex(trainingDB, trainQuery, baseName, lucene_im_mem);
					PrefixIndexExp.runHiIndex(trainingDB, query, baseName, lucene_im_mem);
					System.gc();
					System.out.println(baseName + "GPTree");
					//GPTreeExp.buildIndex(trainingDB, trainingDB, baseName, minSupt);
					//System.gc();
					GPTreeExp.runIndex(trainingDB, trainQuery, baseName, lucene_im_mem);
					GPTreeExp.runIndex(trainingDB, query, baseName, lucene_im_mem);
					System.gc();
					System.out.println(baseName + "CIndexFlat");
					//CIndexExp.buildIndex(trainingDB, trainQuery, trainingDB, baseName, minSupt, lwIndexCount[0]);
					//System.gc();
					CIndexExp.runIndex(trainingDB, trainQuery, baseName, lucene_im_mem);
					CIndexExp.runIndex(trainingDB, query, baseName, lucene_im_mem);
					System.gc();
				}
				if(j==0&&i==2){
					System.out.println(baseName + "CIndexTopDown: " + lwIndexCount[0]);
					CIndexExp.buildIndexTopDown(trainingDB, trainQuery, trainingDB,MyFactory.getUnCanDFS(), baseName, minSupt, 2*trainQuery.getTotalNum()/lwIndexCount[0] ); // 8000 test queries
					//System.gc();
				}
				System.out.println(baseName + "CIndexTopDown: " + lwIndexCount[0]);
				CIndexExp.runIndexTopDown(trainingDB, trainQuery, baseName, lucene_im_mem);
				CIndexExp.runIndexTopDown(trainingDB, query, baseName, lucene_im_mem);
				System.gc();
			}
		}
		AIDSLargeExp.main(args);
	}
	
	public static void runQueries(GraphDatabase query, SubgraphSearch searcher) throws IOException, ParseException{
		// Run the queries
		long[] TimeComponent = new long[4];
		float[] Number = new float[3];
		long[] TimeComponent1 = new long[4];
		int[] Number1 = new int[2];
		TimeComponent[0] = TimeComponent[1] = TimeComponent[2] = TimeComponent[3] = 0;
		Number[0] = Number[1] = Number[2] = 0;
		double memoryConsumption = 0;
		
		for(int i = 0; i< query.getTotalNum(); i++){
			TimeComponent1[0] = TimeComponent1[1] = TimeComponent1[2] = TimeComponent1[3] = 0;
			Number1[0] = Number1[1];
			Graph g = query.findGraph(i);
			List<GraphResult> answers = searcher.getAnswer(g, TimeComponent1, Number1);
			
			if(i == 0)
				memoryConsumption = MemoryConsumptionCal.usedMemory();
			else{
				double ratio = 1/(double)(i+1);
				memoryConsumption = memoryConsumption* ratio * i + MemoryConsumptionCal.usedMemory() * ratio;
			}
				
				
			if(answers.size() == 0)
				continue;
			TimeComponent[0] += TimeComponent1[0];
			TimeComponent[1] += TimeComponent1[1];
			TimeComponent[2] += TimeComponent1[2];
			TimeComponent[3] += TimeComponent1[3];
			Number[0] +=Number1[0];
			Number[1] += Number1[1];
			Number[2] += (float)(Number1[0])/(float)(Number1[1]);
		}
		System.out.println("Query Processing Result: ");
		System.out.print(TimeComponent[0] + "\t" + TimeComponent[1]+ "\t" + TimeComponent[2] + "\t" + TimeComponent[3] + "\t");
		System.out.println(Number[0] + "\t" + Number[1] + "\t" + Number[2] + "\t" + query.getTotalNum());
		System.out.println("Average Memory Consumption: " + (long)memoryConsumption);
	}
	
}
