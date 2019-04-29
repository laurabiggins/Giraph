package uk.ac.babraham.giraph.Maths;
import java.util.Enumeration;
import java.util.Vector;

import uk.ac.babraham.giraph.giraphApplication;

/** 
 * This class calculates the optimal positions of the genelists in relation to each other. 
 */


import uk.ac.babraham.giraph.DataParser.ProgressListener;
import uk.ac.babraham.giraph.DataTypes.GeneListCollection;
import uk.ac.babraham.giraph.DataTypes.GeneList;
import uk.ac.babraham.giraph.Utilities.StopPauseListener;

public class CalculateCoordinates implements Runnable, StopPauseListener {

	boolean continueCalculating;
	private Vector<ProgressListener> listeners = new Vector<ProgressListener>();
	
	private GeneListCollection geneListCollection;
	
	// is this because we don't want to keep getting the valid genelists from the collection each time?
	private GeneList[] validGeneLists;
	
	int minNoOfImprovingPositions = 4;
	
	float coolingOff = (float)1;
	
	
	/**
	 * The constructor 
	 */
	public CalculateCoordinates(GeneListCollection geneListCollection){	
		
		this.geneListCollection = geneListCollection;
		
		this.validGeneLists = geneListCollection.getValidGeneLists();
		
		if(validGeneLists.length < (minNoOfImprovingPositions * 4)){
			minNoOfImprovingPositions = (validGeneLists.length/4); 
		}
		
		//initialSetUp(geneListCollection);
		this.continueCalculating = true;

	}
	

	/**
	 * Run the calculations
	 */
	public void run(){	
		
		
		/** If two circles are at an optimum distance (within threshold of the diffFactor) then they won't be moved. 
		 * Do not reduce this number or everything starts going around in circles if there aren't many gene lists. */
		float diffThreshold = (float)0.2;
		
		/**  x is a count and is used to control how often coordinates are updated etc */
		int x = 1;
		
		/** To work out when to stop calculating */
		int numberToCheck = validGeneLists.length/2;
		float [] previousTotalDifference = new float [numberToCheck];
		int totalDifferenceIndex = 0;
		
		int sumMoveCloser = 0;
		int sumMoveAway = 0;
		
		float highCorrelationThreshold = (float)0.90;
		
		/** The minimum correlation that we care about - if they're not remotely correlated we don't mind where they are in relation to each other,
		 * they'll get pulled around enough by others that they are correlated with anyway... not necessarily true. 
		 * 
		 * This starts off high so that we only move the highly correlated circles around, and decreases on subsequent iterations.
		 * */
		double minCorrelation = 0.95;
		
		
		/** The circles do not want to be too far away from each other - if they try and get too far then they mess up the screen. 
		 * This is high initially so that we don't invoke the if statement (later on in this code) while the minCorrelation is really high.  
		 * if((correlation > minCorrelation) || ((correlation <= minCorrelation)&& (Math.abs(difference)>minDistance))){	
		 * 		then move circle
		 * */
		 
		 double minDistance = 1;
		
		LOOP: while (continueCalculating == true){
		
			
			if(x < 31){
				minCorrelation -= 0.025;
				//minDistance = minDistance - 0.02;
			}
			
			if(x == 31){				
				System.err.println("minCorrelation = " + minCorrelation);
				minDistance = 0.4;
				System.err.println("minDistance = " + minDistance);
			}
						
			float thisTotalDifference = 0;			
						
			/**
			 * calculate overall trajectory for gene list i
			 */
			for (int i = 0; i < validGeneLists.length; i++){								
				
				/** value that is added to and subtracted from as we decide which way the x coordinate should move */
				float sumDiffX = 0;
				
				/** value that is added to and subtracted from as we decide which way the y coordinate should move */
				float sumDiffY = 0;
					
				/** I'm not entirely sure why we get NaN values occasionally, it's something to do with relaxing the filters */				
				if(Float.isNaN(validGeneLists[i].coordinates().unscaledX)){
					System.out.println("coolingOff = " + coolingOff);
					System.out.println("changing unScaledX i " + validGeneLists[i].coordinates().unscaledX +  " to 0.5");
					validGeneLists[i].coordinates().unscaledX  = (float)0.5;
					System.out.println("unScaledX " + validGeneLists[i].coordinates().unscaledX);
				}
				if(Float.isNaN(validGeneLists[i].coordinates().unscaledY)){
					System.out.println("changing unScaledY i" + validGeneLists[i].coordinates().unscaledY +  " to 0.5");
					validGeneLists[i].coordinates().unscaledY  = (float)0.5;
					System.out.println("unscaledY " + validGeneLists[i].coordinates().unscaledY);
				}
				
				int highCorrelationCount = 0;
				
				float highestCorrelation = 0;
				highestCorrelation = highCorrelationThreshold;
				/** This may slow it down way too much, and maybe there is a way of storing this rather than checking it each time?? 
				 * but at the moment if there are 2 gene lists that are 60% correlated but aren't really correlated with other sets,
				 * they don't really come together.
				 */
				// I don't think that this helps
			/*	HIGH_CORRELATION_LOOP: for (int j = 0; j < validGeneLists.length; j++){
					
					if(continueCalculating == false){
						break LOOP;
					}
					
					if(j != i){	
						
						float temporaryCorrelation = validGeneLists[i].getCorrelation(validGeneLists[j]);
						
						if (temporaryCorrelation > highCorrelationThreshold){
							highestCorrelation = highCorrelationThreshold;
							break HIGH_CORRELATION_LOOP;
						}
						
						else if(temporaryCorrelation > highestCorrelation){
							
							highestCorrelation = temporaryCorrelation;
						}
					}
				
				}	
				*/
				
				/** loop through all the other gene lists to compare i to and work out where we want to move i to */
				for (int j = 0; j < validGeneLists.length; j++){
															
					if(continueCalculating == false){
						break LOOP;
					}
					
					if(j != i){	
						
						/** We're adding an extra iteration here as if we have a category that has a ton of others which are all subsets,
						 * it goes mad and ends up breaking		
						 */
						//if(validGeneLists[i].getCorrelation(validGeneLists[j]) > highCorrelation){
						if(validGeneLists[i].getCorrelation(validGeneLists[j]) >= highestCorrelation){	
							
							highCorrelationCount += 1;
						}
					}
				
				}	
				
				float highCorrelationMovementFactor = 1;
				
				if(highCorrelationCount > 0){
					
								
					float highCorrelationProportion = (float)highCorrelationCount/validGeneLists.length;													
					
					if((highCorrelationCount == 1 && validGeneLists.length > 10) || highCorrelationProportion < 0.001) {
						highCorrelationMovementFactor = 30 * highestCorrelation;
						
					}
					else if(highCorrelationProportion < 0.005){
						highCorrelationMovementFactor = 20 * highestCorrelation;
						
					}
					else if(highCorrelationProportion < 0.01){
						highCorrelationMovementFactor = 10 * highestCorrelation;
						
					}
					
					else if(highCorrelationProportion < 0.05){
						highCorrelationMovementFactor = 5 * highestCorrelation;
						
					}
					else if(highCorrelationProportion < 0.1){
						highCorrelationMovementFactor = 2 * highestCorrelation;
						
					}
				}
				
				
				for (int j = 0; j < validGeneLists.length; j++){
					
					if(continueCalculating == false){
						break LOOP;
					}
					
					if(j != i){	
						/** 
						 * move closer or further away if the correlation is greater than minCorrelation. MinCorrelation is the minimum correlation that we care about.
						 *   
						 *  circles with no correlation only need to be minDistance away, not as far away as 1 as this distorts the view, but not too close 
						 *  
						 *  Is it right to say OR when the correlation is less than minCorrelation AND the difference is greater than minDistance?? Apparently so - it all goes wrong if that is removed.
						 */	
						
						
						/** The correlation between the 2 gene lists */
						float correlation = validGeneLists[i].getCorrelation(validGeneLists[j]);
						
						/** The difference in the x coordinates between gene list i and j */ 
						float distanceX = validGeneLists[i].coordinates().unscaledX -  validGeneLists[j].coordinates().unscaledX;
						
						/** The difference in the y coordinates between gene list i and j */
						float distanceY =  validGeneLists[i].coordinates().unscaledY -  validGeneLists[j].coordinates().unscaledY;
						 
						/** The distance between the 2 gene lists */
						float actualDistance = (float)Math.sqrt((distanceX * distanceX) + (distanceY * distanceY)); 													
						
						/** The difference between the actual distance and the ideal distance */
						float difference = (1 - correlation) - actualDistance;
						
						thisTotalDifference += Math.abs(difference);
											
						
						if((correlation > minCorrelation) || ((correlation <= minCorrelation)&& (Math.abs(difference)>minDistance))){														
							
							/** 
							 * If difference between actual location and desired location is greater than a set value (diffThreshold) then we want to move.  
							 * 
							 */
							float movement;
							
							if(correlation < 0.1){

								movement = Math.abs((float)0.01*difference*coolingOff);
							}
							else if(correlation >= highestCorrelation){	
								/** when 2 lists are exactly the same, they sometimes struggle to come together completely when there are 
								lots of categories - they get there eventually but the pull between the 2 isn't strong enough. We can't just
								up the movement factor or it all goes wrong.
								 **/
								movement = Math.abs(highCorrelationMovementFactor*difference*coolingOff);
							}
							else{						
								
								movement = Math.abs(correlation*correlation*difference*coolingOff);
								
							}								
						
						/*	if(validGeneLists[j].getFunctionalSetInfo().description().startsWith("http://www.wikipathways.org/instance/WP458") && validGeneLists[i].getFunctionalSetInfo().description().startsWith("HALLMARK_EPITHELIAL_MESENCHYMAL_TRANSITION")){
								System.out.println("========================================");
								System.out.println("description =  " + validGeneLists[i].getFunctionalSetInfo().description());
								System.out.println("no of genes in set " + validGeneLists[i].getGenes().length);
								System.out.println("description =  " + validGeneLists[j].getFunctionalSetInfo().description());
								System.out.println("no of genes in set " + validGeneLists[j].getGenes().length);
								System.out.println("no of overlapping genes = " + validGeneLists[i].getOverlappingGenes(validGeneLists[j]).length);
								System.out.println("correlation = " + correlation);
								System.out.println("highestCorrelation = " + highestCorrelation);
								System.out.println("difference = " + difference);
								System.out.println("actual distance = " + actualDistance);
								System.out.println("highCorrelationCount = " + highCorrelationCount);
								System.out.println("highCorrelationMovementFactor = " + highCorrelationMovementFactor);	
								
								System.out.println("coolingOff = " + coolingOff);
								System.out.println("movement = " + movement);
								System.out.println("distanceX = " + distanceX);
								System.out.println("distanceY = " + distanceY);
							}
					/*		if(validGeneLists[j].getFunctionalSetInfo().description().startsWith("response to tumor necrosis") && validGeneLists[i].getFunctionalSetInfo().description().startsWith("cellular response to tumor necrosis")){
								System.out.println("========================================");
								System.out.println("description =  " + validGeneLists[i].getFunctionalSetInfo().description());
								System.out.println("correlation = " + correlation);
								System.out.println("difference = " + difference);
								System.out.println("actual distance = " + actualDistance);
								//System.out.println("difference = " + difference);
								System.out.println("movement = " + movement);
								System.out.println("distanceX = " + distanceX);
								System.out.println("distanceY = " + distanceY);
							}
							*/
							
							if((difference < -diffThreshold) || (correlation > 0.95 && difference < -0.01)){
							/** move closer */
								
								sumDiffX = sumDiffX - (distanceX * movement);
								sumDiffY = sumDiffY - (distanceY * movement);
								
								sumMoveCloser +=1;

							}
							//else if (difference > diffThreshold || (correlation > 0.95 && difference > 0.01)){
							else if (difference > (diffThreshold*2)){
							/** move further away */
								
								sumDiffX = sumDiffX + (distanceX * movement); 
								sumDiffY = sumDiffY + (distanceY * movement);
								
								sumMoveAway +=1;

			 				}
						}			
					}
				}
				float meanDiffX = sumDiffX/(float)(validGeneLists.length-1);
				float meanDiffY = sumDiffY/(float)(validGeneLists.length-1);	
								
				validGeneLists[i].coordinates().unscaledX += meanDiffX;
				validGeneLists[i].coordinates().unscaledY += meanDiffY;
				
			}	
			
			previousTotalDifference[totalDifferenceIndex] = thisTotalDifference;
			
			if(totalDifferenceIndex < numberToCheck-1){
				totalDifferenceIndex += 1; 
			}
			else {
				totalDifferenceIndex = 0;
			}
			
			/** If it's the first iteration, notify the app that the first coordinates are ready. */
			if (x == 1){				
				
				System.err.println("first iteration through calculate coordinates");
				/** notify app to create graph panel */
				//appPL.firstCoordinatesReady();
			}
						
			giraphApplication.getInstance().updateGraphPanel();
			
			/** We want to check whether we're still moving in the right direction, if not, stop if we're getting too few improving positions.
			 * or stop if the percentage difference is so small that it's not worth carrying on. */
			if (x%numberToCheck == 0){
								
				/** To check whether we're still moving in the right direction*/
				int improvingPositions = 0;
				float improvingMagnitude = 0;
				
				for (int i = 1; i < numberToCheck; i++){
					
					float diffDiff = previousTotalDifference[i-1] - previousTotalDifference[i];
					
					if(diffDiff > 0){
						improvingPositions++;
						improvingMagnitude = improvingMagnitude + diffDiff/previousTotalDifference[i];						
					}
				}
				
				if(coolingOff > 0.1){
					coolingOff *= 0.9;
				}
				
				if(x > numberToCheck *3 && x > 50){
					
					if(improvingMagnitude < 0.0001){	
						// stopCalculating
						continueCalculating = false;
						
						System.out.println("stopped calculating at x = " + x + " because...");
						System.out.println("no of improvingPositions " + improvingPositions);	
						System.out.println("improvingMagnitude " + improvingMagnitude);	
						System.out.println("sumMoveCloser =  " + sumMoveCloser);
						System.out.println("sumMoveAway =  " + sumMoveAway);						
						
						progressComplete(null);
					}					
				}
			}
			x++;		
		}		
	}
		
	public static void pause(int x){
		try{					 
			Thread.currentThread();
			Thread.sleep(x);				
		}
		catch(InterruptedException ie){
			//If this thread was interrupted by another thread
		}				
	}
	
	/** Add the main giraphApplication as a progress listener */
	public void addProgressListener(ProgressListener pl){
		if (!listeners.contains(pl)) {
			listeners.add(pl);
		}
	}
	
	/** Method inherited from stopPauseListener */
	public void updateValidGeneLists(){
		validGeneLists = geneListCollection.getValidGeneLists();
	}
	/** Method inherited from stopPauseListener */
	public void stopCalculating(){
		continueCalculating = false;
	}
	
	protected void progressCancelled () {
		Enumeration<ProgressListener>en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().progressCancelled();
		}
	}

	protected void progressUpdated (String message, int current, int max) {
		Enumeration<ProgressListener>en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().progressUpdated(message, current, max);
		}
	}

	protected void progressWarningReceived (Exception e) {
		Enumeration<ProgressListener>en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().progressWarningReceived(e);
		}
	}

	protected void progressExceptionReceived (Exception e) {
		Enumeration<ProgressListener>en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().progressExceptionReceived(e);
		}
	}

	protected void progressComplete (Object o) {
		Enumeration<ProgressListener>en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().progressComplete("calculate_coordinates", o);;
		}
	}

}	
