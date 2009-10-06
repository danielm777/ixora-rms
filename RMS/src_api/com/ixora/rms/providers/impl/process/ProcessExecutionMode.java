/**
 * 10-Mar-2006
 */
package com.ixora.rms.providers.impl.process;


import java.io.ObjectStreamException;

import com.ixora.common.Enum;

/**
 * @author Daniel Moraru
 */
public class ProcessExecutionMode extends Enum {
	// TODO localize
	public static final ProcessExecutionMode NORMAL = new ProcessExecutionMode(0, "Normal", "Normal");
	public static final ProcessExecutionMode TELNET = new ProcessExecutionMode(1, "Telnet", "Telnet");
	public  static final ProcessExecutionMode SSH2 = new ProcessExecutionMode(0, "SSH2", "SSH2");
	private String fStringKey;

	/**
	 * @param key
	 * @param name
	 * @param stringKey
	 */
	private ProcessExecutionMode(int key, String name, String stringKey) {
		super(key, name);
		fStringKey = stringKey;
	}

	/**
	 * @param id
	 * @return
	 */
	public static ProcessExecutionMode resolve(String id) {
		if(NORMAL.fStringKey.equals(id)) {
			return NORMAL;
		}
		if(TELNET.fStringKey.equals(id)) {
			return TELNET;
		}
		if(SSH2.fStringKey.equals(id)) {
			return SSH2;
		}
		return null;
	}

	/**
	 * @see com.ixora.common.Enum#readResolve()
	 */
	protected Object readResolve() throws ObjectStreamException {
		switch(this.key) {
		case 0:
			return NORMAL;
		case 1:
			return TELNET;
		case 2:
			return SSH2;
		}
		return null;
	}

	/**
	 * @return
	 */
	public String getStringKey() {
		return fStringKey;
	}
}
