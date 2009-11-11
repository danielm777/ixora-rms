/**
 * 12-Feb-2006
 */
package com.ixora.rms.agents.logfile.parsers;

import java.util.LinkedList;
import java.util.List;

import com.ixora.rms.agents.impl.logfile.LogRecord;
import com.ixora.rms.agents.logfile.LogParserScript;
import com.ixora.rms.agents.logfile.LogParserScriptContextImpl;

/**
 * Works for 8.x
 * @author Daniel Moraru
 */
public class SunAppServerScript implements LogParserScript {

	/**
	 *
	 */
	private SunAppServerScript() {
		super();
	}

	/**
	 * @see com.ixora.rms.agents.logfile.LogParserScript#parse(com.ixora.rms.agents.logfile.LogParserScriptContextImpl, java.util.List)
	 */
	public LogRecord parse(LogParserScriptContextImpl context, String[] lines) {
		java.util.regex.Pattern pattern = (java.util.regex.Pattern)context.getScriptData("pattern");
		if(pattern == null) {
			pattern = java.util.regex.Pattern.compile("\\|([^\\|]+)\\|([^\\|]+)\\|([^\\|]+)\\|([^\\|]+)\\|([^\\|]+)\\|");
			context.setScriptData("pattern", pattern);
		}
		java.text.SimpleDateFormat dateFormat = (java.text.SimpleDateFormat)context.getScriptData("dateFormat");
		if(dateFormat == null) {
			dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
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
		java.util.regex.Matcher matcher = pattern.matcher(first);
		if(!matcher.find()) {
			throw new RuntimeException("Regex match not found. Line: " + first);
		}
		int count = matcher.groupCount();
		if(count != 5) {
			throw new RuntimeException("Unexpected number of groups: " + count + ". Line: " + first);
		}
		try {
			java.util.Date date = dateFormat.parse(matcher.group(1));
			timestamp = date.getTime();
		} catch (java.text.ParseException e) {
			throw new RuntimeException(e);
		}
		severity = matcher.group(2);
		comp = matcher.group(4);
		thread = matcher.group(5);
		StringBuffer buff = new StringBuffer(first.substring(matcher.end(5) + 1));
		for(int j = 0; j < lines.length; j++) {
			if(j > 0) {
				buff.append(lines[j]);
				buff.append("\n");
			}
		}
		buff.delete(buff.length() - 4, buff.length());
		msg = buff.toString();
		return new com.ixora.rms.agents.impl.logfile.LogRecord(timestamp, severity, comp, clazz, method, line, thread, seq, msg);
	}

	public static void main(String[] args) {
		try {

			List<String> lines = new LinkedList<String>();
			lines.add("[#|2005-04-28T00:45:54.078+0100|WARNING|sun-appserver-pe8.1_02|javax.enterprise.tools.launcher|_ThreadID=10;|LAUNCHER005:Spaces in your PATH have been detected. The PATH must be consistently formated (e.g. C:\\Program Files\\Java\\jdk1.5.0\\bin; ) or the Appserver may not be able to start and/or stop.  Mixed quoted spaces in your PATH can cause problems, so the launcher will remove all double quotes before invoking the process. The most reliable solution would be to remove all spaces from your path before starting the Appservers components.  ");
			lines.add("-line2");
			lines.add("-client|#]");
			LogRecord rec = new SunAppServerScript().parse(new LogParserScriptContextImpl(), lines.toArray(new String[0]));
			System.out.println(rec.toString());
//			String regex = "\\|([^\\|]+)\\|([^\\|]+)\\|([^\\|]+)\\|([^\\|]+)\\|([^\\|]+)\\|";
//			Pattern patt = Pattern.compile(regex);
//			Matcher matcher = patt.matcher("|2005-04-28T00:45:54.078+0100|WARNING|sun-appserver-pe8.1_02|javax.enterprise.tools.launcher|_ThreadID=10;|LAUNCHER005:Spaces in your PATH have been detected. The PATH must be consistently formated (e.g. C:\\Program Files\\Java\\jdk1.5.0\\bin; ) or the Appserver may not be able to start and/or stop.  Mixed quoted spaces in your PATH can cause problems, so the launcher will remove all double quotes before invoking the process. The most reliable solution would be to remove all spaces from your path before starting the Appservers components.  |");
//			if(matcher.find()) {
//				int count = matcher.groupCount();
//				System.out.println("SunLogParser.main() - ");
//				for(int i = 1; i <= count; i++) {
//					System.out.println(matcher.group(i));
//				}
//			}
//			String timestamp = matcher.group(1);
//			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
//			Date date = format.parse(timestamp);
//			System.out.println("SunLogParser.main() - " + format.format(date));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
