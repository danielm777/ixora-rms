/*
 * Created on 05-Dec-2004
 */
package com.ixora.rms.agents.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.rms.agents.AgentConfiguration;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.impl.RootEntity;

/**
 * SQLBasedEntity
 */
public class SQLBasedEntity extends RootEntity {
	private static final long serialVersionUID = -9071259766670714662L;
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(SQLBasedEntity.class);
	/** List of database connections */
	private List<Connection> connections = new LinkedList<Connection>();
	private int  numConnections = 0;

	/**
	 * Creates an SQL root entity which uses as many
	 * database connections as specified.
	 */
	public SQLBasedEntity(AgentExecutionContext ctxt, int conns) {
		super(ctxt);
		this.numConnections = conns;
	}

	/** Returns the specified connection */
	public Connection getConnection(int index) {
	    return connections.get(index);
	}

	/**
	 * Call start to force a refresh of the entity tree
     * @see com.ixora.rms.agents.impl.RootEntity#start()
	 */
	public void start() throws Throwable {
	    if (!isConnected()) {
	        connect();
	    }
	}

    /**
     * Disconnect, ignore errors
     * @see com.ixora.rms.agents.impl.RootEntity#stop()
     */
    public void stop() throws Throwable {
	    try {
	        if (isConnected()) {
	            disconnect();
	        }
        } catch (Throwable e) {
        	logger.error("Exception thrown while disconnecting: " + e.getMessage());
        }
    }

    /**
     * Creates all database connections used for data collection
     */
    protected void connect() throws Throwable {
	    // The context holds agent configuration and its custom data
	    AgentConfiguration agentCfg = fContext.getAgentConfiguration();
	    SQLODBCConfiguration cfg = (SQLODBCConfiguration)
	    	fContext.getAgentConfiguration().getAgentCustomConfiguration();

		// Create more connections to the same database
		for (int i = 0; i < numConnections; i++) {
		    Connection conn = DriverManager.getConnection(
		            cfg.getConnectionString(agentCfg.getMonitoredHost()));
		    this.connections.add(conn);
        }
    }

    /**
     * @return true if all connections are open
     */
    protected boolean isConnected() {
        if (connections.size() == 0)
            return false;

        boolean bConnected = true;
        try {
            for (Iterator<Connection> it = connections.iterator(); it.hasNext();) {
                Connection c = it.next();
                if (c.isClosed()) {
                    bConnected = false;
                    break;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return bConnected;
    }

    /**
     * Closes and discards all database connections
     */
    protected void disconnect() throws Throwable {
        for (Iterator<Connection> it = connections.iterator(); it.hasNext();) {
            Connection c = it.next();
            if (!c.isClosed())
                c.close();
        }
        connections.clear();
    }

    /**
     * Ensures all database connections are present at the beginning of the cycle
     * @see com.ixora.rms.agents.impl.EntityForest#beginCycle()
     */
    public void beginCycle() throws Throwable {
		// Connect if a connection was not already present
		if (!isConnected()) {
			connect();
		}

		// Refresh entities and their counters
		updateChildrenEntities(false);
    }

    /**
     * @see com.ixora.rms.agents.impl.EntityForest#endCycle()
     */
    public void endCycle() throws Throwable {
    }

}
