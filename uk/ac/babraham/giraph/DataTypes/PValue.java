package uk.ac.babraham.giraph.DataTypes;

/** Used to store the results of the fishers exact test. 
 * 
 * @author bigginsl
 *
 */


public class PValue implements Comparable<PValue> {
	
	private double p;
	private double q;

	public PValue(double pvalue){
		this.p = pvalue;
	}
	public PValue(){
		
	}
	
	public void setP(double pvalue){
		this.p = pvalue;
	}
	
	public void setQ(double qvalue){
		this.q = qvalue;
	}
	
	public double p(){
		return p;
	}
	
	public double q(){
		return q;
	}	
	
	public int compareTo(PValue o) {
		return Double.compare(p,o.p);
	}
	
}
