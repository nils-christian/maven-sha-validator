package de.rhocas.nce.msv.adapter.sha1;

import java.text.MessageFormat;

import de.rhocas.nce.msv.domain.entity.File;
import de.rhocas.nce.msv.domain.port.FileSystem;
import de.rhocas.nce.msv.domain.port.SHA1Extractor;
import io.vavr.control.Option;

/**
 * This adapter is the default implementation of the {@link SHA1Extractor}.
 */
public final class DefaultSHA1Extractor implements SHA1Extractor {

	private final FileSystem fileSystem;

	public DefaultSHA1Extractor( final FileSystem fileSystem ) {
		this.fileSystem = fileSystem;
	}

	@Override
	public Option<String> extract( final File file ) {
		final String siblingFileName = MessageFormat.format( "{0}.sha1", file.getName( ).toString( ) );
		return fileSystem.getSiblingFile( file, siblingFileName )
				.flatMap( sha1File -> fileSystem.readString( sha1File ) )
				.map( string -> extractActualSHA1String( string ) );
	}

	private String extractActualSHA1String( final String string ) {
		String actualSHA1String = string;
		final int firstSpaceIndex = actualSHA1String.indexOf( ' ' );
		if ( firstSpaceIndex != -1 ) {
			actualSHA1String = actualSHA1String.substring( 0, firstSpaceIndex );
		}
		return actualSHA1String;
	}

}
