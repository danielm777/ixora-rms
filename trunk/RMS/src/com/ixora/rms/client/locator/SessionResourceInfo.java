/*
 * Created on 05-Jun-2005
 */
package com.ixora.rms.client.locator;

import com.ixora.rms.ResourceId;
import com.ixora.common.MessageRepository;
import com.ixora.rms.EntityId;
import com.ixora.rms.client.model.ResourceInfo;

/**
 * SessionResourceInfo
 * @author Daniel Moraru
 */
public class SessionResourceInfo {
    private ResourceId fResourceId;
    private SessionAgentInfo fAgent;
    private SessionEntityInfo fEntity;
    private SessionCounterInfo fCounter;

    /**
     * @param ri
     */
    public SessionResourceInfo(ResourceInfo ri) {
        fResourceId = ri.getResourceId();
        if(fResourceId != null) {
            int rep = fResourceId.getRepresentation();
            switch(rep) {
            // intentional fall-through case stmt
            case ResourceId.COUNTER:
                fCounter = new SessionCounterInfo(ri);
            case ResourceId.ENTITY:
                fEntity = new SessionEntityInfo(ri);
            case ResourceId.AGENT:
                fAgent = new SessionAgentInfo(ri);
            }
        }
    }

   /**
    * @return agent info
    */
   public SessionAgentInfo getAgentInfo() {
       return fAgent;
   }
   /**
    * @return entity info
    */
    public SessionEntityInfo getEntityInfo() {
       return fEntity;
    }
    /**
    * @return counter info
    */
   public SessionCounterInfo getCounterInfo() {
       return fCounter;
   }

   /**
     * @return Returns the resourceId.
     */
    public ResourceId getResourceId() {
        return fResourceId;
    }

    /**
     * @return the translated resource path
     */
    public String getTranslatedPath() {
        if(fResourceId == null) {
            return null;
        }
        int rep = fResourceId.getRepresentation();
        StringBuffer buff = new StringBuffer();
        switch(rep) {
            case ResourceId.HOST:
                return fResourceId.getHostId().toString();
            case ResourceId.AGENT:
                buff.append(fResourceId.getHostId().toString());
                buff.append(EntityId.DELIMITER);
                String messageRepository = fResourceId.getAgentId().getInstallationId();
                buff.append(MessageRepository.get(messageRepository,
                        fResourceId.getAgentId().getInstallationId()));
                int idx = fResourceId.getAgentId().getInstallationIdx();
                if(idx > 0) {
                    buff.append("(");
                    buff.append(idx);
                    buff.append(")");
                }
                break;
            case ResourceId.ENTITY:
                buff.append(fResourceId.getHostId().toString());
                buff.append(EntityId.DELIMITER);
                buff.append(fAgent.getTranslatedName());
                buff.append(EntityId.DELIMITER);
                buff.append(fEntity.getTranslatedEntityPath());
                break;
            case ResourceId.COUNTER:
                buff.append(fResourceId.getHostId().toString());
                buff.append(EntityId.DELIMITER);
                buff.append(fAgent.getTranslatedName());
                buff.append(EntityId.DELIMITER);
                buff.append(fEntity.getTranslatedEntityPath());
                buff.append(EntityId.DELIMITER);
                buff.append(fCounter.getTranslatedName());
                break;
        }
        return buff.toString();
    }
}
