package uk.ac.babraham.giraph;

public class giraphException extends Exception {

	/**
	 * Instantiates a new giraph exception.
	 * 
	 * @param error Error text
	 */
	public giraphException (String error) {
		super(error);
		System.err.println("supposed to be producing an exception here!!!!!" + error);
		
	}

}
