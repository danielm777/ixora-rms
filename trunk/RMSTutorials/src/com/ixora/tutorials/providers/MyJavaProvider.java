package com.ixora.tutorials.providers;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import com.ixora.rms.providers.impl.java.JavaProviderImplementation;
import com.ixora.rms.providers.impl.java.JavaProviderImplementationContext;
import com.ixora.rms.providers.utils.sql.CachedConnection;
import com.ixora.rms.providers.utils.sql.ConnectionCache;

/**
 * The implementation of a Java provider that reads data from a MySQL database.
 * @author Daniel Moraru
 */
public class MyJavaProvider implements JavaProviderImplementation {
	/**
	 * Execution context; use it to report non-fatal errors to the
	 * console.
	 */
	private JavaProviderImplementationContext fContext;
	/** The JDBC driver. It is required by the connection cache. */
	private Driver fDriver;
	/** The JDBC connection string. */
	private String fConnectionString;
	/** The JDBC username */
	private String fUsername;
	/** The JDBC password */
	private String fPassword;
	/** The operation type */
	private String fOperation;

	/**
	 * Use a connection cache which will be shared by all providers
	 * using this implementation class.<br>
	 * If you are concern with the number of connections open to the database
	 * you should use a shared class loader to benefit from connection sharing.
	 */
	private static ConnectionCache fConnectionCache;

	static {
		try {
			fConnectionCache = new ConnectionCache();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * @see com.ixora.rms.providers.impl.java.JavaProviderImplementation#initialize(java.lang.String[], com.ixora.rms.providers.impl.java.JavaProviderImplementationContext)
	 * @param parameters will be the following:
	 * <ul>
	 * <li>jdbc connection string
	 * <li>jdbc driver class name
	 * <li>username - the jdbc username
	 * <li>password - the jdbc password
	 * <li>operation - depending on this value, different values are returned, allowing the same
	 * implementation class to be used by different java providers; this allows for possible sharing of
	 * database connections to reduce the cost of monitoring
	 * </ul>
	 */
	public void initialize(String[] parameters, JavaProviderImplementationContext context) throws Exception {
		if(parameters == null || parameters.length != 5) {
			throw new Exception("Invalid parameters");
		}
		fConnectionString = parameters[0];
		String driverClassName = parameters[1];
		fUsername = parameters[2];
		fPassword = parameters[3];
		//fContext = context;

		PrintWriter w = new PrintWriter(new OutputStreamWriter(System.out));
		DriverManager.setLogWriter(w);

		Class.forName(driverClassName);
		fDriver = DriverManager.getDriver(fConnectionString);

		// Test if the connection is possible
		Connection conn = DriverManager.getConnection(fConnectionString, fUsername, fPassword);
		conn.close();
	}

	/**
	 * @see com.ixora.rms.providers.impl.java.JavaProviderImplementation#getValues()
	 */
	public String[][] getValues() throws Exception {
		CachedConnection connection = null;
		Statement statement = null;
		ResultSet resultSet = null;
		try {
			connection = fConnectionCache.getConnection(
					fDriver, fConnectionString, fUsername, fPassword);
			statement = connection.getSQLConnection().createStatement();
			resultSet = statement.executeQuery(getSQL());
			int columnCount = resultSet.getMetaData().getColumnCount();
			List<String[]> rows = new LinkedList<String[]>();
			while(resultSet.next()) {
				String[] row = new String[columnCount];
				for(int i = 1; i <= columnCount; i++) {
					Object obj = resultSet.getObject(i);
					row[i-1] = obj.toString();
					rows.add(row);
				}
			}
			return rows.toArray(new String[rows.size()][columnCount]);
		} finally {
			if(resultSet != null) {
				resultSet.close();
			}
			if(statement != null) {
				statement.close();
			}
			if(connection != null) {
				connection.close();
			}
		}
	}

	/**
	 * Based on the configured operation it returns the corresponding SQL.<br>
	 * Of course of it was that simple you would have used an SQL provider; probably
	 * depending on the operation specified during initialization a more complex
	 * type of processing would take place; in this example we assume that different
	 * operations means just using different SQLs.
	 * @throws Exception
	 * @return
	 */
	private String getSQL() throws Exception {
		if("default".equals(fOperation)) {
			return "select counter1,counter2,counter3,counter4 from mytable";
		}
		throw new Exception("Unknown operation: " + fOperation);
	}

	/**
	 * @see com.ixora.rms.providers.impl.java.JavaProviderImplementation#cleanup()
	 */
	public void cleanup() throws Exception {
		;// not necessary, the connections in the ConnectionCache will time out if idle
	}
}
