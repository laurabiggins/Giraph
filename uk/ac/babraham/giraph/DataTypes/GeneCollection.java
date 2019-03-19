package uk.ac.babraham.giraph.DataTypes;

import java.util.HashMap;

public class GeneCollection {
	
	private HashMap<String, Gene> genes;

	public GeneCollection(){
		genes = new  HashMap<String, Gene>();
	}
	
	public void addGene(Gene g){
		
		if(genes.containsKey(g.getGeneSymbol())){
			System.err.println(g.getGeneSymbol() + " already present in file");
			return;
		}
		else{
			genes.put(g.getGeneSymbol(), g);
		}
	}
	
	public boolean contains(String geneSymbol){
		if(genes.containsKey(geneSymbol)){
			return true;
		}
		else return false;
	}
	
	public boolean doesNotContain(String geneSymbol){
		if(genes.containsKey(geneSymbol)){
			return false;
		}
		else return true;
	}
	
	public Gene getGene(String geneSymbol){
		
		if (genes.containsKey(geneSymbol)){
			return genes.get(geneSymbol);
		}
		else{
			//System.err.println("Couldn't find a gene with symbol " + geneSymbol);
			return null;
		}
	}
	
	public Gene[] getAllGenes(){
		
		return genes.values().toArray(new Gene[0]);
	}
}

