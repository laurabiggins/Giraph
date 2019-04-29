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
import uk.ac.babraham.giraph.DataParser.GMTGeneParser;
import uk.ac.babraham.giraph.DataParser.GMTParser;
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
	GeneNameParser queryGeneParser;
	GeneNameParser customBackgroundGeneParser = null;
	GMTParser gmtParser;
	// just for parsing the genes from the GMT file
	GMTGeneParser gmtGeneParser;
	
	GeneCollection queryGenes;

	GeneCollection customBackgroundGenes = null;
	GeneListCollection geneListCollection;
	private boolean usingCustomBackground = false;
		
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
			
			else if(optionsPanel.getBackgroundGenesOption().equals("Enter custom background genes") && (optionsPanel.backgroundGenes().isEmpty())){
				String msg = ("Please enter some background genes or select the option to Use all genes in genome.");
				JOptionPane.showMessageDialog(this, msg, "No genes to analyse", JOptionPane.ERROR_MESSAGE);
			}
			
			else if(optionsPanel.minGenesInSet() > optionsPanel.maxGenesInSet()){
				JOptionPane.showMessageDialog(this, "Minimum number of genes in set cannot be greater than maximum.", "Number of genes needs adjusting", JOptionPane.ERROR_MESSAGE);
				return;
			}
			
			else if(optionsPanel.validGeneSetFilepath() == null) {
				String msg = ("A GMT file containing functional categories is required.");
				JOptionPane.showMessageDialog(this, msg, "No valid GMT file", JOptionPane.ERROR_MESSAGE);
			}
			
			else{
					
				giraphApplication.getInstance().removeOldData();
								
				// parse the genes from the gmt file so that we've got a genomic background set to work from
				gmtGeneParser = new GMTGeneParser(optionsPanel.validGeneSetFilepath());
				gmtGeneParser.addOptionsListener(this);
						
				setVisible(false);
				dispose();
			}		
		}
	}	
			
	public void genomicBackgroundGenesImported() {
		
		if(gmtGeneParser.getAllGMTgenes() != null) {
		
			System.err.println("imported genes from gmt file: " + gmtGeneParser.getAllGMTgenes().getAllGenes().length);
			
			if(optionsPanel.getBackgroundGenesOption().equals("Enter custom background genes")){
				System.err.println("loading custom background genes");
				loadCustomBackgroundGenes();			
			}
			// use GMT genes as background
			else {
				loadQueryGenes(optionsPanel.queryGenes(), gmtGeneParser.getAllGMTgenes());
			}
		}
		else {
			JOptionPane.showMessageDialog(this, "couldn't parse genes from GMT file", "No genes to analyse", JOptionPane.ERROR_MESSAGE);
		}
			
	}		
	
	public void loadCustomBackgroundGenes() {
		
		customBackgroundGeneParser = new CustomBackgroundGeneParser(optionsPanel.backgroundGenes(), gmtGeneParser.getAllGMTgenes());
		customBackgroundGeneParser.addOptionsListener(this);
		usingCustomBackground = true;
	}
	
	// If customBackgroundGenes have been used then load the query genes using these as the background
	public void customBackgroundGenesImported(){
		
		if(customBackgroundGeneParser.geneCollection() != null) {
			System.err.println("custom background genes imported");
			customBackgroundGenes = customBackgroundGeneParser.geneCollection();
			loadQueryGenes(optionsPanel.queryGenes(), customBackgroundGenes);
		}
		else {
			
			JOptionPane.showMessageDialog(this, "couldn't load custom background genes", "No genes to analyse", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	public void loadQueryGenes(String queryGenes, GeneCollection backgroundGenes) {
		
		queryGeneParser = new QueryGeneParser(queryGenes, backgroundGenes);
		queryGeneParser.addOptionsListener(this);
	}	
	
	public void queryGenesImported(){
		
		if(queryGeneParser.geneCollection() != null) {
			queryGenes = queryGeneParser.geneCollection();
			
			if(usingCustomBackground) {

				gmtParser = new GMTParser(optionsPanel.validGeneSetFilepath(), customBackgroundGenes, queryGenes);
			}
			else {
				gmtParser = new GMTParser(optionsPanel.validGeneSetFilepath(), gmtGeneParser.getAllGMTgenes(), queryGenes);
			}
			gmtParser.setMaxGenesInCategory(optionsPanel.maxGenesInSet());
			gmtParser.setMinGenesInCategory(optionsPanel.minGenesInSet());
			gmtParser.addOptionsListener(this);
		}
		else {
			JOptionPane.showMessageDialog(this, "couldn't load query genes", "No genes to analyse", JOptionPane.ERROR_MESSAGE);
		}
	}
	
	
	// When all the functional info has been loaded, the rest of the options can be parsed. 
	/**
	 * This is only called when all the categories have been loaded.
	 */
	public void gmtFileParsed(){
		
		if ((optionsPanel.loadingMessageThread() != null) && (optionsPanel.loadingMessageThread().isAlive())){
			optionsPanel.loadingMessageThread().interrupt();
		}	
		
		this.geneListCollection = gmtParser.getGeneListCollection();
		
		System.out.println(geneListCollection.getAllGeneLists().length + " genelists have been created (from options frame)");
		
		calculatePValues();
	}
	
	private void pValuesCalculated(){
		
		// let the progress listener know that the parsing is complete
		//pl.inputFileParsingComplete(geneListCollection, queryGenes, customBackgroundGenes, gmtGeneParser.getAllGMTgenes());
		giraphApplication.getInstance().inputFileParsingComplete(geneListCollection, queryGenes, customBackgroundGenes, gmtGeneParser.getAllGMTgenes(), optionsPanel.pValue());
				
	}
	
	/**check whether the multiple testing correction is working properly and gets applied to the p values in the gl collection */
	private void calculatePValues(){
			
		PValue [] allPValues = new PValue[geneListCollection.getAllGeneLists().length];
		
		int noOfBackgroundGenes;
		
		if(usingCustomBackground){
			noOfBackgroundGenes = customBackgroundGenes.getAllGenes().length;
		}
		else{
			noOfBackgroundGenes = gmtGeneParser.getAllGMTgenes().getAllGenes().length;
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
		
		double[] fishersExactResult = FishersExactTest.fishersExactTest(a, b, c, d);
		
		/** select the right tail */
		PValue pval = new PValue(fishersExactResult[2]);				
		
		return(pval);						
	}
				
}	
	