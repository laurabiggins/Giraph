package uk.ac.babraham.giraph.DataParser;

/** 
 * For parsing custom background genes that have been pasted in.
 */

import uk.ac.babraham.giraph.DataTypes.GeneCollection;

public class CustomBackgroundGeneParser extends GeneNameParser{
	
	public CustomBackgroundGeneParser(String listOfGeneNames, GeneCollection genomicBackgroundGenes) {
		
		super(listOfGeneNames, genomicBackgroundGenes);		
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
	protected void genesImported(){
		ol.customBackgroundGenesImported();

	//	else return (" ");
	}
}
