/*
 * Created on 18-Aug-2004
 */
package com.ixora.rms.ui.artefacts.dashboard;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.ListCellRenderer;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import com.ixora.common.MessageRepository;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionDown;
import com.ixora.common.ui.actions.ActionOk;
import com.ixora.common.ui.actions.ActionUp;
import com.ixora.common.ui.exception.InvalidFormData;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.rms.MonitoringLevel;
import com.ixora.rms.ResourceId;
import com.ixora.rms.client.model.ArtefactInfoContainer;
import com.ixora.rms.client.model.CounterInfo;
import com.ixora.rms.client.model.DataViewInfo;
import com.ixora.rms.client.model.EntityNode;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.client.model.SessionModelTreeNode;
import com.ixora.rms.repository.AuthoredArtefactHelper;
import com.ixora.rms.repository.Dashboard;
import com.ixora.rms.repository.DashboardId;
import com.ixora.rms.repository.DashboardMap;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.repository.DataViewId;
import com.ixora.rms.repository.exception.ArtefactSaveConflict;
import com.ixora.rms.services.DashboardRepositoryService;
import com.ixora.rms.ui.AgentVersionsSelectorPanel;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.SessionModelTreeCellRendererContextAware;
import com.ixora.rms.ui.SessionTreeExplorer;
import com.ixora.rms.ui.actions.ActionFromXML;
import com.ixora.rms.ui.artefacts.dashboard.exception.SaveConflict;
import com.ixora.rms.ui.artefacts.dashboard.messages.Msg;
import com.ixora.rms.ui.dataviewboard.charts.definitions.ChartDef;
import com.ixora.rms.ui.dataviewboard.properties.definitions.PropertiesDef;
import com.ixora.rms.ui.dataviewboard.tables.definitions.TableDef;

/**
 * @author Daniel Moraru
 */
@SuppressWarnings("serial")
final class DashboardEditorDialog extends AppDialog {
	private static final long serialVersionUID = 7553335832657917792L;
	/** Logger */
    private static final AppLogger logger = AppLoggerFactory.getLogger(DashboardEditorDialog.class);

    /**
     * Save callback.
     */
    public interface Listener {
        /**
         * @param rid
         * @param group
         */
        void savedDashboard(ResourceId context, Dashboard group);
    }

	/** Cached icon for the dashboard editor dialog */
//	private static final ImageIcon iconDashboardEditor =
//			UIConfiguration.getIcon("dashboard_editor.gif");

// controls
    private FormPanel formNameDescVersions;
    private JTree jTreeContext;
    private JList jListViews;
    private JList jListCounters;
    private JList jListDashboardItems;
    private JPanel jPanelCenter;
    private JPanel jPanelDashboardItems;
	private JTabbedPane jTabbedPaneCountersAndViews;
	private AgentVersionsSelectorPanel fAgentSelectorPanel;

// cached keys in the form panel
    private String fKeyName;
    private String fKeyDesc;
    private String fKeyAuthor;
    private String fKeyAgentVersions;

    /** View container */
    private RMSViewContainer fViewContainer;
    /** Event handler */
    private EventHandler fEventHandler;
    /** Session model */
    private SessionModel fSessionModel;
    /** Session tree explorer */
    private SessionTreeExplorer fTreeExplorer;
    /** List of views model */
    private DefaultListModel fViewsModel;
    /** List of counters model */
    private DefaultListModel fCountersModel;
    /** List of chosen views model */
    private DefaultListModel fDashboardItemsModel;
    /** The context of this editing session */
    private ResourceId fContext;
	/** Reference to the repository service */
	private DashboardRepositoryService fDashboardRepository;
	/** Last selected tree node */
	private SessionModelTreeNode fLastSelectedNode;
	/** Original dashboard, before editing */
	private Dashboard fOriginalDashboard;
	/** Save listener */
	private Listener fListener;
	/** Title prefx */
	private String fTitlePrefix;
	/** Agent version for the current context */
    private String fSUOVersion;
	/** All agent versions available for this agent */
    private Set<String> fAllSUOVersions;

// actions
	private Action fActionAddView = new ActionAddItemToDashboard();
	private Action fActionRemoveView = new ActionRemoveViewFromDashboard();
	private Action fActionOk = new ActionOk() {
		public void actionPerformed(ActionEvent e) {
			handleOk();
		}
	};
	private Action fActionCancel = new ActionCancel() {
		public void actionPerformed(ActionEvent e) {
			handleCancel();
		}
	};
	private Action fActionFromXML;
	private Action fActionItemUp = new ActionUp(){
		public void actionPerformed(ActionEvent e) {
			handleMoveDashboardItem(false);
		}
	};
	private Action fActionItemDown = new ActionDown(){
		public void actionPerformed(ActionEvent e) {
			handleMoveDashboardItem(true);
		}
	};

    private JTextField fTextFieldName;
    private JTextField fTextFieldDescription;
    private JTextField fTextFieldAuthor;


	/**
	 * Adds an item to the dashboard.
	 */
	private final class ActionAddItemToDashboard extends AbstractAction {
		public ActionAddItemToDashboard() {
			super();
			putValue(Action.NAME, "+");
			//putValue(Action.SMALL_ICON, UIConfiguration.getIcon("select.gif"));
			this.enabled = false;
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
		    if(jTabbedPaneCountersAndViews.getComponentAt(0).isVisible()) {
		        handleAddViewToDashboard();
		        return;
		    }
		    if(jTabbedPaneCountersAndViews.getComponentAt(1).isVisible()) {
		        handleAddCounterToDashboard();
		        return;
		    }
		}
	}

	/**
	 * Removes an item from the dashboard.
	 */
	private final class ActionRemoveViewFromDashboard extends AbstractAction {
		public ActionRemoveViewFromDashboard() {
			super();
			putValue(Action.NAME, "-");
			//putValue(Action.SMALL_ICON, UIConfiguration.getIcon("deselect.gif"));
			this.enabled = false;
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleRemoveItemFromDashboard();
		}
 	}

    /**
     * CounterData.
     */
    private final static class CounterData {
        CounterInfo fCounterInfo;

        CounterData(CounterInfo ci) {
            fCounterInfo = ci;
        }

        /**
         * @see java.lang.Object#toString()
         */
        public String toString() {
            return fCounterInfo.toString();
        }
    }
    
    private final static class ViewsListRenderer extends JLabel implements ListCellRenderer {
    	private static final ImageIcon fIconChart = UIConfiguration.getIcon("chartsboard.gif");
    	private static final ImageIcon fIconTable = UIConfiguration.getIcon("tablesboard.gif");
    	private static final ImageIcon fIconProperties = UIConfiguration.getIcon("propertiesboard.gif");
    	
		public Component getListCellRendererComponent(JList list, Object value,
				int index, boolean isSelected, boolean cellHasFocus) {
			DataViewInfo dvi = (DataViewInfo)value;
			setText(dvi.toString());
			DataView dv = dvi.getDataView();
			if(dv instanceof ChartDef) {
				setIcon(fIconChart);
			} else if(dv instanceof TableDef) {
				setIcon(fIconTable);
			} else if(dv instanceof PropertiesDef) {
				setIcon(fIconProperties);
			}  else {
				setIcon(null);
			}
			return this;
		}    	
    }

    /**
     * CounterListCellRenderer.
     */
    private final static class CounterListCellRenderer extends DefaultListCellRenderer {
        private static final ImageIcon fIconLow = UIConfiguration.getIcon("monlevel_low.gif");
        private static final ImageIcon fIconMedium = UIConfiguration.getIcon("monlevel_medium.gif");
        private static final ImageIcon fIconHigh = UIConfiguration.getIcon("monlevel_high.gif");
        private static final ImageIcon fIconMaximum = UIConfiguration.getIcon("monlevel_max.gif");

        /**
         * @see javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
         */
        public java.awt.Component getListCellRendererComponent(JList list,
                Object value,
                int index,
                boolean isSelected,
                boolean cellHasFocus) {
            JLabel ret = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            CounterData item = (CounterData)value;
            MonitoringLevel level = item.fCounterInfo.getLevel();
            if(level == null) {
                return ret;
            }
            if(level == MonitoringLevel.HIGH) {
                ret.setIcon(fIconHigh);
            } else if(level == MonitoringLevel.LOW) {
                ret.setIcon(fIconLow);
            } else if(level == MonitoringLevel.MAXIMUM) {
                ret.setIcon(fIconMaximum);
            } else if(level == MonitoringLevel.MEDIUM) {
                ret.setIcon(fIconMedium);
            }
            return ret;
        }

    }

	/**
	 * DashboardItemData
	 */
	private static final class DashboardItemData {
		/** The item can be a ResourceId or a DataViewId */
		Object fItem;
		/** The translated name of the item */
		private String fTranslatedItemName;

		DashboardItemData(ResourceId item, CounterInfo ci) {
			super();
			fItem = item;
            if(ci != null) {
                fTranslatedItemName = ci.getTranslatedName();
            }
		}

		DashboardItemData(DataViewId item, DataViewInfo dvi) {
			super();
			fItem = item;
            if(dvi != null) {
                fTranslatedItemName = dvi.getTranslatedName();
            }
		}

		/**
		 * @see java.lang.Object#toString()
		 */
		public String toString() {
            if(fTranslatedItemName != null) {
                return "[" + fTranslatedItemName + "] " + fItem;
            } else {
                return fItem.toString();
            }
		}

		/**
		 * @see java.lang.Object#equals(java.lang.Object)
		 */
		public boolean equals(Object obj) {
			if(obj == null) {
				return false;
			}
			if(!(obj instanceof DashboardItemData)) {
				return false;
			}
			DashboardItemData that = (DashboardItemData)obj;
			if(that.fItem.equals(fItem)) {
				return true;
			}
			return false;
		}

		/**
		 * @see java.lang.Object#hashCode()
		 */
		public int hashCode() {
			return fItem.hashCode();
		}
	}

	/**
     * Event handler.
     */
    private final class EventHandler extends MouseAdapter
    		implements TreeSelectionListener,
    			TreeWillExpandListener,
    				ListSelectionListener {
        /**
         * @see javax.swing.event.TreeSelectionListener#valueChanged(javax.swing.event.TreeSelectionEvent)
         */
        public void valueChanged(TreeSelectionEvent e) {
            handleContextTreeSelected(e);
        }
        /**
         * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
         */
        public void valueChanged(ListSelectionEvent e) {
            if(e.getSource() == jListViews.getSelectionModel()
                    || e.getSource() == jListCounters.getSelectionModel()) {
                handleViewOrCounterSelected();
                return;
            }
            if(e.getSource() == jListDashboardItems.getSelectionModel()) {
                handleDashboardItemSelected();
                return;
            }
        }

        /**
         * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
         */
        public void mouseClicked(MouseEvent e) {
            if(e.getSource() == jListViews) {
                if (e.getClickCount() == 2) {
                    int index = jListViews.locationToIndex(
                            e.getPoint());
                    jListViews.setSelectedIndex(index);
                    handleAddViewToDashboard();
                }
                return;
            }
            if(e.getSource() == jListCounters) {
                if (e.getClickCount() == 2) {
                    int index = jListCounters.locationToIndex(
                            e.getPoint());
                    jListCounters.setSelectedIndex(index);
                    handleAddCounterToDashboard();
                }
                return;
            }
            if(e.getSource() == jListDashboardItems) {
                if (e.getClickCount() == 2) {
                    handleRemoveItemFromDashboard();
                }
            }
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
     * Constructor.
     * @param parent
     * @param treeExplorer
     * @param serviceQueryGroupRepository
     * @param listener
     */
    public DashboardEditorDialog(RMSViewContainer parent,
		       SessionTreeExplorer treeExplorer,
		       DashboardRepositoryService serviceQueryGroupRepository,
		       Listener listener) {
        super(parent.getAppFrame(), VERTICAL);
        init(parent, treeExplorer, serviceQueryGroupRepository, listener);
    }

	/**
	 * Edits the given dashboard or creates a new one.
	 * @param model
	 * @param context the entity to which this dashboard belongs, if
	 * <code>context</code> is null the dashboard is global, if not null
	 * the dashboard belongs to the resource described by the given id.
     * @param allAvailableSUOVersions
	 * @param db the dashboard to edit if <code>db</code> is null a new
	 * dashboard will be created
	 */
	public void edit(
	        SessionModel model,
			ResourceId context,
            Set<String> allAvailableSUOVersions,
			Dashboard db) {
		try {
            fActionItemUp.setEnabled(false);
            fActionItemDown.setEnabled(false);
            fActionAddView.setEnabled(false);
            fActionRemoveView.setEnabled(false);

			// set the current query context
			fContext = context;
			fSessionModel = model;
            fSUOVersion = fSessionModel.getAgentVersionInContext(context);
            fAllSUOVersions = allAvailableSUOVersions;

            // disable versioning if outside an agent context or there are no
            // multiple agent versions
            if(fSUOVersion == null || Utils.isEmptyCollection(allAvailableSUOVersions)) {
                formNameDescVersions.setPairs(
                       new String[]{fKeyName, fKeyDesc, fKeyAuthor},
                       new Component[]{fTextFieldName, fTextFieldDescription, fTextFieldAuthor});
                fAgentSelectorPanel.setSelectedAgentVersions(null);
            } else {
                fAgentSelectorPanel.setAvailableAgentVersions(allAvailableSUOVersions);
                Set<String> dashSUOVersions = null;
                if(db != null) {
	                dashSUOVersions = db.getAgentVersions();
	                // remove SUOVersions which do not exist for this agent
	                // (this can happen when a dashboard is imported from xml or an old dashboard
	                // was picked up by a new version of the agent)
	                if(!Utils.isEmptyCollection(dashSUOVersions)) {
	                	if(Utils.isEmptyCollection(fAllSUOVersions)) {
	                		dashSUOVersions = null;
	                	}
	                	for(Iterator<String> iter = dashSUOVersions.iterator(); iter.hasNext();) {
	                		if(!fAllSUOVersions.contains(iter.next())) {
	                			iter.remove();
	                		}
	                	}
	                }
                }
                fAgentSelectorPanel.setSelectedAgentVersions(dashSUOVersions);
                formNameDescVersions.setPairs(
                        new String[]{fKeyName, fKeyDesc, fKeyAgentVersions, fKeyAuthor},
                        new Component[]{fTextFieldName, fTextFieldDescription, fAgentSelectorPanel, fTextFieldAuthor});
            }

            jTreeContext.setModel(model);
			jTreeContext.setCellRenderer(
			        new SessionModelTreeCellRendererContextAware(context));
			// clear queries lists
			fViewsModel.removeAllElements();
			fDashboardItemsModel.removeAllElements();
			// if dashboard not null set up controls
			if(db != null) {
			    fTextFieldName.setText(db.getName());
			    fTextFieldDescription.setText(db.getDescription());
			    fTextFieldAuthor.setText(db.getAuthor());

			    this.fOriginalDashboard = (Dashboard)Utils.deepClone(db);
				// and add the current's dashboard views
				DataViewId[] views = db.getViews();
				if(views != null) {
				    for(DataViewId view : views) {
				        if(view != null) {
                            // find DataViewInfo
                            DataViewId completedViewId = view.complete(fContext);
                            DataViewInfo dvi = fSessionModel.getDataViewInfo(completedViewId, false);
                            if(dvi == null) {
                                // this could happen if the dashboard contains a data view which was
                                // deleted
                                // TODO localize
                                fViewContainer.getAppStatusBar().setErrorMessage("Data view " + completedViewId + " for dashboard "
                                        + db.getName() + " was not found.", null);
                            }
				            fDashboardItemsModel.addElement(
                                    new DashboardItemData(view, dvi));
				        }
	                }
				}
				// and counters
				ResourceId[] counters = db.getCounters();
				if(counters != null) {
				    for(ResourceId counter : counters) {
				        if(counter != null) {
                            CounterInfo ci = fSessionModel.getCounterInfo(counter.complete(fContext), false);
                            if(ci == null) {
                                // that could happen as the search for counter was not aggressive...
                                if(logger.isTraceEnabled()) {
                                    logger.error("Counter " + counter.complete(fContext)
                                            + " for dashboard " + db + " was not found");
                                }
                            }
                            fDashboardItemsModel.addElement(new DashboardItemData(counter, ci));
				        }
	                }
				}
			} else {
			    this.fOriginalDashboard = null;
                // set the current suoVersion
                if(fSUOVersion != null) {
                    Set<String> suoVersion = new HashSet<String>();
                    suoVersion.add(fSUOVersion);
                    fAgentSelectorPanel.setSelectedAgentVersions(suoVersion);
                }
			}
			// expand the tree to display the begining of the
			// current context and select the node
			DefaultMutableTreeNode node = model.getNodeForResourceId(context);
			TreePath tp = new TreePath(node.getPath());
			jTreeContext.scrollPathToVisible(tp);
			jTreeContext.setSelectionPath(tp);

			updateTitle(fContext, db);

			repaintDisplay();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @param context
	 * @param db
	 */
	private void updateTitle(ResourceId context, Dashboard db) {
		setTitle(MessageRepository.get(Msg.TITLE_DASHBOARD_EDITOR)
				+ ": " + new DashboardId(context, db == null ? "-" : db.getName()));
	}

    /**
     * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
     */
    protected Component[] getDisplayPanels() {
        return new Component[]{formNameDescVersions,
                jPanelCenter, jPanelDashboardItems};
    }

    /**
     * @see com.ixora.common.ui.AppDialog#getButtons()
     */
    protected JButton[] getButtons() {
        return new JButton[]{
             UIFactoryMgr.createButton(fActionOk),
             UIFactoryMgr.createButton(fActionFromXML),
             UIFactoryMgr.createButton(fActionCancel)};
    }

	/**
	 * @param vc
	 * @param te
	 * @param qgr
	 * @param listener
	 */
	private void init(RMSViewContainer vc,
	        SessionTreeExplorer te,
	        DashboardRepositoryService qgr,
	        Listener listener) {
	    fTitlePrefix = MessageRepository.get(Msg.TITLE_DASHBOARD_EDITOR);
	    setTitle(fTitlePrefix);
		fViewContainer = vc;
		fActionFromXML = new ActionFromXML(vc) {
			protected void handleXML(String data) throws Exception {
				loadDataFromXML(data);
			}
		};
		fListener = listener;
		this.fTreeExplorer = te;
		fDashboardRepository = qgr;
		fEventHandler = new EventHandler();
		fViewsModel = new DefaultListModel();
		jListViews = UIFactoryMgr.createList(fViewsModel);
		jListViews.setCellRenderer(new ViewsListRenderer());
		fCountersModel = new DefaultListModel();
		jListCounters = UIFactoryMgr.createList(fCountersModel);
        jListCounters.setCellRenderer(new CounterListCellRenderer());
		fDashboardItemsModel = new DefaultListModel();
		jListDashboardItems = UIFactoryMgr.createList(fDashboardItemsModel);
		jTreeContext = UIFactoryMgr.createTree();
		jTreeContext.setModel(null);

		fTextFieldName = UIFactoryMgr.createTextField();
        fTextFieldDescription = UIFactoryMgr.createTextField();
        fTextFieldAuthor = UIFactoryMgr.createTextField();
        fAgentSelectorPanel = new AgentVersionsSelectorPanel(this, null);
		formNameDescVersions = new FormPanel(FormPanel.VERTICAL1);
		fKeyName = MessageRepository.get(Msg.TEXT_DASHBOARD_NAME);
		fKeyDesc = MessageRepository.get(Msg.TEXT_DASHBOARD_DESCRIPTION);
		fKeyAgentVersions = MessageRepository.get(Msg.TEXT_DASHBOARD_AGENT_VERSIONS);
		fKeyAuthor = MessageRepository.get(Msg.TEXT_DASHBOARD_AUTHOR);

		// views panel with views for the node selected
		// in the space tree
		JPanel jPanelViews = new JPanel(new BorderLayout());
		JScrollPane scrollViews = new JScrollPane(jListViews);
		scrollViews.setPreferredSize(new Dimension(200, 100));
		jPanelViews.add(scrollViews, BorderLayout.CENTER);

		// views panel with counters for the entity node selected
		// in the space tree
		JPanel jPanelCounters = new JPanel(new BorderLayout());
		JScrollPane scrollCounters = new JScrollPane(jListCounters);
		scrollCounters.setPreferredSize(new Dimension(300, 100));
		jPanelCounters.add(scrollCounters, BorderLayout.CENTER);

		jTabbedPaneCountersAndViews = UIFactoryMgr.createTabbedPane();
		jTabbedPaneCountersAndViews.add(MessageRepository.get(Msg.TEXT_VIEWS), jPanelViews);
		jTabbedPaneCountersAndViews.add(MessageRepository.get(Msg.TEXT_COUNTERS), jPanelCounters);

		// query group space panel holding the nodes that the
		// edited query group can access
		JPanel jPanelSpace = new JPanel(new BorderLayout());
		jPanelSpace.setBorder(
			       UIFactoryMgr.createTitledBorder(MessageRepository.get(
							Msg.TEXT_DASHBOARD_CONTEXT)));
		JScrollPane scroll = new JScrollPane(jTreeContext);
		scroll.setPreferredSize(new Dimension(300, 100));
		jPanelSpace.add(scroll, BorderLayout.CENTER);

		// combined panel holding the space and node items list panels
		jPanelCenter = new JPanel();
		jPanelCenter.setLayout(new BoxLayout(jPanelCenter, BoxLayout.X_AXIS));
		jPanelCenter.add(jPanelSpace);
		jPanelCenter.add(Box.createHorizontalStrut(UIConfiguration.getPanelPadding()));
		jPanelCenter.add(jTabbedPaneCountersAndViews);

		// the panel holding the list with chosen dashboard items (views and counters)
		jPanelDashboardItems = new JPanel(new BorderLayout());
		// the panel holding the select and deselect buttons
		JPanel jPanelButtons = new JPanel();
		jPanelButtons.add(
		      UIFactoryMgr.createButton(fActionRemoveView));
		jPanelButtons.add(
		      UIFactoryMgr.createButton(fActionAddView));
		jPanelDashboardItems.add(jPanelButtons, BorderLayout.NORTH);


        JPanel jPanelDashboardItemsList = new JPanel(new BorderLayout());
        scroll = new JScrollPane(jListDashboardItems);
        scroll.setPreferredSize(new Dimension(400, 50));
        JPanel scrollPanel = new JPanel(new BorderLayout());
        scrollPanel.add(scroll, BorderLayout.CENTER);

        jPanelDashboardItemsList.setBorder(
                UIFactoryMgr.createTitledBorder(
                     MessageRepository.get(
                         Msg.TEXT_DASHBOARD_ITEMS)));
        jPanelDashboardItemsList.add(scrollPanel, BorderLayout.CENTER);
        JButton jButtonUp = UIFactoryMgr.createButton(fActionItemUp);
        JButton jButtonDown = UIFactoryMgr.createButton(fActionItemDown);

        int padding = UIConfiguration.getPanelPadding();
        JPanel jPanelUpDown = new JPanel(new GridLayout(4, 1, 0, padding));
        jPanelUpDown.setBorder(BorderFactory.createEmptyBorder(padding, padding, padding, padding));
        jPanelUpDown.add(jButtonUp);
        jPanelUpDown.add(jButtonDown);
        JPanel jPanelUpDownExt = new JPanel(new BorderLayout());
        jPanelUpDownExt.add(jPanelUpDown, BorderLayout.NORTH);

        jPanelDashboardItemsList.add(jPanelUpDownExt, BorderLayout.EAST);
        jPanelDashboardItems.add(jPanelDashboardItemsList, BorderLayout.CENTER);

		// register listeners
		jTreeContext.addTreeWillExpandListener(fEventHandler);
		jTreeContext.getSelectionModel().addTreeSelectionListener(fEventHandler);
		jListViews.getSelectionModel().addListSelectionListener(fEventHandler);
		jListCounters.getSelectionModel().addListSelectionListener(fEventHandler);
		jListDashboardItems.getSelectionModel().addListSelectionListener(fEventHandler);
		jListViews.addMouseListener(fEventHandler);
		jListCounters.addMouseListener(fEventHandler);
		jListDashboardItems.addMouseListener(fEventHandler);

		buildContentPane();
		setPreferredSize(new Dimension(650, 500));
	}

	/**
	 * @param down
	 */
    private void handleMoveDashboardItem(boolean down) {
		try {
            int sel = jListDashboardItems.getSelectedIndex();
            if(sel < 0) {
                return;
            }
            if(down) {
                if(sel == fDashboardItemsModel.getSize() - 1) {
                    return;
                }
                Object obj = fDashboardItemsModel.remove(sel);
                if(obj != null) {
                    fDashboardItemsModel.insertElementAt(obj, ++sel);
                    jListDashboardItems.setSelectedIndex(sel);
                    jListDashboardItems.ensureIndexIsVisible(sel);
                }
            } else {
                if(sel == 0) {
                    return;
                }
                Object obj = fDashboardItemsModel.remove(sel);
                if(obj != null) {
                    fDashboardItemsModel.insertElementAt(obj, --sel);
                    jListDashboardItems.setSelectedIndex(sel);
                    jListDashboardItems.ensureIndexIsVisible(sel);
                }
            }
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
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
	            fViewsModel.removeAllElements();
	            fCountersModel.removeAllElements();
	            return;
	        }
            boolean hasViews = false;
            boolean hasCounters = false;
	        // get views first
	        ArtefactInfoContainer aic = this.fLastSelectedNode
            	.getArtefactInfoContainer();
            Collection<DataViewInfo> col = aic.getDataViewInfo();
            fViewsModel.removeAllElements();
            if(!Utils.isEmptyCollection(col)) {
        		jTabbedPaneCountersAndViews.setEnabledAt(0, true);
	            for(DataViewInfo dvi : col) {
	                fViewsModel.addElement(dvi);
	            }
	            hasViews = true;
            } else {
        		jTabbedPaneCountersAndViews.setEnabledAt(0, false);
            }
            // and now get counters if selected node is an entity
            fCountersModel.removeAllElements();
            if(this.fLastSelectedNode instanceof EntityNode) {
            	EntityNode en = (EntityNode)this.fLastSelectedNode;
            	Collection<CounterInfo> cis = en.getEntityInfo().getCounterInfo();
            	if(!Utils.isEmptyCollection(cis)) {
            		jTabbedPaneCountersAndViews.setEnabledAt(1, true);
            		for(CounterInfo ci : cis) {
            			fCountersModel.addElement(new CounterData(ci));
            		}
            		hasCounters = true;
            	}
            } else {
            	fCountersModel.removeAllElements();
            	jTabbedPaneCountersAndViews.setEnabledAt(1, false);
            }

            // set selected tab the one which has elements giving priority to
            // views
            if(hasViews) {
                jTabbedPaneCountersAndViews.setSelectedIndex(0);
            } else {
                if(hasCounters) {
                    jTabbedPaneCountersAndViews.setSelectedIndex(1);
                }
            }
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
            // check if name is valid
            String dashboardName = fTextFieldName.getText().trim();
            if(Utils.isEmptyString(dashboardName)) {
                throw new InvalidFormData(MessageRepository.get(Msg.TEXT_DASHBOARD_NAME));
            }
            String dashboardDescription = fTextFieldDescription.getText();
            String author = fTextFieldAuthor.getText();
            // build the dasboard
            Object[] objs = this.fDashboardItemsModel.toArray();
            List<ResourceId> counters = new LinkedList<ResourceId>();
            List<DataViewId> views = new LinkedList<DataViewId>();
            for(Object o : objs) {
            	DashboardItemData did = (DashboardItemData)o;
                if(did.fItem instanceof ResourceId) {
                    counters.add((ResourceId)did.fItem);
                } else if(did.fItem instanceof DataViewId) {
                    views.add((DataViewId)did.fItem);
                }
            }
            Dashboard dashboard = new Dashboard(
                    dashboardName,
                    dashboardDescription,
                    author,
                    views,
                    counters,
                    fAgentSelectorPanel == null ? null : fAgentSelectorPanel.getSelectedAgentVersions());

    		// check author info
    		AuthoredArtefactHelper.checkArtefact(dashboard);

            DashboardMap map = fDashboardRepository.getDashboardMap(fContext);
            if(map == null) {
                map = new DashboardMap();
            }
            try {
                map.addOrUpdateWithConflictDetection(fOriginalDashboard, dashboard);
            } catch(ArtefactSaveConflict e) {
                throw new SaveConflict(dashboard.getArtefactName());
            }
            fDashboardRepository.setDashboardMap(fContext, map);
            fDashboardRepository.save();
            updateTitle(fContext, dashboard);

            fListener.savedDashboard(fContext, dashboard);
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
     * Removes the selected query from the query group.
     */
    private void handleRemoveItemFromDashboard() {
        try {
            Object sel = jListDashboardItems.getSelectedValue();
            if(sel == null) {
                return;
            }
            fDashboardItemsModel.removeElement(sel);
        }catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
    }

    /**
     * Adds the selected view as an item of the edited dashboard.
     */
    private void handleAddViewToDashboard() {
        try {
            DataViewInfo dvi = (DataViewInfo)jListViews.getSelectedValue();
            if(dvi == null) {
                return;
            }
	        // generate the path to view resource id
	        // to make it relative to this group's context
	        ResourceId viewContextId = this.fLastSelectedNode.getResourceId();
	        ResourceId relativeId = null;
	        if(this.fContext == null) {
	            relativeId = viewContextId;
            } else {
	        	relativeId = viewContextId.getRelativeTo(this.fContext);
	        }
	        DataViewId dvid = new DataViewId(relativeId, dvi.getDataView().getName());
	        DashboardItemData di = new DashboardItemData(dvid, dvi);
	        if(!this.fDashboardItemsModel.contains(di)) {
	            this.fDashboardItemsModel.addElement(di);
	        }
        }catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
    }

    /**
     * Adds the selected counter as an item of the edited dashboard.
     */
    private void handleAddCounterToDashboard() {
        try {
            CounterData cd = (CounterData)jListCounters.getSelectedValue();
            if(cd == null) {
                return;
            }
            CounterInfo ci = cd.fCounterInfo;
	        // generate the path to view resource id
	        // to make it relative to this group's context
	        ResourceId rid = this.fLastSelectedNode.getResourceId();
	        ResourceId relativeId = null;
	        if(this.fContext == null) {
	            relativeId = rid;
	        } else {
	        	relativeId = rid.getRelativeTo(this.fContext);
	        }
	        ResourceId cid = new ResourceId(
	                relativeId.getHostId(), relativeId.getAgentId(), relativeId.getEntityId(),
	                ci.getId());
	        DashboardItemData di = new DashboardItemData(cid, ci);
	        if(!this.fDashboardItemsModel.contains(di)) {
	            this.fDashboardItemsModel.addElement(di);
	        }
        }catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
    }

    /**
     *
     */
    private void handleDashboardItemSelected() {
        try {
			int sel = jListDashboardItems.getSelectedIndex();
			if(sel < 0) {
				return;
			}
			fActionItemUp.setEnabled(true);
			fActionItemDown.setEnabled(true);
			if(sel == 0) {
				fActionItemUp.setEnabled(false);
			} else if(sel == fDashboardItemsModel.getSize() - 1) {
				fActionItemDown.setEnabled(false);
			}

            if(!fActionRemoveView.isEnabled()) {
                fActionRemoveView.setEnabled(true);
            }
        }catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
    }

    /**
     *
     */
    private void handleViewOrCounterSelected() {
        try {
            if(!fActionAddView.isEnabled()) {
                fActionAddView.setEnabled(true);
        	}
        }catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
    }

	/**
	 * @param data
	 * @throws Exception
	 */
	private void loadDataFromXML(String data) throws Exception {
		Dashboard dashboard = (Dashboard)XMLUtils.fromXMLBuffer(Dashboard.class, new StringBuffer(data), "dashboard");
		edit(fSessionModel, fContext, fAllSUOVersions, dashboard);
	}

    /**
     * @see java.awt.Window#dispose()
     */
    public void dispose() {
        // this will remove the tree as a listener
        // from the session model
        jTreeContext.setModel(null);
        super.dispose();
    }
}
