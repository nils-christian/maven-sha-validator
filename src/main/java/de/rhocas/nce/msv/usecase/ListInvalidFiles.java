package de.rhocas.nce.msv.usecase;

import static io.vavr.API.For;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.rhocas.nce.msv.domain.entity.File;
import de.rhocas.nce.msv.domain.error.RootDirectoryCannotBeAccessed;
import de.rhocas.nce.msv.domain.port.FileSystem;
import de.rhocas.nce.msv.domain.port.SHA1Calculator;
import de.rhocas.nce.msv.domain.port.SHA1Extractor;
import io.vavr.control.Either;
import io.vavr.control.Option;

/**
 * This usecase lists all invalid files from the file system.
 */
public final class ListInvalidFiles {

	private final FileSystem fileSystem;
	private final SHA1Calculator sha1Calculator;
	private final SHA1Extractor sha1Extractor;

	public ListInvalidFiles( final FileSystem fileSystem, final SHA1Calculator sha1Calculator, final SHA1Extractor sha1Extractor ) {
		this.fileSystem = fileSystem;
		this.sha1Calculator = sha1Calculator;
		this.sha1Extractor = sha1Extractor;
	}

	/**
	 * Lists all invalid files starting with the given directory.
	 *
	 * @param directory The directory to start with.
	 *
	 * @return Either an error, if the root directory cannot be accessed, or a list
	 *         with the invalid files.
	 */
	public Either<RootDirectoryCannotBeAccessed, List<File>> listInvalidFiles( final Path directory ) {
		final Either<RootDirectoryCannotBeAccessed, List<File>> eitherErrorOrFiles = fileSystem.listFilesInDirectory( directory );
		final Either<RootDirectoryCannotBeAccessed, List<File>> eitherErrorOrInvalidFiles = eitherErrorOrFiles
				.map( files -> files.parallelStream( )
						.filter( path -> isRelevantForValidation( path ) )
						.filter( path -> isInvalid( path ) )
						.collect( Collectors.toList( ) ) );

		return eitherErrorOrInvalidFiles;
	}

	private boolean isRelevantForValidation( final File file ) {
		return hasAnyExtension( file, "pom", "jar" );
	}

	private boolean hasAnyExtension( final File file, final String... extensions ) {
		return Stream.of( extensions ).anyMatch( extension -> file.getExtension( ).exists( pred -> pred.equalsIgnoreCase( extension ) ) );
	}

	private boolean isInvalid( final File path ) {
		final Option<String> optionalExpectedSha1 = sha1Extractor.extract( path );
		final Option<String> optionalActualSha1 = sha1Calculator.calculate( path );

		return For( optionalExpectedSha1, optionalActualSha1 )
				.yield( ( expectedSha1, actualSha1 ) -> !expectedSha1.equalsIgnoreCase( actualSha1 ) )
				.getOrElse( true );
	}

}
