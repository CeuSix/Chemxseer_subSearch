package edu.psu.chemxseer.structure.supersearch.CIndex;

import java.io.IOException;

import de.parmol.parsers.GraphParser;

import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.setcover.IO.IInputSequential;
import edu.psu.chemxseer.structure.setcover.IO.Input_Mem;
import edu.psu.chemxseer.structure.setcover.featureGenerator.FeatureConverter_SupSearch;
import edu.psu.chemxseer.structure.setcover.impl.MaxCoverSolver_Sequential;
import edu.psu.chemxseer.structure.setcover.maxCoverStatus.ICoverStatus_Set;
import edu.psu.chemxseer.structure.setcover.maxCoverStatus.Status_BooleanMatrix;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureProcessorDuralClass;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.NoPostingFeatures;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.OneFeatureMultiClass;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.PostingFeaturesMultiClass;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphDatabase;
import edu.psu.chemxseer.structure.supersearch.Experiment.GPTreeExp;
import edu.psu.chemxseer.structure.util.MemoryConsumptionCal;

public class CIndexFeatureMiner {
	
	public static PostingFeaturesMultiClass minFreqFeatures( GraphDatabase gDB, 
			GraphDatabase gQuery, GraphParser gParser, String baseName, double minSupt){
		FeatureProcessorDuralClass processor = new FeatureProcessorDuralClass();
		//1. First Step: merge and prepare for the file
		try {
			processor.mergeGraphFile(gDB, gQuery, gParser, baseName + "merge", baseName + "class");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//2. Second Step: mine raw frequent features
		String[] postingFiles = new String[4];
		for(int i = 0; i< 4; i++)
			postingFiles[i] = baseName + "posting" + i;
		PostingFeaturesMultiClass rawFeatures = processor.frequentSubgraphMining(baseName + "merge", baseName + "class", 
				baseName + "feature", postingFiles, gParser, 
				minSupt, -1, 10);
		return rawFeatures;
	}
	
	/**
	 * Mine "K" indexing features, and store all the temporary results under the "baseName" folder
	 * @param K
	 * @param gDB
	 * @param gQuery
	 * @param baseName
	 * @return
	 */
	public static PostingFeaturesMultiClass mineFeatures(int K, GraphDatabase gDB, 
			GraphDatabase gQuery, GraphParser gParser, String baseName, double minSupt){
		FeatureProcessorDuralClass processor = new FeatureProcessorDuralClass();
		//1. First Step: merge and prepare for the file
		try {
			processor.mergeGraphFile(gDB, gQuery, gParser, baseName + "merge", baseName + "class");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//2. Second Step: mine raw frequent features
		String[] postingFiles = new String[4];
		for(int i = 0; i< 4; i++)
			postingFiles[i] = baseName + "posting" + i;
		PostingFeaturesMultiClass rawFeatures = processor.frequentSubgraphMining(baseName + "merge", baseName + "class", 
				baseName + "feature", postingFiles, gParser, 
				minSupt, -1, 10);
		//3. Third Step: using the set-cover to mine the indexing features
		System.out.println("Select Index Patterns for CIndexFlat");
		System.out.println("(1) Boolean Matrix Status, MaxCoverSolver_Sequential solver, " +
				"in_memory input");
		MemoryConsumptionCal.runGC();
		double startMemory = MemoryConsumptionCal.usedMemoryinMB();
		long startTime = System.currentTimeMillis();
		
		IInputSequential input = Input_Mem.newInstance(rawFeatures);
		ICoverStatus_Set status = new Status_BooleanMatrix(
				new FeatureConverter_SupSearch(rawFeatures.getClassGraphsCount()[0], rawFeatures.getClassGraphsCount()[1]));
		MaxCoverSolver_Sequential solver = new MaxCoverSolver_Sequential(input, status);
		int[] selectedIDs = solver.runGreedy(K);
		
		MemoryConsumptionCal.runGC();
		double endMemory = MemoryConsumptionCal.usedMemoryinMB();
		System.out.println("(2) Space of cIndex pattern selection:" + (endMemory-startMemory));
		
		for(int id:selectedIDs)
			rawFeatures.getFeatures().getFeature(id).setSelected();
		System.out.println("(3) Time for cIndex pattern selection: " + (System.currentTimeMillis()-startTime));
		System.out.println("(4) After Pattern Selection: " + selectedIDs.length + " out of " + rawFeatures.getFeatures().getfeatureNum() + " frequent patterns as selected for Indexing");
		//4. Return selected Features
		PostingFeaturesMultiClass selectedFeatures = 
			rawFeatures.getSelectedFeatures(baseName + "featuresel", null, false);
		return selectedFeatures;
	}
}
