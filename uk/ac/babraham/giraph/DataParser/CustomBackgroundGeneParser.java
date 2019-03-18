package uk.ac.babraham.giraph.DataParser;

/** 
 * For parsing custom background genes that have been pasted in.
 */

import uk.ac.babraham.giraph.DataTypes.GeneCollection;

public class CustomBackgroundGeneParser extends GeneNameParser{
	
	public CustomBackgroundGeneParser(String listOfGeneNames, GeneCollection genomicBackgroundGenes) {
		
		super(listOfGeneNames, genomicBackgroundGenes);		
	}
	
	protected void genesImported(){
		ol.customBackgroundGenesImported();
	}
	
	protected String unmatchedCountMessage(int unmatchedCount){
		
		if(unmatchedCount > 0){
		
			return (unmatchedCount + " custom background genes were not found in the genomic background list, ");
		}
		else return (" ");
	}
}
