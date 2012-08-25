package edu.psu.chemxseer.structure.experiment;

import java.io.IOException;
import java.text.ParseException;

import org.apache.commons.math.MathException;

import de.parmol.parsers.GraphParser;
import edu.psu.chemxseer.structure.preprocess.MyFactory;
import edu.psu.chemxseer.structure.query.InFrequentQueryGenerater;
import edu.psu.chemxseer.structure.subsearch.Interfaces.IGraphs;
import edu.psu.chemxseer.structure.subsearch.Interfaces.SubgraphSearch;

public class VariousEdge {
//	public static void main(String[] args) throws IOException, ParseException{
//		for(int i = 30; i < 60; i = i+10){
//			String baseName = "/data/santa/VLDBJExp/VaringEdge/" + i + "/";
//			String dbFileName = baseName + "DBFile";
//			GraphParser dbParser = MyFactory.getSmilesParser();
//			BasicExpBuilder builder = new BasicExpBuilder(dbFileName, dbParser, baseName);
//			builder.buildGIndexDF();
//			builder.buildLindexDF();
//			builder.buildFGindex();
//			builder.buildLindexAdvTCFG();
//			
//			
//			BasicExpRunner runner = new BasicExpRunner(dbFileName, dbParser, baseName);
//			
//			try {
//				runner.genQueries(false);
//			} catch (MathException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
////			String queryFile = baseName + "Queries/QueryInf";
////			InFrequentQueryGenerater qGen = new InFrequentQueryGenerater(queryFile);
////			IGraphs[] queries = qGen.loadInfrequentQueries();
////			
////			SubgraphSearch searcher = null;
////			System.out.println("DF");
////			searcher = runner.loadIndex(1);
////			runner.runExp(queries, searcher);
////			searcher = runner.loadIndex(2);
////			runner.runExp(queries, searcher);
////			
////			System.out.println("TCFG");
////			searcher = runner.loadIndex(6);
////			runner.runExp(queries, searcher);
////			searcher = runner.loadIndex(7);
////			runner.runExp(queries, searcher);
//		}
//	}
}