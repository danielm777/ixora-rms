package com.ixora.common.xml;
import java.io.Serializable;

import org.w3c.dom.Node;

/*
 * Created on 27-Nov-2004
 */

/**
 * XMLTagFactory
 * @author Cristian Costache
 * @author Daniel Moraru
 */
public abstract class XMLTagFactory<T extends XMLTag> implements Serializable {
	private static final long serialVersionUID = 8972236842297479906L;

	/** Override to create different XMLTags based on contents of a Node */
    public abstract T createFromXML(Node n);
}
