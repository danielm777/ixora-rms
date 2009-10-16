/*
 * Created on 05-Mar-2005
 */
package com.ixora.rms.ui.dataviewboard.properties;

import java.awt.Color;
import java.util.Observable;
import java.util.Observer;

import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.ixora.rms.ResourceId;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.ComponentConfiguration;
import com.ixora.common.ui.filter.RowFilter;
import com.ixora.rms.client.locator.SessionArtefactInfoLocator;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.services.ReactionLogService;
import com.ixora.rms.ui.dataviewboard.DataViewBoard;
import com.ixora.rms.ui.dataviewboard.DataViewControlContext;
import com.ixora.rms.ui.dataviewboard.exception.FailedToCreateControl;
import com.ixora.rms.ui.dataviewboard.utils.ButtonCellEditor;
import com.ixora.rms.ui.dataviewboard.utils.ButtonCellRenderer;
import com.ixora.rms.ui.dataviewboard.utils.TableBasedControl;
import com.ixora.rms.ui.dataviewboard.utils.TableBasedControlCellEditor;
import com.ixora.rms.ui.dataviewboard.utils.TableBasedControlDescriptor;
import com.ixora.rms.ui.dataviewboard.utils.TableBasedControlHTMLProvider;
import com.ixora.rms.ui.dataviewboard.utils.TableBasedControlTableModel;

/**
 * @author Daniel Moraru
 */
public class PropertiesControl extends TableBasedControl implements Observer {
	private static final long serialVersionUID = 8441663666892016536L;
	/** Table model */
	private PropertiesControlTableModel fTableModel;
	/** Editor for the third column */
	private ButtonCellEditor fValueCellEditor;
	/** Renderer for the third column */
	private ButtonCellRenderer fValueCellRenderer;

	/**
	 * Constructor.
	 * @param owner
	 * @param listener
	 * @param context
	 * @param locator
	 * @param resourceContext
	 * @param dataView
	 * @throws FailedToCreateControl
	 */
	public PropertiesControl(DataViewBoard owner, Listener listener,
			DataViewControlContext context,
			SessionArtefactInfoLocator locator,
			ReactionLogService rls,
			ResourceId resourceContext,
			DataView dataView)
			throws FailedToCreateControl {
		super(owner, listener, context, locator, rls, resourceContext, dataView);
		ConfigurationMgr.get(PropertiesBoardComponent.NAME).addObserver(this);
		buildLegend();
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControl#createDescriptor(int, boolean, com.ixora.common.ui.filter.RowFilter)
	 */
	protected TableBasedControlDescriptor createDescriptor(int sortedColIdx, boolean sortedDesc, RowFilter filter) {
		return new PropertiesControlDescriptor(sortedColIdx, sortedDesc, filter);
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewControl#cleanup()
	 */
	protected void cleanup() {
		super.cleanup();
		ConfigurationMgr.get(PropertiesBoardComponent.NAME).deleteObserver(this);
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewControl#reset()
	 */
	protected void reset() {
		fTableModel.reset();
	}

	/**
	 * Overriden to set the size of the last column and to resize the 'Property' column.
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControl#setColumnsWitdh()
	 */
	protected void setColumnsWitdh() {
		super.setColumnsWitdh();
        TableColumnModel columnModel = this.fTable.getColumnModel();
        TableColumn column = columnModel.getColumn(2);
        column.setMaxWidth(16);
        // set the width of the 'Property' column to the width of the
        // widest property name (min('Property', val))
        column = columnModel.getColumn(0);
        int width = column.getPreferredWidth();
        int rowCount = fTableModel.getRowCount();
        for(int i = 0; i < rowCount; i++) {
			width = Math.max(width,
					column.getCellRenderer().getTableCellRendererComponent(fTable,
						fTableModel.getValueAt(i, 0), false, false, i, 0).getPreferredSize().width);
		}
        width += 20;
        column.setMaxWidth(width);
        column.setMinWidth(width);
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControl#createHTMLProvider()
	 */
	protected TableBasedControlHTMLProvider createHTMLProvider() {
		return new TableBasedControlHTMLProvider(fTableModelSorter,
				getOwnCellRenderer().getFormatter());
	}

	/**
	 * Overriden to assign a different editor to the third table column.
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControl#setTableCellRendererAndEditor()
	 */
    protected void setTableCellRendererAndEditor() {
    	if(fValueCellEditor == null) {
    		fValueCellEditor = new ButtonCellEditor(
    				getOwnCellRenderer().getFormatter(),
    				fControlContext.getViewContainer().getAppFrame(),
    				fTableModelSorter) {
						private static final long serialVersionUID = 3090567778532118207L;

						/**
    					 * @see com.ixora.rms.ui.dataviewboard.utils.ButtonCellEditor#mapColumnForValue(int)
    					 */
						protected int mapColumnForValue(int column) {
							return column - 1;
						}
    				};
    	}
    	if(fValueCellRenderer == null) {
    		fValueCellRenderer = new ButtonCellRenderer();
    	}
        TableColumnModel columnModel = this.fTable.getColumnModel();
        int cols = columnModel.getColumnCount();
        TableColumn col;
        for(int i = 0; i < cols; i++) {
            col = columnModel.getColumn(i);
            if(i != 2) {
            	col.setCellEditor(fCellEditor);
            	col.setCellRenderer(fCellRenderer);
            } else {
            	col.setCellRenderer(fValueCellRenderer);
            	col.setCellEditor(fValueCellEditor);
            }
        }
    }

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		ComponentConfiguration conf = ConfigurationMgr.get(PropertiesBoardComponent.NAME);
		if(o == conf) {
			if(arg.equals(PropertiesBoardConfigurationConstants.PROPERTIESBOARD_STRIPE_COLOR)) {
				Color color = conf.getColor(PropertiesBoardConfigurationConstants.PROPERTIESBOARD_STRIPE_COLOR);
				getOwnCellRenderer().getFormatter().setRowStripeColor(color);
			} else if(arg.equals(PropertiesBoardConfigurationConstants.PROPERTIESBOARD_DEFAULT_NUMBER_FORMAT)) {
	        	String nf = ConfigurationMgr.getString(PropertiesBoardComponent.NAME,
	   	                   PropertiesBoardConfigurationConstants.PROPERTIESBOARD_DEFAULT_NUMBER_FORMAT);
	        	getOwnCellRenderer().getFormatter().setDefaultNumberFormat(nf);
	            return;
            }
		}
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControl#createTableCellRenderer()
	 */
	protected TableCellRenderer createTableCellRenderer() {
		return new PropertiesControlTableCellRenderer(
				ConfigurationMgr.getColor(PropertiesBoardComponent.NAME,
						PropertiesBoardConfigurationConstants.PROPERTIESBOARD_STRIPE_COLOR),
				ConfigurationMgr.getString(PropertiesBoardComponent.NAME,
						PropertiesBoardConfigurationConstants.PROPERTIESBOARD_DEFAULT_NUMBER_FORMAT));
	}

    /**
     * @return
     */
    private PropertiesControlTableCellRenderer getOwnCellRenderer() {
    	return (PropertiesControlTableCellRenderer)this.fCellRenderer;
    }

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControl#createTableModel()
	 */
	protected TableBasedControlTableModel createTableModel() {
		fTableModel = new PropertiesControlTableModel(getOwnCellRenderer().getFormatter());
		return fTableModel;
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.utils.TableBasedControl#createTableCellEditor()
	 */
	protected TableCellEditor createTableCellEditor() {
		return new TableBasedControlCellEditor(getOwnCellRenderer().getFormatter());
	}
}