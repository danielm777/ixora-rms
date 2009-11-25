/**
 * 
 */
package com.ixora.rms.ui.tools.agentinstaller;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JButton;

import com.ixora.common.MessageRepository;
import com.ixora.common.typedproperties.PropertyEntry;
import com.ixora.common.typedproperties.ui.TypedPropertiesEditor;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.common.utils.Utils;
import com.ixora.rms.CustomConfiguration;
import com.ixora.rms.ui.tools.agentinstaller.messages.Msg;

/**
 * @author Daniel Moraru
 */
public class AgentConfigurationDefaultsDialog extends AppDialog {
	private static final long serialVersionUID = -5689566078673750879L;
	private Map<String, String> fResult;
	private TypedPropertiesEditor fEditor;
	private CustomConfiguration fConfig;
	
	public AgentConfigurationDefaultsDialog(Dialog parent, String agentId, CustomConfiguration customConfig) {
		super(parent, AppDialog.VERTICAL);
		setModal(true);
		setTitle(MessageRepository.get(AgentInstallerComponent.NAME, Msg.TITLE_AGENT_CONFIGURATION_DEFAULTS));
		this.fEditor = new TypedPropertiesEditor(true);
		this.fEditor.setTypedProperties(agentId, customConfig);
		this.fConfig = customConfig;
		this.fEditor.setPreferredSize(new Dimension(400, 400));
		buildContentPane();
	}
	
	public Map<String, String> getResult() {
		return fResult;
	}
	
	/**
	 * @see com.ixora.common.ui.AppDialog#getButtons()
	 */
	@SuppressWarnings("serial")
	protected JButton[] getButtons() {
		return new JButton[]{
				new JButton(new ActionOk() {
					public void actionPerformed(ActionEvent e) {
						setUpResult();
						dispose();
					}}),
				new JButton(new ActionCancel(){
					public void actionPerformed(ActionEvent e) {
						fResult = null;
						dispose();
					}
				})};
	}

	private void setUpResult() {
		fEditor.stopEditing();
		fResult = new HashMap<String, String>();
		for(Map.Entry<String, PropertyEntry<?>> entry : fConfig.getEntries().entrySet()){
			Object val = entry.getValue().getValue();
			if(val != null) {
				String vals = val.toString();
				if(!Utils.isEmptyString(vals)) {
					fResult.put(entry.getKey(), val.toString());
				}
			}
		}
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
	 */
	protected Component[] getDisplayPanels() {
		return new Component[]{fEditor};
	}
}
