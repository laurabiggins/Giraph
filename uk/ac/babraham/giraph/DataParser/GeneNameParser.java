package uk.ac.babraham.giraph.DataParser;

/** 
 * This class parses the gene names that are entered, if the query gene is in the genomic background list it is saved in the GeneCollection genes.
 * 
 * The test for significance is carried out from the optionsFrame.
 * 
 */


import javax.swing.JOptionPane;

import uk.ac.babraham.giraph.giraphApplication;
import uk.ac.babraham.giraph.DataTypes.GeneCollection;

public class GeneNameParser implements Runnable{
	
	// The GeneCollection containing the parsed query/input genes
	private GeneCollection genes;
	
	// The background genes to check the query/input genes against
	private GeneCollection backgroundGenes; 
	
	// The query genes
	private String listOfGeneNames;
	
	protected OptionsListener ol;
	
	public GeneNameParser(String listOfGeneNames, GeneCollection backgroundGenes){
		
		this.listOfGeneNames = listOfGeneNames;
		this.backgroundGenes = backgroundGenes;
		this.genes = new GeneCollection();
		Thread t = new Thread(this);
		t.start();
	}

	public void run(){
		
		parseGenes();
	}
	
	public void parseGenes(){
		
		if(splitList(listOfGeneNames) == null){
			String msg =  ("Something went wrong when trying to parse the genes.");
			System.err.println(msg);
			//JOptionPane.showMessageDialog(giraphApplication.getInstance(), msg, "couldn't parse genes", JOptionPane.ERROR_MESSAGE);
		}
		else {
			String [] geneNames = splitList(listOfGeneNames);
			int unmatchedCount = 0;
			
			// check if they're in the background list
			for (int i = 0; i < geneNames.length; i++){
			
				if(backgroundGenes.contains(cleanGene(geneNames[i]))){
			
					genes.addGene(backgroundGenes.getGene(cleanGene(geneNames[i])));
				}	
				else{
					unmatchedCount++;
				}
			}
			
			String unmatchedCountMessage = unmatchedCountMessage(unmatchedCount);
			
			if((genes.getAllGenes().length > 3) && (genes.getAllGenes().length < backgroundGenes.getAllGenes().length)){
				System.out.println("imported " + genes.getAllGenes().length + " genes, the first one was "+ genes.getAllGenes()[0].getGeneSymbol()+
						", the last one was "+genes.getAllGenes()[genes.getAllGenes().length - 1].getGeneSymbol());
				
				genesImported();
			}
			else if(genes.getAllGenes().length <= 3){
				String msg = ("Fewer than 3 valid query genes were identified," + unmatchedCountMessage + "please enter more genes."); 
				JOptionPane.showMessageDialog(giraphApplication.getInstance(), msg, "couldn't parse genes", JOptionPane.ERROR_MESSAGE);
			}
			else if(genes.getAllGenes().length >= backgroundGenes.getAllGenes().length){
				String msg = ("The number of background genes does not exceed the number of query genes, please enter a different set of genes."); 
				JOptionPane.showMessageDialog(giraphApplication.getInstance(), msg, "couldn't parse genes", JOptionPane.ERROR_MESSAGE);				
			}			
		}		
	}
	
	protected String unmatchedCountMessage(int unmatchedCount){
		return (unmatchedCount + " ");
	}
	
	protected void genesImported(){
		
	}
	
	public GeneCollection geneCollection(){
		return genes;
	}
	
	private static String [] splitList(String listOfGeneNames){
		
		if(listOfGeneNames.contains("\n")){
			return(listOfGeneNames.split("\n"));
		}
		else if(listOfGeneNames.contains(",")){
			return(listOfGeneNames.split(","));
		}
		else if(listOfGeneNames.contains("\t")){
			return(listOfGeneNames.split("\t"));
		}
		else if(listOfGeneNames.contains(" ")){
			return(listOfGeneNames.split(" "));
		}
		else {
			String msg =  ("Unrecognised separating character, please separate the genes using tabs, commas or spaces");
			JOptionPane.showMessageDialog(giraphApplication.getInstance(), msg, "couldn't parse genes", JOptionPane.ERROR_MESSAGE);
			return null;
		}		
	}
	
	private String cleanGene(String str) {
		
		String str1 = str.replaceAll("\t", "");
		String str2 = str1.replaceAll("\\s+", "");
		String str3 = str2.replaceAll("\"", "");
		
		return str3.toUpperCase();
	}
	
	public void addOptionsListener(OptionsListener ol){
		
		this.ol = ol;
	}
				
}
