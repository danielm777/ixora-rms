/*
 * Created on 24-Dec-2003
 */
package com.ixora.rms.ui;

import javax.swing.table.AbstractTableModel;

import com.ixora.rms.HostInformation;
import com.ixora.common.MessageRepository;
import com.ixora.rms.ui.messages.Msg;

/**
 * The model for the host info table.
 * @author Daniel Moraru
 */
public final class HostInfoTableModel extends AbstractTableModel {
	private static final long serialVersionUID = 7080773728207550831L;
	/** Column1 */
	private final String[] column1 = {
		MessageRepository.get(Msg.MODELS_TABLES_HOSTINFO_COL1),
		MessageRepository.get(Msg.MODELS_TABLES_HOSTINFO_COL2),
		MessageRepository.get(Msg.MODELS_TABLES_HOSTINFO_COL3),
		MessageRepository.get(Msg.MODELS_TABLES_HOSTINFO_COL4)
	};
	/** Host info */
	private HostInformation dtls;
	/** Cached value of the time difference in seconds */
	private String timeDeltaInSeconds;

	/**
	 * HostInfoTableModel.
	 */
	public HostInfoTableModel() {
		super();
	}

	/**
	 * @see javax.swing.table.AbstractTableModel#gtColumnName(int)
	 */
	public String getColumnName(int arg1) {
		return "";
	}

	/**
	 * @see javax.swing.table.TableModel#getRowCount()
	 */
	public int getRowCount() {
		return column1.length;
	}

	/**
	 * @see javax.swing.table.TableModel#getColumnCount()
	 */
	public int getColumnCount() {
		return 2;
	}

	/**
	 * @see javax.swing.table.TableModel#getValueAt(int, int)
	 */
	public Object getValueAt(int arg0, int arg1) {
		switch(arg0) {
			case 0: // operating system
				if(arg1 == 0) {
					return column1[0];
				}
				return dtls == null ? "" : dtls.getOperatingSystem().getName()
					+ "(" + dtls.getOperatingSystem().getVersion() + ")";
			case 1: // Architecture
				if(arg1 == 0) {
					return column1[1];
				}
				return dtls == null ? "" : dtls.getOperatingSystem().getArchitecture();
			case 2: // HostManager info
				if(arg1 == 0) {
					return column1[2];
				}
				return dtls == null ? "" : dtls.getHostManagerVersion().toString();
			case 3: // Time difference
				if(arg1 == 0) {
					return column1[3];
				}
				return dtls == null ? "" : this.timeDeltaInSeconds;
		}
		return null;
	}

	/**
	 * Sets the host info.
	 * @param dtls
	 */
	public void setHostInfo(HostInformation dtls) {
		if(dtls != null) {
			this.dtls = dtls;
			this.timeDeltaInSeconds = String.valueOf(
					this.dtls.getDeltaTime() / 1000f);
			fireTableDataChanged();
		}
	}
}
