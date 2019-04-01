package uk.ac.babraham.giraph.DataParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.zip.GZIPInputStream;

import javax.swing.JOptionPane;

import uk.ac.babraham.giraph.CrashReporter;
import uk.ac.babraham.giraph.giraphException;
import uk.ac.babraham.giraph.DataTypes.FunctionalSetInfo;
import uk.ac.babraham.giraph.DataTypes.Gene;
import uk.ac.babraham.giraph.DataTypes.GeneCollection;
import uk.ac.babraham.giraph.DataTypes.GeneList;
import uk.ac.babraham.giraph.DataTypes.GeneListCollection;
import uk.ac.babraham.giraph.giraphApplication;

/** 
 * this parses the info for all the GO terms etc. It creates FunctionalSetInfo objects which are contained within the FunctionalSetInfoCollection.
 * It is specific for the Bader files. http://baderlab.org/GeneSets
 * http://download.baderlab.org/EM_Genesets/March_14_2013/Mouse/symbol/Mouse_GO_AllPathways_no_GO_iea_March_14_2013_symbol.gmt
 * That file is located in D:\D_drive_other_computer\giraph
 * 
 * 
 *  
 * @author bigginsl
 *
 */

public class GMTParser implements Runnable{
	
	// filepath for the gmt file
	public String filepath;
	
	// the maximum no of genes in functional category before we disregard it 
	private int maxGenesInCategory = 1000;
	
	// the maximum no of genes in functional category before we disregard it 
	private int minGenesInCategory = 10;
	
	// the options listener
	protected OptionsListener ol;
	
	// the background genes
	private GeneCollection backgroundGenes; 
	
	// the query genes
	private GeneCollection queryGenes; 
	
	// all the genes in the gmt file
	protected GeneCollection allGMTgenes;
	
//	private ArrayList<GeneList> geneListArrayList;
	
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
		
		Thread t = new Thread(this);
		t.start();
	}
	
	public GMTParser(String file) {

		this.filepath = file;
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
				//giraphApplication.getInstance().functionalSetInfoCollection = new FunctionalSetInfoCollection();
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
		
		//FileReader reader;
		
		try {
			//reader = new FileReader(filepath);
			//BufferedReader in = new BufferedReader(reader);
			
			BufferedReader in = null;
			
			if (filepath.endsWith(".gz")) {
				
				in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(filepath))));	
				
				//in = new BufferedReader(new InputStreamReader(new GZIPInputStream(ClassLoader.getSystemResourceAsStream(filepath))));	
				
			}
			else {
				//in = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(filepath)));
				in = new BufferedReader(new InputStreamReader(new FileInputStream(filepath)));	
			}
			
			String lineOfFile;	
					
			int counter = 0;
															
			while((lineOfFile = in.readLine()) != null){  
			
				if(counter%1000 ==0) {
					System.out.println(counter + " lines parsed");
				}
				//lineOfFile = in.readLine();
				String[] result = lineOfFile.split("\t");								

				try{
					parseLine(result);//, maxGenesInCategory);
					counter++;
				}
				catch(giraphException ex){
					new CrashReporter(ex);
				}
			}
			System.out.println("GMTParser: File loaded and parsed, " + counter + " lines were parsed.");
			//System.out.println("Number of background genes = " + giraphApplication.getInstance().getFunctionalSetInfoCollection().noOfGenes());
			// let the optionFrame know that the file has been parsed so the rest of the options can be parsed
			notifyGMTFileParsed();
			
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	//	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
		//	e.printStackTrace();
		//}		
	}	
	
	/**
	 * Function to just parse the genes, this can be used to get all the background genes.
	 **/
	public void parseGenes(String [] result) {
		
		String[] nameInfo = result[0].split("%"); 
		
		if(nameInfo.length < 3){
			System.err.println("name info was not in the expected format: " + result[0]);
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
	
	public void notifyGMTFileParsed() {
		ol.gmtFileParsed();
	}
	
	// parses the line of a file
	//private void parseLine(String [] result, int maxGenesInCategory) throws giraphException {
	protected void parseLine(String [] result) throws giraphException {	
		
		// We don't want the categories that have a ridiculous number of genes 
		// TODO: we should probably have a menu option to set this
		if ((result.length < maxGenesInCategory) && (result.length > minGenesInCategory)) {
		
			/** Each line should be different so a new FunctionalSetInfo needs to be created. */
			FunctionalSetInfo functionalSetInfo = null;// = new FunctionalSetInfo();
			
			ArrayList<Gene> geneArrayList = new ArrayList<Gene>();
			
			try {
				// This is specific for the gmt files. http://baderlab.org/GeneSets
				String[] nameInfo = result[0].split("%"); 
				
				if(nameInfo.length < 3){
					System.err.println("name info was not in the expected format: " + result[0]);
				}
				
				else{
				
					int noOfBackgroundGenesInCategory = 0;
						
					/** cycle through all the genes, this is going to assume that there are no duplicated genes within the gene set category */ 
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
						geneListCollection.addGeneList(gl);
						
						//geneListArrayList.add(gl);
					}	
				}	
			}
			catch (java.lang.NumberFormatException e){
				String error = e.toString();
				throw new giraphException(error);
			}
		}
		//return noCategories;
	}  
		
	private String cleanGene(String str) {
		
		String str1 = str.replaceAll("\t", "");
		String str2 = str1.replaceAll("\\s+", "");
		String str3 = str2.replaceAll("\"", "");
		
		return str3;
	}
	
/*	public GeneList [] getGeneLists(){
		
		return geneListArrayList.toArray(new GeneList[0]);
	}
*/	
	public GeneCollection getAllGMTgenes(){
		return allGMTgenes;
	}
	
	public GeneListCollection getGeneListCollection(){
		return geneListCollection;
	}
	
	/** Add the optionsFrame as a listener */
	public void addOptionsListener(OptionsListener ol){
	
		this.ol = ol;
	}
	
/*	private void createGene(String geneSym, FunctionalSetInfo gsi){
		
		giraphApp.getFunctionalSetInfoCollection().addGeneInfo(geneSym, gsi);
	}
	
	public void setBackgroundGenes(String [] genes){
		
		backgroundGenes = new HashSet<String>();

		for(int i=0; i<genes.length; i++){
			String cleanGene = genes[i].toUpperCase();
			cleanGene.replaceAll("\\s","");
			backgroundGenes.add(cleanGene);
			
		}
		usingCustomBackgroundGenes = true;
	}
*/		
}
