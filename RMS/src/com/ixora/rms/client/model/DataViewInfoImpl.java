/*
 * Created on 12-Oct-2004
 */
package com.ixora.rms.client.model;

import java.util.List;

import org.w3c.dom.Node;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.dataengine.definitions.ReactionDef;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.repository.VersionableAgentArtefact;

/**
 * Implementation for a data view stored in the repository.
 * @author Daniel Moraru
 */
final class DataViewInfoImpl extends ArtefactInfoImpl
		implements DataViewInfo {
    /** DataView */
    private DataView dataView;

    /**
     * Constructor.
     * @param messageRepository
     * @param dv
     * @param model
     */
    public DataViewInfoImpl(String messageRepository, DataView dv, SessionModel model) {
        super(messageRepository, dv.getName(), dv.getDescription(), model);
        this.dataView = dv;
    }

    /**
     * @see com.ixora.rms.client.model.DataViewInfo#getDataView()
     */
    public DataView getDataView() {
        return dataView;
    }

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#toXML(org.w3c.dom.Node)
	 */
	public void toXML(Node parent) throws XMLException {
	}

	/**
	 * @see com.ixora.common.xml.XMLExternalizable#fromXML(org.w3c.dom.Node)
	 */
	public void fromXML(Node node) throws XMLException {
	}

    /**
     * @see com.ixora.rms.client.model.ArtefactInfo#getVersionableAgentArtefact()
     */
    public VersionableAgentArtefact getVersionableAgentArtefact() {
        return dataView;
    }

	/**
	 * @see com.ixora.rms.client.model.DataViewInfo#hasReactions()
	 */
	public boolean hasReactions() {
		List<ReactionDef> reactions = this.dataView.getQueryDef().getReactions();
		return !Utils.isEmptyCollection(reactions);
	}
}
