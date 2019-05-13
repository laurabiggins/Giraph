package uk.ac.babraham.giraph.Application;

/*
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
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.KeyStroke;
import uk.ac.babraham.giraph.Displays.GeneInfoPanel;
import uk.ac.babraham.giraph.Displays.GeneUploadPanel;
import uk.ac.babraham.giraph.Displays.OptionsFrame;
import uk.ac.babraham.giraph.Displays.Help.HelpDialog;
import uk.ac.babraham.giraph.giraphApplication;
import uk.ac.babraham.giraph.Dialogs.ClusterRValueSelector;
import uk.ac.babraham.giraph.Utilities.StopPauseListener;

public class giraphMenuBar extends JMenuBar implements ActionListener{
		
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5891671699146288721L;
	private giraphApplication application;
	private GiraphToolbar toolbar;
	
	/** This is for the calculate coordinates object */
	StopPauseListener spl; 
		
	/** For doing the filtering through the menu options rather than the toolbar */
	JMenu filterMenu = new JMenu("Filters");
	
	/** To export a picture of the circles */
	JMenuItem fileSaveImage;
	
	JPanel filterPanel = new JPanel(new BorderLayout());

	
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
		
		JMenuItem loadGProfilerFile = new JMenuItem("gProfiler results file...");
		externalResults.add(loadGProfilerFile);
		loadGProfilerFile.setActionCommand("load_gProfiler_file");
		loadGProfilerFile.addActionListener(this);
		
		
		JMenuItem loadDavidFile = new JMenuItem("DAVID results file...");
		externalResults.add(loadDavidFile);
		loadDavidFile.setActionCommand("load_david_file");
		loadDavidFile.addActionListener(this);
	
		JMenuItem loadGenericFile = new JMenuItem("Generic text results file...");
		externalResults.add(loadGenericFile);
		loadGenericFile.setActionCommand("load_generic_file");
		loadGenericFile.addActionListener(this);
		
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
			stopCircles();
							
			String msg = "This will wipe out your existing data, would you still like to continue?";  
			int answer = JOptionPane.showConfirmDialog(application, msg);
			System.err.println("answer is: " + answer);
			
			if(answer == 0){
				reset();
			
			}
			return answer;
		}
		return 3;		
	}
	
	public void stopCircles() {
		
		if(spl != null){
			spl.stopCalculating();
			//application.calculatingCoordinatesStopped();
			
			if(application.getGraphPanel() != null){
			
				application.getGraphPanel().updateCalculatingStatus(false);
				application.getGraphPanel().revalidate();
				application.getGraphPanel().repaint();	
			}	
		}	
	}
	
	public void clearApplicationNoChoice() {
		
		stopCircles();
		reset();		
	}
	
	public void reset() {
		application.removeOldData();
		toolbar.resetButtons();
		disableButtons();
	}
	
	public void actionPerformed(ActionEvent e) {

		String command = e.getActionCommand();		
		if (command.equals("exit")) {
			System.exit(0);
		}
	
		
		else if (command.equals("load_david_file")) {
			
			int answer = clearApplication();
			
			if(answer == 0 || answer == 3){				
				application.loadExternalResultsFile("david");
			}						
		}	
		
		else if (command.equals("load_gorilla_file")) {
			
			int answer = clearApplication();
			
			if(answer == 0 || answer == 3){	
				application.loadExternalResultsFile("gorilla");
			}	
		}
		
		else if (command.equals("load_gProfiler_file")) {
			
			int answer = clearApplication();
			
			if(answer == 0 || answer == 3){	
				application.loadExternalResultsFile("gProfiler");
			}	
		}
			
		else if (command.equals("load_generic_file")) {
			
			int answer = clearApplication();
			
			if(answer == 0 || answer == 3){	
				application.loadExternalResultsFile("generic");
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
			
			stopCirclesMoving();
			
			//application.calculateClusters();
		/*	application.getGraphPanel().updateCalculatingStatus(false);
			if(application.getColouredByProximity()){
				application.calculateClusters();
			}	
			application.getGraphPanel().revalidate();
			application.getGraphPanel().repaint();
			disableStopButton();
			enablePlayButton();	
			enableSaveImageOption();
		*/	
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
				//InputStream in = ClassLoader.getSystemResourceAsStream("/Help");
				//new HelpDialog(in);
				
			}
			catch (URISyntaxException ux) {
				System.err.println("Couldn't parse URL falling back to path");
				new HelpDialog(new File(ClassLoader.getSystemResource("Help").getPath()));				
			}
		}
		
		
		else if (command.equals("filter_by_pval")) {
		
			stopCirclesMoving();
			
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

	/*	else if (command.equals("QC_plots")) {
		
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
		*/
		else if (command.equals("filter_by_mult")) {					 
			// create a optionDialog 
		}
		else if (command.equals("display_info")) {	
					
			if(application.getGraphPanel().gip != null) {
				application.getGraphPanel().gip.closePanel();
			}
			
			application.getGraphPanel().gip = new GeneInfoPanel(application);
		}
		
		else if (command.equals("delete_circle")) {		
			//if (s  pl != null){
			//	spl.stopCalculating();
			//}
			//stopCirclesMoving();
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
			
			moveCircles();
			
			/** make sure it has been stopped before it's started again - really the button should not be usable if it's playing */
			
			// stop any already running calculation threads
		/*	if (spl != null){
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
		*/	
		}
		else if (command.equals("goAnnotation")) {			
			application.getGraphPanel().updateGOAnnotation();
			application.getGraphPanel().repaint();
		}
		else if (command.equals("separateCircles")) {			
			application.getGraphPanel().separateCircles();
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
				application.calculateClusters();
			}
		}
			
		
		else {
			JOptionPane.showMessageDialog(application, "Unknown menu command "+command, "Unknown command", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void moveCircles(){
		
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
		
		disablePlayButton();
		//toolbar.playButton.setEnabled(false);
		enableStopButton();
		//toolbar.stopButton.setEnabled(true);
		
	}
	
	private void loadGenes(){
		
		GeneUploadPanel gup = new GeneUploadPanel(application);
		OptionsFrame of = new OptionsFrame();
		of.addOptionsPanel(gup);
				
	}
	
	public void stopCirclesMoving(){
		
		application.getGraphPanel().updateCalculatingStatus(false);
		
		if(application.getColouredByProximity()){
			application.calculateClusters();
		}	
		application.getGraphPanel().revalidate();
		application.getGraphPanel().repaint();
		disableStopButton();
		enablePlayButton();	
		enableSaveImageOption();	
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

		//private JButton checkGC;
		private JButton separateCirclesButton;
		
		private JToggleButton goAnnotationButton;
		
		private JToggleButton colourOption;
		
		private JToggleButton linesToggleButton;
		
		
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
		    
		    ImageIcon clusterAdjustIcon = new ImageIcon(ClassLoader.getSystemResource("uk/ac/babraham/giraph/Icons/adjust_no_of_clusters.png"));
		    JButton clusterAdjust = new JButton(clusterAdjustIcon);
		    clusterAdjust.setActionCommand("adjust_cluster_r_val");
		    clusterAdjust.setToolTipText("adjust the number of clusters");
		    clusterAdjust.addActionListener(menu);
		    add(clusterAdjust);
			  
		    addSeparator();
		    
		    ImageIcon colourOptionIcon = new ImageIcon(ClassLoader.getSystemResource("uk/ac/babraham/giraph/Icons/coloursIcon.png"));
		    colourOption = new JToggleButton(colourOptionIcon);
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
		    
		    ImageIcon separateCirclesIcon = new ImageIcon(ClassLoader.getSystemResource("uk/ac/babraham/giraph/Icons/separate_circles.png"));
		    
		    separateCirclesButton = new JButton(separateCirclesIcon);	
		    	    
		    separateCirclesButton.setActionCommand("separateCircles");
		    separateCirclesButton.setToolTipText("move highly correlated gene sets away from each other");
		    separateCirclesButton.addActionListener(menu);
		    this.add(separateCirclesButton);
		    
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
		    ImageIcon removeLines  = new ImageIcon(ClassLoader.getSystemResource("uk/ac/babraham/giraph/Icons/remove_lines.png"));
		    linesToggleButton = new JToggleButton();
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
			
			
	/*		checkGC = new JButton("QC");
			checkGC.setToolTipText("");
			checkGC.setActionCommand("QC_plots");
			checkGC.addActionListener(menu);
			add(checkGC);
*/			
		}
		
		// reset the toggle buttons in case they've been selected 
		private void resetButtons() {
			
			colourOption.setSelected(false);
			application.setColouredByProximity(true); // ouch, why are we passing this through the application??
			goAnnotationButton.setSelected(false);
			linesToggleButton.setSelected(false);
			
		}
		
		
		/** these aren't being used properly at the moment */

		private void disableButtons() {
			
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
		//toolbar.checkGC.setEnabled(true);
	}
	public void circlesReady(){
		System.err.println("circles ready!!");
		moveCircles();
		//toolbar.playButton.setEnabled(true);
		//enablePlayButton();
		//toolbar.infoButton.setEnabled(true);
		toolbar.adjustPValue.setEnabled(true);
		//toolbar.checkGC.setEnabled(true);
	}
	
	public void addStopPauseListener(StopPauseListener spl){
		this.spl = spl;
	}
	public JToolBar toolbar () {
		return toolbar;
	}

}	
						
		