/*
 * Created on 19-Aug-2004
 */
package com.ixora.rms.ui.views.session;

import java.awt.Cursor;
import java.awt.Window;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.jobs.UIWorkerJobDefault;
import com.ixora.rms.EntityDescriptorTree;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentId;
import com.ixora.rms.client.model.AgentNode;
import com.ixora.rms.client.model.EntityNode;
import com.ixora.rms.client.model.HostNode;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.client.model.SessionModelTreeNode;
import com.ixora.rms.services.MonitoringSessionService;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.SessionTreeExplorer;
import com.ixora.rms.ui.messages.Msg;

/**
 * @author Daniel Moraru
 */
final class LiveSessionTreeExplorer implements SessionTreeExplorer {
    /** View container */
    private RMSViewContainer viewContainer;
    /** Session */
    private MonitoringSessionService rmsMonitoringSession;

    /**
     * Constructor.
     * @param vc
     * @param mss
     */
    public LiveSessionTreeExplorer(
            RMSViewContainer vc,
            MonitoringSessionService mss) {
        super();
        this.viewContainer = vc;
        this.rmsMonitoringSession = mss;
    }

    /**
     * @see com.ixora.rms.ui.SessionTreeExplorer#expandNode(java.awt.Window, com.ixora.rms.client.model.SessionModel, com.ixora.rms.client.model.SessionModelTreeNode)
     */
    public void expandNode(Window owner, SessionModel model, SessionModelTreeNode node) {
		try {
			// agent node expanded
			if(node instanceof AgentNode) {
				AgentNode an = (AgentNode)node;
				if(an.getChildCount() > 0) {
					// entities already here
					return;
				}
				handleAgentNodeExpanded(owner, model, an);
			// entity node expanded
			} else if (node instanceof EntityNode) {
				EntityNode en = (EntityNode)node;
				if(en.getChildCount() > 0) {
					// entities already here
					return;
				}
				handleEntityNodeExpanded(owner, model, en);
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
    }

	/**
	 * Handles the expansion of an agent node.
	 * @param owner
	 * @param model
	 * @param an
	 */
	private void handleAgentNodeExpanded(final Window owner,
	        final SessionModel model,
	        final AgentNode an) {
		final HostNode hn = (HostNode)an.getParent();
		final AgentId agentId = an.getAgentInfo().getDeploymentDtls().getAgentId();

		final MutableTreeNode n = new DefaultMutableTreeNode("...", false);
		model.insertNodeInto(n, an, an.getChildCount());
		this.viewContainer.getAppWorker().runJob(new UIWorkerJobDefault(
				owner,
				Cursor.WAIT_CURSOR,
				MessageRepository.get(Msg.TEXT_GETTINGAGENTENTITIES)) {
			public void work() throws Exception {
				EntityDescriptorTree entities = rmsMonitoringSession
					.getAgentEntities(hn.getHostInfo().getName(), agentId, null, false, false);
				if(entities == null) {
					return;
				}
				this.fResult = entities;
			}
			public void finished(Throwable ex) {
				if(ex == null) {
					model.removeNodeFromParent(n);
					if(this.fResult == null) {
						return;
					}
					model.updateEntities(
					        hn.getHostInfo().getName(),
					        agentId,
					        (EntityDescriptorTree)this.fResult);
				}
			}
			});
	}

	/**
	 * Handles the expansion of an entity node.
	 * @param owner
	 * @param model
	 * @param an
	 */
	private void handleEntityNodeExpanded(
	        final Window owner,
	        final SessionModel model,
	        final EntityNode en) {
		final String host = en.getAgentNode().getHostNode().getHostInfo().getName();
		final AgentId agentId = en.getAgentNode().getAgentInfo().getDeploymentDtls().getAgentId();
		final EntityId eid = en.getEntityInfo().getId();

		final MutableTreeNode n = new DefaultMutableTreeNode("...", false);
		model.insertNodeInto(n, en, en.getChildCount());
		this.viewContainer.getAppWorker().runJob(new UIWorkerJobDefault(
				owner,
				Cursor.WAIT_CURSOR,
				MessageRepository.get(Msg.TEXT_GETTINGAGENTENTITIES)) {
			public void work() throws Exception {
				EntityDescriptorTree entities = rmsMonitoringSession
					.getAgentEntities(host, agentId, eid, false, false);
				if(entities == null) {
					return;
				}
				this.fResult = entities;
			}
			public void finished(Throwable ex) {
				model.removeNodeFromParent(n);
				if(this.fResult == null) {
					return;
				}
				EntityDescriptorTree entities = (EntityDescriptorTree)
						this.fResult;
				model.updateEntities(host, agentId,
						entities);
			}
			});
	}
}
