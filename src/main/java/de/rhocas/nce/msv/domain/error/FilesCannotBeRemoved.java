package de.rhocas.nce.msv.domain.error;

import java.util.List;

import de.rhocas.nce.msv.domain.entity.File;

/**
 * This error can be used if one or more files cannot be removed.
 */
public final class FilesCannotBeRemoved {

	private final List<File> unremovableFiles;

	public FilesCannotBeRemoved( final List<File> unremovableFiles ) {
		this.unremovableFiles = unremovableFiles;
	}

	public List<File> getUnremovableFiles( ) {
		return unremovableFiles;
	}

}
