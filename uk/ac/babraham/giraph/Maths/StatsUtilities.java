package uk.ac.babraham.giraph.Maths;

/** 
 * This gets data in the right format to perform a Fishers Exact Test or a Chi-Square Test. I'm not using the Chi-Square test at the moment,
 * but I'm leaving it in as it will probably be useful in the future, even if it's for a different project.
 */

import uk.ac.babraham.giraph.DataTypes.GeneList;

public class StatsUtilities {	

	/**
	 * This method takes information about query genes and background genes and returns contingency table values that can 
	 * be used as input for Fishers Exact Test or processed further using the getContingencyTableValuesForChiSquare() method
	 * to get the values in the right format for the Chi-Square test.
	 * 
	 * @param totalNoGenesInCollection  -  total no of genes in gmt file
	 * @param noOfQueryGenes  -  no of query genes that the user entered
	 * @param gl  -  the genelist/category object
	 * @return
	 * 
	 * It returns 4 numbers that are basically a contingency table.
	 * a - no of query genes in genelist/category
	 * b - total no of genes in the genome (gmt file) in category 
	 * c - no of query genes not in category
	 * d - no of genes in the genome (gmt file) that are not in category
	 * 
	 * The Fishers exact test requires input in this format 
	 */
	
	
	/* This shouldn't be in here - it's really not necessary 
	 * 
	 */
	
/*	public static int[] getContingencyTableValuesAsArray(int totalNoGenesInCollection, int noOfQueryGenes, GeneList gl){
		
		// a is no of query genes in category 
		int a = observed(gl);
		
		// b is total no of genes in the genome (gmt file) in category 
		int b = gl.getTotalNoOfGenesInCategory();
		
		// c is no of query genes not in category 
		int c = noOfQueryGenes - a;
		
		// d is no of genes in the genome (gmt file) that are not in category 
		int d = totalNoGenesInCollection - b;
		
		int [] contingencyTable = new int [4];
		contingencyTable [0] = a;
		contingencyTable [1] = b;
		contingencyTable [2] = c;
		contingencyTable [3] = d;
		
		return contingencyTable;		
	}
*/	
	
	/** simple crude correction - not used any more */
	
/*	public static double bonferroniCorrection(double pValue, int noOfTests){
		
		return (pValue * noOfTests);
	}
	
//	public static double [] benHochFDR(double [] pValue, int noOfTests){
		
		// we need our pvalues to be returned in the same order.
		/**
		 * we need to rank the p-values of the gene lists, the largest gets rank n, the others get n-1 etc, down to n,
		 * then each p-value is multiplied by n and divided by its rank to give the adjusted p value.
		 */
		
//	}
	
	/** The Chi-Square Test from apache commons that is implemented in this project requires input in this format i.e. long[][] 
	 * I'm not actually using it as a test for Giraph at the moment but it might be useful at some point.
	 */
	
/*	public static long[][] getContingencyTableValuesForChiSquare(int totalNoGenesInCollection, int noOfQueryGenes, GeneList gl){
		
		int [] contingencyTableArray = getContingencyTableValuesAsArray(totalNoGenesInCollection, noOfQueryGenes, gl);
		
		long [][] contingencyTable = new long [2][2];
		contingencyTable [0][0] = contingencyTableArray[0];
		contingencyTable [0][1] = contingencyTableArray[1];
		contingencyTable [1][0] = contingencyTableArray[2];
		contingencyTable [1][1] = contingencyTableArray[3];
		
		return contingencyTable;		
	}
*/	
	/** 
	 * Returns the observed number of genes in a category/genelist. 
	 */
/*	private static int observed(GeneList gl){
		
		return gl.getNoOfGenesInList();
	}
*/	

	/** 
	 * expected = (no of query genes/total no of genes in gmt file) * no of genes in GeneSetInfo (i.e. the category).
	 * 
	 *  This methods isn't currently being used but may be useful at some point.
	 * 
	 */
	
/*	private static float expectedValue(int totalNoGenes, int noOfQueryGenes, int noInGeneSetInfo){
		
		float expected = ((float)noOfQueryGenes / totalNoGenes ) * noInGeneSetInfo;
		
		return expected;
		
	}

*/
	
	
	

}
