/*
 * Created on 25-Sep-2004
 */
package com.ixora.jobs.impl.commandtelnet;

import com.ixora.jobs.JobData;
import com.ixora.common.xml.XMLTag;
import com.ixora.common.xml.XMLText;


/**
 * @author Daniel Moraru
 */
public class TelnetCommandJobData extends XMLTag implements JobData {
	private static final long serialVersionUID = 3779008024030837290L;
	private XMLText command = new XMLText("command", true);
    private XMLText username = new XMLText("username", true);
    private XMLText password = new XMLText("password", true);
    private XMLText usernamePrompt = new XMLText("usernamePrompt", true);
    private XMLText passwordPrompt = new XMLText("passwordPrompt", true);
    private XMLText shellPrompt = new XMLText("shellPrompt", true);

    /**
     * Constructor to support xml.
     */
    public TelnetCommandJobData() {
        super();
    }

    public TelnetCommandJobData(
    		String command,
    		String username,
    		String password,
    		String usernamePrompt,
    		String passwordPrompt,
    		String shellPrompt) {
        super();
        this.command.setValue(command);
        this.username.setValue(username);
        this.password.setValue(password);
        this.usernamePrompt.setValue(usernamePrompt);
        this.passwordPrompt.setValue(passwordPrompt);
        this.shellPrompt.setValue(shellPrompt);
    }

    public String getCommand() {
        return command.getValue();
    }

	public String getPassword() {
		return password.getValue();
	}

	public String getShellPrompt() {
		return shellPrompt.getValue();
	}

	public String getUsername() {
		return username.getValue();
	}

	public String getUsernamePrompt() {
		return usernamePrompt.getValue();
	}

	public String getPasswordPrompt() {
		return passwordPrompt.getValue();
	}

	/**
	 * @see com.ixora.common.xml.XMLTag#getTagName()
	 */
	public String getTagName() {
		return "jobData";
	}

	/**
	 * @see com.ixora.jobs.JobData#runOnHost()
	 */
	public boolean runOnHost() {
		return false;
	}
}
