package edu.psu.chemxseer.structure.subsearch.Impl.indexfeature;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import de.parmol.parsers.GraphParser;

import edu.psu.chemxseer.structure.parmolExtension.GSpanMiner_MultiClass;
import edu.psu.chemxseer.structure.parmolExtension.GSpanMiner_MultiClass_Iterative;
import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphDatabase;


/**
 * This is the Feature Miner for "Set-Cover" feature selection pre-processing
 * @author dayuyuan
 *
 */
public class FeatureProcessorDuralClass {
	
	
	public FeatureProcessorDuralClass(){
	}
	
	
	/**
	 * Given two graph files, merge them into one, and at the same time create a 
	 * graph class (frequency) file
	 * Assume that the two graph files are not overlapping
	 * The first file (graphFileOne) will be treated as the positive class [frequent graphs will be mined here]
	 * The second file (graphFileTwo) will be treated as the negative class
	 * @param graphFileOne
	 * @param graphFileTwo
	 * @param mergedFileName
	 * @param classFileName
	 * @return
	 * @throws IOException 
	 */
	public void mergeGraphFile(String graphFileOne, String graphFileTwo, 
			String mergedFileName, String classFileName) throws IOException{
		BufferedReader readerOne = new BufferedReader(new FileReader(graphFileOne));
		BufferedReader readerTwo = new BufferedReader(new FileReader(graphFileTwo));
		BufferedWriter writer = new BufferedWriter(new FileWriter(mergedFileName));
		BufferedWriter classWriter = new BufferedWriter(new FileWriter(classFileName));
		
		String aLine = readerOne.readLine();
		String spliter = " => ";
		int id = 0;
		while(aLine!=null){
			String[] graphString = aLine.split(spliter);
			writer.write(id + spliter + graphString[1]);
			writer.newLine();
			classWriter.write(id + spliter + 1 + "," + 0);
			classWriter.newLine();
			aLine = readerOne.readLine();
			id++;
		}
		readerOne.close();
		
		aLine = readerTwo.readLine();
		while(aLine!=null){
			String[] graphString = aLine.split(spliter);
			writer.write(id + spliter + graphString[1]);
			writer.newLine();
			classWriter.write(id + spliter + 0 + "," + 1);
			classWriter.newLine();
			aLine = readerTwo.readLine();
			id++;
		}
		readerTwo.close();
		writer.close();
		classWriter.close();
	}
	
	public void mergeGraphFile(GraphDatabase gDB1, GraphDatabase gDB2, GraphParser gSerilaizer, 
			String mergedFileName, String classFileName) throws IOException{
		BufferedWriter writer = new BufferedWriter(new FileWriter(mergedFileName));
		BufferedWriter classWriter = new BufferedWriter(new FileWriter(classFileName));
		
		String spliter = " => ";
		int id = 0;
		for(int i = 0; i< gDB1.getTotalNum(); i++, id++){
			writer.write(id + spliter + gSerilaizer.serialize(gDB1.findGraph(i)));
			writer.newLine();
			classWriter.write(id + spliter + 1 + "," + 0);
			classWriter.newLine();
		}
		for(int i = 0; i< gDB2.getTotalNum(); i++, id++){
			writer.write(id + spliter + gSerilaizer.serialize(gDB2.findGraph(i)));
			writer.newLine();
			classWriter.write(id + spliter + 0 + "," + 1);
			classWriter.newLine();
		}
		writer.close();
		classWriter.close();
	}
	
	/**
	 * Given two graph files, merge them into one, and at the same time create a 
	 * graph class (frequency) file
	 * Assume that the two graph files are not overlapping
	 * The first file (graphFileOne) will be treated as the positive class [frequent graphs will be mined here]
	 * The second file (graphFileTwo) will be treated as the negative class
	 * @param gDBDFS: Class One Graphs: Graph strings in DFS code format
	 * @param queryFile: Class Two Graphs: the query Graph
	 * @param mergedFileName: The merged FileName: output
	 * @param classFileName: The classification fileName: output
	 * @throws IOException
	 */
	public void mergeGraphFile(String[] gDBDFS, File queryFile,
			String mergedFileName, String classFileName) throws IOException {
		BufferedReader readerTwo = new BufferedReader(new FileReader(queryFile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(mergedFileName));
		BufferedWriter classWriter = new BufferedWriter(new FileWriter(classFileName));
		
		int id = 0;
		String spliter = " => ";
		for(; id< gDBDFS.length; id++){
			String graphString = MyFactory.getSmilesParser().serialize(
					MyFactory.getDFSCoder().parse(gDBDFS[id], MyFactory.getGraphFactory()));
			writer.write(id + spliter + graphString);
			writer.newLine();
			classWriter.write(id + spliter + 1 + "," + 0);
			classWriter.newLine();
		}
		
		String aLine = readerTwo.readLine();
		while(aLine!=null){
			String[] graphString = aLine.split(spliter);
			writer.write(id + spliter + graphString[1]);
			writer.newLine();
			classWriter.write(id + spliter + 0 + "," + 1);
			classWriter.newLine();
			aLine = readerTwo.readLine();
			id++;
		}
		readerTwo.close();
		writer.close();
		classWriter.close();
	}
	
	/**
	 * Mine Features Frequent in the positive Class [First Class]:
	 * The first posting: database graphs containing but not equal to the feature
	 * The second posting: database graphs equals to the feature
	 * The second posting: database graphs containing but not equal to the feature
	 * The third posting: database graphs equal to the feature
	 * .......
	 * @param gDBFileName
	 * @param featureFileName
	 * @param postingFileNames
	 * @param minimumFrequency
	 * @param maxNonSelectDepth
	 * @param gParser
	 * @return
	 */
	public PostingFeaturesMultiClass frequentSubgraphMining(String gDBFileName, String classFrequencyFile,
			String featureFileName, String[] postingFileNames, GraphParser gParser, 
			double minimumFrequency, double minimuFrequency2, int maxNonSelectDepth){

		String[] args = {"-minimumFrequencies="+(-minimumFrequency) + "," + (-minimuFrequency2), 
				"-maximumFrequencies=100%,100%",
				"-maximumFragmentSize="+maxNonSelectDepth,
				"-graphFile="+gDBFileName,
				"-closedFragmentsOnly=flase", "-outputFile=temp", "-parserClass=" + gParser.getClass().getName(), 
				"-classFrequencyFile=" + classFrequencyFile,
				"-serializerClass=edu.psu.chemxseer.structure.iso.CanonicalDFS", "-memoryStatistics=false", "-debug=-1"};
		return GSpanMiner_MultiClass.gSpanMining(args, featureFileName, postingFileNames);
	}
	
	/**
	 * Iterative Feature Miner
	 * @param gDBFileName
	 * @param classFrequencyFile
	 * @param featureFileName
	 * @param postingFileNames
	 * @param gParser
	 * @param minimumFrequency
	 * @param minimumFrequency2
	 * @param maxNonSelectDepth
	 * @return
	 */
	public PostingFeaturesMultiClass frequentSubgraphMining2(String gDBFileName, String classFrequencyFile,
			String featureFileName, String[] postingFileNames, GraphParser gParser, 
			double minimumFrequency, double minimumFrequency2,  int maxNonSelectDepth){
		
		String[] args = {"-minimumFrequencies="+(-minimumFrequency) + "," + (-minimumFrequency2), 
				"-maximumFrequencies=100%,100%",
				"-maximumFragmentSize="+maxNonSelectDepth,
				"-graphFile="+gDBFileName,
				"-closedFragmentsOnly=flase", "-outputFile=temp", "-parserClass=" + gParser.getClass().getName(), 
				"-classFrequencyFile=" + classFrequencyFile,
				"-serializerClass=edu.psu.chemxseer.structure.iso.CanonicalDFS", "-memoryStatistics=false", "-debug=-1"};
		return GSpanMiner_MultiClass_Iterative.gSpanMining(args, featureFileName, postingFileNames);
	}

	/**
	 * Generater and Return a GSpanMiner_MultiClass_Iterative Pattern Enumerator
	 * @param gDBFileName
	 * @param classFrequencyFile
	 * @param gParser
	 * @param minimumFrequency
	 * @param maxNonSelectDepth
	 */
	public GSpanMiner_MultiClass_Iterative getPatternEnumerator(String gDBFileName, String classFrequencyFile,GraphParser gParser,
			double minimumFrequency, double minimumFrequency2, int maxNonSelectDepth) {
		String[] args = {"-minimumFrequencies="+(-minimumFrequency) + "," + (-minimumFrequency2), 
				"-maximumFrequencies=100%,100%",
				"-maximumFragmentSize="+maxNonSelectDepth,
				"-graphFile="+gDBFileName,
				"-closedFragmentsOnly=flase", "-outputFile=temp", "-parserClass=" + gParser.getClass().getName(), 
				"-classFrequencyFile=" + classFrequencyFile,
				"-serializerClass=edu.psu.chemxseer.structure.iso.CanonicalDFS", "-memoryStatistics=false", "-debug=-1"};
		
		return GSpanMiner_MultiClass_Iterative.getMiner(args);
	}
}
