/**
 * 07-Sep-2005
 */
package com.ixora.rms.agents.utils;

import java.util.StringTokenizer;

/**
 * @author Daniel Moraru
 */
public class ConfigurationWithClasspath extends AuthenticationConfiguration {
	public static final String ROOT_FOLDER = Msg.ROOT_FOLDER;
	public static final String CLASSPATH = Msg.CLASSPATH;

	/**
	 *
	 */
	public ConfigurationWithClasspath() {
		super();
        setProperty(ROOT_FOLDER, TYPE_STRING, true, false);
		setProperty(CLASSPATH, TYPE_STRING, true, false);
	}

    /**
     * @see com.ixora.rms.agents.AgentCustomConfiguration#getClasspath()
     */
    public String getClasspath() {
        String home = getString(Msg.ROOT_FOLDER);
        String tmp = getString(Msg.CLASSPATH);
        if(tmp == null) {
            return null;
        }
        StringTokenizer tok = new StringTokenizer(tmp, ",");
        StringBuffer buff = new StringBuffer();
        while(tok.hasMoreTokens()) {
            buff.append(home);
            buff.append(tok.nextToken());
            if(tok.hasMoreTokens()) {
                buff.append(',');
            }
        }
        return buff.toString();
    }
}
