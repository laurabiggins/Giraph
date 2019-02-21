package uk.ac.babraham.giraph.Displays;

/**
 * This class creates the display screen. 
 */

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import uk.ac.babraham.giraph.giraphApplication;
import uk.ac.babraham.giraph.DataTypes.GeneListCollection;
import uk.ac.babraham.giraph.DataTypes.GeneList;
import uk.ac.babraham.giraph.Filters.FilterListener;


public class GraphPanel extends JPanel implements FilterListener{
		
	private static final long serialVersionUID = 1L;
	
	/** The main application */
	giraphApplication application;
	
	/** The min and max values from calculate coordinates */
	private float minValueX;
	private float maxValueX;
	private float minValueY;
	private float maxValueY;
		
	/** The main result object */ 
	//Result dr;
	
	private GeneListCollection geneListCollection;
	
	/** Panel on the right that can display which genes are in a genelist */
	public GeneInfoPanel gip;
	
	/** All the genelists that are left after filtering */
	private GeneList [] validGeneLists;
	
	private boolean geneListSelected = false;
	
	/** circle that the mouse is hovering over */
	private GeneList currentSelectedGeneList;
		
	private boolean geneListClicked = false;
	
	/** circle currently clicked on by the user, we need this as well as the one above so that we can keep the annotation 
	 * showing. */
	private GeneList currentClickedGeneList; 
	
	/** Minimum correlation shown by lines */
	float minCorrelation = (float) 0.3;
	
	int minCircleSize = 15;
	
	boolean play = false;
	
	/** if a circle is being dragged we just display the lines for that circle */
	boolean draggingCircle = false;
	
	/** whether we're exporting the image (so that graphics2 can be turned off) */
	public boolean exportImage = false;
	
	/** 
	 * These are options that the user can toggle
	 * 
	 *	
	/** Whether to highlight the other genelists that are in the same cluster as the selected gene list*/ 
	boolean highlightCluster = false;
	
	/** whether to display all the goAnnotations/categories or not */	
	boolean goAnnotation = false;
	
	/** whether to display the lines connecting the circles */
	boolean lines = true;//false;
	
	/** whether the program is currently calculating i.e. whether calculateCoordinates is running - this is controlled by the stop or play buttons on the menu */
	boolean calculating = false;
	



	
	private float pvalueCutoff;
	
	//private int minGenesInCategory;
	
	private static int BORDER = 20; 
	
	
	public GraphPanel(GeneListCollection geneListCollection, giraphApplication app){	
		
		this.addMouseMotionListener(mldrag);
		this.addMouseListener(ml);
		this.addComponentListener(compAdapter);
	    this.application = app;
	    this.geneListCollection = geneListCollection;

		updateValidGeneLists();
	}
	
	/** 
	 * Used when rValue threshold has been adjusted on the menubar.
	 *  The clusters parameter is passed in from giraphApplication by using the method clusterPair.getValidClusters(menuBar.rValueCutoff())
	 *  This could be done when the clusters are first obtained but I'm going to leave it here for now.
	 */	
	public void setColoursForGeneLists(GeneList[][] clusters){
		
		if (clusters.length > 0){
			
			Color[] colours = createColours(clusters.length);
			
			for (int i = 0; i < clusters.length; i++){
				
				for (int j = 0; j < clusters[i].length; j++){
					
					// set the colour in the gene list object
					clusters[i][j].colour = colours[i];
				}	
			}	
		}	
	}
	
	/** 
	 * Used when coordinates have been updated form CalculateCoordinates
	 */
	public void coordinatesUpdated(){
		
		minValueX = getMinXCoordinate(validGeneLists); //getMinValue(dr.getUnscaledCoordinates()[0]);
		maxValueX = getMaxXCoordinate(validGeneLists);//getMaxValue(dr.getUnscaledCoordinates()[0]);
		minValueY = getMinYCoordinate(validGeneLists);//getMinValue(dr.getUnscaledCoordinates()[1]);
		maxValueY = getMaxYCoordinate(validGeneLists);//getMaxValue(dr.getUnscaledCoordinates()[1]);		
		
		refreshDisplay();						
	}
	
	
	/** Returns an array of n colours */	
	
/*	private Color[] createColours(int n){
		
		float increment;
		
		if(n > 1) {
			increment = 255/(n-1);
		}
		else{
			increment = 255; 
		}
		
		Color[] colours = new Color[n]; 
		
		for (int i = 0; i < n; i++){

			colours[i] = new Color((int)(255-(increment*i)), 0, (int)(increment*i));

		}		
		return colours;
	}
*/	
	private Color[] createColours(int n){
		
		Color[] colours = new Color[n]; 
		
		Color[] colours4 = {new Color(102, 102, 255), new Color(153, 0, 51), new Color(0, 153, 153), new Color(0, 0, 102)};
					
		for (int i = 0; i < n; i++){
		
			if(i < 4){
			
				colours[i] = colours4[i];
			}
			else{ 
				
				if(i < 100){
					colours[i] = hex2Rgb(indexcolors[i]);
				}
				else if(i < 200){
					colours[i] = hex2Rgb(indexcolors[i-100]);
				}
				else{
					colours[i] = hex2Rgb(indexcolors[(int)(i/(float)100)]);
				}
			}	
		}
		
		return colours;
	}	
	
		
		
		/*int noOfColours = n;
		if(n%2 == 1){
			noOfColours = n+1;
		}
		
		Color[] colours = new Color[noOfColours]; 
		
		Color[] colours4 = {new Color(102, 102, 255), new Color(153, 0, 51), new Color(0, 153, 153), new Color(0, 0, 102)};
		
		if(n <= 4){
			
			for (int i = 0; i < n; i++){
			
				colours[i] = colours4[i];
			}	
		}
		else{
			
			ArrayList<Color> colourArrayList = new ArrayList<Color>();
			
			float increment = 255/(n-1);
			
			for (int i = 0; i < n; i++){
				
				if(i < 4){
					colourArrayList.add(colours4[i]);
				}
				else{
					colourArrayList.add(new Color((int)(255-(increment*i)), 0, (int)(increment*i)));
				}
				//colours[i] = new Color((int)(255-(increment*i)), 0, (int)(increment*i));
			}
			colours = colourArrayList.toArray(new Color[0]);
		}
		
		
	/*	switch(n){
			case 1: colours[0] = new Color(102, 102, 255);
			case 2: {
				colours[0] = new Color(102, 102, 255);
				colours[1] = new Color(153, 0, 51);
			}
			case 3: {
				colours[0] = new Color(102, 102, 255);
				colours[1] = new Color(153, 0, 51);
				colours[2] = new Color(0, 153, 153);
			}
			case 4: {
				colours[0] = new Color(102, 102, 255);
				colours[1] = new Color(153, 0, 51);
				colours[2] = new Color(0, 153, 153);
				colours[4] = new Color(0, 0, 102);
			}
		}
		*/

	//}
	
	
	public void filtersUpdated(float pvalue){
		
		updateValidGeneLists();
		if((geneListClicked == true)  &&  (currentClickedGeneList.getValidity() == false)){
			geneListClicked = false;
		}
		updateFilterText(pvalue);
	}
	
	/** 
	 * Used when filters have been adjusted 
	 */		
	public void updateValidGeneLists(){
		
		validGeneLists = geneListCollection.getValidGeneLists();			
		refreshDisplay();
		
	}
	
	private void updateFilterText(float pvalue){
		
		pvalueCutoff = pvalue;
		
		System.out.println("p value < " + pvalueCutoff);
	}
	
	
	/** 
	 * whether to display GO annotation on the screen or not 
	 */
	
	public void updateGOAnnotation(){
		
		if (goAnnotation == true){
			goAnnotation = false;
		}
		else {
			goAnnotation = true; 
		}
	}

	
	/**
	 * Used when adding or removing the lines joining circles
	 */
	
	public void updateLines(){
		if (lines == false){
			lines = true;
		}
		else{
			lines = false;
		}
		refreshDisplay();
	}
	
	
	/** 
	 * Updated when play or stop is selected on the menu
	 * TODO: what is this used for?
	 */	
	public void updateCalculatingStatus(boolean calculatingStatus){
	
		calculating = calculatingStatus;
		
		if(calculating == true){
			
			refreshDisplay();
		}
		
		else{

			refreshDisplay();
		}
	}
	
		
	/**
	 *  To manually increase or decrease the size of the circles by using the buttons on the menu bar.
	 */

	public void refreshDisplay(){
		
		revalidate();
		repaint();					
		
	}
	
	
	/** 
	 * Set whether to highlight all the other genelists that are in the same cluster as the selected gene list 
	 */	
	public void updateHighlightCluster(){
	
		if(highlightCluster == false) {
			highlightCluster = true;
		}
		
		else {
			highlightCluster = false;
		}
	}
	
	
	/** 
	 * This is required because the coordinates need to be rescaled when the screen is resized.
	 *  I had to take the scaling out of the paint method as it was messing up the display. 
	 */
	ComponentAdapter compAdapter = new ComponentAdapter() {

	    public void componentResized(ComponentEvent e){
	    
	        refreshDisplay();
	    }
	};    
	
	
	/** 
	 * Enables the circles to be manipulated by the mouse. 
	 */	
	MouseMotionListener mldrag = new MouseMotionListener(){		
		
				
		/** displays functional term if mouse is over the circle */
		
		public void mouseMoved(MouseEvent e){	
				
			boolean circleSelected = isGeneListSelected(e.getX(), e.getY());
			
			if(circleSelected){
				refreshDisplay();
			}
		}		
	
		
		/** makes the circles draggable so they can be moved around with the mouse (but not if it's still calculating) */
		
		public void mouseDragged(MouseEvent e){	
			
			if (calculating == false){
					
				//check if a circle is selected
				if(currentClickedGeneList != null){	
				
					int newX = e.getX();
					int newY = e.getY();
					
					if((newX <= getXLimits()[1])&&(newX >= getXLimits()[0])&&(newY <= getYLimits()[1])&&(newY >= getYLimits()[0])) {	
						
						getValueFromX(newX);
						getValueFromY(newY);
						currentClickedGeneList.coordinates().unscaledX = getValueFromX(newX);
						currentClickedGeneList.coordinates().unscaledY = getValueFromY(newY);
						
						draggingCircle = true;
						
						refreshDisplay();
								
					}
				}
				
				// we only need colours to change if the circles are coloured by proximity
				if(application.getColouredByProximity()){
				
					application.calculateClusters();
				}	
			}
			else{
				JOptionPane.showMessageDialog(application,"You need to press stop before you can drag the circles", "cannot drag while calculating", JOptionPane.ERROR_MESSAGE);
			}
		}	 
	};
	
	
	public void deleteCircle(){
		
		if(currentClickedGeneList != null){
			
			currentClickedGeneList.setValidity(false);
			updateValidGeneLists();	
			geneListClicked = false;
			refreshDisplay();
		}
	}
	
	
	/** 
	 * identifies which circle/genelist has been selected (clicked on) and displays genes from gene list in gene info panel
	 */	
	MouseListener ml = new MouseAdapter(){
						
		public void mousePressed(MouseEvent e){
			
			if (isGeneListSelected(e.getX(), e.getY())){
			
				geneListClicked = true;
				currentClickedGeneList = currentSelectedGeneList;
				
				if(gip != null){
				
					gip.setGeneListInfo(currentClickedGeneList, 0, currentClickedGeneList.colour);
				}	
			}
			
			else {
				geneListClicked = false;
			}
			refreshDisplay();
		} 
		
		public void mouseReleased(MouseEvent e){
			
			draggingCircle = false;
		}
		
		public void mouseClicked(MouseEvent e){
			
			if ((e.getClickCount() == 2) && (geneListClicked == true)) {
				
				gip = new GeneInfoPanel(application);
				gip.setGeneListInfo(currentClickedGeneList, 0, currentClickedGeneList.colour);
	    
			}			
		}
	};	  
		
	
	/** Returns a boolean to say whether the mouse is over a circle or not
	 * and sets the currentSelected GeneList */
	
	private boolean isGeneListSelected(int firstClickX, int firstClickY) {	
		
		// This counts backwards to get the gene list that is on the top
		for (int i = validGeneLists.length-1; i >= 0; i--){	

			int diameter = getDiameterOfCircle(validGeneLists[i]);
					
			int x0 = getX0(validGeneLists[i].coordinates().unscaledX, diameter);
			int y0 = getY0(validGeneLists[i].coordinates().unscaledY, diameter);
			
			if((firstClickX < (x0 + diameter)) && (firstClickX > x0) && (firstClickY < (y0 +diameter)) && (firstClickY > y0)){	 
				currentSelectedGeneList = validGeneLists[i];
				
				geneListSelected = true;
				return true;
			}
		}

		geneListSelected = false;
		return false;	
	}
		

	//Gets the calc coordinate y value from the screen y value.
	public float getValueFromY (int y) {
		
		float proportion = ((float)(y - getYLimits()[0])/ (float)(getYLimits()[1] - getYLimits()[0]));
		
		float value = proportion * (float)(maxValueY-minValueY) + minValueY;
		
		return value;
	}


	// Gets the calc coordinate x value from the screen x value.
	public float getValueFromX (int x) {

		float proportion = ((float)(x - getXLimits()[0])/ (float)(getXLimits()[1] - getXLimits()[0]));
		float unScaled = maxValueX-minValueX;
		
		float value = proportion * unScaled + minValueX;
		
		return value;
	}

	// The scaled x0 value for the circle
	private int getX0(float value, float diameter) {
		
		float proportion = (value-minValueX)/(maxValueX-minValueX);
		int [] xLimits = getXLimits();
		
		// xCentre that may go off screen
		int x = (int)((xLimits[1] - xLimits[0]) * proportion) + xLimits[0]; 
		
		int x0 = (int)(x-((float)(diameter)/2));
		
		if(x0 < xLimits[0]){
			x0 = xLimits[0];
		}
		if(x0+diameter > xLimits[1]){
			x0 = (int)(xLimits[1] - diameter);
		}
		return x0;
	}

	// The scaled x0 value for the circle
	private int getY0(float value, float diameter) {
		
		float proportion = (value-minValueY)/(maxValueY-minValueY);
		int [] yLimits = getYLimits();
		
		// yCentre that may go off screen
		int y = (int)((yLimits[1] - yLimits[0]) * proportion) + yLimits[0]; 
		
		int y0 = (int)(y-((float)(diameter)/2));
		
		if(y0 < yLimits[0]){
			y0 = yLimits[0];
		}
		if(y0+diameter > yLimits[1]){
			y0 = (int)(yLimits[1] - diameter);
		}
		return y0;
	}
	
	// get the XCentre screen position from the calcCoordinates output
	private int getXCentre(float value, float diameter){
		
		int x0 = getX0(value, diameter);
		return((int)(x0 + (diameter/2)));
	}
	
	// get the YCentre screen position from the calcCoordinates output
	private int getYCentre(float value, float diameter){
		
		int y0 = getY0(value, diameter);
		return((int)(y0 + (diameter/2)));
	}
	
	/* The x axis position in the window. We want it centred. */
	private int [] getXLimits(){
		
		int[] xlimits = new int[2];
		if(getWidth() > getHeight()){
			xlimits[0] = (getWidth() - getHeight())/2 + BORDER;
			xlimits[1] = xlimits[0] + getHeight() - BORDER;
		}	
		else{
			xlimits[0] = BORDER;
			xlimits[1] = getWidth() - BORDER;			
		}
		return xlimits;
	}	
	
	/* The x axis position in the window. We want it centred. */
	private int [] getYLimits(){
		
		int[] ylimits = new int[2];
		if(getHeight() > getWidth()){
			ylimits[0] = (getHeight() - getWidth())/2 + BORDER;
			ylimits[1] = ylimits[0] + getWidth() - BORDER;
		}	
		else{
			ylimits[0] = BORDER;
			ylimits[1] = getHeight() - BORDER;			
		}
		return ylimits;
	}
	
	
	private int getDiameterOfCircle(GeneList gl){
		
		float smallestDim;
		
		
		
		if(getYLimits()[1] < getXLimits()[1]){
			smallestDim = getYLimits()[1];
		}
		else{
			smallestDim = getXLimits()[1];
		}
		float maxCircleSize = smallestDim/8;
		
		//float diameter = (float)(((smallestDim/(float)(validGeneLists.length*1.5))*gl.getGenes().length)/2); 	
		float diameter = (float)(((smallestDim/(float)(validGeneLists.length*3))*gl.getGenes().length)/2); 	
	
		if ((diameter >= minCircleSize) && (diameter <= (maxCircleSize))) {
			return((int)diameter);
		}
		else if (diameter < minCircleSize){
			return(int)(minCircleSize);
		}
		// else if diameter is too big, set a maximum circle size 
		return((int)(maxCircleSize));
	}
	
	// returns min x coordinate
	private float getMinXCoordinate(GeneList[] gls){		
		
		float min = gls[0].coordinates().unscaledX;		
		for (int i = 1; i < gls.length; i++){ 
			if(gls[i].coordinates().unscaledX < min){
				min = gls[i].coordinates().unscaledX;
			}
		}
		return min;
	}	
	
	// returns max x coordinate
	private float getMaxXCoordinate(GeneList[] gls){		
		
		float max = gls[0].coordinates().unscaledX;		
		for (int i = 1; i < gls.length; i++){ 
			if(gls[i].coordinates().unscaledX > max){
				max = gls[i].coordinates().unscaledX;
			}
		}
		return max;
	}
	
	// returns min x coordinate
	private float getMinYCoordinate(GeneList[] gls){		
		
		float min = gls[0].coordinates().unscaledY;		
		for (int i = 1; i < gls.length; i++){ 
			if(gls[i].coordinates().unscaledY < min){
				min = gls[i].coordinates().unscaledY;
			}
		}
		return min;
	}	
	
	// returns max x coordinate
	private float getMaxYCoordinate(GeneList[] gls){		
		
		float max = gls[0].coordinates().unscaledY;		
		for (int i = 1; i < gls.length; i++){ 
			if(gls[i].coordinates().unscaledY > max){
				max = gls[i].coordinates().unscaledY;
			}
		}
		return max;
	}
	
	
	
	/* so we can set the minimum x and y values */
/*	private float getMinValue(float[]coordinates){		
		float min = coordinates[0];		
		for (int i = 1; i < coordinates.length; i++){ 
			if(coordinates[i] < min){
				min = coordinates[i];
			}
		}
		return min;
	}	
*/	
	/* so we can set the maximum x and y values */
/*	private float getMaxValue(float[]coordinates){		
		float max = coordinates[0];		
		for (int i = 1; i < coordinates.length; i++){ 
			if(coordinates[i] > max){
				max = coordinates[i];
			}
		}	
		return max;
	}
*/	
	public void paint(Graphics g){
		
		super.paint(g);	
		
		// if we're exporting the image we need to not be using Graphics2
		if(exportImage == false){
			Graphics2D g2 = (Graphics2D)g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g = g2;
		}
		
		FontMetrics fm = g.getFontMetrics();
		
		g.drawString("FILTERS", 15, (getHeight() - 45));
		//g.drawString("=======", 15, (getHeight() - 45));
		g.drawString(new String("p value < " + pvalueCutoff), 15, (getHeight() - 15));		
		
		int adjust = 5; // this was diameter*0.1 but it seems to be ok with a fixed value  		
		
		/** draw each line joining the circles (though this can be turned off) 
		 * This takes ages when there are lots of circles, it's not the calculations, it's just the redrawing of all of the lines
		 * as there are obviously many more lines than circles.
		 *  */	
		if ((lines == true) && (draggingCircle == false)) {
				
			for (int i = 0; i < validGeneLists.length; i++){	
					
				for (int j = i+1; j < validGeneLists.length; j++){
										
					float correlation = validGeneLists[i].getCorrelation(validGeneLists[j]);
					
					if (correlation > minCorrelation){	
						
						float saturation = 1 - ((correlation));
						
						if (saturation < 0.1){
							saturation = (float) 0.1;
						}
						
						Color lineColour = new Color(saturation, saturation, saturation);
							
						g.setColor(lineColour);

						g.drawLine(getXCentre(validGeneLists[i].coordinates().unscaledX, getDiameterOfCircle(validGeneLists[i])), getYCentre(validGeneLists[i].coordinates().unscaledY, getDiameterOfCircle(validGeneLists[i])),
								getXCentre(validGeneLists[j].coordinates().unscaledX, getDiameterOfCircle(validGeneLists[j])), getYCentre(validGeneLists[j].coordinates().unscaledY, getDiameterOfCircle(validGeneLists[j])));
						
					}	
				}
			}	
		}	
		
		// just paint the lines for the circle being dragged otherwise it takes ages to keep repainting all the lines
		if(draggingCircle == true){
			
			for (int i = 0; i < validGeneLists.length; i++){	
				
				float correlation = currentSelectedGeneList.getCorrelation(validGeneLists[i]);
				
				if (correlation > minCorrelation){	
					
					float saturation = 1 - ((correlation));
					
					if (saturation < 0.1){
						saturation = (float) 0.1;
					}
					
					Color lineColour = new Color(saturation, saturation, saturation);
						
					g.setColor(lineColour);		
					
					g.drawLine(getXCentre(currentSelectedGeneList.coordinates().unscaledX, getDiameterOfCircle(currentSelectedGeneList)), getYCentre(currentSelectedGeneList.coordinates().unscaledY, getDiameterOfCircle(currentSelectedGeneList)), 
							getXCentre(validGeneLists[i].coordinates().unscaledX, getDiameterOfCircle(validGeneLists[i])), getYCentre(validGeneLists[i].coordinates().unscaledY, getDiameterOfCircle(validGeneLists[i])));
				}				
			}
		}
				
		
		/**  draw each circle/genelist */	
		
		for (int i = 0; i < validGeneLists.length; i++){
			
			GeneList gl = validGeneLists[i];
			
			int diameter = getDiameterOfCircle(gl);

			int x0 = getX0(gl.coordinates().unscaledX, diameter);
			int y0 = getY0(gl.coordinates().unscaledY, diameter);				
			
			g.setColor(gl.colour);
			
			g.fillOval(x0, y0, diameter, diameter);
			
			// draw a border round the circle so it can be distinguished from neighbours 			
			g.setColor(Color.white);
					
			g.drawOval(x0, y0, diameter, diameter);		
			
			// draw ring around clusters 
			if (highlightCluster == true){
				
				if (currentClickedGeneList.colour.equals(gl.colour)){
					
					g.setColor(gl.colour);
											
					g.drawOval((int)(x0 - adjust), (int)(y0 - adjust), (int)((diameter + (adjust*2)+1)),
							(int)((diameter)+ (adjust*2)+1));												
				}
			}		
		}	
		
		if (goAnnotation == true){
		
			for (int i = 0; i < validGeneLists.length; i++){
				
				GeneList gl = validGeneLists[i];
				int xCentre = getXCentre(gl.coordinates().unscaledX, getDiameterOfCircle(gl));
				int yCentre = getYCentre(gl.coordinates().unscaledY, getDiameterOfCircle(gl));		
								
				g.setColor(Color.white);
				g.fillRoundRect((int)(xCentre) -2, (int)(yCentre) -10, fm.stringWidth(gl.getFunctionalSetInfo().description())+5, 12, 4, 4);
				g.setColor(Color.black);
				g.drawString(gl.getFunctionalSetInfo().description(), (int)(xCentre), (int)(yCentre));				
			}
		}
			
		
		/**
		 *  display functional term and highlight the selected circle
		 */	
		if(geneListClicked){
			
			int diameter = getDiameterOfCircle(currentClickedGeneList);
			int xCentre = getXCentre(currentClickedGeneList.coordinates().unscaledX, diameter);
			int yCentre = getYCentre(currentClickedGeneList.coordinates().unscaledY, diameter);
			int x0 = (int)(xCentre-((float)(diameter)/2));
			int y0 = (int)(yCentre-((float)(diameter)/2));
			
			
			g.setColor(currentClickedGeneList.colour);
			g.drawOval((int)(x0-adjust), (int)(y0-adjust), (int)(diameter+ (adjust*2)+1), (int)(diameter+ (adjust*2)+1));		
						
			if (goAnnotation == false){
				
				g.setColor(Color.white);
				g.fillRoundRect(xCentre -2 , yCentre-10, fm.stringWidth(currentClickedGeneList.getFunctionalSetInfo().description())+5, 12, 4,4);
				g.setColor(Color.black);
				g.drawString(currentClickedGeneList.getFunctionalSetInfo().description(), xCentre, yCentre);
			}
		}
		
		/** We've got geneListSelected as well as geneListClicked so that 2 terms can be displayed at the same time,
		 *  one that's been clicked on and one that's moused over.
		 */
		if(geneListSelected){
			
			int diameter = getDiameterOfCircle(currentSelectedGeneList);
			int xCentre = getXCentre(currentSelectedGeneList.coordinates().unscaledX, diameter);
			int yCentre = getYCentre(currentSelectedGeneList.coordinates().unscaledY, diameter);
			int x0 = (int)(xCentre-((float)(diameter)/2));
			int y0 = (int)(yCentre-((float)(diameter)/2));
			
			g.setColor(currentSelectedGeneList.colour);
			
			g.drawOval((int)(x0-adjust), (int)(y0-adjust), (int)(diameter+ (adjust*2)+1), (int)(diameter+ (adjust*2)+1));
			
			if (goAnnotation == false){
				
				g.setColor(Color.white);
				g.fillRoundRect(xCentre -2 , yCentre-10, fm.stringWidth(currentSelectedGeneList.getFunctionalSetInfo().description())+5, 12, 4,4);
				g.setColor(Color.black);
				g.drawString(currentSelectedGeneList.getFunctionalSetInfo().description(), xCentre, yCentre);
			}
		}
	}
	
	/**
	 *  Used in giraphMenuBar to display current info
	 */
	public float getMinCorrelation(){
		return minCorrelation;
	}
	
	/**
	 * Increase or decrease the minimum correlation shown by the lines - these could be removed
	 */	
	public void decreaseMincorrelation(){
		if (minCorrelation > 0.05){
			minCorrelation = (float) (minCorrelation - 0.05);
			System.out.println("min correlation shown by lines: " + minCorrelation);
			refreshDisplay();
		}
	}
	
	public void increaseMincorrelation(){
		if (minCorrelation <= 0.95){
			minCorrelation = (float) (minCorrelation + 0.05);
			System.out.println("min correlation shown by lines: " + minCorrelation);			
			refreshDisplay();
		}	
	}
	
	private static final String[] indexcolors = new String[]{
	        "#000000", "#FFFF00", "#1CE6FF", "#FF34FF", "#FF4A46", "#008941", "#006FA6", "#A30059",
	        "#FFDBE5", "#7A4900", "#0000A6", "#63FFAC", "#B79762", "#004D43", "#8FB0FF", "#997D87",
	        "#5A0007", "#809693", "#FEFFE6", "#1B4400", "#4FC601", "#3B5DFF", "#4A3B53", "#FF2F80",
	        "#61615A", "#BA0900", "#6B7900", "#00C2A0", "#FFAA92", "#FF90C9", "#B903AA", "#D16100",
	        "#DDEFFF", "#000035", "#7B4F4B", "#A1C299", "#300018", "#0AA6D8", "#013349", "#00846F",
	        "#372101", "#FFB500", "#C2FFED", "#A079BF", "#CC0744", "#C0B9B2", "#C2FF99", "#001E09",
	        "#00489C", "#6F0062", "#0CBD66", "#EEC3FF", "#456D75", "#B77B68", "#7A87A1", "#788D66",
	        "#885578", "#FAD09F", "#FF8A9A", "#D157A0", "#BEC459", "#456648", "#0086ED", "#886F4C",
	        "#34362D", "#B4A8BD", "#00A6AA", "#452C2C", "#636375", "#A3C8C9", "#FF913F", "#938A81",
	        "#575329", "#00FECF", "#B05B6F", "#8CD0FF", "#3B9700", "#04F757", "#C8A1A1", "#1E6E00",
	        "#7900D7", "#A77500", "#6367A9", "#A05837", "#6B002C", "#772600", "#D790FF", "#9B9700",
	        "#549E79", "#FFF69F", "#201625", "#72418F", "#BC23FF", "#99ADC0", "#3A2465", "#922329",
	        "#5B4534", "#FDE8DC", "#404E55", "#0089A3", "#CB7E98", "#A4E804", "#324E72", "#6A3A4C",
	        "#83AB58", "#001C1E", "#D1F7CE", "#004B28", "#C8D0F6", "#A3A489", "#806C66", "#222800",
	        "#BF5650", "#E83000", "#66796D", "#DA007C", "#FF1A59", "#8ADBB4", "#1E0200", "#5B4E51",
	        "#C895C5", "#320033", "#FF6832", "#66E1D3", "#CFCDAC", "#D0AC94", "#7ED379", "#012C58"
	};
	public static Color hex2Rgb(String colorStr) {
	    return new Color(
	            Integer.valueOf( colorStr.substring( 1, 3 ), 16 ),
	            Integer.valueOf( colorStr.substring( 3, 5 ), 16 ),
	            Integer.valueOf( colorStr.substring( 5, 7 ), 16 ) );
	}
}	
