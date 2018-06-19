package uk.ac.babraham.giraph.Displays.QC;

import uk.ac.babraham.giraph.DataTypes.Gene;
import uk.ac.babraham.giraph.DataTypes.GeneCollection;

public class BiotypeBarPlot  extends BarPlot {
	
	public BiotypeBarPlot(GeneCollection subsetGenes, GeneCollection backgroundGenes){
	
		super(subsetGenes, backgroundGenes);
	}
	
	protected String getDataForGene(Gene gene){
		return gene.biotype();
	}

}
