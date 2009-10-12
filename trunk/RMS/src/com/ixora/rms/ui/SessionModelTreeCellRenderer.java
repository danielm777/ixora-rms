/*
 * Created on 16-Dec-2003
 */
package com.ixora.rms.ui;

import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.ixora.rms.HostReachability;
import com.ixora.rms.HostState;
import com.ixora.common.remote.ServiceState;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.utils.Utils;
import com.ixora.rms.agents.AgentState;
import com.ixora.rms.client.model.AgentNode;
import com.ixora.rms.client.model.EntityNode;
import com.ixora.rms.client.model.HostNode;
import com.ixora.rms.client.model.SessionNode;

/**
 * Session model tree cell renderer.
 * @author Daniel Moraru
 */
public class SessionModelTreeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = -7444191830214954459L;
	protected ImageIcon schemeIcon;
	protected ImageIcon hostIcon;
	protected ImageIcon agentNotRunningIcon;
	protected ImageIcon agentRunningIcon;
	protected ImageIcon agentErrorIcon;
	protected ImageIcon agentNonFatalErrorIcon;
	/** Renderer/editor for the entity nodes */
	protected EntityNodeCellRenderer entityRenderer;
	/** Node decorator */
	protected SessionModelTreeNodeDecorator nodeDecorator;

	/**
	 * Constructor.
	 */
	public SessionModelTreeCellRenderer() {
		super();
		this.schemeIcon = UIConfiguration.getIcon("session.gif");
		this.hostIcon = UIConfiguration.getIcon("host.gif");
		this.agentNotRunningIcon = UIConfiguration.getIcon("agent_stopped.gif");
		this.agentRunningIcon = UIConfiguration.getIcon("agent_running.gif");
		this.agentErrorIcon = UIConfiguration.getIcon("agent_error.gif");
		this.agentNonFatalErrorIcon = UIConfiguration.getIcon("agent_non_fatal_error.gif");
		this.entityRenderer = new EntityNodeCellRenderer();
		nodeDecorator = new SessionModelTreeNodeDecoratorImpl();
	}

	/**
	 * @see javax.swing.tree.TreeCellRenderer#getTreeCellRendererComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	public Component getTreeCellRendererComponent(
		JTree tree,
		Object value,
		boolean sel,
		boolean expanded,
		boolean leaf,
		int row,
		boolean hasFocus) {
		super.getTreeCellRendererComponent(
									tree,
									value,
									sel,
									expanded,
									leaf,
									row,
									hasFocus);
		setFont(getFont().deriveFont(Font.PLAIN));
		if(value instanceof SessionNode) {
			SessionNode node = (SessionNode)value;
			nodeDecorator.decorate(node, this.schemeIcon, false, this);
			setFont(getFont().deriveFont(Font.BOLD));
			setText(node.toString().toUpperCase());
			setToolTipText(getText());
		} else if(value instanceof HostNode) {
			HostNode node = (HostNode)value;
			nodeDecorator.decorate(node, this.hostIcon, false, this);
			HostState state = node.getHostInfo().getState();
			if(state.getServiceState(HostReachability.ICMP_PING) == ServiceState.ONLINE) {

			}
			if(state.getServiceState(HostReachability.HOST_MANAGER) == ServiceState.ONLINE) {

			}
			setToolTipText(getText());
		} else if(value instanceof AgentNode) {
			setFont(getFont().deriveFont(Font.BOLD));
			Icon itemIcon = null;
			AgentNode an = (AgentNode)value;
			AgentState state = an.getAgentInfo().getAgentState();
			if(state == AgentState.STARTED) {
			    itemIcon = this.agentRunningIcon;
			} else if(state == AgentState.ERROR) {
			    itemIcon = this.agentErrorIcon;
			} else {
			    itemIcon = this.agentNotRunningIcon;
			}
			if(an.getAgentInfo().getErrorStateException() != null) {
			    itemIcon = this.agentNonFatalErrorIcon;
			}
			nodeDecorator.decorate(an, itemIcon, false, this);
            if(!an.getAgentInfo().showIdentifiers()) {
                // append agent version to name
                String suoVersion = an.getAgentInfo().getDeploymentDtls().getConfiguration().getSystemUnderObservationVersion();
                if(!Utils.isEmptyString(suoVersion)) {
                    setText(getText() + " (" + suoVersion + ")");
                }
            }
			setToolTipText(getText());
		} else if(value instanceof EntityNode) {
			return this.entityRenderer.getTreeCellRendererComponent(
												tree,
												value,
												sel,
												expanded,
												leaf,
												row,
												hasFocus);
		} else {
			setIcon(null);
		}
		return this;
	}
}
