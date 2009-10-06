/**
 * 02-Jan-2006
 */
package com.ixora.rms.ui.artefacts.dataview.wizard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.common.ui.wizard.Wizard;
import com.ixora.common.ui.wizard.WizardStep;
import com.ixora.common.utils.Utils;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.ui.AgentVersionsSelectorPanel;

/**
 * @author Daniel Moraru
 */
public class WizardStep3 extends WizardStep {

	public static class Data {
		WizardStep2.Data fStep2;
		String fName;
		String fDescription;
		String fAuthor;
		Set<String> fAgentVersions;

		Data(WizardStep2.Data step2, String name, String description, String author, Set<String> agentVersions) {
			fStep2 = step2;
			fName = name;
			fDescription = description;
			fAuthor = author;
			fAgentVersions = agentVersions;
		}
	}
	private WizardStep2.Data fStep2;
	private JTextField fTextFieldName;
	private JTextField fTextFieldDescription;
	private JTextField fTextFieldAuthor;
	private AgentVersionsSelectorPanel fAgentVersionsPanel;

	public WizardStep3(Wizard dlg, Set<String> agentVersions, String currentAgentVersion) {
		super("Data view definition", "Set the properties for this data view");
		setLayout(new BorderLayout());
		fTextFieldName = UIFactoryMgr.createTextField();
		fTextFieldDescription = UIFactoryMgr.createTextField();
		fTextFieldAuthor = UIFactoryMgr.createTextField();
		fAgentVersionsPanel = new AgentVersionsSelectorPanel(dlg, agentVersions);
		if(!Utils.isEmptyCollection(agentVersions)) {
			Set<String> set = new HashSet<String>();
			set.add(currentAgentVersion);
			fAgentVersionsPanel.setSelectedAgentVersions(set);
		}

		FormPanel form = new FormPanel(FormPanel.VERTICAL2);
		form.addPairs(
			new String[]{
				"Name",
				"Description",
				"Author",
				"Assign Agent Versions"
			},
			new Component[]{
				fTextFieldName,
				fTextFieldDescription,
				fTextFieldAuthor,
				fAgentVersionsPanel
			}
		);
		form.setPreferredSize(new Dimension(300, form.getPreferredSize().height));
		JPanel panel = new JPanel(new BorderLayout());
		panel.add(form, BorderLayout.WEST);
		add(panel, BorderLayout.NORTH);
		if(Utils.isEmptyCollection(agentVersions)) {
			form.setEnabled("Assign Agent Versions", false);
		}
	}

	/**
	 * @see com.ixora.common.ui.wizard.WizardStep#activate(java.lang.Object)
	 */
	public void activate(Object obj) throws Exception {
		fStep2 = (WizardStep2.Data)obj;
	}

	/**
	 * @see com.ixora.common.ui.wizard.WizardStep#deactivate()
	 */
	public Object deactivate() throws Exception {
		String name = fTextFieldName.getText().trim();
		if(Utils.isEmptyString(name)) {
			throw new RMSException("A name for the data view must be provided");
		}
		String desc = fTextFieldDescription.getText().trim();
		return new Data(fStep2,
				name,
				desc,
				fTextFieldAuthor.getText().trim(),
				fAgentVersionsPanel.getSelectedAgentVersions());
	}
}
