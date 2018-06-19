package uk.ac.babraham.giraph.Displays;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import uk.ac.babraham.giraph.GiraphPreferences;
import uk.ac.babraham.giraph.giraphApplication;
import uk.ac.babraham.giraph.DataParser.CustomBackgroundGeneParser;
import uk.ac.babraham.giraph.DataParser.GMTParser;
import uk.ac.babraham.giraph.DataParser.GeneInfoParser;
import uk.ac.babraham.giraph.DataParser.OptionsListener;
import uk.ac.babraham.giraph.DataParser.GeneNameParser;
import uk.ac.babraham.giraph.DataParser.ProgressListener;
import uk.ac.babraham.giraph.DataParser.QueryGeneParser;
import uk.ac.babraham.giraph.DataTypes.GeneCollection;
import uk.ac.babraham.giraph.DataTypes.GeneList;
import uk.ac.babraham.giraph.DataTypes.GeneListCollection;
import uk.ac.babraham.giraph.DataTypes.PValue;
import uk.ac.babraham.giraph.Maths.FishersExactTest;
import uk.ac.babraham.giraph.Maths.MultipleTestingCorrection;

public class OptionsFrame extends JFrame implements ActionListener, OptionsListener {
	
	JButton submitButton;
	GeneUploadPanel optionsPanel;
	GeneInfoParser geneInfoParser;
	GeneNameParser queryGeneParser;
	GeneNameParser customBackgroundGeneParser = null;
	GMTParser gmtParser;
	
	GeneCollection queryGenes;
	GeneCollection genomicBackgroundGenes;
	GeneCollection customBackgroundGenes = null;
	GeneListCollection geneListCollection;
	private boolean usingCustomBackground = false;
	
	
	private ProgressListener pl;
	
	
	public OptionsFrame(){
	
		setTitle("info");
		
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600,600);
		setMinimumSize(new Dimension(150,150));
		getContentPane().setLayout(new BorderLayout());
		
		JButton submitButton = new JButton();
		
		submitButton = new JButton("submit");
		submitButton.addActionListener(this);
		submitButton.setActionCommand("submit_options");
		add(submitButton, BorderLayout.SOUTH);
		
		setLocationRelativeTo(giraphApplication.getInstance());
		setVisible(true);
			
		//this.setAlwaysOnTop(true);
		
		addProgressListener(giraphApplication.getInstance());
	}
	

	public JPanel getOptionsPanel(){
		
		return optionsPanel;
		
	}
	
	public void addOptionsPanel(GeneUploadPanel panel){
		
		optionsPanel = panel;
		getContentPane().add(panel, BorderLayout.NORTH);
		
	}


	public void actionPerformed(ActionEvent ae) {
		
		if (ae.getActionCommand().equals("submit_options")){
			
			if(optionsPanel.queryGenes().isEmpty()){
				String msg = ("Please enter some query genes");
				JOptionPane.showMessageDialog(this, msg, "No genes to analyse", JOptionPane.ERROR_MESSAGE);
			}
			
			else if(optionsPanel.minGenesInSet() > optionsPanel.maxGenesInSet()){
				JOptionPane.showMessageDialog(giraphApplication.getInstance(), "Minimum number of genes in set cannot be greater than maximum.", "Number of genes needs adjusting", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			else{
				
				// checking that the query and background genes look ok 
				//if(optionsPanel.checkValidityOfGenes() == true){
					
					giraphApplication.getInstance().removeOldData();
					
					try {
						File dir = GiraphPreferences.getInstance().getGeneInfobase();
							
						//File f = findFile(dir, optionsPanel.species());
						String  f; 
						
						if(optionsPanel.species().startsWith("Human")){

							f = "uk/ac/babraham/giraph/Utilities/Homo_sapiens.GRCh38.92_gene_info.txt.gz";
						}
						else if(optionsPanel.species().startsWith("Mouse")){							

							f = "uk/ac/babraham/giraph/Utilities/Mus_musculus.GRCm38.92_gene_info.txt.gz";							
						}
						
						else{
							f = null;
						}
						
						
						if (f != null){
						
							//geneInfoParser = new GeneInfoParser(f.getAbsolutePath(), giraphApplication.getInstance().genomicBackgroundGenes);
							geneInfoParser = new GeneInfoParser(f, giraphApplication.getInstance().genomicBackgroundGenes);
							
							geneInfoParser.addOptionsListener(this);
																											
						}
						else{
							System.err.println("Couldn't find gene info file");
						}
						
						
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// this has to be done before the query genes can be loaded
					//optionsPanel.loadFunctionalInfo(gmtParser);
					setVisible(false);
					dispose();					
				//}	
			}	
		}		
	}
	
	public void geneInfoFileParsed(){
		
		if ((optionsPanel.loadingMessageThread() != null) && (optionsPanel.loadingMessageThread().isAlive())){
			optionsPanel.loadingMessageThread().interrupt();
		}
		
		genomicBackgroundGenes = geneInfoParser.getGeneCollection();
		
		
		// next we want to parse the custom background genes if they're being used.
		if(optionsPanel.getBackgroundGenesOption().equals("Enter custom background genes")){
			
			customBackgroundGeneParser = new CustomBackgroundGeneParser(optionsPanel.backgroundGenes(), genomicBackgroundGenes);
			customBackgroundGeneParser.addOptionsListener(this);
			usingCustomBackground = true;
		}
		
		// if not using custom background genes then go straight into loading the query genes, using the genomic background as the background 
		else{
			// set the background genes
			//backgroundGenes = geneInfoParser.getGeneCollection();
			
			queryGeneParser = new QueryGeneParser(optionsPanel.queryGenes(), genomicBackgroundGenes);
			queryGeneParser.addOptionsListener(this);
		}	
	}
	
	// If customBackgroundGenes have been used then load the query genes using these as the background
	public void customBackgroundGenesImported(){
		
		customBackgroundGenes = customBackgroundGeneParser.geneCollection();
		
		queryGeneParser = new QueryGeneParser(optionsPanel.queryGenes(), customBackgroundGenes);
		queryGeneParser.addOptionsListener(this);
	}
	
	
	public void queryGenesImported(){
		
		queryGenes = queryGeneParser.geneCollection();
		parseGMTFile();
	}
	
	private void parseGMTFile(){
		
		System.out.println("now about to try and parse gmt file");
				
		try {
			File dir = GiraphPreferences.getInstance().getGMTbase();
				
			//File f = findFile(dir, optionsPanel.species());
			
			String f = new String("");
			
			if(optionsPanel.species().startsWith("Human")){
				
				f = "uk/ac/babraham/giraph/Utilities/Human_GO_AllPathways_no_GO_iea_June_01_2018_symbol.gmt";
			}
			else if(optionsPanel.species().startsWith("Mouse")){
				
				f = "uk/ac/babraham/giraph/Utilities/Mouse_GO_AllPathways_no_GO_iea_June_01_2018_symbol.gmt";
			}
			//else if(optionsPanel.species().startsWith("C Elegans")){
				
			//	f = "uk/ac/babraham/giraph/Utilities/C_elegans.go_terms.including_parents.gmt.gz";			
			//}
			else{
				f = null;
			}
									
			if (f != null){
				if(usingCustomBackground){	
					gmtParser = new GMTParser(f, customBackgroundGenes, queryGenes);
					//gmtParser = new GMTParser(f.getAbsolutePath(), customBackgroundGenes, queryGenes);
				}
				else{
					gmtParser = new GMTParser(f, genomicBackgroundGenes, queryGenes);
					//gmtParser = new GMTParser(f.getAbsolutePath(), genomicBackgroundGenes, queryGenes);
					
				}
				gmtParser.setMaxGenesInCategory(optionsPanel.maxGenesInSet());
				gmtParser.setMinGenesInCategory(optionsPanel.minGenesInSet());
				gmtParser.addOptionsListener(this);
			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	
	
/*	public static File findFile(File dir, String species) {
		
		if(species.startsWith("Human")){
		
			//return new File(ClassLoader.getSystemResource("uk/ac/babraham/giraph/Utilities/Human_Homo_sapiens.GRCh38.80_gene_info.txt"));
			return new File("uk/ac/babraham/giraph/Utilities/Human_Homo_sapiens.GRCh38.80_gene_info.txt");
		}
		else if(species.startsWith("Mouse")){
			
			return new File("uk/ac/babraham/giraph/Utilities/Mouse_Mus_musculus.GRCm38.80_gene_info.txt");			
		}
		
		
		/*File [] files = dir.listFiles();
		if(files == null){
			
		}
		
		for (int i = 0; i < files.length; i++){
		
			if (files[i].getName().startsWith(species) && (files[i].getName().endsWith(".txt") || files[i].getName().endsWith(".gmt"))){

				System.out.println("file is " + files[i].getName());
				return files[i];				
			}
	    }		
		System.err.println("No reference file found in " + dir.getAbsolutePath());		
		return null;
		
	}
*/		
		
	// When all the functional info has been loaded, the rest of the options can be parsed. 
	public void gmtFileParsed(){
		
		if ((optionsPanel.loadingMessageThread() != null) && (optionsPanel.loadingMessageThread().isAlive())){
			optionsPanel.loadingMessageThread().interrupt();
		}	
		
		this.geneListCollection = gmtParser.getGeneListCollection();
		
		System.out.println(geneListCollection.getAllGeneLists().length + " genelists have been created (from options frame)");
		
		// let the app know we've finished parsing and have got the geneListcollection
		//pl.inputFileParsingComplete(gmtParser.getGeneListCollection());
		
		calculatePValues();
		//setFilters();
	}
	
	private void pValuesCalculated(){
		
		// let the progress listener know that the parsing is complete
		pl.inputFileParsingComplete(geneListCollection, queryGenes, customBackgroundGenes, genomicBackgroundGenes);
		
		setFilters();
		
	}
	
	
	private void setFilters(){
		
		// This needs to be passed on to the main app so the graph panel knows what the filters are
		//giraphApplication.getInstance().setFilter(new DataFilter(r, optionsPanel.minGenesInCategory(), optionsPanel.pValue()));
		
		//giraphApplication.getInstance().setFilter(new DataFilter(geneListCollection, optionsPanel.minGenesInCategory(), optionsPanel.pValue()));
		pl.setFilters(optionsPanel.pValue());
		
		//pl.setFilters(optionsPanel.minGenesInCategory(), optionsPanel.pValue());		
	}
	
	
	/**check whether the multiple testing correction is working properly and gets applied to the p values in the gl collection */
	private void calculatePValues(){
			
		PValue [] allPValues = new PValue[geneListCollection.getAllGeneLists().length];
		
		int noOfBackgroundGenes;
		
		if(usingCustomBackground){
			noOfBackgroundGenes = customBackgroundGenes.getAllGenes().length;
		}
		else{
			noOfBackgroundGenes = genomicBackgroundGenes.getAllGenes().length;
		}
		
		System.err.println("Performing Fisher's Exact tests (from options frame)....");
		
		// I don't think this should really be here, but we'll put it here for now
		// TODO: This is very slow - I think its the actual Fishers test calculation so don't think I can speed it up, but do need to put in a message.
		GeneList [] allGeneLists = geneListCollection.getAllGeneLists();
		int noOfQueryGenes = queryGenes.getAllGenes().length;
		
		for  (int i = 0; i < allGeneLists.length; i++){
			
			allPValues[i] = calculatePValueUsingFishersTest(allGeneLists[i], noOfBackgroundGenes, noOfQueryGenes);			
			
			allGeneLists[i].setPvalue(allPValues[i]);
		}
		
		// The multiple testing correction can only be performed once all the p values have been calculated.
		MultipleTestingCorrection.benHochFDR(allPValues);
		
		System.out.println("Finished p value calculations (from options frame)");
		
		pValuesCalculated();
	}

	
	/** check the no of background genes in category vs total no of genes in category */
	private static PValue calculatePValueUsingFishersTest(GeneList gl, int noOfBackgroundGenes, int noOfQueryGenes){		
		
		/** The GeneList contains the number of genes in it and the number in the category in the gmt file. 
		/** Get the right format for the input into Fishers Test  */
		
		// a is no of query genes in category 
		int a = gl.getGenes().length;
		
		// b is total no of genes in the genome (gmt file) in category
		int b = gl.getFunctionalSetInfo().noOfBackgroundGenesInCategory();
		
		// c is no of query genes not in category 
		int c = noOfQueryGenes - a;
		
		// d is no of background genes that are not in category 
		int d = noOfBackgroundGenes - b;		
		
		// To do the modified Fishers test that they use in David (EASE), we subtract 1 from a
		if( a > 0){
			a = a-1;
		}
		
		double[] fishersExactResult = new FishersExactTest().fishersExactTest(a, b, c, d);
		
		/** select the right tail */
		PValue pval = new PValue(fishersExactResult[2]);				
		
		return(pval);						
	}
				
	public void addProgressListener(ProgressListener pl){
		
		this.pl = pl;		
	}		
}

	
	
	
	
	
	
	
	
	
	