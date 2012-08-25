package edu.psu.chemxseer.structure.util;

import java.util.HashSet;
import java.util.List;

// One implementation of set mainly used for fast intersection
// Assumption, all items in IntersectionSet is sorted
public class IntersectionSet{
	private int[] items;
	private int capacity;
	private int size;
	/**
	 * Given two arrays: arrayOne and arrayTwo, and assume that these two arrays are sorted
	 * Find out what is the intersection set size for the two arrays
	 * @param arrayOne
	 * @param arrayTwo
	 */
	public static int getInterSectionSize(int[] arrayOne, int[] arrayTwo){
		if(arrayOne == null || arrayTwo == null)
			return 0;
		else{
			int iter = 0, i = 0, j = 0;
			// i is index on item, j is index on c
			while(i < arrayOne.length && j < arrayTwo.length){
				if(arrayOne[i] > arrayTwo[j])
					j++;
				else if(arrayOne[i]== arrayTwo[j]){
					j++;
					i++;
					iter ++;
				}
				else {// items[i] < c[j]
					i++;
					continue;
				}
			}
			return iter;
		}
	}
	
	/**
	 * Given two array, firstArray & second array and their boundary
	 * Return the position of the item on firstArray = item on second array
	 * @param firstArray
	 * @param start1
	 * @param end1
	 * @param secondArray
	 * @param start2
	 * @param end2
	 * @return
	 */
	public static int[] getInterSectionPosition(int[] firstArray, int start1, int end1,
			int[] secondArray, int start2, int end2) {
		if(firstArray == null || secondArray == null)
			return new int[0];
		int[] pos = new int[end1-start1];
		int iter = 0, i = start1, j = start2;
		// i is index on item, j is index on c
		while(i < end1 && j < end2){
			if(firstArray[i] > secondArray[j])
				j++;
			else if(firstArray[i]== secondArray[j]){
				pos[iter++] = i;
				j++;
				i++;
			}
			else {// items[i] < c[j]
				i++;
				continue;
			}
		}
		int[] result = new int[iter];
		for(int w = 0; w< iter; w++)
			result[w] = pos[w];
		return result;
		
	}
	
	public IntersectionSet(){
		capacity = 0;
		size = 0;
	}
	/**
	 * Remove all index terms that are larger than size
	 * @param numOfSets
	 * @return
	 */
	public void removeLarge(int index){
		this.size = index+1;
	}
	public boolean addAll(int[] c){
		if(c == null|| c.length == 0)
			return false;
		int[] newItems;

		capacity = c.length+size;
		newItems = new int[capacity];
		int iter = 0;
		int i = 0; 
		int j = 0;
		while(i < size){
			while(j < c.length){
				if(items[i]< c[j]){
					newItems[iter]=items[i];
					iter++;
					i++;
					break;
					}
				else if(items[i]==c[j])
					j++;
				else{
					newItems[iter]=c[j];
					iter++;
					j++;
					continue;}
			}
			if(j == c.length)
				break;
		}
		while(i < size){
			newItems[iter] = items[i];
			iter++;
			i++;
		}
		while(j < c.length){
			newItems[iter]=c[j];
			iter++;
			j++;
		}
		items = newItems;
		size = iter;
		return true;
	}
	
	/**
	 * 
	 * @param c
	 * @param fromIndex : inclusive
	 * @param toIndex : exclusive
	 * @return
	 */
	public boolean addAll(int[] c, int fromIndex, int toIndex){
		if(c == null|| c.length == 0 || toIndex-fromIndex <=0)
			return false;
		int[] newItems;

		capacity = toIndex-fromIndex+size;
		newItems = new int[capacity];
		int iter = 0;
		int i = 0; 
		int j = fromIndex;
		while(i < size){
			while(j < toIndex){
				if(items[i]< c[j]){
					newItems[iter]=items[i];
					iter++;
					i++;
					break;
					}
				else if(items[i]==c[j])
					j++;
				else{
					newItems[iter]=c[j];
					iter++;
					j++;
					continue;}
			}
			if(j == toIndex)
				break;
		}
		while(i < size){
			newItems[iter] = items[i];
			iter++;
			i++;
		}
		while(j < toIndex){
			newItems[iter]=c[j];
			iter++;
			j++;
		}
		items = newItems;
		size = iter;
		return true;
	}
	
	public boolean addAll(List<Integer> c){
		if(c == null|| c.size() == 0)
			return false;
		int[] newItems;

		capacity = c.size()+size;
		newItems = new int[capacity];
		int iter = 0;
		int i = 0; 
		int j = 0;
		while(i < size){
			while(j < c.size()){
				if(items[i]< c.get(j)){
					newItems[iter]=items[i];
					iter++;
					i++;
					break;
					}
				else if(items[i]==c.get(j))
					j++;
				else{
					newItems[iter]=c.get(j);
					iter++;
					j++;
					continue;}
			}
			if(j == c.size())
				break;
		}
		while(i < size){
			newItems[iter] = items[i];
			iter++;
			i++;
		}
		while(j < c.size()){
			newItems[iter]=c.get(j);
			iter++;
			j++;
		}
		items = newItems;
		size = iter;
		return true;
	}
	public boolean retainAll(int[] c){
		if(c == null || c.length == 0)
			return false;
		int iter = 0, i = 0, j = 0;
		// i is index on item, j is index on c
		while(i < size && j < c.length){
			if(items[i] > c[j])
				j++;
			else if(items[i]== c[j]){
				items[iter++]=c[j];
				j++;
				i++;
				continue;
			}
			else {// items[i] < c[j]
				i++;
				continue;
			}
		}
		size = iter;
//		//TEST
//		HashSet<Integer> tests = new HashSet<Integer>();
//		for(int w = 0; w < size; w++){
//			if(tests.contains(items[w]))
//				System.out.println("waht");
//			else tests.add(items[w]);		
//		}
//		//END OF TEST
		return true;
	}
	/**
	 * 
	 * @param c
	 * @param fromIndex: inclusive
	 * @param toIndex: exclusive
	 * @return
	 */
	public boolean retainAll(int[] c, int fromIndex, int toIndex){
		if(c == null || c.length == 0 || toIndex-fromIndex <=0)
			return false;
		int iter = 0, i = 0, j = fromIndex;
		// i is index on item, j is index on c
		while(i < size && j < toIndex){
			if(items[i] > c[j])
				j++;
			else if(items[i]== c[j]){
				items[iter++]=c[j];
				j++;
				i++;
				continue;
			}
			else {// items[i] < c[j]
				i++;
				continue;
			}
		}
		size = iter;
		return true;
	}
	
	public boolean retainAll(List<Integer> c){
		if(c == null || c.size() == 0)
			return false;
		int iter = 0, i = 0, j = 0;
		// i is index on item, j is index on c
		while(i < size && j < c.size()){
			if(items[i] > c.get(j))
				j++;
			else if(items[i]== c.get(j)){
				items[iter++]=c.get(j);
				j++;
				i++;
				continue;
			}
			else {// items[i] < c[j]
				i++;
				continue;
			}
		}
		size = iter;
		return true;
	}
	public int size(){
		return size;
	}
	public boolean removeAll(int[] c){
		if(c == null || c.length == 0)
			return false;
		int iter = 0, i = 0, j = 0;
		while(i < size&&j < c.length){
			if(items[i] > c[j])
				j++;
			else if(items[i] == c[j]){
				i++;
				j++;
				// iter did not update
			}
			else{
				// items[i] < c[j]
				items[iter]=items[i];
				i++;
				iter++;
			}
		}
		while(i < size){
			items[iter]=items[i];
			i++;
			iter++;
		}
		size = iter;
		return true;
	}
	/**
	 * 
	 * @param c
	 * @param fromIndex: inclusive
	 * @param toIndex: exclusive
	 * @return
	 */
	public boolean removeAll(int[] c, int fromIndex, int toIndex){
		if(c == null || c.length == 0 || toIndex-fromIndex<=0)
			return false;
		int iter = 0, i = 0, j = fromIndex;
		while(i < size&&j < toIndex){
			if(items[i] > c[j])
				j++;
			else if(items[i] == c[j]){
				i++;
				j++;
				// iter did not update
			}
			else{
				// items[i] < c[j]
				items[iter]=items[i];
				i++;
				iter++;
			}
		}
		while(i < size){
			items[iter]=items[i];
			i++;
			iter++;
		}
		size = iter;
		return true;
	}
	//TODO: if capacity of the IntersectionSet always larger a lot than the size of items
	// We do a further optimization of memory by freeing items;
	public boolean clear(){
		size = 0;
		return true;
	}
	public int[] getItems(){
		int[] results = new int[size];
		for(int i = 0; i< size; i++)
			results[i]=items[i];
		return results;
	}
	
	public String toString(){
		StringBuffer buf = new StringBuffer(4*size);
		buf.append(items[0]);
		for(int i = 1; i < size; i++){
			buf.append(',');
			buf.append(items[i]);
		}
		return buf.toString();
	}
	public boolean print(){
		for(int i = 0; i< size; i++){
			System.out.print(items[i]);
			System.out.print(' ');
		}
		System.out.println();
		System.out.println("Size: " + size + " Capacity: " + capacity);
		return true;
	}
	public HashSet<Integer> toHashSet(){
		HashSet<Integer> results = new HashSet<Integer>();
		for(int i = 0; i< size; i++)
			results.add(items[i]);
		return results;
	}
	public static int[] getCompleteSet(int[] items, int wholeBound){
		int[] results = new int[wholeBound-items.length];
		int i = 0,resultsIndex = 0, itemsIndex = 0;
		for(; i< wholeBound & itemsIndex< items.length ; i++){
			if(i < items[itemsIndex])
				results[resultsIndex++] = i;
			else if(i == items[itemsIndex])
				itemsIndex++;
			else if(i > items[itemsIndex])
				System.out.println("Illigle Items: not sorted");
		}
		for(; i < wholeBound; i++, resultsIndex++)
			results[resultsIndex] = i;
		return results;
		
	}

	
}
