package edu.psu.chemxseer.structure.setcover.IO;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;

import edu.psu.chemxseer.structure.setcover.maxCoverStatus.ICoverStatusStream;
import edu.psu.chemxseer.structure.setcover.sets.ICoverSet_FeatureWrapper;
/**
 * Write the sets one after another: needed in the Sort_SetCover
 * @author dayuyuan
 *
 */
public class Input_FileBucket_Writter{
	private ObjectOutputStream outputStream;
	
	/**
	 * No header output
	 * @param outputFile
	 * @param append: append writing or start new writing
	 */
	public Input_FileBucket_Writter(String outputFile, boolean append){
		try {
			if(append == false){
				this.outputStream = new ObjectOutputStream(new FileOutputStream(outputFile));
//				this.outputStream.writeInt(universeSize);
			}
			else 
				this.outputStream = new NOHeaderOutput_ObjectOuput(new FileOutputStream(outputFile, true));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void finalize(){
		try {
			this.outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeWriter(){
		try {
			this.outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean writerSet(ICoverSet_FeatureWrapper set){
		if(set == null)
			return false;
		try {
			this.outputStream.writeObject(set);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return true;
	}

	public boolean writeSelectedSets(ICoverStatusStream status) throws IOException{
		ICoverSet_FeatureWrapper[] selectedSets = status.getSelectedSets();
		this.outputStream.writeObject(selectedSets.length);
		
		for(ICoverSet_FeatureWrapper set : selectedSets)
			this.writerSet(set);
		this.outputStream.close();
		return true;
	}

}
class NOHeaderOutput_ObjectOuput extends ObjectOutputStream{

	protected NOHeaderOutput_ObjectOuput(OutputStream out) throws IOException,
			SecurityException {
		super(out);
	}
	protected void writeStreamHeader() throws IOException{
		// do nothing
	}
	
}
