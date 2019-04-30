package uk.ac.babraham.giraph.DataTypes;

/** This contains information from the GMT file. Each line of the file becomes a FunctionalSetInfo object. 
 * It does not contain the genes, only the ontology (or other) information.
 * It is linked to the genelists in the GeneListCollection class.
 * 
 */


public class FunctionalSetInfo {

	// The name/id of the category 
	private String name;
	
	// The description of the category
	private String description;
	
	// size of category - needed for stats and filtering
	private int totalNoOfGenesInCategory;

	// no of background genes in category - needed for stats
	private int noOfBackgroundGenesInCategory;
	
	/** We don't actually use the source or source ID at the moment - it's just commented out in case we want to put it back in in the future
	private String sourceIdentifier;	
	
	// the source of the set eg GO
	private String source;
	*/
	
	public FunctionalSetInfo(){
		
	}

	// set the name
	public void setName(String name){
		this.name = name;
	}
	
	// get the name
	public String name(){
		return name;
	}
	
	// set the source
/*	public void setSource(String source){
		this.source = source;
	}
	
	// get the source
	public void setSourceIdentifier(String id){
		this.sourceIdentifier = id;
	}
*/	
	// set the description
	public void setDescription(String desc){
		description = desc;
	}
	
	// get the description
	public String description(){
		return description;
	}
	
	// set total no of genes in functional set
	public void setTotalNoOfGenesInCategory(int n){
		totalNoOfGenesInCategory = n;
	}
	
	// get the total number of genes in set 
	public int totalNoOfGenesInCategory(){
		return totalNoOfGenesInCategory;
	}
	
	public void setNoOfBackgroundGenesInCategory(int n){
		noOfBackgroundGenesInCategory = n;
	}
	
	public int noOfBackgroundGenesInCategory(){
		return noOfBackgroundGenesInCategory;
	}		
	
}
