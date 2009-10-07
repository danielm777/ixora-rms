/**
 * 12-Feb-2006
 */
package com.ixora.rms.agents.logfile.parsers;

import com.ixora.rms.agents.impl.logfile.LogRecord;
import com.ixora.rms.agents.logfile.LogParserScript;
import com.ixora.rms.agents.logfile.LogParserScriptContextImpl;

/**
 * Works for 5.x and 6.x
 * @author Daniel Moraru
 */
public class WebSphereScript implements LogParserScript {

	/**
	 *
	 */
	private WebSphereScript() {
		super();
	}

	/**
	 * @see com.ixora.rms.agents.logfile.LogParserScript#parse(com.ixora.rms.agents.logfile.LogParserScriptContextImpl, java.util.List)
	 */
	public LogRecord parse(LogParserScriptContextImpl context, String[] lines) {
		//[07/03/06 22:55:02:344 GMT] 0000003d AdminServiceI W   ADMN0037W: The Perf
		java.util.regex.Pattern pattern = (java.util.regex.Pattern)context.getScriptData("pattern");
		if(pattern == null) {
			pattern = java.util.regex.Pattern.compile("[^\\s]+[\\s]+([^\\s]+)[\\s]+([^\\s]+)");
			context.setScriptData("pattern", pattern);
		}
		java.text.SimpleDateFormat dateFormat = (java.text.SimpleDateFormat)context.getScriptData("dateFormat");
		if(dateFormat == null) {
			dateFormat = new java.text.SimpleDateFormat("dd/MM/yy' 'HH:mm:ss:SSSz");
			context.setScriptData("dateFormat", dateFormat);
		}
		long timestamp = 0;
		String severity = null;
		String comp = null;
		String clazz = null;
		String method = null;
		String line = null;
		String thread = null;
		long seq = 0;
		String msg = null;
		String first = lines[0];
		if(first.charAt(0) != '[') {
			throw new RuntimeException("Invalid log record: " + first);
		}
		int idx = first.indexOf(']', 1);
		if(idx < 0) {
			throw new RuntimeException("Invalid log record: " + first);
		}
		// get timestamp
		String time = first.substring(1, idx);
		try {
			timestamp = dateFormat.parse(time).getTime();
		} catch (java.text.ParseException e) {
			throw new RuntimeException("Invalid date format: " + time);
		}
		first = first.substring(idx + 1);
		idx = first.indexOf(":");
		String toMatch;
		if(idx < 0) {
			toMatch = first;
		} else {
			toMatch = first.substring(1, idx);
		}
		java.util.regex.Matcher matcher = pattern.matcher(toMatch);
		if(!matcher.find()) {
			throw new RuntimeException("Regex match not found. Line: " + toMatch);
		}
		int count = matcher.groupCount();
		if(count != 2) {
			throw new RuntimeException("Unexpected number of groups: " + count + ". Line: " + toMatch);
		}

		severity = matcher.group(2);
		comp = matcher.group(1);
		StringBuffer buff = new StringBuffer(first.substring(idx + 2));
		for(int j = 0; j < lines.length; j++) {
			if(j > 0) {
				buff.append(lines[j]);
				buff.append("\n");
			}
		}
		msg = buff.toString();
		return new com.ixora.rms.agents.impl.logfile.LogRecord(timestamp, severity, comp, clazz, method, line, thread, seq, msg);
	}

	public static void main(String[] args) {
		try {
			//[07/03/06 22:55:02:344 GMT] 0000003d AdminServiceI W   ADMN0037W: The Perf
			String[] lines = new String[3];
//			lines[0] = "[07/03/06 22:55:02:344 GMT] 0000003d AdminServiceI W   ADMN0037W: The Perf";
			lines[0] = "[07/03/06 22:55:02:344 GMT] 0000003d AdminServiceI W The Perf";
			lines[1] = "line2 sdfsf sfdsdf";
			lines[2] = "line3 sdfsfd dsfsf";
			LogRecord rec = new WebSphereScript().parse(new LogParserScriptContextImpl(), lines);
			System.out.println("WebSphereScript.main()");

			java.util.regex.Pattern pattern = java.util.regex.Pattern
				.compile("[^\\s]+[\\s]+([^\\s]+)[\\s]+([^\\s]+)");
			String txt = "0000003d AdminServiceI W   ADMN0037W";
			System.out.println("WebSphereScript.main() - " + pattern.matcher(txt).find());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
