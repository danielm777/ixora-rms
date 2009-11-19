/**
 * 01-Feb-2006
 */
package com.ixora.rms.tests;

import java.util.List;

import junit.framework.TestCase;

import com.ixora.common.history.HistoryGroupItem;
import com.ixora.common.history.HistoryMgr;
import com.ixora.rms.CustomConfiguration;

/**
 * @author Daniel Moraru
 */
public class HistoryMgrTest extends TestCase {

	@SuppressWarnings("serial")
	public static class Props extends CustomConfiguration {
		public Props() {
			super();
			setProperty("p1", TYPE_STRING, true, true);
			setProperty("p2", TYPE_STRING, true, true);
			setProperty("p3", TYPE_STRING, true, true);

			setString("p1", "val1");
			setString("p2", "val2");
			setString("p3", "val3");
		}

	}

	/**
	 *
	 */
	public HistoryMgrTest() {
		super();
	}

	public void testAdd() {
		for(int i=0; i < 3; ++i) {
			Props props = new Props();
			props.setString("p1", "s" + i);
			HistoryMgr.add("group1", props);
		}
	}

	public void testClear() {
		testAdd();
		HistoryMgr.clear("group1");
		testAdd();
	}

	public void testGet() {
		List<HistoryGroupItem> items = HistoryMgr.get("group1");
		assertEquals(items.size(), 3);
	}
}
