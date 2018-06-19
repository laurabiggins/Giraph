package uk.ac.babraham.giraph.Maths;

import java.util.Arrays;

import uk.ac.babraham.giraph.DataTypes.PValue;

public class MultipleTestingCorrection {
	
	
	public static double bonferroniCorrection(double pValue, int noOfTests){
		
		return (pValue * noOfTests);
	}
	
	
	/**
	 * we need to rank the p-values of the gene lists, the largest gets rank n, the others get n-1 etc, down to n,
	 * then each p-value is multiplied by n and divided by its rank to give the adjusted p value.
	 * 
	 * The q values are added in situ.
	 */
	public static void benHochFDR(PValue [] pValues){
			
		Arrays.sort(pValues);
		
		for (int i=0; i < pValues.length; i++) {
			
			double qVal = pValues[i].p() * ((double)(pValues.length)/(i+1));
			
			pValues[i].setQ(qVal);
			
			/** I guess this is because if two p values are very similar (or the same), the one that is ranked lower (with a lower p value)
			 *  will be corrected more harshly and end up with a higher q value.
			 */
			if (i > 0 && pValues[i].q() < pValues[i-1].q()) {
				
				qVal = pValues[i-1].q();
				pValues[i].setQ(qVal);
			}
			
//				System.out.println("P-value "+tTestValues[i].p+" at index "+i+" with total length "+tTestValues.length+" converted to "+tTestValues[i].q);
		}
		
	}
				
}
	


