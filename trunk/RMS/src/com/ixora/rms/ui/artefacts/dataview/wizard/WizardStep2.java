/**
 * 02-Jan-2006
 */
package com.ixora.rms.ui.artefacts.dataview.wizard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.common.ui.wizard.WizardStep;
import com.ixora.rms.ui.dataviewboard.charts.ChartStyle;

/**
 * @author Daniel Moraru
 */
public class WizardStep2 extends WizardStep {

	public static class Data {
		enum ControlType {TABLE, CHART_BAR, CHART_TIMESERIES, PROPERTIES}
		ControlType fControlType;
		ChartStyle fChartRenderer;
		WizardStep1.Data fStep1;

		Data(WizardStep1.Data step1, ControlType controlType, ChartStyle renderer) {
			fControlType = controlType;
			fChartRenderer = renderer;
			fStep1 = step1;
		}
	}
	// until I learn how to localize the new enums
	private static class ListItem {
		Data.ControlType fType;
		ListItem(Data.ControlType type) {
			fType = type;
		}
		public String toString() {
			switch(fType) {
			case TABLE:
				return "Table";
			case PROPERTIES:
				return "Properties";
			case CHART_BAR:
				return "Bar chart";
			case CHART_TIMESERIES:
				return "Timeseries chart";
			default:
				break;
			}
			return null;
		}
	}

	private WizardStep1.Data fDataViewType;
	private JComboBox fTypesComboBox;
	private JComboBox fComboRenderers;
	private FormPanel fForm;

	private class EventHandler implements ItemListener {
		/**
		 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
		 */
		public void itemStateChanged(ItemEvent e) {
			handleControlTypeChanged();
		}
	}

	public WizardStep2() {
		super("Control Type", "Select the type of control for this data view");
		setLayout(new BorderLayout());
		fTypesComboBox = UIFactoryMgr.createComboBox();
		fComboRenderers = UIFactoryMgr.createComboBox();
		fForm = new FormPanel(FormPanel.VERTICAL2);
		fForm.addPairs(
			new String[]{
				"Select Control Type",
				"Chart Renderer"
			},
			new Component[]{
				fTypesComboBox,
				fComboRenderers
			}
		);
		fForm.setPreferredSize(new Dimension(300, fForm.getPreferredSize().height));
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(fForm, BorderLayout.WEST);
		add(panel, BorderLayout.NORTH);

		fForm.setVisible("Chart Renderer", false);
		fTypesComboBox.addItemListener(new EventHandler());
	}

	/**
	 * @see com.ixora.common.ui.wizard.WizardStep#activate(java.lang.Object)
	 */
	public void activate(Object obj) throws Exception {
		fDataViewType = (WizardStep1.Data)obj;
		if(fDataViewType.fType == WizardStep1.Data.Type.ENTITY_REGEX) {
			fTypesComboBox.setModel(new DefaultComboBoxModel(new Object[]{
					new ListItem(Data.ControlType.CHART_BAR),
					new ListItem(Data.ControlType.CHART_TIMESERIES),
					new ListItem(Data.ControlType.TABLE)
			}));
		} else if(fDataViewType.fType == WizardStep1.Data.Type.DISTINCT) {
			fTypesComboBox.setModel(new DefaultComboBoxModel(new Object[]{
					new ListItem(Data.ControlType.CHART_TIMESERIES),
					new ListItem(Data.ControlType.PROPERTIES)
			}));
		} else if(fDataViewType.fType == WizardStep1.Data.Type.HOST_REGEX) {
			fTypesComboBox.setModel(new DefaultComboBoxModel(new Object[]{
					new ListItem(Data.ControlType.TABLE),
					new ListItem(Data.ControlType.CHART_BAR),
					new ListItem(Data.ControlType.CHART_TIMESERIES),
			}));
		}
		handleControlTypeChanged();
	}

	/**
	 * @see com.ixora.common.ui.wizard.WizardStep#deactivate()
	 */
	public Object deactivate() throws Exception {
		ListItem item = (ListItem)fTypesComboBox.getSelectedItem();
		ChartStyle renderer = null;
		if(item.fType == Data.ControlType.CHART_BAR || item.fType == Data.ControlType.CHART_TIMESERIES) {
			renderer = (ChartStyle)fComboRenderers.getSelectedItem();
		}
		return new Data(fDataViewType, item.fType, renderer);
	}

	private void handleControlTypeChanged() {
		Data.ControlType type = ((ListItem)fTypesComboBox.getSelectedItem()).fType;
		if(type == WizardStep2.Data.ControlType.CHART_BAR) {
			fForm.setVisible("Chart Renderer", true);
			fComboRenderers.setModel(new DefaultComboBoxModel(
					new Object[]{
							ChartStyle.BAR_2D,
							ChartStyle.BAR_3D,
							ChartStyle.STACKED_BAR_2D,
							ChartStyle.STACKED_BAR_3D,
							ChartStyle.CATEGORY_AREA,
							ChartStyle.CATEGORY_LINE,
							ChartStyle.CATEGORY_STACKED_AREA
					}));
		} else if(type == WizardStep2.Data.ControlType.CHART_TIMESERIES) {
			fForm.setVisible("Chart Renderer", true);
			fComboRenderers.setModel(new DefaultComboBoxModel(
					new Object[]{
							ChartStyle.XY_LINE,
							ChartStyle.XY_AREA,
							ChartStyle.STACKED_XY_AREA,
					}));
		} else {
			fForm.setVisible("Chart Renderer", false);
		}

	}
}
