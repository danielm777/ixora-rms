/*
 * Created on 03-Apr-2005
 */
package com.ixora.db2;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 * @author Daniel Moraru
 */
public class Test {

	public static void main(String[] args) {
		try {
			java.io.PrintWriter w =
		        new java.io.PrintWriter
		           (new java.io.OutputStreamWriter(System.out));
		      DriverManager.setLogWriter(w);

		    Class.forName("COM.ibm.db2.jdbc.app.DB2Driver");
			Connection conn = DriverManager.getConnection("jdbc:db2:curam",
					"db2admin", "password");
			System.err.println("Conn: " + conn);
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
