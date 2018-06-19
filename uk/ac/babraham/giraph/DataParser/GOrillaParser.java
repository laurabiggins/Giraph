package uk.ac.babraham.giraph.DataParser;

import java.util.ArrayList;

/** This is specific to the output produced from the GOrilla ontology tool.
 *  
 * @author bigginsl
 *
 */

public class GOrillaParser {

	private String allGenesString;
	
	private String delimiter;
	
	public GOrillaParser(String allGenes, String delimiter){
		
		this.allGenesString = allGenes;
		this.delimiter = delimiter;
	}
	
	public String [] parseGenes(){
		
		String trimmedString = allGenesString.trim();

		ArrayList<String> cleanedGenesTemp = new ArrayList<String>();
		
		if(trimmedString.startsWith("\"[") && trimmedString.endsWith("]\"")){
			
			trimmedString = trimmedString.substring(2, trimmedString.length()-2);
			
			String [] splitGenes = trimmedString.split(delimiter);

			
			for (int i=0; i<splitGenes.length; i++){
				
				String tempGene = splitGenes[i].trim();
				
				if(tempGene.contains(" - ")){
				
					cleanedGenesTemp.add(tempGene.substring(0,  tempGene.indexOf(" - ")));
				}	
				else continue;
				
			}
		}
		return cleanedGenesTemp.toArray(new String[0]);
	}
	
}
