package uk.ac.babraham.giraph.DataParser;

import java.util.Enumeration;

/** 
 * For parsing custom background genes that have been pasted in.
 */

import uk.ac.babraham.giraph.DataTypes.GeneCollection;

public class CustomBackgroundGeneParser extends GeneNameParser{
	
	public CustomBackgroundGeneParser(String listOfGeneNames, GeneCollection genomicBackgroundGenes) {
		
		super(listOfGeneNames, genomicBackgroundGenes);		
	}
	

	protected void progressComplete (Object o) {
		Enumeration<ProgressListener>en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().progressComplete("custom_background_parser", o);;
		}
	}
	
	
	//private String listOfGeneNames;
	
	/*public CustomBackgroundGeneParser(String listOfGeneNames) {
		
		super(listOfGeneNames);

	}
	
/*	public void parseGenes() {

	protected void genesImported(){
		ol.customBackgroundGenesImported();
	}
	
	protected String unmatchedCountMessage(int unmatchedCount){

		
		if(unmatchedCount > 0){
		
			return (unmatchedCount + " custom background genes were not found in the genomic background list, ");
		}

		else {
			String [] geneNames = splitList(listOfGeneNames);
			System.err.println("number of genes parsed = " + geneNames.length);
			
			for (int i = 0; i < geneNames.length; i++){
				
				Gene g = new Gene(cleanGene(geneNames[i]));
			
				backgroundGenes.addGene(g);
			}
			genesImported();
		}	
	}
*/	

}
