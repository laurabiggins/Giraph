package uk.ac.babraham.giraph.Maths;

import java.util.Vector;

import uk.ac.babraham.giraph.DataTypes.GeneList;

/**
 * @author bigginsl
 *
 */

public class ClusterPair {

	private ClusterPair pair1 = null;
	private ClusterPair pair2 = null;
	private GeneList gl1 = null;
//	private GeneList gl2 = null;
	private float rValue;
	
	/** This is used when setting up the initial ClusterPairs and forms the bottom level of the tiers */ 
	public ClusterPair (GeneList singleGeneList) {
		this.gl1 = singleGeneList;
		this.rValue = 1;
	}
	
	public ClusterPair (ClusterPair pair1, ClusterPair pair2, float rValue) {
		this.pair1 = pair1;
		this.pair2 = pair2;
		this.rValue = rValue;
	}
	
	protected ClusterPair pair1 () {
		return pair1;
	}
	
	protected ClusterPair pair2 () {
		return pair2;
	}
	
	protected GeneList geneList1 () {
		return gl1;
	}
	
/*	protected GeneList geneList2 () {
		return gl2;
	}
*/	
	public float rValue () {
		return rValue;
	}
	
	
	/**
	 * method to obtain a vector of cluster pairs for a given r value  
	 */
	 private Vector<ClusterPair> getClusterPairs(Vector<ClusterPair> cp, ClusterPair inputClusterPair, float rValueCutoff){
			 
		 if(inputClusterPair.rValue() >= rValueCutoff){
			 cp.add(inputClusterPair);				 
		 }
		 // hopefully this won't just go off down one path and not get all the gene lists
		 else{
			 getClusterPairs(cp, inputClusterPair.pair1(), rValueCutoff);
			 getClusterPairs(cp, inputClusterPair.pair2(), rValueCutoff);
		 }
		 return cp;
	 }
	 
	 /**
	  * returns all gene lists that are in a cluster pair
	  * @param gl
	  * @param cp
	  * @return
	  */
	 private Vector<GeneList> getAllGeneListsInClusterPair(Vector<GeneList> gl, ClusterPair cp){
		 
		 if(cp.geneList1() != null){
			 gl.add(cp.geneList1());
		 }
		 // hopefully this won't just go off down one path and not get all the gene lists
		 else{
			 getAllGeneListsInClusterPair(gl, cp.pair1());
			 getAllGeneListsInClusterPair(gl, cp.pair2());
		 }
		 return gl;
	 }
	 
	 /**
	  * This returns an array of valid genelists.
	  * @param rValueCutoff
	  * @return
	  */
	 public GeneList[][] getValidClusters(float rValueCutoff){		 
		 
		 // we need a new vector of cluster pairs which all have the right rValue
		Vector<ClusterPair> clusterPairsCorrectRValue = getClusterPairs(new Vector<ClusterPair>(), this, rValueCutoff);
		 
		Vector<Vector<GeneList>> vecGeneList = new Vector<Vector<GeneList>>(); 
		 
		for (int i = 0; i < clusterPairsCorrectRValue.size(); i++){
			 
			if(getAllGeneListsInClusterPair(new Vector<GeneList>(), clusterPairsCorrectRValue.get(i)).size() > 0){

				vecGeneList.add(getAllValidGeneListsInClusterPair(new Vector<GeneList>(), clusterPairsCorrectRValue.get(i)));
			}	 
		}
		 
		GeneList [][] geneList = new GeneList[vecGeneList.size()][];
		for (int i = 0; i < vecGeneList.size(); i++){
			geneList[i] = vecGeneList.get(i).toArray(new GeneList[0]);	 			
		}	
	//	System.out.println("no of clusters from valid genelists for rValue " + rValueCutoff + "  =  " + geneList.length);
		 
		return geneList;
	}	 
	
	 //TODO: this doesn't work properly - actually I think it does work properly now. 
	 private Vector<GeneList> getAllValidGeneListsInClusterPair(Vector<GeneList> gl, ClusterPair cp){
		 
		 if(cp.geneList1() != null && cp.geneList1().getValidity()){
			 gl.add(cp.geneList1());
		 }
		 else if(cp.geneList1() != null && cp.geneList1().getValidity() == false){
		 }
		 else{
			 getAllValidGeneListsInClusterPair(gl, cp.pair1());
			 getAllValidGeneListsInClusterPair(gl, cp.pair2());
		 }
		 return gl;
	 }	 
	 
}
