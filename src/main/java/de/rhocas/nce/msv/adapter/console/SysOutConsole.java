package de.rhocas.nce.msv.adapter.console;

import de.rhocas.nce.msv.domain.port.Console;

/**
 * This adapter is an implementation of {@link Console} that uses the real
 * SysOut console.
 */
public final class SysOutConsole implements Console {

	@Override
	public void print( final String msg ) {
		System.out.println( msg );
	}

}
