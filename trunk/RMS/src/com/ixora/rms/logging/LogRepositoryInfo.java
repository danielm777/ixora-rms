package com.ixora.rms.logging;

import java.io.Serializable;

/**
 * LogRepositoryInfo.
 * @author Daniel Moraru
 */
public final class LogRepositoryInfo implements Serializable {
	public static final String TYPE_XML = "xml";
	public static final String TYPE_DATABASE = "db";

	/**
	 * Repository name.
	 */
	private String repositoryName;
	/**
	 * Repository type.
	 */
	private String repositoryType;

	/**
	 * Constructor for LogRepositoryInfo.
	 * @param info the text form of the log repository info
	 * as returned by <code>toString()</code>
	 */
	public LogRepositoryInfo(String info) {
		super();
		int idx = info.indexOf("|");
		if(idx < 0) {
		    throw new IllegalArgumentException("invalid string " + info);
		}
		String tmp = info.substring(0, idx);
		if(tmp.equalsIgnoreCase(TYPE_XML)) {
		    repositoryType = TYPE_XML;
		} else if(tmp.equalsIgnoreCase(TYPE_DATABASE)) {
		    repositoryType = TYPE_DATABASE;
		} else {
		    throw new IllegalArgumentException("invalid string " + info);
		}
		this.repositoryName = info.substring(idx + 1);
	}

	/**
	 * Constructor for LogRepositoryInfo.
	 * @param repositoryType the type of repository to use
	 * @param repositoryName the name of the log repository
	 */
	public LogRepositoryInfo(
			String repositoryType,
			String repositoryName) {
		super();
		this.repositoryName = repositoryName;
		this.repositoryType = repositoryType;
	}

	/**
	 * @return the name of the repository
	 */
	public String getRepositoryName() {
		return repositoryName;
	}

	/**
	 * @return Returns the type of log repository to use.
	 */
	public String getRepositoryType() {
		return repositoryType;
	}
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return repositoryType + "|" + repositoryName;
    }
}
