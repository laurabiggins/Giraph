package uk.ac.babraham.giraph.Maths;

/** 
 * This class calculates the optimal positions of the genelists in relation to each other. 
 */


import uk.ac.babraham.giraph.DataParser.ProgressListener;
import uk.ac.babraham.giraph.DataTypes.GeneListCollection;
import uk.ac.babraham.giraph.DataTypes.GeneList;
import uk.ac.babraham.giraph.Utilities.StopPauseListener;


public class CalculateCoordinates implements Runnable, StopPauseListener {

	float shiftFactor = (float)0.5;
	boolean continueCalculating;
	private ProgressListener appPL;
	
	private GeneListCollection geneListCollection;
	
	// is this because we don't want to keep getting the valid genelists from the collection each time?
	private GeneList[] validGeneLists;
	
	int minNoOfImprovingPositions = 4;
	
	
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
	 * Getting the info required before calculating can start 
	 */
	/*private void initialSetUp(GeneListCollection geneListCollection){
		
		this.geneListCollection = geneListCollection;
		validGeneLists = geneListCollection.getValidGeneLists();
		if(validGeneLists.length < (minNoOfImprovingPositions * 4)){
			minNoOfImprovingPositions = (validGeneLists.length/4); 
		}
	}
*/	
	
	/**
	 * Run the calculations
	 */
	public void run(){	
		
		/** The minimum correlation that we care about - if they're not remotely correlated we don't mind where they are in relation to each other,
		 * they'll get pulled around enough by others that they are correlated with anyway... not necessarily true. */
		//float minCorrelation = (float)0.3;
		float minCorrelation = (float)0.0;
		
		/** If two circles are at an optimum distance (within threshold of the diffFactor) then they won't be moved. 
		 * Do not reduce this number or everything starts going around in circles if there aren't many gene lists. */
		float diffThreshold = (float)0.2;
		
		/**  x is a count and is used to control how often coordinates are updated etc */
		int x = 1;
		
		/** The circles do not want to be too far away from each other - if they try and get too far then they mess up the screen. */
		float minDistance = (float) 0.4;
		
		/** To work out when to stop calculating */
		float [] previousTotalDifference = new float [20];
		int totalDifferenceIndex = 0;
				
		LOOP: while (continueCalculating == true){
		
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
					System.out.println("changing unScaledX i " + validGeneLists[i].coordinates().unscaledX +  " to 0.5");
					validGeneLists[i].coordinates().unscaledX  = (float)0.5;
					System.out.println("unScaledX " + validGeneLists[i].coordinates().unscaledX);
				}
				if(Float.isNaN(validGeneLists[i].coordinates().unscaledY)){
					System.out.println("changing unScaledY i" + validGeneLists[i].coordinates().unscaledY +  " to 0.5");
					validGeneLists[i].coordinates().unscaledY  = (float)0.5;
					System.out.println("unscaledY " + validGeneLists[i].coordinates().unscaledY);
				}
				
				
				/** loop through all the other gene lists to compare i to and work out where we want to move i to */
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
						
						thisTotalDifference = thisTotalDifference + Math.abs(difference);
											
						
						if((correlation > minCorrelation) || ((correlation <= minCorrelation)&& (Math.abs(difference)>minDistance))){														
							
							/** 
							 * If difference between actual location and desired location is greater than a set value (diffThreshold) then we want to move.  
							 * 
							 */
							//float movement = (1-correlation)/actualDistance;
							float movement;
							
							if(correlation < 0.01){
								movement = (float)0.01/actualDistance;
							}
							else{
								movement = correlation/actualDistance;
							}								
							
						/*	if(validGeneLists[i].getTerm().startsWith("BIOLOGICAL OXIDATION")){
								System.out.println("========================================");
								System.out.println(validGeneLists[j].getTerm());
								System.out.println("difference = " + difference);
								System.out.println("actual distance = " + actualDistance);
								//System.out.println("difference = " + difference);
								System.out.println("movement = " + movement);
								System.out.println("distanceX = " + distanceX);
								System.out.println("distanceY = " + distanceY);
							}
						*/	
							if(difference < -diffThreshold){
							/** move closer */
								
								sumDiffX = sumDiffX - (distanceX * movement);
								sumDiffY = sumDiffY - (distanceY * movement);

							}
							else if (difference > diffThreshold){
							/** move further away */
								
								sumDiffX = sumDiffX + (distanceX * movement); 
								sumDiffY = sumDiffY + (distanceY * movement);

			 				}
						}			
					}
				}
				float meanDiffX = sumDiffX/(float)(validGeneLists.length-1);
				float meanDiffY = sumDiffY/(float)(validGeneLists.length-1);	
				
				float xShift = (float) (meanDiffX*shiftFactor);
				float yShift = (float) (meanDiffY*shiftFactor);				
				validGeneLists[i].coordinates().unscaledX = (float)(validGeneLists[i].coordinates().unscaledX + xShift); 
				validGeneLists[i].coordinates().unscaledY = (float)(validGeneLists[i].coordinates().unscaledY + yShift);	
				
			}	
			
			previousTotalDifference[totalDifferenceIndex] = thisTotalDifference;
			
			if(totalDifferenceIndex < 19){
				totalDifferenceIndex = totalDifferenceIndex +1; 
			}
			else {
				totalDifferenceIndex = 0;
			}
			
			/** If it's the first iteration, notify the app that the first coordinates are ready. */
			if (x == 1){
				
				/** notify app to create graph panel */
				appPL.firstCoordinatesReady();
			}
						
			appPL.updateGraphPanel();

			/** We want to check whether we're still moving in the right direction, if not, stop if we're getting too few improving positions.
			 * or stop if the percentage difference is so small that it's not worth carrying on. */
			if (x%20 == 0){
								
				/** To check whether we're still moving in the right direction*/
				int improvingPositions = 0;
				float improvingMagnitude = 0;
				
				for (int i = 1; i < 20; i++){
					
					float diffDiff = previousTotalDifference[i-1] - previousTotalDifference[i];
					
					if(diffDiff > 0){
						improvingPositions++;
						improvingMagnitude = improvingMagnitude + diffDiff/previousTotalDifference[i];						
					}
				}
				
				if((improvingPositions < 5) || (improvingMagnitude < 0.00001)){
					
					// stopCalculating
					continueCalculating = false;
					appPL.calculatingCoordinatesStopped();
					
					System.out.println("stopped calculating at x = " + x + " because...");
					System.out.println("no of improvingPositions " + improvingPositions);	
					System.out.println("improvingMagnitude " + improvingMagnitude);	
					
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
		this.appPL = pl;
	}
	/** Method inherited from stopPauseListener */
//	public void changeSpeed(int frequency){

//	}	
	/** Method inherited from stopPauseListener */
//	public void changeShiftFactor(float sf){
//		shiftFactor = sf;
//		System.out.println("shift factor: " + shiftFactor);
//	}
	/** Method inherited from stopPauseListener */
	public void updateValidGeneLists(){
		validGeneLists = geneListCollection.getValidGeneLists();
	}
	/** Method inherited from stopPauseListener */
	public void stopCalculating(){
		continueCalculating = false;
	}
}	
