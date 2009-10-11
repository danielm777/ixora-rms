/*
 * Created on 03-Jan-2005
 */
package com.ixora.rms.providers.parsers.ui;

import java.awt.LayoutManager;

import javax.swing.JCheckBox;
import javax.swing.JPanel;

import com.ixora.common.MessageRepository;
import com.ixora.rms.providers.ProvidersComponent;
import com.ixora.rms.providers.parsers.ParsingRulesDefinition;
import com.ixora.rms.providers.parsers.exception.InvalidParsingRules;
import com.ixora.rms.providers.parsers.messages.Msg;

/**
 * @author Daniel Moraru
 */
public abstract class ParsingRulesPanel extends JPanel {
	private static final long serialVersionUID = 4805252399857574978L;
	protected static final String LABEl_ACCUMULATE_VOLATILE_ENTITIES = MessageRepository.get(
            ProvidersComponent.NAME, Msg.TEXT_ACCUMULATE_VOLATILE_ENTITIES);
    protected static final String LABEl_ACCUMULATE_VOLATILE_ENTITIES_TOOLTIP = MessageRepository.get(
            ProvidersComponent.NAME, Msg.TOOLTIP_ACCUMULATE_VOLATILE_ENTITIES);
    /** Accumulate volatile entities check box */
    protected JCheckBox fCheckBoxAccumulateEntities;

	/**
	 * Constructor.
	 */
	protected ParsingRulesPanel() {
		super();
	}

	/**
	 * Constructor.
	 * @param lm
	 */
	protected ParsingRulesPanel(LayoutManager lm) {
		super(lm);
	}

	/**
	 * Creates an instance of the supported parsing rules.
	 * @return
	 */
	public abstract ParsingRulesDefinition createRules();

	/**
	 * @param rules rules to edit
	 */
	public abstract void setRules(ParsingRulesDefinition rules);

    /**
     * Stops editing.
     */
    public abstract void stopEditing();

    /**
	 * @return the edited rules or null if the parsing rules are not valid
     * @throws InvalidParsingRules
	 */
	public abstract ParsingRulesDefinition getRules() throws InvalidParsingRules;
}
