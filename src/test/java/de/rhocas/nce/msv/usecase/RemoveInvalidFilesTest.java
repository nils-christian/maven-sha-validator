package de.rhocas.nce.msv.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.rhocas.nce.msv.domain.entity.File;
import de.rhocas.nce.msv.domain.error.FilesCannotBeRemoved;
import de.rhocas.nce.msv.domain.port.FileSystem;
import io.vavr.control.Option;

@DisplayName( "Unit-Test for RemoveInvalidFiles" )
final class RemoveInvalidFilesTest {

	@Test
	@DisplayName( "Success should be routed through" )
	void successShouldBeRoutedThrough( ) {
		final List<File> files = Collections.emptyList( );
		final FileSystem fileSystem = mock( FileSystem.class );
		when( fileSystem.removeFiles( files ) ).thenReturn( Option.none( ) );

		final RemoveInvalidFiles removeInvalidFiles = new RemoveInvalidFiles( fileSystem );
		assertThat( removeInvalidFiles.remove( files ).isEmpty( ) ).isTrue( );
	}

	@Test
	@DisplayName( "Error should be routed through" )
	void errorShouldBeRoutedThrough( ) {
		final List<File> files = Collections.emptyList( );
		final FileSystem fileSystem = mock( FileSystem.class );
		final FilesCannotBeRemoved error = new FilesCannotBeRemoved( Collections.emptyList( ) );
		when( fileSystem.removeFiles( files ) ).thenReturn( Option.of( error ) );

		final RemoveInvalidFiles removeInvalidFiles = new RemoveInvalidFiles( fileSystem );
		assertThat( removeInvalidFiles.remove( files ).get( ) ).isEqualTo( error );
	}

}
