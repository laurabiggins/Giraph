package uk.ac.babraham.giraph.DataParser;

import java.io.File;

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
		System.err.println("parsing genes from david file");
		return genes.trim().split(delimiter);
		
	}

}
