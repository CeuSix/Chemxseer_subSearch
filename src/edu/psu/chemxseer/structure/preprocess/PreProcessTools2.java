package edu.psu.chemxseer.structure.preprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.parmol.graph.Graph;
import de.parmol.graph.MutableGraph;
import edu.psu.chemxseer.structure.iso.CanonicalDFS;


public class PreProcessTools2 {
	
	/**
	 * Given Xifengyan's format sample file, parse it to connectivity representation
	 * Construct a graphs
	 * then use smiles to serialize it
	 * @author dayuyuan
	 * @param args
	 * @throws IOException
	 */
	public static void convertToSmiles(String inputFileName, String outputFileName) throws IOException{
		BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
		String aline = null;
		int count = 0;
		HashMap<Integer, Integer> mapping = new HashMap();
		boolean firstLine = true;
		while((aline = reader.readLine())!=null){
			String[] tokens = aline.split(" ");
			if(tokens[0].equals("t")){
				if(firstLine)
					writer.write(count + " => ");
				if(!firstLine){
					writer.write("\n" + count + " => ");
					count++;
				}
				 mapping.clear();
			}
			else if(tokens[0].equals("v")){
				mapping.put(Integer.parseInt(tokens[1]), Integer.parseInt(tokens[2]));
			}
			else if(tokens[0].equals("e")){
				int node1label = mapping.get(Integer.parseInt(tokens[1]));
				int node2label = mapping.get(Integer.parseInt(tokens[2]));
				StringBuffer sb = new StringBuffer();
				sb.append('<');
				sb.append(tokens[1]);sb.append(' ');
				sb.append(tokens[2]);sb.append(' ');
				sb.append(node1label);sb.append(' ');
				sb.append(tokens[3]);sb.append(' ');
				sb.append(node2label);sb.append(' ');
				sb.append('>');
				writer.write(sb.toString());
			}
			firstLine = false;
		}
		writer.write("\n");
		count++;
		reader.close();
		writer.close();
		BufferedWriter metaWriter = new BufferedWriter(new FileWriter(outputFileName + "_Meta"));
		// 1. Processing Date
		SimpleDateFormat bartDateFormat = new SimpleDateFormat("EEEE-MMMM-dd-yyyy"); 
		Date date = new Date(); 
		metaWriter.write(bartDateFormat.format(date));
		metaWriter.newLine();
		// 2. Number of graphs in this file
		metaWriter.write("Number of Graphs:" + count);
		// Close meta data file
		try {
			metaWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Given Xifeng Yan format to STD
	 * @author dayuyuan
	 * @param args
	 * @throws IOException
	 */
	public static void covertToSdtGraph(String inputFileName, String outputFileName) throws IOException{
		CanonicalDFS coder = MyFactory.getDFSCoder();
		
		BufferedReader reader = new BufferedReader(new FileReader(inputFileName));
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputFileName));
		
		List<String> graphStrings = new ArrayList<String>();
		graphStrings.add(reader.readLine());
		String aLine = reader.readLine();
		int count = 0;
		while(aLine!=null){
			if(aLine.startsWith("t")){
				// serialize the previous read graphs
				Graph g = PreProcessTools2.parseGraph(graphStrings);
				writer.write(count++ + " => " + coder.serialize(g) + "\n");
				graphStrings.clear();
			}
			graphStrings.add(aLine);
			aLine = reader.readLine();
		}
		// last graph
		Graph g = PreProcessTools2.parseGraph(graphStrings);
		writer.write(count++ + " => " + coder.serialize(g) + "\n");
		graphStrings.clear();
		
		reader.close();
		writer.close();
		
		BufferedWriter metaWriter = new BufferedWriter(new FileWriter(outputFileName + "_Meta"));
		// 1. Processing Date
		SimpleDateFormat bartDateFormat = new SimpleDateFormat("EEEE-MMMM-dd-yyyy"); 
		Date date = new Date(); 
		metaWriter.write(bartDateFormat.format(date));
		metaWriter.newLine();
		// 2. Number of graphs in this file
		metaWriter.write("Number of Graphs:" + count);
		// Close meta data file
		try {
			metaWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/**
	 * Given a list of strings (representing a graph), return a in-memory graph
	 *  Input Form: 
	 * "t # N" means the Nth graph (N starts from 0),
	 * "v M L" means that the Mth vertex in this graph has label L (M and L start from 0),
	 * "e P Q L" means that there is an edge connecting the Pth vertex with the Qth vertex. 
	 * @param graphStrings
	 * @return
	 */
	private static Graph parseGraph(List<String> graphStrings){
		String firstline = graphStrings.get(0);
		if(!firstline.startsWith("t")){
			System.out.println("illegal graph strings");
			return null;
		}
		String[] tokens = firstline.split(" ");
		if(tokens.length < 3)
			return null;
		MutableGraph g = MyFactory.getGraphFactory().createGraph(tokens[2]);
		// nodeMap <string node ID, graph node ID>
		Map<String, Integer> nodeMap = new HashMap<String, Integer>();
		for(int i  =1; i< graphStrings.size(); i++){
			String line = graphStrings.get(i);
			tokens = line.split(" ");
			if(tokens[0].equals("v")){
				// add one node
				int nodeID = g.addNode(Integer.parseInt(tokens[2]));
				nodeMap.put(tokens[1], nodeID);
			}
			else if(tokens[0].equals("e")){
				Integer nodeID1 = nodeMap.get(tokens[1]);
				Integer nodeID2 = nodeMap.get(tokens[2]);
				if(nodeID1 == null || nodeID2 == null){
					System.out.println("error in parsing graphs");
					return null;
				}
				else g.addEdge(nodeID1, nodeID2, Integer.parseInt(tokens[3]));
			}
		}
		return g;
	}
}
