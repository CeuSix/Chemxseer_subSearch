package edu.psu.chemxseer.structure.setcover.maxCoverStatus;

public class Util_IntersectionSet {
	private Util_IntersectionSet(){
		//dummy constructor to make sure that MaxCoverStatus_IntersectionSet is not constructable
	}
	
	/**
	 * Prerequisite: firstArray & secondArray all contain no-negative number
	 * Return -1, if fistArray-secondArray = empty set
	 * Return -2, if firstArray-secondArray = set with size >1
	 * Return n, if firstArray-secondArray = n. 
	 * @param firstArray
	 * @param seconArray
	 */
	public static int retain(short[] firstArray, int firstArrayBoundary, 
			short[] secondArray, int secondArrayBoundary, int exceptItemI){
		int result = -1;
		if(firstArrayBoundary == 0)
			return -1; //firstArray is empty
		
		// firstArray is not empty or null
		int i = 0, j = 0;
		while( i < firstArrayBoundary && j < secondArrayBoundary){
			if(firstArray[i] == secondArray[j]){
				i ++; j++;
			}
			// firstArray[i] not in secondArray
			else if(firstArray[i] < secondArray[j]){
				if(firstArray[i] == exceptItemI){
					i++; // continue;
				}
				else{
					if(result!=-1)
						return -2; //no single result
					else result = firstArray[i];
					i++;
				}
			}
			else //firstArray[i] > secondArray[j]
				j++;
		}
		for(; i < firstArrayBoundary; i++){
			if(firstArray[i] == exceptItemI)
				continue;
			if(result!=-1)
				return -2; // no single result;
			else result = firstArray[i];
		}
		
		return result;
	}

	/**
	 * Return A-B
	 * @param A
	 * @param B
	 */
	public static short[] retain(short[] A, short[] B) {
		if(B == null || B.length == 0)
			return A;
		else if(A == null)
			return A;
		int iter = 0, i = 0, j = 0;
		short[] result = new short[A.length];
		// i is index on item, j is index on c
		while(i < A.length && j < B.length){
			if(A[i] > B[j])
				j++;
			else if(A[i]== B[j]){
				result[iter++]=B[j];
				j++;
				i++;
				continue;
			}
			else {// items[i] < c[j]
				i++;
				continue;
			}
		}
		short[] finalResult = new short[iter];
		for(int w = 0; w < iter; w++)
			finalResult[w] = result[w];
		return finalResult;
	}
}
