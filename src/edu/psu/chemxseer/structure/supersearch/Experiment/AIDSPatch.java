package edu.psu.chemxseer.structure.supersearch.Experiment;

import java.io.IOException;
import java.text.ParseException;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.store.LockObtainFailedException;

import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphDatabase_OnDisk;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphDatabase;
import edu.psu.chemxseer.structure.supersearch.LWTree.LWIndexSearcher;
import edu.psu.chemxseer.structure.supersearch.LWTree.SupSearch_LWIndex;
import edu.psu.chemxseer.structure.supersearch.LWTree.SupSearch_LWIndexBuilder;

public class AIDSPatch {
	/**
	 * In the current version, the cIndexTopDown lucene index is not complete.
	 * Therefore, we need to re-build and re-run CIndexTopDown Experiments
	 * @throws IOException 
	 */
	public void reBuildCIndexTopDown(){
		String dirName ="/data/home/duy113/SupSearchExp/AIDSNew/";
		String dbFileName = dirName + "DBFile";
		String testQuery25 = dirName + "TestQuery25";
		String trainQueryName= dirName + "TrainQuery";
		GraphDatabase query = new GraphDatabase_OnDisk(testQuery25, MyFactory.getSmilesParser());
		GraphDatabase trainQuery = new GraphDatabase_OnDisk(trainQueryName, MyFactory.getSmilesParser());		
		double[] minSupts = new double[4];
		minSupts[0] = 0.05; minSupts[1] = 0.03; minSupts[2] =0.02; minSupts[3] = 0.01;
		System.out.println("ReBuildIndex");
		for(int j = 0; j< 4; j++){
			double minSupt = minSupts[j];
			for(int i = 2; i<=10; i = i+2){
				String baseName = dirName + "G_" + i + "MinSup_" + minSupt + "/";
				GraphDatabase realDB = new GraphDatabase_OnDisk(dbFileName + i, MyFactory.getDFSCoder());		
				try {
					System.out.println(baseName);
					//CIndexExp.buildIndexTopDownLuceneOnly(realDB, baseName, minSupt);
					SupSearch_LWIndexBuilder builder = new SupSearch_LWIndexBuilder();
					SupSearch_LWIndex temp = builder.loadIndex(realDB, baseName+ "LWIndex/", false);
					CIndexExp.buildIndexTopDown(realDB, trainQuery, realDB, MyFactory.getUnCanDFS(), baseName, minSupt, 2*trainQuery.getTotalNum()/temp.getFeatureCount());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				CIndexExp.runIndexTopDown(realDB, query, baseName, false);
			}
		}
	}
	/**
	 * Current Memory Measurement is not precise. The following function measure the static memory consumption of each index
	 * @throws IOException
	 */
	public void memoryMesurement(){
		String dirName ="/data/home/duy113/SupSearchExp/AIDSNew/";
		String dbFileName = dirName + "DBFile";
		double[] minSupts = new double[4];
		minSupts[0] = 0.05; minSupts[1] = 0.03; minSupts[2] =0.02; minSupts[3] = 0.01;
	
		for(int j = 0; j< 4; j++){
			double minSupt = minSupts[j];
			for(int i = 2; i<=10; i = i+2){
				
				String baseName = dirName + "G_" + i + "MinSup_" + minSupt + "/";
				System.out.println(baseName);
				GraphDatabase realDB = new GraphDatabase_OnDisk(dbFileName + i, MyFactory.getDFSCoder());	
				try {
					System.out.println("LWIndex");
					LWIndexExp.memoryConsumption(realDB,baseName, false);
					System.out.println("PrefixHi");
					PrefixIndexExp.memoryConsumptionHi(realDB, baseName, false);
					System.out.println("GPTree");
					GPTreeExp.memoryConsumption(realDB,baseName,false);
					System.out.println("CIndexFlat");
					CIndexExp.memoryConsumptionFlat(realDB, baseName,false);
					System.out.println("CIndexTopDown");
					CIndexExp.memoryConsumptionTopDown(realDB,baseName+"2",false);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	public void postingMemoryMesurement(){
		String dirName ="/data/home/duy113/SupSearchExp/AIDSNew/";
		String dbFileName = dirName + "DBFile";
		double[] minSupts = new double[4];
		minSupts[0] = 0.05; minSupts[1] = 0.03; minSupts[2] =0.02; minSupts[3] = 0.01;
	
		for(int j = 0; j< 4; j++){
			double minSupt = minSupts[j];
			for(int i = 2; i<=10; i = i+2){
				
				String baseName = dirName + "G_" + i + "MinSup_" + minSupt + "/";
				System.out.println(baseName);
				GraphDatabase realDB = new GraphDatabase_OnDisk(dbFileName + i, MyFactory.getDFSCoder());	
				try {
					//System.out.println("LWIndex");
					LWIndexExp.pMemoryConsumption(realDB,baseName);
					//System.out.println("PrefixHi");
					PrefixIndexExp.pMemoryConsumptionHi(realDB, baseName);
					//System.out.println("GPTree");
					GPTreeExp.pMemoryConsumption(realDB,baseName);
					//System.out.println("CIndexFlat");
					CIndexExp.pMemoryConsumptionFlat(realDB, baseName);
					//System.out.println("CIndexTopDown");
					CIndexExp.pMemoryConsumptionTopDown(realDB,baseName+"2");
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
		}
	}
	
	/**
	 * For large experiments only:
	 * Due to the fact that LW-index takes longer time to be mined on large queries. 
	 * We use the Database distribution as the surrogate to test the performance of LW-index
	 * @throws ParseException 
	 * @throws IOException 
	 * @throws LockObtainFailedException 
	 * @throws CorruptIndexException 
	 */
	public void buildLWIndexFake() {
		String home = "/data/home/duy113/SupSearchExp/AIDSLargeNew/";
		GraphDatabase trainingDB = new GraphDatabase_OnDisk(home + "DBFile_Raw", MyFactory.getSmilesParser());
		GraphDatabase query = new GraphDatabase_OnDisk(home + "testQuery", MyFactory.getUnCanDFS());		//i > =6
		boolean lucene_im_mem = false;
		double[] minSupts = new double[4];
		minSupts[0] = 0.05; minSupts[1] = 0.03; minSupts[2] = 0.02; minSupts[3]= 0.01;
		int lwIndexCount[] = new int[1];
		lwIndexCount[0] = 590;
		
		for(int i = 0; i< minSupts.length; i++){
			double minSupt = minSupts[i];
			String baseName = home + "MinSup_Fake_" + minSupt;
			try {
				LWIndexExp.buildIndex(trainingDB, trainingDB, trainingDB, MyFactory.getUnCanDFS(), baseName, minSupt, lwIndexCount);
				LWIndexExp.runIndex(trainingDB, query, baseName, lucene_im_mem);
			} catch (CorruptIndexException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (LockObtainFailedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static void main(String[] args){
		AIDSPatch exp = new AIDSPatch();
		//exp.buildLWIndexFake(); // build the Fake LW-Index for Large Experiments
		//exp.reBuildCIndexTopDown(); //rebuild the CIndexTopDown and Run
		//measure the memory consumption
		//exp.memoryMesurement();
		exp.postingMemoryMesurement();
		try {
			AIDSLargeExp.memConsumption();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
