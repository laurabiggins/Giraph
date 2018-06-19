package uk.ac.babraham.giraph.Dialogs;


/**
 * An interface to indicate that a class performs a long running task which
 * can be cancelled.
 */
public interface Cancellable {
	
	public void cancel ();
	
}
