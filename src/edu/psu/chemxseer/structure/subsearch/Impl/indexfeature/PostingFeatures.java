package edu.psu.chemxseer.structure.subsearch.Impl.indexfeature;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

import edu.psu.chemxseer.structure.subsearch.Interfaces.IOneFeature;
/**
 * This is a Class saving candidate Features
 * GFeatures implementation
 * @author dayuyuan
 *
 */
public class PostingFeatures {
	protected NoPostingFeatures<OneFeatureImpl> features;
	protected FeaturePosting postingFetcher;
	protected FeaturePostingMem memRepresent;
	
	public PostingFeatures(String postingFile, NoPostingFeatures<OneFeatureImpl> features){
		if(postingFile!=null)
			this.postingFetcher = new FeaturePosting(postingFile);
		else this.postingFetcher = null;
		
		this.features = features;
		this.memRepresent = null;
	}
	
	public PostingFeatures(FeaturePosting postingFetcher2,
			NoPostingFeatures<OneFeatureImpl> newFeatures) {
		this.postingFetcher = postingFetcher2;
		this.features = newFeatures;
		this.memRepresent = null;
	}
	
	/**
	 * Load the posting file into memory
	 */
	public void loadPostingIntoMemory(){
		if(memRepresent!=null)
			System.out.println("Error in loadPostingIntoMemory:: PostingFeatures, the posting already" +
					"exists");
		else{
			this.memRepresent = new FeaturePostingMem();
			for(int i = 0; i< features.getfeatureNum(); i++){
				long shift = features.getFeature(i).shift;
				this.memRepresent.insertPosting(shift, postingFetcher.getPosting(shift));
			}		
		}
	}
	
	/**
	 * To save the memory, remove and grabage college the in-memory posting
	 */
	public void discardInMemPosting(){
		this.memRepresent = null;
	}

	public int[] getPosting(IOneFeature feature){ 
		if(this.memRepresent == null){
			return this.postingFetcher.getPosting(feature.getPostingShift());
		}
		else return this.memRepresent.getPosting(feature.getPostingShift());
	}

	public int[] getPosting(Integer featureID) {
		return this.getPosting(this.features.getFeature(featureID));
	}

	public PostingFeatures getSelectedFeatures(String newFeatureFile, String newPostingFile, 
			boolean reserveID){
		//1. Get Selected Features
		List<OneFeatureImpl> selectedFeatures = this.features.getSelectedFeatures();
		//2. Store
		try {
			return this.saveFeatures(newFeatureFile, newPostingFile, reserveID, selectedFeatures);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
			
	}
	
	public PostingFeatures getUnSelectedFeatures(String newFeatureFile, String newPostingFile,
			boolean reserveID) throws IOException{
		//1. Get Selected Features
		List<OneFeatureImpl> selectedFeatures = this.features.getUnSelectedFeatures();
		//2. Store
		try {
			return this.saveFeatures(newFeatureFile, newPostingFile, reserveID, selectedFeatures);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;	
	}
	
	private PostingFeatures saveFeatures(String newFeatureFile, 
			String newPostingFile, boolean reserveID, List<OneFeatureImpl> selectedFeatures) 
	throws IOException{
		//2. Record the Postings
		if(newPostingFile!=null){
			FileChannel postingChannel = new FileOutputStream(newPostingFile).getChannel();
			int index = 0;
			for(OneFeatureImpl oneFeature : selectedFeatures){
				int fID = index;
				if(reserveID)
					fID = oneFeature.getFeatureId();
				long shift = oneFeature.getPostingShift();
				long newShift = this.postingFetcher.savePostings(
						  postingChannel, shift, fID);
				oneFeature.setPostingShift(newShift);
				index++;
			}
		}
		//3. Save the Features
		NoPostingFeatures<OneFeatureImpl> newFeatures = 
			new NoPostingFeatures<OneFeatureImpl>(newFeatureFile, selectedFeatures, reserveID);
		//4. Return 
		if(newPostingFile!=null){
			return new PostingFeatures(newPostingFile, newFeatures);
		}
		else return new PostingFeatures(this.postingFetcher, newFeatures);
	}

	public NoPostingFeatures<OneFeatureImpl> getFeatures() {
		return this.features;
	}

}
