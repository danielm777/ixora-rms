/**
 * 26-Dec-2005
 */
package com.ixora.rms.agents.mysql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import com.ixora.rms.CounterId;
import com.ixora.rms.agents.AgentDataBufferImpl;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.agents.impl.AbstractAgent;
import com.ixora.rms.exception.InvalidConfiguration;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public abstract class MySQLAgentForVersion extends AbstractAgent {
	protected Map<CounterId, Object> fValues;
	protected Connection fConnection;

	private class MySQLContext extends ExecutionContext implements MySQLAgentContext {
		/**
		 * @see com.ixora.rms.agents.mysql.MySQLAgentContext#getValues()
		 */
		public Map<CounterId, Object> getValues() {
			return fValues;
		}
	}

	/**
	 * @param agentId
	 * @param listener
	 * @throws Throwable
	 */
	protected MySQLAgentForVersion(AgentId agentId, Listener listener) throws Throwable {
		super(agentId, listener);
		replaceContext(new MySQLContext());
		fValues = new HashMap<CounterId, Object>();
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#configCustomChanged()
	 */
	protected void configCustomChanged() throws InvalidConfiguration, Throwable {
		Configuration custom = getMySQLConfiguration();
		String driverClass = custom.getString(Configuration.JDBC_DRIVER_CLASS_NAME);
		try {
			Class.forName(driverClass);
		} catch(ClassNotFoundException e) {
			// TODO localize
			throw new RMSException("Driver class " + driverClass + " was not found. Check your classpath.");
		}

		// see if we can get a connection and convert SQLExceptions into RMS ones
		// to avoid being reported as system errors
		try {
			Connection conn = DriverManager.getConnection(
					custom.getConnectionString(fConfiguration.getMonitoredHost()),
							custom.getString(Configuration.USERNAME),
							custom.getString(Configuration.PASSWORD));
			conn.close();
		} catch(SQLException e) {
			// TODO localize
			throw new RMSException("Failed to establish connection.", e);
		}
		fConnection = null;
	}

	/**
	 * @see com.ixora.rms.agents.impl.AbstractAgent#prepareBuffer(com.ixora.rms.agents.AgentDataBufferImpl)
	 */
	protected void prepareBuffer(AgentDataBufferImpl buffer) throws Throwable {
		// retrieve data and put it in the context to be passed to
		// entities
		retreiveValues();
		super.prepareBuffer(buffer);
		fValues.clear();
	}

	/**
	 * @return the MySQLAgent custom configuration
	 */
	private Configuration getMySQLConfiguration() {
		return (Configuration)fConfiguration.getAgentCustomConfiguration();
	}

	/**
	 * Retrieves the counter values.
	 * @throws SQLException
	 */
	private void retreiveValues() throws SQLException {
		if(fConnection == null) {
			Configuration custom = getMySQLConfiguration();
			fConnection = DriverManager.getConnection(
					custom.getConnectionString(fConfiguration.getMonitoredHost()),
							custom.getString(Configuration.USERNAME),
							custom.getString(Configuration.PASSWORD));
		}
		Statement stat = null;
		ResultSet rs = null;
		try {
			stat = fConnection.createStatement();
			rs = stat.executeQuery("show status");
			//ResultSetMetaData metaData = rs.getMetaData();
			while(rs.next()) {
				// counter name
				Object obj = rs.getObject(1);
				// counter value
				Object val = rs.getObject(2);
				fValues.put(new CounterId((String)obj), val);
			}
		} finally {
			if(rs != null) {
				try {
					rs.close();
				} catch(Exception e) {
					fContext.error(e);
				}
			}
			if(stat != null) {
				try {
					stat.close();
				} catch(Exception e) {
					fContext.error(e);
				}
			}
		}
	}

	/**
	 * @see com.ixora.rms.agents.Agent#deactivate()
	 */
	public synchronized void deactivate() throws Throwable {
		if(fConnection != null) {
			try {
				fConnection.close();
			} catch(Exception e) {
				;
			}
		}
	}
}
