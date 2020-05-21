package de.rhocas.nce.msv.adapter.sha1;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import de.rhocas.nce.msv.domain.entity.File;
import de.rhocas.nce.msv.domain.port.FileSystem;
import io.vavr.control.Option;

@DisplayName( "Unit-Test for DefaultSHA1Calculator" )
final class DefaultSHA1CalculatorTest {

	@Test
	@DisplayName( "Optional from file system should be routed through" )
	void optionalFromFileSystemShouldBeRoutedThrough( ) {
		final File file = new File( null, null );

		final FileSystem fileSystem = mock( FileSystem.class );
		when( fileSystem.readBytes( file ) ).thenReturn( Option.none( ) );

		final DefaultSHA1Calculator calculator = new DefaultSHA1Calculator( fileSystem );
		final Option<String> result = calculator.calculate( file );

		assertThat( result.isEmpty( ) ).isTrue( );
	}

	@Test
	@DisplayName( "SHA-1 for empty String should be calculated correctly" )
	void sha1ForEmptyStringShouldBeCalculatedCorrectly( ) {
		final File file = new File( null, null );

		final FileSystem fileSystem = mock( FileSystem.class );
		when( fileSystem.readBytes( file ) ).thenReturn( Option.of( new byte[0] ) );

		final DefaultSHA1Calculator calculator = new DefaultSHA1Calculator( fileSystem );
		final Option<String> result = calculator.calculate( file );

		assertThat( result.get( ) ).isEqualTo( "da39a3ee5e6b4b0d3255bfef95601890afd80709" );
	}

	@Test
	@DisplayName( "SHA-1 for simple String should be calculated correctly" )
	void sha1ForSimpleStringShouldBeCalculatedCorrectly( ) {
		final File file = new File( null, null );

		final FileSystem fileSystem = mock( FileSystem.class );
		when( fileSystem.readBytes( file ) ).thenReturn( Option.of( "Franz jagt im komplett verwahrlosten Taxi quer durch Bayern".getBytes( StandardCharsets.UTF_8 ) ) );

		final DefaultSHA1Calculator calculator = new DefaultSHA1Calculator( fileSystem );
		final Option<String> result = calculator.calculate( file );

		assertThat( result.get( ) ).isEqualTo( "68ac906495480a3404beee4874ed853a037a7a8f" );
	}

}
