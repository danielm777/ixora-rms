package com.ixora.common.xml;
import java.io.Serializable;

import org.w3c.dom.Node;

/*
 * Created on 27-Nov-2004
 */

/**
 * XMLTagFactory
 */
public abstract class XMLTagFactory implements Serializable {

    /** Override to create different XMLTags based on contents of a Node */
    public abstract XMLTag createFromXML(Node n);
}
