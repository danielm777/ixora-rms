/**
 * 22-Jul-2006
 */
package com.ixora.rms.logging.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityDataBuffer;
import com.ixora.rms.agents.AgentDataBuffer;
import com.ixora.rms.client.session.MonitoringSessionDescriptor;
import com.ixora.rms.logging.DataLogWriter;
import com.ixora.rms.logging.exception.DataLogException;

/**
 * @author Daniel Moraru
 */
public class DataLogWriterDB implements DataLogWriter {
	/** Connection to the database */
	private Connection fConnection;

	/**
	 *
	 */
	DataLogWriterDB(String repositoryInfo) {
		super();
	}

	/**
	 * @see com.ixora.rms.logging.DataLogWriter#writeSessionDescriptor(com.ixora.rms.client.session.MonitoringSessionDescriptor)
	 */
	public void writeSessionDescriptor(MonitoringSessionDescriptor scheme)
			throws DataLogException {
		// TODO Auto-generated method stub

	}

	/**
	 * @see com.ixora.rms.logging.DataLogWriter#writeBuffer(com.ixora.rms.agents.AgentDataBuffer)
	 */
	public void writeBuffer(AgentDataBuffer db) throws DataLogException {
		if(fConnection == null) {
			try {
				fConnection = DatabaseLoggerUtil.createConnection();
			} catch(Exception e) {
				throw new DataLogException("Failed to open database connection", e);
			}
		}
		try {
			PreparedStatement statement = fConnection.prepareStatement(
					"insert into agent_data_buffer (host,agent,data) values(?,?,?)");
			statement.setString(1, db.getHost().toString());
			statement.setString(2, db.getAgent().toString());
			statement.setString(3, db.getAgentDescriptor().toString());

			//int agentRecordId = 0;
			EntityDataBuffer[] edbs = db.getBuffers();
			if(!Utils.isEmptyArray(edbs)) {
				for(EntityDataBuffer edb : edbs) {
					writeEntityBuffer(edb);
				}
			}
		} catch(SQLException e) {
			throw new DataLogException(e);
		}
	}

	/**
	 * @param edb
	 */
	private void writeEntityBuffer(EntityDataBuffer edb) {


	}

	/**
	 * @see com.ixora.rms.logging.DataLogWriter#close()
	 */
	public void close() throws DataLogException {
		if(fConnection != null) {
			try {
				fConnection.close();
			} catch (SQLException e) {
				throw new DataLogException(e);
			}
		}
	}
}
