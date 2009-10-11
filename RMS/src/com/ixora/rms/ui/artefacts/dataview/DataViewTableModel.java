/*
 * Created on 24-Dec-2003
 */
package com.ixora.rms.ui.artefacts.dataview;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import com.ixora.common.ui.UIConfiguration;
import com.ixora.rms.client.model.DataViewInfo;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.repository.DataViewBoardInstallationData;
import com.ixora.rms.services.DataViewBoardRepositoryService;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.artefacts.SelectableArtefactTableModel;

/**
 * The model for the queries table.
 * @author Daniel Moraru
 */
public final class DataViewTableModel
	extends SelectableArtefactTableModel<DataViewInfo> {
	private static final long serialVersionUID = 7278855922270165234L;
	private static final int EXTRA_COLUMNS_COUNT = 2;
	private static Icon iconWithReactions = UIConfiguration.getIcon("dec_reactions.gif");

    /**
     * Cached board icons.
     */
    private Map<String, ImageIcon> fBoardIcons;

    /**
	 * Constructor.
	 * @param vc
	 * @param sm
	 * @param logReplayMode
     * @param dataViewBoardRepository
	 */
	public DataViewTableModel(RMSViewContainer vc, SessionModel sm,
	        boolean logReplayMode, DataViewBoardRepositoryService dataViewBoardRepository) {
		super(vc, sm, logReplayMode);
        Map<String, DataViewBoardInstallationData> map = dataViewBoardRepository.getInstalledDataViewBoards();
        this.fBoardIcons = new HashMap<String, ImageIcon>(map.size());
        for(Map.Entry<String, DataViewBoardInstallationData> me : map.entrySet()) {
            this.fBoardIcons.put(me.getValue().getBoardClass(),
                    UIConfiguration.getIcon(me.getValue().getBoardIcon()));
        }
	}

	/**
	 * @return the queries that must be realized (enabled but not committed)<br>
	 * List of DataViewInfo
	 */
	public List<DataViewInfo> getDataViewsToRealize() {
		return getArtefactsToRealize();
	}

	/**
	 * @return the views that must be unrealized(disabled but not committed)<br>
	 * List of DataViewInfo
	 */
	public List<DataViewInfo> getDataViewsToUnRealize() {
		return getArtefactsToUnRealize();
	}


    /**
     * @see com.ixora.rms.ui.artefacts.SelectableArtefactTableModel#getColumnClass(int)
     */
    public Class<?> getColumnClass(int columnIndex) {
    	switch(columnIndex) {
    	case 0:
    	case 1:
    		return ImageIcon.class;
    	default:
            return super.getColumnClass(columnIndex - EXTRA_COLUMNS_COUNT);
        }
    }

    /**
     * @see com.ixora.rms.ui.artefacts.SelectableArtefactTableModel#getColumnCount()
     */
    public int getColumnCount() {
        return super.getColumnCount() + EXTRA_COLUMNS_COUNT;
    }

    /**
     * @see com.ixora.rms.ui.artefacts.SelectableArtefactTableModel#getValueAt(int, int)
     */
    public Object getValueAt(int row, int column) {
    	switch(column) {
    	case 0:
            DataViewInfo dvi = (DataViewInfo)this.fArtefactData.get(row);
            return fBoardIcons.get(dvi.getDataView().getBoardClass());
    	case 1:
            dvi = (DataViewInfo)this.fArtefactData.get(row);
            return dvi.hasReactions() ? iconWithReactions : null;
    	default:
            return super.getValueAt(row, column - EXTRA_COLUMNS_COUNT);
        }
    }

    /**
     * @see com.ixora.rms.ui.artefacts.SelectableArtefactTableModel#isCellEditable(int, int)
     */
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if(columnIndex < EXTRA_COLUMNS_COUNT) {
            return false;
        }
        return super.isCellEditable(rowIndex, columnIndex - EXTRA_COLUMNS_COUNT);
    }

    /**
     * @see com.ixora.rms.ui.artefacts.SelectableArtefactTableModel#setValueAt(java.lang.Object, int, int)
     */
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        if(columnIndex < EXTRA_COLUMNS_COUNT) {
            return;
        }
        super.setValueAt(aValue, rowIndex, columnIndex - EXTRA_COLUMNS_COUNT);
    }

    /**
     * @param rowIndex
     */
    public void enableDataView(int rowIndex) {
    	setArtefactEnabled(rowIndex, true);
    }

    /**
     * @see com.ixora.rms.ui.artefacts.SelectableArtefactTableModel#setArtefactEnabled(int, boolean)
     */
    protected void setArtefactEnabled(int rowIndex, boolean e) {
		DataViewInfo dvi = (DataViewInfo)this.fArtefactData.get(rowIndex);
		fSessionModel.getDataViewHelper().setDataViewFlag(
		        DataViewInfo.ENABLED,
				fContext,
				dvi.getDataView().getName(),
				e, false);
    }
}