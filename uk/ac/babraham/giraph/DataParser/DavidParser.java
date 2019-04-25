package uk.ac.babraham.giraph.DataParser;

import java.io.File;

import javax.swing.JOptionPane;

import uk.ac.babraham.giraph.giraphApplication;

public class DavidParser extends ExternalResultsParser{
	
	
	public DavidParser(File file){
		
		super(file);
	}
	
	public void setColumnInfoForFile(){
		
		System.err.println("setting fields for david file");
		
		delimitersValue = "\t";
		geneDelimitersValue = ",";
		
		startRowValue = 1;
		queryGeneColValue = 5;
		categoryNameColValue = 1;
		categoryDescriptionColValue = 1;
		pValueColValue = 12;		
	}
	

	public String [] parseGenes(String genes, String delimiter){
		//System.err.println("parsing genes from david file");
		return genes.trim().split(delimiter);	
	}

	
	public void checkHeader(String line){
	// The message should be more informative about which column is wrong
		System.err.println("header line = " + line);
		String [] headerSections = line.split(delimitersValue,-1);
		
		if(!(headerSections[1].contains("Term") && headerSections[5].contains("Genes") && headerSections[12].contains("FDR"))){
			
			JOptionPane.showMessageDialog(giraphApplication.getInstance(), "Unexpected column name found in file, is this a results file from DAVID?\n"
					+ " Giraph will try and parse the file but it may not produce the right output.", 
					"Unexpected column header", JOptionPane.WARNING_MESSAGE);	
			
		}
		// Expected format
		//"GO Term	Description	P-value	FDR q-value	Enrichment	N	B	n	b	Genes"
	}
	
}
