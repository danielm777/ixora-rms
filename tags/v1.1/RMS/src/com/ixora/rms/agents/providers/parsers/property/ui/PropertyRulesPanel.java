/*
 * Created on 03-Jan-2005
 */
package com.ixora.rms.agents.providers.parsers.property.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

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
import com.ixora.rms.agents.providers.parsers.property.PropertyRulesDefinition;
import com.ixora.rms.agents.providers.parsers.property.messages.Msg;
import com.ixora.rms.providers.ProvidersComponent;
import com.ixora.rms.providers.parsers.ParsingRulesDefinition;
import com.ixora.rms.providers.parsers.exception.InvalidParsingRules;
import com.ixora.rms.providers.parsers.ui.ParsingRulesPanel;

/**
 * @author Daniel Moraru
 */
public final class PropertyRulesPanel extends ParsingRulesPanel {
	private static final long serialVersionUID = -2990205469056388909L;
	//	private static final String LABEl_ENTITY_INDENTATION = MessageRepository.get(
//				ProvidersComponent.NAME, Msg.TEXT_PARSERS_PROPERTY_ENTITY_INDENTATION);
    private static final String LABEl_PROPERTY_VALUE_REGEX = MessageRepository.get(
            ProvidersComponent.NAME, Msg.TEXT_PARSERS_PROPERTY_PROPERTY_VALUE_REGEX);
    private static final String LABEl_PROPERTY_VALUE_REGEXP_TOOLTIP = MessageRepository.get(
            ProvidersComponent.NAME, Msg.TEXT_PARSERS_PROPERTY_PROPERTY_VALUE_REGEX_TOOLTIP);
    private static final String LABEl_ENTITY_REGEX = MessageRepository.get(
            ProvidersComponent.NAME, Msg.TEXT_PARSERS_PROPERTY_ENTITY_REGEX);
    private static final String LABEl_ENTITY_REGEX_TOOLTIP = MessageRepository.get(
            ProvidersComponent.NAME, Msg.TEXT_PARSERS_PROPERTY_ENTITY_REGEX_TOOLTIP);
    private static final String LABEl_IGNORE_LINES_MATCHING = MessageRepository.get(
            ProvidersComponent.NAME, Msg.TEXT_PARSERS_PROPERTY_IGNORE_LINES_MATCHING);
    private static final String LABEl_IGNORE_LINES_MATCHING_TOOLTIP = MessageRepository.get(
            ProvidersComponent.NAME, Msg.TEXT_PARSERS_PROPERTY_IGNORE_LINES_MATCHING_TOOLTIP);
    private static final String LABEl_BASE_ENTITY = MessageRepository.get(
            ProvidersComponent.NAME, Msg.TEXT_PARSERS_PROPERTY_BASE_ENTITY);
    private static final String LABEl_BASE_ENTITY_TOOLTIP = MessageRepository.get(
            ProvidersComponent.NAME, Msg.TEXT_PARSERS_PROPERTY_BASE_ENTITY_TOOLTIP);
    private static final String LABEl_VALUE_IS_SECOND_MATCH = MessageRepository.get(
            ProvidersComponent.NAME, Msg.TEXT_PARSERS_PROPERTY_VALUE_IS_SECOND_MATCH);
    private static final String LABEl_VALUE_IS_SECOND_MATCH_TOOLTIP = MessageRepository.get(
            ProvidersComponent.NAME, Msg.TEXT_PARSERS_PROPERTY_VALUE_IS_SECOND_MATCH_TOOLTIP);

	/** TypedPropertiesListEditor */
	private TypedPropertiesListEditor fRulesEditor;
	/** Typed properties used as a prototype for column definitions */
	private TypedProperties fPrototype;
	/** Form */
	private FormPanel fForm;
	/** Field indentation text field */
	private JTextField fTextFieldEntityIndentation;
    /** Property/value regex text field */
    private JTextField fTextFieldPropertyValueRegex;
    /** Entity regex text field */
    private JTextField fTextFieldEntityRegex;
    /** Ignore lines text field */
    private JTextField fTextFieldIgnoreLines;
    /** Base entity */
    private JTextField fTextFieldBaseEntity;
	/** Value is second match checkbox */
    private JCheckBox fCheckBoxValueIsSecondMatch;

	/**
	 * Constructor.
	 */
	public PropertyRulesPanel() {
		super(new BorderLayout());
		fPrototype = createFromResourceId(null);

		fTextFieldEntityIndentation = UIFactoryMgr.createTextField();
		Dimension d = fTextFieldEntityIndentation.getPreferredSize();
		fTextFieldEntityIndentation.setPreferredSize(new Dimension(50, d.height));
		JPanel panel1 = new JPanel(new BorderLayout());
		panel1.add(fTextFieldEntityIndentation, BorderLayout.WEST);

        fTextFieldPropertyValueRegex = UIFactoryMgr.createTextField();
        d = fTextFieldPropertyValueRegex.getPreferredSize();
        fTextFieldPropertyValueRegex.setPreferredSize(new Dimension(200, d.height));
        JPanel panel2 = new JPanel(new BorderLayout());
        JPanel panel22 = new JPanel(new BorderLayout());
        panel22.add(fTextFieldPropertyValueRegex, BorderLayout.NORTH);
        panel2.add(panel22, BorderLayout.WEST);
        fCheckBoxValueIsSecondMatch = UIFactoryMgr.createCheckBox();
        fCheckBoxValueIsSecondMatch.setText(LABEl_VALUE_IS_SECOND_MATCH);
        fCheckBoxValueIsSecondMatch.setToolTipText(LABEl_VALUE_IS_SECOND_MATCH_TOOLTIP);
        JPanel panel23 = new JPanel(new BorderLayout());
        panel23.add(fCheckBoxValueIsSecondMatch,BorderLayout.NORTH);
        panel2.add(panel23, BorderLayout.CENTER);

        fTextFieldEntityRegex = UIFactoryMgr.createTextField();
        d = fTextFieldEntityRegex.getPreferredSize();
        fTextFieldEntityRegex.setPreferredSize(new Dimension(200, d.height));
        JPanel panel3 = new JPanel(new BorderLayout());
        panel3.add(fTextFieldEntityRegex, BorderLayout.WEST);

        fTextFieldIgnoreLines = UIFactoryMgr.createTextField();
        fTextFieldBaseEntity = UIFactoryMgr.createTextField();

		fCheckBoxAccumulateEntities = UIFactoryMgr.createCheckBox();

		fForm = new FormPanel(FormPanel.VERTICAL1);
		fForm.addPairs(
				new String[]{
                        LABEl_ENTITY_REGEX,
                        LABEl_PROPERTY_VALUE_REGEX,
                     //   LABEl_ENTITY_INDENTATION,
                        LABEl_BASE_ENTITY,
                        LABEl_IGNORE_LINES_MATCHING,
                        LABEl_ACCUMULATE_VOLATILE_ENTITIES},
				new String[]{
                        LABEl_ENTITY_REGEX_TOOLTIP,
                        LABEl_PROPERTY_VALUE_REGEXP_TOOLTIP,
                     //   LABEl_ENTITY_INDENTATION,
                        LABEl_BASE_ENTITY_TOOLTIP,
                        LABEl_IGNORE_LINES_MATCHING_TOOLTIP,
                        LABEl_ACCUMULATE_VOLATILE_ENTITIES_TOOLTIP},
			new Component[] {
                        panel3,
                        panel2,
                     //  panel1,
                        fTextFieldBaseEntity,
                        fTextFieldIgnoreLines,
                        fCheckBoxAccumulateEntities});

		add(fForm, BorderLayout.NORTH);
	}

	/**
	 * @param cd can be null
	 * @return
	 */
	private static TypedProperties createFromResourceId(ResourceId ri) {
		TypedProperties prop = new TypedProperties();
		prop.setProperty(Msg.COLUMN_ENTITY_ID, TypedProperties.TYPE_STRING, true, false);
		prop.setProperty(Msg.COLUMN_COUNTER_ID, TypedProperties.TYPE_STRING, true, false);
		if(ri != null) {
			CounterId cid = ri.getCounterId();
			if(cid != null) {
				prop.setString(Msg.COLUMN_COUNTER_ID, cid.toString());
			}
			EntityId eid = ri.getEntityId();
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
	private static ResourceId createFromTypedProperties(TypedProperties prop) {
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
		return new ResourceId(null, null, eid, cid);
	}

	/**
	 * @see com.ixora.rms.providers.parsers.ui.ParsingRulesPanel#createRules()
	 */
	public ParsingRulesDefinition createRules() {
		return new PropertyRulesDefinition();
	}

	/**
	 * @see com.ixora.rms.providers.parsers.ui.ParsingRulesPanel#setRules(com.ixora.rms.providers.parsers.ParsingRulesDefinition)
	 */
	public void setRules(ParsingRulesDefinition rules) {
        PropertyRulesDefinition prules = (PropertyRulesDefinition)rules;
		String ei = prules.getEntityIndentation();
		if(ei != null) {
			fTextFieldEntityIndentation.setText(ei);
		}
        String tmp = prules.getPropertyValueRegex();
        if(tmp != null) {
            fTextFieldPropertyValueRegex.setText(tmp);
        }
        tmp = prules.getEntityRegex();
        if(tmp != null) {
            fTextFieldEntityRegex.setText(tmp);
        }
        tmp = prules.getIgnoreLines();
        if(tmp != null) {
            fTextFieldIgnoreLines.setText(tmp);
        }
        EntityId eid = prules.getBaseEntity();
        if(eid != null) {
            fTextFieldBaseEntity.setText(eid.toString());
        }

        fCheckBoxValueIsSecondMatch.setSelected(prules.isValueSecondMatch());
		fCheckBoxAccumulateEntities.setSelected(prules.accumulateVolatileEntities());
		Set<ResourceId> rids = prules.getResourceIds();
		List<TypedProperties> lst = null;
		if(!Utils.isEmptyCollection(rids)) {
			lst = new ArrayList<TypedProperties>(rids.size());
			for(ResourceId ri : rids) {
				lst.add(createFromResourceId(ri));
			}
		}
		fRulesEditor = new TypedPropertiesListEditor(
				new PropertyListTableModel(
				fPrototype, ProvidersComponent.NAME, lst), TypedPropertiesListEditor.BUTTON_ALL);
		if(fRulesEditor != null) {
			remove(fRulesEditor);
		}
		add(fRulesEditor, BorderLayout.CENTER);
	}

	/**
	 * @throws InvalidParsingRules
	 * @see com.ixora.rms.providers.parsers.ui.ParsingRulesPanel#getRules()
	 */
	public ParsingRulesDefinition getRules() throws InvalidParsingRules {
		if(fRulesEditor == null) {
			return null;
		}
		fRulesEditor.stopEditing();
		List<TypedProperties> ret = fRulesEditor.getModel().getAllProperties();
		if(ret == null || ret.size() == 0) {
			return null;
		}
		Set<ResourceId> rids = new LinkedHashSet<ResourceId>(ret.size());
		//trules.
		for(TypedProperties p : ret) {
            ResourceId  rid = createFromTypedProperties(p);
			rids.add(rid);
		}
		PropertyRulesDefinition prules = new PropertyRulesDefinition(
				rids,
				fTextFieldEntityIndentation.getText(),
                fTextFieldBaseEntity.getText(),
                fTextFieldEntityRegex.getText(),
                fTextFieldPropertyValueRegex.getText(),
                fCheckBoxValueIsSecondMatch.isSelected(),
				fCheckBoxAccumulateEntities.isSelected(), getIgnoreLines());
		return prules;
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
}
