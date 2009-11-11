/*
 * Created on Feb 21, 2004
 */
package com.ixora.rms.ui.dataviewboard.legend;

import java.awt.Component;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;

import com.ixora.common.ui.UIFactoryMgr;

/**
 * @author Daniel Moraru
 */
public class LegendPanelSimple extends LegendPanel {
	private static final long serialVersionUID = -8734005760191421929L;
	/** Timestamp label */
	private JLabel jLabelTimestamp;
	/** List of components to remove when rebuilding the entries */
	private List<Component> components;

	/**
	 * Constructor.
	 * @param legend
	 */
	public LegendPanelSimple(Legend legend) {
		super(legend);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(jLabelTimestamp = UIFactoryMgr.createLabel(""));
		add(Box.createHorizontalStrut(5));
		this.components = new LinkedList<Component>();
	}


	/**
	 * @param cis
	 */
	private void buildEntries(LegendItemInfo[] cis) {
		removeEntries();
		LegendEntry le;
		LegendItemInfo ci;
		for(int i = 0; i < cis.length; i++) {
			ci = cis[i];
			le = getLegendEntry(ci);
			legendEntries.add(le);
			add(le);
			components.add(le);
			Component strut = Box.createHorizontalStrut(5);
			add(strut);
			components.add(strut);
		}
		revalidate();
		repaint();
	}


	/**
	 * @param ci
	 * @return
	 */
	protected LegendEntry getLegendEntry(LegendItemInfo ci) {
		return new LegendEntry(ci);
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.legend.LegendPanel#handleValuesChanged(java.lang.Number[], long)
	 */
	protected void handleValuesChanged(Number[] values, long time) {
		super.handleValuesChanged(values, time);
		jLabelTimestamp.setText("[" + new Date(time) + "]");
	}


	/**
	 * @see com.ixora.rms.ui.dataviewboard.legend.LegendPanel#handleItemsInfoChanged(com.ixora.rms.ui.dataviewboard.legend.LegendItemInfo[])
	 */
	protected void handleItemsInfoChanged(LegendItemInfo[] ii) {
		super.handleItemsInfoChanged(ii);
		buildEntries(ii);
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.legend.LegendPanel#removeEntries()
	 */
	protected void removeEntries() {
		super.removeEntries();
		for(Iterator<Component> iter = components.iterator(); iter.hasNext();) {
			remove(iter.next());
		}
	}


	/**
	 * @see com.ixora.rms.ui.exporter.HTMLProvider#toHTML(java.lang.StringBuilder, java.io.File)
	 */
	public void toHTML(StringBuilder buff, File root) throws IOException {
	}
}
