package de.rhocas.nce.msv.domain.port;

import java.nio.file.Path;
import java.util.List;

import de.rhocas.nce.msv.domain.entity.File;
import de.rhocas.nce.msv.domain.error.FilesCannotBeRemoved;
import de.rhocas.nce.msv.domain.error.RootDirectoryCannotBeAccessed;
import io.vavr.control.Either;
import io.vavr.control.Option;

/**
 * A port to access the file system.
 */
public interface FileSystem {

	/**
	 * Lists all files in the given directory.
	 *
	 * @param directory The root directory.
	 *
	 * @return Either an error, if the root directory cannot be accessed, or a list
	 *         with all files.
	 */
	Either<RootDirectoryCannotBeAccessed, List<File>> listFilesInDirectory( Path directory );

	/**
	 * Removes the given files from the file system.
	 *
	 * @param invalidFiles The files to be removed.
	 *
	 * @return An empty option if the operation succeeded, or an error if some files
	 *         could not be removed.
	 */
	Option<FilesCannotBeRemoved> removeFiles( List<File> files );

	/**
	 * Reads all bytes from the given file.
	 *
	 * @param file The file to read bytes from.
	 *
	 * @return The bytes, if the file could be accessed, an empty option otherwise.
	 */
	Option<byte[]> readBytes( File file );

	/**
	 * Gets the sibling of the given file with the given name.
	 *
	 * @param file   The original file.
	 * @param string The name of the sibling.
	 *
	 * @return The sibling file, if it exists, an empty option otherwise.
	 */
	Option<File> getSiblingFile( File file, String string );

	/**
	 * Reads the content of the given file as an UTF-8 string.
	 *
	 * @param file The file to read the content from.
	 *
	 * @return The content, if the file could be accessed, an empty option
	 *         otherwise.
	 */
	Option<String> readString( File file );

}
