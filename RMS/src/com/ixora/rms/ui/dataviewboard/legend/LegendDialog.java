/*
 * Created on Feb 21, 2004
 */
package com.ixora.rms.ui.dataviewboard.legend;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;

import javax.swing.JDialog;
import javax.swing.JScrollPane;

import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.rms.ui.messages.Msg;
import com.ixora.rms.ui.preferences.PreferencesConfigurationConstants;

/**
 * @author Daniel Moraru
 */
public class LegendDialog extends JDialog
	implements PreferencesConfigurationConstants {
	private static final long serialVersionUID = 3213865412437253294L;
	/** Reference to the visible dialog */
	private static LegendDialog legendDlg;
	/** Legend panel */
	private LegendPanelDetailed legendPanel;
	/** The scroll pane holding the legend panel */
	private JScrollPane scroll;

	/**
	 * @param parent
	 */
	public LegendDialog(Frame parent) {
		super(parent, MessageRepository.get(Msg.TEXT_LEGEND));
		setModal(false);
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		scroll = new JScrollPane(legendPanel);
		setContentPane(scroll);
		setSize(ConfigurationMgr.getInt(PREFERENCES, LEGEND_WIDTH),
				ConfigurationMgr.getInt(PREFERENCES, LEGEND_HEIGHT));
		setLocation(ConfigurationMgr.getInt(PREFERENCES, LEGEND_X),
				ConfigurationMgr.getInt(PREFERENCES, LEGEND_Y));
	}

	/**
	 * Sets the new legend panel.
	 * @param p
	 */
	public void setLegendPanelDetailed(LegendPanelDetailed p) {
		if(legendPanel == p) {
			return;
		}
		legendPanel = p;
		scroll.setViewportView(legendPanel);
	}

	/**
	 * Shows the legend window.
	 * @param parent
	 * @return the visible legend dialog
	 */
	public static LegendDialog showLegendDialog(Frame parent) {
		if(legendDlg == null) {
			legendDlg = new LegendDialog(parent);
			legendDlg.setVisible(true);
		}
		return legendDlg;
	}

	/**
	 * Hides the legend window.
	 */
	public static void hideLegendDialog() {
		if(legendDlg != null) {
			legendDlg.dispose();
			legendDlg = null;
		}
	}

	/**
	 * @return the visible dialog or <code>null</code> if not visible
	 */
	public static LegendDialog getLegendDialog() {
		return legendDlg;
	}

	/**
	 * @see java.awt.Window#dispose()
	 */
	public void dispose() {
		// save preferences
		Point p = getLocation();
		Dimension d = getSize();
		ConfigurationMgr.setInt(PREFERENCES, LEGEND_X, p.x);
		ConfigurationMgr.setInt(PREFERENCES, LEGEND_Y, p.y);
		ConfigurationMgr.setInt(PREFERENCES, LEGEND_WIDTH, d.width);
		ConfigurationMgr.setInt(PREFERENCES, LEGEND_HEIGHT, d.height);
		super.dispose();
		legendDlg = null;
	}
}
