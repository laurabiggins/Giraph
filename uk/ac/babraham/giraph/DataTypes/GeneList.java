package uk.ac.babraham.giraph.DataTypes;

/**
 * One GeneList contains one or more genes that all have the same associated term (e.g. function, location etc.).
 * A Gene can belong to multiple GeneLists, multiple Genes can belong to one GeneList.
 * 
 * This was originally for a Genelist returned from David. 
 * Now, query genes are entered and looked up in the gmt file.
 *  * 
 * It contains genes, stats about the term, and coordinates for where the circle goes 
 * on the GraphPanel. 
 * 
 *  The stats can only be calculated once all the genelists have been created.
 */

import java.awt.Color;
import java.util.ArrayList;

import uk.ac.babraham.giraph.giraphApplication;

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
	
	
	
	//private String term;
	//private String category;
	//private int clusterNo;
	//private float foldEnrichment;
	//private float fdr;
	//private String [] geneSymbols; //TODO: These really should just be extracted from the gene objects.
	
	/*	
	public void setGeneSymbols(String [] geneSymbols){
		this.geneSymbols = geneSymbols;
	}
	public void setTerm(String t){
		this.term = t;
	}	
	public String getTerm(){
		return term;
	}
	public void setNoOfGenesInCategory(int n){
		this.noOfGenesInCategory = n;
	}
	public int getTotalNoOfGenesInCategory(){
		return noOfGenesInCategory;
	}
	public int getNoOfGenesInList(){
		int n = genes.length;
		return n;
	}	
*/
	
	/*	public void addSingleGene(Gene g){
	int newLength;
	if(genes == null){
		newLength = 1;
	}
	else{
		// check if gene is already in the array 
		// why would it already be there if there are no duplicate genes - because this is used to create the 'genelist' objects
		 // and lots of the genes in the gmt file are duplicated.
		 
		if(geneAlreadyInArray(g) == true){
			System.out.println("From GeneList, " + g.getGeneSymbol() + " gene already exists in " + this.term);
			return;
		}
		newLength = genes.length+1;
	}
	Gene [] newGeneArray = new Gene[newLength];
	String [] newGeneSymbols = new String[newLength];
	
	for (int i = 0; i < (newLength -1); i++){
		newGeneArray[i] = genes[i];
		newGeneSymbols[i] = geneSymbols[i];
	}
	newGeneArray[newLength-1] = g;
	newGeneSymbols[newLength-1] = g.getGeneSymbol();
	genes = newGeneArray;
	geneSymbols = newGeneSymbols;
	//System.out.println("GeneList: no of genes in genelist: " + genes.length);
	//System.out.println("GeneList: no of geneSymbols in genelist: " + geneSymbols.length);
}

private boolean geneAlreadyInArray(Gene g){
	
	boolean x = false;
	
	for (int i = 0; i < genes.length; i++){
		
		if(genes[i].equals(g)){
			x = true;
		}			
	}
	return x;
}

public void setClusterNo(int x){
	this.clusterNo = x;
}	
public int getClusterNo(){
	return clusterNo;
}
*/
	
	// The number of genes that are in the term/category in the gmt file  
//	private int noOfGenesInCategory;
	
/*	public void setFoldEnrichment(float fe){
		this.foldEnrichment = fe;
	}	
	public float getFoldEnrichment(){
		return foldEnrichment;
	}
	public void setFDR(float fd){
		this.fdr = fd;
	}	
	public float getFDR(){
		return fdr;
	}
*/	
}
