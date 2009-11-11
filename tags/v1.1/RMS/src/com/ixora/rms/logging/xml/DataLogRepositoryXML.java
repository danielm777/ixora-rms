/*
 * Created on 07-Jun-2004
 */
package com.ixora.rms.logging.xml;

import com.ixora.rms.logging.DataLogReader;
import com.ixora.rms.logging.DataLogRepository;
import com.ixora.rms.logging.DataLogWriter;
import com.ixora.rms.logging.LogRepositoryInfo;
import com.ixora.rms.logging.exception.DataLogException;
import com.ixora.rms.logging.exception.InvalidLogRepository;

/**
 * Saves data as XML.
 * @author Daniel Moraru
 */
public final class DataLogRepositoryXML implements DataLogRepository {
	/**
	 * Constructor.
	 */
	public DataLogRepositoryXML() {
		super();
	}
	/**
	 * @see com.ixora.rms.logging.DataLogRepository#getReader(com.ixora.rms.control.struct.LogRepositoryInfo)
	 */
	public DataLogReader getReader(LogRepositoryInfo rep) throws InvalidLogRepository, DataLogException {
		return new DataLogReaderXML(rep.getRepositoryName());
	}
	/**
	 * @see com.ixora.rms.logging.DataLogRepository#getWriter(com.ixora.rms.control.struct.LogRepositoryInfo)
	 */
	public DataLogWriter getWriter(LogRepositoryInfo rep) throws InvalidLogRepository {
		return new DataLogWriterXML(rep.getRepositoryName());
	}
}
