/*
 * Created on Feb 21, 2004
 */
package com.ixora.rms.ui.dataviewboard.legend;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JPanel;

import com.ixora.rms.ui.exporter.HTMLProvider;


/**
 * @author Daniel Moraru
 */
public abstract class LegendPanel extends JPanel implements HTMLProvider {
	private static final long serialVersionUID = 6618120863907605083L;
	/** Legend */
	protected Legend legend;
	/** Legend entries */
	protected List<LegendEntry> legendEntries;
	/** Event handler */
	private EventHandler eventHandler;

	private final class EventHandler implements Legend.Listener {
		/**
		 * @see com.ixora.rms.ui.dataviewboard.legend.Legend.Listener#valuesChanged(java.lang.Number[], long)
		 */
		public void valuesChanged(Number[] values, long time) {
			handleValuesChanged(values, time);
		}
		/**
		 * @see com.ixora.rms.ui.dataviewboard.legend.Legend.Listener#itemsInfoUpdated(com.ixora.rms.ui.dataviewboard.legend.LegendItemInfo[])
		 */
		public void itemsInfoUpdated(LegendItemInfo[] ii) {
			handleItemsInfoChanged(ii);
		}
	}

	/**
	 * Constructor.
	 * @param legend
	 */
	protected LegendPanel(Legend legend) {
		super();
		this.eventHandler = new EventHandler();
		this.legendEntries = new LinkedList<LegendEntry>();
		this.legend = legend;
		legend.addListener(this.eventHandler);
	}

	/**
	 * Highlights the entry with the given name.
	 * @param name
	 */
	public void highlightEntry(String name) {
		LegendEntry le;
		for(Iterator<LegendEntry> iter = legendEntries.iterator(); iter.hasNext();) {
			le = iter.next();
			if(le.isHighlighted()) {
				le.highlight(false);
			}
			if(le.getName().equals(name)) {
				le.highlight(true);
			}
		}
	}

	/**
	 * @param values
	 * @param time
	 */
	protected void handleValuesChanged(Number[] values, long time) {
		if(this.legendEntries.size() != values.length) {
			return;
		}
		int i = 0;
		LegendEntry le;
		for(Iterator<LegendEntry> iter = this.legendEntries.iterator(); iter.hasNext(); ++i) {
			le = iter.next();
			le.setValue(values[i]);
		}
	}

	/**
	 * @param ii
	 */
	protected void handleItemsInfoChanged(LegendItemInfo[] ii) {
		; // nothing
	}

	/**
	 * Removes all legend entries.
	 */
	protected void removeEntries() {
		legendEntries.clear();
	}

	/**
	 * @param ci
	 * @return
	 */
	protected abstract LegendEntry getLegendEntry(LegendItemInfo ci);
}
