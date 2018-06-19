package uk.ac.babraham.giraph.Displays.QC;

import java.awt.BorderLayout;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

// contains the density plot and slider for varying the smoothing degree

public class DensityPlotPanel extends JPanel implements ChangeListener{
	
	private DensityPlot densityPlot;
	//private JPanel densityPlot;
	private JPanel sliderPanel;
	private JSlider smoothingSliderBar;
	
	
	public DensityPlotPanel(){
		
		densityPlot = new DensityPlot();
				
		sliderPanel = new JPanel();
		sliderPanel.setLayout(new BorderLayout());
		
		smoothingSliderBar = new JSlider();
		smoothingSliderBar.setOrientation(JSlider.HORIZONTAL);
		smoothingSliderBar.setMajorTickSpacing(10);
		smoothingSliderBar.setPaintTicks(true);
		Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
		labelTable.put(new Integer(0), new JLabel("-"));
		labelTable.put(new Integer(100), new JLabel("+"));
		smoothingSliderBar.setLabelTable(labelTable);
		smoothingSliderBar.setPaintLabels(true);
		smoothingSliderBar.setSnapToTicks(false);
		smoothingSliderBar.setName("smoothingSliderBar");
		smoothingSliderBar.addChangeListener(this);
		
		sliderPanel.add(smoothingSliderBar, BorderLayout.CENTER);
		smoothingSliderBar.addChangeListener(this);
		
		//setSize(100,250);
		
		this.setLayout(new BorderLayout());
		this.add(densityPlot, BorderLayout.CENTER);
		this.add(sliderPanel, BorderLayout.PAGE_END);
		
		setVisible(true);
	}

	public void setDensityPlot(DensityPlot dp){
		
		this.remove(densityPlot);
		
		this.densityPlot = dp;
		this.add(densityPlot, BorderLayout.CENTER);
	}
	
	public void stateChanged(ChangeEvent ce) {

		int fps = smoothingSliderBar.getValue();
		densityPlot.updateSmoothingWindow(fps);
		//System.out.println("fps = " + fps);
	//	application.adjustClusterStringency(fps/100);		
	}
	
}
