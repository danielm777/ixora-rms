/*
 * Created on 17-Oct-2004
 */
package com.ixora.rms.ui.dataviewboard.tables.definitions;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.w3c.dom.Node;

import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLAttribute;
import com.ixora.common.xml.XMLAttributeBoolean;
import com.ixora.common.xml.XMLAttributeString;
import com.ixora.common.xml.XMLSameTagList;
import com.ixora.common.xml.XMLTag;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.ResourceId;
import com.ixora.rms.dataengine.RealizedQuery;
import com.ixora.rms.dataengine.definitions.QueryDef;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.ui.dataviewboard.tables.TablesBoard;

/**
 * Definition of a table view.
 * @author Daniel Moraru
 * @author Cristian Costache
 */
public final class TableDef extends DataView {
	private static final long serialVersionUID = -7421956014909788874L;
	/** The resource which plays the role of the category */
	private CategoryTag	category = new CategoryTag();
    /** Columns */
	private XMLSameTagList<ColumnTag> columns = new XMLSameTagList<ColumnTag>(ColumnTag.class);

    /**
	 * CategoryTag
	 * Internal class holding the definition for a table category
	 */
	private static class CategoryTag extends XMLTag {
		private static final long serialVersionUID = -3651439309255850501L;
		private XMLAttributeString id = new XMLAttributeString("id", true);
		/**
		 * Flag that specifies whether or not to remove
		 * stale categories from the view. A stale category is one which has not been
		 * updated during the last cycle.
		 */
		private XMLAttributeBoolean removeStale = new XMLAttributeBoolean("removeStale", false);
		/**
		 * Default constructor, this tag is always mandatory
		 */
		public CategoryTag() {
			super(true);
		}
        /**
         * @param cat
         */
        public CategoryTag(String cat) {
        	this(cat, false);
        }
		/**
         * @param cat
         * @param removeStaleCategories
         */
        public CategoryTag(String cat, boolean removeStaleCategories) {
            super(true);
            id.setValue(cat);
            removeStale.setValue(removeStaleCategories);
        }
		/**
		 * @see com.ixora.common.xml.XMLTag#getTagName()
		 */
		public String getTagName() {
			return "category";
		}
		/**
		 * @return resource id to use for creating categories
		 */
		public String getID() {
			return id.getValue();
		}
		/**
		 * @return
		 */
		public boolean removeStale() {
			return removeStale.getBoolean().booleanValue();
		}
		/**
		 * @see com.ixora.common.xml.XMLTag#fromXML(org.w3c.dom.Node)
		 */
		public void fromXML(Node node) throws XMLException {
			// TODO Auto-generated method stub
			super.fromXML(node);
		}
		/**
		 * @see com.ixora.common.xml.XMLTag#toXML(org.w3c.dom.Node)
		 */
		public void toXML(Node parent) throws XMLException {
			// TODO Auto-generated method stub
			super.toXML(parent);
		}


	}

    /**
     * ColumnTag
     * Internal class holding the definition for a table column
     */
    // an ugly hack here, the class must be public in order to work with
    // XMLSameTagList class
    public static class ColumnTag extends XMLTag {
		private static final long serialVersionUID = -2865179674549086234L;
		private XMLAttribute id = new XMLAttributeString("id", true);
        /**
         * Default constructor, this tag is not mandatory
         */
        public ColumnTag() {
            super();
        }
        /**
         * @param column
         */
        public ColumnTag(String column) {
            super();
            id.setValue(column);
        }
        /**
         * @see com.ixora.common.xml.XMLTag#getTagName()
         */
        public String getTagName() {
            return "column";
        }
        /**
         * @return resource id to use for creating categories
         */
        public String getID() {
            return id.getValue();
        }
    }

    /**
     * Constructor.
     */
    public TableDef(String category, List<String> columns) {
        super();
        this.category = new CategoryTag(category);
        for(String col : columns) {
        	this.columns.add(new ColumnTag(col));
        }
    }

    /**
     * Constructor.
     */
    public TableDef(String category, List<String> columns, String name, String desc, QueryDef query, String author) {
    	this(category, columns, name, desc, query, author, false);
    }

    /**
     * Constructor.
     */
    public TableDef(String category, List<String> columns, String name, String desc, QueryDef query,
    		String author, boolean removeStaleCategories) {
        super(name, desc, query, author);
        this.category = new CategoryTag(category, removeStaleCategories);
        for(String col : columns) {
        	this.columns.add(new ColumnTag(col));
        }
    }

    /**
     * Constructor.
     */
    public TableDef() {
        super();
    }

    /**
     * @see com.ixora.rms.repository.DataView#getBoardClass()
     */
    public String getBoardClass() {
        return TablesBoard.class.getName();
    }

    /**
     * @return the string representing the id of the category.
     */
    public String getCategory() {
        return category.getID();
    }

    /**
     * Whether or not to remove stale categories; i.e. categories that
     * have not been updated during the last cycle.
     * @return
     */
    public boolean removeStaleCategories() {
    	return category.removeStale();
    }

    /**
     * @return a list of strings representing the ids of the columns
     */
    public Set<String> getColumns() {
        // linked set is needed in order to preserve ordering
        Set<String> ret = new LinkedHashSet<String>();
        for(Object o : columns) {
            ColumnTag column = (ColumnTag)o;
            ret.add(column.getID());
        }
        return ret;
    }

    /**
     * @return true if the given id is that of a column
     */
    public boolean isIdAccepted(String id) {
        if(Utils.isEmptyCollection(columns)) {
            return true;
        }
        for(Object o : columns) {
            ColumnTag column = (ColumnTag)o;
            if(id.equals(column.getID())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Throws the appropriate exception if a problem occurs while trying
     * to initialize a control from the given DataView. Used to test
     * the validity of a custom-defined view.
	 * @see com.ixora.rms.repository.DataView#testDataView(com.ixora.rms.ResourceId)
     */
    public void testDataView(ResourceId context) throws RMSException {
    	super.testDataView(context);

        // Make sure that the cube can be realized (query definition ok)
	    RealizedQuery realizedCube = new RealizedQuery(getQueryDef(), context);

	    // Make sure that category id is valid (exists in the query)
	   	realizedCube.getQueryResult(getCategory());

        if(!Utils.isEmptyCollection(columns)) {
    	    // make sure the columns ids are valid (exists in the query)
            for(String column : getColumns()) {
                realizedCube.getQueryResult(column);
            }
        }
    }
}