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
import com.ixora.common.Product;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.rms.ui.tools.agentinstaller.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class AgentInstallationTypeSelectorDialog extends AppDialog {
	public static final int CUSTOM_AGENT = 1;
	public static final int PACKAGED_AGENT = 2;
	public static final int OTHER_PRODUCT_INSTANCE_AGENT = 3;
	private JPanel fPanel;
	private JComboBox jComboBoxType;
    private int fSelectedType;

	/**
	 * Type data.
	 */
	private static final class TypeData {
	    int fType;
        String fTypeName;

        TypeData(int type, String name) {
            super();
            fType = type;
            fTypeName = name;
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return fTypeName;
        }
	}

	/**
	 * Constructor.
	 * @param parent the parent of this dialog
	 */
	public AgentInstallationTypeSelectorDialog(Dialog parent) {
		super(parent, VERTICAL);
		setModal(true);
		setTitle(MessageRepository.get(
                AgentInstallerComponent.NAME,
				Msg.AGENTINSTALLER_TITLE_SELECT_INSTALLATION_TYPE));
		setPreferredSize(new Dimension(300, 120));
        fPanel = new JPanel(new BorderLayout());
		FormPanel formPanel = new FormPanel(FormPanel.VERTICAL2);
        fPanel.add(formPanel, BorderLayout.NORTH);
		jComboBoxType = UIFactoryMgr.createComboBox();

        TypeData[] model = new TypeData[]{
        		new TypeData(CUSTOM_AGENT,
        				MessageRepository.get(AgentInstallerComponent.NAME,
        						Msg.TEXT_CUSTOM_AGENT_INSTALLTION)),
        		new TypeData(PACKAGED_AGENT,
        				MessageRepository.get(AgentInstallerComponent.NAME,
        						Msg.TEXT_PACKAGED_AGENT_INSTALLATION)),
        		new TypeData(OTHER_PRODUCT_INSTANCE_AGENT,
        				MessageRepository.get(AgentInstallerComponent.NAME,
        						Msg.TEXT_OTHER_PRODUCT_INSTANCE_AGENT_INSTALLATION,
        						new String[]{Product.getProductInfo().getName()}))
        };
		jComboBoxType.setModel(new DefaultComboBoxModel(model));

		formPanel.addPairs(
				new String[] {
						MessageRepository.get(AgentInstallerComponent.NAME,
                                Msg.AGENTINSTALLER_SELECT_INSTALLATION_TYPE),
					},
				new Component[] {
						jComboBoxType
				}
		);

		buildContentPane();
	}

    /**
     * @return the selected value
     */
    public int getSelectedInstallationType() {
        return fSelectedType;
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
		fSelectedType = ((TypeData)jComboBoxType.getSelectedItem()).fType;
		this.dispose();
	}
}
