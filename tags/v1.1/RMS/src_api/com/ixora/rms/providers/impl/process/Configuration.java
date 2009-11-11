/*
 * Created on 28-Dec-2004
 */
package com.ixora.rms.providers.impl.process;

import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.typedproperties.exception.VetoException;
import com.ixora.common.utils.Utils;
import com.ixora.rms.providers.ProviderCustomConfiguration;

/**
 * @author Daniel Moraru
 */
public class Configuration extends ProviderCustomConfiguration {
	private static final long serialVersionUID = 9203389310218399894L;
	public static final int PORT_TELNET = 23;
	public static final int PORT_SSH2 = 22;

	public static final String COMMAND = "providers.process.command";
    public static final String PROCESS_IS_VOLATILE = "providers.process.is_volatile";
    public static final String PROCESS_END_OF_BUFFER_MARKER = "providers.process.end_of_buffer_marker";

    public static final String EXECUTION_MODE = "providers.process.execution_mode";
    public static final String USERNAME = "providers.process.username";
    public static final String PASSWORD = "providers.process.password";
    public static final String SHELL_PROMPT = "providers.process.shell_prompt";
    public static final String USERNAME_PROMPT = "providers.process.username_prompt";
    public static final String PASSWORD_PROMPT = "providers.process.password_prompt";
    public static final String PORT = "providers.process.port";

	/**
	 * Constructor.
	 */
	public Configuration() {
		super();
		setProperty(COMMAND, TYPE_STRING, true, true);
        setProperty(PROCESS_IS_VOLATILE, TYPE_BOOLEAN, true, false);
        setProperty(PROCESS_END_OF_BUFFER_MARKER, TYPE_STRING, true, false);

        setProperty(EXECUTION_MODE, TYPE_STRING, true, false,
        		new String[]{"{agent.ExecutionMode}",
        		ProcessExecutionMode.NORMAL.getStringKey(),
        		ProcessExecutionMode.TELNET.getStringKey(),
        		ProcessExecutionMode.SSH2.getStringKey()});
        setProperty(USERNAME, TYPE_STRING, true, false);
        setProperty(PASSWORD, TYPE_STRING, true, false);
        setProperty(USERNAME_PROMPT, TYPE_STRING, true, false);
        setProperty(PASSWORD_PROMPT, TYPE_STRING, true, false);
        setProperty(SHELL_PROMPT, TYPE_STRING, true, false);
        setProperty(PORT, TYPE_STRING, true, false);

        // inherit by default from the agent
        setString(EXECUTION_MODE, "{agent.ExecutionMode}");
        setString(SHELL_PROMPT, "{agent.ShellPrompt}");
        setString(USERNAME, "{agent.Username}");
        setString(PASSWORD, "{agent.Password}");
        setString(USERNAME_PROMPT, "{agent.UsernamePrompt}");
        setString(PASSWORD_PROMPT, "{agent.PasswordPrompt}");
        setString(PORT, "{agent.Port}");
	}

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#veto()
	 */
	public void veto() throws VetoException {
		// if telnet is enabled, user, pass and shell must be filled
		String sem = getString(EXECUTION_MODE);
		if("{agent.ExecutionMode}".equals(sem)) {
			return;
		}
		ProcessExecutionMode execMode = ProcessExecutionMode.resolve(sem);
		if(execMode == null) {
			throw new VetoException("Process execution mode is missing");
		}
		boolean usetelnet = (execMode == ProcessExecutionMode.TELNET);
		boolean ssh2 = (execMode == ProcessExecutionMode.SSH2);
		if(!usetelnet && !ssh2) {
			return;
		}
		Object username = getObject(USERNAME);
		if(username == null) {
			// TODO localize
			throw new VetoException("When telnet or ssh2 is enabled, the username must be specified");
		}
		if(usetelnet) {
			Object shell = getObject(SHELL_PROMPT);
			if(shell == null) {
				// TODO localize
				throw new VetoException("When telnet enabled, the shell prompt must be specified");
			}
		}
	}

// package access
	/**
	 * Helper method used by the process provider to extract the port number.
	 * @return the port to be used for the remote connection
	 */
	int getPort() {
		if("{agent.Port}".equals(PORT)) {
			throw new AppRuntimeException("Invalid value for the port number. " +
					"Agent configuration token was not replaced.");
		}
		String port = getString(PORT);
		if(!Utils.isEmptyString(port)) {
			return Integer.parseInt(port.trim());
		}
		// if telnet is enabled, user, pass and shell must be filled
		ProcessExecutionMode execMode = ProcessExecutionMode.resolve(getString(EXECUTION_MODE));
		if(execMode == ProcessExecutionMode.TELNET) {
			return PORT_TELNET;
		} else if(execMode == ProcessExecutionMode.SSH2) {
			return PORT_SSH2;
		}
		return 0;
	}
}
