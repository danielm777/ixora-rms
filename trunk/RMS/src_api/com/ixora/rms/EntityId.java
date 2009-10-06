package com.ixora.rms;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ixora.common.MessageRepository;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;

/**
 * Monitored entity identifier.
 * @author Daniel Moraru
 */
public final class EntityId implements Serializable, Cloneable, Comparable<EntityId> {
	/** Path element delimiter */
	public static final char DELIMITER = '/';
	private static final String DELIMITER_STRING = "/";
	/** Alternate delimiter */
	private static final char ALTERNATE_DELIMITER = '\\';
	private static final String ALTERNATE_DELIMITER_STRING = "\\";
	/**
	 * Component boundary delimiter. Used to solve the situation when
	 * the normal escaping mechanism doesn't work: when a comnponent starts
	 * or ends with the delimiter or alternate delimiter.
	 */
	private static final char COMPONENT_BOUNDARY_DELIMITER = '!';
	private static final String COMPONENT_BOUNDARY_DELIMITER_STRING = "!";

	/** Entity path, stored escaped */
	private String fPath;
	/** Path components, stored unescaped */
	private String[] fComponents;

	/**
	 * Constructor for EntityId. Simply stores the path passed.
	 * Used in conjunction with <code>toString()</code> to
	 * persist and restore entity ids.
	 * @param path the path through the entity hierarchy, the path is escaped.
	 */
	public EntityId(String path) {
		super();
		if(path == null || path.length() == 0) {
			throw new IllegalArgumentException("Illegal entity path " + path);
		}
		// remove head and tail delimiters
		char ch = path.charAt(0);
		if(ch == DELIMITER || ch == ALTERNATE_DELIMITER) {
		    if(path.length() < 2) {
		        throw new IllegalArgumentException("Illegal entity path " + path);
		    }
		    path = path.substring(1);
		}
		ch = path.charAt(path.length() - 1);
		if(ch == DELIMITER || ch == ALTERNATE_DELIMITER) {
		    if(path.length() < 2) {
		        throw new IllegalArgumentException("Illegal entity path " + path);
		    }
		    path = path.substring(0, path.length() - 1);
		}
		calculateComponentsAndPath(path);
	}

	/**
	 * Constructor for EntityId.
	 * @param p the path through the entity hierarchy, the components are unescaped.
	 */
	public EntityId(String[] p) {
		super();
		if(p == null || p.length == 0) {
			throw new IllegalArgumentException("entity path is null or empty");
		}
		StringBuffer path = new StringBuffer();
		for(int i = 0; i < p.length; i++) {
			String comp = p[i];
			path.append(escapePathComponent(comp));
			if(i < p.length - 1) {
				path.append(DELIMITER);
			}
		}
		calculateComponentsAndPath(path.toString());
	}

	/**
	 * Constructor for EntityId.
	 * @param idParent the id of the parent entity
	 * @param name the name of this entity
	 */
	public EntityId(EntityId idParent, String name) {
		super();
		this.fPath = idParent.fPath;
		add(name);
		// recalculate
		calculateComponentsAndPath(fPath);
	}

	/**
	 * Calculates the path components.
	 * @param path the escaped path as outputed by <code>toString()</code>
	 */
	private void calculateComponentsAndPath(String path) {
		List<String> lst = new ArrayList<String>(5);
		StringBuffer buff = new StringBuffer();
		char[] chars = path.toCharArray();
		for(int i = 0; i < chars.length; i++) {
			char ch = chars[i];
			buff.append(ch);
			char delim = 0;
			if(ch == DELIMITER) {
				delim = DELIMITER;
			} else if(ch == ALTERNATE_DELIMITER) {
				delim = ALTERNATE_DELIMITER;
			}
			if(delim != 0) {
				int count = 1;
				while(i < chars.length - 1) {
					ch = chars[i + 1];
					if(ch != delim) {
						break;
					}
					count++;
					i++;
					buff.append(ch);
				}
				// if an odd number of delims was added, remove last entry
				if(count % 2 != 0) {
					// remove last entry
					buff.deleteCharAt(buff.length() - 1);
					lst.add(buff.toString());
					buff.delete(0, buff.length());
				}
			}
		}
		if(buff.length() > 0) {
			lst.add(buff.toString());
			buff.delete(0, buff.length());
		}
		this.fComponents = lst.toArray(new String[lst.size()]);
		// unescape components and build path
		for (int i = 0; i < fComponents.length; i++) {
			String comp = fComponents[i];
			// add first the escaped component to the path
			buff.append(comp);
			if(i < fComponents.length - 1) {
				buff.append(DELIMITER);
			}
			// unescape
			fComponents[i] = unescapePathComponent(comp);
		}
		fPath = buff.toString();
	}


	/**
	 * Adds the specified path to the end of this path. Used for building
	 * IDs dynamically.
	 * @param name
	 */
	public EntityId add(String name) {
		this.fPath += DELIMITER + escapePathComponent(name);
		return this;
	}

	/**
	 * @return the name of the entity extracted as the last element
	 * on the path (unescaped)
	 */
	public String getName() {
		return fComponents[fComponents.length - 1];
	}

	/**
	 * @return the id of the parent
	 */
	public EntityId getParent() {
		if(fComponents.length == 1) {
			// this is a root entity
			return null;
		}
		String[] parentComps = new String[fComponents.length - 1];
		System.arraycopy(fComponents, 0, parentComps, 0, parentComps.length);
		EntityId parent = new EntityId(parentComps);
		return parent;
	}

	/**
	 * Returns true if the this id is an ancestor of the given
	 * id.
	 * @return
	 */
	public boolean isAncestorOf(EntityId id) {
	    if(equals(id.fPath)) {
	        return true;
	    }
	    int idx = id.fPath.indexOf(fPath);
	    if(idx == 0) {
	        return true;
	    }
	    return false;
	}

	/**
	 * @return the path with all components unescaped
	 */
	public String getPath() {
		StringBuffer buff = new StringBuffer();
		String[] c = getPathComponents();
		for(int i = 0; i < c.length; i++) {
			buff.append(c[i]);
			if(i != c.length - 1) {
				buff.append(DELIMITER);
			}
		}
		return buff.toString();
	}

	/**
	 * @return the full name path of this entity with
	 * all elements unescaped
	 */
	public String[] getPathComponents() {
		return this.fComponents;
	}

	/**
	 * @return all ancestors of this entity
	 */
	public EntityId[] getAncestors() {
	    String[] path = getPathComponents();
	    EntityId[] ret = new EntityId[path.length - 1];
	    EntityId id = null;
	    for(int i = 0; i < path.length - 1; i++) {
            if(id == null) {
                id = new EntityId(path[0]);
            } else {
                id = new EntityId(id, path[i]);
            }
            ret[i] = id;
        }
	    return ret;
	}

	/**
	 * If only interested in the length of the path
	 * use this more efficient method rather then calling
	 * <code>getPath()</code>.
	 * @return the lenght of the path
	 */
	public int getPathLength() {
		return this.fComponents.length;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(!(obj instanceof EntityId)) {
			return false;
		}
		EntityId id = (EntityId)obj;
		return fPath.equals(id.fPath);
	}

	/**
	 * Compares this ID with a string
	 * @param obj
	 */
	public boolean equals(String obj) {
		if(obj == null) {
			return false;
		}
		return fPath.equals(obj);
	}

	/**
	 * @see java.lang.Object#clone()
	 */
	protected Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			throw new InternalError(); // can't happen
		}
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return this.fPath.hashCode();
	}

	/**
	 * @return the full unprocessed path in a form
	 * suitable for reconstructing this object with
	 * <code>EntityId(String)</code> constructor.
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return fPath;
	}

	/**
	 * "/" and "\" will be escaped by "//" and "\\"
	 * @param comp
	 * @return
	 */
	public static String escapePathComponent(String comp) {
		// if the component starts or ends with a delimiter, insert a boundary
		// delimiter char
		if(comp.charAt(0) == DELIMITER || comp.charAt(0) == ALTERNATE_DELIMITER) {
			comp = COMPONENT_BOUNDARY_DELIMITER_STRING + comp;
		}
		int idx = comp.length() - 1;
		if(comp.charAt(idx) == DELIMITER || comp.charAt(idx) == ALTERNATE_DELIMITER) {
			comp = comp + COMPONENT_BOUNDARY_DELIMITER_STRING;
		}
		String ret = Utils.replace(comp, DELIMITER_STRING, DELIMITER_STRING + DELIMITER_STRING);
		ret = Utils.replace(ret, ALTERNATE_DELIMITER_STRING, ALTERNATE_DELIMITER_STRING + ALTERNATE_DELIMITER_STRING);
		// make it XML safe
		ret = XMLUtils.makeXMLSafe(ret);
		return ret;
	}

	/**
	 * "//" and "\\" will be reverted to "/" and "\"
	 * @param comp
	 * @return
	 */
	public static String unescapePathComponent(String comp) {
		// remove component boundary char if at the start or end
		// if the component starts or ends with a delimiter, insert a boundary
		// delimiter char
		if(comp.charAt(0) == COMPONENT_BOUNDARY_DELIMITER) {
			comp = comp.substring(1);
		}
		int idx = comp.length() - 1;
		if(comp.charAt(idx) == COMPONENT_BOUNDARY_DELIMITER) {
			comp.substring(0, idx);
		}
		String ret = Utils.replace(comp, DELIMITER_STRING + DELIMITER_STRING, DELIMITER_STRING);
		ret = Utils.replace(ret, ALTERNATE_DELIMITER_STRING + ALTERNATE_DELIMITER_STRING, ALTERNATE_DELIMITER_STRING);
		return ret;
	}

	/**
	 * @see java.lang.Comparable#compareTo(com.ixora.rms.EntityId)
	 */
	public int compareTo(EntityId arg0) {
		if(arg0 == this) {
			return 0;
		}
		return this.fPath.compareTo(arg0.fPath);
	}

	/**
	 * Returns the translated path of the given entity id.
	 * @param messageRepository
	 * @param eid
	 * @returns
	 */
	public static String getTranslatedPath(String messageRepository, EntityId eid) {
		String[] path = eid.getPathComponents();
		StringBuffer buff = new StringBuffer();
		for(int i = 0; i < path.length; i++) {
            buff.append(MessageRepository.get(messageRepository, path[i]));
            if(i != path.length - 1) {
                buff.append(EntityId.DELIMITER);
            }
        }
		return buff.toString();
	}
}