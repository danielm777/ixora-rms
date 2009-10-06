package com.ixora.rms;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterId;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentId;

/**
 * Identifies one or a set of monitoring entities (counter,
 * entity, agent...) by specifying the host, agent, entity and counter id
 * or a regular expression for each of the above.
 * @author Cristian Costache
 * @author Daniel Moraru
 */
/*
 * Note: this class should remain immutable
 * Modification history
 * --------------------------------------------------
 * 07 July 2004 - DM fixed equals() and hashcode()
 * 11 July 2004 - DM added isComplete() and complete() methods
 * 16 July 2004 - DM added appliesTo() and regexp support
 * 11 Aug 2004 - DM added parse()
 */
public final class ResourceId implements Serializable {
	/** The host part of the resource */
	public static final int HOST = 0;
	/** The agent part of the resource */
	public static final int AGENT = 1;
	/** The entity part of the resource */
	public static final int ENTITY = 2;
	/** The counter part of the resource as well as the index in the bitset for the counter ID */
	public static final int COUNTER = 3;
	/**
	 * If this counter has missing leading parts then <code>getRepresentation()</code> returns this value
	 * This flag doubles as a RELATIVE flag as this id might represent a resource relative to another.
	 */
	public static final int INVALID = 4;

    /** Parts of the resource id */
    private String[] parts;
    /** This is null if this resource id is not a regex */
    private boolean[] regex;
    /** Cached regex patterns for all parts; null if this is not a regex id  */
    private Pattern[] patterns;


// this are just cached here as they will be needed often
    /** Host ID */
	private HostId hostID;
	/** Agent ID */
	private AgentId	agentID;
	/** Entity ID */
	private EntityId entityID;
	/** Counter ID */
	private CounterId counterID;

    /** Marks the begining of the regex part */
	private static final char LBRACKET = '(';
    /** Marks the end of the regex part */
    private static final char RBRACKET = ')';

    public static final HostId EMPTY_HOST = new HostId("-");
    public static final AgentId EMPTY_AGENT = new AgentId("-");
    public static final EntityId EMPTY_ENTITY = new EntityId("-");
    public static final CounterId EMPTY_COUNTER = new CounterId("-");

	/**
	 * Constructor that builds a resource id from a string
	 * expected to be of the form outputed by <code>toString()</code>.
	 * @param id
	 */
	public ResourceId(String id) {
	    parse(id);
        buildParts();
	}

    /**
	 * Constructor. Any last parameters can be null and if so
	 * the resource identified will be an entityId if counterId is
	 * null or an agentId if both entityId and counterId are null.
	 * @param hostID
	 * @param agentID can be null only if the params bellow are null
	 * @param entityID can be null only if the counterId is null
	 * @param counterID can be null to fully identify an entity
	 */
	public ResourceId(
		HostId hostID,
		AgentId agentID,
		EntityId entityID,
		CounterId counterID) {
		super();
		this.hostID = hostID;
		this.agentID = agentID;
		this.entityID = entityID;
		this.counterID = counterID;
        buildParts();
	}

    /**
	 * @return AgentId
	 */
	public AgentId getAgentId()	{
		return agentID;
	}

	/**
	 * @return CounterId
	 */
	public CounterId getCounterId() {
		return counterID;
	}

	/**
	 * @return EntityId
	 */
	public EntityId getEntityId() {
		return entityID;
	}

    /**
     * @param module
     * @return a resource id identifying a resource type as specified
     * by <code>module</code> which is one of the following AGENT, HOST, ENTITY or COUNTER;
     * if it is COUNTER than a clone of the current resource id will be returned
     */
    public ResourceId getSubResourceId(int module) {
        switch(module) {
        case HOST:
            return new ResourceId(this.hostID, null, null, null);
        case AGENT:
            return new ResourceId(this.hostID, this.agentID, null, null);
        case ENTITY:
            return new ResourceId(this.hostID, this.agentID, this.entityID, null);
        case COUNTER:
            return new ResourceId(this.hostID, this.agentID, this.entityID, this.counterID);
        }
        throw new IllegalArgumentException("Unknown module " + module);
    }

	/**
	 * @return HostId
	 */
	public HostId getHostId() {
		return hostID;
	}

	/**
	 * @return the length of of the path represented by this id<br>
	 * (host + agent + no of elements in entity id + counter)
	 */
	public int getPathLength() {
	    int rep = getRepresentation();
	    switch(rep) {
	    case HOST:
	        return 1;
	    case AGENT:
	        return 2;
	    case ENTITY:
	        return 2 + this.entityID.getPathLength();
	    case COUNTER:
	        return 3 + this.entityID.getPathLength();
	    }
	    return 0;
	}

	/**
	 * Returns a new id using information from the
	 * given id. Can be used to build a valid id from an invalid one.
	 * @param rid a valid id
	 * @return the new resource id
	 */
	public ResourceId complete(ResourceId rid) {
		if(rid == null) {
			return new ResourceId(this.hostID, this.agentID, this.entityID, this.counterID);
		}
		HostId hid = hostID;
		if(isValidPart(rid.hostID) && !isValidPart(hostID)) {
			hid = rid.hostID;
		}
		AgentId aid = agentID;
        if(isValidPart(rid.agentID) && !isValidPart(agentID)) {
			aid = rid.agentID;
		}
		EntityId eid = entityID;
        if(isValidPart(rid.entityID) && !isValidPart(entityID)) {
			eid = rid.entityID;
		}
		CounterId cid = counterID;
        if(isValidPart(rid.counterID) && !isValidPart(counterID)) {
			cid = rid.counterID;
		}
		return new ResourceId(hid, aid, eid, cid);
	}

	/**
	 * If this id contains regexps then it returns true if <code>rid</code>
	 * is an element in the set of ids described by this instance i.e
	 * <code>rid</code> is a full perfect match for the given regular expression.<br>
	 * If this id doesn't contain regexps then it returns true only if
	 * <code>rid</code> is equal to <code>this</code>.
	 * @param rid a non-regex id
	 * @return
	 */
	public boolean matches(ResourceId rid) {
		return matches(rid, null);
	}

	/**
	 * If this id contains regexps then it returns true if <code>cid</code>
	 * is an element in the set of counter ids described by this instance i.e
	 * <code>cid</code> is a full perfect match for the counter regular expression.<br>
	 * If this id doesn't contain regexps then it returns true only if
	 * <code>cid</code> is equal to the counter part of this id.
	 * @param cid a non-regex counter id
	 * @return
	 */
	public boolean matchesCounterId(CounterId cid) {
		if(!isRegex(COUNTER)) {
			return this.counterID.equals(cid);
		}
		Pattern counterPattern = this.patterns[this.patterns.length - 1];
        Matcher m = counterPattern.matcher(cid.toString());
        return m.matches();
    }

    /**
     * If this id contains regexps then it returns true if <code>rid</code>
     * is an element in the set of ids described by this instance i.e
     * <code>rid</code> is a full perfect match for the given regular expression.<br>
     * If this id doesn't contain regexps then it returns true only if
     * <code>rid</code> is equal to <code>this</code>.
     * @param rid a non-regex id
     * @return
     */
    public boolean matches(ResourceId rid, List<String> matches) {
    	if(!isRegex()) {
            return equals(rid);
        }
        // get rid of the simple cases first
        if(rid == null || rid.isRegex()) {
            return false;
        }
        int rep = getRepresentation();
        if(rep != rid.getRepresentation()) {
            return false;
        }
        if(this.parts.length != rid.parts.length) {
            return false;
        }
        // check all regex for a match
        for(int i = 0; i < this.parts.length; ++i) {
            Pattern p = this.patterns[i];
            if(p == null) {
                if(!this.parts[i].equals(rid.parts[i])) {
                    return false;
                }
            } else {
                Matcher m = p.matcher(rid.parts[i]);
                if(!m.matches()) {
                    return false;
                } else {
                    if(matches != null) {
                        makeCapturesList(m, matches);
                    }
                }
            }
        }
        return true;
    }

	/**
	 * @param base
	 * @return a relative resource id for this resource, relative to the <code>base</code>
	 * resource (e.g this=host/agent/root/ent1/ent2 and base=host/agent/ then result=-/-/root/ent1/ent2)
	 */
	public ResourceId getRelativeTo(ResourceId base) {
		if(base == null) {
			return this;
		}
        if(!base.isValid()) {
            throw new IllegalArgumentException("Base is not valid. Base: " + base);
        }
		if(!contains(base)) {
			throw new IllegalArgumentException("Base resource id must be contained in this " +
					"resource id. Base: " + base + " This: " + this);
		}
		int baseRepresentation = base.getRepresentation();
		ResourceId ret = new ResourceId(
				baseRepresentation == INVALID ? hostID : null,
				baseRepresentation == HOST ? agentID : null,
				entityID,
				counterID);
		return ret;
	}

	/** Small internal utility to extract captured groups from a matcher */
	private void makeCapturesList(Matcher m, List<String> captures) {
	    // no captures = return empty list
	    if(captures == null || m == null)
		    return;

	    // Add subgroups ($1 - $n)
	    int cnt = m.groupCount();
	    for(int i = 1; i <= cnt; i++) {
	    	captures.add(m.group(i));
	    }
	    //int debug = 0;
	}

	/**
	 * If this id contains regexps then it returns true if the given resource id
	 * contains a subpath that matches this id.
	 * If this id doesn't contain regexps then it returns the value of
	 * <code>rid.contains(this)</code>
	 * @param rid a non-regex id
     * @param matches out parameters; a list that will store the regex matches
	 * @return
	 */
	public boolean matchesSubpathIn(ResourceId rid, List<String> matches) {
		if(rid == null || rid.isRegex()) {
			return false;
		}
		if(getRepresentation() == INVALID) {
		    return false;
        }
		if(this.parts.length > rid.parts.length) {
		    return false;
		}
		if(!this.isRegex()) {
			return rid.contains(this);
		}

        boolean found = false;
        // check all regex for a match
        for(int i = 0; i < this.parts.length; ++i) {
            String part = this.parts[i];
            if(!isValidPart(part)) {
                return found;
            }
            Pattern p = this.patterns[i];
            if(p == null) {
                if(!part.equals(rid.parts[i])) {
                    return false;
                } else {
                    found = true;
                }
            } else {
                Matcher m = p.matcher(rid.parts[i]);
                if(!m.matches()) {
                    return false;
                } else {
                    found = true;
                    if(matches != null) {
                        makeCapturesList(m, matches);
                    }
                }
            }
        }
        return found;
	}

	/**
	 * @param rid a non-regex id
	 * @return true if the given id is a subset of the resource
	 * described by this id (which can be a regex id)
	 */
	public boolean contains(ResourceId rid) {
        if(rid == null) {
            return true;
        }
        if(rid.isRegex() || !rid.isValid()) {
            return false;
        }
		if(!isValid()) {
			return false;
		}
        if(rid.parts.length > this.parts.length) {
            return false;
        }

        // check all regex for a match
        for(int i = 0; i < rid.parts.length; ++i) {
            if(isRegex()) {
                Pattern p = this.patterns[i];
                if(p == null) {
                    String s = rid.parts[i];
                    if(!this.parts[i].equals(s)) {
                        return !isValidPart(s);
                    }
                } else {
                    String s = rid.parts[i];
                    Matcher m = p.matcher(s);
                    if(!m.matches()) {
                        return !isValidPart(s);
                    }
                }
            } else {
                String s = rid.parts[i];
                if(!this.parts[i].equals(s)) {
                    return !isValidPart(s);
                }
            }
        }
        return true;
    }

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if(obj == this) {
			return true;
		}
		if(!(obj instanceof ResourceId)) {
			return false;
		}
		ResourceId	that = (ResourceId)obj;
		return Arrays.equals(this.parts, that.parts);
	}

	/**
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return Utils.hashCode(this.parts);
	}

	/**
	 * @return true if this id contains regular expressions
	 */
	public boolean isRegex() {
		return this.patterns == null ? false : true;
	}

	/**
	 * @param part one of HOST, AGENT, ENTITY, COUNTER
	 * @return true if the given part is a regular expression
	 */
	public boolean isRegex(int part) {
		if(!isRegex()) {
			return false;
		}
        switch(part) {
        case HOST:
            return this.patterns[0] != null;
        case AGENT:
            return this.patterns[1] != null;
        case COUNTER:
            return this.patterns[this.patterns.length - 1] != null;
        case ENTITY:
            // check if any of the patterns between indexes 2 and len - 1 are non null
            for(int i = 2; i < patterns.length - 1; ++i) {
                if(this.patterns[i] != null) {
                    return true;
                }
            }
            return false;
        }
		return false;
	}

	/**
	 * @return true if this id has a valid representation
	 */
	public boolean isValid() {
		if(!isValidPart(this.hostID)) {
			return false;
		}
		return true;
	}

	/**
	 * If this resource represents a host it returns HOST.<br>
	 * If this resource represents an agent it returns AGENT.<br>
	 * If this resource represents an entity it returns ENTITY.<br>
	 * If this resource represents a counter it returns COUNTER.<br>
	 * If this id is invalid it returns INVALID.
	 * @return one of HOST, AGENT, ENTITY, COUNTER, INVALID
	 * Note: a value of INVALID still has a meaning as an id relative
	 * to other resource
	 */
	public int getRepresentation() {
		if(!isValid()) {
			return INVALID;
		}
        if(!isValidPart(this.agentID)) {
            return HOST;
        }
        if(!isValidPart(this.entityID)) {
            return AGENT;
        }
        if(!isValidPart(this.counterID)) {
            return ENTITY;
        }
        return COUNTER;
	}

    /**
     * @return the representation of this resource, ignoring the fact that
     * the representation is not complete (it's relative)
     */
    public int getRelativeRepresentation() {
        if(isValidPart(this.counterID)) {
            return COUNTER;
        }
        if(isValidPart(this.entityID)) {
            return ENTITY;
        }
        if(isValidPart(this.agentID)) {
            return AGENT;
        }
        if(isValidPart(this.hostID)) {
            return HOST;
        }
        return INVALID;
    }

	/**
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
	    StringBuffer buff = new StringBuffer();
	    int rep = getRelativeRepresentation();
	    switch(rep) {
		    case HOST:
		        toStringHostId(buff);
		        break;
		    case AGENT:
		        toStringHostId(buff);
		        buff.append(EntityId.DELIMITER);
		        toStringAgentId(buff);
		        break;
		    case ENTITY:
		        toStringHostId(buff);
		        buff.append(EntityId.DELIMITER);
		        toStringAgentId(buff);
		        buff.append(EntityId.DELIMITER);
		        toStringEntityId(buff);
		        break;
		    case COUNTER:
		        toStringHostId(buff);
		        buff.append(EntityId.DELIMITER);
		        toStringAgentId(buff);
		        buff.append(EntityId.DELIMITER);
		        toStringEntityId(buff);
		        buff.append(EntityId.DELIMITER);
		        buff.append("[");
		        buff.append(this.counterID);
		        buff.append("]");
		        break;
	    }
	    return buff.toString();
	}

	/**
	 * Builds this object from the given string.
     * @param id
     */
    private void parse(String id) {
    	if(id.length() == 0) {
	        return;
	    }
	    if(id.charAt(0) == EntityId.DELIMITER) {
	        id = id.substring(1);
	    }
        if(id.charAt(id.length() - 1) == EntityId.DELIMITER) {
        	id = id.substring(0, id.length() - 1);
        }

	    char last = id.charAt(id.length() - 1);
	    int idx = id.lastIndexOf('[');
	    if(last == ']' && idx >= 0) {
	        String tmp = id.substring(idx + 1,
	                id.length() - 1);
	        this.counterID = new CounterId(tmp);
	        id = id.substring(0, idx - 1);
	    }
	    idx = id.indexOf(EntityId.DELIMITER);
	    String tmp;
	    if(idx > 0) { // host
	        tmp = id.substring(0, idx).trim();
	    } else {
	        tmp = id;
	    }
        if(tmp.length() > 0) {
            this.hostID = new HostId(tmp);
        }
        if(idx > 0) {
	        id = id.substring(idx + 1);
	        idx = id.indexOf(EntityId.DELIMITER);
		    if(idx > 0) { // agent
		        tmp = id.substring(0, idx).trim();
		    } else {
		    	tmp = id;
		    }
	        if(tmp.length() > 0 ) {
               this.agentID = new AgentId(tmp);
	        }

	        if(idx > 0) {
		        id = id.substring(idx + 1).trim();
		        if(id.length() > 0) {
                    this.entityID = new EntityId(id);
		        }
	        }
        }
    }

    /**
     * Replaces null entries with empty ones.
     */
    private void normalize() {
        if(this.hostID == null) {
            this.hostID = EMPTY_HOST;
        }
        if(this.agentID == null) {
            this.agentID = EMPTY_AGENT;
        }
        if(this.entityID == null) {
            this.entityID = EMPTY_ENTITY;
        }
        if(this.counterID == null) {
            this.counterID = EMPTY_COUNTER;
        }
    }

    /**
     * Builds the string parts, analyses them and sets up regex info related to them.
     */
    private void buildParts() {
        // normalize first to avoid null ids
        normalize();

        // build parts
        String[] eparts = this.entityID.getPathComponents();
        this.parts = new String[eparts.length + 3];
        this.parts[0] = this.hostID.toString();
        this.parts[1] = this.agentID.toString();
        System.arraycopy(eparts, 0, this.parts, 2, eparts.length);
        this.parts[this.parts.length - 1] = this.counterID.toString();

        // check for regex
        for(int i = 0; i < parts.length; i++) {
            String part = parts[i];
            if(isRegexString(part)) {
                if(this.regex == null) {
                    this.regex = new boolean[parts.length];
                }
                this.regex[i] = true;
                parts[i] = removeBrackets(part);
            }
        }

        // cache regex patterns
        if(this.regex != null) {
            this.patterns = new Pattern[this.parts.length];
            for(int i = 0; i < this.regex.length; i++) {
                if(this.regex[i]) {
                    this.patterns[i] = Pattern.compile(this.parts[i]);
                }
            }
        }
    }

    /**
     * Checks whether or not the given string represents a
     * regular expression.<br>
     * A string is considered a regular expression if it is
     * surrounded by open ranthesis. (e.g. (counter*))
     * @param s
     * @return
     */
    private boolean isRegexString(String s) {
        if(s.length() >= 3) {
            if(s.charAt(0) == LBRACKET &&
                s.charAt(s.length() - 1) == RBRACKET) {
                return true;
            }
        }
        return false;
    }


    /**
     * @param buff
     */
    private void toStringHostId(StringBuffer buff) {
        if(this.hostID != null && this.hostID.toString().length() != 0) {
        	buff.append(this.hostID.toString());
        } else {
            buff.append('-');
        }
    }

    /**
     * @param buff
     */
    private void toStringAgentId(StringBuffer buff) {
        if(this.agentID != null && this.agentID.toString().length() != 0) {
        	buff.append(this.agentID.toString());
        } else {
            buff.append('-');
        }
    }

    /**
     * @param buff
     */
    private void toStringEntityId(StringBuffer buff) {
        if(this.entityID != null && this.entityID.toString().length() != 0) {
            if(isRegex(ENTITY)) {
                for(int i = 2; i < this.parts.length - 1; ++i) {
                    if(this.patterns[i] != null) {
                        buff.append(LBRACKET);
                        buff.append(this.parts[i].toString());
                        buff.append(RBRACKET);
                    } else {
                        buff.append(this.parts[i].toString());
                    }
                    if(i != this.parts.length - 2) {
                        buff.append(EntityId.DELIMITER);
                    }
                }
            } else {
                buff.append(this.entityID.toString());
            }
        } else {
            buff.append('-');
        }
    }

    /**
     * @param buff
     */
//    private void toStringCounterId(StringBuffer buff) {
//        if(this.counterID != null && this.counterID.toString().length() != 0) {
//            buff.append('[');
//            buff.append(this.counterID.toString());
//            buff.append(']');
//        } else {
//            buff.append('-');
//        }
//    }

    /**
     * Removes the regex helpers (brackets and the negate char)
     */
    private String removeBrackets(String s) {
        if(s.length() < 3) {
            return s;
        }
        return s.substring(1, s.length() - 1);
    }

    /**
     * @param id
     * @return true if the given string represents a valid part
     */
    private static boolean isValidPart(String id) {
        if(id == null || id.equals("-")) {
            return false;
        }
        return true;
    }
    /**
     * @param id
     * @return true if the given id represents a valid part
     */
    private static boolean isValidPart(HostId id) {
        if(id == null || id.toString().equals("-")) {
            return false;
        }
        return true;
    }
    /**
     * @param id
     * @return true if the given id represents a valid part
     */
    private static boolean isValidPart(AgentId id) {
        if(id == null || id.toString().equals("-")) {
            return false;
        }
        return true;
    }
    /**
     * @param id
     * @return true if the given id represents a valid part
     */
    private static boolean isValidPart(EntityId id) {
        if(id == null || id.toString().equals("-")) {
            return false;
        }
        return true;
    }
    /**
     * @param id
     * @return true if the given id represents a valid part
     */
    private static boolean isValidPart(CounterId id) {
        if(id == null || id.toString().equals("-")) {
            return false;
        }
        return true;
    }
}
