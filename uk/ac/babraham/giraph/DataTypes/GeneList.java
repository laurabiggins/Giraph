package uk.ac.babraham.giraph.DataTypes;

/**
 * One GeneList contains one or more genes that all have the same associated term (e.g. function, location etc.).
 * A Gene can belong to multiple GeneLists, multiple Genes can belong to one GeneList.
 * 
 * This was originally for a Genelist returned from David. 
 * Now, query genes are entered and looked up in the gmt file.
 * 
 * It contains genes, stats about the term, and coordinates for where the circle goes 
 * on the GraphPanel. 
 * 
 *  The stats can only be calculated once all the genelists have been created.
 */

import java.awt.Color;
import java.util.ArrayList;

public class GeneList {
	
	// genes in the gene list
	private Gene [] genes;
	
	// functional set info
	private FunctionalSetInfo functionalSetInfo;
	
	// p value from Fisher's test 
	private PValue pvalue;
	
	// whether the gene list passes the filters
	private boolean validity = true;
	
	// These are the scaled coordinates
	private Coordinates coordinates;
	
	// The colour of the circle
	public Color colour = Color.darkGray;
	
	public GeneList(){
		
		this.coordinates = new Coordinates();
	}
	
	public Coordinates coordinates(){
		return coordinates;
	}
		
	public void setPvalue(PValue pv){
		this.pvalue = pv;
	}
	
	public PValue getPvalue(){
		return pvalue;
	}
	
	public void setFunctionalSetInfo(FunctionalSetInfo f){
		
		this.functionalSetInfo = f;
	}
	
	// access the functional set information
	public FunctionalSetInfo getFunctionalSetInfo(){
		
		return functionalSetInfo;
	}
	
	// to get to all the gene symbols for a genelist
	public String [] getGeneSymbols(){
		
		String [] geneSymbols = new String[genes.length];
		
		for(int i=0; i<genes.length; i++){
			
			geneSymbols[i] = genes[i].getGeneSymbol();
		}
		return geneSymbols;
	}	
	
	public Gene[] getGenes(){
		return genes;
	}
	
	public void setGenes(Gene[] g){
		genes = g;
	}
	
	public void setValidity(boolean b){

		validity = b;
	}
	public boolean getValidity(){
		return validity;
	}
	
	// The number of genes that overlap between this genelist and another one
	public int countOverlap(GeneList otherGeneList){		
		int count = 0;
		String [] otherGeneSymbols = otherGeneList.getGeneSymbols();		
		String [] theseGeneSymbols = getGeneSymbols();
		OUTER: for (int i=0; i < genes.length; i++){			
			//String theseGeneSymbols = getGeneSymbols[i];		
			for (int j=0; j < otherGeneSymbols.length; j++){				
				if (otherGeneSymbols[j].equals(theseGeneSymbols[i])){
					count++;
					continue OUTER;
				}
			}			
		}
		return count;
	}
	
	// The genes that overlap between this genelist and another one
	public Gene[] getOverlappingGenes(GeneList otherGeneList){	
		
		ArrayList<Gene> overlappingGenesAL = new ArrayList<Gene>();;
		String [] otherGeneSymbols = otherGeneList.getGeneSymbols();
		String [] theseGeneSymbols = getGeneSymbols();
		OUTER: for (int i=0; i < genes.length; i++){			
			//String theseGeneSymbols = geneSymbols[i];		
			for (int j=0; j < otherGeneSymbols.length; j++){				
				if (otherGeneSymbols[j].equals(theseGeneSymbols[i])){
					overlappingGenesAL.add(genes[i]);
					continue OUTER; // don't need to keep looping through if it has matched - there shouldn't be duplicate genes in a gene list
				}
			}			
		}
		Gene[] overlappingGenes = overlappingGenesAL.toArray(new Gene[overlappingGenesAL.size()]);
		return overlappingGenes;
	}		
	
	/**
	 * This gets the maximum correlation which I think is what we want - if one gene list is basically a subset of another one, they should be close.
	 */
	public float getCorrelation(GeneList otherGeneList){
		
		float corr;
		int count = countOverlap(otherGeneList);
		if (genes.length < otherGeneList.getGenes().length){	
			corr = (float)count/genes.length;
		}
		else{
			corr = (float)count/otherGeneList.getGenes().length;
		}
		//System.out.println("correlation: " + corr);
		return corr;
	}
	
}
