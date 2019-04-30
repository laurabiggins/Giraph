package uk.ac.babraham.giraph.DataParser;

import java.util.Enumeration;

import uk.ac.babraham.giraph.giraphException;
import uk.ac.babraham.giraph.DataTypes.GeneCollection;

public class GMTGeneParser extends GMTParser{

	public GMTGeneParser(String file) {

		super(file);
		allGMTgenes = new GeneCollection();
	}

	public void parseLine(String [] result, int linenumber) throws giraphException{// throws giraphException {	
		//System.err.println("we're using this");
		parseGenes(result, linenumber);
	
	}
	
	protected void progressComplete (Object o) {
		Enumeration<ProgressListener>en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().progressComplete("gmt_gene_parser", o);;
		}
	}

	
}
