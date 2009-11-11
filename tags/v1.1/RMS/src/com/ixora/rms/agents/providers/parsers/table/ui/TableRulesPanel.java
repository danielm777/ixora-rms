/*
 * Created on 03-Jan-2005
 */
package com.ixora.rms.agents.providers.parsers.table.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

import javax.swing.JCheckBox;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ixora.common.MessageRepository;
import com.ixora.common.typedproperties.TypedProperties;
import com.ixora.common.typedproperties.ui.list.PropertyListTableModel;
import com.ixora.common.typedproperties.ui.list.TypedPropertiesListEditor;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterId;
import com.ixora.rms.EntityId;
import com.ixora.rms.ResourceId;
import com.ixora.rms.agents.providers.parsers.exception.DuplicateResourceInParsingRules;
import com.ixora.rms.agents.providers.parsers.table.ColumnDefinition;
import com.ixora.rms.agents.providers.parsers.table.TableRulesDefinition;
import com.ixora.rms.agents.providers.parsers.table.messages.Msg;
import com.ixora.rms.providers.ProvidersComponent;
import com.ixora.rms.providers.parsers.ParsingRulesDefinition;
import com.ixora.rms.providers.parsers.ui.ParsingRulesPanel;

/**
 * @author Daniel Moraru
 */
public final class TableRulesPanel extends ParsingRulesPanel {
	private static final long serialVersionUID = -1701264304369155136L;
	private static final String LABEl_COLUMN_SEPARATOR = MessageRepository.get(
				ProvidersComponent.NAME, Msg.TEXT_PARSERS_TABLE_COLUMN_SEPARATOR);
	private static final String LABEl_COLUMN_SEPARATOR_TOOLTIP = MessageRepository.get(
			ProvidersComponent.NAME, Msg.TOOLTIP_PARSERS_TABLE_COLUMN_SEPARATOR);
	private static final String LABEl_IGNORE_LINES_WITH = MessageRepository.get(
				ProvidersComponent.NAME, Msg.TEXT_PARSERS_TABLE_IGNORE_LINES_MATCHING);
	private static final String LABEl_IGNORE_LINES_WITH_TOOLTIP = MessageRepository.get(
			ProvidersComponent.NAME, Msg.TOOLTIP_PARSERS_TABLE_IGNORE_LINES_MATCHING);
	private static final String LABEl_COLUMNS_TO_IGNORE = MessageRepository.get(
			ProvidersComponent.NAME, Msg.TEXT_PARSERS_TABLE_COLUMNS_TO_IGNORE);
	private static final String LABEl_COLUMNS_TO_IGNORE_TOOLTIP = MessageRepository.get(
			ProvidersComponent.NAME, Msg.TOOLTIP_PARSERS_TABLE_COLUMNS_TO_IGNORE);
	private static final String LABEl_CONVERT_SINGLE_COLUMN_TO_ROW = MessageRepository.get(
			ProvidersComponent.NAME, Msg.TEXT_PARSERS_TABLE_CONVERT_SINGLE_COLUMN_TO_ROW);
	private static final String LABEl_CONVERT_SINGLE_COLUMN_TO_ROW_TOOLTIP = MessageRepository.get(
			ProvidersComponent.NAME, Msg.TOOLTIP_PARSERS_TABLE_CONVERT_SINGLE_COLUMN_TO_ROW);

	/** TypedPropertiesListEditor */
	private TypedPropertiesListEditor fRulesEditor;
	/** Typed properties used as a prototype for column definitions */
	private TypedProperties fPrototype;
	/** Form */
	private FormPanel fFormColumnSeparator;
	/** Column separator text field */
	private JTextField fTextFieldColumnSeparator;
	/** Ignore lines text field */
	private JTextField fTextFieldIgnoreLines;
	/** Columns... text field */
	private JTextField fTextFieldColumnsToIgnoreWhenMoreThanExpected;
	/** Convert column to row check box */
	private JCheckBox fCheckBoxConvertColToRow;

	/**
	 * Model for the object editor.
	 */
	@SuppressWarnings("serial")
	private final class CustomPropertiesListTableModel extends PropertyListTableModel {
		public CustomPropertiesListTableModel(TypedProperties arg0, String arg1, List<TypedProperties> arg2) {
			super(arg0, arg1, arg2, false);
		}
		/**
		 * @throws Exception
		 * @see com.ixora.common.typedproperties.ui.list.PropertyListTableModel#addNewProperty()
		 */
		public void addNewProperty() throws Exception {
			super.addNewProperty();
			reassignIndexes();
		}
		/**
		 * @see com.ixora.common.typedproperties.ui.list.PropertyListTableModel#movePropertyDown(int)
		 */
		public void movePropertyDown(int row) {
			super.movePropertyDown(row);
			reassignIndexes();
		}
		/**
		 * @see com.ixora.common.typedproperties.ui.list.PropertyListTableModel#movePropertyUp(int)
		 */
		public void movePropertyUp(int row) {
			super.movePropertyUp(row);
			reassignIndexes();
		}
		/**
		 * @see com.ixora.common.typedproperties.ui.list.PropertyListTableModel#removeProperty(int)
		 */
		public void removeProperty(int row) {
			super.removeProperty(row);
			reassignIndexes();
		}
		/**
		 * Reassigns indexes.
		 */
		private void reassignIndexes() {
			for(int i = 0; i < this.fProperties.size(); i++) {
				TypedProperties p = this.fProperties.get(i);
				p.setInt(Msg.COLUMN_INDEX, i);
			}
			fireTableDataChanged();
		}
		/**
		 * @see javax.swing.table.TableModel#isCellEditable(int, int)
		 */
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 0 ? false : true;
		}
	}

	/**
	 * Constructor.
	 */
	public TableRulesPanel() {
		super(new BorderLayout());
		fPrototype = createFromColumnDefinition(null);

		fTextFieldColumnSeparator = UIFactoryMgr.createTextField();
		Dimension d = fTextFieldColumnSeparator.getPreferredSize();
		fTextFieldColumnSeparator.setPreferredSize(new Dimension(50, d.height));
		JPanel panelColumnSep = new JPanel(new BorderLayout());
		panelColumnSep.add(fTextFieldColumnSeparator, BorderLayout.WEST);

		fTextFieldIgnoreLines = UIFactoryMgr.createTextField();
		fTextFieldColumnsToIgnoreWhenMoreThanExpected = UIFactoryMgr.createTextField();
		fCheckBoxAccumulateEntities = UIFactoryMgr.createCheckBox();
		fCheckBoxConvertColToRow = UIFactoryMgr.createCheckBox();

		fFormColumnSeparator = new FormPanel(FormPanel.VERTICAL1);
		fFormColumnSeparator.addPairs(
				new String[]{
						LABEl_CONVERT_SINGLE_COLUMN_TO_ROW,
						LABEl_COLUMN_SEPARATOR,
						LABEl_IGNORE_LINES_WITH,
						LABEl_COLUMNS_TO_IGNORE,
						LABEl_ACCUMULATE_VOLATILE_ENTITIES},
				new String[]{
						LABEl_CONVERT_SINGLE_COLUMN_TO_ROW_TOOLTIP,
						LABEl_COLUMN_SEPARATOR_TOOLTIP,
						LABEl_IGNORE_LINES_WITH_TOOLTIP,
						LABEl_COLUMNS_TO_IGNORE_TOOLTIP,
						LABEl_ACCUMULATE_VOLATILE_ENTITIES_TOOLTIP},
			new Component[] {
						fCheckBoxConvertColToRow,
						panelColumnSep,
						fTextFieldIgnoreLines,
						fTextFieldColumnsToIgnoreWhenMoreThanExpected,
						fCheckBoxAccumulateEntities});

		add(fFormColumnSeparator, BorderLayout.NORTH);
	}

	/**
	 * @param cd can be null
	 * @return
	 */
	private static TypedProperties createFromColumnDefinition(ColumnDefinition cd) {
		TypedProperties prop = new TypedProperties();
		prop.setProperty(Msg.COLUMN_INDEX, TypedProperties.TYPE_INTEGER, true);
		prop.setProperty(Msg.COLUMN_ENTITY_ID, TypedProperties.TYPE_STRING, true, false);
		prop.setProperty(Msg.COLUMN_COUNTER_ID, TypedProperties.TYPE_STRING, true, false);
		if(cd != null) {
			prop.setInt(Msg.COLUMN_INDEX, cd.getColumnIndex());
			CounterId cid = cd.getCounterId();
			if(cid != null) {
				prop.setString(Msg.COLUMN_COUNTER_ID, cid.toString());
			}
			EntityId eid = cd.getEntityId();
			if(eid != null) {
				prop.setString(Msg.COLUMN_ENTITY_ID, eid.toString());
			}
		}
		return prop;
	}

	/**
	 * @param prop
	 * @return
	 */
	private static ColumnDefinition createFromTypedProperties(TypedProperties prop) {
		int idx = prop.getInt(Msg.COLUMN_INDEX);
		String scid = prop.getString(Msg.COLUMN_COUNTER_ID);
		CounterId cid = null;
		if(scid != null && scid.trim().length() > 0) {
			cid = new CounterId(scid.trim());
		}
		String seid = prop.getString(Msg.COLUMN_ENTITY_ID);
		EntityId eid = null;
		if(seid != null && seid.trim().length() > 0) {
			eid = new EntityId(seid.trim());
		}
		return new ColumnDefinition(idx, eid, cid);
	}

	/**
	 * @see com.ixora.rms.providers.parsers.ui.ParsingRulesPanel#createRules()
	 */
	public ParsingRulesDefinition createRules() {
		return new TableRulesDefinition();
	}

	/**
	 * @see com.ixora.rms.providers.parsers.ui.ParsingRulesPanel#setRules(com.ixora.rms.providers.parsers.ParsingRulesDefinition)
	 */
	public void setRules(ParsingRulesDefinition rules) {
		TableRulesDefinition trules = (TableRulesDefinition)rules;
		String cs = trules.getColumnSeparator();
		if(cs != null) {
			fTextFieldColumnSeparator.setText(cs);
		}
		String ignoreLines = trules.getIgnoreLines();
		if(!Utils.isEmptyString(ignoreLines)) {
			fTextFieldIgnoreLines.setText(ignoreLines);
		}

		String columsToIgnore = trules.getColumnsToIgnoreWhenMoreThanExpectedAsString();
		if(!Utils.isEmptyString(columsToIgnore)) {
			fTextFieldColumnsToIgnoreWhenMoreThanExpected.setText(columsToIgnore);
		}

		fCheckBoxAccumulateEntities.setSelected(trules.accumulateVolatileEntities());
		fCheckBoxConvertColToRow.setSelected(trules.convertColumnToRow());

		ColumnDefinition[] cds = trules.getColumns();
		List<TypedProperties> lst = null;
		if(cds != null && cds.length > 0) {
			lst = new ArrayList<TypedProperties>(cds.length);
			for(ColumnDefinition cd : cds) {
				lst.add(createFromColumnDefinition(cd));
			}
		}
		fRulesEditor = new TypedPropertiesListEditor(
				new CustomPropertiesListTableModel(
				fPrototype, ProvidersComponent.NAME, lst), TypedPropertiesListEditor.BUTTON_ALL);
		if(fRulesEditor != null) {
			remove(fRulesEditor);
		}
		fRulesEditor.getTable().getColumnModel().getColumn(0).setMaxWidth(50);
		add(fRulesEditor, BorderLayout.CENTER);
	}

	/**
	 * @throws DuplicateResourceInParsingRules
	 * @see com.ixora.rms.providers.parsers.ui.ParsingRulesPanel#getRules()
	 */
	public ParsingRulesDefinition getRules() throws DuplicateResourceInParsingRules {
        stopEditing();
		List<TypedProperties> ret = fRulesEditor.getModel().getAllProperties();
		if(ret == null || ret.size() == 0) {
			return null;
		}
		List<ColumnDefinition> cds = new ArrayList<ColumnDefinition>(ret.size());
        // check that the regex for lines to be ignored is correct
        String regex = fTextFieldIgnoreLines.getText();
        if(!Utils.isEmptyString(regex)) {
            Pattern.compile(regex);
        }
        // check that we don't have two or more entries for the same counter
		Set<ResourceId> tmp = new HashSet<ResourceId>();
        for(TypedProperties p : ret) {
            ColumnDefinition cd = createFromTypedProperties(p);
            // check for duplicates
            EntityId eid = cd.getEntityId();
            CounterId cid = cd.getCounterId();
            if(eid != null && cid != null) {
                ResourceId rid = new ResourceId(null, null, eid, cid);
                if(tmp.contains(rid)) {
                    throw new DuplicateResourceInParsingRules(rid);
                }
                tmp.add(rid);
            }
			cds.add(cd);

		}
        if(tmp.size() == 0) {
            return null;
        }
		TableRulesDefinition trules = new TableRulesDefinition(
				cds.toArray(new ColumnDefinition[cds.size()]),
				fTextFieldColumnSeparator.getText(),
				getIgnoreLines(),
				fTextFieldColumnsToIgnoreWhenMoreThanExpected.getText(),
				fCheckBoxAccumulateEntities.isSelected(),
				fCheckBoxConvertColToRow.isSelected());
		return trules;
	}

	/**
	 * @return
	 */
	private String getIgnoreLines() {
		String tmp = fTextFieldIgnoreLines.getText();
		if(tmp.trim().length() == 0) {
			return null;
		}
		return tmp.trim();
	}

    /**
     * @see com.ixora.rms.providers.parsers.ui.ParsingRulesPanel#stopEditing()
     */
    public void stopEditing() {
        if(fRulesEditor == null) {
            return;
        }
        fRulesEditor.stopEditing();
    }
}
