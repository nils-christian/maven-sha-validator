package de.rhocas.nce.msv.domain.entity;

import java.nio.file.Path;

import io.vavr.control.Option;

/**
 * This domain entity represents a single file within this application.
 */
public final class File {

	private final Path path;
	private final String extension;

	public File( final Path path, final String extension ) {
		this.path = path;
		this.extension = extension;
	}

	public Option<String> getExtension( ) {
		return Option.of( extension );
	}

	public String getName( ) {
		return path.getFileName( ).toString( );
	}

	public Path getPath( ) {
		return path;
	}

	@Override
	public String toString( ) {
		return path.toString( );
	}

}
