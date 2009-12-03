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
		LogRepositoryInfo.Type type = repositoryInfo1.getRepositoryType();
		DataLogReader ret = null;
		switch (type) {
		case xml:
			ret = new DataLogRepositoryXML().getReader(repositoryInfo1);
			break;
		case db:
			ret = new DataLogRepositoryDB().getReader(repositoryInfo1);
			break;
		default:
			break;
		}
		if(ret == null) {
			InvalidLogRepository e = new InvalidLogRepository(
			        Msg.LOGGING_UNRECOGNIZED_LOG_TYPE,
			        new String[]{repositoryInfo1.getRepositoryType().name()});
			e.setIsInternalAppError();
			throw e;
		}
		return ret;
	}
	
	public static DataLogWriter createWriter(LogRepositoryInfo repositoryInfo) throws InvalidLogRepository {
		LogRepositoryInfo.Type type = repositoryInfo.getRepositoryType();
		DataLogWriter ret = null;
		switch (type) {
		case xml:
			ret = new DataLogRepositoryXML().getWriter(repositoryInfo);
			break;
		case db:
			ret = new DataLogRepositoryDB().getWriter(repositoryInfo);
			break;
		default:
			break;
		}
		if(ret == null) {
			InvalidLogRepository e = new InvalidLogRepository(
			        Msg.LOGGING_UNRECOGNIZED_LOG_TYPE,
			        new String[]{repositoryInfo.getRepositoryType().name()});
			e.setIsInternalAppError();
			throw e;
		}
		return ret;
	}
}
