/*
 * Created on 18-Jun-2004
 */
package com.ixora.common.update;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.ixora.common.Stopper;
import com.ixora.common.utils.Utils;

/**
 * @author Daniel Moraru
 */
public final class UpdatePartInstaller {
	/**
	 * Constructor.
	 */
	private UpdatePartInstaller() {
		super();
	}

	/**
	 * Installs the given part.
	 * @param part
	 * @param contentStream
	 * @param localUpdateRepository
	 * @param stopper
	 * @throws IOException
	 */
	public static void installUpdatePart(
			UpdatePartDescriptor part,
			InputStream contentStream,
			File localUpdateRepository,
			Stopper stopper) throws IOException {
		String local = part.getInstallLocation();
		File localFile = new File(localUpdateRepository, local);
		localFile.mkdirs();
		localFile = new File(localFile, part.getContentLocation());
		OutputStream os = new BufferedOutputStream(
					new FileOutputStream(localFile));
		Utils.transferContent(contentStream, os, stopper);
	}
}
