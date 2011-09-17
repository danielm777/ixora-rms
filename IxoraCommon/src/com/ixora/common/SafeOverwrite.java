/*
 * Created on 23-Mar-2005
 */
package com.ixora.common;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;

/**
 * @author Daniel Moraru
 */
public final class SafeOverwrite {
    /** Logger */
    private static final AppLogger logger = AppLoggerFactory.getLogger(SafeOverwrite.class);

	private File fFile;
	private File fTmp;

	/**
	 * Constructor.
	 */
	public SafeOverwrite(File file) {
		super();
		this.fFile = file;
	}

	/**
	 * @param store
	 */
	public SafeOverwrite(String store) {
		super();
		this.fFile = new File(store);
	}

	/**
	 * Entry method.
	 * @throws IOException
	 */
	public void backup() throws IOException {
		if(!fFile.exists()) {
			return;
		}
		fTmp = File.createTempFile("backup", null);
		if(logger.isTraceEnabled()) {
			logger.trace("Backing up file " + fFile + " to " + fTmp + 
					"...");
		}
		Utils.copyFile(fFile, fTmp);
		if(logger.isTraceEnabled()) {
			logger.trace("Back up done");
		}
	}

	/**
	 * Exit method on success.
	 * @param os the output stream on which writing was performed; if not null it will be closed
	 */
	public void commit(OutputStream os) {
		if(os != null) {
			try {
				os.flush();
				os.close();
			} catch(IOException e) {
				logger.error(e);
			}
		}
		if(fTmp != null) {
			fTmp.delete();
		}
	}

	/**
	 * Exit method on failure.
	 * @param os the output stream on which writing failed; if not null it will be closed
	 * @throws IOException
	 */
	public void rollback(OutputStream os) throws IOException {
		if(os != null) {
			try {
				os.close();
			} catch(IOException e) {
				logger.error(e);
			}
		}
		if(fTmp != null) {
			if(logger.isTraceEnabled()) {
				logger.trace("Rolling back file " + fTmp + " to " + fFile + "...");
			}
			Utils.copyFile(fTmp, fFile);
			fTmp.delete();
			if(logger.isTraceEnabled()) {
				logger.trace("Roll back done");
			}
		}
	}
}
