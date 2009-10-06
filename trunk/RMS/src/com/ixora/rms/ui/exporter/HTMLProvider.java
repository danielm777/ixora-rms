/**
 * 04-Feb-2006
 */
package com.ixora.rms.ui.exporter;

import java.io.File;
import java.io.IOException;

/**
 * @author Daniel Moraru
 */
public interface HTMLProvider {
	/**
	 * @param buff html to contribute to
	 * @param root the root of the site so that providers
	 * could add their artefacts
	 * @throws IOException
	 */
	void toHTML(StringBuilder buff, File root) throws IOException;
}
