package de.rhocas.nce.msv.adapter.console;

import de.rhocas.nce.msv.domain.port.Console;

/**
 * This adapter is an implementation of {@link Console} that uses the real
 * SysOut console.
 */
public final class SysOutConsole implements Console {

	@Override
	public void printMessage( final String message ) {
		System.out.println( message );
	}

	@Override
	public void printError( final String error ) {
		System.err.println( error );
	}

}
