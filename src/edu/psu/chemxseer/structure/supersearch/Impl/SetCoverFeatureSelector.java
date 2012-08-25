package edu.psu.chemxseer.structure.supersearch.Impl;

import java.io.File;
import java.io.IOException;

import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.setcover.featureGenerator.IFeatureSetConverter;
import edu.psu.chemxseer.structure.setcover.interfaces.IMaxCoverSolver;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.NoPostingFeatures;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.OneFeatureMultiClass;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.PostingFeaturesMultiClass;

/**
 * The Feature Selector
 * (1) Mine Raw Features [DuralClassFeature miner]
 * (2) Mine the useful features with the Set-cover solver
 * @author dayuyuan
 *
 */
public class SetCoverFeatureSelector {
//	// All the Parameters are tuned here:
//	private float minSuport = 0.1F;
//	private int maxNonSelect = 10;
//	
//	private String mergedFileName = "mergedFile";
//	private String classFileName = "classFile";
//	private String featureFileName = "featureFile";
//	private String postingFileName = "postingFile";
//	private String featureTempFile = "tempFile";
//	
//	
//	public PostingFeaturesMultiClass mineRawFeatures(String[] gDBDFS, File queryFile,
//			String prefix) {
//		FeatureProcessorDuralClass miner = new FeatureProcessorDuralClass(minSuport, maxNonSelect);
//		PostingFeaturesMultiClass rawFeatures = null;
//		try {
//			miner.mergeGraphFile(gDBDFS, queryFile, prefix + mergedFileName, prefix+classFileName);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		String[] postingFileNames = new String[2];
//		for(int i = 0; i<2; i++)
//			postingFileNames[i] = prefix+ postingFileName + i;
//		miner.frequentSubgraphMining(prefix+mergedFileName, prefix+classFileName, prefix+featureFileName, 
//				postingFileNames, true, MyFactory.getSmilesParser());
//		return rawFeatures;
//	}
//	
//	/**
//	 * Mine The Raw Features & store all the temporary file into the "prefix" folder
//	 * @param graphFile
//	 * @param queryFile
//	 * @param prefix
//	 * @return
//	 */
//	public PostingFeaturesMultiClass mineRawFeatures(String graphFile, String queryFile, String prefix){
//		FeatureProcessorDuralClass miner = new FeatureProcessorDuralClass(minSuport, maxNonSelect);
//		PostingFeaturesMultiClass rawFeatures = null;
//		try {
//			miner.mergeGraphFile(graphFile, queryFile, prefix + mergedFileName, prefix+classFileName);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		String[] postingFileNames = new String[2];
//		for(int i = 0; i<2; i++)
//			postingFileNames[i] = prefix+ postingFileName + i;
//		miner.frequentSubgraphMining(prefix+mergedFileName, prefix+classFileName, prefix+featureFileName, 
//				postingFileNames, true, MyFactory.getSmilesParser());
//		return rawFeatures;
//	}
//	/**
//	 * Mine the Features, all the things are stored in memory
//	 * @param rawFeatures: all the frequent features
//	 * @param K: selected the Top K features
//	 * @param solverType: the setcover solver type used to do the feature selection
//	 * @param selectedFeaturesFile: all the selected features are stored here
//	 * @return
//	 */
//	public PostingFeaturesMultiClass topKFeatureMinning(PostingFeaturesMultiClass rawFeatures, int K,
//			int solverType, String selectedFeaturesFile){
//		IFeatureSetConverter converter = new IFeatureSetConverter();
//		try {
//			converter.supFeatureToSet(rawFeatures, featureTempFile);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		IMaxCoverSolver solver = IMaxCoverSolver.SolverLoader(solverType, featureTempFile);
//		int[] selectedFIds = solver.runGreedy(K);
//		
//		NoPostingFeatures<OneFeatureMultiClass> noPostingFeatures = rawFeatures.getFeatures();
//		noPostingFeatures.setAllUnSelected();
//		for(int i = 0; i< selectedFIds.length; i++)
//			noPostingFeatures.getFeature(selectedFIds[i]).setSelected();
//		PostingFeaturesMultiClass result = null;
//		result = rawFeatures.getSelectedFeatures(selectedFeaturesFile, new String[0], true);
//		return result;
//	}

}
