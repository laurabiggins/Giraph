package uk.ac.babraham.giraph.Analysis;

public class AnalyseQueryGenes {
	
	/**
	 * Superseded by the gene name parser
	 * 
	 *   
	 * @param geneNames
	 * @param functionalSetInfoCollection
	 * @return
	 */
	
/*	public OptionsListener ol;

	public void lookUpQueryGenes(String [] geneNames, boolean customBackgroundGenes, int noOfBackgroundGenes){
		
		int totalNoBackgroundGenes;
		FunctionalSetInfoCollection functionalSetInfoCollection = giraphApplication.getInstance().functionalSetInfoCollection;
		String [] queryGenes = geneNames;
		
		// I think that this is already checked.
		if(queryGenes.length <= 10){
			
			String msg =  ("Too few genes entered, please enter at least 10 genes");
			JOptionPane.showMessageDialog(giraphApplication.getInstance(), msg, "Too few genes", JOptionPane.ERROR_MESSAGE);
			//return null;
		}
		
		GeneListCollection geneListCollection = new GeneListCollection();
		
		int genesNotMatched = 0;
		//int duplicatedGenes = 0;
		
		for (int i = 0; i < queryGenes.length; i++){
			
			String queryGene = stripExcess(queryGenes[i]).toUpperCase();
			
			/** This is just checking whether the gene exists in the parsed gmt info
			if(functionalSetInfoCollection.geneAlreadyExists(queryGene)){
				
				//System.out.println("AnalyseQueryGenes: geneInfo found: " + queryGene);
				// create new Gene object
				Gene newGene = new Gene(queryGene);
				// get the functionalSetInfo for the matched gene - there's a hash of gene names and functionalSetInfo objects
				FunctionalSetInfo[] gsi = functionalSetInfoCollection.getFunctionalSetInfo(queryGene);
				
				// There may be only 1 term associated with the gene or there may be many, we have to cycle through and check
				for(int j = 0; j < gsi.length; j++){
					
					geneListCollection.addGeneList(gsi[j].getFunctionalSetName(), newGene, gsi[j].getNoOfGenesInCategory());
					
				}
			}
			else{
				System.err.println("AnalyseQueryGenes: gene not found: " + queryGene);
				genesNotMatched++;
			}
		}
		
		if(customBackgroundGenes == true){
			totalNoBackgroundGenes = noOfBackgroundGenes;
		}
		else {
			totalNoBackgroundGenes = functionalSetInfoCollection.noOfGenes();
		}
		
		PValue [] allPValues = new PValue[geneListCollection.getGeneLists().length];
		
		// set the p values for all the gene lists		
		for  (int i = 0; i < geneListCollection.getGeneLists().length; i++){
			
			allPValues[i] = calculatePValueUsingFishersTest(geneListCollection.getGeneLists()[i], totalNoBackgroundGenes, queryGenes.length);
			
			geneListCollection.getGeneLists()[i].setPvalue(allPValues[i]);
			
			if (i < 10){
				System.err.println("for " + geneListCollection.getGeneLists()[i].getTerm() + ", no of genes in gene list = " + 
			geneListCollection.getGeneLists()[i].getGenes().length + ", totalNoBackgroundGenes = " + totalNoBackgroundGenes + ", no of query genes = " + queryGenes.length +
			", no of genes in category = " + geneListCollection.getGeneLists()[i].getTotalNoOfGenesInCategory() + ", uncorrected p value = " + allPValues[i].p());
			}	
			if(geneListCollection.getGeneLists()[i].getTerm().startsWith("LEPTIN")){
				System.out.println("========================================");
				System.err.println("for " + geneListCollection.getGeneLists()[i].getTerm() + ", no of genes in gene list = " + 
						geneListCollection.getGeneLists()[i].getGenes().length + ", totalNoBackgroundGenes = " + totalNoBackgroundGenes + ", no of query genes = " + queryGenes.length +
						", no of genes in category = " + geneListCollection.getGeneLists()[i].getTotalNoOfGenesInCategory() + ", uncorrected p value = " + allPValues[i].p());
				System.out.println("========================================");
			}
			
			
		}
		
		// The multiple testing correction can only be performed once all the p values have been calculated.
		MultipleTestingCorrection.benHochFDR(allPValues);
		
		System.out.println("from AnalyseQueryGenes, total no of genelists: " + geneListCollection.getGeneLists().length);
		System.out.println("from AnalyseQueryGenes, number of unmatched genes = " + genesNotMatched);
		
		// tell the options frame that the genes have been parsed
		ol.queryGenesAnalysed(geneListCollection.getGeneLists());

	}
	
	/** 
	 *  If a background genelist is entered, it will need to be checked to see how many of the genes match, then that number can be passed to this method and used
	 *  as the totalNoGenesInCollection   
	 
	private static PValue calculatePValueUsingFishersTest(GeneList gl, int totalNoGenesInCollection , int noOfQueryGenes){
		
		/** The GeneList contains the number of genes in it and the number in the category in the gmt file. 
		/** Get the right format for the input into Fishers Test 
		
		/** a is no of query genes in category 
		int a = gl.getGenes().length;
		
		/** b is total no of genes in the genome (gmt file) in category 
		int b = gl.getTotalNoOfGenesInCategory();
		
		/** c is no of query genes not in category 
		int c = noOfQueryGenes - a;
		
		/** d is no of genes in the genome (gmt file) that are not in category 
		int d = totalNoGenesInCollection - b;		
		
		// To do the modified Fishers test that they use in David (EASE), we subtract 1 from a
		if( a > 0){
			a = a-1;
		}
		
		double[] fishersExactResult = new FishersExactTest().fishersExactTest(a, b, c, d);
		
		/** select the right tail 
		PValue pval = new PValue(fishersExactResult[2]);
		
		return(pval);
						
	}
	
	
	public void addOptionsListener(OptionsListener ol){
		
		this.ol = ol;
	}
	
	public static String stripExcess(String str) {
		
		while ((str.startsWith("\"")) || (str.endsWith("\"")) || (str.startsWith("\t")) || (str.endsWith("\t")) || (str.startsWith(" ")) || (str.endsWith(" "))) {
			
			if (str.startsWith("\"")){
				str = str.substring(1, str.length());
			}
			if (str.endsWith("\"")){
				str = str.substring(0, str.length() - 1);
			}
			if (str.startsWith("\t")){
				str = str.substring(1, str.length());
			}
			if (str.endsWith("\t")){
				str = str.substring(0, str.length() - 1);
			}
			if (str.startsWith(" ")){
				str = str.substring(1, str.length());
			}
			if (str.endsWith(" ")){
				str = str.substring(0, str.length() - 1);
			}					
		}
		return str;
	}
*/		
}
