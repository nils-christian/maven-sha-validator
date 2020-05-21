package de.rhocas.nce.msv.domain.port;

import de.rhocas.nce.msv.domain.entity.File;
import io.vavr.control.Option;

/**
 * A port to calculate the SHA1 for a given file.
 */
public interface SHA1Calculator {

	/**
	 * Calculates the SHA1 for the given file.
	 *
	 * @param path The file.
	 *
	 * @return The SHA1 if the file could be processed, an empty option otherwise.
	 */
	Option<String> calculate( File path );

}
