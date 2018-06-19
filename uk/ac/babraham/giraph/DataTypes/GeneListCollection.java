package uk.ac.babraham.giraph.DataTypes;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;

/**
 * @author bigginsl
 * 
 * This is how the gene lists are stored
 * 
 */

public class GeneListCollection {

	
	// key is the category name
	private HashMap<String, GeneList> geneListCollection;

	public GeneListCollection(){
		geneListCollection = new HashMap<String, GeneList>();
	}
	
	public GeneList getGeneList(String categoryName){
		return geneListCollection.get(categoryName);
	}
	
	public GeneList[] getAllGeneLists(){

		return geneListCollection.values().toArray(new GeneList[0]);
	}	
	
	public GeneList[] getValidGeneLists(){
		
		GeneList [] allGeneLists = getAllGeneLists();
		ArrayList <GeneList> al = new ArrayList<GeneList>();
		for (int i=0; i<allGeneLists.length; i++){
			
			if(allGeneLists[i].getValidity()){
				al.add(allGeneLists[i]);
			}
		}
		return al.toArray(new GeneList[0]);	
	}
	
	public void addGeneList(GeneList gl){
		
		String categoryName = gl.getFunctionalSetInfo().name();
		
		if(geneListCollection.containsKey(categoryName)){
			System.err.println("Already have a functional set named " + categoryName);
		}
		else{
			geneListCollection.put(categoryName, gl);
		}		
	}
}	
	