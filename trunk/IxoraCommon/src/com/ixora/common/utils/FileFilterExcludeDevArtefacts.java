/**
 * 
 */
package com.ixora.common.utils;

import java.io.File;
import java.io.FileFilter;

/**
 * @author Daniel Moraru
 */
public class FileFilterExcludeDevArtefacts implements FileFilter {

	/**
	 * @see java.io.FileFilter#accept(java.io.File)
	 */
	public boolean accept(File pathname) {
		return !(pathname.isDirectory() && pathname.getName().startsWith("."));
	}

}
