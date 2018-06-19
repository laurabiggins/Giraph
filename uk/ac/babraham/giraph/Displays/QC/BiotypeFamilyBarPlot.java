package uk.ac.babraham.giraph.Displays.QC;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import uk.ac.babraham.giraph.DataTypes.Gene;
import uk.ac.babraham.giraph.DataTypes.GeneCollection;

public class BiotypeFamilyBarPlot extends BarPlot {
	
	public BiotypeFamilyBarPlot(GeneCollection subsetGenes, GeneCollection backgroundGenes){
	
		super(subsetGenes, backgroundGenes);
	}
	
	protected String getDataForGene(Gene gene){
		return gene.biotypeFamily();
	}
}