package de.rhocas.nce.msv.adapter.console;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.rhocas.nce.msv.domain.port.Console;

@DisplayName( "Unit-Test for the SysOutConsole" )
final class SysOutConsoleTest {

	@Test
	@DisplayName( "Print should work" )
	void printShouldWork( ) {
		final Console console = new SysOutConsole( );
		console.print( "Test" );
	}

}
