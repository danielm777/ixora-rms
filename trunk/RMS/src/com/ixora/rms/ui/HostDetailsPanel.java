/*
 * Created on 24-Dec-2003
 */
package com.ixora.rms.ui;

import java.awt.BorderLayout;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import com.ixora.rms.HostInformation;

/**
 * Panel showing the host details.
 * @author Daniel Moraru
 */
public final class HostDetailsPanel extends JPanel {
	private JScrollPane jScrollPaneDtls = null;
	private JTable jTableDtls = null;
	private HostInfoTableModel model;

	/**
	 * Constructor.
	 */
	public HostDetailsPanel() {
		super(new BorderLayout());
		initialize();
	}

	/**
	 * This method initializes this
	 */
	private void initialize() {
		this.model = new HostInfoTableModel();
		this.add(getJScrollPaneDtls(), BorderLayout.CENTER);
	}

	/**
	 * @return javax.swing.JScrollPane
	 */
	private javax.swing.JScrollPane getJScrollPaneDtls() {
		if(jScrollPaneDtls == null) {
			jScrollPaneDtls = new javax.swing.JScrollPane();
			jScrollPaneDtls.setViewportView(getJTableDtls());
		}
		return jScrollPaneDtls;
	}

	/**
	 * @return javax.swing.JTable
	 */
	private JTable getJTableDtls() {
		if(jTableDtls == null) {
			jTableDtls = new javax.swing.JTable(this.model);
		}
		return jTableDtls;
	}

	/**
	 * Sets the host info to show.
	 * @param dtls
	 */
	public void setHostInfo(HostInformation dtls) {
		this.model.setHostInfo(dtls);
	}
}
