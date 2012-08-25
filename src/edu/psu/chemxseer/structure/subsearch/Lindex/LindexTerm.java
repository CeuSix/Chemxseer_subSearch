package edu.psu.chemxseer.structure.subsearch.Lindex;

import java.util.ArrayList;

import edu.psu.chemxseer.structure.iterative.QueryInfo;

/**
 * The class for a LindexTerm
 * @author dayuyuan
 *
 */
public class LindexTerm {
	private int m_id; 
	private int frequency;
	
	private LindexTerm[] m_children;
	private LindexTerm t_parent;// only one parent per IndexTerm, minimum spanning tree parent
	private int[][] extension; // the extension of this index Term based on it t_parent
	
	
	public long getMemoryConsumption() {
		// m_id, m_shift
		long memorySize = 12;
		// m_children
		if(m_children!=null)
		memorySize += m_children.length << 3;
		// t_parent
		if(t_parent!=null);
		memorySize +=8;
		// extension
		if(extension!=null)
		for(int i = 0; i< extension.length; i++)
			memorySize += extension[i].length << 2;
		return memorySize;
	}
	
	/**
	 * 
	 * @param id
	 * @param indexString
	 * @param frequency2
	 */
	public LindexTerm(int id, int frequency2){
		this.m_id = id;
		this.frequency = frequency2;
	}
	/**
	 * 
	 * @param label
	 * @param id
	 * @param indexString
	 * @param frequency2
	 */
	public LindexTerm(int[][] label, int id, int frequency2) {
		this.extension = label;
		this.m_id = id;
		this.frequency = frequency2;
	}
	
	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public int getId() {
		return m_id;
	}
	public void setId(int m_id) {
		this.m_id = m_id;
	}
	public LindexTerm[] getChildren() {
		if(this.m_children == null)
			return new LindexTerm[0];
		else return m_children;
	}
	public void setChildren(LindexTerm[] m_children) {
		this.m_children = m_children;
	}
	public LindexTerm getParent() {
		return t_parent;
	}
	public int[][] getExtension() {
		return extension;
	}
	public int[][] getLabel(){
		return extension;
	}
	public void setLabel(int[][] graphLabel){
		this.extension = graphLabel;
	}
	public void setParent(LindexTerm parent) {
		this.t_parent = parent;
	}
	
//	public void addChildren(LindexTerm it){
//		if(this.m_children == null){
//			this.m_children = new LindexTerm[1];
//			this.m_children[0] = it;
//			return;
//		}
//		// first test if this children is existed
//		for( int i = 0; i< this.m_children.length; i++){
//			if(this.m_children[i].getId()==it.getId())
//				return;
//		}
//		LindexTerm[] newChildren = new LindexTerm[this.m_children.length + 1];
//		for( int i = 0; i< this.m_children.length; i++){
//			newChildren[i] = this.m_children[i];
//		}
//		newChildren[newChildren.length-1] = it; 
//		this.setChildren(newChildren);
//	}
	/**
	 * add the LindexTerm it as the new term of the this LindexTerm
	 * If the it.children = this.children, then remove this.children
	 * @param it
	 * @param childrens
	 */
	public void addChildren(LindexTerm it){
		if(this.m_children == null){
			this.m_children = new LindexTerm[1];
			this.m_children[0] = it;
			return;
		}
		// First test: if this children is existed
		int count = 0;
		if(it.m_children!=null && it.m_children.length!=0){
			for( int i = 0; i< this.m_children.length; i++){
				boolean hit = false;
				for (int j = 0; j< it.m_children.length; j++)
					if(this.m_children[i] == it.m_children[j]){
						hit = true;
						break;
					}
				if(hit){
					this.m_children[i] = null;
				}
				else count++;			
			}
		}
		else count = this.m_children.length;
			
		LindexTerm[] newChildren = new LindexTerm[count + 1];
		for( int i = 0, j = 0; i< this.m_children.length; i++){
			if(this.m_children[i]!=null)
				newChildren[j++] = this.m_children[i];
		}
		newChildren[count] = it; 
		this.setChildren(newChildren);
	}
	
	public int getMaxNodeIndex(){
		int maxNodeIndex = Integer.MIN_VALUE;
		for(int i = 0; i< extension.length; i++)
		{
			if(extension[i][0] < maxNodeIndex)
				maxNodeIndex = extension[i][0];
			if(extension[i][1] < maxNodeIndex)
				maxNodeIndex = extension[i][1];
		}
		return maxNodeIndex;
	}
	/**
	 * DFSCode[Extension]=>Index=>postingFileShift
	 * => childrenIndex 1,2,3 => tParentIndex
	 * @param ithTerm
	 * @return
	 */
	public String toString(LindexTerm dummyHead){
		StringBuffer buf = new StringBuffer(1024);
		// label or extension of this code
		int[][] label = this.extension;
		for(int i = 0; i< label.length; i++){
			buf.append('<');
			buf.append(label[i][0]);
			buf.append(',');
			buf.append(label[i][1]);
			buf.append(',');
			buf.append(label[i][2]);
			buf.append(',');
			buf.append(label[i][3]);
			buf.append(',');
			buf.append(label[i][4]);
			buf.append('>');
		}
		buf.append(" => ");
		// Index
		buf.append(this.m_id);
		buf.append(" => ");
		buf.append(this.m_id);
		buf.append(" => ");
		buf.append(this.frequency);
		buf.append(" => ");
		// Add children
		LindexTerm[] c = this.m_children;
		if(c != null){
			if(c.length > 0)
			buf.append(c[0].getId());
			// else do nothing
			if(c.length >1)
				for(int j = 1; j<c.length; j++ )
				{
					buf.append(",");
					buf.append(c[j].getId());
				}
		}
		// Add tparent
		if(this.t_parent!=null && this.t_parent!=dummyHead){
			buf.append(" => ");
			buf.append(this.t_parent.m_id);
		}
		buf.append('\n');
		return buf.toString();
	}

}
