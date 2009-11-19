/**
 * 07-Sep-2005
 */
package com.ixora.rms.agents.utils;

import java.util.StringTokenizer;

import com.ixora.common.utils.Utils;

/**
 * @author Daniel Moraru
 */
public class ConfigurationWithClasspath extends AuthenticationConfiguration {
	private static final long serialVersionUID = 1366253808394881425L;
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
        if(Utils.isEmptyString(tmp)) {
            return null;
        }
        StringTokenizer tok = new StringTokenizer(tmp, ",");
        StringBuffer buff = new StringBuffer();
        while(tok.hasMoreTokens()) {
        	Utils.stitchPathFragments(buff, home, tok.nextToken().trim());
            if(tok.hasMoreTokens()) {
                buff.append(',');
            }
        }
        return buff.toString();
    }
}
