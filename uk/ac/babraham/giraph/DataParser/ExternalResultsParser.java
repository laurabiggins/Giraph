package uk.ac.babraham.giraph.DataParser;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;

import uk.ac.babraham.giraph.giraphApplication;
import uk.ac.babraham.giraph.giraphException;
import uk.ac.babraham.giraph.DataTypes.FunctionalSetInfo;
import uk.ac.babraham.giraph.DataTypes.Gene;
import uk.ac.babraham.giraph.DataTypes.GeneCollection;
import uk.ac.babraham.giraph.DataTypes.GeneList;
import uk.ac.babraham.giraph.DataTypes.GeneListCollection;
import uk.ac.babraham.giraph.DataTypes.PValue;
import uk.ac.babraham.giraph.Dialogs.Cancellable;

/** 
 * This should be the generic class and then we should have specific DAVID, GOrilla and text parsers.
 *  
 * This class is for parsing an external results file so that we can display it without having to rerun the analysis within Giraph.
 * 
 * Error checking - skip a line if we don't have the right info
 * 
 * rearrange the options on screen
 * 
 * remove some of the options - check what we actually need.
 * 
 * 
 * @author bigginsl
 *
 */

public class ExternalResultsParser implements Cancellable{

	// filepath for the gmt file
	private File file;
	
	// create a new geneCollection then set this as the query genes in the application
	//public GeneCollection gc;
		
	// create a new geneListCollection
	//public GeneListCollection glc;
	
	// so we can notify the app when we're ready
	public ProgressListener pl;
	
	public boolean cancel;
	
	public String delimitersValue = "\t";
	public String geneDelimitersValue = ",";
	
	public static int startRowValue = 0;
	public static int queryGeneColValue = -1;
	public static int categoryNameColValue = -1;
	public static int categoryDescriptionColValue = -1;
	public static int pValueColValue = -1;
	
	public JDialog options = null;
	
	public ExternalResultsParser(File file){
		
		this.file = file;
		
	}
	
	public void setCancel(boolean cancel){
		this.cancel = cancel;
	}
	
	public void run(){
		
		try {
			GeneListCollection geneListCollection = parseResultsFile();
			
			if(geneListCollection != null){
				pl.externalResultsParsed(geneListCollection);
			}
						
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}					
	}	
	
	public GeneListCollection parseResultsFile() throws Exception{
	
		setColumnInfoForFile();
				
		System.err.println("queryGeneColValue = " + queryGeneColValue);
		/*GenericResultsFileParser grfp = null;
		
		if (options == null) {
			options = new JDialog(giraphApplication.getInstance());
			options.setModal(true);
			grfp = new GenericResultsFileParser(file);
			options.setContentPane(grfp.createGenericAnnotationParserOptions(options));
			//options.setContentPane(new GenericAnnotationParserOptions(options));
			options.setSize(700,400);
			options.setLocationRelativeTo(null);
		}

		// We have to set cancel to true as a default so we don't try to 
		// proceed with processing if the user closes the options using
		// the X on the window.

		options.setTitle("Format for "+file.getName()+"...");
		
		cancel = true;
		options.setVisible(true);
				
		//if (cancel) {
		if (grfp.cancel) {
			System.err.println("cancel from externalResultsParser");
			progressCancelled();
			return null;
		}
		
		*/
		// When continue is pressed, cancel is set to false.
		// check we've got the required info
		
		BufferedReader br;

		if (file.getName().toLowerCase().endsWith(".gz")) {
			br = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(file))));	
		}
		else {
			br = new BufferedReader(new FileReader(file));
		}

		
		String line;
		// First skip the header lines
		for (int i=0;i<startRowValue;i++) {
			line = br.readLine();
			if (line == null) {
				throw new Exception ("Ran out of file before skipping all of the header lines");
			}
		}

		int maxIndexValue = 0;
		if (queryGeneColValue > maxIndexValue) maxIndexValue = queryGeneColValue;
		if (categoryNameColValue > maxIndexValue) maxIndexValue = categoryNameColValue;
		if (categoryDescriptionColValue > maxIndexValue) maxIndexValue = categoryDescriptionColValue;
		if (pValueColValue > maxIndexValue) maxIndexValue = pValueColValue;
		
		
		// create the gene collection
		GeneCollection gc = new GeneCollection();
		
		// create the genelist collection
		GeneListCollection glc = new GeneListCollection();
		
		int lineCount = 0;
		// Now process the rest of the file
		while ((line = br.readLine())!= null) {
			++lineCount;

			if (cancel) {
				progressCancelled();
				return null;
			}

			if (lineCount%1000 == 0) {
				progressUpdated("Read "+lineCount+" lines from "+file.getName(),0,1);
			}

			String [] sections = line.split(delimitersValue,-1);

			// Check to see if we've got enough data to work with
			if (maxIndexValue >= sections.length) {
				progressWarningReceived(new giraphException("Not enough data ("+sections.length+") to get a probe name on line '"+line+"'"));
				continue; // Skip this line...						
			}

			String [] genesInCategory = null;
			
			System.err.println("queryGeneColValue = " + queryGeneColValue);
			
			System.err.println("sections[queryGeneColValue] = " + sections[queryGeneColValue]);
			
			genesInCategory = parseGenes(sections[queryGeneColValue], geneDelimitersValue);
			
			
/*			if(gorilla){
				GOrillaParser gorillaParser = new GOrillaParser(sections[queryGeneColValue], geneDelimitersValue);
				genesInCategory = gorillaParser.parseGenes();
			}
			else if(david){
				DavidParser davidParser = new DavidParser(sections[queryGeneColValue], geneDelimitersValue);
				genesInCategory = davidParser.parseGenes(); 
			}
*/			
			if(genesInCategory == null){
				String msg = "Could not find any genes in category on line " + lineCount + ", skipping this line";
				System.err.println("line = " + line);
				progressWarningReceived(new giraphException(msg)); // this doesn't seem to do anything
				JOptionPane.showMessageDialog(null, msg, "No genes to analyse", JOptionPane.ERROR_MESSAGE);
				continue;
				//return null;
			}
			// either we're not parsing the list of genes properly, or it may be that some reports contain lists of genes with only one gene in - clearly this should not be included.
			if(genesInCategory.length < 2){
				
				String msg = ("Only 1 gene found for category on line " + lineCount + ", skipping this line");
				progressWarningReceived(new giraphException(msg));
				JOptionPane.showMessageDialog(null, msg, "No genes to analyse", JOptionPane.ERROR_MESSAGE);
				System.err.println("fewer than 2 genes found in gene list, line skipped");
				continue; // Skip this line...	
				//return null;
			}
						  
			// Create new functional category for each line
			FunctionalSetInfo functionalSetInfo = new FunctionalSetInfo();
			
			
			/** These must have been specified */
			functionalSetInfo.setName(sections[categoryNameColValue]);
						
			// description isn't required
			if(categoryDescriptionColValue >=0) functionalSetInfo.setDescription(sections[categoryDescriptionColValue]);
			else functionalSetInfo.setDescription(sections[categoryNameColValue]);
			System.err.println("adding description " + functionalSetInfo.description());			
			
			/** This will be changed into a gene list once all the genes have been processed */ 
			ArrayList<Gene> geneArrayList = new ArrayList<Gene>();
			
			
			/** cycle through all the genes, this is going to assume that there are no duplicated genes within the gene set category */ 
			for (int i = 0; i < genesInCategory.length; i++){
				
				String geneSym = cleanGene(genesInCategory[i].toUpperCase());			
				
				// if gene doesn't already exist, create it
				if (gc.getGene(geneSym) == null){
					
					gc.addGene(new Gene(geneSym));
				}	
				
				// add the gene to the geneArrayList
				geneArrayList.add(gc.getGene(geneSym));
			}						
			
			// create gene list from the gene list array
			if(geneArrayList.size() > 0){
								
				GeneList gl = new GeneList();
				gl.setGenes(geneArrayList.toArray(new Gene[0]));
				
				gl.setFunctionalSetInfo(functionalSetInfo);
				
				PValue pvalue = new PValue();
				//pvalue.setQ(Double.parseDouble(sections[pValueColValue]));
				pvalue.setQ(Double.valueOf(sections[pValueColValue]));
				
				gl.setPvalue(pvalue);
				
				// add the gene list to the collection
				glc.addGeneList(gl);
				
				System.err.println("adding gene list " + functionalSetInfo.description());
			}				
		}
		// We're finished with the file.
		br.close();
		
		//options.dispose();
		options = null;
		
		return glc;
	}	
		
	public String[] parseGenes(String string, String delimiter) {
		// TODO Auto-generated method stub
		System.err.println("running the generic method - why?");
		return null;
	}

	public Vector<ProgressListener>listeners = new Vector<ProgressListener>();
	
	protected void progressCancelled () {
		Enumeration<ProgressListener>en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().progressCancelled();
		}
	}

	protected void progressUpdated (String message, int current, int max) {
		Enumeration<ProgressListener>en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().progressUpdated(message, current, max);
		}
	}

	protected void progressWarningReceived (Exception e) {
		Enumeration<ProgressListener>en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().progressWarningReceived(e);
		}
	}
	
	public void cancel() {
		// TODO Auto-generated method stub		
	}
	
	public void addProgressListener(ProgressListener pl){
		this.pl = pl;
	}
	
	public String cleanGene(String str) {
		
		String str1 = str.replaceAll("\t", "");
		String str2 = str1.replaceAll("\\s+", "");
		String str3 = str2.replaceAll("\"", "");
		
		return str3;
	}
	

	public String getDelimitersValue(){
		
		return delimitersValue;
	}
	
	public void setColumnInfoForFile(){
		
		System.err.println("running the generic method for setting column info");
		
		delimitersValue = "\t";
		geneDelimitersValue = ",";
		
		startRowValue = 0;
		queryGeneColValue = -1;
		categoryNameColValue = -1;
		categoryDescriptionColValue = -1;
		pValueColValue = -1;
	}
	
}	
	
