/*
 * Created on 07-Mar-2005
 */
package com.ixora.rms.client.locator;

import com.ixora.rms.ResourceId;
import com.ixora.common.MessageRepository;
import com.ixora.rms.EntityId;
import com.ixora.rms.client.model.ResourceInfo;

/**
 * @author Daniel Moraru
 */
public class SessionEntityInfo {
	private String fTranslatedEntityPath;
	private String[] fTranslatedEntityPathFragments;

	/**
	 * Constructor.
	 * @param entityInfo
	 */
	public SessionEntityInfo(ResourceInfo entityInfo) {
		super();
		fTranslatedEntityPath = entityInfo.getEntityInfo().getTranslatedPath();
		fTranslatedEntityPathFragments = entityInfo.getEntityInfo().getTranslatedPathFragments();
	}

	/**
	 * Constructor.
	 * @param rid
	 * @deprecated this should not be used as it doesn;t work for entities
	 * with alternate names
	 */
	public SessionEntityInfo(ResourceId rid) {
		super();
		String messageRepository = rid.getAgentId().getInstallationId();
		String[] path = rid.getEntityId().getPathComponents();
		fTranslatedEntityPathFragments = new String[path.length];
		StringBuffer buff = new StringBuffer();
		for(int i = 0; i < path.length; i++) {
			String t = MessageRepository.get(messageRepository, path[i]);
			fTranslatedEntityPathFragments[i] = t;
            buff.append(t);
            if(i != path.length - 1) {
                buff.append(EntityId.DELIMITER);
            }
        }
		fTranslatedEntityPath = buff.toString();
	}

	/**
	 * @return the translated entity path
	 */
	public String getTranslatedEntityPath() {
		return fTranslatedEntityPath;
	}

	/**
	 * @return all path fragments translated
	 */
	public String[] getTranslatedEntityPathFragments() {
		return fTranslatedEntityPathFragments;
	}
}
