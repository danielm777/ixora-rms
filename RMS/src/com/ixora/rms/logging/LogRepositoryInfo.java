package com.ixora.rms.logging;

import java.io.Serializable;

import com.ixora.common.utils.Utils;

/**
 * LogRepositoryInfo.
 * @author Daniel Moraru
 */
public final class LogRepositoryInfo implements Serializable {
	private static final long serialVersionUID = 1805612046621913256L;
	public enum Type {
		xml, db
	}

	/**
	 * Repository name.
	 */
	private String fRepositoryName;
	/**
	 * Repository type.
	 */
	private Type fRepositoryType;

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
		if(tmp.equalsIgnoreCase(Type.xml.name())) {
		    fRepositoryType = Type.xml;
		} else if(tmp.equalsIgnoreCase(Type.db.name())) {
		    fRepositoryType = Type.db;
		} else {
		    throw new IllegalArgumentException("invalid string " + info);
		}
		this.fRepositoryName = info.substring(idx + 1);
		validate();
	}

	/**
	 * Constructor for LogRepositoryInfo.
	 * @param repositoryType the type of repository to use
	 * @param repositoryName the name of the log repository
	 */
	public LogRepositoryInfo(
			Type repositoryType,
			String repositoryName) {
		super();
		this.fRepositoryName = repositoryName;
		this.fRepositoryType = repositoryType;
		validate();
	}

	private void validate() {
		if(Utils.isEmptyString(fRepositoryName)) {
			throw new IllegalArgumentException("null repository name");
		}
	}

	/**
	 * @return the name of the repository
	 */
	public String getRepositoryName() {
		return fRepositoryName;
	}

	/**
	 * @return Returns the type of log repository to use.
	 */
	public Type getRepositoryType() {
		return fRepositoryType;
	}
    /**
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return fRepositoryType.name() + "|" + fRepositoryName;
    }
}
