package de.rhocas.nce.msv;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.lang.reflect.Field;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.rhocas.nce.msv.domain.entity.File;
import de.rhocas.nce.msv.domain.error.FilesCannotBeRemoved;
import de.rhocas.nce.msv.domain.error.RootDirectoryCannotBeAccessed;
import de.rhocas.nce.msv.domain.port.Console;
import de.rhocas.nce.msv.usecase.ListInvalidFiles;
import de.rhocas.nce.msv.usecase.RemoveInvalidFiles;
import io.vavr.control.Either;
import io.vavr.control.Option;

@DisplayName( "Unit-Test for MavenSHAValidatorCommand" )
final class MavenSHAValidatorCommandTest {

	@Test
	@DisplayName( "Command should only print files" )
	void commandShouldOnlyPrintFiles( ) throws Exception {
		final Console console = mock( Console.class );
		final ListInvalidFiles listInvalidFiles = mock( ListInvalidFiles.class );
		final Path rootDirectory = Paths.get( "." );
		final File invalidFile = new File( rootDirectory.resolve( "test.jar" ), "jar", rootDirectory );
		when( listInvalidFiles.listInvalidFiles( rootDirectory, Collections.emptyList( ) ) ).thenReturn( Either.right( Collections.singletonList( invalidFile ) ) );
		final RemoveInvalidFiles removeInvalidFiles = mock( RemoveInvalidFiles.class );

		final MavenSHAValidatorCommand command = new MavenSHAValidatorCommand( console, listInvalidFiles, removeInvalidFiles );
		setField( command, "repositoryPath", rootDirectory );
		setField( command, "remove", false );

		assertThat( command.call( ) ).isEqualTo( 0 );
		verify( console ).printMessage( "Invalid file detected: ./test.jar" );
		verifyNoMoreInteractions( console );
		verifyNoInteractions( removeInvalidFiles );
	}

	private void setField( final MavenSHAValidatorCommand command, final String fieldName, final Object value ) throws ReflectiveOperationException {
		final Field field = command.getClass( ).getDeclaredField( fieldName );
		field.setAccessible( true );
		field.set( command, value );
	}

	@Test
	@DisplayName( "Command should print and remove files" )
	void commandShouldPrintAndRemoveFiles( ) throws Exception {
		final Console console = mock( Console.class );
		final ListInvalidFiles listInvalidFiles = mock( ListInvalidFiles.class );
		final Path rootDirectory = Paths.get( "." );
		final File invalidFile = new File( rootDirectory.resolve( "test.jar" ), "jar", rootDirectory );
		when( listInvalidFiles.listInvalidFiles( rootDirectory, Collections.emptyList( ) ) ).thenReturn( Either.right( Collections.singletonList( invalidFile ) ) );
		final RemoveInvalidFiles removeInvalidFiles = mock( RemoveInvalidFiles.class );
		final List<File> invalidFiles = Collections.singletonList( invalidFile );
		when( removeInvalidFiles.remove( invalidFiles ) ).thenReturn( Option.none( ) );

		final MavenSHAValidatorCommand command = new MavenSHAValidatorCommand( console, listInvalidFiles, removeInvalidFiles );
		setField( command, "repositoryPath", rootDirectory );
		setField( command, "remove", true );

		assertThat( command.call( ) ).isEqualTo( 0 );
		verify( console ).printMessage( "Invalid file detected: ./test.jar" );
		verifyNoMoreInteractions( console );
		verify( removeInvalidFiles ).remove( invalidFiles );
	}

	@Test
	@DisplayName( "Command should stop if root directory cannot be accessed" )
	void commandShouldStopIfRootDirectoryCannotBeAccessed( ) throws Exception {
		final Console console = mock( Console.class );
		final ListInvalidFiles listInvalidFiles = mock( ListInvalidFiles.class );
		final Path rootDirectory = Paths.get( "." );
		when( listInvalidFiles.listInvalidFiles( rootDirectory, Collections.emptyList( ) ) ).thenReturn( Either.left( new RootDirectoryCannotBeAccessed( ) ) );
		final RemoveInvalidFiles removeInvalidFiles = mock( RemoveInvalidFiles.class );

		final MavenSHAValidatorCommand command = new MavenSHAValidatorCommand( console, listInvalidFiles, removeInvalidFiles );
		setField( command, "repositoryPath", rootDirectory );
		setField( command, "remove", true );

		assertThat( command.call( ) ).isEqualTo( -1 );
		verify( console ).printError( "The given directory '.' cannot be accessed." );
		verifyNoMoreInteractions( console );
		verifyNoInteractions( removeInvalidFiles );
	}

	@Test
	@DisplayName( "Command should stop if files cannot be removed" )
	void commandShouldStopIfFilesCannotBeRemoved( ) throws Exception {
		final Console console = mock( Console.class );
		final ListInvalidFiles listInvalidFiles = mock( ListInvalidFiles.class );
		final Path rootDirectory = Paths.get( "." );
		final File invalidFile = new File( rootDirectory.resolve( "test.jar" ), "jar", rootDirectory );
		when( listInvalidFiles.listInvalidFiles( rootDirectory, Collections.emptyList( ) ) ).thenReturn( Either.right( Collections.singletonList( invalidFile ) ) );
		final RemoveInvalidFiles removeInvalidFiles = mock( RemoveInvalidFiles.class );
		final List<File> invalidFiles = Collections.singletonList( invalidFile );
		when( removeInvalidFiles.remove( invalidFiles ) ).thenReturn( Option.some( new FilesCannotBeRemoved( invalidFiles ) ) );

		final MavenSHAValidatorCommand command = new MavenSHAValidatorCommand( console, listInvalidFiles, removeInvalidFiles );
		setField( command, "repositoryPath", rootDirectory );
		setField( command, "remove", true );

		assertThat( command.call( ) ).isEqualTo( -2 );
		verify( console ).printMessage( "Invalid file detected: ./test.jar" );
		verify( console ).printError( "Following files could not be removed: [./test.jar]" );
		verifyNoMoreInteractions( console );
		verify( removeInvalidFiles ).remove( invalidFiles );
	}

}
