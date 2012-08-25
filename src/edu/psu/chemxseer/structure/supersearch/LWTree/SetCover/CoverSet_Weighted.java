package edu.psu.chemxseer.structure.supersearch.LWTree.SetCover;

public class CoverSet_Weighted {
	private int[] itemID;
	private float[] itemScore;
	
	public CoverSet_Weighted(int[] itemID, float [] itemScore){
		this.itemID = itemID;
		this.itemScore = itemScore;
	}
	
	public CoverSet_Weighted(int[] itemID){
		this.itemID = itemID;
		this.itemScore = new float[itemID.length];
		for(int i = 0; i< itemScore.length; i++)
			itemScore[i] = 0;
	}
	
	/**
	 * Assign the score to the item with itemID
	 * @param itemID
	 * @param score
	 */
	public boolean assignScore(int ID, float score) {
		if(ID < 0 || ID >= itemID.length)
			return false;
		else {
			itemScore[ID] = score;
			return true;
		}
	}
	
	/**
	 * Get the ID with "ID"
	 * @param ID
	 * @return
	 */
	public int getItem(int ID){
		if(ID < 0 || ID >= itemID.length)
			return -1;
		else return itemID[ID];
	}
	
	/**
	 * Get the Score of the item with "ID"
	 * @param ID
	 * @return
	 */
	public float getScore(int ID){
		if(ID < 0 || ID >= itemID.length)
			return -1;
		else return itemScore[ID];
	}
	
	public int size(){
		return this.itemID.length;
	}
	
	
}
