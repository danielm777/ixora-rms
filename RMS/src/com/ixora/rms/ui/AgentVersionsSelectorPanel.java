/*
 * Created on 27-Jun-2005
 */
package com.ixora.rms.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.utils.Utils;

/**
 * AgentVersionsSelectorPanel
 */
public class AgentVersionsSelectorPanel extends JPanel {
	private static final long serialVersionUID = 3797488438571130908L;
	public static final int BUTTON_LAST = 0;
    public static final int BUTTON_FIRST = 1;
    private static final String ALL = "All"; // TODO localize
    private Window fParent;
    private JTextField fAgentVersionsTextField;
    private JButton fButtonAgentVersionsSelector;
    private Set<String> fAgentVersionsList;
    private List<Listener> fListeners;

    /**
     * Listener.
     */
    public interface Listener {
    	/**
    	 * Invoked when the selection has been modified.
    	 */
    	void selectionModified();
    }

    /**
     * @param parent
     * @param agentVersions all agent versions available for selection
     */
    public AgentVersionsSelectorPanel(JFrame parent, Set<String> agentVersions) {
        init(parent, agentVersions, null, BUTTON_LAST);
    }

    /**
     * @param parent
     * @param agentVersions all agent versions available for selection
     */
    public AgentVersionsSelectorPanel(JDialog parent, Set<String> agentVersions) {
        init(parent, agentVersions, null, BUTTON_LAST);
    }

    /**
     * @param parent
     * @param agentVersions all agent versions available for selection
     * @param buttonText
     */
    public AgentVersionsSelectorPanel(JFrame parent, Set<String> agentVersions, String buttonText) {
        init(parent, agentVersions, buttonText, BUTTON_LAST);
    }

    /**
     * @param parent
     * @param agentVersions all agent versions available for selection
     * @param buttonText
     */
    public AgentVersionsSelectorPanel(JDialog parent, Set<String> agentVersions, String buttonText) {
        init(parent, agentVersions, buttonText, BUTTON_LAST);
    }

    /**
     * @param parent
     * @param agentVersions all agent versions available for selection
     * @param buttonText
     */
    public AgentVersionsSelectorPanel(JFrame parent, Set<String> agentVersions, String buttonText, int orientation) {
        init(parent, agentVersions, buttonText, orientation);
    }

    /**
     * @param parent
     * @param agentVersions all agent versions available for selection
     * @param buttonText
     */
    public AgentVersionsSelectorPanel(JDialog parent, Set<String> agentVersions, String buttonText, int orientation) {
        init(parent, agentVersions, buttonText, orientation);
    }

    /**
     * @param parent
     * @param agentVersions
     * @param buttonText
     */
    private void init(Window parent, Set<String> agentVersions, String buttonText, int orientation) {
        setLayout(new BorderLayout());
        fParent = parent;
        fAgentVersionsList = agentVersions;
        fListeners = new LinkedList<Listener>();
        fAgentVersionsTextField = UIFactoryMgr.createTextField();
        fAgentVersionsTextField.setEnabled(false);
        fButtonAgentVersionsSelector = UIFactoryMgr.createButton();
        fButtonAgentVersionsSelector.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 handleSelectAgentVersions();
            }
        });
        if(Utils.isEmptyString(buttonText)) {
            fButtonAgentVersionsSelector.setPreferredSize(new Dimension(16, 16));
            fButtonAgentVersionsSelector.setText("...");
        } else {
            UIUtils.setUsabilityDtls(buttonText, fButtonAgentVersionsSelector);
        }
        if(orientation == BUTTON_LAST) {
            add(fAgentVersionsTextField, BorderLayout.CENTER);
            add(fButtonAgentVersionsSelector, BorderLayout.EAST);
        } else {
            add(fAgentVersionsTextField, BorderLayout.CENTER);
            add(fButtonAgentVersionsSelector, BorderLayout.WEST);
        }
    }

    /**
     * @param l
     */
    public void addListener(Listener l) {
    	if(!fListeners.contains(l)) {
    		fListeners.add(l);
    	}
    }

    /**
     * @param l
     */
    public void removeListener(Listener l) {
    	fListeners.remove(l);
    }

    /**
     * @param agentVersions
     */
    public void setAvailableAgentVersions(Set<String> agentVersions) {
        fAgentVersionsList = agentVersions;
    }

    /**
     * Displays the given agent versions as selected
     * @param versions
     */
    public void setSelectedAgentVersions(Set<String> versions) {
        setAgentVersionsTextFieldText(versions);
    }

    /**
     * @return the selected agent versions
     */
    public Set<String> getSelectedAgentVersions() {
        return getAgentVersionsFromTextField();
    }

    /**
     * @see javax.swing.JComponent#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        fAgentVersionsTextField.setEnabled(enabled);
        fButtonAgentVersionsSelector.setEnabled(enabled);
    }

    /**
     *
     */
    private void handleSelectAgentVersions() {
        try {
            AgentVersionsSelectorDialog dlg;
            if(fParent instanceof JFrame) {
                dlg = new AgentVersionsSelectorDialog(
                    (JFrame)fParent, fAgentVersionsList, getAgentVersionsFromTextField());
            } else {
                dlg = new AgentVersionsSelectorDialog(
                    (JDialog)fParent, fAgentVersionsList, getAgentVersionsFromTextField());
            }
            dlg.setModal(true);
            UIUtils.centerDialogAndShow(fParent, dlg);
            Set<String> versions = dlg.getSelectedVersions();
            if(versions != null) { // dialog not discarded
                setAgentVersionsTextFieldText(versions);
            }
        } catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
    }

    /**
     * @param versions
     */
    private void setAgentVersionsTextFieldText(Set<String> versions) {
        if(Utils.isEmptyCollection(versions)) {
               // clear
               // TODO localize
               fAgentVersionsTextField.setText(ALL);
        } else {
            StringBuffer buff = new StringBuffer();
            int i = 0;
            int size = versions.size();
            for(String s : versions) {
                buff.append(s);
                if(i < size - 1) {
                    buff.append(",");
                }
                ++i;
            }
            fAgentVersionsTextField.setText(buff.toString());
        }
        fireSelectionChanged();
    }

    /**
     * @return
     */
    private Set<String> getAgentVersionsFromTextField() {
        String selected = fAgentVersionsTextField.getText();
        Set<String> selectedLst = null;
        if(!ALL.equals(selected)) {
            if(!Utils.isEmptyString(selected)) {
                selectedLst = new HashSet<String>();
                StringTokenizer tok = new StringTokenizer(selected, ",");
                while(tok.hasMoreTokens()) {
                    String v = tok.nextToken().trim();
                    if(!Utils.isEmptyString(v)) {
                        selectedLst.add(v);
                    }
                }
            }
        }
        return selectedLst;
    }

    /**
     *
     */
    private void fireSelectionChanged() {
    	for(Listener listener : fListeners) {
    		listener.selectionModified();
    	}
    }
}
