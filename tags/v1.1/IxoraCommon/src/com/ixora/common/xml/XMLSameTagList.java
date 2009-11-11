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
 * @author Cristian Costache
 * @author Daniel Moraru
 */
public class XMLSameTagList<T extends XMLTag> extends XMLTagList<T> {
	private static final long serialVersionUID = -3970763468824453759L;
	/** An XMLTag class */
    private Class<T> tagClass;

    public XMLSameTagList(Class<T> c) {
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
	        List<Node> nodes = XMLUtils.findChildren(node, tagDummy.getTagName());
			for (Iterator<Node> it = nodes.iterator(); it.hasNext();) {
			    Node n = it.next();

                T tag = tagClass.newInstance();
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
