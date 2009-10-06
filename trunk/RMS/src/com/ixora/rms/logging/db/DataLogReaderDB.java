/**
 * 22-Jul-2006
 */
package com.ixora.rms.logging.db;

import com.ixora.rms.client.session.MonitoringSessionDescriptor;
import com.ixora.rms.logging.DataLogReader;
import com.ixora.rms.logging.exception.DataLogException;

/**
 * @author Daniel Moraru
 */
public class DataLogReaderDB implements DataLogReader {

	/**
	 *
	 */
	DataLogReaderDB(String repositoryInfo) {
		super();
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see com.ixora.rms.logging.DataLogReader#readSessionDescriptor()
	 */
	public MonitoringSessionDescriptor readSessionDescriptor()
			throws DataLogException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @see com.ixora.rms.logging.DataLogReader#scan(com.ixora.rms.logging.DataLogReader.ScanCallback)
	 */
	public void scan(ScanCallback cb) throws DataLogException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see com.ixora.rms.logging.DataLogReader#read(com.ixora.rms.logging.DataLogReader.ReadCallback, long, long)
	 */
	public void read(ReadCallback cb, long timestampBegin, long timestampEnd)
			throws DataLogException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see com.ixora.rms.logging.DataLogReader#close()
	 */
	public void close() throws DataLogException {
		// TODO Auto-generated method stub

	}

}
