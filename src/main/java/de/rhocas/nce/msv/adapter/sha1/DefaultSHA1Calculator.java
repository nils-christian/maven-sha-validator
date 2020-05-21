package de.rhocas.nce.msv.adapter.sha1;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;

import de.rhocas.nce.msv.domain.entity.File;
import de.rhocas.nce.msv.domain.port.FileSystem;
import de.rhocas.nce.msv.domain.port.SHA1Calculator;
import io.vavr.control.Option;

/**
 * This adapter is the default implementation of the {@link SHA1Calculator}.
 */
public final class DefaultSHA1Calculator implements SHA1Calculator {

	private static final String SHA1_ALGORITHM = "SHA-1";
	private final FileSystem fileSystem;

	public DefaultSHA1Calculator( final FileSystem fileSystem ) {
		this.fileSystem = fileSystem;
	}

	@Override
	public Option<String> calculate( final File file ) {
		return fileSystem.readBytes( file )
				.map( bytes -> calculateSHA1Bytes( bytes ) )
				.map( bytes -> byteToHex( bytes ) );
	}

	private byte[] calculateSHA1Bytes( final byte[] bytes ) {
		try {
			final MessageDigest messageDigest = MessageDigest.getInstance( SHA1_ALGORITHM );
			return messageDigest.digest( bytes );
		} catch ( final NoSuchAlgorithmException ex ) {
			// TODO: Can we do this better?
			throw new RuntimeException( "The SHA1-Algorithm is not available.", ex );
		}
	}

	private String byteToHex( final byte[] bytes ) {
		try ( final Formatter formatter = new Formatter( ) ) {
			for ( final byte b : bytes ) {
				formatter.format( "%02x", b );
			}
			return formatter.toString( );
		}
	}

}
