package uk.ac.babraham.giraph.Displays.QC;

import java.util.Arrays;

import uk.ac.babraham.giraph.DataTypes.Gene;
import uk.ac.babraham.giraph.DataTypes.GeneCollection;

public class TranscriptNumberBarplot extends BarPlot {
	
	public TranscriptNumberBarplot(GeneCollection subsetGenes, GeneCollection backgroundGenes){
	
		super(subsetGenes, backgroundGenes);
		legendPosition = "right";
	}
	
	protected String getDataForGene(Gene gene){
		return ((Integer)(gene.getNoOfTranscripts())).toString();
	}

	// sort the names into numerical order
	protected void orderCategoryNames(){
		
		int [] numericalNames = new int[categoryNames.length];
		
		for(int i=0; i<categoryNames.length; i++){
			
			numericalNames[i] = Integer.parseInt(categoryNames[i]);
		}
		Arrays.sort(numericalNames);
		
		String [] sortedCategoryNames = new String[categoryNames.length];
		
		for(int i=0; i<numericalNames.length; i++){
			
			sortedCategoryNames[i] = ((Integer)(numericalNames[i])).toString();
		}
		categoryNames = sortedCategoryNames;
		
	}
	
}
