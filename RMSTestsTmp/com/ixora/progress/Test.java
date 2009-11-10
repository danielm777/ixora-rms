/**
 * 26-Mar-2006
 */
package com.ixora.progress;


/**
 * @author Daniel Moraru
 */
public class Test {

	/**
	 *
	 */
	public Test() {
		super();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			Class.forName("com.progress.sql.jdbc.JdbcProgressDriver");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

}
