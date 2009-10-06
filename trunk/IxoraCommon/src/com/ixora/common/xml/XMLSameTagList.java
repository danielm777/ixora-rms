package com.ixora.common.xml;
import java.util.Iterator;
import java.util.List;

import org.w3c.dom.Node;

import com.ixora.common.xml.exception.XMLException;

/*
 * Created on 27-Nov-2004
 */

/**
 * XMLSameTagList
 */
public class XMLSameTagList extends XMLTagList {
    /** An XMLTag class */
    private Class tagClass;

    public XMLSameTagList(Class c) {
        if (!XMLTag.class.isAssignableFrom(c))
            throw new IllegalArgumentException("Subclass of XMLTag required");
        tagClass = c;
    }

    /**
     * @see XMLExternalizable#fromXML(org.w3c.dom.Node)
     */
    public void fromXML(Node node) throws XMLException {
        super.fromXML(node);

        try {
            // Rather ugly hack: getTagName is not accessible otherwise
            // Daniel: another issue is that the class must be public; is is possible to
            // override specifier access?!
            XMLTag tagDummy = (XMLTag)tagClass.newInstance();

            // Look for children
	        List nodes = XMLUtils.findChildren(node, tagDummy.getTagName());
			for (Iterator it = nodes.iterator(); it.hasNext();) {
			    Node n = (Node) it.next();

                XMLTag tag = (XMLTag)tagClass.newInstance();
	            tag.fromXML(n);

			    this.add(tag);
			}
        } catch (IllegalAccessException e) {
            throw new XMLException(e);
        } catch (InstantiationException e) {
            throw new XMLException(e);
        }
    }

}
