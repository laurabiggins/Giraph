package uk.ac.babraham.giraph.Displays.QC;

/**
 * This class takes a set of input values eg GC content values and makes a density plot  
 * The density values are calculated within this class. 
 * 
 * TODO: sort out the first and last data points. 
 * 
 * TODO: make the heights comparable between the subset and background. 
 * 
 */
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.ArrayList;

import javax.swing.JPanel;

import uk.ac.babraham.giraph.DataTypes.GeneCollection;
import uk.ac.babraham.giraph.Utilities.AxisScale;

public class DensityPlot extends JPanel implements Runnable{
	
	private GeneCollection subsetGenes;
	private GeneCollection backgroundGenes;
	
	// these are raw coordinates that are smoothed at time of plotting 
	private double [][] rawSubsetCoordinates;
	private double [][] rawBackgroundCoordinates;
	
	/** The min value x. */
	private double minValueX = 0;

	/** The max value x. */
	private double maxValueX = 1;

	/** The min value y. */
	private double minValueY = 0;

	/** The max value y. */
	private double maxValueY = 1;
	
	private static final int X_AXIS_SPACE = 50;
	private static final int Y_AXIS_SPACE = 30;
	
	//protected boolean isReady = false;
	protected int isReady = 0;
	protected String plotName = "plot";
	protected String xLabel= "";
	
	private int smoothingValue = 50;
	
	public DensityPlot(){
		
		repaint();
	}	
	
	public DensityPlot(GeneCollection subsetGenes, GeneCollection backgroundGenes){
		
		this.subsetGenes = subsetGenes;
		this.backgroundGenes= backgroundGenes; 
			
		Thread t = new Thread(this);
		t.start();
		repaint();
	}

		
	public void run(){
		
		repaint();
		
		calculateRawDensityValues();
				
	}
	
	// This needs to be specified in the child class eg get GC content
	/*protected double[] getSubsetData(GeneCollection subsetGenes){
		
		return null;
	}
	
	protected double[] getBackgroundData(GeneCollection backgroundGenes){
		
		return null;
	}
	*/
	
	protected double[] getData(GeneCollection geneCollection){
		
		return null;
	}
	
	private void calculateRawDensityValues(){
		
		double [][] subsetXBinData = createXBins(getData(subsetGenes), 2);
		double [] subsetBinXValues = subsetXBinData[0];
		double [] subsetBinAreas = subsetXBinData[1];
		
		double [][] backgroundXBinData = createXBins(getData(backgroundGenes), 2);
		double [] backgroundBinXValues = backgroundXBinData[0];
		double [] backgroundBinAreas = backgroundXBinData[1];
		
		double backgroundBinAreaSum = 0;
		for(int i=0; i<backgroundBinAreas.length; i++){
			backgroundBinAreaSum += backgroundBinAreas[i];
		}
		
		double subsetBinAreaSum = 0;
		for(int i=0; i<subsetBinAreas.length; i++){
			subsetBinAreaSum += subsetBinAreas[i];
		}
		
		double adjustmentFactor = backgroundBinAreaSum/subsetBinAreaSum;
		System.err.println("adjustment factor = " + adjustmentFactor);
		
		// apply correction factor
		for(int i=0; i<subsetBinAreas.length; i++){
			subsetBinAreas[i] *= adjustmentFactor;
		}
		
		rawSubsetCoordinates = getDensityValues(subsetBinXValues, subsetBinAreas);
		
		rawBackgroundCoordinates = getDensityValues(backgroundBinXValues, backgroundBinAreas);
		
		revalidate();
		repaint();

	}
	
	// convert slider value to usable window 
	public void updateSmoothingWindow(int sliderValue){
		
		smoothingValue = sliderValue;
		if(smoothingValue == 0){
			smoothingValue = 1;
		}		
		repaint();
	}
	
	// we don't want to smooth by the same number of data points as the background data set may be much larger than the subset.
	private int getSmoothingValue(int noOfDataPoints){
		
		float smoothingWindow = ((float)(smoothingValue)/100) * noOfDataPoints;
				
		//System.err.println("smoothing window = " + smoothingWindow);
		return (int)smoothingWindow;		
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
		for (int i=1; i<values2.length; i++){
			if(values2[i] > max){
				max=values2[i];
			}
		}
		//System.out.println("max value = " + max);
		return max;
	}
	
	/**
	 * Gets the y.
	 * 
	 * @param value the value
	 * @return the y
	 */
	public int getY (double value) {
		double proportion = (value-minValueY)/(maxValueY-minValueY);

		int y = getHeight()-Y_AXIS_SPACE;

		y -= (int)((getHeight()-(10+Y_AXIS_SPACE))*proportion);

		return y;
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
	
	/** 
	 * this extracts every nth rawValue - the rawValues need to be sorted
	 * if ends of the bin are the same value, we go to the next and increment the n values
	 * 
	 * it's a 2D array as it contains the binArea and the x value
	 * 
	 * @param rawValues
	 */
	private double[][] createXBins(double [] rawValues, int n){
		
		ArrayList<Double> bins = new ArrayList<Double>();
		ArrayList<Double> binAreas = new ArrayList<Double>();
		
		// add the first value;
		bins.add(rawValues[0]);
		binAreas.add((double)0);
		double lastValidValue = rawValues[0];
		double binArea = n;
		
		// extract every nth value
		for (int i=n; i<rawValues.length; i+=n){
			
			if(rawValues[i] > lastValidValue){
				
				lastValidValue = rawValues[i]; 
				bins.add(lastValidValue);
				binAreas.add(binArea);
				// reset it to n in case it's been increased
				binArea=n;
			}
			// if 2 numbers are the same we need to increase the bin width
			else{				
				binArea +=n;
			}
		}
		double[][] binData = new double[2][bins.size()];
		
		// convert back to double
		for(int i=0; i<bins.size(); i++){
			binData[0][i] = bins.get(i);
			binData[1][i] = binAreas.get(i);
		}		
		return binData;
	}
	
	// The x centres are the x values we want to plot 
	private double[] getXCentres(double [] xBins){
		
		double [] xCentres = new double[xBins.length-1];
		
		for (int i=0; i<xCentres.length; i++){
			
			xCentres[i] = (xBins[i] + xBins[i+1])/2;
		}
		return xCentres;
	}
	
	/* 
	 * To get the density values
	 * n is no of raw values between each x value 
	 */
	private double[][] getDensityValues(double [] xBinValues, double [] binAreas){
		
		// the x values to use for plotting
		double[] xCentres = getXCentres(xBinValues);
		
		double[][] densityValues = new double[2][xCentres.length];
				
		for (int i=0; i<xBinValues.length-1; i++){
		
			double xDiff = xBinValues[i+1] - xBinValues[i];
			
			double area = binAreas[i+1];
			double width = xDiff;
						
			double height = area/width;
			
			// x value
			densityValues[0][i] = xCentres[i];
			// y value			
			if(height < 1){
				densityValues[1][i] = 0;
			}
			else{
				densityValues[1][i] = Math.log(height);
			}
		}
		
		isReady++;		
		return densityValues;
	}

	
	// this needs to add some extra values until the 1st y value gets down to 0, but we also need to add in the x values too 
	private double[] smoothData(double[] rawValues, int smoothingWindow){
		
		int window;
		
		// we want an even number either side - we're disregarding the actual data point
		if(smoothingWindow%2 != 0){
			window = smoothingWindow-1;
		}
		else if (smoothingWindow < 2){
			window = 2;
		}
		else window = smoothingWindow; 
			
		double [] smoothed = new double[rawValues.length];
		
		for (int i=0; i<smoothed.length; i++){
		
			double subsetSum = 0; 
			
			for (int j=0; j<window/2; j++){
			
				// get the lower numbers
				if(i - (window/2 - j) > 0){
					subsetSum += rawValues[i - (window/2-j)];
				}
				
				// get the higher values
				if(i + (window/2 - j) < rawValues.length){
					subsetSum += rawValues[i + (window/2-j)];
				}
			}
			// subsetSum should never be 0, right?! 
			smoothed[i] = subsetSum/window;
		}
		return smoothed;
	}
	
	public void paint (Graphics g) {
		
		super.paint(g);
		
		Graphics2D g2 = (Graphics2D) g;
		
		//g2.drawLine(...);   //thick
		
		g2.setColor(Color.WHITE);
		
		g2.fillRect(0, 0, getWidth(), getHeight());
		
		FontMetrics metrics = getFontMetrics(g.getFont());
				
		g2.setColor(Color.BLACK);

				
		if(isReady == 2){
		
			double [] backgroundXValues = rawBackgroundCoordinates[0];
			double [] backgroundYValues = smoothData(rawBackgroundCoordinates[1], getSmoothingValue(rawBackgroundCoordinates[1].length));
			double [] subsetXValues = rawSubsetCoordinates[0];
			double [] subsetYValues = smoothData(rawSubsetCoordinates[1], getSmoothingValue(rawSubsetCoordinates[1].length));
			
			minValueX = getMinValue(backgroundXValues, subsetXValues);
			maxValueX = getMaxValue(backgroundXValues, subsetXValues);
			minValueY = getMinValue(backgroundYValues, subsetYValues);
			maxValueY = getMaxValue(backgroundYValues, subsetYValues);			
			
			// X axis
			g2.drawLine(X_AXIS_SPACE, getHeight()-Y_AXIS_SPACE, getWidth()-10, getHeight()-Y_AXIS_SPACE);
	
			AxisScale xAxisScale = new AxisScale(minValueX, maxValueX);
			double currentXValue = xAxisScale.getStartingValue();
			while (currentXValue < maxValueX) {
				g2.drawString(xAxisScale.format(currentXValue), getX(currentXValue), getHeight()-(Y_AXIS_SPACE-(3+g.getFontMetrics().getHeight())));
				g2.drawLine(getX(currentXValue),getHeight()-Y_AXIS_SPACE,getX(currentXValue),getHeight()-(Y_AXIS_SPACE-3));
				currentXValue += xAxisScale.getInterval();
			}
	
			// Y axis
			g2.drawLine(X_AXIS_SPACE, 10, X_AXIS_SPACE, getHeight()-Y_AXIS_SPACE);
	
			AxisScale yAxisScale = new AxisScale(minValueY, maxValueY);
			double currentYValue = yAxisScale.getStartingValue();
			while (currentYValue < maxValueY) {
				g2.drawString(yAxisScale.format(currentYValue), 5, getY(currentYValue)+(g.getFontMetrics().getAscent()/2));
				g2.drawLine(X_AXIS_SPACE,getY(currentYValue),X_AXIS_SPACE-3,getY(currentYValue));
				currentYValue += yAxisScale.getInterval();
			}
	
			g2.setColor(Color.BLUE);
			g2.setStroke(new BasicStroke(2));
			
			for (int p=0;p<backgroundXValues.length-1;p++) {
	
				g2.drawLine(getX(backgroundXValues[p]),getY(backgroundYValues[p]),getX(backgroundXValues[p+1]),getY(backgroundYValues[p+1]));			
			}
	
			g2.setColor(Color.RED);
	
			for (int p=0;p<subsetXValues.length-1;p++) {
				
				g.drawLine(getX(subsetXValues[p]),getY(subsetYValues[p]),getX(subsetXValues[p+1]),getY(subsetYValues[p+1]));
			}
			g2.setColor(Color.black);
			// X label
			g2.drawString(xLabel,(getWidth()/2)-(metrics.stringWidth(xLabel)/2),getHeight()-3);		
		}
		else{
			g2.drawString("calculating...", getWidth()/2, getHeight()/2);
		}			
		// Y label
		//g.drawString(yStore.name(),X_AXIS_SPACE+3,15);
	}		
}	
	