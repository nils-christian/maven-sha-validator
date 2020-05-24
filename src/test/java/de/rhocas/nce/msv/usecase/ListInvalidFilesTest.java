package de.rhocas.nce.msv.usecase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.rhocas.nce.msv.domain.entity.File;
import de.rhocas.nce.msv.domain.error.RootDirectoryCannotBeAccessed;
import de.rhocas.nce.msv.domain.port.FileSystem;
import de.rhocas.nce.msv.domain.port.SHA1Calculator;
import de.rhocas.nce.msv.domain.port.SHA1Extractor;
import io.vavr.control.Either;
import io.vavr.control.Option;

@DisplayName( "Unit-Test for ListInvalidFiles" )
final class ListInvalidFilesTest {

	@Test
	@DisplayName( "Only relevant files should be listed" )
	void onlyRelevantFilesShouldBeListed( ) {
		final File file1 = new File( Paths.get( "commons-logging-1.0.4.pom" ), "pom", Paths.get( "." ) );
		final File file2 = new File( Paths.get( "_remote.repositories" ), "repositories", Paths.get( "." ) );
		final File file3 = new File( Paths.get( "commons-logging-1.0.4-sources.jar" ), "jar", Paths.get( "." ) );
		final File file4 = new File( Paths.get( "m2e-lastUpdated.properties" ), "properties", Paths.get( "." ) );

		final Path directory = Paths.get( "repository" );

		final FileSystem fileSystem = mock( FileSystem.class );
		when( fileSystem.listFilesInDirectory( directory ) ).thenReturn( Either.right( Arrays.asList( file1, file2, file3, file4 ) ) );
		final SHA1Calculator sha1Calculator = mock( SHA1Calculator.class );
		when( sha1Calculator.calculate( file1 ) ).thenReturn( Option.of( "f029a2aefe2b3e1517573c580f948caac31b1056" ) );
		when( sha1Calculator.calculate( file3 ) ).thenReturn( Option.none( ) );
		final SHA1Extractor sha1Extractor = mock( SHA1Extractor.class );
		when( sha1Extractor.extract( file1 ) ).thenReturn( Option.of( "f0" ) );
		when( sha1Extractor.extract( file3 ) ).thenReturn( Option.none( ) );

		final ListInvalidFiles listInvalidFiles = new ListInvalidFiles( fileSystem, sha1Calculator, sha1Extractor );
		final Either<RootDirectoryCannotBeAccessed, List<File>> result = listInvalidFiles.listInvalidFiles( directory, Collections.emptyList( ) );

		assertThat( result.get( ) ).containsExactly( file1, file3 );
	}

	@Test
	@DisplayName( "Valid files should not be listed" )
	void validFilesShouldNotBeListed( ) {
		final File file = new File( Paths.get( "commons-logging-1.0.4.pom" ), "pom", Paths.get( "." ) );

		final Path directory = Paths.get( "repository" );

		final FileSystem fileSystem = mock( FileSystem.class );
		when( fileSystem.listFilesInDirectory( directory ) ).thenReturn( Either.right( Arrays.asList( file ) ) );
		final SHA1Calculator sha1Calculator = mock( SHA1Calculator.class );
		when( sha1Calculator.calculate( file ) ).thenReturn( Option.of( "f029a2aefe2b3e1517573c580f948caac31b1056" ) );
		final SHA1Extractor sha1Extractor = mock( SHA1Extractor.class );
		when( sha1Extractor.extract( file ) ).thenReturn( Option.of( "f029a2aefe2b3e1517573c580f948caac31b1056" ) );

		final ListInvalidFiles listInvalidFiles = new ListInvalidFiles( fileSystem, sha1Calculator, sha1Extractor );
		final Either<RootDirectoryCannotBeAccessed, List<File>> result = listInvalidFiles.listInvalidFiles( directory, Collections.emptyList( ) );

		assertThat( result.get( ) ).isEmpty( );
	}

	@Test
	@DisplayName( "Ignored path prefixes should be honored" )
	void ignoredPathPrefixesShouldBeHonored( ) {
		final File file = new File( Paths.get( "a" ).resolve( "b" ).resolve( "commons-logging-1.0.4.pom" ), "pom", Paths.get( "a" ) );

		final Path directory = Paths.get( "repository" );

		final FileSystem fileSystem = mock( FileSystem.class );
		when( fileSystem.listFilesInDirectory( directory ) ).thenReturn( Either.right( Arrays.asList( file ) ) );
		final SHA1Calculator sha1Calculator = mock( SHA1Calculator.class );
		final SHA1Extractor sha1Extractor = mock( SHA1Extractor.class );

		final ListInvalidFiles listInvalidFiles = new ListInvalidFiles( fileSystem, sha1Calculator, sha1Extractor );
		final Either<RootDirectoryCannotBeAccessed, List<File>> result = listInvalidFiles.listInvalidFiles( directory, Collections.singletonList( "B" ) );

		verifyNoInteractions( sha1Calculator );
		verifyNoInteractions( sha1Extractor );
		assertThat( result.get( ) ).isEmpty( );
	}

}
