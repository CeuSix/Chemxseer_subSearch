package edu.psu.chemxseer.structure.supersearch.Impl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class PostingBuilderMem {
	protected ArrayList<int[]> postings;
	protected Map<Integer, Integer> nameConverter;
	
	//Dummy Constructor
	public PostingBuilderMem(){
		this.postings = new ArrayList<int[]>();
		this.nameConverter = new HashMap<Integer, Integer>();
	}
	/**
	 * Load the Postings From the Disk
	 * @param fileName
	 */
	public PostingBuilderMem(String fileName) {
		try {
			this.loadPostings(fileName);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * Copy Constructor
	 * @param postingBuilder
	 */
	public PostingBuilderMem(PostingBuilderMem postingBuilder) {
		this.postings = postingBuilder.postings;
		this.nameConverter = postingBuilder.nameConverter;
	}
	
	
	private void loadPostings(String fileName) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(new File(fileName)));
		String aLine = reader.readLine();
		int size = Integer.parseInt(aLine);
		this.postings = new ArrayList<int[]> (size);
		this.nameConverter = new HashMap<Integer, Integer>(size);
		for(int i = 0; i< size; i++){
			aLine = reader.readLine();
			String[] tokens = aLine.split(",");
			int[] posting = new int[tokens.length];
			for(int w = 0; w < posting.length; w++)
				posting[w] = Integer.parseInt(tokens[w]);
			this.postings.add(posting);
		}
		for(int i = 0; i < size; i++){
			aLine = reader.readLine();
			String[] tokens = aLine.split(",");
			Integer key = Integer.parseInt(tokens[0]);
			Integer value = Integer.parseInt(tokens[1]);
			this.nameConverter.put(key, value);
		}
	}
	
	/**
	 * Save the Positngs
	 * @param fileName
	 * @throws IOException
	 */
	public void savePosting(String fileName) throws IOException{
		BufferedWriter writer = new BufferedWriter(new FileWriter(new File(fileName)));
		writer.write(this.postings.size() + "\n");
		for(int[] posting: postings){
			StringBuffer sbuf = new StringBuffer();
			for(int i : posting){
				sbuf.append(i);
				sbuf.append(',');
			}
			sbuf.deleteCharAt(sbuf.length()-1);
			sbuf.append('\n');
			writer.write(sbuf.toString());
		}
		for(Entry<Integer, Integer> entry : this.nameConverter.entrySet()){
			writer.write(entry.getKey().toString() + "," + entry.getKey() + "\n");
		}
		writer.close();
	}
	/**
	 * Insert the postings for the feature: featureID
	 * @param featureID
	 * @param postings
	 */
	public void insertPosting(Integer featureID, int[] postings){
		if(this.nameConverter.containsKey(featureID)){
			System.out.println("Error in Insert Postings: This feature exists");
			return;
		}
		else{
			this.nameConverter.put(featureID, this.postings.size());
			this.postings.add(postings);
		}
	}
}
