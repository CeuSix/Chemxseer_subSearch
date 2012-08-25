package edu.psu.chemxseer.structure.subsearch.Impl;


import de.parmol.parsers.GraphParser;
import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.subsearch.Impl.indexfeature.NoPostingFeatures;
import edu.psu.chemxseer.structure.subsearch.Interfaces.GraphDatabase;

public class GraphDatabase_InMem extends GraphDatabase{
	private String[] graphString;
	
	public GraphDatabase_InMem(String[] gString, GraphParser gParser){
		super(gParser);
		this.graphString = gString;
	}
	
	public GraphDatabase_InMem(NoPostingFeatures features){
		super(MyFactory.getDFSCoder());
		this.graphString = new String[features.getfeatureNum()];
		for(int i = 0; i< graphString.length; i++)
			graphString[i] = features.getFeature(i).getDFSCode();
	}
//
//	@Override
//	public Graph[] loadGraphs(int startNum, int endNum) {
//		if(startNum < 0 || endNum > graphString.length || startNum >= endNum){
//			System.out.println("Error: Illigal input");
//			return null;
//		}
//		Graph[] result= new Graph[endNum-startNum];
//		for(int i = startNum; i< endNum; i++ )
//			try {
//				result[i-startNum] = gParser.parse(graphString[i], MyFactory.getGraphFactory());
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		return result;
//	}

	@Override
	public String findGraphString(int id) {
		if(id < 0 || id >= graphString.length){
			System.out.println("Error: Illligal graphID");
			return null;
		}
		else return this.graphString[id];
	}

	@Override
	public int getTotalNum() {
		return this.graphString.length;
	}

}
