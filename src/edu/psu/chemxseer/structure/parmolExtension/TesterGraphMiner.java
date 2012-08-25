package edu.psu.chemxseer.structure.parmolExtension;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;

import de.parmol.parsers.GraphParser;
import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureProcessorDuralClass;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.PostingFeatures;

public class TesterGraphMiner {
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException, InstantiationException, IllegalAccessException, ClassNotFoundException{
		String gDBFileName = "/Users/dayuyuan/Documents/workspace/Experiment1/graphDataSelected";
		String featureFileName = "/Users/dayuyuan/Documents/workspace/Experiment1/features";
		String postingFileName = "/Users/dayuyuan/Documents/workspace/Experiment1/postings";
		
		String[] postingFileNames = new String[4];
		for(int i = 0; i< postingFileNames.length; i++)
			postingFileNames[i] = postingFileName + i ;
		FeatureProcessorDuralClass test = new FeatureProcessorDuralClass();
		test.mergeGraphFile(gDBFileName, gDBFileName, gDBFileName + "_merged", gDBFileName + "_class");
		long time1 = System.currentTimeMillis();
		test.frequentSubgraphMining(gDBFileName + "_merged", gDBFileName + "_class", featureFileName, 
				postingFileNames, MyFactory.getSmilesParser(), 0.1, 0.1, 10);
		long time2 = System.currentTimeMillis();
		test.frequentSubgraphMining2(gDBFileName + "_merged", gDBFileName + "_class", featureFileName, 
				postingFileNames, MyFactory.getSmilesParser(), 0.1, 0.1, 10);
		long time3 = System.currentTimeMillis();
		//System.out.println(time2-time1);
		//System.out.println(time3-time2);
	}
}
