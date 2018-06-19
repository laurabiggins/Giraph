package uk.ac.babraham.giraph.DataParser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;

import uk.ac.babraham.giraph.CrashReporter;
import uk.ac.babraham.giraph.giraphApplication;
import uk.ac.babraham.giraph.giraphException;
import uk.ac.babraham.giraph.DataTypes.Gene;
import uk.ac.babraham.giraph.DataTypes.GeneCollection;

/** 
 * for parsing tab-delimited file of gene lengths, GC content etc.
 * format must be gene_name, GC content, gene length, chr 
 * 
 * creates a GeneCollection object containing all the genes.
 * 
 * could have average expression
 * 
 * TODO: should implement the seqmonk text parser 
 * 
 * @author bigginsl
 *
 */

public class GeneInfoParser implements Runnable{
	
	// The file containing the info for all the genes
	private String filepath;
	
	// The options listener
	private OptionsListener ol;
	
	// The genes
	private GeneCollection genes;
	
	public GeneInfoParser(String filepath, GeneCollection genes){
		this.filepath = filepath;
		this.genes = genes;
		
		Thread t = new Thread(this);
		t.start();
	}

	public void run(){
		
		if(filepath == null){
			System.err.println("No file found to parse");
		}
		else{
			//JOptionPane.showMessageDialog(giraphApplication.getInstance(), "Now we'd like to load and parse the gene info file" + filepath, "", JOptionPane.INFORMATION_MESSAGE);
			System.out.println("Now we'd like to load and parse the gene info file " + filepath);
			try {
				//giraphApplication.getInstance().geneSetInfoCollection = new GeneSetInfoCollection();
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

	public GeneCollection getGeneCollection(){
		return genes;
	}
	
	private void importFile (String filepath) throws giraphException, IOException{
		
		//JOptionPane.showMessageDialog(giraphApplication.getInstance(), "trying to import gene info file " + filepath, "", JOptionPane.INFORMATION_MESSAGE);
		
		BufferedReader in = null;
		
		if (filepath.endsWith(".gz")) {
			//in = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FileInputStream(probeFiles[f]))));	
			
			in = new BufferedReader(new InputStreamReader(new GZIPInputStream(ClassLoader.getSystemResourceAsStream(filepath))));	
			//in = null;
			
		}
		else {
			in = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(filepath)));
		}
		
		
		//ZipInputStream zis = new ZipInputStream(ClassLoader.getSystemResourceAsStream(filepath)); 
		
		//ZipEntry ze= null;
		
		
		/*while((ze = zis.getNextEntry()) != null){
			
			in = new BufferedReader(new InputStreamReader(zis));
			
		}
		*/
		//BufferedReader in = new BufferedReader(new InputStreamReader(zis));
		
		//FileReader reader;
	//	int noOfCategories = 0;
		
	//	try {
			//reader = new FileReader(filepath);
			//BufferedReader in = new BufferedReader(reader);
		
		    //BufferedReader in = new BufferedReader(new InputStreamReader(ClassLoader.getSystemResourceAsStream(filepath)));
			
			String lineOfFile;						
					
			try {
				int counter = 0;
				
				String headerLine = in.readLine();
				String[] header = headerLine.split("\t");	
				// check that it's in the expected format
				if(header.length < 10){
					System.err.println("GeneInfoParser isn't happy - too few header fields.");
					JOptionPane.showMessageDialog(giraphApplication.getInstance(),"header line of gene info file wasn't as expected", "unrecognised fields", JOptionPane.ERROR_MESSAGE);
					return;
				}
				if (header[0].contains("gene_id") && header[1].contains("gene_name") && header[2].contains("chromosome") && header[8].contains("length") && header[9].contains("GC_content") && header[10].contains("no_of_transcripts")){
					System.err.println("header is as expected");
				}
				else{
					System.err.println("GeneInfoParser isn't happy - header fields weren't quite right.");
					JOptionPane.showMessageDialog(giraphApplication.getInstance(),"header line of gene info file wasn't as expected", "unrecognised fields", JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				while((lineOfFile = in.readLine()) != null){  
				
					if(counter%1000 ==0) {
						System.out.println(counter + " lines parsed");
					}
					//lineOfFile = in.readLine();
					String[] result = lineOfFile.split("\t");								
	
					try{
						parseLine(result);
						counter++;
					}
					catch(giraphException ex){
						new CrashReporter(ex);
					}
				}
				
				//JOptionPane.showMessageDialog(giraphApplication.getInstance(),"gene info file loaded", "", JOptionPane.INFORMATION_MESSAGE);
				System.out.println("Gene Info Parser: File loaded and parsed, " + counter + " lines were parsed.");
				System.out.println(genes.getAllGenes().length + " genomic background genes were created.");
				// let the optionFrame know that the file has been parsed so the rest of the options can be parsed
				ol.geneInfoFileParsed();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	//	} 
	//	catch (Exception e) {
			// TODO Auto-generated catch block
	//		e.printStackTrace();
	//	}		
	}	
	
		
	private void parseLine(String [] result) throws giraphException {
		
		String geneSym = new String(result[1].toUpperCase());
		String geneSym1 = geneSym.replaceAll("\\s+", "");
		//System.err.println("geneSymbol=" + geneSym1);
				
		float gcContent;
		if(result[9].isEmpty()){
			gcContent = 0;
		}
		else{
			gcContent = Float.parseFloat(result[9]);
		}	
		int length = Integer.parseInt(result[8]);
		int noOfTranscripts = Integer.parseInt(result[10]);
		String biotypeFamily = result[7]; 
		String biotype = result[6]; 
		
		String chr = result[2];
		
		if(gcContent > 1 || gcContent < 0){
			System.err.println("invalid GC value "+ geneSym);
		}
		if(length < 10){
			System.err.println("length < 10 for "+ geneSym);
		}
		
		Gene g = new Gene(geneSym1, gcContent, length, noOfTranscripts, chr);
		
		if(biotypeFamily.length() > 0){
			g.setBiotypeFamily(biotypeFamily);
		}
		
		if(biotype.length() > 0){
			g.setBiotype(biotype);
		}
		
		genes.addGene(g);
	}	
	
	public void addOptionsListener(OptionsListener ol){
		
		this.ol = ol;
	}

}
