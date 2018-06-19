package uk.ac.babraham.giraph.Application;

/*
 * TODO: the correlation for the lines doesn't quite match the r values for the clustering. This is because the correlation values
 * are max correlation but the clustering is done by taking averages.
 * 
 */

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import uk.ac.babraham.giraph.Analysis.Density;
import uk.ac.babraham.giraph.DataTypes.GeneCollection;
import uk.ac.babraham.giraph.Displays.GeneInfoPanel;
import uk.ac.babraham.giraph.Displays.GeneUploadPanel;
import uk.ac.babraham.giraph.Displays.OptionsFrame;
import uk.ac.babraham.giraph.Displays.QC.GCContentPlot;
import uk.ac.babraham.giraph.Displays.QC.QCPanel;
import uk.ac.babraham.giraph.Displays.Help.HelpDialog;
import uk.ac.babraham.giraph.giraphApplication;
import uk.ac.babraham.giraph.Dialogs.ClusterRValueSelector;
import uk.ac.babraham.giraph.Utilities.StopPauseListener;

public class giraphMenuBar extends JMenuBar implements ActionListener{//, ChangeListener{
		
	/**
	 *  I should do this more like SeqMonk and have more stored menu items so that they can be selectively enabled when options become available.
	 */
	private static final long serialVersionUID = 1L;
	private giraphApplication application;
	private GiraphToolbar toolbar;
	
	/** This is for the calculate coordinates object */
	StopPauseListener spl; 
	
	/** How much the circles will move around - it wants to be high if there are loads of circles and calculations to do, small if 
	 * it's already been worked out. This is where the diff factor stuff in calculateCoordinates would be useful.  
	 */
	//JSlider jiggleFactor;
	
	/** so the minimum no of genes in the genelist can be adjusted */
	//JSlider adjustGeneListSize;
	
	/** so the user can adjust the p value cut off */ 
	//JSlider adjustStringency;
	
	/** For doing the filtering through the menu options rather than the toolbar */
	JMenu filterMenu = new JMenu("Filters");
	
	/** To export a picture of the circles */
	JMenuItem fileSaveImage;
	
	JPanel filterPanel = new JPanel(new BorderLayout());
	//JLabel geneNoSliderLabel;
	
	//int geneListSizeCutOff = 3; 
	//float pValCutoff = (float)0.05;

	
	public giraphMenuBar (giraphApplication application) {
		this.application = application;
		toolbar = new GiraphToolbar(this);
		
		JMenu fileMenu = new JMenu("File");
		fileMenu.setMnemonic(KeyEvent.VK_F);
		
		JMenu externalResults = new JMenu("Load external results file...");
		fileMenu.add(externalResults);
		
		JMenuItem loadGorillaFile = new JMenuItem("GOrilla results file...");
		externalResults.add(loadGorillaFile);
		loadGorillaFile.setActionCommand("load_gorilla_file");
		loadGorillaFile.addActionListener(this);
		
		JMenuItem loadDavidFile = new JMenuItem("DAVID results file...");
		externalResults.add(loadDavidFile);
		loadDavidFile.setActionCommand("load_david_file");
		loadDavidFile.addActionListener(this);
	
		fileMenu.addSeparator();
		

		/** To load a list of genes, and select a gmt file. */
		
		JMenuItem enterGenes = new JMenuItem("Enter gene list");
		enterGenes.setActionCommand("analyseQueryGenes");
		enterGenes.addActionListener(this);
		fileMenu.add(enterGenes);		
		fileMenu.addSeparator();
		
		fileSaveImage = new JMenuItem("Save Image...");
		fileSaveImage.setMnemonic(KeyEvent.VK_S);
		fileSaveImage.setAccelerator(KeyStroke.getKeyStroke('S', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		fileSaveImage.setActionCommand("saveImage");
		fileSaveImage.addActionListener(this);
		fileMenu.add(fileSaveImage);		
		fileMenu.addSeparator();				
		add(fileMenu);
		
		fileSaveImage.setEnabled(false);
		
		
		JMenu helpMenu = new JMenu("Help");
		helpMenu.setMnemonic(KeyEvent.VK_H);

		JMenuItem helpContents = new JMenuItem("Contents...");
		helpContents.setActionCommand("help_contents");
		helpContents.setAccelerator(KeyStroke.getKeyStroke('H', Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		helpContents.addActionListener(this);
		helpContents.setMnemonic(KeyEvent.VK_C);
		helpMenu.add(helpContents);
		
		add(helpMenu);
		
		toolbar.disableButtons();
	}
	

	
	// Return the answer and respond individually  
	
	public int clearApplication(){
		
		if(application.getGeneListCollection() != null){
			
			// stop the circles
			
			if(spl != null){
				spl.stopCalculating();
				//application.calculatingCoordinatesStopped();
				
				if(application.getGraphPanel() != null){
				
					application.getGraphPanel().updateCalculatingStatus(false);
					application.getGraphPanel().revalidate();
					application.getGraphPanel().repaint();	
				}	
			}	
							
			String msg = "This will wipe out your existing data, would you still like to continue?";  
			int answer = JOptionPane.showConfirmDialog(application, msg);
			System.err.println("answer is: " + answer);
			
			if(answer == 0){
				application.removeOldData();
				disableButtons();
			}
			return answer;
		}

		return 3;
		
	}
	
	
	
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();		
		if (command.equals("exit")) {
			System.exit(0);
		}
	
		
		else if (command.equals("load_david_file")) {
			
			int answer = clearApplication();
			
			if(answer == 0 || answer == 3){				
				application.loadExternalResultsFile(false, true);				
			}						
		}	
		
		else if (command.equals("load_gorilla_file")) {
			
			int answer = clearApplication();
			
			if(answer == 0 || answer == 3){	
				application.loadExternalResultsFile(true, false);
			}	
		}
		
		else if (command.equals("analyseQueryGenes")) {
			
			int answer = clearApplication();
			
			if(answer == 0|| answer == 3){	
			
				loadGenes();
			}	
		
		}
		else if (command.equals("saveImage")) {
			application.saveImage();
		}
		else if (command.equals("stop")) {			
			spl.stopCalculating();
			
			//application.calculateClusters();
			application.getGraphPanel().updateCalculatingStatus(false);
			application.getGraphPanel().revalidate();
			application.getGraphPanel().repaint();
			disableStopButton();
			enablePlayButton();	
			enableSaveImageOption();
		}
		
		else if (command.equals("help_contents")) {
			try {

				// Java has a bug in it which affects the creation of valid URIs from
				// URLs relating to an windows UNC path.  We therefore have to mung
				// URLs starting file file:// to add 5 forward slashes so that we
				// end up with a valid URI.

				URL url = ClassLoader.getSystemResource("Help");
				if (url.toString().startsWith("file://")) {
					try {
						url = new URL(url.toString().replace("file://", "file://///"));
					} catch (MalformedURLException e2) {
						throw new IllegalStateException(e2);
					}
				}
				new HelpDialog(new File(url.toURI()));
			}
			catch (URISyntaxException ux) {
				System.err.println("Couldn't parse URL falling back to path");
				new HelpDialog(new File(ClassLoader.getSystemResource("Help").getPath()));				
			}
		}
		
		
		else if (command.equals("filter_by_pval")) {
		
			String msg = "Enter a p value threshold to filter the dataset, this is currently set at " + application.dataFilter().getPvalueCutoff();
			
			String s = (String)JOptionPane.showInputDialog(application, msg, "p value filter",  JOptionPane.PLAIN_MESSAGE);
		
			if (s != null){
			
				try{
					float pValCutOff = Float.parseFloat(s);
					if (pValCutOff <= 1 && pValCutOff > 0){
			
						if (spl != null){
							spl.stopCalculating();
						}	
						application.setFilters(pValCutOff);					
					}
					else {
						JOptionPane.showMessageDialog(application,"p value cut off must be between 0 and 1", "incompatible value entered", JOptionPane.ERROR_MESSAGE);
					}
				}
				catch (NumberFormatException n){
					JOptionPane.showMessageDialog(application,"invalid value entered, must be numeric", "invalid value entered", JOptionPane.ERROR_MESSAGE);					
				}
			}	
		}
/*		else if (command.equals("filter_by_size")) {	
		
			String msg = "Enter minimum number of genes within functional group, this is currently set at " + application.dataFilter().getMinNoGenes(); 
			
			String s = (String)JOptionPane.showInputDialog(application, msg, "gene list size filter",  JOptionPane.PLAIN_MESSAGE);
		
			if (s != null){
				try{
			
					int geneListSizeCutOff = Integer.parseInt(s);
					
					if (geneListSizeCutOff >= 1){
					
						if (spl != null){
							spl.stopCalculating();
						}
						application.setFilters(geneListSizeCutOff, application.dataFilter().getPvalueCutoff());
					}					
					else {
						JOptionPane.showMessageDialog(application,"size filter must be greater than 0", "incompatible value entered", JOptionPane.ERROR_MESSAGE);
					}
				}
				catch(NumberFormatException n){
					JOptionPane.showMessageDialog(application,"value entered must be an integer", "value entered must be an integer", JOptionPane.ERROR_MESSAGE);
				}
			}
		}
*/		else if (command.equals("QC_plots")) {
		
			if(application.queryGenes() != null && (application.genomicBackgroundGenes != null || application.customBackgroundGenes != null)){
			
				GeneCollection bgGenes;
				
				if(application.customBackgroundGenes == null){
					bgGenes = application.genomicBackgroundGenes;
				}
				else{
					bgGenes = application.customBackgroundGenes;
				}
				new QCPanel(application.queryGenes(), bgGenes); 	
			}
			else{
				JOptionPane.showMessageDialog(application,"query genes must be entered to use this function", "no query genes", JOptionPane.INFORMATION_MESSAGE);
			}			
		}
		
		else if (command.equals("filter_by_mult")) {					 
			// create a optionDialog 
		}
		else if (command.equals("display_info")) {	
			
			GeneInfoPanel gip2 = new GeneInfoPanel(application);
			application.getGraphPanel().gip = gip2;
			
		}
		
		else if (command.equals("delete_circle")) {		
			if (spl != null){
				spl.stopCalculating();
			}	
			application.getGraphPanel().deleteCircle();						
		}
		else if (command.equals("increase_lines")) {			
			application.getGraphPanel().decreaseMincorrelation();					
		}
		else if (command.equals("decrease_lines")) {			
			application.getGraphPanel().increaseMincorrelation();						
		}
		else if (command.equals("update_lines")) {			
			application.getGraphPanel().updateLines();
		
		}
		
		else if (command.equals("play")) {
			/** make sure it has been stopped before it's started again - really the button should not be usable if it's playing */
			
			// stop any already running calculation threads
			if (spl != null){
				spl.stopCalculating();
				pause(10);
			}	
		
			application.calculate();
						
			if (application.getGraphPanel() != null){
				
				application.getGraphPanel().updateCalculatingStatus(true);
			}	
			
			//disablePlayButton();
			toolbar.playButton.setEnabled(false);
			//enableStopButton();
			toolbar.stopButton.setEnabled(true);
			
		}
		else if (command.equals("goAnnotation")) {			
			application.getGraphPanel().updateGOAnnotation();
			application.getGraphPanel().repaint();
		}
		else if(command.equals("adjust_cluster_r_val")){
			new ClusterRValueSelector(application);
		}
		else if(command.equals("change_colour_option")){
			boolean colourOption = application.getColouredByProximity();
			if(colourOption == false){
				application.setColouredByProximity(true);
				application.calculateClusters();
			}
			else{
				application.setColouredByProximity(false);
				application.calculateClusters();//dByProximity(false);
			}
			
		}
			
		
		else {
			JOptionPane.showMessageDialog(application, "Unknown menu command "+command, "Unknown command", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void loadGenes(){
		
		GeneUploadPanel gup = new GeneUploadPanel(application);
		OptionsFrame of = new OptionsFrame();
		of.addOptionsPanel(gup);
				
	}
	
	private class GiraphToolbar extends JToolBar {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;

		/** Stop calculating button. */
		private JButton stopButton;		
		
		private JButton playButton;
		
		private JButton infoButton;

		private JButton adjustPValue;
		
//		private JButton adjustMinGenes;

		private JButton checkGC;
		
		private JToggleButton goAnnotationButton;
		
		private JToggleButton colourOption;
		
		
		public GiraphToolbar (giraphMenuBar menu) {
		
			setFocusable(false);
			
			ImageIcon stopIcon = new ImageIcon(ClassLoader.getSystemResource("uk/ac/babraham/giraph/Icons/stopIcon.png"));
			stopButton = new JButton(stopIcon);
		    stopButton.setActionCommand("stop");
		    stopButton.setToolTipText("stop");
		    stopButton.addActionListener(menu);
		    add(stopButton);
		       
		    ImageIcon playIcon = new ImageIcon(ClassLoader.getSystemResource("uk/ac/babraham/giraph/Icons/play.png"));
		    playButton = new JButton(playIcon);		
		    playButton.setActionCommand("play");
		    playButton.setToolTipText("recalculate circle positions from current positions");
			playButton.addActionListener(menu);
		    add(playButton);
		    
		    addSeparator();		    
		  		    
		    JButton deleteButton = new JButton(new ImageIcon(ClassLoader.getSystemResource("uk/ac/babraham/giraph/Icons/delete_circle.png")));   	
		    deleteButton.setActionCommand("delete_circle");
		    deleteButton.setToolTipText("delete selected circle");
		    deleteButton.addActionListener(menu);
		    add(deleteButton);
		   					   
		    addSeparator();
		    
		    infoButton = new JButton(new ImageIcon(ClassLoader.getSystemResource("uk/ac/babraham/giraph/Icons/info_rounded.png")));   
		    infoButton.setActionCommand("display_info");
		    infoButton.setToolTipText("display information about gene list");
		    infoButton.addActionListener(menu);
		    add(infoButton);
		    
		    addSeparator();
		    
		    ImageIcon clusterAdjustIcon = new ImageIcon(ClassLoader.getSystemResource("uk/ac/babraham/giraph/Icons/coloursIcon.png"));
		    JButton clusterAdjust = new JButton(clusterAdjustIcon);
		    clusterAdjust.setActionCommand("adjust_cluster_r_val");
		    clusterAdjust.setToolTipText("adjust the number of clusters");
		    clusterAdjust.addActionListener(menu);
		    add(clusterAdjust);
			  
		    addSeparator();
		    
		    colourOption = new JToggleButton(clusterAdjustIcon);
		    colourOption.setActionCommand("change_colour_option");
		    colourOption.setToolTipText("colour by proximity or relatedness");
		    colourOption.addActionListener(menu);
		    this.add(colourOption);
		    
		    addSeparator();
		    
		    ImageIcon goAnnotIcon = new ImageIcon(ClassLoader.getSystemResource("uk/ac/babraham/giraph/Icons/functional_annotations.png"));
		    ImageIcon removeGoAnnotIcon = new ImageIcon(ClassLoader.getSystemResource("uk/ac/babraham/giraph/Icons/functional_annotations_remove.png"));
		    
		    goAnnotationButton = new JToggleButton(goAnnotIcon);	
		    
		    goAnnotationButton.setSelectedIcon(removeGoAnnotIcon);
		    
		    goAnnotationButton.setActionCommand("goAnnotation");
		    goAnnotationButton.setToolTipText("display functional information for all the circles");
		    goAnnotationButton.addActionListener(menu);
		    this.add(goAnnotationButton);
		    
		    addSeparator();
		    
		    
		    ImageIcon moreLinesIcon = new ImageIcon(ClassLoader.getSystemResource("uk/ac/babraham/giraph/Icons/more_lines.png"));
		    JButton moreLinesButton = new JButton(moreLinesIcon);		
		    moreLinesButton.setActionCommand("increase_lines");
		    moreLinesButton.setToolTipText("increase number of lines");
		    moreLinesButton.addActionListener(menu);
		    add(moreLinesButton);
		   
		    ImageIcon fewerLinesIcon = new ImageIcon(ClassLoader.getSystemResource("uk/ac/babraham/giraph/Icons/fewer_lines.png"));
		    JButton fewerLinesButton = new JButton(fewerLinesIcon);		
		    fewerLinesButton.setActionCommand("decrease_lines");
		    fewerLinesButton.setToolTipText("decrease number of lines");
		    fewerLinesButton.addActionListener(menu);
		    add(fewerLinesButton);
			  
		    addSeparator();
		    
		    ImageIcon showLines  = new ImageIcon(ClassLoader.getSystemResource("uk/ac/babraham/giraph/Icons/more_lines.png"));
		    ImageIcon removeLines  = new ImageIcon(ClassLoader.getSystemResource("uk/ac/babraham/giraph/Icons/more_lines.png"));
		    JToggleButton linesToggleButton = new JToggleButton();
		    linesToggleButton.setSelectedIcon(showLines);
		    linesToggleButton.setIcon(removeLines);
		    linesToggleButton.setActionCommand("update_lines");
		    linesToggleButton.addActionListener(menu);
		    linesToggleButton.setToolTipText("add or remove all lines");
		    add(linesToggleButton);
		    
		    addSeparator();
		       		    
			/** This should be enabled as soon as the dataset has been loaded */
			ImageIcon pIcon = new ImageIcon(ClassLoader.getSystemResource("uk/ac/babraham/giraph/Icons/filter_by_pvalue.png"));
			adjustPValue = new JButton(pIcon);
			adjustPValue.setToolTipText("filter by likelihood of significance");
			adjustPValue.setActionCommand("filter_by_pval");
			adjustPValue.addActionListener(menu);
			add(adjustPValue);
			
			addSeparator();
			
/*			adjustMinGenes = new JButton("set min");
			adjustMinGenes.setToolTipText("");
			adjustMinGenes.setActionCommand("filter_by_size");
			adjustMinGenes.addActionListener(menu);
			add(adjustMinGenes);
			
			addSeparator();
*/			
			checkGC = new JButton("QC");
			checkGC.setToolTipText("");
			checkGC.setActionCommand("QC_plots");
			checkGC.addActionListener(menu);
			add(checkGC);
			
		}
		/** these aren't being used properly at the moment */

		private void disableButtons() {
			/** Disable everything on the toolbar.  I think that this is causing problems  when I'm trying to delete buttons.*/
			Component [] c = toolbar.getComponents();
			System.out.println("component length = " + c.length);
			
			for (int i=0;i<c.length;i++) {
				c[i].setEnabled(false);
				c[i].setFocusable(false);
			}
			/*Component [] cf = filterPanel.getComponents();
			for (int i=0;i<cf.length;i++) {
				cf[i].setEnabled(false);
				cf[i].setFocusable(false);
			}
			*/
		}
		private void enableButtons(){
			Component [] c = getComponents();
			for (int i=0;i<c.length;i++) {				
				c[i].setEnabled(true);
				c[i].setFocusable(false);
			}
			Component [] cf = filterPanel.getComponents();
			for (int i=0;i<cf.length;i++) {
				cf[i].setEnabled(true);
				cf[i].setFocusable(false);
			}
			
			
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
	
	public void enablePlayButton(){
		toolbar.playButton.setEnabled(true);
	}
	
	public void disablePlayButton(){
		toolbar.playButton.setEnabled(false);
	}
	
	public void enableSaveImageOption(){
		fileSaveImage.setEnabled(true);
	}
	
	public void enableStopButton(){
		toolbar.stopButton.setEnabled(true);
	}
	public void disableStopButton(){
		toolbar.enableButtons();
		toolbar.stopButton.setEnabled(false);	
	}
	
	public void disableButtons(){
		toolbar.disableButtons();
		filterMenu.setEnabled(false);
		fileSaveImage.setEnabled(false);
	}
	public void dataLoaded(){
		//toolbar.playButton.setEnabled(true);
		//toolbar.adjustMinGenes.setEnabled(true);
		toolbar.adjustPValue.setEnabled(true);
		toolbar.checkGC.setEnabled(true);
	}
	public void circlesReady(){
		toolbar.playButton.setEnabled(true);
		//toolbar.infoButton.setEnabled(true);
		toolbar.adjustPValue.setEnabled(true);
		toolbar.checkGC.setEnabled(true);
	}
	
	public void addStopPauseListener(StopPauseListener spl){
		this.spl = spl;
	}
	public JToolBar toolbar () {
		return toolbar;
	}

}	
						
		