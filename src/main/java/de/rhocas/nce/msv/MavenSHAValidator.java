package de.rhocas.nce.msv;

import picocli.CommandLine;

/**
 * The main entry point of the application.
 */
public final class MavenSHAValidator {

	public static void main( final String[] args ) {
		final Configuration configuration = new Configuration( );
		final CommandLine commandLine = new CommandLine( configuration.getCommand( ) );
		final int exitCode = commandLine.execute( args );
		if ( exitCode != 0 ) {
			System.exit( exitCode );
		}
	}

}
