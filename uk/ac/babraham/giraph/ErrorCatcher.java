package uk.ac.babraham.giraph;

import uk.ac.babraham.giraph.CrashReporter;

/**
 * This is from SeqMonk
 * The error catcher can be attached to the main JVM and is triggered
 * any time a throwable exception makes it back all the way through the
 * stack without being caught so we don't miss any errors.
 */
public class ErrorCatcher implements Thread.UncaughtExceptionHandler {

	/* (non-Javadoc)
	 * @see java.lang.Thread.UncaughtExceptionHandler#uncaughtException(java.lang.Thread, java.lang.Throwable)
	 */
	public void uncaughtException(Thread thread, Throwable t) {
		new CrashReporter(t);		
	}
	
}
