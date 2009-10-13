/*
 * Created on 13-Jan-2004
 */
package com.ixora.common.xml;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.xerces.util.XMLChar;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.common.xml.exception.XMLNodeMissing;

/**
 * @author Daniel Moraru
 */
public final class XMLUtils {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(XMLUtils.class);
	/**
	 * This is the encoding used by this class
	 * to writer xml documents. This value is specified
	 * in the identity.xsl file.
	 */
	public static final String ENCODING = "UTF-8";
	/**
	 * Constructor.
	 */
	private XMLUtils() {
		super();
	}

	/**
	 * Reads an XML into a DOM.
	 * @param is
	 * @return
	 */
	public static Document read(InputStream is) throws XMLException {
		if(is == null) {
			throw new IllegalArgumentException("null input stream");
		}
		Document document;
		DocumentBuilderFactory factory =
		DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.parse(is);
		} catch(SAXException sxe) {
			if(sxe.getException() != null) {
				throw new XMLException(sxe.getException());
			}
			throw new XMLException(sxe);
		} catch(ParserConfigurationException pce) {
			throw new XMLException(pce);
		} catch (IOException ioe) {
			throw new XMLException(ioe);
		}
		return document;
	}

	/**
	 * Writes the given DOM to the given output stream.
	 * @param document
	 * @param os
	 * @throws XMLException
	 */
	public static void write(Document document, OutputStream os) throws XMLException {
		write(document, os, XMLUtils.class.getResourceAsStream("/com/ixora/common/xml/identity.xsl"));
	}

	/**
	 * Writes the given DOM to the given output stream.
	 * @param document
	 * @param os
	 * @param isStyle
	 * @throws XMLException
	 */
	public static void write(Document document, OutputStream os, InputStream isStyle) throws XMLException {
		try {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer;
			transformer = factory.newTransformer(new StreamSource(isStyle));
			DOMSource source = new DOMSource(document);
			StreamResult result = new StreamResult(os);
			transformer.transform(source, result);
		} catch(TransformerConfigurationException tce) {
			if(tce.getException() != null) {
				throw new XMLException(tce.getException());
			}
			throw new XMLException(tce);
		} catch(TransformerException te) {
			if(te.getException() != null) {
				throw new XMLException(te.getException());
			}
			throw new XMLException(te);
		}
	}

	/**
	 * @param rootElementName if not null a root element with the given
	 * name will be created as well
	 * @return an empty DOM
	 * @throws XMLException
	 */
	public static Document createEmptyDocument(String rootElementName) throws XMLException {
		DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
		DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			throw new XMLException(e);
		}
		Document ret = builder.newDocument();
		if(rootElementName != null) {
			ret.appendChild(ret.createElement(rootElementName));
		}
		return ret;
	}

	/**
	 * @param node
	 * @param name
	 * @return the first child node with the given name or <code>null</code>
	 * if none found
	 */
	public static Node findChild(Node node, String name) {
		NodeList nl = node.getChildNodes();
		int len = nl.getLength();
		Node child;
		for(int i = 0; i < len; i++) {
			child = nl.item(i);
			if(name.equals(child.getNodeName())) {
				return child;
			}
		}
		return null;
	}

	/**
	 * @param node
	 * @param name
	 * @return all children nodes with the given name
	 */
	public static List<Node> findChildren(Node node, String name) {
		List<Node> ret = new LinkedList<Node>();
		NodeList nl = node.getChildNodes();
		int len = nl.getLength();
		Node child;
		for(int i = 0; i < len; i++) {
			child = nl.item(i);
			if(name.equals(child.getNodeName())) {
				ret.add(child);
			}
		}
		return ret;
	}

	/**
	 * @param node
	 * @param name
	 * @return all children nodes whose names contain <code>like</code>
	 */
	public static List<Node> findChildrenLike(Node node, String like) {
		List<Node> ret = new LinkedList<Node>();
		NodeList nl = node.getChildNodes();
		int len = nl.getLength();
		Node child;
		for(int i = 0; i < len; i++) {
			child = nl.item(i);
			if(child.getNodeName().indexOf(like) > 0) {
				ret.add(child);
			}
		}
		return ret;
	}

	/**
	 * @param node
	 * @param name
	 * @return a map with all children nodes with the given name
	 * mapped by the value of their first child
	 */
	public static Map<String, Node> findChildrenMapByFirstChild(Node node, String name) {
		Map<String, Node> ret = new HashMap<String, Node>();
		NodeList nl = node.getChildNodes();
		int len = nl.getLength();
		Node child;
		for(int i = 0; i < len; i++) {
			child = nl.item(i);
			if(name.equals(child.getNodeName())) {
				ret.put(child.getFirstChild().getNodeValue(), child);
			}
		}
		return ret;
	}

	/**
	 * @param node node
	 * @param name attribute name
	 * @return the attribute with the given name or <code>null</code>
	 * if none found
	 */
	public static Attr findAttribute(Node node, String name) {
		NamedNodeMap attrs = node.getAttributes();
		if(attrs == null) {
			return null;
		}
		return (Attr)attrs.getNamedItem(name);
	}

	/**
	 * Returns the text associated to the given node
	 * i.e. the value of the
	 * @param node
	 * @return
	 */
	public static String getText(Node node) {
	    Node textNode = node.getFirstChild();
	    if(textNode == null) {
	    	return null;
	    }
	    short nodeType = textNode.getNodeType();
	    if(nodeType != Node.TEXT_NODE
	    		&& nodeType != Node.CDATA_SECTION_NODE) {
	        return null;
	    }
	    return textNode.getNodeValue().trim();
	}

	/**
	 * @param node
	 * @return
	 */
	public static Element getFirstElement(Node node) {
		NodeList children = node.getChildNodes();
		if(children == null || children.getLength() == 0) {
			return null;
		}
		int len = children.getLength();
		for(int i = 0; i < len; i++) {
			Node n = children.item(i);
			if(n instanceof Element) {
				return (Element)n;
			}
		}
		return null;
	}

	/**
	 * @param node
	 * @return
	 */
	public static Element getLastElement(Node node) {
		NodeList children = node.getChildNodes();
		if(children == null || children.getLength() == 0) {
			return null;
		}
		int len = children.getLength();
		for(int i = len - 1; i >= 0; i--) {
			Node n = children.item(i);
			if(n instanceof Element) {
				return (Element)n;
			}
		}
		return null;
	}

	/**
	 * @param defaultClass @see writeObject methods
	 * @param rootTag
	 * @param obj
	 * @param xmlHeader if false the xml header will be removed from the resulted buffer
	 * @return the xml text of <code>obj</code>
	 * @throws XMLException
	 */
	public static StringBuffer toXMLBuffer(
			Class<? extends XMLExternalizable> defaultClass,
			XMLExternalizable obj,
			String rootTag,
			boolean xmlHeader) throws XMLException {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Document doc = XMLUtils.createEmptyDocument(rootTag);
		XMLUtils.writeObject(defaultClass, doc.getDocumentElement(), obj);
		XMLUtils.write(doc, os);
		try {
			StringBuffer buff = new StringBuffer(
					os.toString(XMLUtils.ENCODING));
			if(xmlHeader) {
				return buff;
			}
			// remove header
			int idx = buff.indexOf(">");
			if(idx < 0) {
				return buff;
			}
			while(idx < buff.length()
					&& Character.isWhitespace(buff.charAt(idx + 1))) {
				idx++;
			}
			return buff.delete(0, idx + 1);
		} catch(UnsupportedEncodingException e) {
			throw new XMLException(e);
		}
	}

	/**
	 * @param defaultClass @see writeObjects methods
	 * @param buff
	 * @param parentNode the name of the node on the document in <code>buff</code> where to start
	 * reading from; if null the root of the document will be used
	 * @return
	 * @throws XMLException
	 */
	public static XMLExternalizable fromXMLBuffer(
			Class<? extends XMLExternalizable> defaultClass,
			StringBuffer buff, String parentNode) throws XMLException {
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(
					buff.toString().getBytes(XMLUtils.ENCODING));
			Document doc = XMLUtils.read(is);
			Node node;
			if(parentNode != null) {
				node = XMLUtils.findChild(doc.getDocumentElement(), parentNode);
				if(node == null) {
					throw new XMLNodeMissing(parentNode);
				}
			} else {
				node = doc.getDocumentElement();
			}
			return XMLUtils.readObject(defaultClass, node);
		} catch(XMLException e) {
			throw e;
		} catch(Exception e) {
			throw new XMLException(e);
		}
	}

	/**
	 * Creates a document from the given xml string.
	 * @param xml
	 * @return
	 * @throws XMLException
	 */
	public static Document createDocument(String xml) throws XMLException {
		ByteArrayInputStream is;
		try {
			is = new ByteArrayInputStream(
					xml.getBytes(XMLUtils.ENCODING));
			return XMLUtils.read(is);
		} catch (UnsupportedEncodingException e) {
			throw new XMLException(e);
		}
	}

	/**
	 * Finds children whose attribute with name <code>attrName</code>
	 * whose value is <code>attrValue</code>.
	 * @param node
	 * @param childName
	 * @param attrName
	 * @param attrValue
	 * @return
	 */
	public static List<Node> findChildrenWithAttribute(
			Node node,
			String childName,
			String attrName,
			String attrValue) {
		NodeList nl = node.getChildNodes();
		int len = nl.getLength();
		List<Node> ret = new LinkedList<Node>();
		Node n;
		Attr a;
		for(int i = 0; i < len; i++) {
			n = nl.item(i);
			if(n.getNodeName().equals(childName)) {
				a = XMLUtils.findAttribute(n, attrName);
				if(a != null && a.getValue().equals(attrValue)) {
					ret.add(n);
				}
			}
		}
		return ret;
	}

	/**
	 * Loads XMLExternalizable objects from an XML document like:
	 * <pre>
	 * <node>
	 * 	<subnode class="com.some.class">
	 * 	</subnode>
	 * 	<subnode class="com.some.other.class">
	 * 	</subnode>
	 *  <subnode></subnode>
	 * </node>
	 * </pre>
	 * @param defaultClass
	 * @param node
	 * @param subnode
	 * @return
	 * @throws XMLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static XMLExternalizable[] readObjects(
			Class<?> defaultClass,
			Node node,
			String subnode) throws
				XMLException,
				InstantiationException,
				IllegalAccessException,
				ClassNotFoundException {
		List<Node> elements = XMLUtils.findChildren(node, subnode);
		if(elements.size() == 0) {
			return null;
		}
		List<XMLExternalizable> ret = new ArrayList<XMLExternalizable>(elements.size());
		XMLExternalizable ext;
		Node element;
		for(Iterator<Node> iter = elements.iterator(); iter.hasNext();) {
			element = iter.next();
			// search for a class attribute
			Attr a = XMLUtils.findAttribute(element, "class");
			if(a == null) {
				// use the default class
				ext = (XMLExternalizable)defaultClass.newInstance();
			} else {
				Class<?> c = Utils.getClassLoader(XMLUtils.class).loadClass(a.getValue());
				ext = (XMLExternalizable)c.newInstance();
			}
			ext.fromXML(element);
			ret.add(ext);
		}
		return ret.toArray(
				new XMLExternalizable[ret.size()]);
	}

	/**
	 * Loads XMLExternalizable objects from an XML document like:
	 * <node class="com.some.class">
	 * </node>
	 * @param defaultClass default class used if "class" attribute is missing
	 * @param node
	 * @return
	 * @throws XMLException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws ClassNotFoundException
	 */
	public static XMLExternalizable readObject(
			Class<? extends XMLExternalizable> defaultClass,
			Node node) throws
				XMLException,
				InstantiationException,
				IllegalAccessException,
				ClassNotFoundException {
		// search for a class attribute
	    XMLExternalizable ext;
	    Attr a = XMLUtils.findAttribute(node, "class");
		if(a == null) {
			if(defaultClass == null) {
				throw new XMLException("Invalid XML definition: class information is missing");
			}
			// use the default class
			ext = defaultClass.newInstance();
		} else {
			Class<?> c = Utils.getClassLoader(XMLUtils.class).loadClass(a.getValue());
			ext = (XMLExternalizable)c.newInstance();
		}
		ext.fromXML(node);
		return ext;
	}

	/**
	 * Writes the given objects into XML in a form suitable
	 * to be read back by <code>loadObjects</code>.<br>
	 * @param defaultClass default class
	 * @param parent
	 * @param objs
	 * @see readObjects(Class, Node, String)
	 */
	public static void writeObjects(
			Class<? extends XMLExternalizable> defaultClass, Node parent,
			XMLExternalizable[] objs) throws XMLException {
		if(objs == null || objs.length == 0) {
			return;
		}
		Document doc = parent.getOwnerDocument();
		XMLExternalizable obj;
		for(int i = 0; i < objs.length; i++) {
			obj = objs[i];
			obj.toXML(parent);
			if(!obj.getClass().equals(defaultClass)) {
				// add class name
				Node last = parent.getLastChild();
				Attr a = doc.createAttribute("class");
				a.setNodeValue(obj.getClass().getName());
				last.getAttributes().setNamedItem(a);
			}
		}
	}

	/**
	 * Writes the given object into XML in a form suitable
	 * to be read back by <code>readObject</code>.<br>
	 * @param defaultClass default class
	 * @param parent
	 * @param obj
	 * @see readObject(Class, Node)
	 */
	public static void writeObject(
			Class<? extends XMLExternalizable> defaultClass, Node parent,
			XMLExternalizable obj) throws XMLException {
		obj.toXML(parent);
		if(!obj.getClass().equals(defaultClass)) {
			Document doc = parent.getOwnerDocument();
			// add class name
			Node last = parent.getLastChild();
			Attr a = doc.createAttribute("class");
			a.setNodeValue(obj.getClass().getName());
			last.getAttributes().setNamedItem(a);
		}
	}

	/**
	 * @return the string <code>s</code> normalized.
	 * @param s
	 */
	public static String normalize(String s) {
		int len = (s != null) ? s.length() : 0;
		StringBuffer out = new StringBuffer((int)(len * 1.15));
        for(int i = 0; i < len; i++) {
            char c = s.charAt(i);
	        switch(c) {
	        case '<':
	            out.append("&lt;");
	            break;
	        case '>':
	            out.append("&gt;");
	            break;
	        case '&':
	            out.append("&amp;");
	            break;
	        case '"':
	            out.append("&quot;");
	            break;
	        case '\r':
	        case '\n':
	        case '\'':
                out.append("&#");
                out.append(Integer.toString(c));
                out.append(';');

                break;
	        default:
	        	if(XMLChar.isValid(c)) {;
	            	out.append(c);
	        	} else {
	        		logger.error("Character " + c + " ignored during normalization as it's not a valid xml character");
	        	}
	        }
        }
        return out.toString();
	}

	/**
	 * @param s
	 * @return
	 */
	public static String makeXMLSafe(String s) {
		char[] chars = s.toCharArray();
		for(int i = 0; i < chars.length; i++) {
			char ch = chars[i];
	        switch(ch) {
	        case '<':
	        case '>':
	        case '&':
	        case '"':
	        case '\r':
	        case '\n':
	        case '\'':
                break;
	        default:
	        	if(!XMLChar.isValid(ch)) {
	        		chars[i] = ' ';
	        	}
	        }
		}
		return new String(chars);
	}

	/**
	 * @param file
	 * @param objs
	 * @param defaultClass
	 * @throws XMLException
	 */
	public static void writeObjectsToFile(File file, XMLExternalizable[] objs, 
			Class<? extends XMLExternalizable> defaultClass) throws XMLException {
		BufferedOutputStream os = null;
		try {
			os = new BufferedOutputStream(new FileOutputStream(file));
			Document doc = XMLUtils.createEmptyDocument("rms");
			Node node = XMLUtils.findChild(doc, "rms");
			writeObjects(defaultClass, node, objs);
			XMLUtils.write(doc, os);
		} catch(XMLException e) {
			throw e;
		} catch(Exception e) {
			throw new XMLException(e);
		} finally {
			if(os != null) {
				try {
					os.close();
				} catch(Exception e) {
				}
			}
		}
	}

	/**
	 * @param file
	 * @param defaultClass
	 * @param node
	 * @return
	 * @throws XMLException
	 */
	public static XMLExternalizable[] readObjectsFromFile(File file, 
			Class<? extends XMLExternalizable> defaultClass, String node) throws XMLException {
		if(!file.exists()) {
			return null;
		}
		BufferedInputStream bs = null;
		try {
			bs = new BufferedInputStream(new FileInputStream(file));
			Document doc = XMLUtils.read(bs);
			Node n = XMLUtils.findChild(doc, "rms");
			if(n == null) {
			    throw new XMLNodeMissing("rms");
			}
			return readObjects(defaultClass, n, node);
		} catch(XMLException e) {
			throw e;
		} catch(Exception e) {
			throw new XMLException(e);
		} finally {
			if(bs != null) {
				try {
					bs.close();
				} catch (IOException e) {
				}
			}
		}
	}

	/**
	 * Reads one object from a document like this:
	 * <pre>
	 * <root>
	 * 	<unknownObjectRoot></unknownObjectRoot>
	 * </root>
	 * </pre>
	 * <br>
	 * It is used when reading objects for which the name of root node is unknown.
	 * @param defaultClass @see writeObjects methods
	 * @param buff
	 * @return
	 * @throws XMLException
	 */
	public static XMLExternalizable fromXMLBuffer(
			Class<? extends XMLExternalizable> defaultClass,
			StringBuffer buff) throws XMLException {
		try {
			ByteArrayInputStream is = new ByteArrayInputStream(
					buff.toString().getBytes(XMLUtils.ENCODING));
			Document doc = XMLUtils.read(is);
			Node node = XMLUtils.getFirstElement(doc.getDocumentElement());
			if(node == null) {
				throw new XMLException("Invalid document structure");
			}
			return XMLUtils.readObject(defaultClass, node);
		} catch(XMLException e) {
			throw e;
		} catch(Exception e) {
			throw new XMLException(e);
		}
	}
}
