package de.rhocas.nce.msv.domain.port;

import de.rhocas.nce.msv.domain.entity.File;
import io.vavr.control.Option;

/**
 * A port to extract the expected SHA1 for a given file.
 */
public interface SHA1Extractor {

	/**
	 * Extracts the SHA1 for the given file.
	 *
	 * @param path The file.
	 *
	 * @return The SHA1 if the expected SHA1 for the file could be found, an empty
	 *         option otherwise.
	 */
	Option<String> extract( File path );

}
