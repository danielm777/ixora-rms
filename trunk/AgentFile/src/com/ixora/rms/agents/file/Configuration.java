/**
 * 25-Dec-2005
 */
package com.ixora.rms.agents.file;

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.ixora.common.typedproperties.exception.VetoException;
import com.ixora.rms.agents.AgentCustomConfiguration;
import com.ixora.rms.agents.file.messages.Msg;

/**
 * @author Daniel Moraru
 */
public class Configuration extends AgentCustomConfiguration {
	private static final long serialVersionUID = -4374531369480868776L;
	public static final String ROOT_FOLDER = Msg.ROOT_FOLDER;
	public static final String FILE_NAME_PATTERN = Msg.FILE_NAME_PATTERN;
	public static final String IGNORE_FOLDERS = Msg.IGNORE_FOLDERS;

	/**
	 *
	 */
	public Configuration() {
		super();
		setProperty(ROOT_FOLDER, TYPE_STRING, true);
		setString(ROOT_FOLDER, "C:/App/Logs/");

		setProperty(FILE_NAME_PATTERN, TYPE_STRING, true);
		setString(FILE_NAME_PATTERN, "[^\\.].log");

		setProperty(IGNORE_FOLDERS, TYPE_BOOLEAN, true);
		setBoolean(IGNORE_FOLDERS, false);
	}

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#veto()
	 */
	public void veto() throws VetoException {
		String fileName = getString(FILE_NAME_PATTERN);
		// check if valid pattern
		try {
			Pattern.compile(fileName);
		} catch(PatternSyntaxException e) {
			// check for this common mistake
			String hint = null;
			if(fileName.indexOf('*') >= 0 && fileName.indexOf('.') < 0) {
				hint = "\nInstead of * use .*";
			}
			throw new VetoException("Error in file pattern regular expression: "
					+ e.getMessage() + (hint != null ? ". " + hint : ""));
		}
	}
}
