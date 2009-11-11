package com.ixora.rms.dataengine;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.dataengine.definitions.StyleDef;


/**
 * HintManager
 * @author Cristian Costache
 * @author Daniel Moraru
 */
public final class HintManager {
	/** Single instance of this class */
	private static final HintManager instance = new HintManager();
	
	public static HintManager instance() { return instance; }

	/** Repository: Styles, keyed by ID */
	protected Map<String, Style> fStyles = new HashMap<String, Style>();

	/** Loads the XML definitions for all counters/values etc */
	public HintManager() {
		File file = new File(Utils.getPath("config/dataengine/style_repository.xml"));
		Document doc = null;
		try {
			doc = XMLUtils.read(
					new BufferedInputStream(
							new FileInputStream(file)));
			Node firstChild = doc.getDocumentElement();

			// Look for style definitions
			List<Node> nodesStyles = XMLUtils.findChildren(firstChild, "style");
			for (Iterator<Node> it = nodesStyles.iterator(); it.hasNext();) {
				// Load style definition from XML
				StyleDef sd = new StyleDef();
				sd.fromXML(it.next());

				// Add style to repository
				Style style = new Style(sd);
				fStyles.put(style.getID(), style);
			}
		}
		catch (XMLException e)	{}
		catch (FileNotFoundException e){}
	}

	/**
	 * Fully resolves the style by recursively merging with other
	 * styles in repository
	 * @param s style to resolve
	 */
	public void resolveStyle(Style s) {
		String baseStyle = s.getStyle();
		if (baseStyle != null) {
			// The name of a style to inherit is specified: look for it
			Style bs = fStyles.get(baseStyle);
			if(bs != null) {
				// First resolve the base style recursively
				resolveStyle(bs);

				// Now merge results into output style
				s.merge(bs);

				// And we're done
				return;
			}
		}
	}
}
