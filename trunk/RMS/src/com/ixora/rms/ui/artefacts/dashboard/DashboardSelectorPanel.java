/*
 * Created on 06-Jul-2004
 */
package com.ixora.rms.ui.artefacts.dashboard;

import java.awt.Cursor;
import java.util.Collection;
import java.util.List;

import javax.swing.JTable;

import com.ixora.rms.ResourceId;
import com.ixora.common.MessageRepository;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.jobs.UIWorkerJobDefault;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.rms.client.QueryRealizer;
import com.ixora.rms.client.locator.SessionArtefactInfoLocator;
import com.ixora.rms.client.locator.SessionArtefactInfoLocatorImpl;
import com.ixora.rms.client.locator.SessionDataViewInfo;
import com.ixora.rms.client.locator.SessionResourceInfo;
import com.ixora.rms.client.model.ArtefactInfo;
import com.ixora.rms.client.model.ArtefactInfoContainer;
import com.ixora.rms.client.model.DashboardInfo;
import com.ixora.rms.client.model.DataViewInfo;
import com.ixora.rms.client.model.ResourceInfo;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.dataengine.definitions.SingleCounterQueryDef;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.repository.Dashboard;
import com.ixora.rms.repository.DashboardId;
import com.ixora.rms.repository.DashboardMap;
import com.ixora.rms.repository.DataViewId;
import com.ixora.rms.repository.QueryId;
import com.ixora.rms.services.DashboardRepositoryService;
import com.ixora.rms.services.DataViewRepositoryService;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.SessionTreeExplorer;
import com.ixora.rms.ui.artefacts.ArtefactSelectorPanel;
import com.ixora.rms.ui.artefacts.dashboard.messages.Msg;

/**
 * A panel that allows to edit and plot from a set of query groups.
 * @author Daniel Moraru
 */
public final class DashboardSelectorPanel extends ArtefactSelectorPanel<DashboardInfo> {
	private static final long serialVersionUID = 1751373651781903520L;

	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(DashboardSelectorPanel.class);

    /** Callback */
    private Callback callback;
    /** DashboardRepositoryService */
    private DashboardRepositoryService fDashboardRepository;
    /** Tree explorer */
    private SessionTreeExplorer fTreeExplorer;
    /** Event handler */
    private EventHandler fEventHandlef;
    /** Artefact info locator */
    private SessionArtefactInfoLocator fArtefactInfoLocator;

    /**
	 * Callback.
	 */
	public interface Callback {
		/**
		 * Invoked when a request to plot a dashboard was made.
		 * @param di the dashboard
		 */
		void plot(DashboardId did);
	}

	/**
	 * Event handler.
	 */
	private final class EventHandler implements DashboardEditorDialog.Listener {
        /**
         * @see com.ixora.rms.ui.artefacts.dashboard.DashboardEditorDialog.Listener#savedDashboard(ResourceId, com.ixora.rms.repository.QueryGroup)
         */
        public void savedDashboard(ResourceId context, Dashboard group) {
            handleSavedDashboard(context, group);
        }
	}


	/**
     * Constructor used by a log replay session.
     * @param viewContainer
     * @param dashboardRepository
     * @param viewRepository
     * @param sessionData
     * @param treeExplorer
     * @param cb
     */
    public DashboardSelectorPanel(
           RMSViewContainer viewContainer,
           DashboardRepositoryService dashboardRepository,
           DataViewRepositoryService viewRepository,
           SessionModel sessionData,
           SessionTreeExplorer treeExplorer,
           Callback cb) {
        this(viewContainer, dashboardRepository, viewRepository, sessionData, null,
                treeExplorer, cb, true);
    }

    /**
     * Constructor used by a live session.
     * @param viewContainer
     * @param dashboardRepository
     * @param viewRepository
     * @param sessionData
     * @param queryRealizer
     * @param treeExplorer
     * @param cb
     * @param b
     */
    public DashboardSelectorPanel(
           RMSViewContainer viewContainer,
           DashboardRepositoryService dashboardRepository,
           DataViewRepositoryService viewRepository,
           SessionModel sessionData,
           QueryRealizer queryRealizer,
           SessionTreeExplorer treeExplorer,
           Callback cb) {
        this(viewContainer, dashboardRepository, viewRepository, sessionData, queryRealizer,
                treeExplorer, cb, false);
    }

    /**
     * Constructor.
     * @param viewContainer
     * @param dashboardRepository
     * @param viewRepository
     * @param sessionData
     * @param queryRealizer
     * @param treeExplorer
     * @param cb
     * @param logReplayMode
     */
    private DashboardSelectorPanel(
           RMSViewContainer viewContainer,
           DashboardRepositoryService dashboardRepository,
           DataViewRepositoryService viewRepository,
           SessionModel sessionData,
           QueryRealizer queryRealizer,
           SessionTreeExplorer treeExplorer,
           Callback cb,
           boolean logReplayMode) {
   		super(viewContainer,
				sessionData, queryRealizer,
				new DashboardTableModel(viewContainer,
				        sessionData, logReplayMode),
				Msg.ACTIONS_ADD_DASHBOARD,
				Msg.ACTIONS_REMOVE_DASHBOARD,
				Msg.ACTIONS_EDIT_DASHBOARD,
				Msg.ACTIONS_PLOT_DASHBOARD);
   		this.fTreeExplorer = treeExplorer;
   		this.callback = cb;
   		this.fDashboardRepository = dashboardRepository;
   		this.fArtefactInfoLocator = new SessionArtefactInfoLocatorImpl(sessionData, viewRepository);
   		this.fEventHandlef = new EventHandler();
    }

    /**
     * @param context
     * @param db
     */
    public void setDashboards(
            ResourceId context, Collection<DashboardInfo> db) {
        this.setArtefacts(context, db);
    }

    /**
     * @see com.ixora.rms.ui.artefacts.ArtefactSelectorPanel#refreshArtefactStatus()
     */
    protected void refreshArtefactStatus() {
        this.fSessionData.getDashboardHelper()
        	.recalculateDashboardsStatus(fContext);
    }

    /**
     * @see com.ixora.rms.ui.artefacts.ArtefactSelectorPanel#uncommittedArtifacts()
     */
    protected boolean uncommittedArtifacts() {
        ArtefactInfoContainer aic = this.fSessionData.getArtefactContainerForResource(fContext, true);
        if(aic == null) {
            logger.error("Couldn't find info container for: " + fContext);
            return false;
        }
        return aic.uncommittedVisibleDashboards();
    }

	/**
	 * Cancels changes.
	 */
	protected void handleCancel() {
		try {
			fSessionData.getDashboardHelper().rollbackDashboards(this.fContext);
			getDashboardTableModel().fireTableDataChanged();
			this.fActionApply.setEnabled(false);
			this.fActionCancel.setEnabled(false);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

    /**
     * @see com.ixora.rms.ui.artefacts.ArtefactSelectorPanel#handleApplyChanges()
     */
    protected void handleApplyChanges() {
        try {
            applyChangesLocally();
		} catch(Exception e) {
			handleCancel();
			UIExceptionMgr.userException(e);
		}
  	}

    /**
     * @see com.ixora.rms.ui.artefacts.ArtefactSelectorPanel#handlePlotArtefact()
     */
    protected void handlePlotArtefact() {
		try {
			this.fViewContainer.runJobSynch(
			        new UIWorkerJobDefault(
			                fViewContainer.getAppFrame(), Cursor.WAIT_CURSOR,
			                // TODO localize
			        		"Plotting dashboard...") {
                public void work() throws Exception {
        			JTable table = getJTableArtefacts();
        			int sel = table.getSelectedRow();
        			if (sel < 0) {
        				return;
        			}
        			DashboardInfo di = (DashboardInfo)
        				table.getModel().getValueAt(sel, 1);

        			Dashboard dtls = di.getDashboard();
        			if(dtls == null) {
        			    logger.error("No dashboard");
        			    return;
        			}

        			DataViewId[] members = dtls.getViews();
        			ResourceId[] counters = dtls.getCounters();
        			if(Utils.isEmptyArray(members)
        					&& Utils.isEmptyArray(counters)) {
        				// TODO localize
        				throw new RMSException(
        						"Dashboard " + di.getTranslatedName()
								+ " has no data views.");
        			}

        			if(!di.getFlag(DataViewInfo.ENABLED) && di.isCommitted()) {
        				// enable it first
        				((DashboardTableModel)fTableModelArtefacts).enableDashboard(sel);
        				applyChangesLocally();
        			}

        			callback.plot(new DashboardId(fContext, dtls.getName()));
                }
                public void finished(Throwable ex) {
                    ; // nothing, synched job
                }
			});
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
    }

    /**
     * @see com.ixora.rms.ui.artefacts.ArtefactSelectorPanel#handleEditArtefact()
     */
    protected void handleEditArtefact() {
		try {
			JTable table = getJTableArtefacts();
			int sel = table.getSelectedRow();
			if(sel < 0) {
				return;
			}
			DashboardInfo cd = (DashboardInfo)
				table.getModel().getValueAt(sel, 1);
			DashboardEditorDialog editor = new DashboardEditorDialog(
			       fViewContainer,
			       fTreeExplorer,
			       fDashboardRepository,
			       fEventHandlef);
			editor.setModal(true);
			editor.edit(fSessionData, fContext, fAllSUOVersions, cd.getDashboard());
			UIUtils.centerDialogAndShow(fViewContainer.getAppFrame(), editor);
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
    }

	/**
	 * @see com.ixora.rms.ui.artefacts.ArtefactSelectorPanel#getXMLDefinitionForSelectedArtefact()
	 */
	protected String getXMLDefinitionForSelectedArtefact() throws Exception {
		JTable table = getJTableArtefacts();
		int sel = table.getSelectedRow();
		if(sel < 0) {
			return "";
		}
		DashboardInfo dashboard = (DashboardInfo)getArtefactInfoAtRow(sel);
		return XMLUtils.toXMLBuffer(null, dashboard.getDashboard(), "rms", false).toString();
	}

    /**
     * @see com.ixora.rms.ui.artefacts.ArtefactSelectorPanel#handleAddArtefact()
     */
    protected void handleAddArtefact() {
		try {
			DashboardEditorDialog editor = new DashboardEditorDialog(
			       fViewContainer,
			       fTreeExplorer,
			       fDashboardRepository,
				   fEventHandlef);
            editor.setModal(true);
			editor.edit(fSessionData, fContext, fAllSUOVersions, null);
			UIUtils.centerDialogAndShow(fViewContainer.getAppFrame(), editor);
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
    }

    /**
     * @see com.ixora.rms.ui.artefacts.ArtefactSelectorPanel#handleRemoveArtefact()
     */
    protected void handleRemoveArtefact() {
		try {
			JTable table = getJTableArtefacts();
			int sel = table.getSelectedRow();
			if(sel < 0) {
				return;
			}
			DashboardInfo gi = (DashboardInfo)
				table.getModel().getValueAt(sel, 1);
			DashboardMap map = fDashboardRepository.getDashboardMap(fContext);
			if(map == null) {
			    logger.error("Couldn't find query gruop map for context: " + this.fContext);
			    return;
			}
			// ask for confitmation
			if(!UIUtils.getBooleanOkCancelInput(this.fViewContainer.getAppFrame(),
					MessageRepository.get(Msg.TITLE_CONFIRM_REMOVE_DASHBOARD),
					MessageRepository.get(Msg.TEXT_CONFIRM_REMOVE_DASHBOARD,
							new String[] {gi.getTranslatedName()}))) {
				return;
			}
			// remove the dashboard only for the current fSUOVersion
			map.remove(gi.getDashboard().getName(), fSUOVersion);
            fDashboardRepository.setDashboardMap(fContext, map);
            fDashboardRepository.save();
			// update model
			fSessionData.getDashboardHelper().removeDashboard(fContext,
			        gi.getDashboard().getName());
			// refresh table model
			refreshTableModel();
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
    }

    /**
     * @see com.ixora.rms.ui.artefacts.ArtefactSelectorPanel#showPlotMenuItemForArtefact(com.ixora.rms.client.model.ArtefactInfo)
     */
	protected boolean showPlotMenuItemForArtefact(ArtefactInfo ai) {
		// show the plot menu no matter what is the state of the artefact
		return ai.isCommitted();
	}

    /**
     * Updates the query group table model.
     */
    private void refreshTableModel() {
    	setDescriptionText(null);
		// reread data from model
		ArtefactInfoContainer aic = fSessionData.getArtefactContainerForResource(fContext, true);
		if(aic == null) {
		    logger.error("Couldn't find dashboard info container for: " + fContext);
			getDashboardTableModel().setArtefacts(fContext,
			        null);
		} else {
			getDashboardTableModel().setArtefacts(fContext,
		        aic.getDashboardInfo());
		}
    }

    /**
     * @return DashboardTableModel
     */
    private DashboardTableModel getDashboardTableModel() {
        return (DashboardTableModel)getJTableArtefacts().getModel();
    }

    /**
     * @param context
     * @param dashboard
     */
    private void handleSavedDashboard(ResourceId context, Dashboard dashboard) {
        // add to model only if the data view applies for the given context
        String version = fSessionData.getAgentVersionInContext(context);
        // add to model only if:
        // 1. outside of an agent context
        // 2. dashboard applies to version in context
        if(version == null || dashboard.appliesToAgentVersion(version)) {
    		fSessionData.getDashboardHelper()
    			.addDashboard(context, dashboard);
        } else {
            // otherwise remove
            fSessionData.getDashboardHelper()
                .removeDashboard(context, dashboard.getName());
        }
        // refresh table model
        refreshTableModel();
    }

    /**
     * Applies changes at global level i.e it will realize
     * all realizable query groups in the session model.
     */
/*    private void applyChangesGlobally() {
		// get all queries to realize
	    Map map = sessionData.getQueryGroupHelper().getAllDashboardsToRealize();
	    ResourceId rid;
	    Collection groups;
	    Map.Entry me;
	    for(Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
            me = (Map.Entry)iter.next();
            rid = (ResourceId)me.getKey();
            groups = (Collection)me.getValue();
            if(groups != null) {
				DashboardInfo ginfo;
				for(Iterator iter1 = groups.iterator(); iter1.hasNext();) {
					 ginfo = (DashboardInfo)iter1.next();
					 // get queries
					 QueryId[] members = ginfo.getDashboard().getMembers();
					 for(int i = 0; i < members.length; i++) {
					    QueryId m = members[i];
	                    if(context != null) {
	                        m = m.complete(context);
	                    }
	                    if(context != null) {
	                        m = m.complete(context);
	                    }
	                    ArtefactInfoContainer aic = sessionData.getArtefactContainerForResource(m.getContext());
	                    if(aic == null) {
	                        logger.error("Couldn't find info container for: " + m.getContext());
	                        break;
	                    }
	                    final QueryInfo qinfo = aic.getQueryInfo(m.getName());
	                    if(qinfo == null) {
	                        logger.error("Couldn't find query info for: " + m);
	                        break;
	                    }
	                    final QueryId fm = m;
	                    // Note: this method is reading from the session model
						// and as a result it can only be used safely from
						// the event dispatching thread
						this.viewContainer.runJobSynch(new UIWorkerJobDefault(
								viewContainer,
								Cursor.WAIT_CURSOR,
								MessageRepository.get(
									Msg.TEXT_REALIZINGQUERY,
									new String[]{qinfo.getTranslatedName()})) {
							public void work() throws Exception {
								queryRealizer.realizeQuery(
									fm.getContext(), qinfo.getQuery());					}
							public void finished(Throwable ex) {
								if(ex != null) {
									UIExceptionMgr.userException(ex);
								}
							}
						});
					}
				}
            }
	    }

		// get all query groups to unrealize
	    map = sessionData.getQueryGroupHelper().getAllDashboardsToUnRealize();
	    for(Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
            me = (Map.Entry)iter.next();
            rid = (ResourceId)me.getKey();
            groups = (Collection)me.getValue();
            if(groups != null) {
                DashboardInfo ginfo;
				for(Iterator iter1 = groups.iterator(); iter1.hasNext();) {
					 ginfo = (DashboardInfo)iter1.next();
					 // get queries
					 QueryId[] members = ginfo.getDashboard().getMembers();
					 for(int i = 0; i < members.length; i++) {
					    QueryId m = members[i];
	                    if(context != null) {
	                        m = m.complete(context);
	                    }
	                    if(context != null) {
	                        m = m.complete(context);
	                    }
	                    ArtefactInfoContainer aic = sessionData.getArtefactContainerForResource(m.getContext());
	                    if(aic == null) {
	                        logger.error("Couldn't find query container for: " + m.getContext());
	                        break;
	                    }
	                    final QueryInfo qinfo = aic.getQueryInfo(m.getName());
	                    if(qinfo == null) {
	                        logger.error("Couldn't find query info for: " + m);
	                        break;
	                    }
	                    final QueryId fm = m;
	                    // Note: this method is reading from the session model
						// and as a result it can only be used safely from
						// the event dispatching thread
						this.viewContainer.runJobSynch(new UIWorkerJobDefault(
								viewContainer,
								Cursor.WAIT_CURSOR,
								MessageRepository.get(
									Msg.TEXT_REALIZINGQUERY,
									new String[]{qinfo.getTranslatedName()})) {
							public void work() throws Exception {
								queryRealizer.unrealizeQuery(
									fm.getContext(), qinfo.getQuery());					}
							public void finished(Throwable ex) {
								if(ex != null) {
									UIExceptionMgr.userException(ex);
								}
							}
						});
					 }
				}
            }
        }

        sessionData.getQueryGroupHelper().recalculateDashboardsStatus(context);
		getQueryGroupTableModel().fireTableDataChanged();
		actionApply.setEnabled(false);
		actionCancel.setEnabled(false);
    }
*/

    /**
     * Applies only changes made to the current context.
     */
    private void applyChangesLocally() {
		// get all dashboards  to realize
        Collection<DashboardInfo> dashboards = getDashboardTableModel().getDashboardsToRealize();
        if(dashboards != null) {
			for(DashboardInfo dinfo : dashboards) {
				 // register views with the query realizer
				 DataViewId[] views = dinfo.getDashboard().getViews();
				 if(!Utils.isEmptyArray(views)) {
					 for(DataViewId view : views) {
	                    if(fContext != null) {
	                        view = view.complete(fContext);
	                    }
	                    // ask the locator for info on the required data view
	                    final SessionDataViewInfo dvInfo = this.fArtefactInfoLocator.getDataViewInfo(view);
	                    if(dvInfo == null) {
	                    	if(logger.isTraceEnabled()) {
	                    		logger.error("Couldn't find data view info for: " + view + ". Skipping...");
	                    	}
	                        continue;
	                    }
	                    final DataViewId fv = view;
	                    // Note: this method is reading from the session model
						// and as a result it can only be used safely from
						// the event dispatching thread
						this.fViewContainer.runJobSynch(new UIWorkerJobDefault(
								fViewContainer.getAppFrame(),
								Cursor.WAIT_CURSOR,
								MessageRepository.get(
									Msg.TEXT_REALIZING_DATAVIEW,
									new String[]{dvInfo.getTranslatedName()})) {
							public void work() throws Exception {
								fQueryRealizer.realizeQuery(
									fv.getContext(), dvInfo.getDataView().getQueryDef(), new QueryRealizer.Callback(){
										public boolean acceptIncreaseInMonitoringLevel(List<ResourceInfo> counters) {
											boolean ret = UIUtils.getBooleanYesNoInput(
												fViewContainer.getAppFrame(),
												MessageRepository.get(Msg.TITLE_CONFIRM_MONITORING_LEVEL_INCREASE),
												MessageRepository.get(Msg.TEXT_CONFIRM_MONITORING_LEVEL_INCREASE_FOR_DATA_VIEW, new String[]{dvInfo.getTranslatedName()}));
											if(!ret) {
												// undo changes
												fSessionData.getDataViewHelper().rollbackDataView(fv.getContext(), fv.getName());
											}
											return ret;
										}});
								}
							public void finished(Throwable ex) {
								if(ex != null) {
									UIExceptionMgr.userException(ex);
								}
							}
						});
					}
				 }
				// register counters with the query realizer
				// for every counter create a query and register it
				ResourceId[] counters = dinfo.getDashboard().getCounters();
				if(!Utils.isEmptyArray(counters)) {
					for(ResourceId counter : counters) {
	                    if(fContext != null) {
	                        counter = counter.complete(fContext);
	                    }
					    final SingleCounterQueryDef query =
					    	new SingleCounterQueryDef(counter, null, null);
					    final ResourceId counterContext = counter.getSubResourceId(ResourceId.ENTITY);

                        // add query to the model
                        fSessionData.getQueryHelper().addQuery(counterContext, query);

					    String translatedCounterName = counter.getCounterId().toString();
					    // ask the locator for info on this counter
					    SessionResourceInfo rInfo = this.fArtefactInfoLocator.getResourceInfo(counter);
					    if(rInfo != null && rInfo.getCounterInfo() != null) {
					        translatedCounterName = rInfo.getCounterInfo().getTranslatedName();
					    }
					    final String finalTranslatedCounterName = translatedCounterName;
					    final ResourceId fc = counter;
					    // Note: this method is reading from the session model
						// and as a result it can only be used safely from
						// the event dispatching thread
						this.fViewContainer.runJobSynch(new UIWorkerJobDefault(
								fViewContainer.getAppFrame(),
								Cursor.WAIT_CURSOR,
								MessageRepository.get(
									Msg.TEXT_REALIZING_DATAVIEW,
									new String[]{translatedCounterName})) {
							public void work() throws Exception {
								fQueryRealizer.realizeQuery(
									counterContext, query, new QueryRealizer.Callback(){
										public boolean acceptIncreaseInMonitoringLevel(List<ResourceInfo> counters) {
											boolean ret = UIUtils.getBooleanYesNoInput(
													fViewContainer.getAppFrame(),
													MessageRepository.get(Msg.TITLE_CONFIRM_MONITORING_LEVEL_INCREASE),
													MessageRepository.get(Msg.TEXT_CONFIRM_MONITORING_LEVEL_INCREASE_FOR_COUNTER, new String[]{finalTranslatedCounterName}));
											if(!ret) {
												// undo changes
												fSessionData.getCounterHelper().rollbackCounter(fc, true);
											}
											return ret;
										}});
								}
							public void finished(Throwable ex) {
								if(ex != null) {
									UIExceptionMgr.userException(ex);
								}
							}
						});
					}
				}
			}
        }

		// get all dashboards to unrealize
    	dashboards = getDashboardTableModel().getDashboardsToUnRealize();
        if(dashboards != null) {
			for(DashboardInfo dinfo : dashboards) {
				 // unregister views with the query realizer
				 DataViewId[] views = dinfo.getDashboard().getViews();
				 // get views
				 if(!Utils.isEmptyArray(views)) {
					 for(DataViewId view : views) {
	                    if(fContext != null) {
	                        view = view.complete(fContext);
	                    }
	                    // ask the locator for info on the required data view
	                    String viewTranslatedName = view.getName();
	                    final SessionDataViewInfo dvInfo = this.fArtefactInfoLocator.getDataViewInfo(view);
	                    if(dvInfo == null) {
	                    	if(logger.isTraceEnabled()) {
	                    		logger.error("Couldn't find data view info for: " + view);
	                    	}
	                    } else {
	                        viewTranslatedName = dvInfo.getTranslatedName();
	                    }
	                    final DataViewId fv = view;
	                    // Note: this method is reading from the session model
						// and as a result it can only be used safely from
						// the event dispatching thread
						this.fViewContainer.runJobSynch(new UIWorkerJobDefault(
								fViewContainer.getAppFrame(),
								Cursor.WAIT_CURSOR,
								MessageRepository.get(
									Msg.TEXT_REALIZING_DATAVIEW,
									new String[]{viewTranslatedName})) {
							public void work() throws Exception {
								QueryId qid = new QueryId(fv.getContext(), fv.getName());
								fQueryRealizer.unrealizeQuery(qid, false);
							}
							public void finished(Throwable ex) {
								if(ex != null) {
									UIExceptionMgr.userException(ex);
								}
							}
						});
					 }

					// unregister counters with the query realizer
					ResourceId[] counters = dinfo.getDashboard().getCounters();
					if(!Utils.isEmptyArray(counters)) {
						for(ResourceId counter : counters) {
		                    if(fContext != null) {
		                        counter = counter.complete(fContext);
		                    }
						    final ResourceId counterContext = counter.getSubResourceId(ResourceId.ENTITY);
						    String translatedCounterName = counter.getCounterId().toString();
						    // ask the locator for info on this counter
						    SessionResourceInfo rInfo = this.fArtefactInfoLocator.getResourceInfo(counter);
						    if(rInfo != null && rInfo.getCounterInfo() != null) {
						        translatedCounterName = rInfo.getCounterInfo().getTranslatedName();
						    }
						    final ResourceId finalCounter = counter;
						    // Note: this method is reading from the session model
							// and as a result it can only be used safely from
							// the event dispatching thread
							this.fViewContainer.runJobSynch(new UIWorkerJobDefault(
									fViewContainer.getAppFrame(),
									Cursor.WAIT_CURSOR,
									MessageRepository.get(
										Msg.TEXT_REALIZING_DATAVIEW,
										new String[]{translatedCounterName})) {
								public void work() throws Exception {
								    QueryId queryId = new QueryId(counterContext,
								            finalCounter.getCounterId().toString());
									fQueryRealizer.unrealizeQuery(queryId, false);
									}
								public void finished(Throwable ex) {
									if(ex != null) {
										UIExceptionMgr.userException(ex);
									}
								}
							});
						}
					}

					String dashboardName = dinfo.getDashboard().getName();
				    fSessionData.getDashboardHelper().setDashboardFlag(
				     		ArtefactInfo.ENABLED, fContext, dashboardName, false, true);
				}
            }


        }
        fSessionData.getDashboardHelper().recalculateDashboardsStatus(fContext);
		getDashboardTableModel().fireTableDataChanged();
		fActionApply.setEnabled(false);
		fActionCancel.setEnabled(false);
    }
}