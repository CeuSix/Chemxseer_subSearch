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
/**
 * All Sets are Pre-computed and Stored on Disk
 * The streaming implementation of the standard input interface
 * (1) Load all the sets from the disk
 * (2) Can output one sets each batch or a list of sets each batch
 * @author dayuyuan
 *
 */
public class Input_FileStream implements IInputSequential{
	protected String inputFileName;
	protected ObjectInputStream objectReader;
	protected int setCount;
//	protected int universeSize;
	protected boolean containHeader;
	
	/**
	 * The file records all the CoverSet_FeatureWrapper. 
	 * Assuming they are too-big to be loaded into memory
	 * So, load one at each time.
	 * @param containHeader: the first line denotes the # of sets contained
	**/
	public static Input_FileStream newInstance(String fileName, boolean containHeader){
		return new Input_FileStream(fileName, containHeader);
	}
	
	private Input_FileStream(String fileName, boolean containHeader){
		inputFileName = fileName;
		this.containHeader = containHeader;
		try {
			this.objectReader = new ObjectInputStream(new FileInputStream(fileName));
			if(this.containHeader){
				this.setCount = this.objectReader.readInt();
			}
			else this.setCount = -1;
//			this.universeSize = objectReader.readInt();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in Initializing the StreamInput of Set Cover");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	
	public void finalize(){
		try {
			if(objectReader!=null)
				this.objectReader.close();
		} catch (IOException e) {
			System.out.println("Error in Closing the StreamInput of Set Cover");
		}
	}
	
	/**
	 * Return the next set by reading the file
	 * Return null if no such file exists
	 * @return
	 */
	public CoverSet_FeatureWrapper nextSet(){
		try {
			Object obj = this.objectReader.readObject();
			if(obj == null)
				return null;
			else return (CoverSet_FeatureWrapper) obj;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Return the next few sets by reading the file
	 * The number of sets returned depends on an internal parameter batchNum
	 * The sets returned is less than batchNum if the reader reached the end of the input file
	 * @return
	 */
	public List<CoverSet_FeatureWrapper> nextSets(int batchNum){
		List<CoverSet_FeatureWrapper> results = new ArrayList<CoverSet_FeatureWrapper>();
		for(int i = 0; i< batchNum; i++){
			Object obj = null;
			try {
				obj = this.objectReader.readObject();
			} catch (IOException e) {
				// Do nothing: may be end of the file
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(obj == null)
				break;
			else{
				results.add((CoverSet_FeatureWrapper)obj);
			}
		}
		return results;
	}
	
	/**
	 * Rewind, I think here, it is better to reopen the file
	 */
	public void reWind(){
		try {
			this.objectReader.close();
			this.objectReader = new ObjectInputStream(new FileInputStream(this.inputFileName));
			// skip the first line denoting the total number of sets in this input file
			if(containHeader)
				this.objectReader.readInt();
//			this.universeSize = this.objectReader.readInt();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			System.out.println("Error in Initializing the StreamInput of Set Cover");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}


	public void storeSelected(int[] result, String selectedFeatureFile){
		Arrays.sort(result);
		this.reWind();
		
		ObjectOutputStream outputStream;
		try {
			outputStream = new ObjectOutputStream(new FileOutputStream(selectedFeatureFile));
			int setCount = result.length;
			if(this.containHeader)
				outputStream.writeInt(setCount);
			
//			outputStream.writeInt(this.universeSize);
			int resultIndex = 0;
			CoverSet_FeatureWrapper oneFeature = null;
			while((oneFeature = this.nextSet())!=null && resultIndex < result.length){
				if(oneFeature.getFeatureId() < result[resultIndex])
					continue;
				else if(oneFeature.getFeatureId() == result[resultIndex]){
					outputStream.writeObject(oneFeature);
					resultIndex++;
				}
				else System.out.println("Error in Input_FileStream: storeSelected");
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

	@Override
	public int getSetCount() {
		return this.setCount;
	}

//	@Override
//	public int getUniverseCount() {
//		return this.universeSize;
//	}
}
