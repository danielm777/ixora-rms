/**
 * 30-Jul-2005
 */
package com.ixora.rms.reactions;

import java.util.List;

/**
 * @author Daniel Moraru
 */
public final class ReactionDeliveryInfoEmail extends ReactionDeliveryInfo {
	private static final long serialVersionUID = -4560711991028751605L;
	private String fServer;
	private int fPort;
	private String fFrom;
	private List<String> fTo;
	private String fSubject;

	/**
	 * @param server
	 * @param port
	 * @param from
	 * @param to
	 * @param subject
	 * @param message
	 */
	public ReactionDeliveryInfoEmail(
			String server, int port, String from, List<String> to, String subject, String message) {
		super(message);
		fServer = server;
		fPort = port;
		fFrom = from;
		fTo = to;
		fSubject = subject;
	}
	/**
	 * @return from.
	 */
	public String getFrom() {
		return fFrom;
	}
	/**
	 * @return port.
	 */
	public int getPort() {
		return fPort;
	}
	/**
	 * @return server.
	 */
	public String getServer() {
		return fServer;
	}
	/**
	 * @return subject.
	 */
	public String getSubject() {
		return fSubject;
	}
	/**
	 * @return to.
	 */
	public List<String> getTo() {
		return fTo;
	}
}
