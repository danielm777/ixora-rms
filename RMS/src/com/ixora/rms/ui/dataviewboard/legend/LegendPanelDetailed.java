/*
 * Created on Feb 21, 2004
 */
package com.ixora.rms.ui.dataviewboard.legend;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIFactoryMgr;

/**
 * @author Daniel Moraru
 */
public class LegendPanelDetailed extends LegendPanel {
	private static final long serialVersionUID = -6735868351281565820L;
	/** Label with name and decsription */
	private JEditorPane jHtml;
	/** Panel holding legend entries */
	private JPanel entriesPanel;

	/**
	 * Constructor.
	 * @param legend
	 */
	public LegendPanelDetailed(Legend legend) {
		super(legend);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		int pad = UIConfiguration.getPanelPadding();
		setBorder(BorderFactory.createEmptyBorder(pad, pad, pad, pad));
		this.jHtml = UIFactoryMgr.createHtmlPane();
		this.jHtml.setText(legend.getHtml());
		JScrollPane sp = new JScrollPane();
		sp.setViewportView(this.jHtml);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		sp.setPreferredSize(new Dimension(300, 100));
		add(sp);
		entriesPanel = new JPanel();
		entriesPanel.setLayout(new BoxLayout(entriesPanel, BoxLayout.Y_AXIS));
        JPanel entriesPanelHolder = new JPanel(new BorderLayout());
        entriesPanelHolder.add(entriesPanel, BorderLayout.NORTH);
		add(entriesPanelHolder);
	}


	/**
	 * @param cis
	 */
	private void buildEntries(LegendItemInfo[] cis) {
		removeEntries();
		LegendItemInfo ci;
		LegendEntry le;
		for(int i = 0; i < cis.length; i++) {
			ci = cis[i];
			le = getLegendEntry(ci);
			JPanel p = new JPanel(new BorderLayout());
			p.add(le, BorderLayout.CENTER);
			entriesPanel.add(p);
			entriesPanel.add(Box.createVerticalStrut(3));
			legendEntries.add(le);
		}
		revalidate();
		repaint();
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.legend.LegendPanel#removeEntries()
	 */
	protected void removeEntries() {
		super.removeEntries();
		entriesPanel.removeAll();
	}

	/**
	 * @param ci
	 * @return
	 */
	protected LegendEntry getLegendEntry(LegendItemInfo ci) {
		return new LegendEntry(ci);
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.legend.LegendPanel#handleItemsInfoChanged(com.ixora.rms.ui.dataviewboard.legend.LegendItemInfo[])
	 */
	protected void handleItemsInfoChanged(LegendItemInfo[] ii) {
		super.handleItemsInfoChanged(ii);
		buildEntries(ii);
	}

	/**
	 * @see com.ixora.rms.exporter.html.HTMLProvider#toHTML(java.lang.StringBuilder, java.io.File)
	 */
	public void toHTML(StringBuilder buff, File root) throws IOException {
	}
}
