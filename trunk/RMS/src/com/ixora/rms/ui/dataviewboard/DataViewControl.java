/*
 * Created on 14-Jan-2004
 */
package com.ixora.rms.ui.dataviewboard;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.SwingUtilities;

import com.ixora.common.ComponentConfiguration;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.popup.PopupListener;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.CounterDescriptor;
import com.ixora.rms.CounterId;
import com.ixora.rms.ResourceId;
import com.ixora.rms.client.locator.SessionDataViewInfo;
import com.ixora.rms.client.locator.SessionResourceInfo;
import com.ixora.rms.dataengine.QueryResultData;
import com.ixora.rms.dataengine.RealizedQuery;
import com.ixora.rms.dataengine.external.QueryData;
import com.ixora.rms.dataengine.external.QueryListener;
import com.ixora.rms.dataengine.external.QuerySeries;
import com.ixora.rms.exporter.html.HTMLProvider;
import com.ixora.rms.reactions.ReactionId;
import com.ixora.rms.reactions.ReactionsComponent;
import com.ixora.rms.reactions.ReactionsComponentConfigurationConstants;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.repository.DataViewId;
import com.ixora.rms.ui.actions.ActionViewXML;
import com.ixora.rms.ui.dataviewboard.exception.FailedToCreateControl;
import com.ixora.rms.ui.dataviewboard.legend.Legend;
import com.ixora.rms.ui.dataviewboard.legend.LegendPanelDetailed;
import com.ixora.rms.ui.dataviewboard.legend.LegendPanelSimple;
import com.ixora.rms.ui.messages.Msg;

/**
 * Abstract UI control displaying a resource.
 * @author Daniel Moraru
 */
public abstract class DataViewControl extends JPanel
	implements QueryListener, HTMLProvider {
	private static final long serialVersionUID = -3114652834320555577L;

	/** Listener */
	public interface Listener {
		/**
		 * Invoked when the control gains focus.
		 * @param control
		 */
		void controlInFocus(DataViewControl control);
	}
	
	/** Callback */
	public interface Callback {		
		/**
		 * Invoked when the control needs to move to another view board.
		 */
		void move(DataViewControl source, String screen, String viewBoard);
	}

	/** Listener */
	protected Listener fListener;
	/** DataViewBoard owning this control */
	protected DataViewBoard fOwner;
	/** Legend */
	protected Legend fLegend;
	/** Legend panel simple */
	protected LegendPanelSimple fLegendPanelSimple;
	/** Legend panel detailed */
	protected LegendPanelDetailed fLegendPanelDetailed;
	/** Popup menu */
	protected JPopupMenu fPopupMenu;
	/** Remove popup menu item */
	protected JMenuItem fMenuItemRemove;
	/** View data view as XML menu item */
	protected JMenuItem fMenuItemViewAsXML;
	/** Move menu */
	protected JMenu fMenuMove;	
	/** Context */
	protected ResourceId fContextId;
	/** The data view used by this control */
	protected DataView fDataView;
	/** Realized cube */
	protected RealizedQuery fRealizedCube;
	/** Cache of localization data for resource infos */
	protected Map<ResourceId, SessionResourceInfo> fLocalizationInfoCache;
	/**
	 * Execution context. Used by controls to get access to plotting system so that
	 * they can provide different controls for their data view
	 */
	protected DataViewControlContext fControlContext;
	/** Event handler */
	protected PopupEventHandler fPopupEventHandler;
	/**
	 * Info on the represented data view. This can be null if info was not
	 * available when the control was created. It is the responsibility of the
	 * control to get this data when the first data sample arrives.
	 */
	protected SessionDataViewInfo fDataViewInfo;
	/**
	 * If this control plots a counter then this is the session resource
	 * info on that counter; it is null otherwise.
	 */
	protected SessionResourceInfo fSessionResourceInfoOnCounter;
	/** Translated context */
	protected String fTranslatedContext;
	/** This panel must be used by subclasses to display themself */
	protected JPanel fDisplayPanel;
	/** Event handler */
	private EventHandler fEventHandler;
	/** Reactions alert panel */
	private ReactionsAlertPanel fReactionsAlertPanel;

// this is the RUN FAST option for swing

	private static long sTotalCallsToDataAvailable;

/////////////////////////////////////////

	/**
	 * Popup event handler. It's protected so subclasses
	 * can register it with various components.
	 */
	protected final class PopupEventHandler
		extends PopupListener {
		/**
		 * @see com.ixora.common.ui.PopupListener#showPopup(java.awt.event.MouseEvent)
		 */
		protected void showPopup(MouseEvent e) {
			handleShowPopup(e);
		}
	}
	
	/**
	 * Event handler.
	 */
	private final class EventHandler implements ActionListener, Observer {
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			if(e.getSource() == fMenuItemRemove) {
				handleRemove();
			}
		}

		/**
		 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
		 */
		public void update(Observable o, Object arg) {
			handleUpdate(o, arg);
		}
	}

	/**
	 * Constructor used for on the fly data views.
	 * @param owner
	 * @param listener
	 * @param context
	 * @param resourceContext
	 * @param dataViewInfo
	 * @throws FailedToCreateControl
	 */
	protected DataViewControl(
			DataViewBoard owner,
			Listener listener,
			DataViewControlContext context,
			ResourceId resourceContext,
			DataView dataView) throws FailedToCreateControl {
		super(new BorderLayout());
		init(owner, listener, context);
		add(getDisplayPanel(), BorderLayout.CENTER);
		if(context.getReactionLogService() != null) {
			fReactionsAlertPanel = new ReactionsAlertPanel(context.getViewContainer(), context.getReactionLogService());
			fReactionsAlertPanel.setVisible(false);
			add(fReactionsAlertPanel, BorderLayout.NORTH);
		}
		this.fContextId = resourceContext;
		this.fLocalizationInfoCache = new HashMap<ResourceId, SessionResourceInfo>();
	    // try to locate info using the locator
		this.fDataView = dataView;
		if(this.fDataView.getSource() == DataView.DATAVIEW_SOURCE_COUNTER) {
			// try and find info on the counter
			// build counter resource id
			ResourceId counterId = new ResourceId(
					resourceContext.getHostId(),
					resourceContext.getAgentId(),
					resourceContext.getEntityId(),
					new CounterId(dataView.getQueryDef().getIdentifier()));
			fSessionResourceInfoOnCounter = fControlContext.getSessionArtefactLocator().getResourceInfo(counterId);
			if(fSessionResourceInfoOnCounter != null
					&& fSessionResourceInfoOnCounter.getCounterInfo() != null) {
				this.fDataViewInfo = new SessionDataViewInfo(
						fSessionResourceInfoOnCounter.getCounterInfo(), fDataView);
				this.fTranslatedContext = fSessionResourceInfoOnCounter.getCounterInfo().getTranslatedCounterPath();
			}
		} else if(fDataView.getSource() == DataView.DATAVIEW_SOURCE_REPOSITORY) {
			DataViewId vid = new DataViewId(resourceContext, fDataView.getName());
			this.fDataViewInfo = fControlContext.getSessionArtefactLocator().getDataViewInfo(vid);
			if(this.fDataViewInfo == null) {
				// this should never happen for a repository data view
				throw new FailedToCreateControl("Could not find data view " + vid, false);
			} else {
                SessionResourceInfo rInfo = fControlContext.getSessionArtefactLocator().getResourceInfo(vid.getContext());
                if(rInfo != null) {
                    this.fTranslatedContext = rInfo.getTranslatedPath();
                }
			}
		} else {
			// on the fly defined view
			this.fDataViewInfo = new SessionDataViewInfo(fDataView);
            SessionResourceInfo rInfo = fControlContext.getSessionArtefactLocator().getResourceInfo(resourceContext);
            if(rInfo != null) {
                this.fTranslatedContext = rInfo.getTranslatedPath();
            }
		}
		// Realize query (merging styles etc)
		realizeQuery(resourceContext);

		ConfigurationMgr.get(ReactionsComponent.NAME).addObserver(fEventHandler);
	}

	/**
	 * @param owner
	 * @param listener
	 * @param context
	 * @param locator
	 * @param rls
	 */
	private void init(DataViewBoard owner,
			Listener listener,
			DataViewControlContext context) {
		this.fListener = listener;
		this.fOwner = owner;
		this.fControlContext = context;

		this.fPopupEventHandler = new PopupEventHandler();
		this.fEventHandler = new EventHandler();

		fPopupMenu = UIFactoryMgr.createPopupMenu();
		fMenuItemRemove = UIFactoryMgr.createMenuItem();
		fMenuItemRemove.setText(MessageRepository.get(Msg.ACTIONS_REMOVE_DATAVIEW_CONTROL));
		fMenuItemViewAsXML = UIFactoryMgr.createMenuItem(
				new ActionViewXML(fControlContext.getViewContainer()) {
					private static final long serialVersionUID = 391758259112654610L;

					protected String getXML() throws Exception {
						return getXMLDefinitionForDataView();
					}
				});
		fMenuMove = UIFactoryMgr.createMenu();
		fMenuMove.setText(MessageRepository.get(Msg.ACTIONS_MOVE_DATAVIEW_CONTROL));
		
		fMenuItemRemove.addActionListener(fEventHandler);

		fPopupMenu.add(fMenuItemRemove);
		fPopupMenu.add(fMenuItemViewAsXML);
		fPopupMenu.add(fMenuMove);
	}


	/**
	 * @param owner the new owner of the control
	 */
	public void setOwner(DataViewBoard owner) {
		fOwner = owner;
	}

	/**
	 * @return the owner of this control
	 */
	public DataViewBoard getOwner() {
		return fOwner;
	}
	
	/**
	 * @param controlContext the new control context
	 */
	public void setControlContext(DataViewControlContext controlContext) {
		fControlContext = controlContext;
	}

	/**
	 * @return
	 * @throws XMLException
	 */
	protected String getXMLDefinitionForDataView() throws XMLException {
		return XMLUtils.toXMLBuffer(null, fDataView, "rms", false).toString();
	}

	/**
	 * @return The realized query
	 */
	public RealizedQuery getRealizedQuery() {
	    return fRealizedCube;
	}

	/**
	 * @return The data view
	 */
	public DataView getDataView() {
	    return fDataView;
	}

	/**
	 * Realizes internal query based on the given context.
	 * @param context ResourceID to use when completing relative resources
	 */
	private void realizeQuery(ResourceId context) {
	    this.fRealizedCube = new RealizedQuery(fControlContext.getSessionArtefactLocator(),
	    		this.fDataView.getQueryDef(), context);
	}

	/**
	 * @return the context id
	 */
	public ResourceId getContextId() {
		return fContextId;
	}

	/**
	 * @return an instance of LegendPanelSimple that shows simplified info
	 */
	public LegendPanelSimple getLegendPanelSimple() {
		return this.fLegendPanelSimple;
	}

	/**
	 * @return an instance of LegendPanelDetailed that shows verbose info
	 */
	public LegendPanelDetailed getLegendPanelDetailed() {
		return this.fLegendPanelDetailed;
	}

	/**
	 * @see com.ixora.rms.dataengine.DataQuery.FineListener#dataAvailable(com.ixora.rms.dataengine.DataQuery.Data)
	 */
	public final void dataAvailable(final QueryData data) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					_handleDataAvailable(data);
					sTotalCallsToDataAvailable++;
					// see if it's time to force the painting of the
					// app frame
					if(sTotalCallsToDataAvailable > 20) {
						sTotalCallsToDataAvailable = 0;
						JRootPane appRootPane = fControlContext.getViewContainer().getAppFrame().getRootPane();
						appRootPane.paintImmediately(appRootPane.getBounds());
					}
				} catch(Exception e) {
					UIExceptionMgr.exception(e);
				}
			}
		});
	}

	/**
	 * @see com.ixora.rms.dataengine.DataQueryExecutor.QueryListener#isExpired()
	 */
	public void expired() {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					handleExpired();
				} catch(Exception e) {
					UIExceptionMgr.exception(e);
				}
			}
		});
	}


	/**
	 * @see com.ixora.rms.dataengine.external.QueryListener#reactionsArmed(com.ixora.rms.reactions.ReactionId)
	 */
	public void reactionsArmed(final ReactionId[] reactions) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					handleReactionsArmed(reactions);
				} catch(Exception e) {
					UIExceptionMgr.exception(e);
				}
			}
		});
	}

	/**
	 * @see com.ixora.rms.dataengine.external.QueryListener#reactionsDisarmed(com.ixora.rms.reactions.ReactionId)
	 */
	public void reactionsDisarmed(final ReactionId[] reactions) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					handleReactionsDisarmed(reactions);
				} catch(Exception e) {
					UIExceptionMgr.exception(e);
				}
			}
		});
	}

	/**
	 * @see com.ixora.rms.dataengine.external.QueryListener#reactionsFired(com.ixora.rms.reactions.ReactionId)
	 */
	public void reactionsFired(final ReactionId[] reactions) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				try {
					handleReactionsFired(reactions);
				} catch(Exception e) {
					UIExceptionMgr.exception(e);
				}
			}
		});
	}

	/**
	 * @return a descriptor for this control's state
	 */
	public abstract DataViewControlDescriptor getDescriptor();

	/**
	 * Subclasses must read their state from the given descriptor.
	 * @param desc
	 */
	public abstract void setUpFromDescriptor(DataViewControlDescriptor desc);

	/**
	 * Builds the legend. This will be also invoked when data view info becomes available so it
	 * can be used by subclasses to update their info widgets.
	 */
	protected void buildLegend() {
		fLegend = new Legend(createHmlDescription());
		fLegendPanelSimple = new LegendPanelSimple(fLegend);
		fLegendPanelDetailed = new LegendPanelDetailed(fLegend);
		getDisplayPanel().setToolTipText(createHmlDescriptionFormatted());
	}

	/**
	 * @return
	 */
	protected JPanel getDisplayPanel() {
		if(fDisplayPanel == null) {
			fDisplayPanel = new JPanel();
		}
		return fDisplayPanel;
	}

	/**
	 * @return the translated context of the current view
	 */
	protected String getTranslatedContextPath() {
		return fTranslatedContext == null ? "-" : fTranslatedContext ;
	}

	/**
	 * @return
	 */
	protected String getTranslatedViewName() {
		String name;
		if(fDataViewInfo == null) {
			name = fDataView.getName();
		} else {
			name = fDataViewInfo.getTranslatedName();
		}
		return name;
	}

	/**
	 * @return
	 */
	protected String getTranslatedViewDescription() {
		String desc;
		if(fDataViewInfo == null) {
			desc = fDataView.getDescription();
		} else {
			desc = fDataViewInfo.getTranslatedDescription();
		}
		return desc;
	}

	/**
	 * Creates an html text with the name and description of the
	 * hosted data view. Subclasses could choose to show this text using
	 * a tooltip; by default the tooltip of the main control panel will have
	 * this text for it's tooltip.
	 * @return
	 */
	protected String createHmlDescription() {
		return "<html><b>" + getTranslatedViewName()
		+ "</b><br><span style='color:#FFFFFF'>" + getTranslatedContextPath()
		+ "</span><br>" + getTranslatedViewDescription() + "</html>";
	}

	/**
	 * Creates an html text with the name and description of the
	 * hosted data view. Subclasses could choose to show this text using
	 * a tooltip; by default the tooltip of the main control panel will have
	 * this text for it's tooltip.
	 * @return
	 */
	protected String createHmlDescriptionFormatted() {
		return "<html><b>" + getTranslatedViewName()
		+ "</b><br><span style='color:#FFFFFF'>"
		+ UIUtils.getMultilineHtmlFragment(getTranslatedContextPath(),
				UIConfiguration.getMaximumLineLengthForToolTipText())
		+ "</span><br>"
		+ UIUtils.getMultilineHtmlFragment(getTranslatedViewDescription(),
				UIConfiguration.getMaximumLineLengthForToolTipText()) + "</html>";
	}

	/**
	 * Performs the cleanup before
	 * the destruction.
	 */
	protected void cleanup() {
		// clean up listeners
		ConfigurationMgr.get(ReactionsComponent.NAME).deleteObserver(fEventHandler);
	}

	/**
	 * Resets the view.
	 */
	protected abstract void reset();

	/**
	 * Shows the popup menu.
	 * @param e
	 */
	protected void handleShowPopup(MouseEvent e) {
		try {
			Map<String, List<DataViewBoard>> ab = fControlContext.getAvailableDataViewBoards(this);
			if(Utils.isEmptyMap(ab)) {
				fMenuMove.setVisible(false);
			} else {
				// clean up first
				fMenuMove.removeAll();
				// add the new ones
				for(Map.Entry<String, List<DataViewBoard>> entry : ab.entrySet()) {
					final String fscreen = entry.getKey();
					JMenu sm = UIFactoryMgr.createMenu();
					sm.setText(entry.getKey() + "...");
					for(DataViewBoard dvb : entry.getValue()) {
						final DataViewBoard fdvb = dvb;
						JMenuItem mi = UIFactoryMgr.createMenuItem();
						mi.setText(dvb.getTitle());
						mi.addActionListener(new ActionListener(){
							public void actionPerformed(ActionEvent e) {
								handleMove(fscreen, fdvb.getTitle());
							}});
						sm.add(mi);
					}
					fMenuMove.add(sm);
				}
				fMenuMove.setVisible(true);
			}
			fPopupMenu.show(e.getComponent(), e.getX(), e.getY());
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * Removes this control from its parent.
	 */
	protected void handleRemove() {
		try {
			cleanup();
			fOwner.removeControl(this);
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * Removes this control from its parent.
	 * @param screen
	 * @param viewBoard
	 */
	protected void handleMove(String screen, String viewBoard) {
		try {
			fControlContext.getDataViewControlCallback().move(this, screen, viewBoard);
		} catch(Exception ex) {
			UIExceptionMgr.userException(ex);
		}
	}

	/**
	 * @param data
	 */
	protected abstract void handleDataAvailable(QueryData data);

	/**
	 * @param data
	 */
	private void _handleDataAvailable(QueryData data) {
		// get info if needed
		if(this.fDataViewInfo == null) {
			// this only happens for counters
			// try and find info on the counter
			ResourceId rid = fContextId;
			// for counter queries, the context is the entity to which
			// the counter belongs so in order to find counter info
			// we need to construct a resource id from context and
			// query identifier
			if(fDataView.getSource() == DataView.DATAVIEW_SOURCE_COUNTER) {
				rid = new ResourceId(
					fContextId.getHostId(),
					fContextId.getAgentId(),
					fContextId.getEntityId(),
					new CounterId(fDataView.getQueryDef().getIdentifier()));
			}
			fSessionResourceInfoOnCounter = fControlContext.getSessionArtefactLocator().getResourceInfo(rid);
			if(fSessionResourceInfoOnCounter != null
					&& fSessionResourceInfoOnCounter.getCounterInfo() != null) {
				this.fDataViewInfo = new SessionDataViewInfo(
						fSessionResourceInfoOnCounter.getCounterInfo(), fDataView);
				this.fTranslatedContext = fSessionResourceInfoOnCounter.getCounterInfo().getTranslatedCounterPath();
			}
			buildLegend();
		}

        // Localize query results
        for (QuerySeries querySeries : data) {
            for (QueryResultData queryResultData : querySeries) {
            	// Look for translation data in the cache
            	// but ignore TIMESTAMP
            	ResourceId rID = queryResultData.getMatchedResourceId();
            	if(rID.getRepresentation() == ResourceId.COUNTER
            			&& CounterDescriptor.TIMESTAMP_ID.equals(rID.getCounterId())) {
            		continue;
            	}
            	SessionResourceInfo rInfo = fLocalizationInfoCache.get(rID);
            	if (rInfo == null) {
            		rInfo = fControlContext.getSessionArtefactLocator().getResourceInfo(rID);
            		fLocalizationInfoCache.put(rID, rInfo);
            	}
                queryResultData.localizeTokens(rInfo);
            }
        }

        // let subclasses process data
        handleDataAvailable(data);
	}

	/**
	 * Subclasses interested in handling the event of the control expiration
	 * should override this method.
	 */
	protected void handleExpired() {
		setBackground(Color.RED);
	}

	/**
	 * @param reactions
	 * @param revs
	 */
	protected void handleReactionsArmed(ReactionId[] reactions) {
		if(fReactionsAlertPanel == null) {
			return;
		}
		fReactionsAlertPanel.setVisible(true);
		fReactionsAlertPanel.reactionsArmed(reactions);
	}

	/**
	 * @param reaction
	 * @param revs
	 */
	protected void handleReactionsDisarmed(ReactionId[] reactions) {
		if(fReactionsAlertPanel == null) {
			return;
		}
		fReactionsAlertPanel.setVisible(false);
		fReactionsAlertPanel.reactionsDisarmed(reactions);
	}

	/**
	 * @param reactions
	 */
	protected void handleReactionsFired(ReactionId[] reactions) {
		if(fReactionsAlertPanel == null) {
			return;
		}
		fReactionsAlertPanel.setVisible(true);
		fReactionsAlertPanel.reactionsFired(reactions);
	}

	/**
	 * Fires the control in focus event.
	 */
	protected void fireControlInFocus() {
		if(fListener != null) {
			this.fListener.controlInFocus(this);
		}
	}

	/**ó
	 * @param desc
	 */
	protected void prepareDescriptor(DataViewControlDescriptor desc) {
		if(fDataView.getSource() == DataView.DATAVIEW_SOURCE_REPOSITORY) {
			desc.setDataViewId(new DataViewId(this.fContextId, this.fDataView.getName()));
		} else if(fDataView.getSource() == DataView.DATAVIEW_SOURCE_USER) {
			desc.setDataView(this.fContextId, this.fDataView);
		} else if(fDataView.getSource() == DataView.DATAVIEW_SOURCE_COUNTER) {
			desc.setDataView(this.fContextId, this.fDataView);
		}
	}

	/**
	 * @param o
	 * @param arg
	 */
	private void handleUpdate(Observable o, Object arg) {
		ComponentConfiguration conf = ConfigurationMgr.get(ReactionsComponent.NAME);
		if(conf == o) {
			if(ReactionsComponentConfigurationConstants.REACTIONS_ENABLED.equals(arg)) {
				if(!conf.getBoolean(ReactionsComponentConfigurationConstants.REACTIONS_ENABLED)) {
					if(fReactionsAlertPanel != null) {
						fReactionsAlertPanel.setVisible(false);
					}
				}
			}
		}
	}

	/**
	 * @see com.ixora.rms.exporter.html.HTMLProvider#toHTML(java.lang.StringBuilder, java.io.File)
	 */
	public void toHTML(StringBuilder buff, File root) throws IOException {
		buff.append("<p><div class='controlName'>").append(getTranslatedViewName()).append("</div>");
		buff.append("<div class='controlDescription'>").append(getTranslatedViewDescription()).append("</div>");
		buff.append("<div class='controlContext'>").append(getTranslatedContextPath()).append("</div></p>");
	}
}
