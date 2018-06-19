package uk.ac.babraham.giraph.Dialogs;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Hashtable;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.ac.babraham.giraph.giraphApplication;

/**
 * This has been taken from SeqMonk->Dialogs->DataZoomSelector
 * 
 * @author bigginsl
 *
 */

public class ClusterRValueSelector extends JDialog implements ActionListener, ChangeListener{
	
	private giraphApplication application;
	private JSlider slider;	
	//private Hashtable<Integer, JLabel> labels = null;
		
	/**
	 * Instantiates a new data zoom selector.
	 * 
	 * @param application
	 */
	public ClusterRValueSelector(giraphApplication application) {
		
		super(application,"Adjust clusters");
		this.application = application;	
		
		// We use custom labels since we want 200 positions on the
		// slider but the labels to run from 2-20 in increments of 2
	/*	if (labels == null) {
			labels = new Hashtable<Integer, JLabel>();
			
			for (int i=0;i<=20;i++) {
				if (i%2 == 0) {
					labels.put(new Integer(i), new JLabel(""+i));
				}
				else {
					labels.put(new Integer(i), new JLabel(""));
				}
			}
		}
		*/
		slider = new JSlider();
		slider.setOrientation(JSlider.VERTICAL);
		slider.setMajorTickSpacing(10);
		slider.setPaintTicks(true);
		Hashtable<Integer, JLabel> labelTable2 = new Hashtable<Integer, JLabel>();
		labelTable2.put( new Integer(0), new JLabel("-"));
		labelTable2.put( new Integer(100), new JLabel("+"));
		slider.setLabelTable(labelTable2);
		slider.setPaintLabels(true);
		slider.setSnapToTicks(false);
		slider.setName("slider");
		slider.setValue((int)(application.getRValue()*100));
		add(slider);
		slider.addChangeListener(this);
		getContentPane().add(slider,BorderLayout.CENTER);
		
		JButton closeButton = new JButton("Close");
		getRootPane().setDefaultButton(closeButton);
		closeButton.addActionListener(this);
		
		getContentPane().add(closeButton,BorderLayout.SOUTH);
		
		setSize(100,250);
		setLocationRelativeTo(application);
		setVisible(true);
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent ae) {
		setVisible(false);
		dispose();
	}


	
	public void stateChanged(ChangeEvent ce) {

		float fps = (float)slider.getValue();
		application.adjustClusterStringency(fps/100);		
	}

}
