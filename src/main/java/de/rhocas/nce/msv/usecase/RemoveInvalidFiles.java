package de.rhocas.nce.msv.usecase;

import java.util.List;

import de.rhocas.nce.msv.domain.entity.File;
import de.rhocas.nce.msv.domain.error.FilesCannotBeRemoved;
import de.rhocas.nce.msv.domain.port.FileSystem;
import io.vavr.control.Option;

/**
 * This use case removes files from the file system.
 */
public final class RemoveInvalidFiles {

	private final FileSystem fileSystem;

	public RemoveInvalidFiles( final FileSystem fileSystem ) {
		this.fileSystem = fileSystem;
	}

	/**
	 * Removes the given files from the file system.
	 *
	 * @param invalidFiles The files to be removed.
	 *
	 * @return An empty option if the operation succeeded, or an error if some files
	 *         could not be removed.
	 */
	public Option<FilesCannotBeRemoved> remove( final List<File> invalidFiles ) {
		return fileSystem.removeFiles( invalidFiles );
	}

}
