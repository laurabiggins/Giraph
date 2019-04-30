package uk.ac.babraham.giraph.DataParser;

/** This is used to notify the app of updates  
 *  
 *  @author bigginsl
 *
 */


public interface ProgressListener {
			
	public void progressCancelled();
	
	public void progressUpdated(String s, int x1, int x2);
	
	public void progressWarningReceived (Exception e);

	public void progressExceptionReceived (Exception e);

	public void progressComplete(String process, Object result);
	
}


