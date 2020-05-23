package de.rhocas.nce.msv;

import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

import de.rhocas.nce.msv.domain.entity.File;
import de.rhocas.nce.msv.domain.error.FilesCannotBeRemoved;
import de.rhocas.nce.msv.domain.error.RootDirectoryCannotBeAccessed;
import de.rhocas.nce.msv.domain.port.Console;
import de.rhocas.nce.msv.usecase.ListInvalidFiles;
import de.rhocas.nce.msv.usecase.RemoveInvalidFiles;
import io.vavr.control.Either;
import io.vavr.control.Option;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * This is the actual command executed by the application.
 */
@Command( name = "MavenSHAValidator", description = "Validates the given Maven repository." )
public final class MavenSHAValidatorCommand implements Callable<Integer> {

	@Parameters( index = "0", description = "The path to the repository that should be validated." )
	private Path repositoryPath;

	@picocli.CommandLine.Option( names = "-r", description = "Removes the invalid files" )
	private boolean remove;

	@picocli.CommandLine.Option( names = "-i", description = "Ignores files starting with those paths (relative to the repository; multiple usages possible)" )
	private final List<String> ignorePathPrefixes = Collections.emptyList( );

	private final Console console;
	private final ListInvalidFiles listInvalidFiles;
	private final RemoveInvalidFiles removeInvalidFiles;

	MavenSHAValidatorCommand( final Console console, final ListInvalidFiles listInvalidFiles, final RemoveInvalidFiles removeInvalidFiles ) {
		this.console = console;
		this.listInvalidFiles = listInvalidFiles;
		this.removeInvalidFiles = removeInvalidFiles;
	}

	@Override
	public Integer call( ) throws Exception {
		final Either<RootDirectoryCannotBeAccessed, List<File>> eitherErrorOrInvalidFiles = listInvalidFiles.listInvalidFiles( repositoryPath, ignorePathPrefixes );

		if ( eitherErrorOrInvalidFiles.isLeft( ) ) {
			console.print( MessageFormat.format( "The given directory ''{0}'' cannot be accessed.", repositoryPath ) );
			return -1;
		}

		final List<File> invalidFiles = eitherErrorOrInvalidFiles.get( );
		invalidFiles.stream( )
				.map( invalidFile -> MessageFormat.format( "Invalid file detected: {0}", invalidFile ) )
				.forEach( msg -> console.print( msg ) );

		if ( remove ) {
			final Option<FilesCannotBeRemoved> optionalError = removeInvalidFiles.remove( invalidFiles );
			if ( !optionalError.isEmpty( ) ) {
				console.print( MessageFormat.format( "Following files could not be removed:", optionalError.get( ).getUnremovableFiles( ) ) );
				return -2;
			}
		}

		return 0;
	}

}
