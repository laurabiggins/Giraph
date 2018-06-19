package uk.ac.babraham.giraph.Displays.QC;

import java.util.Arrays;

import uk.ac.babraham.giraph.DataTypes.Gene;
import uk.ac.babraham.giraph.DataTypes.GeneCollection;

public class GeneLengthPlot extends DensityPlot{

	
	public GeneLengthPlot(GeneCollection subsetGenes, GeneCollection backgroundGenes) {
		
		super(subsetGenes, backgroundGenes);
		xLabel= "gene length";
	}
/*	
	protected double[] getSubsetData(GeneCollection subsetGeneCollection){
		
		Gene [] queryGenes = subsetGeneCollection.getAllGenes();
		
		double [] queryLength = new double[queryGenes.length];
		
		for(int i=0; i<queryLength.length; i++){
			queryLength[i] = Math.log(queryGenes[i].getLength());
		}
		
		Arrays.sort(queryLength);
		
		return queryLength;
	}
	
	protected double[] getBackgroundData(GeneCollection backgroundGeneCollection){
		
		Gene [] backgroundGenes = backgroundGeneCollection.getAllGenes();
		
		double [] backgroundLength = new double[backgroundGenes.length];
		
		for(int i=0; i<backgroundLength.length; i++){
			backgroundLength[i] = Math.log(backgroundGenes[i].getLength());
		}
		Arrays.sort(backgroundLength);
		
		return backgroundLength;
	}
*/
	protected double[] getData(GeneCollection geneCollection){
		
		Gene [] genes = geneCollection.getAllGenes();
		
		double [] geneLength = new double[genes.length];
		
		for(int i=0; i<geneLength.length; i++){
			geneLength[i] = Math.log(genes[i].getLength());
		}
		Arrays.sort(geneLength);
		
		return geneLength;
	}		
}
