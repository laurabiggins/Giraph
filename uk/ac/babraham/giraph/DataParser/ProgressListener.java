package uk.ac.babraham.giraph.DataParser;

import uk.ac.babraham.giraph.DataTypes.GeneCollection;
import uk.ac.babraham.giraph.DataTypes.GeneListCollection;
import uk.ac.babraham.giraph.Maths.ClusterPair;

/** This is used to notify the app of updates  
 *  
 *  @author bigginsl
 *
 */


public interface ProgressListener {
	
	public void firstCoordinatesReady();
	
	public void calculate();
		
	public void updateGraphPanel();
	
	public void clusteringComplete(ClusterPair cp);
	
	//public void calculateClusters();
	
	public void calculatingCoordinatesStopped();
	
	public void inputFileParsingComplete(GeneListCollection geneListCollection, GeneCollection queryGenes, GeneCollection customBackgroundGenes, GeneCollection genomicBackgroundGenes);
	
	//public void setFilters(int geneListSize, float pvalue);
	public void setFilters(float pvalue);
	
	public void externalResultsParsed(GeneListCollection geneListCollection);

	public void progressCancelled();
	
	public void progressUpdated(String s, int x1, int x2);
	
	public void progressWarningReceived (Exception e);
	
}


