/*
 * Created on 06-Jul-2004
 */
package com.ixora.rms.ui.artefacts.dataview;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.TableColumn;

import com.ixora.rms.ResourceId;
import com.ixora.common.MessageRepository;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.jobs.UIWorkerJobDefault;
import com.ixora.common.ui.wizard.Wizard;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.rms.client.QueryRealizer;
import com.ixora.rms.client.model.ArtefactInfo;
import com.ixora.rms.client.model.ArtefactInfoContainer;
import com.ixora.rms.client.model.DataViewInfo;
import com.ixora.rms.client.model.QueryInfo;
import com.ixora.rms.client.model.ResourceInfo;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.dataengine.definitions.QueryDef;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.repository.DataViewBoardInstallationData;
import com.ixora.rms.repository.DataViewBoardSampleData;
import com.ixora.rms.repository.DataViewId;
import com.ixora.rms.repository.DataViewMap;
import com.ixora.rms.repository.QueryId;
import com.ixora.rms.services.DataEngineService;
import com.ixora.rms.services.DataViewBoardRepositoryService;
import com.ixora.rms.services.DataViewRepositoryService;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.SessionTreeExplorer;
import com.ixora.rms.ui.actions.ActionFromXML;
import com.ixora.rms.ui.artefacts.ArtefactSelectorPanel;
import com.ixora.rms.ui.artefacts.SelectableArtefactTableCellRenderer;
import com.ixora.rms.ui.artefacts.dataview.exception.DataViewNotReady;
import com.ixora.rms.ui.artefacts.dataview.messages.Msg;
import com.ixora.rms.ui.artefacts.dataview.wizard.WizardStep1;
import com.ixora.rms.ui.artefacts.dataview.wizard.WizardStep2;
import com.ixora.rms.ui.artefacts.dataview.wizard.WizardStep3;
import com.ixora.rms.ui.artefacts.dataview.wizard.WizardStep4;

/**
 * A panel that allows to edit and plot from a data view.
 * @author Daniel Moraru
 */
public final class DataViewSelectorPanel extends ArtefactSelectorPanel<DataViewInfo> {
	private static final long serialVersionUID = 1414108777191255948L;
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(DataViewSelectorPanel.class);
//	private static final Icon reactionIcon = UIConfiguration.getIcon("reaction_armed.gif");
	/**
	 * Callback.
	 */
	public interface Callback {
		/**
		 * Invoked when a request to plot a data view was made.
		 * @param view view id
		 */
		void plot(DataViewId view);
	}

	/** Data view board repository */
	private DataViewBoardRepositoryService fDataViewBoardRepository;
	/** Data view repository */
	private DataViewRepositoryService fDataViewRepository;
	/** Data engine */
	private DataEngineService fDataEngine;
	/** Callback */
	private Callback fCallback;
	/** Event handler */
	private EventHandler fEventHandler;
	/** Session tree explorer */
	private SessionTreeExplorer fSessionTreeExplorer;

	private Action fActionAddUsingWizard;

	/**
	 * Add data view using wizard.
	 */
	private final class ActionAddUsingWizard extends AbstractAction {
		private static final long serialVersionUID = 1155229676364906177L;
		public ActionAddUsingWizard() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(
			        Msg.ACTIONS_ADD_DATAVIEW_USING_WIZARD), this);
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleAddDataViewUsingWizard();
		}
	}

	/**
	 * Event handler.
	 */
	private final class EventHandler implements DataViewEditor.Listener {
        /**
         * @see com.ixora.rms.ui.artefacts.dataview.DataViewEditor.Listener#saveDataView(ResourceId, DataView)
         */
        public void savedDataView(ResourceId context, DataView dv) {
            handleSavedDataView(context, dv);
        }
	}

	/**
	 * Constructor used by a log replay session.
	 * @param viewContainer
	 * @param boardRepository
	 * @param dvService
	 * @param sessionData
	 * @param queryRealizer
	 * @param cb
	 */
	public DataViewSelectorPanel(
			RMSViewContainer viewContainer,
			SessionTreeExplorer treeExplorer,
			DataViewBoardRepositoryService boardRepository,
			DataViewRepositoryService dataViewService,
			DataEngineService dataEngineService,
			SessionModel sessionData,
			Callback cb) {
	    this(viewContainer, treeExplorer, boardRepository, dataViewService, dataEngineService, sessionData, null, cb, true);
	}

	/**
	 * Constructor used by a live session.
	 * @param viewContainer
	 * @param boardRepository
	 * @param dvService
	 * @param sessionData
	 * @param queryRealizer
	 * @param cb
	 */
	public DataViewSelectorPanel(
			RMSViewContainer viewContainer,
			SessionTreeExplorer treeExplorer,
			DataViewBoardRepositoryService boardRepository,
			DataViewRepositoryService dataViewService,
			DataEngineService dataEngineService,
			SessionModel sessionData,
			QueryRealizer queryRealizer,
			Callback cb) {
	    this(viewContainer, treeExplorer, boardRepository, dataViewService, dataEngineService, sessionData, queryRealizer, cb, false);
	}

	/**
	 * Constructor.
	 * @param viewContainer
	 * @param treeExplorer
	 * @param boardRepository
	 * @param dvService
	 * @param sessionData
	 * @param queryRealizer
	 * @param cb
	 * @param logReplayMode
	 */
	private DataViewSelectorPanel(
			RMSViewContainer viewContainer,
			SessionTreeExplorer treeExplorer,
			DataViewBoardRepositoryService boardRepository,
			DataViewRepositoryService dataViewService,
			DataEngineService dataEngineService,
			SessionModel sessionData,
			QueryRealizer queryRealizer,
			Callback cb,
			boolean logReplayMode) {
		super(viewContainer,
				sessionData, queryRealizer,
				new DataViewTableModel(viewContainer,
				        sessionData, logReplayMode, boardRepository),
						null,
				Msg.ACTIONS_REMOVE_DATAVIEW,
				Msg.ACTIONS_EDIT_DATAVIEW,
				Msg.ACTIONS_PLOT_DATAVIEW);
   		if(cb == null) {
   		    throw new IllegalArgumentException("null callback");
   		}
   		this.fCallback = cb;
   		this.fSessionTreeExplorer = treeExplorer;
   		this.fDataViewBoardRepository = boardRepository;
   		this.fDataViewRepository = dataViewService;
   		this.fDataEngine = dataEngineService;
   		this.fEventHandler = new EventHandler();
   		this.fActionAddUsingWizard = new ActionAddUsingWizard();
	}

	/**
     * @param context
     * @param dataViewInfo
     */
    public void setDataViews(ResourceId context, Collection<DataViewInfo> dataViewInfo) {
        this.setArtefacts(context, dataViewInfo);
    }

    /**
     * Updates the state of the data views after checking the query realizer.
     * @param context
     * @param dataViewInfo
     */
    private void checkRealizerForDataViews(ResourceId context, Collection<DataViewInfo> dataViewInfo) {
        // see if any of this context's queries are already registered
   		// with the query realizer and if so mark it enabled and committed
        if(!Utils.isEmptyCollection(dataViewInfo)) {
	        for(DataViewInfo vi : dataViewInfo) {
	        	// if data view is not committed do not update state
	        	if(!vi.isCommitted()) {
		        	String identifier = vi.getDataView().getQueryDef().getIdentifier();
		        	QueryId qId = new QueryId(context, identifier);
		            	// commit query status
		        		fSessionData.getQueryHelper().setQueryFlag(
		        		        QueryInfo.ENABLED,
		        		        context, identifier,
								fQueryRealizer.isQueryRegistered(qId),
								true,
                                false);
	            }
	        }
        }
    }


    /**
     * @see com.ixora.rms.ui.artefacts.ArtefactSelectorPanel#getJTableArtefacts()
     */
    protected JTable getJTableArtefacts() {
        if(jTableArtefacts == null) {
            jTableArtefacts = new javax.swing.JTable(
                    this.fTableModelArtefacts);
            jTableArtefacts.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
            TableColumn c = jTableArtefacts.getColumnModel().getColumn(0);
            c.setPreferredWidth(20);
            c.setMaxWidth(20);
            c = jTableArtefacts.getColumnModel().getColumn(1);
            c.setPreferredWidth(16);
            c.setMaxWidth(16);
            c = jTableArtefacts.getColumnModel().getColumn(2);
            c.setPreferredWidth(25);
            c.setMaxWidth(25);
            c = jTableArtefacts.getColumnModel().getColumn(3);
            c.setCellRenderer(new SelectableArtefactTableCellRenderer());
            jTableArtefacts.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        }
        return jTableArtefacts;
    }

    /**
     * @see com.ixora.rms.ui.artefacts.ArtefactSelectorPanel#getArtefactInfoAtRow(int)
     */
    protected ArtefactInfo getArtefactInfoAtRow(int rowIndex) {
        return (ArtefactInfo)getJTableArtefacts().getValueAt(rowIndex, 3);
    }

    /**
     * @see com.ixora.rms.ui.artefacts.ArtefactSelectorPanel#refreshArtefactStatus()
     */
    protected void refreshArtefactStatus() {
    	// check with realizer, if data view is registered
        // keep it committed and enabled in the view
		ArtefactInfoContainer aic = fSessionData.getArtefactContainerForResource(fContext, true);
		if(aic == null) {
		   logger.error("Couldn't find container for: " + fContext);
		   return;
		}
        checkRealizerForDataViews(fContext, aic.getDataViewInfo());
        // now recalculate data view state
        this.fSessionData.getDataViewHelper()
        	.recalculateDataViewsStatus(fContext);
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
        return aic.uncommittedVisibleDataViews();
    }

	/**
	 * Cancels changes.
	 */
	protected void handleCancel() {
		try {
			fSessionData.getDataViewHelper().rollbackDataViews(this.fContext);
			getDataViewTableModel().fireTableDataChanged();
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
		    refreshArtefactStatus();
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
			final JTable table = getJTableArtefacts();
			final int sel = table.getSelectedRow();
			if (sel < 0) {
				return;
			}
			final DataViewInfo dvi = (DataViewInfo)getArtefactInfoAtRow(sel);
			this.fViewContainer.runJobSynch(
			        new UIWorkerJobDefault(
			                fViewContainer.getAppFrame(), Cursor.WAIT_CURSOR,
			                // TODO localize
			        		"Plotting data view...") {
                public void work() throws Exception {
        			if(!dvi.getFlag(DataViewInfo.ENABLED) && dvi.isCommitted()) {
        				// enable it first
        				((DataViewTableModel)fTableModelArtefacts).enableDataView(sel);
        				applyChangesLocally();
        			}
                	fCallback.plot(new DataViewId(fContext, dvi.getDataView().getName()));
                }
                public void finished(Throwable ex) {
                    if(ex != null) {
                       UIExceptionMgr.userException(ex);
                    }
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
			final DataViewInfo dvi = (DataViewInfo)getArtefactInfoAtRow(sel);
			fViewContainer.runJobSynch(
					new UIWorkerJobDefault(fViewContainer.getAppFrame(),
							Cursor.WAIT_CURSOR, "") {
				public void work() throws Throwable {
					DataViewEditor editor = new DataViewEditor(
			   		        fViewContainer, fSessionTreeExplorer,
							fSessionData, fDataViewRepository, fEventHandler);
					editor.edit(
							fContext,
							dvi.getDataView(), false);
				}
				public void finished(Throwable ex) {
					if(ex != null) {
						UIExceptionMgr.userException(ex);
					}
				}
			});
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
    }

    /**
     * @see com.ixora.rms.ui.artefacts.ArtefactSelectorPanel#handleAddArtefact()
     */
    protected void handleAddArtefact(DataView sample, boolean saveEnabled) {
		try {
			DataViewEditor editor = new DataViewEditor(
	   		        fViewContainer, fSessionTreeExplorer,
					fSessionData, fDataViewRepository, fEventHandler);
			editor.edit(fContext, sample, saveEnabled);
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
			final DataViewInfo dvi = (DataViewInfo)getArtefactInfoAtRow(sel);
			DataViewMap map = fDataViewRepository.getDataViewMap(fContext);
			if(map == null) {
			    logger.error("Couldn't find data view map for context: " + this.fContext);
			    return;
			}
			// ask for confirmation
			if(!UIUtils.getBooleanOkCancelInput(this.fViewContainer.getAppFrame(),
					MessageRepository.get(Msg.TITLE_CONFIRM_REMOVE_DATAVIEW),
					MessageRepository.get(Msg.TEXT_CONFIRM_REMOVE_DATAVIEW,
							new String[] {dvi.getTranslatedName()}))) {
				return;
			}

            // remove view for the current agent version
			map.remove(dvi.getDataView().getName(), fSUOVersion);
			fDataViewRepository.setDataViewMap(fContext, map);
			fDataViewRepository.save();
			// update model
			this.fSessionData.getDataViewHelper()
				.removeDataView(fContext,
			        dvi.getDataView().getName());
			// refreshes the table model
			refreshTableModel();
			if(!fLogReplayMode) {
				// unregister query with the query realizer
				// Note: this method is reading from the session model
				// and as a result it can only be used safely from
				// the event dispatching thread
				this.fViewContainer.runJobSynch(new UIWorkerJobDefault(
						fViewContainer.getAppFrame(),
						Cursor.WAIT_CURSOR,
						MessageRepository.get(
							Msg.TEXT_UNREALIZING_DATAVIEW,
							new String[]{dvi.getTranslatedName()})) {
					public void work() throws Exception {
						QueryId qid = new QueryId(fContext, dvi.getDataView().getQueryDef().getIdentifier());
						fQueryRealizer.unrealizeQuery(qid, false);
						}
					public void finished(Throwable ex) {
						if(ex != null) {
							UIExceptionMgr.userException(ex);
						}
					}
					});
			}
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
    }

    /**
     * @return DataViewTableModel
     */
    private DataViewTableModel getDataViewTableModel() {
        return (DataViewTableModel)getJTableArtefacts().getModel();
    }

    /**
     * Refreshes the data view table model.
     */
    private void refreshTableModel() {
		setDescriptionText(null);
    	// reread data from model
		ArtefactInfoContainer aic = fSessionData.getArtefactContainerForResource(fContext, true);
		if(aic == null) {
		    logger.error("Couldn't find info container for: " + fContext);
		    getDataViewTableModel().setArtefacts(fContext, null);
		} else {
			getDataViewTableModel().setArtefacts(fContext,
		        aic.getDataViewInfo());
		}
    }

    /**
     * @param context
     * @param dv
     */
    private void handleSavedDataView(ResourceId context, DataView dv) {
        try {
        	savedDataView(context, dv);
        } catch(Exception e) {
            UIExceptionMgr.userException(e);
        }
    }

    /**
     * @param context
     * @param dv
     * @throws RMSException
     */
    private void savedDataView(final ResourceId context, DataView dv) throws RMSException {
        // deregister the view from the data engine and query realizer
        // get artefact container for the context of this view
        ArtefactInfoContainer aic = fSessionData.getArtefactContainerForResource(context, true);
        if(aic == null) {
            logger.error("Couldn't find artefact container for " + context);
        } else {
            final DataViewInfo dvi = aic.getDataViewInfo(dv.getArtefactName());
            if(dvi != null) {
                if(dvi.isCommitted() && dvi.getFlag(DataViewInfo.ENABLED)){
                    // unregister old query with the data engine
                    QueryId qid = new QueryId(context, dv.getArtefactName());
                    fDataEngine.removeQuery(qid);
                    // check if the view to be overwritten is
                    // enabled and committed and if so, unrealize
                    // it's query if we are in a live session
                    if(!fLogReplayMode) {
                        // unregister query with the query realizer
                        // Note: this method is reading from the session model
                        // and as a result it can only be used safely from
                        // the event dispatching thread
                        this.fViewContainer.runJobSynch(new UIWorkerJobDefault(
                                fViewContainer.getAppFrame(),
                                Cursor.WAIT_CURSOR,
                                MessageRepository.get(
                                    Msg.TEXT_UNREALIZING_DATAVIEW,
                                    new String[]{dvi.getTranslatedName()})) {
                            public void work() throws Exception {
                                QueryId qid = new QueryId(context, dvi.getDataView().getQueryDef().getIdentifier());
                                fQueryRealizer.unrealizeQuery(qid, true);
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

        // add to model if the data view applies for the given context
        String version = fSessionData.getAgentVersionInContext(context);
        // this might happen when importing data view from xml
        if(version != null && !dv.appliesToAgentVersion(version)) {
        	// TODO localize
        	throw new RMSException("DataView \"" + dv.getName() + "\" does not apply to current agent version \"" + version + "\"");
        }
        // update model only if:
        // 1. outside of an agent context
        // 2. data view applies to version in context
        if(version == null || dv.appliesToAgentVersion(version)) {
            // update model
			this.fSessionData.getDataViewHelper()
				.addDataView(context, dv);
	        // if in log replay mode, check if all counters are available
	        if(fLogReplayMode) {
	            if(!this.fSessionData.getDataViewHelper()
	                    .isDataViewReady(context, dv)) {
	                throw new DataViewNotReady(dv.getName());
	            }
	        }
        } else {
            // remove from model
            this.fSessionData.getDataViewHelper()
                .removeDataView(context, dv.getName());
        }
        // refreshes the table model
        refreshTableModel();
    }

    /**
     * Applies changes to all realizable queries in the
     * session model.
     */
    private void applyChangesLocally() {
		// get all data views to realize
	    Collection<DataViewInfo> dvs = getDataViewTableModel().getDataViewsToRealize();
	    if(dvs != null) {
			for(Iterator<DataViewInfo> iter = dvs.iterator(); iter.hasNext();) {
				final DataViewInfo dv = iter.next();
				final QueryDef query = dv.getDataView().getQueryDef();
				// Note: this method is reading from the session model
				// and as a result it can only be used safely from
				// the event dispatching thread
				this.fViewContainer.runJobSynch(new UIWorkerJobDefault(
						fViewContainer.getAppFrame(),
						Cursor.WAIT_CURSOR,
						MessageRepository.get(
							Msg.TEXT_REALIZING_DATAVIEW,
							new String[]{dv.getTranslatedName()})) {
					public void work() throws Exception {
						// register query def with the query realizer
						fQueryRealizer.realizeQuery(fContext, query, new QueryRealizer.Callback(){
							public boolean acceptIncreaseInMonitoringLevel(List<ResourceInfo> counters) {
								return UIUtils.getBooleanYesNoInput(
										fViewContainer.getAppFrame(),
										MessageRepository.get(Msg.TITLE_CONFIRM_MONITORING_LEVEL_INCREASE),
										MessageRepository.get(Msg.TEXT_CONFIRM_MONITORING_LEVEL_INCREASE));
							}
						});
					}
					public void finished(Throwable ex) {
						if(ex != null) {
							UIExceptionMgr.userException(ex);
						}
					}
				});
			}
        }

		// get all views to unrealize
	    dvs = getDataViewTableModel().getDataViewsToUnRealize();
	    if(dvs != null) {
			for(Iterator<DataViewInfo> iter = dvs.iterator(); iter.hasNext();) {
				final DataViewInfo dv = iter.next();
				final QueryDef query = dv.getDataView().getQueryDef();
				// Note: this method is reading from the session model
				// and as a result it can only be used safely from
				// the event dispatching thread
				this.fViewContainer.runJobSynch(new UIWorkerJobDefault(
						fViewContainer.getAppFrame(),
						Cursor.WAIT_CURSOR,
						MessageRepository.get(
							Msg.TEXT_UNREALIZING_DATAVIEW,
							new String[]{dv.getTranslatedName()})) {
					public void work() throws Exception {
						// unrealize with query realizer
						QueryId qid = new QueryId(fContext, query.getIdentifier());
	                    fDataEngine.removeQuery(qid);
						fQueryRealizer.unrealizeQuery(qid, true);
					}
					public void finished(Throwable ex) {
						if(ex != null) {
							UIExceptionMgr.userException(ex);
						}
					}
					});
			}
	    }

	    fSessionData.getDataViewHelper().recalculateDataViewsStatus(fContext);
	    // rollback any data views for which the query realizer callback returned false
	    fSessionData.getDataViewHelper().rollbackDataViews(this.fContext);
	    getDataViewTableModel().fireTableDataChanged();
		fActionApply.setEnabled(false);
		fActionCancel.setEnabled(false);
    }

    /**
     * @see com.ixora.rms.ui.artefacts.ArtefactSelectorPanel#showPlotMenuItemForArtefact(com.ixora.rms.client.model.ArtefactInfo)
     */
	protected boolean showPlotMenuItemForArtefact(ArtefactInfo ai) {
		// show the plot menu no matter what is the state of the artefact
		return ai.isCommitted();
	}

	/**
	 * @see com.ixora.rms.ui.artefacts.ArtefactSelectorPanel#getJPopupMenu()
	 */
	protected JPopupMenu getJPopupMenu() {
		if(jPopupMenu == null) {
			jPopupMenu = UIFactoryMgr.createPopupMenu();
			jPopupMenu.add(getJMenuItemPlot());
			jPopupMenu.add(getJMenuItemEdit());

			JMenu addMenu = UIFactoryMgr.createMenu();
			addMenu.setText(MessageRepository.get(Msg.ACTIONS_ADD_DATAVIEW));
			JMenu addUsingXMLMenu = UIFactoryMgr.createMenu();
			addUsingXMLMenu.setText(MessageRepository.get(Msg.ACTIONS_ADD_DATAVIEW_USING_XML_EDITOR));
			addMenu.add(addUsingXMLMenu);

			Map<String, DataViewBoardInstallationData> boards = fDataViewBoardRepository
							.getInstalledDataViewBoards();
			for(DataViewBoardInstallationData board : boards.values()) {
				if(!board.allowUserToCreateView()) {
					continue;
				}
				List<DataViewBoardSampleData> samples = board.getSamples();
				DataViewBoardSampleData sampleData = null;
				if(!Utils.isEmptyCollection(samples)) {
					sampleData = samples.get(0);
				}
				final DataView samplef = (sampleData == null ? null : sampleData.getDataView());
				addUsingXMLMenu.add(new JMenuItem(new AbstractAction(
						MessageRepository.get(board.getBoardComponenName(), board.getViewName()) + "...") {
					private static final long serialVersionUID = -6304644066992997069L;
					public void actionPerformed(ActionEvent e) {
						fViewContainer.runJobSynch(
								new UIWorkerJobDefault(fViewContainer.getAppFrame(),
										Cursor.WAIT_CURSOR, "") {
							public void work() throws Throwable {
								// IMPORTANT: sample must be cloned as it will be modified
								DataView sample = null;
								if(samplef != null) {
									sample = (DataView)Utils.deepClone(samplef);
									if(fSUOVersion != null) {
										// set up version info for sample
					                    Set<String> suoVersions = new HashSet<String>();
					                    suoVersions.add(fSUOVersion);
					                    sample.addAgentVersions(suoVersions);
									}
				                }
								handleAddArtefact(sample, false);
							}
							public void finished(Throwable ex) {
								if(ex != null) {
									UIExceptionMgr.userException(ex);
								}
							}
						});
					}
				}));
			}
			JMenuItem mi = UIFactoryMgr.createMenuItem(fActionAddUsingWizard);
			mi.setToolTipText(null);
			addMenu.add(mi);
			mi = UIFactoryMgr.createMenuItem(new ActionFromXML(fViewContainer){
				private static final long serialVersionUID = -7525644609373971388L;
				protected void handleXML(String data) throws Exception {
					addDataViewFromXML(data);
				}
			});
			mi.setToolTipText(null);
			addMenu.add(mi);
			jPopupMenu.add(addMenu);
			jPopupMenu.add(getJMenuItemRemove());
			jPopupMenu.add(getJMenuItemViewXML());
		}
		return jPopupMenu;
	}

    /**
	 * Adds a data view from XML.
	 * @throws Exception
	 */
	private void addDataViewFromXML(String data) throws Exception {
		DataView dv = (DataView)XMLUtils.fromXMLBuffer(null, new StringBuffer(data), "view");
		DataViewEditor.saveDataView(fEventHandler, fContext, fDataViewRepository, dv, null);
	}

	/**
	 *
	 */
	private void handleAddDataViewUsingWizard() {
		try {
			Wizard dlg = new Wizard(
					fViewContainer.getAppFrame(), new Wizard.Listener(){
						public void finished(Object value) {
							handleAddArtefact((DataView)value, true);
						}
						public void cancelled() {
						}
						// TODO localize
					}, "Data View Wizard");
			dlg.addStep(new WizardStep1(fContext));
			dlg.addStep(new WizardStep2());
			dlg.addStep(new WizardStep3(dlg, fAllSUOVersions, fSUOVersion));
			dlg.addStep(new WizardStep4(dlg, fContext, fSessionData, fSessionTreeExplorer));
			UIUtils.centerDialogAndShow(fViewContainer.getAppFrame(), dlg);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
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
		DataViewInfo dvi = (DataViewInfo)getArtefactInfoAtRow(sel);
		return XMLUtils.toXMLBuffer(null, dvi.getDataView(), "rms", false).toString();
	}

	/**
     * Applies changes to all realizable queries in the
     * session model.
     */
/*    private void applyChangesGlobally() {
		// get all queries to realize
	    Map map = sessionData.getQueryHelper().getAllQueriesToRealize();
	    ResourceId rid;
	    Collection queries;
	    Map.Entry me;
	    for(Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
            me = (Map.Entry)iter.next();
            rid = (ResourceId)me.getKey();
            queries = (Collection)me.getValue();
            if(queries != null) {
				for(Iterator iter1 = queries.iterator(); iter1.hasNext();) {
					final QueryInfo query = (QueryInfo)iter1.next();
					// Note: this method is reading from the session model
					// and as a result it can only be used safely from
					// the event dispatching thread
					this.viewContainer.runJobSynch(new UIWorkerJobDefault(
							viewContainer,
							Cursor.WAIT_CURSOR,
							MessageRepository.get(
								Msg.TEXT_REALIZINGQUERY,
								new String[]{query.getTranslatedName()})) {
						public void work() throws Exception {
							queryRealizer.realizeQuery(
								context, query.getQuery());					}
						public void finished(Throwable ex) {
							if(ex != null) {
								UIExceptionMgr.userException(ex);
							}
						}
					});
				}
            }
	    }

		// get all queries to unrealize
	    map = sessionData.getQueryHelper().getAllQueriesToUnRealize();
	    for(Iterator iter = map.entrySet().iterator(); iter.hasNext();) {
            me = (Map.Entry)iter.next();
            rid = (ResourceId)me.getKey();
            queries = (Collection)me.getValue();
            if(queries != null) {
				for(Iterator iter2 = queries.iterator(); iter2.hasNext();) {
					final QueryInfo query = (QueryInfo)iter2.next();
					// Note: this method is reading from the session model
					// and as a result it can only be used safely from
					// the event dispatching thread
					this.viewContainer.runJobSynch(new UIWorkerJobDefault(
							viewContainer,
							Cursor.WAIT_CURSOR,
							MessageRepository.get(
								Msg.TEXT_REALIZINGQUERY,
								new String[]{query.getTranslatedName()})) {
						public void work() throws Exception {
							queryRealizer.unrealizeQuery(
								context, query.getQuery());					}
						public void finished(Throwable ex) {
							if(ex != null) {
								UIExceptionMgr.userException(ex);
							}
						}
						});
				}
            }
	    }

		getQueryTableModel().fireTableDataChanged();
		actionApply.setEnabled(false);
		actionCancel.setEnabled(false);
    } */
}