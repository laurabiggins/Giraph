package uk.ac.babraham.giraph.DataParser;

import java.io.File;

import javax.swing.JOptionPane;

import uk.ac.babraham.giraph.giraphApplication;

public class GProfilerParser extends ExternalResultsParser {
		
		
	public GProfilerParser(File file){
		
		super(file);
	}
	
	public void setColumnInfoForFile(){
		
		System.err.println("setting fields for g profiler file");
		
		delimitersValue = ",";
		geneDelimitersValue = ",";
		
		startRowValue = 1;
		queryGeneColValue = 9;
		categoryNameColValue = 1;
		categoryDescriptionColValue = 2;
		pValueColValue = 3;		
	}
	
	public void checkHeader(String line){
	// The message should be more informative about which column is wrong
		System.err.println("header line = " + line);
		String [] headerSections = line.split(delimitersValue,-1);
		
		if(!(headerSections[1].contains("term") && headerSections[9].contains("intersections") && headerSections[3].contains("adjusted_p_value"))){
			
			JOptionPane.showMessageDialog(giraphApplication.getInstance(), "Unexpected column name found in file, is this a results file from gProfiler?\n"
					+ " Giraph will try and parse the file but it may not produce the right output.", 
					"Unexpected column header", JOptionPane.WARNING_MESSAGE);	
			
		}
		// Expected format
		//"GO Term	Description	P-value	FDR q-value	Enrichment	N	B	n	b	Genes"
	}
	
	public String getGenesForParsing(String line, String [] sections, int queryGeneColValue){
		
		String regexSplit = ",\"";
		String[] genes = line.split(regexSplit,-1);
		
		return(cleanGene(genes[1]));
		
	}
	
}
