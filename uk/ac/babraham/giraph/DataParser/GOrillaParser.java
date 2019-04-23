package uk.ac.babraham.giraph.DataParser;

import java.io.File;
import java.util.ArrayList;

/** This is specific to the output produced from the GOrilla ontology tool.
 *  
 * @author bigginsl
 *
 */

public class GOrillaParser extends ExternalResultsParser{

	private String allGenesString;
	
	private String delimiter;
	
	public GOrillaParser(File file){
		
		super(file);
	}
	
	public void setColumnInfoForFile(){
		System.err.println("setting fields for david file");
		delimitersValue = "\t";
		geneDelimitersValue = ",";
		
		startRowValue = 1;
		queryGeneColValue = 9;
		categoryNameColValue = 0;
		categoryDescriptionColValue = 1;
		pValueColValue = 3;		
	}
	
	

	public String [] parseGenes(String allGenesString, String delimiter){
		
		String trimmedString = allGenesString.trim();
		System.err.println("trimmedString = " + trimmedString);
		
		ArrayList<String> cleanedGenesTemp = new ArrayList<String>();
		
		if(trimmedString.startsWith("\"[") && trimmedString.endsWith("]\"")){
			
			trimmedString = trimmedString.substring(2, trimmedString.length()-2);
			
		} else if(trimmedString.startsWith("[") && trimmedString.endsWith("]")){
			
			trimmedString = trimmedString.substring(1, trimmedString.length()-1);
		}
		else if(trimmedString.startsWith("\"[")){
			
			trimmedString = trimmedString.substring(1, trimmedString.length());
		}
		else if(trimmedString.endsWith("]\"")){
			
			trimmedString = trimmedString.substring(0, trimmedString.length()-1);
		}
		else if(trimmedString.startsWith("[")){
			
			trimmedString = trimmedString.substring(1, trimmedString.length());
		}
		else if(trimmedString.endsWith("]")){
			
			trimmedString = trimmedString.substring(0, trimmedString.length()-1);
		}
		
		else{
			
			System.err.println("didn't find the usual characters at the end of the line");
			return null;
		}	
						
		String [] splitGenes = trimmedString.split(delimiter);

		
		for (int i=0; i<splitGenes.length; i++){
			
			String tempGene = splitGenes[i].trim();
			
			//System.err.println("tempGene = " + tempGene);
			
			if(tempGene.contains(" - ")){
				//System.err.println("tempGene cleaned = " + tempGene.substring(0,  tempGene.indexOf(" - ")));
				cleanedGenesTemp.add(tempGene.substring(0,  tempGene.indexOf(" - ")));
			}	
			else continue;
			
		}
		
		return cleanedGenesTemp.toArray(new String[0]);
	}
	
}
