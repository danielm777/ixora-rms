/*
 * Created on 03-Apr-2005
 */
package com.ixora.rms.agents.db2;

/**
 * DB2NativeException
 * Thrown by native code for any DB2 exception
 */
public class DB2NativeException extends DB2AgentException {
	/**
	 * Constructor
	 * @param s
	 */
	public DB2NativeException(String s) {
        super(Msg.DB2_ERROR_NATIVE_ERROR, new String[] { s });
    }
}
