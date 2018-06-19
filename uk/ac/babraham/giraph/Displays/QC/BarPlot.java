package uk.ac.babraham.giraph.Displays.QC;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.JPanel;

import uk.ac.babraham.giraph.DataTypes.Gene;
import uk.ac.babraham.giraph.DataTypes.GeneCollection;
import uk.ac.babraham.giraph.Utilities.AxisScale;

public class BarPlot extends JPanel implements Runnable{
	
	
	GeneCollection subsetGenes;
	
	GeneCollection backgroundGenes;
	
	/** The min value x. */
	private double minValueX = 0;

	/** The max value x. */
	private double maxValueX = 1;

	/** The min value y. */
	private double minValueY = 0;

	/** The max value y. */
	private double maxValueY = 1;
	
	private boolean verticalLabels = false;
	
	private static final int X_AXIS_SPACE = 50;
	private static int Y_AXIS_SPACE = 10;
		
	protected double [] backgroundProportions;// =  new double[] {45, 50, 42, 30};
	protected double [] subsetProportions;// =  new double[] {55, 25, 52, 3};
	
	protected String [] categoryNames;// =  new String[] {"cat1", "category2", "name3", "n4"};
	private String longestCategoryName = "";
	
	protected String legendPosition = "right";
	
	public BarPlot(GeneCollection subsetGenes, GeneCollection backgroundGenes){
		
		this.subsetGenes = subsetGenes;
		this.backgroundGenes = backgroundGenes;
		
		setVisible(true);		
		Thread t = new Thread(this);
		t.start();

	}

	public void run(){
		
		categoryNames = getCategoryNames(backgroundGenes);
		orderCategoryNames();
		
		backgroundProportions = getData(backgroundGenes, categoryNames);
		subsetProportions = getData(subsetGenes, categoryNames);
		
		setMinMaxValues();
		setLongestCategoryName();
		
		
		revalidate();
		repaint();						
	}
	
	protected String getDataForGene(Gene gene){
		return null;
	}
	
	// this should just be done for the background
	protected String[] getCategoryNames(GeneCollection gc){
		
		Gene [] genes = gc.getAllGenes();
		
		HashSet<String> categoryNameHashset = new HashSet<String>();  
		
		for(int i=0; i<genes.length; i++){
		
			String categoryName = getDataForGene(genes[i]);
		
			if(categoryName == null){
				continue;
			}
			if(categoryNameHashset.contains(categoryName)){
				continue;
			}
			else{
				categoryNameHashset.add(categoryName);
			}
		}
		
		Object[] categoryNamesObject = categoryNameHashset.toArray();
		
		String [] categoryNames = new String[categoryNamesObject.length];
		
		for(int i=0; i<categoryNames.length; i++){
			categoryNames[i] = categoryNamesObject[i].toString();						
		}		
		return categoryNames;				
	}
	
	protected double[] getData(GeneCollection gc, String[] categoryNames){
		
		Gene [] genes = gc.getAllGenes();
		
		Map<String, MutableInt> categoryHashMap = new HashMap<String, MutableInt>();
		
		// populate the hash with the category names
		for(int i=0; i<categoryNames.length; i++){
			
			categoryHashMap.put(categoryNames[i], new MutableInt());			
		}
		
		// go through each gene
		for(int i=0; i<genes.length; i++){
			
			String category = getDataForGene(genes[i]);
			
			if(category == null){
				continue;
			}
				
			MutableInt count =  categoryHashMap.get(category);
				 
			if (count == null) {
				System.err.println("category  " +  category + " wasn't found");
			}
			else {
			    count.increment();
			}			
		}
		
		int [] categoryCounts = new int[categoryNames.length];					
		
		for (int i=0; i<categoryNames.length; i++){
			
			categoryCounts[i] = categoryHashMap.get(categoryNames[i]).value;
			
			//System.err.println(categoryNames[i] + ": " +  biotypeCounts[i]);
		}
		
		return getProportions(categoryCounts);		
	}
	
	// this is actually going to be percentages
	private double[] getProportions(int[] counts){
		
		int sum = 0;
		
		double[] proportions = new double[counts.length];
		
		// add up the counts
		for(int i=0; i<counts.length; i++){
			sum += counts[i];
		}
		// get the proportions/percentages
		for(int i=0; i<counts.length; i++){
			proportions[i] = ((double)(counts[i])/sum)*100;
		}		
		return proportions;
	}
	
	
	
	
	
	// so we know where to put the axes
	private void setLongestCategoryName(){
				
		for(int i=0; i<categoryNames.length; i++){
			
			if(categoryNames[i].length() > longestCategoryName.length()){
				
				longestCategoryName = categoryNames[i];
			}
		}				
	}
	

	
	private void setMinMaxValues(){
		
		maxValueX = subsetProportions.length + backgroundProportions.length;
		
		maxValueY = getMaxValue(subsetProportions, backgroundProportions);
		//System.err.println()
	}
	
	
	/**
	 *  To get the min value 
	 */
	public double getMinValue(double [] values1, double [] values2){
		double min = values1[0];
		for (int i=1; i<values1.length; i++){
			if(values1[i] < min){
				min=values1[i];
			}
		}
		for (int i=1; i<values2.length; i++){
			if(values2[i] < min){
				min=values2[i];
			}
		}
		//System.out.println("min value = " + min);
		return min;
	}
	/**
	 *  To get the max value 
	 */
	public double getMaxValue(double [] values1, double [] values2){
		double max = values1[0];
		for (int i=1; i<values1.length; i++){
			if(values1[i] > max){
				max=values1[i];
			}
		}
		for (int i=0; i<values2.length; i++){
			if(values2[i] > max){
				max=values2[i];
			}
		}
		System.out.println("max value = " + max);
		return max;
	}
	
	/**
	 * Gets the y.
	 * 
	 * @param value the value
	 * @return the y
	 */
	public int getY (double value) {
		//System.out.println("value = " + value);
		//System.out.println("max y value = " + maxValueY);
		double proportion = (value-minValueY)/(maxValueY-minValueY);
		//System.out.println("y proportion = " + proportion);
		
		int y = getHeight()-Y_AXIS_SPACE;
		//System.out.println("y1 = " + y);

		//System.out.println(" get height = " + getHeight());
		y -= (int)((getHeight()-(10+Y_AXIS_SPACE))*proportion);
		//System.out.println("y2 = " + y);
		
		return y;
	}

	protected void orderCategoryNames(){
		
	}
	
	/**
	 * Gets the x.
	 * 
	 * @param value the value
	 * @return the x
	 */
	public int getX (double value) {
		double proportion = (value-minValueX)/(maxValueX-minValueX);

		int x = X_AXIS_SPACE;

		x += (int)((getWidth()-(10+X_AXIS_SPACE))*proportion);

		return x;
	}	
	
	public static void drawRotate(Graphics2D g2d, double x, double y, int angle, String text){    
	    g2d.translate((float)x,(float)y);
	    g2d.rotate(Math.toRadians(angle));
	    g2d.drawString(text,0,0);
	    g2d.rotate(-Math.toRadians(angle));
	    g2d.translate(-(float)x,-(float)y);
	} 
	
	public void paint (Graphics g) {
		
		super.paint(g);
		Graphics2D g2 = (Graphics2D) g;
		
		// background
		g2.setColor(Color.WHITE);
		g2.fillRect(0, 0, getWidth(), getHeight());
		
		FontMetrics metrics = getFontMetrics(g.getFont());
		
		// determine width of bars
		int barWidth = (int)(((getWidth()-X_AXIS_SPACE)/maxValueX)*0.7);
		
		// if the plot area is wide enough, 
		if(barWidth*2 + 0.8 < metrics.stringWidth(longestCategoryName)){
			verticalLabels = true; 
			Y_AXIS_SPACE = metrics.stringWidth(longestCategoryName) + 20;
		}
		else{
			verticalLabels = false;
			Y_AXIS_SPACE = metrics.getHeight()*3;
		}
		
		// Y axis
		g2.setColor(Color.BLACK);
		int xAxisPos = (int) (X_AXIS_SPACE*0.9);
		g2.drawLine(xAxisPos, 10, xAxisPos, getHeight()-Y_AXIS_SPACE);

		AxisScale yAxisScale = new AxisScale(minValueY, maxValueY);
		double currentYValue = yAxisScale.getStartingValue();
		while (currentYValue < maxValueY) {
			g2.drawString(yAxisScale.format(currentYValue), 5, getY(currentYValue)+(g.getFontMetrics().getAscent()/2));
			g2.drawLine(xAxisPos,getY(currentYValue),xAxisPos-3,getY(currentYValue));
			currentYValue += yAxisScale.getInterval();
		}
				
		for (int i=0; i<backgroundProportions.length; i++){
			
			// plot the background bars
			int yValue = getY(backgroundProportions[i]);
			int xValue = getX(i*2);
			
			g2.setColor(new Color(0,153,153));
			g2.fillRect(xValue, yValue, barWidth, getHeight()-Y_AXIS_SPACE-yValue);
			
			g2.setColor(Color.BLACK);
			g2.drawRect(xValue, yValue, barWidth, getHeight()-Y_AXIS_SPACE-yValue);
			
			// plot the subset bars 
			yValue = getY(subsetProportions[i]);			
			int xValueSubset = getX((i*2)+0.8);

			g2.setColor(new Color(102,102,255));
			g2.fillRect(xValueSubset, yValue, barWidth, getHeight()-Y_AXIS_SPACE-yValue);
			
			g2.setColor(Color.BLACK);
			g2.drawRect(xValueSubset, yValue, barWidth, getHeight()-Y_AXIS_SPACE-yValue);
			
			
			// add the category labels
			if(verticalLabels == true){
				int yPos = getHeight()-(metrics.stringWidth(longestCategoryName)-metrics.stringWidth(categoryNames[i]))-10;  
				drawRotate(g2, xValueSubset, yPos, 270, categoryNames[i]);
			}
			else{
				int yPos = getHeight()- Y_AXIS_SPACE/3;
				int xPos = (int) xValue+barWidth + ((xValueSubset-(xValue+barWidth))/2) - (metrics.stringWidth(categoryNames[i])/2); 
				g2.drawString(categoryNames[i], xPos, yPos);
			}
		}
				
		// draw the key if the graph is big enough
		if(getWidth()>200){
		
			// background box
			int xCoordinate;
			int yCoordinate = 20;
			int boxWidth = getWidth()/40;
			int whiteBoxWidth = (int) (boxWidth*1.4 + metrics.stringWidth("background") +5);
			
			if(legendPosition.contains("left")){
				xCoordinate = 20;
			}
			else{
				xCoordinate = getWidth() - whiteBoxWidth;
			}
			
			// draw a white box in case we're drawing over the bars
			int whiteBoxHeight = (int) ((boxWidth*1.4)*2);
			g2.setColor(Color.white);
			g2.fillRect(xCoordinate-5, yCoordinate-5, whiteBoxWidth, whiteBoxHeight);
			
			// background box
			g2.setColor(new Color(0,153,153));
			g2.fillRect(xCoordinate, yCoordinate, boxWidth, boxWidth);
			
			g2.setColor(Color.BLACK);
			g2.drawRect(xCoordinate, yCoordinate, boxWidth, boxWidth); 
			
			int yTextPos = yCoordinate+(boxWidth/2) + (metrics.getHeight()/2);
			
			g2.drawString("background", (int)(xCoordinate+(boxWidth*1.4)), yTextPos);
			
			yCoordinate += (int)(boxWidth*1.4);
						
			// subset box
			g2.setColor(new Color(102,102,255));
			g2.fillRect(xCoordinate, yCoordinate, boxWidth, boxWidth);
			
			g2.setColor(Color.BLACK);
			g2.drawRect(xCoordinate, yCoordinate, boxWidth, boxWidth);
			
			yTextPos = yCoordinate+(boxWidth/2) + (metrics.getHeight()/2);
			
			g2.drawString("subset", (int)(xCoordinate+(boxWidth*1.4)), yTextPos);
		}	
	}			
		
/*	public void exportData (File file) throws IOException {
		
		PrintWriter pr = new PrintWriter(file);
		
		pr.println("Lower Bound\tUpper Bound\tCount");
		
		for (int c=0;c<categories.length;c++) {
			pr.println(categories[c].minValue+"\t"+categories[c].maxValue+"\t"+categories[c].count);
		}
			
		pr.close();
	}	
*/	
}

/** class to increment values in hash */

class MutableInt {
	int value = 0; // starting at 0 as some categories may be absent
	
	public void increment(){
		++value;
	}
	
	public int  get(){
		return value;
	}
}
