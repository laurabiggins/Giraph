package uk.ac.babraham.giraph.DataParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import javax.swing.JDialog;
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

public class ExternalResultsParser implements Cancellable, Runnable {

	// filepath for the gmt file
	private File file;

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

	public void startParsing () {
		Thread t = new Thread(this);
		t.start();
	}
	
	public void run(){

		GeneListCollection geneListCollection = parseResultsFile();

		if (geneListCollection == null) return;
		
		progressComplete(geneListCollection);
	}	

	public GeneListCollection parseResultsFile() {

		setColumnInfoForFile();

		System.err.println("queryGeneColValue = " + queryGeneColValue);

		BufferedReader br = null;

		try {
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
					progressExceptionReceived(new Exception ("Ran out of file before skipping all of the header lines"));
					return null;
				}
				checkHeader(line);
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

				if (lineCount == 1){

					System.err.println("queryGeneColValue = " + queryGeneColValue);

					System.err.println("sections[queryGeneColValue] = " + sections[queryGeneColValue]);
				}
				
				String genesForParsing = getGenesForParsing(line, sections, queryGeneColValue);
				
				genesInCategory = parseGenes(genesForParsing, geneDelimitersValue);
				//genesInCategory = parseGenes(sections[queryGeneColValue], geneDelimitersValue);


				if(genesInCategory == null){
					String msg = "Could not find any genes in category on line " + lineCount + ", skipping this line";
					progressWarningReceived(new giraphException(msg)); 
					continue;
				}
				// either we're not parsing the list of genes properly, or it may be that some reports contain lists of genes with only one gene in - clearly this should not be included.
				if(genesInCategory.length < 2){

					String msg = ("Only 1 gene found for category on line " + lineCount + ", skipping this line");
					progressWarningReceived(new giraphException(msg));
					continue; // Skip this line...	  

				}

				// Create new functional category for each line
				FunctionalSetInfo functionalSetInfo = new FunctionalSetInfo();

				/** These must have been specified */
				functionalSetInfo.setName(sections[categoryNameColValue]);				
				
				// description isn't required
				/*if(categoryDescriptionColValue >=0) {
					functionalSetInfo.setDescription(sections[categoryDescriptionColValue]);
				}
				else {
					functionalSetInfo.setDescription(sections[categoryNameColValue]);
				}		
*/ 
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
					
					if(isNumeric(sections[pValueColValue])) {
						pvalue.setQ(Double.valueOf(sections[pValueColValue]));
					}
					else {
						String msg = "Unexpected values found in p-value column";
						
						progressExceptionReceived(new giraphException(msg));
						
						giraphApplication.getInstance().menuBar.clearApplicationNoChoice();
						return null;
					}

					gl.setPvalue(pvalue);

					// add the gene list to the collection
					try {
						glc.addGeneList(gl);
					} 
					catch(giraphException ge) {
						
						progressWarningReceived(ge);
					}
					
				}				
			}
			// We're finished with the file.
			br.close();
			return glc;

		}
		catch (IOException ioe) {
			progressExceptionReceived(ioe);
			return null;
		}

	}	

	public String getGenesForParsing(String line, String [] sections, int queryGeneColValue){
		
		return(sections[queryGeneColValue]);
		
	}

	public String [] parseGenes(String genes, String delimiter){
		
		return genes.trim().split(delimiter);
		
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

	protected void progressExceptionReceived (Exception e) {
		Enumeration<ProgressListener>en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().progressExceptionReceived(e);
		}
	}

	protected void progressComplete (Object o) {
		Enumeration<ProgressListener>en = listeners.elements();
		while (en.hasMoreElements()) {
			en.nextElement().progressComplete("results_parser", o);;
		}
	}


	public void cancel() {
		cancel = true;
	}

	public void addProgressListener(ProgressListener pl){
		if (!listeners.contains(pl)) {
			listeners.add(pl);
		}
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

	public static boolean isNumeric(String strNum) {
		try {
			Double.parseDouble(strNum);
		} catch (NumberFormatException | NullPointerException nfe) {
			return false;
		}
		return true;
	}

	public void checkFile(String line){

	}

	public void checkHeader(String line){

	}

}	

