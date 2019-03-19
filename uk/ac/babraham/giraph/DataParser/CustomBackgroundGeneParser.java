package uk.ac.babraham.giraph.DataParser;

import javax.swing.JOptionPane;

import uk.ac.babraham.giraph.giraphApplication;
import uk.ac.babraham.giraph.DataTypes.Gene;

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
		
		if(splitList(listOfGeneNames) == null){
			String msg =  ("Something went wrong when trying to parse the genes.");
			System.err.println(msg);
			JOptionPane.showMessageDialog(giraphApplication.getInstance(), msg, "couldn't parse genes", JOptionPane.ERROR_MESSAGE);
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
	}
	
}
