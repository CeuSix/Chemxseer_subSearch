package edu.psu.chemxseer.structure.setcover.IO;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;

import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter;
import edu.psu.chemxseer.structure.setcover.sets.CoverSet_FeatureWrapper;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.NoPostingFeatures;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.OneFeatureMultiClass;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.PostingFeaturesMultiClass;

/**
 * Given a set of selected features, change then write them to
 * a File of sets
 * @author dayuyuan
 *
 */
public class FeatureFileGenerater {
	
	private FeatureFileGenerater(){
		// prevent the FeatureFilegenerator Object being created
	}
	/**
	 * Given the whole set of MultiClass Features with their postings
	 * convert all the features to CoverSet_FeatureWrapper:
	 * And then store them on outputFile
	 * The header of the file also contains: An array of feature count & item count
	 * @param features
	 * @param outputFile
	 * @param type
	 * @throws IOException
	 */
	public static void FeatureToSetFile(
			PostingFeaturesMultiClass features, 
			String outputFile, IFeatureSetConverter converter) throws IOException{
		int[] classGraphsCount = features.getClassGraphsCount();
		if(classGraphsCount.length!=2)
			System.out.println("This May not work, because the graph class !=2");
		int classOneNumber = classGraphsCount[0];
		int classTwoNumber = classGraphsCount[1];
		
		NoPostingFeatures<OneFeatureMultiClass> noPostFeatures = features.getFeatures();
		ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(outputFile));
		//1. Write the header of the output file
		int[] statistics = new int[2];
		statistics[0] = noPostFeatures.getfeatureNum();
		statistics[1] = classOneNumber * classTwoNumber;
		outputStream.writeObject(statistics);
		
		//2. Writer set one after another
		// Here I used the Simple Feature Converter [No upper bound supported]
		
		for(int i = 0; i< noPostFeatures.getfeatureNum(); i++){
			OneFeatureMultiClass oneFeature = noPostFeatures.getFeature(i);
			int[][] postings = features.getPosting(oneFeature);
			CoverSet_FeatureWrapper wrapper= new CoverSet_FeatureWrapper(oneFeature, postings);
			outputStream.writeObject(wrapper);
		}
		outputStream.close();
	}
}
