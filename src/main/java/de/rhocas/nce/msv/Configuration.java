package de.rhocas.nce.msv;

import de.rhocas.nce.msv.adapter.console.SysOutConsole;
import de.rhocas.nce.msv.adapter.filesystem.RealFileSystem;
import de.rhocas.nce.msv.adapter.sha1.DefaultSHA1Calculator;
import de.rhocas.nce.msv.adapter.sha1.DefaultSHA1Extractor;
import de.rhocas.nce.msv.domain.port.Console;
import de.rhocas.nce.msv.domain.port.FileSystem;
import de.rhocas.nce.msv.domain.port.SHA1Calculator;
import de.rhocas.nce.msv.domain.port.SHA1Extractor;
import de.rhocas.nce.msv.usecase.ListInvalidFiles;
import de.rhocas.nce.msv.usecase.RemoveInvalidFiles;

/**
 * The default configuration for the whole application. It assembles all
 * dependencies.
 */
public final class Configuration {

	private final FileSystem fileSystem = new RealFileSystem( );
	private final Console console = new SysOutConsole( );

	private final SHA1Calculator sha1Calculator = new DefaultSHA1Calculator( fileSystem );
	private final SHA1Extractor sha1Extractor = new DefaultSHA1Extractor( fileSystem );

	private final ListInvalidFiles listInvalidFiles = new ListInvalidFiles( fileSystem, sha1Calculator, sha1Extractor );
	private final RemoveInvalidFiles removeInvalidFiles = new RemoveInvalidFiles( fileSystem );

	public MavenSHAValidatorCommand getCommand( ) {
		return new MavenSHAValidatorCommand( console, listInvalidFiles, removeInvalidFiles );
	}

}
