package de.rhocas.nce.msv.domain.entity;

import java.nio.file.Path;

import io.vavr.control.Option;

/**
 * This domain entity represents a single file within this application.
 */
public final class File {

	private final Path path;
	private final String extension;
	private final Path rootDirectory;

	public File( final Path path, final String extension, final Path rootDirectory ) {
		this.path = path;
		this.extension = extension;
		this.rootDirectory = rootDirectory;
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

	public Path getRootDirectory( ) {
		return rootDirectory;
	}

	public String getPathRelativeToDirectory( ) {
		return rootDirectory.relativize( path ).toString( );
	}

}
