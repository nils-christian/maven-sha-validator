package de.rhocas.nce.msv.adapter.filesystem;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import de.rhocas.nce.msv.domain.entity.File;
import de.rhocas.nce.msv.domain.error.FilesCannotBeRemoved;
import de.rhocas.nce.msv.domain.error.RootDirectoryCannotBeAccessed;
import de.rhocas.nce.msv.domain.port.FileSystem;
import io.vavr.control.Either;
import io.vavr.control.Option;

/**
 * This adapter is an implementation of {@link FileSystem} that uses the real
 * file system.
 */
public final class RealFileSystem implements FileSystem {

	@Override
	public Either<RootDirectoryCannotBeAccessed, List<File>> listFilesInDirectory( final Path directory ) {
		try ( Stream<Path> stream = Files.walk( directory ) ) {
			return Either.right( stream
					.filter( path -> Files.isRegularFile( path ) )
					.map( path -> pathToFile( path, directory ) )
					.collect( Collectors.toList( ) ) );
		} catch ( final IOException ex ) {
			return Either.left( new RootDirectoryCannotBeAccessed( ) );
		}
	}

	private File pathToFile( final Path path, final Path directory ) {
		return new File( path, findExtension( path ), directory );
	}

	@Override
	public Option<FilesCannotBeRemoved> removeFiles( final List<File> files ) {
		final List<File> unremovableFiles = new ArrayList<>( );

		for ( final File file : files ) {
			try {
				Files.delete( file.getPath( ) );
			} catch ( final IOException e ) {
				unremovableFiles.add( file );
			}
		}

		return Option.when( !unremovableFiles.isEmpty( ), ( ) -> new FilesCannotBeRemoved( unremovableFiles ) );
	}

	private String findExtension( final Path path ) {
		final String fileName = path.getFileName( ).toString( );
		final int lastIndex = fileName.lastIndexOf( '.' );
		if ( lastIndex == -1 ) {
			return null;
		}
		return fileName.substring( lastIndex + 1 );
	}

	@Override
	public Option<byte[]> readBytes( final File file ) {
		try {
			final byte[] allBytes = Files.readAllBytes( file.getPath( ) );
			return Option.of( allBytes );
		} catch ( final IOException e ) {
			return Option.none( );
		}
	}

	@Override
	public Option<File> getSiblingFile( final File file, final String siblingFileName ) {
		return Option.of( file.getPath( ).resolveSibling( siblingFileName ) )
				.filter( path -> Files.isRegularFile( path ) )
				.map( path -> pathToFile( path, file.getRootDirectory( ) ) );
	}

	@Override
	public Option<String> readString( final File file ) {
		try {
			final String string = Files.readString( file.getPath( ) );
			return Option.of( string );
		} catch ( final IOException e ) {
			return Option.none( );
		}
	}

}
