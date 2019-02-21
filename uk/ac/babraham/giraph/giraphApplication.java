package uk.ac.babraham.giraph;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;

import uk.ac.babraham.giraph.Application.giraphMenuBar;
import uk.ac.babraham.giraph.DataParser.ExternalResultsParser;
import uk.ac.babraham.giraph.DataParser.ProgressListener;
import uk.ac.babraham.giraph.DataTypes.GeneCollection; 
import uk.ac.babraham.giraph.DataTypes.GeneList;
import uk.ac.babraham.giraph.DataTypes.GeneListCollection;
import uk.ac.babraham.giraph.Displays.GraphPanel;
import uk.ac.babraham.giraph.Filters.DataFilter;
import uk.ac.babraham.giraph.Filters.FilterListener;
import uk.ac.babraham.giraph.Maths.CalculateCoordinates;
import uk.ac.babraham.giraph.Maths.Cluster;
import uk.ac.babraham.giraph.Maths.ClusterPair;
import uk.ac.babraham.giraph.Utilities.ImageSaver;
import uk.ac.babraham.giraph.Utilities.StopPauseListener;

/** 
 *  TODO: add option for uploading ensembl ids or gene names - the ids would have to be converted to symbols for using the gmt file. 
 * 
 *  TODO: allow download of gmt file
 *  
 *  
 *  TODO: instead of filter results, have a more comprehensive info display i.e. 
 *  	total no query genes
 *  		no of matched genes
 *  		no of unmatched genes
 *  	total no functional categories
 *  		no after filtering 
 *  Then, click for more info on unmatched genes OR play/continue....
 *  
 *  TODO: when a circle is deleted, it reappears when new filters are applied - maybe store the deleted categories.
 *  
 *  TODO: OptionsFrame has lots of references to the main app using getInstance - it should probably be passed to it properly.
 *  
 *  TODO: play automatically 
 *  
 *  TODO: Catching errors - put in a load of checks
 *  TODO: Go through the process of running the tool and clean it up - sort out the little things. 
 *  
 *  TODO: Check whether the file looks right when it's selected/loaded.
 *   
 *  TODO: fix the info button so that it doesn't say DavidResultProperties and change a few of the fields plus sort out the position.
 * 
 *	TODO: If there are too few circles when the list of genes is entered, allow the filters to be adjusted.
 *	TODO: activate the p value filter when the parsing has happened - if the filters result in 0 circles you're screwed.
 *
 *  TODO: enable the removal of duplicate circles (exact duplicates) ??
 *  TODO: put in limit to number of genes you can enter. 
 * 
 *  TODO: print number of genes that were found - of the x genes that were entered, x were matched, and x were not   
 *  TODO: see if I can get something saying filters for the 2 filters.
 *  
 *  TODO: think about the steps taken to run the software - they're probably not obvious
 *  TODO: annotate Result class.
 *  TODO: have current filters displayed somewhere
 *  TODO: enable filter menu before playing
 *  
 *  TODO: sort out circle overlap function
 *  
 *  TODO: barplot for chromosomes - decide if we're showing background or not as this'll affect how the plot is constructed
 *  
 * (TODO: Export ontology info for all query genes that matched - save as tab-delimited text file.)
 *  
 * @author bigginsl
 *
 */

public class giraphApplication extends JFrame implements ProgressListener, FilterListener{
	
	
	private static final long serialVersionUID = 1L;
	
	/** The main application */	
	private static giraphApplication app;
	
	/** The menubar */
	giraphMenuBar menuBar; 
	
	/** filepath for file to be loaded from */
//	String filepath;
	
	/** The result object */
//	Result dr;
	
	/** The clustered genelists */
	ClusterPair clusterPair;
	
	/** for the clusters */
	float rValueCutoff = (float)0.6;
	
	/** The main display panel */
	GraphPanel gp; 
		
	JSplitPane mainPane;
	JSplitPane infoPane;

	StopPauseListener spl;
	
	private boolean colouredByProximity = true;
	
	private DataFilter df; 
	
	// This contains all the genes in the genome. It is needed for the QC plots.
	public GeneCollection genomicBackgroundGenes;
	
	public GeneCollection customBackgroundGenes = null;
	
	private GeneCollection queryGenes;
	
	private GeneListCollection geneListCollection;	
	
	private Cluster clusters = null;
	
	
	public giraphApplication () {		
		setTitle("giraph");
		
		setIconImage(new ImageIcon(ClassLoader.getSystemResource("uk/ac/babraham/giraph/Icons/giraph2.png")).getImage());
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,600);
		setLocationRelativeTo(null);
		setMinimumSize(new Dimension(150,150));
		menuBar = new giraphMenuBar(this);
		setJMenuBar(menuBar);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(menuBar.toolbar(),BorderLayout.NORTH);
		setVisible(true);
	
		mainPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		mainPane.setOneTouchExpandable(true);
		mainPane.setResizeWeight(0.9);
		mainPane.setDividerLocation(600);
		
		infoPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		infoPane.setOneTouchExpandable(true);
		infoPane.setResizeWeight(0.9);
		infoPane.setDividerLocation(600);		
		infoPane.setTopComponent(mainPane);
	
		getContentPane().add(BorderLayout.CENTER, infoPane);
		
		genomicBackgroundGenes = new GeneCollection();
	}

	/** Returns the main app */
	public static giraphApplication getInstance() {
		return app;
	}
	
	public float getRValue(){
		return rValueCutoff;
	}
	
	/** Access the main display */
	public GraphPanel getGraphPanel(){
		return gp;
	}
	
	/** Access the main result */
	public GeneListCollection getGeneListCollection(){
		return geneListCollection;
	}
	
	/** Set the gene list collection */
	public void setGeneListCollection(GeneListCollection r){
		geneListCollection = r;
	}
	
	/** Access the datafilter */
	public DataFilter dataFilter(){
		return df;
	}
	
	public void setColouredByProximity(boolean x){
		colouredByProximity = x;
	}
	
	public boolean getColouredByProximity(){
		return colouredByProximity;
	}
	
	
	public void setGenomicBackgroundGenes(GeneCollection genomicBackgroundGenes){
		this.genomicBackgroundGenes = genomicBackgroundGenes;
	}
	
	public GeneCollection genomicBackgroundGenes(){
		return(genomicBackgroundGenes);
	}
	
	public void setCustomBackgroundGenes(GeneCollection customBackgroundGenes){
		this.customBackgroundGenes = customBackgroundGenes;
	}
	
	public GeneCollection customBackgroundGenes(){
		return(customBackgroundGenes);
	}
	
	public void setQueryGenes(GeneCollection queryGenes){
		this.queryGenes = queryGenes;
	}
	
	public GeneCollection queryGenes(){
		return(queryGenes);
	}
	
	
	public void removeOldData(){
		
		System.err.println("trying to clear the panel");
		geneListCollection = null;
		genomicBackgroundGenes = new GeneCollection();
		customBackgroundGenes = null;
		queryGenes = null;
		
	    if (gp != null){
			gp.setVisible(false);
		}	
		gp = null;
	}

	/** 
	 * When the play button is pressed, this method is called and starts a new CalculateCoordinates.
	 */
	public void calculate(){
		
		CalculateCoordinates cc = new CalculateCoordinates(geneListCollection);;
		
		menuBar.addStopPauseListener(cc);

		cc.addProgressListener(this);
		Runnable r = cc;
		Thread thr = new Thread(r);
		thr.start();
	}
	

	/** 
	 * This is called from CalculateCoordinates (via the app being a progress listener)
	 * Set up the graph panel
	 */
	public void firstCoordinatesReady(){		
		
		if (gp == null){   
			
			gp = new GraphPanel(geneListCollection, this);
			gp.setMinimumSize(new Dimension(100,100));
			mainPane.setLeftComponent(gp);
			gp.setPreferredSize(new Dimension(600,500));
			gp.setMinimumSize(new Dimension(200, 100));	
			gp.filtersUpdated(df.getPvalueCutoff());//, df.getMinNoGenes());
		}
		
		gp.updateCalculatingStatus(true);
		
 		if(clusterPair != null){
			
			gp.setColoursForGeneLists(clusterPair.getValidClusters(rValueCutoff));
			
		}
		
		/** Activate the buttons on the toolbar */
		menuBar.enableStopButton();
	}
	
	/**
	 * This is called from the Cluster class via the app being a progress listener 
	 */
	public void clusteringComplete(ClusterPair cp){
		
		this.clusterPair = cp;
		
		if(gp != null){

			gp.setColoursForGeneLists(clusterPair.getValidClusters(rValueCutoff));
			gp.revalidate();
			gp.repaint();
		}	
		menuBar.circlesReady();
	}
	
	/**
	 *  This method is called from calculate coordinates via the app being a progress listener
	 */	
	public void updateGraphPanel(){
		
		gp.coordinatesUpdated();

	}
	
	public void calculatingCoordinatesStopped(){
		
		calculateClusters();
		
		menuBar.stopCirclesMoving();
		
		/*getGraphPanel().updateCalculatingStatus(false);
		getGraphPanel().revalidate();
		getGraphPanel().repaint();
		menuBar.disableStopButton();
		menuBar.enablePlayButton();
		*/
	}
	
	
	/** 
	 * Used when rValue for clusters is adjusted on the menu bar
	 */
	public void adjustClusterStringency(float rValueCutoff){
		
		this.rValueCutoff = rValueCutoff;
		gp.setColoursForGeneLists(clusterPair.getValidClusters(rValueCutoff));
		gp.repaint();

	}
	
	
	public void inputFileParsingComplete(GeneListCollection geneListCollection, GeneCollection queryGenes, GeneCollection customBackgroundGenes, GeneCollection genomicBackgroundGenes) {
		
		this.geneListCollection = geneListCollection;
		this.queryGenes = queryGenes;
		this.customBackgroundGenes = customBackgroundGenes;
		this.genomicBackgroundGenes = genomicBackgroundGenes;
		
		// we can set the coordinates before filtering as we want starting coordinates for each gene list
		// so that if the filter thresholds are lowered they have somewhere to go
		setStartingGridCoordinates();
		
	}
	
	
	
	
	
	/**
	 * Used when filters are altered on the menubar or set up initially
	 */
	
	public void setFilters(float stringency){
		
		DataFilter potentialDataFilter = new DataFilter(geneListCollection, stringency);
		// we don't want to set the data filter if the parameters are rejected.
		if(potentialDataFilter.filterBySizeAndStringency() != null){
				
		    // Use datafilter and set the validities
			df = new DataFilter(geneListCollection, stringency);
			df.addFilterListener(this);
			
			if(gp != null){
				df.addFilterListener(gp);
			}
			df.filterBySizeAndStringency();
			
			int noGeneLists = geneListCollection.getValidGeneLists().length;
			if (noGeneLists > 400){
				
				JOptionPane.showMessageDialog(giraphApplication.getInstance(), "Using your list of genes and this filter (p value " + stringency + ") would leave " + 
				noGeneLists + " siginficant gene ontology categories.\n" + "Giraph might be able to cope with this, but it may be slow. Try increasing the stringency of the filters.", 
				"Too many circles", JOptionPane.WARNING_MESSAGE);	
			}
			else{
				JOptionPane.showMessageDialog(giraphApplication.getInstance(), "Setting p value threshold to " + stringency +
					" leaves "+ noGeneLists + " circles", "filter results", JOptionPane.INFORMATION_MESSAGE);
			}
						
		}	
	}	

	public void filtersUpdated(float pvalue) {
		
		menuBar.dataLoaded();
		
		// kill the current thread if one is running
		if(clusters != null){
			clusters.stopRunning();
		}
		
		calculateClusters();		
	}
	
	
	public void saveImage(){
		
		new ImageSaver();
		try {
			ImageSaver.saveImage(gp);
		} catch (giraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		gp.exportImage = false;
	}	
	
	public void setStartingGridCoordinates(){
		
		GeneList [] geneLists = geneListCollection.getAllGeneLists();
		
		int n = geneLists.length;
			
		// TODO: This might not be great if n is small 
		int dim = (int)(Math.ceil(Math.sqrt(n)));
		//System.out.println("dim: " + dim);
		
		float increment = (float)(1/(Math.ceil(Math.sqrt(n))));
		//System.out.println("increment: " + increment);
		int i = 0;
		float y = 0;
		
		LOOP: while(i < n){
			for (int j = 0; j <= dim; j++){
				if (i < n){
					
					geneLists[i].coordinates().unscaledX = (float)(increment)*j;
					geneLists[i].coordinates().unscaledY = (float)(increment)*y;
	
					i++;
				}	
				else{
					break LOOP;
				}
			}
			y++;	
		}
	}
	
	
	public void calculateClusters(){
		
		clusters = new Cluster(getGeneListCollection().getValidGeneLists(), colouredByProximity);
		clusters.addProgressListener(this);
		Runnable r = clusters;
		Thread thr = new Thread(r);
		thr.start();
	}
	
	public void loadExternalResultsFile(boolean gorilla, boolean david){
		

		JFileChooser chooser = new JFileChooser(GiraphPreferences.getInstance().getDataLocation());
		
		int result = chooser.showOpenDialog(this);
		if (result == JFileChooser.CANCEL_OPTION) return;

		File file = chooser.getSelectedFile();
		
		GiraphPreferences.getInstance().setLastUsedDataLocation(file);
				
		ExternalResultsParser erp = new ExternalResultsParser(file, gorilla, david);
		erp.addProgressListener(this);
		erp.run();
		
	}
	
	public void externalResultsParsed(GeneListCollection geneListCollection) {
		
		this.geneListCollection = geneListCollection;
		
		// set default filters
		setFilters((float)0.05);
		
		System.err.println("parsed from giraph app");
		setStartingGridCoordinates();
		
	}

	
	
	/**
	 * Main method
	 * 
	 */
	
	public static void main(String[] a) {	
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} 
		catch (Exception e) {}
		
		try {
			
			// This allows us to catch all throwable errors reported
			// in our application.
			
			Thread.setDefaultUncaughtExceptionHandler(new ErrorCatcher());			
			app = new giraphApplication();
			app.setVisible(true);			
		}
		catch (Exception e) {
			new CrashReporter(e);
			e.printStackTrace();
		}
		
	}	
	
	
	protected ImageIcon createImageIcon(String path, String description) {
		java.net.URL imgURL = getClass().getResource(path);
		if (imgURL != null) {
			return new ImageIcon(imgURL, description);
		} 
		else {
			System.err.println("Couldn't find file: " + path);
			return null;
		}
	}

	@Override
	public void progressCancelled() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void progressUpdated(String s, int x1, int x2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void progressWarningReceived(Exception e) {
		// TODO Auto-generated method stub
		
	}



}

	
	
	
	