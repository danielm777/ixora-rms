/**
 * 12-Feb-2006
 */
package com.ixora.rms.agents.logfile.parsers;

import com.ixora.rms.agents.impl.logfile.LogRecord;
import com.ixora.rms.agents.logfile.LogParserScript;
import com.ixora.rms.agents.logfile.LogParserScriptContextImpl;

/**
 * Works for 8.x and 9.x
 * @author Daniel Moraru
 */
public class WeblogicScript implements LogParserScript {

	/**
	 *
	 */
	private WeblogicScript() {
		super();
	}

	/**
	 * @see com.ixora.rms.agents.logfile.LogParserScript#parse(com.ixora.rms.agents.logfile.LogParserScriptContextImpl, java.util.List)
	 */
	public LogRecord parse(LogParserScriptContextImpl context, String[] lines) {
		//####<09-Mar-2006 23:53:59 o'clock GMT> <Notice> <Security> <panda> <examplesServer> <main> <<WLS Kernel>> <> <BEA-090170> <Loading the private key stored under the alias DemoIdentity from the jks keystore file C:\bea80\WEBLOG~1\server\lib\DemoIdentity.jks.>
		java.util.regex.Pattern pattern = (java.util.regex.Pattern)context.getScriptData("pattern");
		if(pattern == null) {
			pattern = java.util.regex.Pattern.compile("####<([^>]+)>[\\s]+<([^>]+)>[\\s]+<([^>]+)>[\\s]+<([^>]+)>[\\s]+<([^>]+)>[\\s]+<([^>]+)>");
			context.setScriptData("pattern", pattern);
		}
		java.text.SimpleDateFormat dateFormat = (java.text.SimpleDateFormat)context.getScriptData("dateFormat");
		if(dateFormat == null) {
			dateFormat = new java.text.SimpleDateFormat("dd-MMM-yyyy HH:mm:ss 'o''clock' z");
			context.setScriptData("dateFormat", dateFormat);
		}
		String first = lines[0];
		java.util.regex.Matcher matcher = pattern.matcher(first);
		if(!matcher.find()) {
			throw new RuntimeException("Cannot match regex: " + pattern + ". Line: " + first);
		}
		int count = matcher.groupCount();
		if(count != 6) {
			throw new RuntimeException("Unexpected number of regex groups: " + count + ". Line: " + first);
		}

		long timestamp;
		String severity = matcher.group(2);
		String comp = matcher.group(3) + "/" + matcher.group(4) + "/" + matcher.group(5);
		String clazz = null;
		String method = null;
		String line = null;
		String thread = matcher.group(6);
		long seq = 0;
		String msg = null;

		// get timestamp
		String time = matcher.group(1);
		try {
			timestamp = dateFormat.parse(time).getTime();
		} catch (java.text.ParseException e) {
			throw new RuntimeException("Invalid date format: " + time);
		}
		int idx = first.lastIndexOf("<");
		StringBuffer buff = new StringBuffer(first.substring(idx + 1));
		for(int j = 0; j < lines.length; j++) {
			if(j > 0) {
				buff.append("\n");
				buff.append(lines[j]);
			}
		}
		// remove last ">"
		idx = buff.lastIndexOf(">");
		msg = buff.delete(idx, buff.length() - 1).toString();
		return new com.ixora.rms.agents.impl.logfile.LogRecord(timestamp, severity, comp, clazz, method, line, thread, seq, msg);
	}

/*	public static void main(String[] args) {
		try {
			//####<09-Mar-2006 23:53:59 o'clock GMT> <Notice> <Security> <panda> <examplesServer> <main> <<WLS Kernel>> <> <BEA-090170> <Loading the private key stored under the alias DemoIdentity from the jks keystore file C:\bea80\WEBLOG~1\server\lib\DemoIdentity.jks.>
			String[] lines = new String[2];
			lines[0] = "####<09-Mar-2006 23:53:59 o'clock GMT> <Notice> <Security> <panda> <examplesServer> <main> <<WLS Kernel>> <> <BEA-090170> <Loading the private key stored under the alias ";
			lines[1] = "DemoIdentity from the jks keystore file C:\\bea80\\WEBLOG~1\\server\\lib\\DemoIdentity.jks.> ";
			LogRecord rec = new WeblogicScript().parse(new LogParserScriptContextImpl(), lines);

			//<TIME> <SEVERITY> <COMP1> <COMP2> <COMP3> <COMP4> <<IGNORE>> <IGNORE> <IGNORE>
//			java.util.regex.Pattern pattern = java.util.regex.Pattern
//				.compile("####<([^>]+)>[\\s]+<([^>]+)>[\\s]+<([^>]+)>[\\s]+<([^>]+)>[\\s]+<([^>]+)>[\\s]+<([^>]+)>");
//			DateFormat df = new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss 'o''clock' z");
//			System.out.println("WeblogicScript.main() - " + df.format(new Date()));
			int debug = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
*/}
