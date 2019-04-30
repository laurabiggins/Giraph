package uk.ac.babraham.giraph.DataParser;

import java.util.Enumeration;

/** 
 * for parsing the query genes
 */

import uk.ac.babraham.giraph.DataTypes.GeneCollection;

public class QueryGeneParser extends GeneNameParser {
		
	public QueryGeneParser(String listOfGeneNames, GeneCollection customBackgroundGenes) {
		
		super(listOfGeneNames, customBackgroundGenes);
		
	}
	
/*	protected void genesImported(){
		ol.queryGenesImported();
	}
*/
	
	protected void progressComplete (Object o) {
		Enumeration<ProgressListener>en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().progressComplete("query_gene_parser", o);;
		}
	}
	
	protected String unmatchedCountMessage(int unmatchedCount){
		
		if(unmatchedCount > 0){
		
			return (unmatchedCount + " query genes were not found in the background genes, ");
		}
		else return (" ");
	}
}
