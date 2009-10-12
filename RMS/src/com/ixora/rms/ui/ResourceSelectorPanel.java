/*
 * Created on 27-Jun-2005
 */
package com.ixora.rms.ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.ixora.rms.ResourceId;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.utils.Utils;
import com.ixora.rms.client.model.SessionModel;

/**
 * AgentVersionsSelectorPanel
 */
public class ResourceSelectorPanel extends JPanel {
	private static final long serialVersionUID = 1780338494542393743L;
	public static final int BUTTON_LAST = 0;
    public static final int BUTTON_FIRST = 1;
    public static final int SELECT_ENTITY = 0;
    public static final int SELECT_COUNTER = 1;
    private Window fParent;
    private JTextField fResourceTextField;
    private JButton fButton;
    private List<Listener> fListeners;
    private SessionTreeExplorer fTreeExplorer;
    private SessionModel fModel;
    private ResourceId fContext;
    private int fSelectType;

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
     */
    public ResourceSelectorPanel(JFrame parent, int selectType, SessionTreeExplorer explorer, SessionModel model, ResourceId context) {
        init(parent, selectType, BUTTON_LAST, explorer, model, context);
    }

    /**
     * @param parent
     */
    public ResourceSelectorPanel(JDialog parent, int selectType, SessionTreeExplorer explorer, SessionModel model, ResourceId context) {
        init(parent, selectType, BUTTON_LAST, explorer, model, context);
    }

    /**
     * @param parent
     * @param orientation
     */
    public ResourceSelectorPanel(JFrame parent, int selectType, int orientation, SessionTreeExplorer explorer, SessionModel model, ResourceId context) {
        init(parent, selectType, orientation, explorer, model, context);
    }

    /**
     * @param parent
     * @param orientation
     */
    public ResourceSelectorPanel(JDialog parent, int selectType, int orientation, SessionTreeExplorer explorer, SessionModel model, ResourceId context) {
        init(parent, selectType, orientation, explorer, model, context);
    }

    /**
     * @param parent
     * @param orientation
     */
    private void init(Window parent, int selectType, int orientation, SessionTreeExplorer explorer, SessionModel model, ResourceId context) {
        setLayout(new BorderLayout());
        fParent = parent;
        fTreeExplorer = explorer;
        fModel = model;
        fContext = context;
        fSelectType = selectType;
        fListeners = new LinkedList<Listener>();
        fResourceTextField = UIFactoryMgr.createTextField();
        if(context != null) {
        	fResourceTextField.setText(context.getRelativeTo(context).toString());
        }
        fResourceTextField.setEnabled(true);
        fButton = UIFactoryMgr.createButton();
        fButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                 handleSelect();
            }
        });
        fButton.setPreferredSize(new Dimension(16, 16));
        fButton.setText("...");
        if(orientation == BUTTON_LAST) {
            add(fResourceTextField, BorderLayout.CENTER);
            add(fButton, BorderLayout.EAST);
        } else {
            add(fResourceTextField, BorderLayout.CENTER);
            add(fButton, BorderLayout.WEST);
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
     * @see javax.swing.JComponent#setEnabled(boolean)
     */
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        fResourceTextField.setEnabled(enabled);
        fButton.setEnabled(enabled);
    }

    /**
     * @return
     */
    public ResourceId getResourceId() {
    	String txt = fResourceTextField.getText().trim();
    	if(!Utils.isEmptyString(txt)) {
    		return new ResourceId(txt);
    	}
    	return null;
    }

    /**
     *
     */
    private void handleSelect() {
        try {
            ResourceSelectorDialog dlg;
            if(fParent instanceof JFrame) {
                dlg = new ResourceSelectorDialog(
                    (JFrame)fParent, fSelectType, fTreeExplorer, fModel, fContext, true);
            } else {
                dlg = new ResourceSelectorDialog(
                    (JDialog)fParent, fSelectType, fTreeExplorer, fModel, fContext, true);
            }
            dlg.setModal(true);
            UIUtils.centerDialogAndShow(fParent, dlg);
            List<ResourceId> rids = dlg.getResult();
            if(!Utils.isEmptyCollection(rids)) {
            	ResourceId rid = rids.get(0);
            	fResourceTextField.setText(rid.getRelativeTo(fContext).toString());
                fireSelectionChanged();
            }
        } catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
    }

    /**
     * @return
     */
    public JTextField getResourceIdTextField() {
        return fResourceTextField;
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
