package de.rhocas.nce.msv.domain.port;

/**
 * A port to write on the console.
 */
public interface Console {

	/**
	 * Prints the given message on the console.
	 *
	 * @param msg The message to print.
	 */
	void printMessage( String message );

	/**
	 * Prints the given error on the console.
	 *
	 * @param error The error to print.
	 */
	void printError( String error );

}
