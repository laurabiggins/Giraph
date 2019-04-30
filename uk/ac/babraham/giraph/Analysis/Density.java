package uk.ac.babraham.giraph.Analysis;

import java.util.Arrays;

public class Density implements Runnable{
	
	private double[] rawValues; 
	
	private double[] xValues;
	
	private double[] yValues;
	
	int smoothingWindow = 5;
	
	
	
	public Density(double [] rawValues, int noOfPointsToPlot){
		
		this.rawValues = rawValues;
		//this.smoothingWindow = smoothingWindow;
		//this.xValues = new double[(int)(rawValues.length/smoothingWindow)];
		//this.yValues = new double[(int)(rawValues.length/smoothingWindow)];
		System.out.println("no of points to plot " + noOfPointsToPlot);
		this.xValues = new double[noOfPointsToPlot];
		this.yValues = new double[noOfPointsToPlot];
		
		Arrays.sort(rawValues);

		Thread t = new Thread(this);
		t.start();
	}

	public void run(){
	
		setXValues();		
	}
	
	public double[] xValues(){
		return xValues;
	}
	
	public double[] yValues(){
		return yValues;
	}
	
	// change this
	// This should set x values that are spaced out evenly between min and max values
	// rawValues has been sorted
	private void setXValues(){
	
		// this might need adjusting slightly to get the last values right
		double increment = (rawValues[rawValues.length-1] - rawValues[0]) / xValues.length; 
		
		xValues[0] = rawValues[0];
		double currentXValue = rawValues[0];
		System.out.println("xvalue[0] = " + xValues[0]);
		System.out.println("increment = " + increment);
		System.out.println("rawValues[rawValues.length-1]" + rawValues[rawValues.length-1]);
		yValues[0] = 0;
		
		for (int i=1; i<xValues.length; i++){
						
			currentXValue += increment;
			xValues[i] = currentXValue;
			
			yValues[i] = getYValue(rawValues, xValues[i]-(increment/2), xValues[i]+(increment/2));
			System.out.println("yValue = " + yValues[i]);
		
		}
	
	}	
	
	// change this - we need to be finding out how many values are greater than value 1 and how many are less than value 2
	private double getYValue(double[] allValues, double value1, double value2){
		
		int count = 0;
		System.out.println("length allValues = " + allValues.length);
		System.out.println("value1 = " + value1);
		System.out.println("value2 = " + value2);
		
		// get the number of values that are greater than value1 and less than value2 
		for(int i=0; i<allValues.length; i++){
			
			if (allValues[i] >= value2){
				return count;
			}
			
			else if((allValues[i] >= value1) && (allValues[i] < value2)){
				count++;
			}
		}
		
		
	/*	for(int i=allValues.length; allValues[i] >= value1; i--){			
			count++;
		}
				
		for(int i=index1+1; i < allValues.length && allValues[i] <= allValues[index2]; i++){						
			count++;
		}
		
	*/	//System.out.println("count = " + count);
		
		return ((double)count)/allValues.length;
		// although the values are sorted, there may be more that have the same value
				
	}
}

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	