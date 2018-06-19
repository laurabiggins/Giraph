package uk.ac.babraham.giraph.Displays.QC;


/** 
 * This contains the panel plot and the button panel
 * 
 */
import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import uk.ac.babraham.giraph.DataTypes.GeneCollection;

public class QCPanel extends JPanel implements ActionListener{
	
	private JButton gcButton;
	private JButton lengthButton;
	private JButton chrButton;
	private JButton transcriptNoButton;
	private JButton barPlotBiotypeButton;
	private JButton barPlotBiotypeFamilyButton;
	
	private JPanel plotPanel;
	private JPanel plotPanelComponent;
	
	private GeneCollection queryGenes;
	// this can be genomic or custom background genes
	private GeneCollection backgroundGenes;
	
	
	public QCPanel(GeneCollection queryGenes, GeneCollection backgroundGenes){
		
		this.queryGenes = queryGenes;
		this.backgroundGenes = backgroundGenes;
		
		JFrame frame = new JFrame();
		frame.setName("QC info");
		frame.setLayout(new BorderLayout());
		frame.setSize(500, 500);
		frame.setVisible(true);
		
		plotPanel = new JPanel();
		plotPanel.setLayout(new BorderLayout());
				
		frame.add(plotPanel, BorderLayout.CENTER); 
				
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(2,2,2,2);
		gbc.gridx=0;
		gbc.gridy=0;
		
		gcButton = new JButton("GC content");
		gcButton.addActionListener(this);
		gcButton.setActionCommand("plot_density_GC");
				
		//buttonPanel.add(gcButton, gbc);
		
		lengthButton = new JButton("length");
		lengthButton.addActionListener(this);
		lengthButton.setActionCommand("plot_density_length");
						
		gbc.gridy++;
		
		//buttonPanel.add(lengthButton, gbc);
		
		chrButton = new JButton("chr");
		chrButton.addActionListener(this);
		chrButton.setActionCommand("plot_chr");
						
		gbc.gridy++;
		
		buttonPanel.add(chrButton, gbc);
		
		transcriptNoButton = new JButton("no of transcripts");
		transcriptNoButton.addActionListener(this);
		transcriptNoButton.setActionCommand("plot_no_of_transcripts");
						
		gbc.gridy++;
		
		buttonPanel.add(transcriptNoButton, gbc);
		
		
		barPlotBiotypeFamilyButton = new JButton("biotype family");
		barPlotBiotypeFamilyButton.addActionListener(this);
		barPlotBiotypeFamilyButton.setActionCommand("plot_biotype_family");
						
		gbc.gridy++;
		
		buttonPanel.add(barPlotBiotypeFamilyButton, gbc);	
		
		barPlotBiotypeButton = new JButton("biotype");
		barPlotBiotypeButton.addActionListener(this);
		barPlotBiotypeButton.setActionCommand("plot_biotype");
						
		gbc.gridy++;
		
		buttonPanel.add(barPlotBiotypeButton, gbc);	
				
		frame.add(buttonPanel, BorderLayout.EAST);
	}
	
	public void actionPerformed(ActionEvent ae) {
			
	//		should probably remove an existing plot panel
	//		plotPanel.remove(densityPlotPanel);
		
		if(plotPanelComponent != null){
			
			System.err.println("removing the plot panel component");
			
			plotPanel.remove(plotPanelComponent);
		}
		
		if (ae.getActionCommand().startsWith("plot_density")){
						
			DensityPlot densityPlot = new DensityPlot();	
				
			if (ae.getActionCommand().equals("plot_density_GC")){
															
				densityPlot = new GCContentPlot(queryGenes, backgroundGenes);
				System.out.println("creating new density plot");
			}
				
			else if (ae.getActionCommand().equals("plot_density_length")){
				
				densityPlot = new GeneLengthPlot(queryGenes, backgroundGenes);
			}
		
			//densityPlotPanel = new DensityPlotPanel();
			plotPanelComponent = new DensityPlotPanel();
			//densityPlotPanel.setDensityPlot(densityPlot);
			((DensityPlotPanel) plotPanelComponent).setDensityPlot(densityPlot);
					
			//plotPanel.add(densityPlotPanel, BorderLayout.CENTER);				
		}
		
		else if (ae.getActionCommand().equals("plot_biotype")){
			
			plotPanelComponent = new BiotypeBarPlot(queryGenes, backgroundGenes);			
		}	
		else if (ae.getActionCommand().equals("plot_biotype_family")){
			
			plotPanelComponent = new BiotypeFamilyBarPlot(queryGenes, backgroundGenes);			
		}	
		else if (ae.getActionCommand().equals("plot_no_of_transcripts")){
			
			plotPanelComponent = new TranscriptNumberBarplot(queryGenes, backgroundGenes);			
		}	
		else if (ae.getActionCommand().equals("plot_chr")){
			
			plotPanelComponent = new ChromosomeBarPlot(queryGenes, backgroundGenes);			
		}	
		
		
		plotPanel.add(plotPanelComponent, BorderLayout.CENTER);	
	}
	
	//private void removeCurrentPlot(){
	//}
}
