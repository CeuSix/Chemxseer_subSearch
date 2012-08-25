package edu.psu.chemxseer.structure.util;

import java.util.ArrayList;

import edu.psu.chemxseer.structure.newISO.newFastSU.Embedding;

public class PriorityQueueSelfImp <T extends Comparable<T> & HashID> {
	private ArrayList<T> heap;
	private boolean minHeap;
	
	// Constructor
	public PriorityQueueSelfImp (boolean minHeap){
		this.heap = new ArrayList<T>();
		this.minHeap = minHeap;
	}
	public int getSize(){
		return heap.size();
	}
	public T getMin(){
		if(minHeap)
			return (T) heap.get(0);
		else{
			System.out.println("Error: can not return min value from a max heap");
			return null;
		}
	}
	public void clear(){
		for(T entry: heap)
			entry.setID(-1);
		this.heap.clear();
	}

	public boolean isEmpty(){
		return this.heap.isEmpty();
	}
	
	private int leftChild(int pos){
		return 2*pos+1;
	}
	private int rightChild(int pos){
		return 2*pos +2;
	}
	private int parent(int pos){
		return (pos-1)/2;
	}
	
	private boolean isleaf(int pos){
		if(pos >= (heap.size()-1)/2 && pos < heap.size())
			return true;
		else return false;
	}
	
	private void swap(int pos1, int pos2){
		if(pos1!=heap.get(pos1).getID() || pos2!=heap.get(pos2).getID())
			System.out.println("Error in Swap of PriorityQueueSelfImpl");
		T temp= heap.get(pos1);
		heap.set(pos1, heap.get(pos2));
		heap.set(pos2, temp);
		heap.get(pos1).setID(pos1);
		heap.get(pos2).setID(pos2);
	}
	
	public void add(T entry){
		entry.setID(heap.size());
		heap.add(entry);
		int currentPosition = heap.size()-1;
		if(minHeap){
			while(currentPosition!=0 && heap.get(currentPosition).compareTo(heap.get(parent(currentPosition)))==-1){
				swap(currentPosition, parent(currentPosition));
				currentPosition = parent(currentPosition);
			}
		}
		else{
			while(currentPosition!=0 && heap.get(currentPosition).compareTo(heap.get(parent(currentPosition)))==1){
				swap(currentPosition, parent(currentPosition));
				currentPosition = parent(currentPosition);
			}
		}
	}
	/**
	 * Remove the Entry 
	 * Return false if Entry is not in the heap
	 * @param entry
	 * @return
	 */
	public boolean remove(T entry) {
		int pos = entry.getID();
		if(pos < 0 || pos >= this.getSize())
			return false;
		if(entry!=this.heap.get(pos))
			return false;
		// Real Remove
		entry.setID(-1); // not in the set
		if(pos == this.getSize()-1){ // the last node, just remove
			this.heap.remove(pos);
		}
		else{
			int s = this.getSize()-1;
			T moved =  heap.get(s); // last item
            heap.remove(s); // remove the last;
            heap.add(pos, moved);
            moved.setID(pos);
            pushdown(pos);
            if (heap.get(pos) == moved) {
                pushup(pos);
            }
		}
		return true;
	}
	
	public void print(){
		for(T oneT: this.heap)
			System.out.println(oneT.toString());
	}
	
	public T deleteMin(){
		if(this.minHeap)
			return deleteTop();
		else {
			System.out.println("Error in delete min from the max heap");
			return null;
		}
	}
	public T deleteMax(){
		if(this.minHeap){
			System.out.println("Error in delete max from the min heap");
			return null;
		}
		else return deleteTop();
	}
	private T deleteTop(){
		int lastOneIndex = heap.size()-1;
		swap(0, lastOneIndex);
		T result = this.heap.remove(lastOneIndex);
		result.setID(-1);
		lastOneIndex--;
		if(lastOneIndex!=0) // at least two items
			pushdown(0);
		return result;
	}
	
	private void pushdown(int position){
		if(minHeap){
			int smallestchild;
			while(!isleaf(position)){
				smallestchild = leftChild(position);
				int rightChild = rightChild(position);
				if(rightChild <  heap.size() && heap.get(smallestchild).compareTo(heap.get(rightChild)) ==1)
					smallestchild = rightChild;
				if(heap.get(position).compareTo(heap.get(smallestchild))==1){
					swap(position, smallestchild);
					position  = smallestchild;
				}
				else break;
			}
		}
		else{
			if(!minHeap){
				int maxChild;
				while(!isleaf(position)){
					maxChild = leftChild(position);
					int rightChild = rightChild(position);
					if(rightChild <  heap.size() && heap.get(maxChild).compareTo(heap.get(rightChild)) ==-1)
						maxChild = rightChild;
					if(heap.get(position).compareTo(heap.get(maxChild))==-1){
						swap(position, maxChild);
						position  = maxChild;
					}
					else break;
				}
			}
		}
	}
	
	private void pushup(int position){
		while(position!=1){
			int parent = parent(position);
			if(minHeap && heap.get(position).compareTo(heap.get(parent))==-1){
				swap(position, parent);
				position = parent;
			}
			else if(!minHeap && heap.get(position).compareTo(heap.get(parent))== 1){
				swap(position, parent);
				position = parent;
			}
			else break;
		}
	}
	
	public boolean changeKey(T oneT, int keyChange){
		T theT = this.heap.get(oneT.getID());
		if(!theT.equals(oneT)){
			System.out.println("Error in Change Key, the ID does not match");
			return false;
		}
		else{
			if(minHeap && keyChange > 0){
				pushdown(theT.getID());
			}
			else if (minHeap && keyChange < 0)
				pushup(theT.getID());
			else if(!minHeap && keyChange > 0)
				pushup(theT.getID());
			else if(!minHeap && keyChange < 0)
				pushdown(theT.getID());
			return true;
		}
	}
	public boolean contains(T entry) {
		int pos = entry.getID();
		if(pos < 0 || pos >= this.getSize())
			return false;
		else{
			if(entry!= this.heap.get(pos))
				return false;
			else return true;
		}
	}

	
}
