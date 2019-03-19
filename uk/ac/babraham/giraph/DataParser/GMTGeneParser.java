package uk.ac.babraham.giraph.DataParser;

import uk.ac.babraham.giraph.giraphException;
import uk.ac.babraham.giraph.DataTypes.GeneCollection;

public class GMTGeneParser extends GMTParser{

	public GMTGeneParser(String file) {

		super(file);
		allGMTgenes = new GeneCollection();
	}

	protected void parseLine(String [] result) throws giraphException {	
		
		parseGenes(result);
	
	}
	
	public void notifyGMTFileParsed() {
		ol.genomicBackgroundGenesImported();		
	}
	
}
