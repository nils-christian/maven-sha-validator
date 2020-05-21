package de.rhocas.nce.msv.adapter.sha1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.file.Paths;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.rhocas.nce.msv.domain.entity.File;
import de.rhocas.nce.msv.domain.port.FileSystem;
import io.vavr.control.Option;

@DisplayName( "Unit-Test for DefaultSHA1Extractor" )
final class DefaultSHA1ExtractorTest {

	@Test
	@DisplayName( "Optional from file system should be routed through" )
	void optionalFromFileSystemShouldBeRoutedThrough( ) {
		final File file = new File( Paths.get( "file.jar" ), null );

		final FileSystem fileSystem = mock( FileSystem.class );
		when( fileSystem.getSiblingFile( file, "file.jar.sha1" ) ).thenReturn( Option.none( ) );

		final DefaultSHA1Extractor calculator = new DefaultSHA1Extractor( fileSystem );
		final Option<String> result = calculator.extract( file );

		assertThat( result.isEmpty( ) ).isTrue( );
	}

	@Test
	@DisplayName( "Optional readString from file system should be routed through" )
	void optionalReadStringFromFileSystemShouldBeRoutedThrough( ) {
		final File file = new File( Paths.get( "file.jar" ), "jar" );
		final File sha1File = new File( Paths.get( "file.jar.sha1" ), "sha1" );

		final FileSystem fileSystem = mock( FileSystem.class );
		when( fileSystem.getSiblingFile( file, "file.jar.sha1" ) ).thenReturn( Option.of( sha1File ) );
		when( fileSystem.readString( sha1File ) ).thenReturn( Option.none( ) );

		final DefaultSHA1Extractor calculator = new DefaultSHA1Extractor( fileSystem );
		final Option<String> result = calculator.extract( file );

		assertThat( result.isEmpty( ) ).isTrue( );
	}

	@Test
	@DisplayName( "Simple String should be extracted correctly" )
	void simpleStringShouldBeExtractedCorrectly( ) {
		final File file = new File( Paths.get( "file.jar" ), "jar" );
		final File sha1File = new File( Paths.get( "file.jar.sha1" ), "sha1" );

		final FileSystem fileSystem = mock( FileSystem.class );
		when( fileSystem.getSiblingFile( file, "file.jar.sha1" ) ).thenReturn( Option.of( sha1File ) );
		when( fileSystem.readString( sha1File ) ).thenReturn( Option.of( "05adf2e681c57d7f48038b602f3ca2254ee82d47" ) );

		final DefaultSHA1Extractor calculator = new DefaultSHA1Extractor( fileSystem );
		final Option<String> result = calculator.extract( file );

		assertThat( result.get( ) ).isEqualTo( "05adf2e681c57d7f48038b602f3ca2254ee82d47" );
	}

	@Test
	@DisplayName( "String with garbage should be extracted correctly" )
	void stringWithGarbageShouldBeExtractedCorrectly( ) {
		final File file = new File( Paths.get( "file.jar" ), "jar" );
		final File sha1File = new File( Paths.get( "file.jar.sha1" ), "sha1" );

		final FileSystem fileSystem = mock( FileSystem.class );
		when( fileSystem.getSiblingFile( file, "file.jar.sha1" ) ).thenReturn( Option.of( sha1File ) );
		when( fileSystem.readString( sha1File ) ).thenReturn( Option.of( "682f7ac17fed79e92f8e87d8455192b63376347b  /home/maven/repository-staging/to-ibiblio/maven2/backport-util-concurrent/backport-util-concurrent/3.1/backport-util-concurrent-3.1.jar" ) );

		final DefaultSHA1Extractor calculator = new DefaultSHA1Extractor( fileSystem );
		final Option<String> result = calculator.extract( file );

		assertThat( result.get( ) ).isEqualTo( "682f7ac17fed79e92f8e87d8455192b63376347b" );
	}

}
