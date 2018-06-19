package uk.ac.babraham.giraph.DataParser;

/** 
 * for parsing the query genes
 */

import uk.ac.babraham.giraph.DataTypes.GeneCollection;

public class QueryGeneParser extends GeneNameParser {
		
	public QueryGeneParser(String listOfGeneNames, GeneCollection customBackgroundGenes) {
		
		super(listOfGeneNames, customBackgroundGenes);
		
	}
	
	protected void genesImported(){
		ol.queryGenesImported();
	}
	
	protected String unmatchedCountMessage(int unmatchedCount){
		
		if(unmatchedCount > 0){
		
			return (unmatchedCount + " query genes were not found in the background genes, ");
		}
		else return (" ");
	}
}
