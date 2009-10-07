/**
 * 12-Feb-2006
 */
package com.ixora.rms.agents.logfile.parsers;

import com.ixora.rms.agents.impl.logfile.LogRecord;
import com.ixora.rms.agents.logfile.LogParserScript;
import com.ixora.rms.agents.logfile.LogParserScriptContextImpl;

/**
 * Works for 4.x
 * @author Daniel Moraru
 */
public class JBossScript implements LogParserScript {

	/**
	 *
	 */
	private JBossScript() {
		super();
	}

	/**
	 * @see com.ixora.rms.agents.logfile.LogParserScript#parse(com.ixora.rms.agents.logfile.LogParserScriptContextImpl, java.util.List)
	 */
	public LogRecord parse(LogParserScriptContextImpl context, String[] lines) {
		//2006-03-05 00:43:26,921 DEBUG [org.jboss.deployment.scanner.URLDeploymentScanner] Watch URL for: file:/C:/jboss-4.0.3RC1/server/default/deploy/ejb3-interceptors-aop.xml -> file:/C:/jboss-4.0.3RC1/server/default/deploy/ejb3-interceptors-aop.xml
		java.util.regex.Pattern pattern = (java.util.regex.Pattern)context.getScriptData("pattern");
		if(pattern == null) {
			pattern = java.util.regex.Pattern.compile("([^-]+-[^-]+-[^-]+[\\s]+[^:]+:[^:]+:[^,]+,[^\\s]+)[\\s]+([^\\s]+)[\\s]+\\[([^\\]]+)\\]");
			context.setScriptData("pattern", pattern);
		}
		java.text.SimpleDateFormat dateFormat = (java.text.SimpleDateFormat)context.getScriptData("dateFormat");
		if(dateFormat == null) {
			dateFormat = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss,SSS");
			context.setScriptData("dateFormat", dateFormat);
		}
		String first = lines[0];
		java.util.regex.Matcher matcher = pattern.matcher(first);
		if(!matcher.find()) {
			throw new RuntimeException("Cannot match regex: " + pattern + ". Line: " + first);
		}
		int count = matcher.groupCount();
		if(count != 3) {
			throw new RuntimeException("Unexpected number of regex groups: " + count + ". Line: " + first);
		}

		long timestamp;
		String severity = matcher.group(2);
		String comp = null;
		String clazz = matcher.group(3);
		String method = null;
		String line = null;
		String thread = null;
		long seq = 0;
		String msg = null;

		// get timestamp
		String time = matcher.group(1);
		try {
			timestamp = dateFormat.parse(time).getTime();
		} catch (java.text.ParseException e) {
			throw new RuntimeException("Invalid date format: " + time);
		}
		int idx = first.indexOf("]");
		StringBuffer buff = new StringBuffer(first.substring(idx + 1));
		for(int j = 0; j < lines.length; j++) {
			if(j > 0) {
				buff.append("\n");
				buff.append(lines[j]);
			}
		}
		msg = buff.toString();
		return new com.ixora.rms.agents.impl.logfile.LogRecord(timestamp, severity, comp, clazz, method, line, thread, seq, msg);
	}

	public static void main(String[] args) {
		try {
			//2006-03-05 00:43:26,921 DEBUG [org.jboss.deployment.scanner.URLDeploymentScanner] Watch URL for: file:/C:/jboss-4.0.3RC1/server/default/deploy/ejb3-interceptors-aop.xml -> file:/C:/jboss-4.0.3RC1/server/default/deploy/ejb3-interceptors-aop.xml
			String[] lines = new String[2];
			lines[0] = "2006-03-05 00:43:26,921 DEBUG [org.jboss.deployment.scanner.URLDeploymentScanner] Watch URL for: file:/C:/jboss-4.0.3RC1/server/default/deploy/ejb3-interceptors-aop.xml -> file:/C:/jboss-4.0.3RC1/server/default/deploy/ejb3-interceptors-aop.xml";
			lines[1] = "DemoIdentity from the jks keystore file C:\\bea80\\WEBLOG~1\\server\\lib\\DemoIdentity.jks.> ";
			LogRecord rec = new JBossScript().parse(new LogParserScriptContextImpl(), lines);

			int debug = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
