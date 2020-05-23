package de.rhocas.nce.msv.adapter.console;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.rhocas.nce.msv.domain.port.Console;

@DisplayName( "Unit-Test for the SysOutConsole" )
final class SysOutConsoleTest {

	@Test
	@DisplayName( "PrintMessage should work" )
	void printMessageShouldWork( ) {
		final Console console = new SysOutConsole( );
		console.printMessage( "Test" );
	}

	@Test
	@DisplayName( "PrintError should work" )
	void printErrorShouldWork( ) {
		final Console console = new SysOutConsole( );
		console.printError( "Test" );
	}
}
