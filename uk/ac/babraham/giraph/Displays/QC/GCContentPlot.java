package uk.ac.babraham.giraph.Displays.QC;

import java.util.Arrays;

import uk.ac.babraham.giraph.DataTypes.Gene;
import uk.ac.babraham.giraph.DataTypes.GeneCollection;

public class GCContentPlot extends DensityPlot{

	
	public GCContentPlot(GeneCollection subsetGenes, GeneCollection backgroundGenes) {
		
		super(subsetGenes, backgroundGenes);
		xLabel= "GC content";
	}
	
/*	protected double[] getSubsetData(GeneCollection subsetGeneCollection){
		
		return getData(subsetGeneCollection);
		
		/*Gene [] queryGenes = subsetGeneCollection.getAllGenes();
		
		double [] queryGC = new double[queryGenes.length];
		
		for(int i=0; i<queryGC.length; i++){
			queryGC[i] = queryGenes[i].getGCContent();
		}
		
		Arrays.sort(queryGC);
		
		return queryGC;
		
	}
	
/*	protected double[] getBackgroundData(GeneCollection backgroundGeneCollection){
		
		return getData(backgroundGeneCollection);
		
		/*Gene [] backgroundGenes = backgroundGeneCollection.getAllGenes();
		
		double [] backgroundGC = new double[backgroundGenes.length];
		
		for(int i=0; i<backgroundGC.length; i++){
			backgroundGC[i] = backgroundGenes[i].getGCContent();
		}
		Arrays.sort(backgroundGC);
		
		return backgroundGC;
	
	}
*/	
	protected double[] getData(GeneCollection geneCollection){
		
		Gene [] genes = geneCollection.getAllGenes();
		
		double [] GC = new double[genes.length];
		
		for(int i=0; i<GC.length; i++){
			GC[i] = genes[i].getGCContent();
		}
		Arrays.sort(GC);
		
		return GC;		
	}
	
}
