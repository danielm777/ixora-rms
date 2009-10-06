/*
 * Created on 18-Aug-2004
 */
package com.ixora.rms.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import com.ixora.rms.ResourceId;
import com.ixora.common.MessageRepository;
import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.common.ui.actions.ActionSelect;
import com.ixora.common.utils.Utils;
import com.ixora.rms.client.model.CounterInfo;
import com.ixora.rms.client.model.EntityNode;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.client.model.SessionModelTreeNode;
import com.ixora.rms.ui.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class ResourceSelectorDialog extends AppDialog {
    public static final int SELECT_ENTITY = 0;
    public static final int SELECT_COUNTER = 1;
    private JTree fTreeContext;
    private JTable fTableResources;
    private JPanel fPanelResources;
    private JPanel fPanelSpace;
    private int fSelectType;

    /** Event handler */
    private EventHandler fEventHandler;
    /** Session model */
    private SessionModel fSessionModel;
    /** Session tree explorer */
    private SessionTreeExplorer fTreeExplorer;
    /** The context of this selector */
    private ResourceId fContext;
	/** Last selected tree node */
	private SessionModelTreeNode fLastSelectedNode;
	/** Result */
	private List<ResourceId> fResult;
	/** Resources table model */
	private ResourcesTableModel fResourcesModel;

	private Action fActionOk;
	private Action fActionSelect;

	/**
	 * Model for selectable resources table.
	 */
	private static class ResourcesTableModel extends AbstractTableModel {
		private static class Entry implements Comparable<Entry> {
			ResourceId fResourceId;
			Boolean fSelected;

			Entry(ResourceId rid, boolean selected) {
				fResourceId = rid;
				fSelected = Boolean.valueOf(selected);
			}
			public int compareTo(Entry o) {
				return String.valueOf(fResourceId).compareTo(String.valueOf(o.fResourceId));
			}
		}
		private List<Entry> fResources;
		private boolean fSingleSelection;

		public ResourcesTableModel(boolean singleSelection) {
			fResources = new LinkedList<Entry>();
			fSingleSelection = singleSelection;
		}
		public int getRowCount() {
			return fResources.size();
		}
		public int getColumnCount() {
			return 2;
		}
		public String getColumnName(int column) {
			return "";
		}
		public Class< ? > getColumnClass(int columnIndex) {
			return columnIndex == 0 ? Boolean.class : String.class;
		}
		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return columnIndex == 0;
		}
		public Object getValueAt(int rowIndex, int columnIndex) {
			return columnIndex == 0 ? fResources.get(rowIndex).fSelected : fResources.get(rowIndex).fResourceId;
		}
		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			// disable any selected entries first if in single selection mode
			if(fSingleSelection) {
				for(Entry entry : fResources) {
					entry.fSelected = Boolean.FALSE;
				}
			}
			fResources.get(rowIndex).fSelected = (Boolean)aValue;
			// this will get rid of the selection
			fireTableDataChanged();
		}
		public void removeAll() {
			fResources.clear();
			fireTableDataChanged();
		}
		public void setResourceIds(List<ResourceId> rids) {
			fResources.clear();
			if(rids != null) {
				for(ResourceId rid : rids) {
					fResources.add(new Entry(rid, false));
				}
			}
			Collections.sort(fResources);
			fireTableDataChanged();
		}
		public List<ResourceId> getSelectedResourceIds() {
			List<ResourceId> lst = new LinkedList<ResourceId>();
			for(Entry e : fResources) {
				if(e.fSelected.booleanValue()) {
					lst.add(e.fResourceId);
				}
			}
			return lst;
		}
        public ResourceId getResourceAt(int row) {
            return fResources.get(row).fResourceId;
        }
	}

	/**
     * Event handler.
     */
    private final class EventHandler
    		implements TreeSelectionListener,
    			TreeWillExpandListener {
        /**
         * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
         */
        public void valueChanged(TreeSelectionEvent e) {
            handleContextTreeSelected(e);
        }
        /**
         * @see javax.swing.event.TreeWillExpandListener#treeWillCollapse(javax.swing.event.TreeExpansionEvent)
         */
        public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
            ; // nothing
        }
        /**
         * @see javax.swing.event.TreeWillExpandListener#treeWillExpand(javax.swing.event.TreeExpansionEvent)
         */
        public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
            handleContextTreeExpanded(event);
        }
    }

    /**
     * @param parent
     * @param treeExplorer
     * @param model
     * @param context
     * @param singleSelection
     */
    public ResourceSelectorDialog(Frame parent, int selectType,
		       SessionTreeExplorer treeExplorer, SessionModel model, ResourceId context,
			   boolean singleSelection) {
        super(parent, VERTICAL);
        init(selectType, treeExplorer, model, context, singleSelection);
    }

    /**
     * @param parent
     * @param treeExplorer
     * @param model
     * @param context
     * @param singleSelection
     */
    public ResourceSelectorDialog(Dialog parent, int selectType,
		       SessionTreeExplorer treeExplorer, SessionModel model, ResourceId context,
			   boolean singleSelection) {
        super(parent, VERTICAL);
        init(selectType, treeExplorer, model, context, singleSelection);
    }

	/**
	 * @return the result of the editing process
	 */
	public List<ResourceId> getResult() {
	    return fResult;
	}

    /**
     * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
     */
    protected Component[] getDisplayPanels() {
    	if(fSelectType == SELECT_COUNTER) {
    		return new Component[]{fPanelSpace, fPanelResources};
    	} else {
    		return new Component[]{fPanelSpace};
    	}
    }

    /**
     * @see com.ixora.common.ui.AppDialog#getButtons()
     */
    protected JButton[] getButtons() {
        return new JButton[]{
        		UIFactoryMgr.createButton(
        			fSelectType == SELECT_COUNTER ?
        				fActionOk :
        					fActionSelect),
             UIFactoryMgr.createButton(new ActionCancel() {
				public void actionPerformed(ActionEvent e) {
					handleCancel();
				}
             })};
    }

	/**
	 * @param te
	 * @param model
	 * @param context
	 * @param singleSelection
	 * @param qgr
	 * @param scb
	 */
	private void init(
			int selectType,
	        SessionTreeExplorer te,
	        SessionModel model,
	        ResourceId context,
	        boolean singleSelection) {
	    setTitle(MessageRepository.get(Msg.TITLE_RESOURCE_SELECTOR));
	    setModal(true);
		fTreeExplorer = te;
		fSessionModel = model;
		fContext = context;
		fSelectType = selectType;
		fEventHandler = new EventHandler();
		fActionOk = new ActionOk() {
			public void actionPerformed(ActionEvent e) {
				handleOk();
			}
         };
		fActionSelect = new ActionSelect() {
			public void actionPerformed(ActionEvent e) {
				handleOk();
			}
         };
		fTreeContext = UIFactoryMgr.createTree();
		fTreeContext.setModel(fSessionModel);
		fTreeContext.setCellRenderer(
		        new SessionModelTreeCellRendererContextAware(context));

		// panel with resources for the node selected
		// in the space tree
		fPanelResources = new JPanel(new BorderLayout());
		fPanelResources.setBorder(
				UIFactoryMgr.createTitledBorder(MessageRepository.get(
						Msg.TEXT_RESOURCES)));
		fResourcesModel = new ResourcesTableModel(singleSelection);
		fTableResources = new JTable(fResourcesModel);
		fTableResources.getColumnModel().getColumn(0).setMaxWidth(25);
		JScrollPane scrollResources = new JScrollPane(fTableResources);
		scrollResources.setPreferredSize(new Dimension(200, 100));
		fPanelResources.add(scrollResources, BorderLayout.CENTER);

		// query group space panel holding the nodes that the
		// edited query group can access
		fPanelSpace = new JPanel(new BorderLayout());
		fPanelSpace.setBorder(
			       UIFactoryMgr.createTitledBorder(MessageRepository.get(
							Msg.TEXT_RESOURCE_TREE_CONTEXT)));
		JScrollPane scrollTree = new JScrollPane(fTreeContext);
		scrollTree.setPreferredSize(new Dimension(300, 100));
		fPanelSpace.add(scrollTree, BorderLayout.CENTER);

		// expand the tree to display the begining of the
		// current context and select the node
		DefaultMutableTreeNode node = model.getNodeForResourceId(context);
		if(node == null) {
			// TODO localize (it happens if the agent has already been removed
			// from the session model)
			throw new AppRuntimeException("Couldn't find node for context \""
					+  context == null ? "root" : context + "\"");
		}
		TreePath tp = new TreePath(node.getPath());
		fTreeContext.scrollPathToVisible(tp);

		fTreeContext.getSelectionModel().addTreeSelectionListener(fEventHandler);
		fTreeContext.addTreeWillExpandListener(fEventHandler);

		// this must be here so that the listener will pick up
		// the selection event
		fTreeContext.setSelectionPath(tp);
		fTreeContext.expandPath(tp);

		buildContentPane();
		setPreferredSize(new Dimension(550, 450));
	}

    /**
     * @param ev
     */
    private void handleContextTreeSelected(TreeSelectionEvent ev) {
        try {
            TreePath path = ev.getNewLeadSelectionPath();
            if(path == null) {
                return;
            }
            this.fLastSelectedNode = (SessionModelTreeNode)path
            	.getLastPathComponent();
            ResourceId rid = this.fLastSelectedNode.getResourceId();
	        if(fContext != null &&
	                (rid == null || !rid.contains(fContext))) {
	            // outside context
	            fResourcesModel.removeAll();
	            if(fSelectType == SELECT_ENTITY) {
	            	fActionSelect.setEnabled(false);
	            }
	            return;
	        }
            if(fSelectType == SELECT_ENTITY) {
            	fActionSelect.setEnabled(true);
            }
            if(!(fLastSelectedNode instanceof EntityNode)) {
	            // no counters
            	fResourcesModel.removeAll();
            	return;
            }
            EntityNode entityNode = (EntityNode)fLastSelectedNode;
            List<ResourceId> rids = new LinkedList<ResourceId>();
            Collection<CounterInfo> counters = entityNode.getEntityInfo().getCounterInfo();
            for(CounterInfo counter : counters) {
            	ResourceId cid = new ResourceId(rid.getHostId(),
            			rid.getAgentId(), rid.getEntityId(), counter.getId());
            	rids.add(cid.getRelativeTo(fContext));
            }
            fResourcesModel.setResourceIds(rids);
        } catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
    }

	/**
	 * Handles the tree expanded event.
	 * @param ev
	 */
	private void handleContextTreeExpanded(final TreeExpansionEvent ev) {
		try {
			TreePath sp = ev.getPath();
			if(sp == null) {
				return;
			}
			Object o = sp.getLastPathComponent();
			fTreeExplorer.expandNode(this, fSessionModel, (SessionModelTreeNode)o);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Applies the changes.
	 */
    private void handleOk() {
        try {
        	if(fSelectType == SELECT_COUNTER) {
	        	fResult = fResourcesModel.getSelectedResourceIds();
	            if(Utils.isEmptyCollection(fResult)) {
	                // make the selected one the result
	                int row = fTableResources.getSelectedRow();
	                if(row >= 0) {
	                    ResourceId rid = fResourcesModel.getResourceAt(row);
	                    fResult = new ArrayList(1);
	                    fResult.add(rid);
	                }
	            }
        	} else {
        		if(fLastSelectedNode != null) {
		            ResourceId rid = this.fLastSelectedNode.getResourceId();
		            if(rid != null) {
			            if(fSelectType == SELECT_ENTITY) {
			            	fResult = new ArrayList(1);
			            	fResult.add(rid);
			            }
		            }
        		}
        	}
        	dispose();
        } catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
    }

	/**
	 * Applies the changes.
	 */
    private void handleCancel() {
        try {
            this.dispose();
        } catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
    }

    /**
     * @see java.awt.Window#dispose()
     */
    public void dispose() {
        // this will remove the tree as a listener
        // from the session model
        fTreeContext.setModel(null);
        super.dispose();
    }
}
