package uk.ac.babraham.giraph;

public class giraphException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3582168982465400337L;

	/**
	 * Instantiates a new giraph exception.
	 * 
	 * @param error Error text
	 */
	public giraphException (String error) {
		super(error);
	}
}
