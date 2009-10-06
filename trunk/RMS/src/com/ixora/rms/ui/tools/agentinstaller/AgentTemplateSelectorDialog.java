/*
 * Created on Nov 8, 2004
 */
package com.ixora.rms.ui.tools.agentinstaller;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;

import com.ixora.common.MessageRepository;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.common.utils.Utils;
import com.ixora.rms.repository.AgentTemplate;
import com.ixora.rms.ui.tools.agentinstaller.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class AgentTemplateSelectorDialog extends AppDialog {
	private JPanel fPanel;
	private JComboBox jComboBoxTemplates;
    private AgentTemplate fSelectedTemplate;

	/**
	 * Template data.
	 */
	private static final class TemplateData {
	    AgentTemplate fTemplate;
        String fName;

        TemplateData(AgentTemplate temp) {
            super();
            fTemplate = temp;
            fName = MessageRepository.get(AgentInstallerComponent.NAME, fTemplate.getTemplateName());
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return fName;
        }
	}

	/**
	 * Constructor.
	 * @param parent the parent of this dialog
	 * @param templates
	 */
	public AgentTemplateSelectorDialog(Dialog parent, AgentTemplate[] templates) {
		super(parent, VERTICAL);
		if(Utils.isEmptyArray(templates)) {
			throw new IllegalArgumentException("No agent templates");
		}
		setModal(true);
		setTitle(MessageRepository.get(
                AgentInstallerComponent.NAME,
				Msg.AGENTINSTALLER_TITLE_SELECT_TEMPLATE));
		setPreferredSize(new Dimension(300, 120));
        fPanel = new JPanel(new BorderLayout());
		FormPanel formPanel = new FormPanel(FormPanel.VERTICAL2);
        fPanel.add(formPanel, BorderLayout.NORTH);
		jComboBoxTemplates = UIFactoryMgr.createComboBox();

        TemplateData[] model = new TemplateData[templates.length];
        for(int i = 0; i < templates.length; i++) {
            model[i] = new TemplateData(templates[i]);
        }
		jComboBoxTemplates.setModel(new DefaultComboBoxModel(model));

		formPanel.addPairs(
				new String[] {
						MessageRepository.get(AgentInstallerComponent.NAME,
                                Msg.AGENTINSTALLER_SELECT_TEMPLATE),
					},
				new Component[] {
						jComboBoxTemplates
				}
		);

		buildContentPane();
	}

    /**
     * @return the selected template
     */
    public AgentTemplate getSelectedTemplate() {
        return fSelectedTemplate;
    }

	/**
	 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
	 */
	protected Component[] getDisplayPanels() {
		return new Component[] {fPanel};
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getButtons()
	 */
	protected JButton[] getButtons() {
		return new JButton[] {
			new JButton(new ActionOk() {
				public void actionPerformed(ActionEvent e) {
					handleOk();
				}}),
			new JButton(new ActionCancel() {
				public void actionPerformed(ActionEvent e) {
					dispose();
				}
			})};
	}

	/**
	 * Collects return data and closes this dialog.
	 */
	private void handleOk() {
		fSelectedTemplate = ((TemplateData)jComboBoxTemplates.getSelectedItem()).fTemplate;
		this.dispose();
	}
}
