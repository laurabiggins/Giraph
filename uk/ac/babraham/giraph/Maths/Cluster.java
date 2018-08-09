package uk.ac.babraham.giraph.Maths;

/** 
 * This class creates the clusters that are shown as colours.
 * 
 * Each genelist becomes a ClusterPair object in the method createInitialClusterPairs(). 
 * We then iterate through each pair of genelists to find the 2 most closely matched i.e. the 2 with the highest number
 * of the same genes. If the genelists are exactly the same they get an rValue of 0 (it does stop calculating if it finds an 
 * rValue of 0 as we're not going to find a lower value). The 2 genelists with the lowest rValue are paired up by creating a 
 * new ClusterPair object.
 * It then goes through pairing up all the ClusterPair objects until they are all paired up. 
 * 
 * They can be extracted by r value (for when you want to change the colours).
 *  
 *  The rValue is the mean correlation between either just the 2 genelists or all the genelists in the ClusterPair object.
 */

import uk.ac.babraham.giraph.DataParser.ProgressListener;
import uk.ac.babraham.giraph.DataTypes.GeneList;

import java.sql.Timestamp;
import java.util.Vector;

public class Cluster implements Runnable{ 
	
	private Vector<ClusterPair> clusterPairs;
	private GeneList [] gl;
	public ProgressListener appPL;
	private boolean colouredByProximity;
	private boolean running;
	
	public Cluster(GeneList [] genelist, boolean colouredByProximity){
		
		this.gl = genelist;
		clusterPairs = new Vector<ClusterPair>();
		this.colouredByProximity = colouredByProximity;
		running = true;
	}
	
	public void stopRunning(){
		running=false;
	}
	
	public void startRunning(){
		running=true;
	}
	
	public void run(){
		
		startRunning();
		//java.util.Date date= new java.util.Date();
		//System.out.println("At start of run method in Cluster: " + new Timestamp(date.getTime()));
		
		createInitialClusterPairs(gl);		
		findNextHighestCorrelation();
		
		if(clusterPairs.size() == 1){
			notifyClusteringComplete(appPL, clusterPairs.get(0));
		}
		else{
			// throw an error
			System.err.println("The clustering appears not to have completed, number of cluster pairs = " + clusterPairs.size());
		}
		//System.err.println("At end of run method in Cluster: " + new Timestamp(new java.util.Date().getTime()));
	}
	
	/**
	 * This creates the initial cluster pair objects and adds them to the clusterPairs vector.
	 * It uses the overloaded ClusterPair constructor to create the bottom level of the ClusterPairs object i.e. ClusterPairs that
	 * only contain one genelist. These are then combined into 'real' ClusterPair objects when we check the correlations and 
	 * pair them up.
	 *  
	 * @param geneLists
	 */
	 private void createInitialClusterPairs(GeneList [] geneLists){
		 
		 for (int i = 0; i < geneLists.length; i++){
			 clusterPairs.add(new ClusterPair(geneLists[i]));
		 }	 
		 
		//System.out.println("Initial cluster pairs created");
	 }
	
	 /** this needs to iterate through all the cluster pairs
	  * 
	  * to start with it's going to check everything, even if it's been checked before. I don't know if there's an efficient way to set a flag to say
	  * if I've already done that exact comparison.  
	  */	 
	 private void findNextHighestCorrelation(){
		 
		 /** have lastMax variable so that if we hit on an r value that is the same as the last highest one found, we
		  * might as well just go with that than searching through the whole lot 
		  */
		 float lastMaxRValue = 0;
		
		 WHILE_LOOP: while (clusterPairs.size() > 1){ 
		 
			System.err.println("clusterpairs size = " + clusterPairs.size()); 
			
			if(running==false){
				break WHILE_LOOP;
			}
			
			Float meanCorr = (float) 0;
			GeneList [] geneListsInClusterPair1;
			GeneList [] geneListsInClusterPair2;
			Float meanCorrTemp;
			int index1 = 0;
			int index2 = 1;	
			 
			/**
			 * loop through comparing everything against everything in the clusterPairs vector 
			 */
			 for (int i = 0; i < (clusterPairs.size() -1); i++){
				
				 geneListsInClusterPair1 = getAllGeneListsInClusterPair(new Vector<GeneList>(), clusterPairs.get(i)).toArray(new GeneList[0]);
				 
				 for (int j = i+1; j < clusterPairs.size(); j++){
					
					geneListsInClusterPair2 = getAllGeneListsInClusterPair(new Vector<GeneList>(), clusterPairs.get(j)).toArray(new GeneList[0]);						
				
					if(colouredByProximity){
						float d = getMeanDistance(geneListsInClusterPair1, geneListsInClusterPair2);
						meanCorrTemp = transformDistanceToCorrelation(d);
					}
						
					// This is for doing the clustering on correlation rather than distance on screen
					else{
						meanCorrTemp = getMeanCorrelation(geneListsInClusterPair1, geneListsInClusterPair2);  
					}  
					
					if(meanCorrTemp > meanCorr){
					
						meanCorr = meanCorrTemp;
						index1 = i;
						index2 = j;	
						
						if((meanCorr == 1) || (meanCorr == lastMaxRValue)){
						/**
						 *  we're not going to find a higher r value so might as well use this one	
						 */
							makeNewClusterPair(index1, index2, meanCorr);
							lastMaxRValue = meanCorr;
							continue WHILE_LOOP;
						}						
					}
				}
			 }
			/**
			 * we've got to the end of this iteration of the while loop so we create a new ClusterPair with the two 
			 * cluster pairs that were the most highly correlated. If the highest correlation was 0, the indices would be 0 and 1 
			 * from when they were declared. This is fine, because these will be clustered together, added to the end of the vector,
			 * and removed from the front.   
			 */			 
			makeNewClusterPair(index1, index2, meanCorr);
			lastMaxRValue = meanCorr;	
			
		 }				 			
	 }
	 
	 /** this should iterate through all the cluster pairs, getting all the genelists and
	  * cluster pairs. gl has to be passed in as a parameter because it keeps getting added to each time the
	  * method is run. It has to return gl as a vector so it can iterate.
	  * If we want to turn it into an array afterwards that can be done outside of the method.
	  * 
	  * This method is duplicated in the ClusterPair class
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
	 
	 private void makeNewClusterPair(int index1, int index2, float rValue){
		 		 
		 /** create the new cluster pair */
		 ClusterPair newClusterPair = new ClusterPair(clusterPairs.get(index1), clusterPairs.get(index2), rValue);
		 /** remove the old clusterPairs from the vector as they should now be held within the new ClusterPair object */
		
		 removeClusterPairs(index1, index2);
		 //maybe this should be added to the front of the vector?
		 clusterPairs.add(newClusterPair); 
		
		// System.out.println("made new cluster pair with r value of " + rValue + ", cluster pair length is now " + clusterPairs.size());
	 }	 		  

	 // removes the 2 cluster pairs so that they can be added as one later on
	 private void removeClusterPairs(int index1, int index2){
		 // maybe index 1 is always less than index 2 and this if statement is useless?
		 if(index1 < index2){
			 clusterPairs.remove(index1);		
			 // this has to be -1 because index 1 has been removed
			 clusterPairs.remove(index2 -1);			 
		 }
		 else { 
			 clusterPairs.remove(index2);		
			 // this has to be -1 because index 2 has been removed
			 clusterPairs.remove(index1 -1);
		 }
	 }
	
	 private static float transformDistanceToCorrelation(float dist){
		 
		// System.out.println("from Cluster: " + dist);
		 
		 return (1 - dist);
	 }
	 
	 
	 /** This is going to calculate the clusters using the distances rather than the overlap
	  * to try and make the colours match what you actually see on the screen.
	  */
	 private static float getMeanDistance(GeneList[] gl1, GeneList[] gl2){
		 
		 float distSum = 0;
		 
		 if(gl1.length == 1 && gl2.length == 1){
			 
			 return getDistance(gl1[0], gl2[0]);
		 }
		 
		 for (int i = 0; i < gl1.length; i++){
				
				float distTemp = 0;
				
				for (int j = 0; j < gl2.length; j++){

					// sum up all correlations for gl1[i] against each genelist in gl2 
					distTemp = distTemp + getDistance(gl1[i], gl2[j]);					
				}
				// get mean
				distTemp = distTemp/gl2.length;			
				// sum up means
				distSum = distSum + distTemp;
			}	
			// calculate overall mean correlation
			return distSum/gl1.length;		 
	 }
	 
	 // finds the distance between a pair of coordinates
	 private static float getDistance(GeneList gl1, GeneList gl2){
		 
		 //risky_del
		  
		 float x1 = gl1.coordinates().unscaledX;
		 float y1 = gl1.coordinates().unscaledY;
		 float x2 = gl2.coordinates().unscaledX;
		 float y2 = gl2.coordinates().unscaledY;
		 
		// System.out.println("The dodgy ones: x1 = " + x1 + ", y1 = " + y1 + ", x2:  " + x2 + ", y2: " + y2);	
		 
		 //float 
		// x1 = gl1.getunscaledXCoordinate();
		 //float 
		// y1 = gl1.getunscaledYCoordinate();
		 //float 
		// x2 = gl2.getunscaledXCoordinate();
		 //float 
		// y2 = gl2.getunscaledYCoordinate();
		 
		// System.out.println("The right ones: x1 = " + x1 + ", y1 = " + y1 + ", x2:  " + x2 + ", y2: " + y2);	
		 
		 
		 float dist = (float)Math.sqrt(((x1 - x2) * (x1 - x2)) + ((y1 - y2) * (y1 - y2)));
				 
		 return dist;
	 }
	 
	 
	 
	/**
	 * This calculates the mean correlation between 2 sets of gene lists
	 * and returns the rValue 
	 * @param gl1
	 * @param gl2
	 * @return
	 */	 
	private static float getMeanCorrelation(GeneList[] gl1, GeneList[] gl2){		

		float corrSum = 0;
		
		if(gl1.length == 1 && gl2.length == 1){
			
			return gl1[0].getCorrelation(gl2[0]);
		}
		
		for (int i = 0; i < gl1.length; i++){
				
			float corrTemp = 0;
			
			for (int j = 0; j < gl2.length; j++){

				// sum up all correlations for gl1[i] against each genelist in gl2 
				corrTemp = corrTemp + gl1[i].getCorrelation(gl2[j]);					
			}
			// get mean
			corrTemp = corrTemp/gl2.length;			
			// sum up means
			corrSum = corrSum + corrTemp;
		}	
		// calculate overall mean correlation
		return corrSum/gl1.length;
	}
	
	public void addProgressListener(ProgressListener pl){
		//System.out.println("adding app listener in cc");
		this.appPL = pl;
	}
	
	public void notifyClusteringComplete(ProgressListener appPL, ClusterPair cp){
		
		appPL.clusteringComplete(cp);
	}	
} 