/*
 * Created on 12-Jan-2005
 */
package com.ixora.rms.agents.messages;

/**
 * @author Daniel Moraru
 */
public interface Msg {
//	 configuration entries for the generic config
	public static final String PARAMETER1
		= "Parameter1";
	public static final String PARAMETER2
		= "Parameter2";
	public static final String PARAMETER3
		= "Parameter3";
	public static final String PARAMETER4
		= "Parameter4";
	public static final String PARAMETER5
		= "Parameter5";
	public static final String PARAMETER6
		= "Parameter6";
	public static final String PARAMETER7
		= "Parameter7";
	public static final String PARAMETER8
		= "Parameter8";
	public static final String PARAMETER9
		= "Parameter9";

// configuration entries for process based agents
    public static final String EXECUTION_MODE =
    	"ExecutionMode";
	public static final String PORT =
		"Port";
    public static final String USERNAME =
    	"Username";
    public static final String PASSWORD =
    	"Password";
    public static final String USERNAME_PROMPT =
    	"UsernamePrompt";
    public static final String PASSWORD_PROMPT =
    	"PasswordPrompt";
    public static final String SHELL_PROPMT =
    	"ShellPrompt";

// configuration entries for the sql config
	public static final String SQL_DATABASE_NAME
		= "Database";
	public static final String SQL_DATABASE_PORT
		= "Port";
	public static final String SQL_USERNAME
		= "Username";
	public static final String SQL_PASSWORD
		= "Password";
	public static final String SQL_JDBC_CLASS
		= "JDBCDriverClass";
	public static final String SQL_CLASSPATH
		= "Classpath";
}
