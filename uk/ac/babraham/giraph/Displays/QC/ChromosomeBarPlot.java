package uk.ac.babraham.giraph.Displays.QC;

import java.util.Arrays;
import java.util.Vector;

import uk.ac.babraham.giraph.DataTypes.Gene;
import uk.ac.babraham.giraph.DataTypes.GeneCollection;

public class ChromosomeBarPlot extends BarPlot {
	
	public ChromosomeBarPlot(GeneCollection subsetGenes, GeneCollection backgroundGenes){
	
		super(subsetGenes, backgroundGenes);
		legendPosition = "right";
	}
	
	protected String getDataForGene(Gene gene){
		return gene.getChr();
	}

	protected void orderCategoryNames(){
		
		Vector<Integer> numericalVector = new Vector<Integer>();
		Vector<String> stringVector = new Vector<String>();
		
		for(int i=0; i<categoryNames.length; i++){
			
			if(isInteger(categoryNames[i])){
				numericalVector.add(Integer.parseInt(categoryNames[i]));
				//System.err.println("cat name: " + categoryNames[i]);
			}
			else{
				stringVector.add(categoryNames[i]);
				//System.err.println("cat name parsed: " + Integer.parseInt(categoryNames[i]));
			}
		}
		Integer [] numericalNames = numericalVector.toArray(new Integer[numericalVector.size()]);				
		Arrays.sort(numericalNames);
		
		String [] stringNames = stringVector.toArray(new String[stringVector.size()]);				
		Arrays.sort(stringNames);
		
		String [] sortedCategoryNames = new String[categoryNames.length];
		
		for(int i=0; i<numericalNames.length; i++){
			
			sortedCategoryNames[i] = numericalNames[i].toString();
		}
		for(int i=0; i<stringNames.length; i++){
			
			sortedCategoryNames[numericalNames.length+i] = stringNames[i];
		}
		
		categoryNames = sortedCategoryNames;

	}
	
	public static boolean isInteger(String s) {
	    return isInteger(s,10);
	}

	public static boolean isInteger(String s, int radix) {
	    if(s.isEmpty()) return false;
	    for(int i = 0; i < s.length(); i++) {
	        if(i == 0 && s.charAt(i) == '-') {
	            if(s.length() == 1) return false;
	            else continue;
	        }
	        if(Character.digit(s.charAt(i),radix) < 0) return false;
	    }
	    return true;
	}
		
}
