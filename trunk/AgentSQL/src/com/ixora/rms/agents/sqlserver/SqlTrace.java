package com.ixora.rms.agents.sqlserver;

import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.ixora.common.utils.Utils;
import com.ixora.rms.agents.AgentConfiguration;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.sqlserver.events.SQLTraceEventCursors;
import com.ixora.rms.agents.sqlserver.events.SQLTraceEventDatabase;
import com.ixora.rms.agents.sqlserver.events.SQLTraceEventErrors;
import com.ixora.rms.agents.sqlserver.events.SQLTraceEventLocks;
import com.ixora.rms.agents.sqlserver.events.SQLTraceEventScans;
import com.ixora.rms.agents.sqlserver.events.SQLTraceEventSessions;
import com.ixora.rms.agents.sqlserver.events.SQLTraceEventStoredProcedures;
import com.ixora.rms.agents.sqlserver.events.SQLTraceEventTSQL;
import com.ixora.rms.agents.sqlserver.events.SQLTraceEventTransactions;
import com.ixora.rms.agents.sqlserver.events.TracedEventColumn;
import com.ixora.rms.agents.utils.SQLBasedEntity;
import com.ixora.rms.exception.RMSException;

/**
 * @author Cristian Costache
 * @author Daniel Moraru
 */
public class SqlTrace extends SQLBasedEntity implements Runnable {
	private static final int BEGIN_TRACE_COLUMN_ID = 65530;
	private static final int LAST_COLUMN_ID = 65522;
	private static final int EVENT_CLASS_COLUMN_ID = 65526;

	private boolean fRunning = false;
    private GregorianCalendar 	calendar = new GregorianCalendar();

	private int 				traceID;
	private CallableStatement	statSetEvent;

	private SQLTraceEventCursors 	sqlTraceEventCursors;
	private SQLTraceEventDatabase 	sqlTraceEventDatabase;
	private SQLTraceEventErrors 	sqlTraceEventErrors;
	private SQLTraceEventLocks		sqlTraceEventLocks;
	private SQLTraceEventScans		sqlTraceEventScans;
	//private SQLTraceEventSecurity	sqlTraceEventSecurity;
	private SQLTraceEventSessions	sqlTraceEventSessions;
	private SQLTraceEventStoredProcedures	sqlTraceEventStoredProcedures;
	private SQLTraceEventTransactions	sqlTraceEventTransactions;
	private SQLTraceEventTSQL		sqlTraceEventTSQL;

	/**
	 * SQLColumnData
	 * Holds data for each column returned by the trace, mapped to a column ID. Its getter
	 * functions will handle type conversions. The Trace will fill this structure and pass
	 * it to event entities to fill their counters.
	 */
	public class SQLColumnData extends HashMap<Integer, byte[]> {

	    private static final int COL_TYPE_UNKNOWN = 0;
	    private static final int COL_TYPE_STRING = 1;
	    private static final int COL_TYPE_INT = 2;
	    private static final int COL_TYPE_LONG = 3;
	    private static final int COL_TYPE_TIME = 4;

	    /**
	     * Returns a double representing the contents of the specified column,
	     * performing appropriate conversions if required.
	     * @param columnId
	     * @return
	     */
	    public double getDouble(int columnId) {
	        byte[] bytes = get(new Integer(columnId));
	        if (bytes == null) {
	            // some trace events don't return all columns all the time,
	            // this is not an error condition.
	            return 0;
	        }

	        switch (getColType(columnId)) {
	        	case COL_TYPE_STRING:
	        	    return 0; // Double.valueOf(new String(bytes, "UTF-16LE")); throws exception
	        	case COL_TYPE_INT:
	        	    return makeInt(bytes);
	        	case COL_TYPE_LONG:
	        	    return makeLong(bytes);
	        	case COL_TYPE_TIME:
	        	    return makeTime(bytes);
	        	default: // case COL_TYPE_UNKNOWN:
	        	    return 0;
	        }
	    }

	    /**
	     * Convenience method: returns getDouble cast to int
	     * @param columnId
	     * @return
	     */
	    public int getInteger(int columnId) {
	        return (int)getDouble(columnId);
	    }

	    /**
	     * Returns a String representing the contents of the specified column,
	     * performing appropriate conversions if required.
	     * @param columnId
	     * @return
	     */
	    public String getString(int columnId) {
	        byte[] bytes = get(new Integer(columnId));
	        if (bytes == null) {
	            // some trace events don't return all columns all the time,
	            // this is not an error condition.
	            return new String();
	        }

	        switch (getColType(columnId)) {
	        	case COL_TYPE_STRING:
	        	    try {
	        	        return new String(bytes, "UTF-16LE");
	        	    } catch (UnsupportedEncodingException e) {
	        	        // should never happen, but just in case
		        	    try {
		        	        return new String(bytes, "UTF-8");
		        	    } catch (UnsupportedEncodingException e2) {
		        	        // oh well, we tried
		        	        return new String();
		        	    }
	        	    }
	        	case COL_TYPE_INT:
	        	    return Integer.toString(makeInt(bytes));
	        	case COL_TYPE_LONG:
	        	    return Long.toString(makeLong(bytes));
	        	case COL_TYPE_TIME:
	        	    return Long.toString(makeTime(bytes));
	        	default: // case COL_TYPE_UNKNOWN:
	        	    return new String();
	        }
	    }

	    /**
	     * Helper to return binary data column
	     * @return
	     */
	    public byte[] getBinaryData() {
	        return get(new Integer(SQLTraceCounter.COL_BINARYDATA));
	    }

	    /**
	     * Based on column ID, returns the type of the bytes[] associated with it.
	     * @param columnId
	     * @return
	     */
		private int getColType(int columnId) {
			switch (columnId) {
				// String columns (UNICODE)
				case 1: // TextData
				case 6: // NTUserName
				case 7: // NTDomainName
				case 8: // ClientHostName
				case 10: // ApplicationName
				case 11: // SQLSecurityLoginName
				case 26: // ServerName
				case 34: // ObjectName
				case 35: // DatabaseName
				case 36: // Filename
				case 38: // TargetRoleName
				case 39: // TargetUserName
				case 40: // DatabaseUserName
				case 42: // TargetLoginName
				    return COL_TYPE_STRING;

				// Integer columns (4 bytes)
				case 3: // DatabaseID
				case 4: // TransactionID
				case 9: // ClientProcessID
				case 12: // SPID Server
				case 18: // CPU
				case 20: // Severity
				case 21: // EventSubClass
				case 22: // ObjectID
				case 23: // Success
				case 24: // IndexID
				case 25: // IntegerData
				case 27: // EventClass
				case 28: // ObjectType
				case 29: // NestLevel
				case 30: // State
				case 31: // Error
				case 32: // Mode
				case 33: // Handle
				case 37: // ObjectOwner
					return COL_TYPE_INT;

				// Long columns (8 bytes)
				case 13: // Duration
				case 16: // Reads
				case 17: // Writes
					return COL_TYPE_LONG;

				// Time columns (16 bytes)
				case 14: // StartTime
				case 15: // EndTime
					return COL_TYPE_TIME;

				// Unknown columns
				// 2 - BinaryData
				// 5 - Reserved
				// 19 - Permissions
				// 41 - LoginSID
				// 43 - TargetLoginSID
				// 44 - ColumnPermissionsSet
				default:
				    return COL_TYPE_UNKNOWN;
			}
		}
	}

	/**
	 * Default constructor, using 1 database connection
	 * @param ctxt
	 */
	public SqlTrace(AgentExecutionContext ctxt) {
		super(ctxt, 1);
	}

	/**
	 * Called by the agent when it is first requested the list of entities
	 */
	public void createChildren() throws Throwable {
		addChildEntity(sqlTraceEventCursors = new SQLTraceEventCursors(getId(), fContext));
		addChildEntity(sqlTraceEventDatabase = new SQLTraceEventDatabase(getId(), fContext));
		addChildEntity(sqlTraceEventErrors = new SQLTraceEventErrors(getId(), fContext));
		addChildEntity(sqlTraceEventLocks = new SQLTraceEventLocks(getId(), fContext));
		addChildEntity(sqlTraceEventScans = new SQLTraceEventScans(getId(), fContext));
		//addChildEntity(sqlTraceEventSecurity = new SQLTraceEventSecurity(getId(), fContext));
		addChildEntity(sqlTraceEventSessions = new SQLTraceEventSessions(getId(), fContext));
		addChildEntity(sqlTraceEventStoredProcedures = new SQLTraceEventStoredProcedures(getId(), fContext));
		addChildEntity(sqlTraceEventTransactions = new SQLTraceEventTransactions(getId(), fContext));
		addChildEntity(sqlTraceEventTSQL = new SQLTraceEventTSQL(getId(), fContext));

		// Test connection to database. We don't want to keep the
		// connection open needlessly.
		try {
			connect();
			disconnect();
		} catch (SQLException e) {
		    // Avoid this being presented as an internal error
		    throw new RMSException("Failed to establish connection.", e);
		}
	}

    /**
     * Makes sure that the tracing thread is always running
     * @see com.ixora.rms.agents.impl.EntityForest#onBeginCycle()
     */
    protected void onBeginCycle() throws Throwable {
        // Start will do nothing if the thread is running
        start();
    }

    /**
     * Helper to enable appropriate columns and events for each child entity
     * @param entity
     * @param bOn
     */
    private void enableEntity(SQLTraceChildEntity entity, boolean bOn) {
        if (bOn) {
		    List<TracedEventColumn> listEvents = entity.getTracedEventsAndColumns();
			for (TracedEventColumn tec : listEvents) {
		        setEvent(tec.eventID, tec.columnID, true);
			}
        }
    }

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		try {
			this.fRunning = true;

		    // The context holds agent configuration and its custom data
		    AgentConfiguration agentCfg = fContext.getAgentConfiguration();
			Configuration cfg = (Configuration)fContext.getAgentConfiguration().getAgentCustomConfiguration();

			// First (and only) connection, managed by base class
			Connection conn = getConnection(0);
			statSetEvent = conn.prepareCall("{ call sp_trace_setevent(?, ?, ?, ?) }");

			// Create the trace
			CallableStatement traceCreate = conn.prepareCall("{ call sp_trace_create(?, 1)}");
			traceCreate.registerOutParameter(1, Types.INTEGER);
			traceCreate.execute();
			traceID = traceCreate.getInt(1);

			enableEntity(sqlTraceEventCursors, cfg.getBoolean(Configuration.EVENT_CURSORS));
			enableEntity(sqlTraceEventDatabase, cfg.getBoolean(Configuration.EVENT_DATABASE));
			enableEntity(sqlTraceEventErrors, cfg.getBoolean(Configuration.EVENT_ERRORS));
			enableEntity(sqlTraceEventLocks, cfg.getBoolean(Configuration.EVENT_LOCKS));
			enableEntity(sqlTraceEventScans, cfg.getBoolean(Configuration.EVENT_SCANS));
			//enableEntity(sqlTraceEventSecurity, cfg.getBoolean(Configuration.EVENT_SECURITY));
			enableEntity(sqlTraceEventSessions, cfg.getBoolean(Configuration.EVENT_SESSIONS));
			enableEntity(sqlTraceEventStoredProcedures, cfg.getBoolean(Configuration.EVENT_STORED_PROCEDURES));
			enableEntity(sqlTraceEventTransactions, cfg.getBoolean(Configuration.EVENT_TRANSACTIONS));
			enableEntity(sqlTraceEventTSQL, cfg.getBoolean(Configuration.EVENT_TSQL));

			// Start the trace
			Statement stat = conn.createStatement();
			stat.execute("{ call sp_trace_setstatus(" + traceID + ", 1) }");

			// Get the results in a continuous loop
			SQLColumnData	columnData = new SQLColumnData();
			int		lastEventId = -1;
			ResultSet rs = stat.executeQuery("{ call sp_trace_getdata(" + traceID + ", 0)}");

			while (rs.next() && isRunning()) {
				int columnid = rs.getInt("columnid");
				int length = rs.getInt("length");
				byte[] bytes = rs.getBytes("data");

/*
			//////////////////////// debug stuff
				int intVal = 0;
				long longVal = 0;
				String strVal;
				if (length == 4)
				{
					intVal = makeLoWord(bytes);
					System.out.println(columnid + " -- " + intVal + "\n");
				}
				else if (length == 8)
				{
					longVal = makeLong(bytes);
					System.out.println(columnid + " -- " + longVal + "\n");
				}
				else if (length == 16)
				{
					Timestamp tm = new Timestamp(makeTime(bytes));
					System.out.println(columnid + " -- " + tm.toString() + "\n");
				}
				else if (length != 0)
				{
					strVal = new String(bytes, "UTF-16LE");
					System.out.println(columnid + " -- " + length + " -- " + strVal + "\n");
				}
				else
					System.out.println(columnid + "\n");
			///////////////////////
*/

				// A row of data comes in a batch: special column IDs mark
				// the beginning and the end of the batch.
				if (columnid != BEGIN_TRACE_COLUMN_ID) {
					if (columnid == LAST_COLUMN_ID) { // last column: dispatch data to counters
						if (lastEventId != -1) {
							dispatchData(lastEventId, columnData);
							columnData.clear();
						}
					} else if (columnid == EVENT_CLASS_COLUMN_ID) { // new batch of data
						if (lastEventId != -1) {
							dispatchData(lastEventId, columnData);
							columnData.clear();
						}
						lastEventId = makeLoWord(bytes);
					} else {
						columnData.put(new Integer(columnid), bytes);
					}
				}
			}
			rs.close();

			// Stop and destroy the trace
			stat.execute("exec sp_trace_setstatus " + traceID + ", 0");
			stat.execute("exec sp_trace_setstatus " + traceID + ", 2");
			stat.close();
		} catch (Throwable e) {
		    // Exceptions (eg invalid JDBC class, connection lost) get reported
		    // The thread will end here, but will be resurrected on the next beginCycle.
		    fContext.error(e);
		}

		this.fRunning = false;
	}

	/**
	 * Helper to enable/disable the specified column for an event in the trace.
	 * @param event
	 * @param column
	 * @param on
	 */
	private synchronized void setEvent(int event, int column, boolean on) {
		try {
			statSetEvent.setInt(1, traceID);
			statSetEvent.setInt(2, event);
			statSetEvent.setInt(3, column);
			statSetEvent.setBoolean(4, on);
			statSetEvent.execute();
		} catch (SQLException e) {
		    // Report error but keep going
			fContext.error(e);
		}
	}


	/**
	 * Dispatches a row of trace information to the associated event handlers,
	 * passing the event and column information.
	 */
	private synchronized void dispatchData(
	        int eventId, SQLColumnData columnData) {
	    boolean childEntitiesChanged = false;
	    // Pass the event for inspection to all predefined entities
	    for (Iterator itE = fChildrenEntities.values().iterator(); itE.hasNext();) {
            SQLTraceChildEntity sqlEntity = (SQLTraceChildEntity) itE.next();
            childEntitiesChanged = sqlEntity.eventReceived(eventId, columnData) || childEntitiesChanged;
        }

	    // Signal that entity tree has changed
	    if (childEntitiesChanged) {
	        try {
	            this.fContext.childrenEntitiesChanged(extractDescriptorTree(true));
			} catch (Throwable e) {
			    // Report error but keep going
				fContext.error(e);
			}
	    }
	}

    /**
     * Start listening for SQL queries in a separate thread
     * @see	com.ixora.rms.agents.impl.RootEntity#start()
     */
    public void start() throws Throwable {
	    if (isRunning()) {
	        return; // nothing to do, already running
	    }

	    // Recreate connection if dropped
        super.start();

	    // Start listening for SQL queries in a separate thread
		new Thread(this).start();
    }

    /**
     * Disable the running query which feeds the listening thread;
     * also set an internal flag, just in case.
     * The tracing thread is still waiting for data, so we open another
     * connection and disable the trace. Then the thread will see that
     * its trace has been closed, and will exit.
     *
     * @see com.ixora.rms.agents.impl.RootEntity#stop()
     */
    public void stop() throws Throwable {
		if (!isRunning())
			return;

		// Stop and destroy the trace. NOTE: this has to be done on a
		// separate DB connection, because the first connection is blocked
		// by the trace. This command will unblock the other connection.
		Connection conn2 = null;

		try {
		    // The context holds agent configuration and its custom data
		    AgentConfiguration agentCfg = fContext.getAgentConfiguration();
			Configuration cfg = (Configuration)fContext.getAgentConfiguration().getAgentCustomConfiguration();

			conn2 = DriverManager.getConnection(cfg.getConnectionString(agentCfg.getMonitoredHost()));
			Statement stat = conn2.createStatement();

			stat.execute("{ call sp_trace_setstatus(" + traceID + ", 0)}");
			stat.execute("{ call sp_trace_setstatus(" + traceID + ", 2)}");

			stat.close();

		} catch (SQLException e) {
		    // Report error but keep going
			fContext.error(e);
		}

		if (conn2 != null) {
		    conn2.close();
		}

		// Close the managed connection
		super.stop();

		// Set the flag just in case the thread went zombie
	    this.fRunning = false;
	}

	/**
	 * @return whether tracing is started on the other thread
	 */
	private boolean isRunning() {
		return fRunning;
	}

	/**
	 * Helper to convert 4 bytes into an int, in little endian format
	 * @param bytes the array of four bytes
	 * @return the integer value
	 */
	private static final int makeInt(byte[] bytes) {
		return Utils.ubyte(bytes[0]) + (Utils.ubyte(bytes[1]) << 8) +
				(Utils.ubyte(bytes[2]) << 16) + (Utils.ubyte(bytes[3]) << 24);
	}

	/**
	 * Helper to make a word out of two bytes
	 * @param bytes
	 * @return
	 */
	private static final int makeLoWord(byte[] bytes) {
		return Utils.ubyte(bytes[0]) + (Utils.ubyte(bytes[1]) << 8);
	}

	/**
	 * Helper to make the high half of a word out of two bytes
	 * @param bytes
	 * @return
	 */
//	private static final int makeHiWord(byte[] bytes) {
//		return Utils.ubyte(bytes[2]) + (Utils.ubyte(bytes[3]) << 8);
//	}

	/**
	 * Helper to convert 8 bytes into a long, in little endian format
	 * @param bytes the array of eight bytes
	 * @return the long value
	 */
	private static final long makeLong(byte[] bytes) {
		return Utils.ubyte(bytes[0]) + (Utils.ubyte(bytes[1]) << 8) +
				(Utils.ubyte(bytes[2]) << 16) + (Utils.ubyte(bytes[3]) << 24) +
				(Utils.ubyte(bytes[4]) << 32) + (Utils.ubyte(bytes[5]) << 40) +
				(Utils.ubyte(bytes[6]) << 48) + (Utils.ubyte(bytes[7]) << 56);
	}

	/**
	 * Helper to create a long representing time in milliseconds,
	 * out of 16 bytes
	 * @param bytes
	 * @return
	 */
	private final long makeTime(byte[] bytes) {
		int year = (Utils.ubyte(bytes[0]) << 0) + (Utils.ubyte(bytes[1]) << 8);
		int month = (Utils.ubyte(bytes[2]) << 0) + (Utils.ubyte(bytes[3]) << 8);
		int dummy = (Utils.ubyte(bytes[4]) << 0) + (Utils.ubyte(bytes[5]) << 8);
		int day = (Utils.ubyte(bytes[6]) << 0) + (Utils.ubyte(bytes[7]) << 8);
		int hour = (Utils.ubyte(bytes[8]) << 0) + (Utils.ubyte(bytes[9]) << 8);
		int minute = (Utils.ubyte(bytes[10]) << 0) + (Utils.ubyte(bytes[11]) << 8);
		int second = (Utils.ubyte(bytes[12]) << 0) + (Utils.ubyte(bytes[13]) << 8);
		int milli = (Utils.ubyte(bytes[14]) << 0) + (Utils.ubyte(bytes[15]) << 8);

		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1);
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.HOUR, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, milli);

		return calendar.getTimeInMillis();
	}

	/**
     * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
     */
    protected void retrieveCounterValues() throws Throwable {
        ; // nothing, counters already have values
    }

}
