package edu.psu.chemxseer.structure.supersearch.Experiment;
import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.subsearch.Impl.GraphDatabase_OnDisk;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphDatabase;

public class AIDSRun {
	public static void runAIDS() {	
		String dirName ="/data/home/duy113/SupSearchExp/AIDSNew/";
		String dbFileName = dirName + "DBFile";
//		String testQuery15 = dirName + "TestQuery15";
		String testQuery25 = dirName + "TestQuery25";
		String testQuery35 = dirName + "TestQuery35";
		double[] minSupts = new double[4];
		minSupts[0] = 0.05; minSupts[1] = 0.03; minSupts[2] =0.02; minSupts[3] = 0.01;
		for(int t = 0; t < 2; t++){
			GraphDatabase query = null;
			if(t == 0){
				query = new GraphDatabase_OnDisk(testQuery25, MyFactory.getSmilesParser());
				System.out.println("queries of size 25");
			}
			else {
				query = new GraphDatabase_OnDisk(testQuery35, MyFactory.getSmilesParser());
				System.out.println("queries of size 35");
			}
			for(int w = 0; w < 2; w++){
				boolean  lucene_im_mem = false;
				if(w == 1){
					lucene_im_mem = true;
					System.out.println("in-memory lucence");
				}
				else System.out.println("on-disk lucnece");
				for(int j = 0; j< 4; j++){
					double minSupt = minSupts[j];
					for(int i = 2; i<=10; i = i+2){
						String baseName = dirName + "G_" + i + "MinSup_" + minSupt + "/";
						GraphDatabase trainingDB = new GraphDatabase_OnDisk(dbFileName + i, MyFactory.getDFSCoder());
						System.out.println(baseName + "LWindex");
						LWIndexExp.runIndex(trainingDB, query, baseName, lucene_im_mem);
						System.gc();			
						System.out.println(baseName + "PrefixIndexHi");
						PrefixIndexExp.runHiIndex(trainingDB, query, baseName, lucene_im_mem);
						System.gc();
						System.out.println(baseName + "GPTree");
						GPTreeExp.runIndex(trainingDB, query, baseName, lucene_im_mem);
						System.gc();
						System.out.println(baseName + "CIndexFlat");
						CIndexExp.runIndex(trainingDB, query, baseName, lucene_im_mem);
						System.gc();
						System.out.println(baseName + "CIndexTopDown");
						CIndexExp.runIndexTopDown(trainingDB, query, baseName, lucene_im_mem);
						System.gc();
					}
				}
			}
		}
	}
	
	public static void runAIDSLarge(){
		String dirName ="/data/home/duy113/SupSearchExp/AIDSLargeNew/";
		GraphDatabase trainingDB = new GraphDatabase_OnDisk(dirName + "DBFile_Raw", MyFactory.getSmilesParser());
		GraphDatabase query = new GraphDatabase_OnDisk(dirName + "testQuery", MyFactory.getUnCanDFS());	
		boolean lucene_im_mem = false;
		double[] minSupts = new double[6];
		minSupts[0] = 0.05; minSupts[1] = 0.03; minSupts[2] = 0.02; minSupts[3]= 0.01;
		minSupts[4]=0.008; minSupts[5] = 0.006;
		
		for(int i = 0; i< 4; i++){
			double minSupt = minSupts[i];
			String baseName = dirName + "MinSup_" + minSupt;
			
			System.out.println(baseName + "Run The Indexes:");
			System.out.println(baseName + "LWIndex");
			LWIndexExp.runIndex(trainingDB, query, baseName, lucene_im_mem);
			System.gc();
			System.out.println(baseName + "LWIndexFake");
			LWIndexExp.runIndex(trainingDB, query, dirName + "MinSup_Fake_" + minSupt, lucene_im_mem);
			System.gc();
			System.out.println(baseName + "PrefixIndexHi");
			PrefixIndexExp.runHiIndex(trainingDB, query, baseName, lucene_im_mem);
			System.gc();
			System.out.println(baseName + "GPTree");
			GPTreeExp.runIndex(trainingDB, query, baseName, lucene_im_mem);
			System.gc();
		}
		
		for(int i = 4; i< 6; i++){
			double minSupt = minSupts[i];
			String baseName = dirName + "MinSup_" + minSupt;
			
			System.out.println(baseName + "Run The Indexes:");
			System.out.println(baseName + "LWIndex");
			LWIndexExp.runIndex(trainingDB, query, baseName, lucene_im_mem);
			System.gc();
			System.out.println(baseName + "PrefixIndexHi");
			PrefixIndexExp.runHiIndex(trainingDB, query, baseName, lucene_im_mem);
			System.gc();
		}
	}
	
	public static void main(String[] args){
		//runAIDS();
		runAIDSLarge();
	}
}
