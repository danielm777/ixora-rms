/*
 * Created on 16-Aug-2004
 */
package com.ixora.rms.client.model;

import java.util.List;

import com.ixora.rms.ResourceId;

/**
 * Interface implemented by all the nodes in the
 * monitoring session tree but the scheme node.
 * @author Daniel Moraru
 */
public interface ResourceNode extends SessionModelTreeNode {
    /**
     * Finds resource paths that match the given regular expression
     * resource id that reside bellow this node.
     * @param ridex
     * @param result
     * @param aggresive if true it loads missing children using the monitoring
     * session
     */
    void findPathsMatching(ResourceId ridex, List<ResourcePath> result, boolean aggressive);
    /**
     * @return the resource path that corresponds to this node.
     */
    ResourcePath getResourcePath();
}
