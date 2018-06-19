package uk.ac.babraham.giraph.DataParser;

public class DavidParser {
	
	private String allGenesString;
	
	private String delimiter;
	
public DavidParser(String allGenes, String delimiter){
		
		this.allGenesString = allGenes;
		this.delimiter = delimiter;
	}
	
	public String [] parseGenes(){
		
		return allGenesString.trim().split(delimiter);
		
	}

}
