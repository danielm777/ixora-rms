/**
 * 25-Dec-2005
 */
package com.ixora.rms.agents.url;

import java.net.MalformedURLException;
import java.net.URL;

import com.ixora.common.typedproperties.exception.VetoException;
import com.ixora.rms.agents.AgentCustomConfiguration;
import com.ixora.rms.agents.url.messages.Msg;

/**
 * @author Daniel Moraru
 */
public class Configuration extends AgentCustomConfiguration {
	private static final long serialVersionUID = 5001280453537520692L;
	public static final String HTTP_METHOD_GET = "GET";
	public static final String HTTP_METHOD_POST = "POST";

	public static final String URL = Msg.URL;
	public static final String HTTP_METHOD = Msg.HTTP_METHOD;
	public static final String HTTP_PARAMETERS = Msg.HTTP_PARAMETERS;

	/**
	 *
	 */
	public Configuration() {
		super();
		setProperty(URL, TYPE_STRING, true, true);
		setProperty(HTTP_METHOD, TYPE_STRING, true, true,
				new String[]{HTTP_METHOD_GET, HTTP_METHOD_POST});
		setProperty(HTTP_PARAMETERS, TYPE_XML_EXTERNALIZABLE, false, false);

		setString(Configuration.URL, "http://localhost:8080/index.php");
		setString(Configuration.HTTP_METHOD, Configuration.HTTP_METHOD_POST);
	}

	/**
	 * @see com.ixora.common.typedproperties.TypedProperties#veto()
	 */
	public void veto() throws VetoException {
		try {
			new URL(getString(URL));
		} catch (MalformedURLException e) {
			throw new VetoException("Invalid URL: " + e.getLocalizedMessage());
		}
	}
}
