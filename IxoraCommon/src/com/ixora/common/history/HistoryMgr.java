/**
 * 01-Feb-2006
 */
package com.ixora.common.history;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLExternalizable;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLNodeMissing;

/**
 * @author Daniel Moraru
 */
public class HistoryMgr {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(HistoryMgr.class);
	/** History mapped by id */
	private static Map<String, HistoryGroup> fHistory;

	static {
		fHistory = new HashMap<String, HistoryGroup>();
	}

	/**
	 *
	 */
	private HistoryMgr() {
		super();
	}

	/**
	 * Adds an item to the history with the given id
	 * @param id the id of the history
	 * @param props
	 */
	public synchronized static void add(String id, XMLExternalizable props) {
		HistoryGroup history = fHistory.get(id);
		if(history == null) {
			history = new HistoryGroup(id);
			fHistory.put(id, history);
		}
		history.addItem(props);
		saveHistoryGroup(id);
	}

	/**
	 * @param id the id of the history
	 * @return
	 */
	public synchronized static List<HistoryGroupItem> get(String id) {
		HistoryGroup hg = fHistory.get(id);
		if(hg == null) {
			// try to load it
			hg = loadHistoryGroup(id);
			if(hg == null) {
				return null;
			}
		}
		return hg.getItems();
	}

	/**
	 * @param id
	 * @return the most recent item from the history group with the
	 * given id
	 */
	public synchronized static XMLExternalizable getMostRecent(String id) {
		List<HistoryGroupItem> items = get(id);
		if(Utils.isEmptyCollection(items)) {
			return null;
		}
		HistoryGroupItem item = items.get(0);
		return item.getItem();
	}

	/**
	 * Clears history for the group with the given id.
	 * @param id
	 */
	public synchronized static void clear(String id) {
		HistoryGroup history = fHistory.get(id);
		if(history != null) {
			fHistory.remove(id);
			File file = getFileForGroup(id);
			file.delete();
		}

	}

	/**
	 * Clears all history items.
	 */
	public synchronized static void clear() {
		for(Iterator<String> iter = fHistory.keySet().iterator(); iter.hasNext();) {
			String id = iter.next();
			iter.remove();
			File file = getFileForGroup(id);
			file.delete();
		}
	}

	/**
	 * Loads the history items for the group with the given id.
	 * @param id
	 */
	private static HistoryGroup loadHistoryGroup(String id) {
		File file = getFileForGroup(id);
		if(!file.exists()) {
			return null;
		}
		BufferedInputStream bs = null;
		try {
			bs = new BufferedInputStream(new FileInputStream(file));
			Document doc = XMLUtils.read(bs);
			Node n = XMLUtils.findChild(doc, "rms");
			if(n == null) {
			    throw new XMLNodeMissing("rms");
			}
			n = XMLUtils.findChild(n, "historyGroup");
			if(n == null) {
			    throw new XMLNodeMissing("historyGroup");
			}
			HistoryGroup hg = new HistoryGroup();
			hg.fromXML(n);
			fHistory.put(id, hg);
			return hg;
		} catch(Exception e) {
			logger.error(e);
		} finally {
			if(bs != null) {
				try {
					bs.close();
				} catch (IOException e) {
				}
			}
		}
		return null;
	}

	/**
	 * @param id
	 * @return
	 */
	private static File getFileForGroup(String id) {
		return new File(Utils.getPath("config/history/" + id));
	}

	/**
	 * @param id
	 */
	private static void saveHistoryGroup(String id) {
		HistoryGroup hg = fHistory.get(id);
		if(hg == null) {
			return;
		}
		File out = getFileForGroup(id);
		BufferedOutputStream os = null;
		try {
			os = new BufferedOutputStream(new FileOutputStream(out));
			Document doc = XMLUtils.createEmptyDocument("rms");
			hg.toXML(XMLUtils.findChild(doc, "rms"));
			XMLUtils.write(doc, os);
		} catch(Exception e) {
			logger.error(e);
		} finally {
			if(os != null) {
				try {
					os.close();
				} catch(Exception e) {
				}
			}
		}
	}
}
