/**
 * 04-Feb-2006
 */
package com.ixora.rms.ui.exporter;

import java.io.File;

import com.ixora.common.xml.XMLAttributeInt;
import com.ixora.common.xml.XMLAttributeString;
import com.ixora.common.xml.XMLTag;

/**
 * @author Daniel Moraru
 */
public class HTMLGeneratorSettings extends XMLTag {
	private XMLAttributeInt fRefreshInterval = new XMLAttributeInt("refresh", true);
	private XMLAttributeString fSiteRoot = new XMLAttributeString("site", true);

	/**
	 * XML constructor.
	 */
	public HTMLGeneratorSettings() {
	}

	/**
	 *
	 */
	public HTMLGeneratorSettings(File root, int refresh) {
		super();
		fRefreshInterval.setValue(refresh);
		fSiteRoot.setValue(root.getAbsolutePath());
	}

	/**
	 * @return
	 */
	public int getRefreshInterval() {
		return fRefreshInterval.getInteger().intValue();
	}

	/**
	 * @return
	 */
	public File getSiteRoot() {
		return new File(fSiteRoot.getValue());
	}

	/**
	 * @see com.ixora.common.xml.XMLTag#getTagName()
	 */
	public String getTagName() {
		return "htmlGeneratorSettings";
	}
}
