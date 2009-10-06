/*
 * Created on 22-Mar-2005
 */
package com.ixora.rms.ui;

import javax.swing.Icon;
import javax.swing.JLabel;

import com.ixora.common.ui.CompositeIcon;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.rms.client.model.AgentNode;
import com.ixora.rms.client.model.EntityNode;
import com.ixora.rms.client.model.SessionModelTreeNode;

/**
 * @author Daniel Moraru
 */
public class SessionModelTreeNodeDecoratorImpl implements SessionModelTreeNodeDecorator {
	private static Icon iconClean = UIConfiguration.getIcon("dec_clean.gif");
	private static Icon iconEnabled = UIConfiguration.getIcon("dec_en.gif");
	private static Icon iconDataViews = UIConfiguration.getIcon("dec_dv.gif");
	private static Icon iconDashboards = UIConfiguration.getIcon("dec_db.gif");
	private static Icon iconDataViewsAndDashboards = UIConfiguration.getIcon("dec_dv_db.gif");
	private static Icon iconWithReactions = UIConfiguration.getIcon("dec_reactions.gif");
	private static Icon refreshSchedule = UIConfiguration.getIcon("refresh_sched.gif");

	/**
	 * Constructor.
	 *
	 */
	public SessionModelTreeNodeDecoratorImpl() {
		super();
	}

	/**
	 * @see com.ixora.rms.ui.SessionModelTreeNodeDecorator#decorate(com.ixora.rms.client.model.SessionModelTreeNode, javax.swing.JLabel)
	 */
	public void decorate(SessionModelTreeNode node, Icon itemIcon, boolean showCheckbox, JLabel label) {
	    // TODO cache this, one instance is enough
		CompositeIcon compositeIcon = new CompositeIcon();

	    boolean hasDataViews = node.getArtefactInfoContainer().hasDataViews();
	    boolean hasDashboards = node.getArtefactInfoContainer().hasDashboards();
		boolean hasReactions = node.getArtefactInfoContainer().hasDataViewsWithReactions();

	    // Place the checkbox (if visible) before the item icon
	    if (showCheckbox) {
		    boolean hasCheckbox = node.hasEnabledDescendants();
            if(hasCheckbox) {
                compositeIcon.add(iconEnabled);
            } else {
                compositeIcon.add(iconClean);
            }
	    }

	    // Place the item icon, if present
	    if (itemIcon != null) {
            compositeIcon.add(itemIcon);
	    }

	    if (hasDataViews) {
	        if (hasDashboards){
	            compositeIcon.add(iconDataViewsAndDashboards);
	        } else {
	            compositeIcon.add(iconDataViews);
	        }
	    } else {
	        if (hasDashboards){
	            compositeIcon.add(iconDashboards);
	        } else {
	            // don't add anything
	        }
	    }
	    if(hasReactions) {
	    	compositeIcon.add(iconWithReactions);
	    }
	    if(node instanceof EntityNode) {
	    	EntityNode en = (EntityNode)node;
	    	Integer interval = en.getEntityInfo().getConfiguration().getRefreshInterval();
	    	if(interval != null && interval > 0) {
	    		compositeIcon.add(refreshSchedule);
	    	}
	    } else if(node instanceof AgentNode) {
	    	AgentNode an = (AgentNode)node;
	    	Integer interval = an.getAgentInfo().getDeploymentDtls().getConfiguration().getRefreshInterval();
	    	if(interval != null && interval > 0) {
	    		compositeIcon.add(refreshSchedule);
	    	}
	    }
	    label.setIcon(compositeIcon);
	}
}
