package uk.ac.babraham.giraph.DataParser;

import java.util.Enumeration;
import java.util.Vector;

/** 
 * This class parses the gene names that are entered, if the query gene is in the genomic background list it is saved in the GeneCollection genes.
 * 
 * The test for significance is carried out from the optionsFrame.
 * 
 */


import javax.swing.JOptionPane;

import uk.ac.babraham.giraph.giraphApplication;
import uk.ac.babraham.giraph.giraphException;
import uk.ac.babraham.giraph.DataTypes.GeneCollection;
import uk.ac.babraham.giraph.Dialogs.Cancellable;

public class GeneNameParser implements Runnable, Cancellable{
	
	// The GeneCollection containing the parsed query/input genes
	private GeneCollection genes;
	
	// The background genes to check the query/input genes against
	private GeneCollection backgroundGenes; 
	
	public boolean cancel;
	
	// The query genes
	private String listOfGeneNames;
	
	//protected OptionsListener ol;
	
	public GeneNameParser(String listOfGeneNames, GeneCollection backgroundGenes){
		
		this.listOfGeneNames = listOfGeneNames;
		this.backgroundGenes = backgroundGenes;
		this.genes = new GeneCollection();
		Thread t = new Thread(this);
		t.start();
	}

	public void run(){
		
		try {
			parseGenes();
		} catch (giraphException e) {
			// TODO Auto-generated catch block
			progressWarningReceived(e);
		}
	}
	
	public void parseGenes() throws giraphException{
		
		if(splitList(listOfGeneNames) == null){
			String msg =  ("Something went wrong when trying to parse the genes.");
			//System.err.println(msg);
			throw new giraphException(msg);
			//JOptionPane.showMessageDialog(giraphApplication.getInstance(), msg, "couldn't parse genes", JOptionPane.ERROR_MESSAGE);
		}
		else {
			String [] geneNames = splitList(listOfGeneNames);
			System.err.println("number of genes parsed = " + geneNames.length);
			int unmatchedCount = 0;
			
			// check if they're in the background list
			for (int i = 0; i < geneNames.length; i++){
			
				if(backgroundGenes.contains(cleanGene(geneNames[i]))){
			
					genes.addGene(backgroundGenes.getGene(cleanGene(geneNames[i])));
				}	
				else{
					unmatchedCount++;
				}
			}
			
			String unmatchedCountMessage = unmatchedCountMessage(unmatchedCount);
			
			if((genes.getAllGenes().length > 3) && (genes.getAllGenes().length < backgroundGenes.getAllGenes().length)){
				String msg = "Imported " + genes.getAllGenes().length + " genes, the first one was "+ genes.getAllGenes()[0].getGeneSymbol()+
						", the last one was "+genes.getAllGenes()[genes.getAllGenes().length - 1].getGeneSymbol();
				progressUpdated(msg, 0, 0);
				progressComplete(genes);
				//genesImported();
			}
			else if(genes.getAllGenes().length <= 3){
				String msg = ("Fewer than 3 valid query genes were identified, " + unmatchedCountMessage); 
				//JOptionPane.showMessageDialog(giraphApplication.getInstance(), msg, "couldn't parse genes", JOptionPane.ERROR_MESSAGE);
				throw new giraphException(msg);
			}
			else if(genes.getAllGenes().length >= backgroundGenes.getAllGenes().length){
				String msg = ("The number of background genes does not exceed the number of query genes, please enter a different set of genes."); 
				//JOptionPane.showMessageDialog(giraphApplication.getInstance(), msg, "couldn't parse genes", JOptionPane.ERROR_MESSAGE);
				throw new giraphException(msg);
			}			
		}		
	}
	
	protected String unmatchedCountMessage(int unmatchedCount){
		return (unmatchedCount + " ");
	}
	
/*	protected void genesImported(){
		
	}
*/	
	public GeneCollection geneCollection(){
		return genes;
	}
	
	private static String [] splitList(String listOfGeneNames){
		
		if(listOfGeneNames.contains("\n")){
			System.err.println("splitting by \n");
			return(listOfGeneNames.split("\n"));
		}
		else if(listOfGeneNames.contains(",")){
			System.err.println("splitting by ,");
			return(listOfGeneNames.split(","));
		}
		else if(listOfGeneNames.contains("\t")){
			System.err.println("splitting by \t");
			return(listOfGeneNames.split("\t"));
		}
		else if(listOfGeneNames.contains(" ")){
			System.err.println("splitting by whitespace");
			return(listOfGeneNames.split(" "));
		}
		else {
			String msg =  ("Unrecognised separating character, please separate the genes using tabs, commas or spaces");
			JOptionPane.showMessageDialog(giraphApplication.getInstance(), msg, "couldn't parse genes", JOptionPane.ERROR_MESSAGE);
			return null;
		}		
	}
	
	private String cleanGene(String str) {
		
		String str1 = str.replaceAll("\t", "");
		String str2 = str1.replaceAll("\\s+", "");
		String str3 = str2.replaceAll("\"", "");
		
		return str3.toUpperCase();
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
			en.nextElement().progressComplete("gene_name_parser", o);;
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

	
}
