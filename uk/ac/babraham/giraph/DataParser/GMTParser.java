package uk.ac.babraham.giraph.DataParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Vector;
import java.util.zip.GZIPInputStream;

import uk.ac.babraham.giraph.giraphException;
import uk.ac.babraham.giraph.DataTypes.FunctionalSetInfo;
import uk.ac.babraham.giraph.DataTypes.Gene;
import uk.ac.babraham.giraph.DataTypes.GeneCollection;
import uk.ac.babraham.giraph.DataTypes.GeneList;
import uk.ac.babraham.giraph.DataTypes.GeneListCollection;
import uk.ac.babraham.giraph.Dialogs.Cancellable;

/** 
 * this parses the info for all the GO terms etc. It creates FunctionalSetInfo objects which are contained within the FunctionalSetInfoCollection.
 * It is specific for GMT files and mostly uses the Bader files. http://baderlab.org/GeneSets
 * http://download.baderlab.org/EM_Genesets/March_14_2013/Mouse/symbol/Mouse_GO_AllPathways_no_GO_iea_March_14_2013_symbol.gmt
 * 
 * @author bigginsl
 *
 */

public class GMTParser implements Runnable, Cancellable {
	
	// filepath for the gmt file
	public String filepath;
	
	// the maximum no of genes in functional category before we disregard it 
	private int maxGenesInCategory = 1000;
	
	// the maximum no of genes in functional category before we disregard it 
	private int minGenesInCategory = 10;
	
	public boolean cancel;
	
	// the background genes
	private GeneCollection backgroundGenes; 
	
	// the query genes
	private GeneCollection queryGenes; 
	
	// all the genes in the gmt file
	protected GeneCollection allGMTgenes;
	
	private GeneListCollection geneListCollection;
	
	
	public void setMaxGenesInCategory(int n){
		maxGenesInCategory = n;
	}
	
	public void setMinGenesInCategory(int n){
		minGenesInCategory = n;
	}
	
	public GMTParser(String file, GeneCollection backgroundGenes, GeneCollection queryGenes){
		
		this.filepath = file;
		
		this.backgroundGenes = backgroundGenes;
		
		this.queryGenes = queryGenes;
		
		this.geneListCollection = new GeneListCollection();
		
	}
	
	public GMTParser(String file) {

		this.filepath = file;

	}

	public void startParsing () {
		Thread t = new Thread(this);
		t.start();
	}
	
	public void run(){
		
		if(filepath == null){
			System.err.println("No gmt file found to parse");
		}
		else{
			System.out.println("Now we'd like to load and parse the gmt file " + filepath);
			try {
				try {
					importFile(filepath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (giraphException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}	
	}

	private void importFile (String filepath) throws giraphException, IOException{
		
		try {

			BufferedReader in = null;
			
			if (filepath.endsWith(".gz")) {
				
				in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filepath))));	
				
			}
			else {

				in = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));	
			}
			
			String lineOfFile;	
					
			int counter = 1;
			int allLinesCounter = 1;
			
			while((lineOfFile = in.readLine()) != null){  
			
				if(counter%1000 ==0) {
					System.out.println(counter + " lines parsed");
				}
				String[] result = lineOfFile.split("\t");								
				allLinesCounter++;
				
				try{
					parseLine(result, allLinesCounter);
					counter++;
				}
				catch(giraphException e){
					
					progressWarningReceived(e);
				}
			}
			
			if (counter < 3){
				progressExceptionReceived(new giraphException("couldn't parse GMT file"));
			}			

			else{
				progressComplete(geneListCollection);
			}	
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}	
	
	/**
	 * Function to just parse the genes, this can be used to get all the background genes.
	 * @throws giraphException 
	 **/
	public void parseGenes(String [] result, int linenumber) throws giraphException {
		
		String[] nameInfo = result[0].split("%"); 
		
		if(nameInfo.length < 3){
			throw new giraphException("category name info was not in the expected format on line " + linenumber);
		}
		
		else{
				
			/** cycle through all the genes, this is going to assume that there are no duplicated genes within the gene set category */ 
			for (int i = 2; i < result.length; i++){
				
				String geneSym = cleanGene(result[i].toUpperCase());
				
				if(allGMTgenes.doesNotContain(geneSym)){
					
					Gene g = new Gene(cleanGene(geneSym));
					
					allGMTgenes.addGene(g);							
				}
			}
		}	
	}
	
	// parses the line of a file
	protected void parseLine(String [] result, int linenumber) throws giraphException {	
		// We don't want the categories that have a ridiculous number of genes 
		if ((result.length < maxGenesInCategory) && (result.length > minGenesInCategory)) {
			
			// Each line should be different so a new FunctionalSetInfo needs to be created. 
			FunctionalSetInfo functionalSetInfo = null;// = new FunctionalSetInfo();
			
			ArrayList<Gene> geneArrayList = new ArrayList<Gene>();
			
			try {			
				// This is specific for the gmt files. http://baderlab.org/GeneSets
				String[] nameInfo = result[0].split("%"); 
				
				if(nameInfo.length < 3){
					throw new giraphException("category name info was not in the expected format on line " + linenumber + "\n" + result[0]);
				}
				
				else{
				
					int noOfBackgroundGenesInCategory = 0;
						
					// cycle through all the genes, this is going to assume that there are no duplicated genes within the gene set category  
					for (int i = 2; i < result.length; i++){
						
						String geneSym = cleanGene(result[i].toUpperCase());
						
						// only create a gene list if a query gene is in the category 
						if(backgroundGenes.contains(geneSym)){
							
							noOfBackgroundGenesInCategory++;
							
							if(queryGenes.contains(geneSym)){
								
								geneArrayList.add(queryGenes.getGene(geneSym));
								
								if(functionalSetInfo == null){
									
									functionalSetInfo = new FunctionalSetInfo();
									
									functionalSetInfo.setName(nameInfo[0]);
									
									// this is required for the stats
									functionalSetInfo.setTotalNoOfGenesInCategory(result.length - 2);
									functionalSetInfo.setDescription(result[1]);
								}
							}	
						}	
					}
					// create gene list from the gene list array
					if(geneArrayList.size() > 0){
						GeneList gl = new GeneList();
						gl.setGenes(geneArrayList.toArray(new Gene[0]));
						functionalSetInfo.setNoOfBackgroundGenesInCategory(noOfBackgroundGenesInCategory);
						gl.setFunctionalSetInfo(functionalSetInfo);
						// add the gene list to the collection
						try {
							geneListCollection.addGeneList(gl);

						}
						catch(giraphException ge) {
							
							progressWarningReceived(ge);
						}
					}	
				}	
			}
			catch (java.lang.NumberFormatException e){
				String error = e.toString();
				System.err.println(error);
			}
		}
	}  
		
	private String cleanGene(String str) {
		
		String str1 = str.replaceAll("\t", "");
		String str2 = str1.replaceAll("\\s+", "");
		String str3 = str2.replaceAll("\"", "");
		
		return str3;
	}
	
	public GeneCollection getAllGMTgenes(){
		return allGMTgenes;
	}
	
	public GeneListCollection getGeneListCollection(){
		return geneListCollection;
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
			en.nextElement().progressComplete("gmt_file_parser", o);;
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
