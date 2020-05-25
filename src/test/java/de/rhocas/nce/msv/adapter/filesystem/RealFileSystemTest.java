package de.rhocas.nce.msv.adapter.filesystem;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import de.rhocas.nce.msv.domain.entity.File;
import de.rhocas.nce.msv.domain.error.FilesCannotBeRemoved;
import de.rhocas.nce.msv.domain.error.RootDirectoryCannotBeAccessed;
import de.rhocas.nce.msv.domain.port.FileSystem;
import io.vavr.control.Either;
import io.vavr.control.Option;

@DisplayName( "Unit-Test for RealFileSystem" )
final class RealFileSystemTest {

	@TempDir
	Path tempDir;

	@Test
	@DisplayName( "listFilesInDirectory should return files" )
	void listFilesInDirectoryShouldReturnFiles( ) throws URISyntaxException {
		final FileSystem fileSystem = new RealFileSystem( );

		final URL fileUrl = getClass( ).getResource( "file.txt" );
		final Path filePath = Paths.get( fileUrl.toURI( ) );
		final Path rootPath = filePath.getParent( );
		final Either<RootDirectoryCannotBeAccessed, List<File>> result = fileSystem.listFilesInDirectory( rootPath );

		assertThat( result.isRight( ) ).isTrue( );
		final List<File> list = result.get( );
		assertThat( list ).filteredOn( f -> f.getExtension( ).exists( e -> e.equals( "txt" ) ) ).hasSize( 2 );
		assertThat( list ).filteredOn( f -> f.getExtension( ).isEmpty( ) ).hasSize( 1 );
	}

	@Test
	@DisplayName( "listFilesInDirectory should return error" )
	void listFilesInDirectoryShouldReturnError( ) {
		final FileSystem fileSystem = new RealFileSystem( );
		final Either<RootDirectoryCannotBeAccessed, List<File>> result = fileSystem.listFilesInDirectory( Path.of( "doesNotExist" ) );

		assertThat( result.isLeft( ) ).isTrue( );
		assertThat( result.getLeft( ) ).isNotNull( );
	}

	@Test
	@DisplayName( "removeFiles should return error" )
	void removeFilesShouldReturnError( ) {
		final FileSystem fileSystem = new RealFileSystem( );

		final File file = new File( Paths.get( "doesNotExist.txt" ), ".txt", Paths.get( "." ) );
		final Option<FilesCannotBeRemoved> result = fileSystem.removeFiles( Collections.singletonList( file ) );

		assertThat( result.isDefined( ) ).isTrue( );
		final FilesCannotBeRemoved error = result.get( );
		assertThat( error.getUnremovableFiles( ) ).containsExactly( file );
	}

	@Test
	@DisplayName( "removeFiles should return empty option" )
	void removeFilesShouldReturnEmptyOption( ) throws URISyntaxException, IOException {
		final FileSystem fileSystem = new RealFileSystem( );

		final URL fileUrl = getClass( ).getResource( "file.txt" );
		final Path filePath = Paths.get( fileUrl.toURI( ) );
		final Path tempFilePath = tempDir.resolve( "file.txt" );
		Files.copy( filePath, tempFilePath );

		final File file = new File( tempFilePath, ".txt", tempDir );
		final Option<FilesCannotBeRemoved> result = fileSystem.removeFiles( Collections.singletonList( file ) );

		assertThat( result.isEmpty( ) ).isTrue( );
		assertThat( Files.exists( tempFilePath ) ).isFalse( );
	}

	@Test
	@DisplayName( "readBytes on non-existing file should return empty option" )
	void readBytesOnNonExistingFileShouldReturnEmptyOption( ) {
		final FileSystem fileSystem = new RealFileSystem( );

		final File file = new File( Paths.get( "doesNotExist.txt" ), ".txt", Paths.get( "." ) );
		final Option<byte[]> result = fileSystem.readBytes( file );
		assertThat( result ).isEmpty( );
	}

	@Test
	@DisplayName( "readBytes on existing file should return content" )
	void readBytesOnExistingFileShouldReturnContent( ) throws URISyntaxException {
		final FileSystem fileSystem = new RealFileSystem( );

		final URL fileUrl = getClass( ).getResource( "file.txt" );
		final Path filePath = Paths.get( fileUrl.toURI( ) );
		final Option<byte[]> result = fileSystem.readBytes( new File( filePath, ".txt", filePath.getParent( ) ) );
		assertThat( result ).contains( "SomeContent".getBytes( StandardCharsets.UTF_8 ) );
	}

	@Test
	@DisplayName( "readString on non-existing file should return empty optional" )
	void readStringOnNonExistingFileShouldReturnEmptyOptional( ) {
		final FileSystem fileSystem = new RealFileSystem( );

		final Option<String> result = fileSystem.readString( new File( Paths.get( "doesNotExist.txt" ), ".txt", Paths.get( "." ) ) );
		assertThat( result ).isEmpty( );
	}

	@Test
	@DisplayName( "readString on existing file should return content" )
	void readStringOnExistingFileShouldReturnContent( ) throws URISyntaxException {
		final FileSystem fileSystem = new RealFileSystem( );

		final URL fileUrl = getClass( ).getResource( "file.txt" );
		final Path filePath = Paths.get( fileUrl.toURI( ) );
		final Option<String> result = fileSystem.readString( new File( filePath, ".txt", filePath.getParent( ) ) );
		assertThat( result ).contains( "SomeContent" );
	}

	@Test
	@DisplayName( "getSiblingFile should return existing file" )
	void getSiblingFileShouldReturnExistingFile( ) throws URISyntaxException {
		final FileSystem fileSystem = new RealFileSystem( );

		final URL fileUrl = getClass( ).getResource( "file.txt" );
		final Path filePath = Paths.get( fileUrl.toURI( ) );
		final Option<File> result = fileSystem.getSiblingFile( new File( filePath, ".txt", filePath.getParent( ) ), "sibling.txt" );
		assertThat( result ).isNotEmpty( );
	}

	@Test
	@DisplayName( "getSiblingFile shouldn't return non existing file" )
	void getSiblingFileShouldNotReturnNonExistingFile( ) throws URISyntaxException {
		final FileSystem fileSystem = new RealFileSystem( );

		final URL fileUrl = getClass( ).getResource( "file.txt" );
		final Path filePath = Paths.get( fileUrl.toURI( ) );
		final Option<File> result = fileSystem.getSiblingFile( new File( filePath, ".txt", filePath.getParent( ) ), "doesNotExist.txt" );
		assertThat( result ).isEmpty( );
	}

}
