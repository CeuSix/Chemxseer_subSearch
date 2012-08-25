package edu.psu.chemxseer.structure.setcover.IO;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.psu.chemxseer.structure.setcover.sets.CoverSet_FeatureWrapper;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.FeatureFactory.FeatureFactoryType;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.NoPostingFeatures;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.OneFeatureImpl;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.OneFeatureMultiClass;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.PostingFeaturesMultiClass;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IOneFeature;

/**
 * A in-memory input, 
 * assuming all the CoverSet_FeatureWrapper (both feature & postings) objects
 * can be stored in-memory
 * @author dayuyuan
 *
 */
public class Input_Mem implements IInputSequential, IInputRandom{
	protected List<CoverSet_FeatureWrapper> allSets;
	protected int internalCounter;
//	protected int universeSize;
	
	/**
	 * Read the input from a File recording all the CoverSet_FeatureWrapper object
	 * A header: including the set-count should exist
	 * @param fileName
	 * @param containHeader
	 * @return
	 */
	public static Input_Mem newInstance(String fileName){
		return new Input_Mem(fileName);
	}
	public static Input_Mem newInstance(PostingFeaturesMultiClass MultiClasses){
		return new Input_Mem(MultiClasses);
	}
	public static Input_Mem newInstance(List<CoverSet_FeatureWrapper> sets){
		return new Input_Mem(sets);
	}
	public static Input_Mem newEmptyInstance() {
		return new Input_Mem();
	}

	
	private Input_Mem(String fileName){
		try {
			ObjectInputStream objectReader = new ObjectInputStream(new FileInputStream(fileName));
			int setCount = objectReader.readInt();
//			this.universeSize = objectReader.readInt();
			this.allSets = new ArrayList<CoverSet_FeatureWrapper>(setCount);
			for(int i = 0; i< setCount; i++){
				Object obj = objectReader.readObject();
				if(obj == null)
					break;
				else allSets.add((CoverSet_FeatureWrapper) obj);
			}
			this.internalCounter = 0;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in Initializing the StreamInput of Set Cover");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private Input_Mem(PostingFeaturesMultiClass features){
//		this.universeSize = features.getClassGraphsCount()[0] * features.getClassGraphsCount()[1];
		this.allSets = new ArrayList<CoverSet_FeatureWrapper>(features.getFeatures().getfeatureNum());
		int featureCount = features.getFeatures().getfeatureNum();
		for(int i = 0; i< featureCount; i++){
			allSets.add(new CoverSet_FeatureWrapper(features.getFeatures().getFeature(i),
					features.getPosting(i)));
		}
		this.internalCounter = 0;
	}
	
	private Input_Mem(List<CoverSet_FeatureWrapper> sets){
		this.allSets = sets;
		this.internalCounter = 0;
	}
	private Input_Mem(){
		this.allSets = new ArrayList<CoverSet_FeatureWrapper>();
		this.internalCounter = 0;
	}
	
	/**
	 * Return the next set by reading the file
	 * Return null if no such set exists
	 * @return
	 */
	public CoverSet_FeatureWrapper nextSet(){
		if(this.internalCounter == allSets.size())
			return null;
		else{
			return this.allSets.get(this.internalCounter++);
		}
	}
	
	
	/**
	 * Rewind, I think here, it is better to reopen the file
	 */
	public void reWind(){
		this.internalCounter = 0;		
	}
	
	/**
	 * Write all the Selected Sets on disk for further usage. 
	 */
	public void storeSelected(int[] result, String selectedFeatureFile){
		Arrays.sort(result);
		ObjectOutputStream outputStream;
		try {
			outputStream = new ObjectOutputStream(new FileOutputStream(selectedFeatureFile));
			int setCount = result.length;
			outputStream.writeInt(setCount);
//			outputStream.writeInt(universeSize);
			for(int rID: result){
				if(rID < 0)
					break;
				outputStream.writeObject(this.allSets.get(rID));
			}
			outputStream.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Given the in memory type, to noPostingFeatures
	 * @param onefeature
	 * @return
	 * @throws IOException 
	 */
	public NoPostingFeatures toNoPostingFeatures(FeatureFactoryType featureType) throws IOException {
		if(featureType ==FeatureFactoryType.MultiFeature){
			List<OneFeatureMultiClass> features = new ArrayList<OneFeatureMultiClass>();
			for(CoverSet_FeatureWrapper oneFeature:this.allSets){
				features.add(oneFeature.getFeature());
			}
			return new NoPostingFeatures<OneFeatureMultiClass>(null, features, false);
		}
		else{
			//OneFeatureImpl
			List<OneFeatureImpl> features = new ArrayList<OneFeatureImpl>();
			for(CoverSet_FeatureWrapper oneFeature:this.allSets){
				features.add(oneFeature.getFeature().toFeatureImpl());
			}
			return new NoPostingFeatures<OneFeatureImpl>(null, features, false);
		}
	}
	@Override
	public CoverSet_FeatureWrapper getSet(int setID) {
		assert setID > 0 && setID < this.allSets.size();
		return this.allSets.get(setID);
	}
//	@Override
//	public int getUniverseCount() {
//		return this.universeSize;
//	}
	public int getSetCount() {
		return allSets.size();
	}
	@Override
	public List<CoverSet_FeatureWrapper> nextSets(int batchNum) {
		List<CoverSet_FeatureWrapper> result = new ArrayList<CoverSet_FeatureWrapper>();
		for(int i = 0; i< batchNum; i++){
			CoverSet_FeatureWrapper oneF = this.nextSet();
			if(oneF == null)
				break;
			else result.add(oneF);
		}
		return result;
	}
	/**
	 * Append on feature at the end of the current feature list
	 * @param feature
	 */
	public void appendFeature(CoverSet_FeatureWrapper feature) {
		this.allSets.add(feature);
	}
}
