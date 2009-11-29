package com.ixora.rms.logging;

import com.ixora.rms.logging.db.DataLogRepositoryDB;
import com.ixora.rms.logging.exception.DataLogException;
import com.ixora.rms.logging.exception.InvalidLogRepository;
import com.ixora.rms.logging.messages.Msg;
import com.ixora.rms.logging.xml.DataLogRepositoryXML;

/**
 * @author Daniel Moraru
 */
public class DataLogUtils {
	/**
	 * @param repositoryInfo1
	 * @return
	 * @throws InvalidLogRepository
	 * @throws DataLogException
	 */
	public static DataLogReader createReader(LogRepositoryInfo repositoryInfo1) throws InvalidLogRepository, DataLogException {
		String type = repositoryInfo1.getRepositoryType();
		DataLogReader ret = null;
		if(LogRepositoryInfo.TYPE_XML.equals(type)) {
			ret = new DataLogRepositoryXML().getReader(repositoryInfo1);
		} else if(LogRepositoryInfo.TYPE_DATABASE.equals(type)) {
			ret = new DataLogRepositoryDB().getReader(repositoryInfo1);
		}
		if(ret == null) {
			InvalidLogRepository e = new InvalidLogRepository(
			        Msg.LOGGING_UNRECOGNIZED_LOG_TYPE,
			        new String[]{repositoryInfo1.getRepositoryType()});
			e.setIsInternalAppError();
			throw e;
		}
		return ret;
	}
}
