/*
 * Created on 01-May-2004
 */
package com.ixora.rms.agents.apache;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Daniel Moraru
 */
final class ApacheUtils {
	public static final int IDX_CHILD_SERVER_NUMBER = 0;
	public static final int IDX_CHILD_SERVER_GENERATION = 1;
	public static final int IDX_CHILD_OS_PROCESS_ID = 2;
	public static final int IDX_ACCESSES_THIS_CONNECTION = 3;
	public static final int IDX_ACCESSES_THIS_CHILD = 4;
	public static final int IDX_ACCESSES_THIS_SLOT = 5;
	public static final int IDX_MODE_OF_OPERATION = 6;
	public static final int IDX_SECONDS_SINCE_BEGINING_MOST_RECENT_REQUEST = 7;
	public static final int IDX_MILLISECONDS_REQUIRED_TO_PROCESS_MOST_RECENT_REQUEST = 8;
	public static final int IDX_KILOBYTES_TRANSFERRED_THIS_CONNECTION = 9;
	public static final int IDX_MEGABYTES_TRANSFERRED_THIS_CHILD = 10;
	public static final int IDX_TOTAL_MEGABYTES_TRANSFERRED_THIS_SLOT = 11;
	public static final int IDX_CLIENT = 12;
	public static final int IDX_VHOST = 13;
	public static final int IDX_REQUEST = 14;

	// VERSION_IBM_60
	private static final String regexExtendedStatusTableIBM60 =
		"<td><b>([0-9]+)-([0-9]+)</b></td><td>([0-9]+)</td><td>([0-9]+)/([0-9]+)/([0-9]+)</td><td>[<b>]*([^/])[</b>]*"
		+ "\\s"
		+ "</td><td>([0-9]+)</td><td>([0-9]+)</td><td>([0-9]*.[0-9]+)</td><td>([0-9]*.[0-9]+)</td><td>([0-9]*.[0-9]+)"
		+ "\\s"
		+ "</td><td>([^<]+)</td><td nowrap>([^<]+)</td><td nowrap>([^<]+)</td>";
	public static final Pattern patternExtendedStatusTableIBM60 =
		Pattern.compile(regexExtendedStatusTableIBM60);

	/**
	 *
	 */
	private ApacheUtils() {
		super();
	}

	/**
	 * Parses the extended status portion of the status page
	 * and extracts the following server tokens:
	 *
	 * Child server number
	 * Child server generation
	 * OS process ID
	 * Number of accesses this connection
	 * Number of accesses this child
	 * Number of accesses this slot
	 * Mode of operation
	 * Seconds since beginning of most recent request
	 * Milliseconds required to process most recent request
	 * Kilobytes transferred this connection
	 * Megabytes transferred this child
	 * Total megabytes transferred this slot
	 * Client
	 * VHost
	 * Request
	 *
	 * @param content
	 * @return
	 */
	public static String[][] parseExtendedStatus_1_3_x(String content) {
		List<String[]> ret = new LinkedList<String[]>();
		int idx = content.indexOf("Request</");
		int idxEnd = content.indexOf("</tab", idx);
		int idx2;
		String tmp;
		while((idx = content.indexOf("<tr", idx)) > 0 && idx < idxEnd) {
			String[] record = new String[15];
			// Child server number
			idx = content.indexOf('b', idx);
			idx2 = content.indexOf('-', idx);
			tmp = content.substring(idx + 2, idx2);
			record[0] = trim(tmp);

			// Child server generation
			idx = content.indexOf('<', idx2);
			tmp = content.substring(idx2 + 1, idx);
			record[1] = trim(tmp);

			// OS process ID
			idx = content.indexOf('d', idx);
			idx2 = content.indexOf('<', idx);
			tmp = content.substring(idx + 2, idx2);
			record[2] = trim(tmp);

			// Number of accesses this connection
			idx = content.indexOf('d', idx2);
			idx2 = content.indexOf('/', idx);
			tmp = content.substring(idx + 2, idx2);
			record[3] = trim(tmp);

			// Number of accesses this child
			idx = idx2 + 1;
			idx2 = content.indexOf('/', idx);
			tmp = content.substring(idx, idx2);
			record[4] = trim(tmp);

			// Number of accesses this slot
			idx = idx2 + 1;
			idx2 = content.indexOf('<', idx);
			tmp = content.substring(idx, idx2);
			record[5] = trim(tmp);

			// Mode of operation
			idx = content.indexOf('>', idx2);
			idx2 = content.indexOf("<t", idx);
			if(content.charAt(idx + 2) == 'b') {
				idx += 3;
			}
			tmp = content.substring(idx + 1, idx2);
			record[6] = trim(tmp);

			// Seconds since beginning of most recent request
			idx = content.indexOf('d', idx2);
			idx2 = content.indexOf('<', idx);
			tmp = content.substring(idx + 2, idx2);
			record[7] = trim(tmp);

			// Milliseconds required to process most recent request
			idx = content.indexOf('d', idx2);
			idx2 = content.indexOf('<', idx);
			tmp = content.substring(idx + 2, idx2);
			record[8] = trim(tmp);

			// Kilobytes transferred this connection
			idx = content.indexOf('d', idx2);
			idx2 = content.indexOf('<', idx);
			tmp = content.substring(idx + 2, idx2);
			record[9] = trim(tmp);

			// Megabytes transferred this child
			idx = content.indexOf('d', idx2);
			idx2 = content.indexOf('<', idx);
			tmp = content.substring(idx + 2, idx2);
			record[10] = trim(tmp);

			// Total megabytes transferred this slot
			idx = content.indexOf('d', idx2);
			idx2 = content.indexOf('<', idx);
			tmp = content.substring(idx + 2, idx2);
			record[11] = trim(tmp);

			// Client
			idx = content.indexOf('d', idx2);
			idx2 = content.indexOf('<', idx);
			tmp = content.substring(idx + 2, idx2);
			record[12] = trim(tmp);

			// VHost
			idx = content.indexOf('>', idx2);
			idx2 = content.indexOf('<', idx);
			tmp = content.substring(idx + 1, idx2);
			record[13] = trim(tmp);

			// Request
			idx = content.indexOf('>', idx2);
			idx2 = content.indexOf('<', idx);
			tmp = content.substring(idx + 1, idx2);
			record[14] = trim(tmp);

			//System.out.println(trim(tmp));
			ret.add(record);
		}
		return ret.toArray(new String[ret.size()][]);
	}

	/**
	 * Parses the extended status portion of the status page
	 * and extracts the following server tokens:
	 *
	 * Child server number
	 * Child server generation
	 * OS process ID
	 * Number of accesses this connection
	 * Number of accesses this child
	 * Number of accesses this slot
	 * Mode of operation
	 * Seconds since beginning of most recent request
	 * Milliseconds required to process most recent request
	 * Kilobytes transferred this connection
	 * Megabytes transferred this child
	 * Total megabytes transferred this slot
	 * Client
	 * VHost
	 * Request
	 *
	 * @param content
	 * @return null if parsing failed
	 */
	public static String[][] parseExtendedStatus_ibm_6_0(String content) {
		List<String[]> ret = new LinkedList<String[]>();
		int idx = content.indexOf("Request</");
		int idxEnd = content.indexOf("</tab", idx);
		String tmp;
		while((idx = content.indexOf("<tr", idx)) > 0 && idx < idxEnd) {
			String[] record = new String[15];
			// get string to apply regex to
			int idx2 = content.indexOf("</tr", idx) + 5;
			String row = content.substring(idx, idx2);
			idx = idx2;
			Matcher m = patternExtendedStatusTableIBM60.matcher(row);
			if(!m.find()) {
				return null;
			}
			int groups = m.groupCount();
			if(groups != 15) {
				return null;
			}
			for(int i = 0; i < record.length; i++) {
				record[i] = m.group(i + 1);
			}
			ret.add(record);
		}
		return ret.toArray(new String[ret.size()][]);
	}

	/**
	 * Checks whether or not extended status info is available
	 * in the given text.
	 * @param text
	 * @return
	 */
	public static boolean isExtendedStatusOn(int version, String text) {
		if(version == ApacheConstants.VERSION_ORACLE_1_3_X
				|| version == ApacheConstants.VERSION_IBM_1_3_X
				|| version == ApacheConstants.VERSION_IBM_2_0_X
				|| version == ApacheConstants.VERSION_ORACLE_2_0_X
				|| version == ApacheConstants.VERSION_IBM_6_0_X) {
			if(text.indexOf("Total accesses") > 0) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes spaces and new line chars from the begining and end
	 * of the given string
	 * @param s
	 * @return
	 */
	private static String trim(String s) {
		int len = s.length();
		int st = 0;
		char ch;
		while((st < len)) {
			ch = s.charAt(st);
			if(ch != ' ' && ch != '\n' && ch != '\r' ) {
				break;
			}
		    st++;
		}
		while((st < len)) {
			ch = s.charAt(len - 1);
			if(ch != ' ' && ch != '\n' && ch != '\r' ) {
				break;
			}
		    len--;
		}
		return ((st > 0) || (len < s.length())) ? s.substring(st, len) : s;
	}
}
