package uk.ac.babraham.giraph.DataTypes;

/**
 * The query gene object. Genes can belong to multiple genelists.
 * 
 * @author bigginsl
 */

public class Gene {
	
	private String geneSymbol;
	private GeneList[] geneList;
	private float GCContent;
	private int length;
	private int noOfTranscripts;
	private String chromosome;
	private String biotypeFamily;
	private String biotype;
	
	public Gene(String gs) {
		this.geneSymbol = gs;
		//System.out.println("new gene created: " + gs);
	}
	public Gene(String gs, float gcContent, int length, int noOfTranscripts, String chr) {
		this.geneSymbol = gs;
		this.GCContent = gcContent;
		this.length = length;
		this.noOfTranscripts = noOfTranscripts;
		this.chromosome = chr;
		//System.out.println("new gene created: " + gs);
	}
	
	public void setGeneSymbol(String gs){
		this.geneSymbol = gs;
	}
	public String getGeneSymbol(){
		return geneSymbol;
	}
	public void setGCContent(float gc){
		this.GCContent = gc;
	}
	public float getGCContent(){
		return GCContent;
	}
	public void setLength(int length){
		this.length = length;
	}
	public float getLength(){
		return length;
	}
	public int getNoOfTranscripts(){
		return noOfTranscripts;
	}
	public String getChr(){
		return chromosome;
	}
	public void setBiotypeFamily(String biotypeFamily){
		this.biotypeFamily = biotypeFamily;
	}
	public String biotypeFamily(){
		return biotypeFamily;
	}	
	public void setBiotype(String biotype){
		this.biotype = biotype;
	}
	public String biotype(){
		return biotype;
	}
	public void addGeneList(GeneList gl){
		if (geneList == null){
			geneList = new GeneList[1];
			geneList[0] = gl;
		}
		else {
			GeneList[] newGeneList= new GeneList[geneList.length + 1];
			for (int i = 0; i < geneList.length; i++){
				newGeneList[i] = geneList[i];
			}
			newGeneList[geneList.length] = gl;
			geneList = newGeneList;
		}		
	}
	public GeneList[] getGeneLists(){
		return geneList;
	}

}
