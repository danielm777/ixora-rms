/*
 * Created on 14-Jan-2004
 */
package com.ixora.rms.ui.dataviewboard;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.plaf.InternalFrameUI;
import javax.swing.plaf.basic.BasicInternalFrameUI;

import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.popup.PopupListener;
import com.ixora.rms.ResourceId;
import com.ixora.rms.client.QueryRealizer;
import com.ixora.rms.client.locator.SessionArtefactInfoLocator;
import com.ixora.rms.client.locator.SessionDataViewInfo;
import com.ixora.rms.dataengine.RealizedQuery;
import com.ixora.rms.dataengine.Style;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.repository.DataViewId;
import com.ixora.rms.repository.QueryId;
import com.ixora.rms.services.DataEngineService;
import com.ixora.rms.services.ReactionLogService;
import com.ixora.rms.ui.dataviewboard.exception.FailedToCreateControl;
import com.ixora.rms.ui.dataviewboard.exception.FailedToPlotView;
import com.ixora.rms.ui.exporter.HTMLProvider;

/**
 * @author Daniel Moraru
 */
public abstract class DataViewBoard extends JInternalFrame implements HTMLProvider {
	private static final long serialVersionUID = -9120328043793730316L;
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(DataViewBoard.class);

	/** Popup menu that appears on the title bar */
	private JPopupMenu fTitleBarPopupMenu;
	
    /**
	 * Listener.
	 */
	public interface Listener {
		/**
		 * Invoked when the control gains focus.
		 * @param control
		 */
		void controlInFocus(DataViewControl control);
		/**
		 * Invoked when the control loses focus.
		 * @param control
		 */
		void controlOutOfFocus(DataViewControl control);
		/**
		 * Invoked when a control has been removed from the board.
		 * @param control
		 */
		void controlRemoved(DataViewControl control);
	}

   /**
	* Event handler.
	*/
	private final class EventHandler extends PopupListener implements DataViewControl.Listener {
		/**
		 * @see com.ixora.rms.ui.dataviewboard.DataViewControl.Listener#controlInFocus(com.ixora.rms.ui.dataviewboard.DataViewControl)
		 */
		public void controlInFocus(DataViewControl control) {
			handleControlInFocus(control);
		}
		protected void showPopup(MouseEvent me) {
			handleShowTitleBarPopupMenu(me);
		}
	}


	/** Listener */
	private Listener fListener;
	/** DataViewControlContext */
	private DataViewControlContext fControlContext;
	/** The control having the focus */
	private DataViewControl fControlInFocus;
	/** Control board */
	protected JPanel fBoard;
	/** Event handler */
	protected EventHandler fEventHandler;
	/** Date engine service */
	protected DataEngineService fDataEngine;
	/** Reaction log service */
	protected ReactionLogService fReactionLog;
	/** Reference to all controls on the board. List of DataViewControl. */
	protected LinkedList<DataViewControl> fControls;
	/** Info locator */
	protected SessionArtefactInfoLocator fLocator;
	/** Query realizer */
	protected QueryRealizer fQueryRealizer;

	/**
	 * @param des
	 * @param model
	 * @param title
	 * @param resizable
	 * @param closable
	 * @param maximizable
	 * @param iconifiable
	 */
	protected DataViewBoard(
		QueryRealizer qr,
		DataEngineService des,
		ReactionLogService rls,
		SessionArtefactInfoLocator locator,
		String title,
		boolean resizable,
		boolean closable,
		boolean maximizable,
		boolean iconifiable) {
		super(title, resizable, closable, maximizable, iconifiable);
		init(qr, des, rls, locator);
	}

	/**
	 * @param me
	 */
	public void handleShowTitleBarPopupMenu(MouseEvent me) {
		fTitleBarPopupMenu.show(me.getComponent(), me.getX(), me.getY());		
	}

	/**
	 * Sets the listener.
	 * @param listener
	 */
	public void setListener(Listener listener) {
		if(listener == null) {
			throw new IllegalArgumentException("null listener");
		}
		this.fListener = listener;
	}

	/**
	 * Sets the context for the controls managed by this board.
	 * @param ctxt
	 */
	public void setDataViewControlContext(DataViewControlContext ctxt) {
		if(ctxt == null) {
			throw new IllegalArgumentException("null data view control context");
		}
		this.fControlContext = ctxt;
		this.fTitleBarPopupMenu.add(fControlContext.getViewContainer()
				.getSessionView().getActionSetViewBoardName());		
	}

	/**
	 * Asks the view board to plot the given resource representing
	 * a counter.
	 * @param counterId counter to plot
	 * @param name the name of the data view
	 * @param desc the description of the data view
	 * @param style style to assign to the data view
	 * @throws FailedToPlotView
	 * @throws FailedToCreateControl
	 */
	public void plot(ResourceId counterId, String name, String desc, Style style)
		throws FailedToPlotView, FailedToCreateControl {
		plot(counterId, name, desc, style, (DataViewControlDescriptor)null);
	}

    /**
     * Asks the view board to plot the given resources representing
     * a set of counter.
     * @param context the context of the given counters
     * @param counters counters to plot
     * @param viewName the name of the newly created view
     * @return a set of counters which could not be plotted by this board
     * @throws FailedToPlotView
     * @throws FailedToCreateControl
     */
    public List<ResourceId> plot(ResourceId context, List<ResourceId> counters, String viewName) throws FailedToPlotView, FailedToCreateControl {
        // create a resource info for the context of this counter
        List<ResourceId> rejected = new LinkedList<ResourceId>();
        DataView cdv = createDataViewForCounters(context, counters, viewName, rejected);
        if(cdv == null) {
            return counters;
        }
        plot(context, cdv, null);
        if(rejected.size() > 0) {
            return rejected;
        }
        return null;
    }

	/**
	 * Asks the view board to plot the given view.
	 * @param context
	 * @param view
	 * @throws FailedToPlotView
	 * @throws FailedToCreateControl
	 */
	public void plot(ResourceId context, DataView view) throws FailedToPlotView, FailedToCreateControl {
		plot(context, view, (DataViewControlDescriptor)null);
	}

	/**
	 * Asks the view board to plot the given resource representing
	 * a counter.
	 * @param counterId counter to plot
	 * @param name
	 * @param desc
	 * @param style style to attach to the created data view
	 * @param controlDescriptor
	 * @throws FailedToPlotView
	 * @throws FailedToCreateControl
	 */
	private void plot(ResourceId counterId, String name, String desc, Style style, DataViewControlDescriptor controlDescriptor)
		throws FailedToPlotView, FailedToCreateControl {
		if(counterId.getRepresentation() != ResourceId.COUNTER) {
			// this should not happen
			FailedToPlotView ex = new FailedToPlotView("Tried to plot a resource which was not a counter: " + counterId);
			ex.setIsInternalAppError();
		    throw ex;
		}
		// create a resource info for the context of this counter
		DataView cdv = createDataViewForCounter(counterId, name, desc, style);
		if(cdv == null) {
			// this should not happen
			FailedToPlotView ex = new FailedToPlotView("Data view board could not create a view for counter " + counterId);
			ex.setIsInternalAppError();
			throw ex;
		}
		// the context of a counter query is the entity to which the counter belongs
		plot(new ResourceId(
				counterId.getHostId(),
				counterId.getAgentId(),
				counterId.getEntityId(),
				null), cdv, controlDescriptor);
	}

	/**
	 * Asks the view board to plot the given view.
	 * @param context
	 * @param dataView
	 * @param controlDescriptor
	 * @throws FailedToPlotView
	 * @throws FailedToCreateControl
	 */
	private void plot(
	        ResourceId context,
	        DataView dataView,
			DataViewControlDescriptor controlDescriptor) throws FailedToPlotView, FailedToCreateControl {
		// create a control to render the cube
        DataViewControl dvc = createControl(fControlContext, context, dataView);
		prepareControl(context, dvc, controlDescriptor);
	}

	/**
	 * @param dvc
	 * @param controlDescriptor
	 */
	private void prepareControl(
			ResourceId context,
			DataViewControl dvc, DataViewControlDescriptor controlDescriptor) {
		// apply descriptor if given
		if(controlDescriptor != null) {
			dvc.setUpFromDescriptor(controlDescriptor);
		}
		// register query with the data engine and register the control
		// as a listener to the query
		RealizedQuery q = dvc.getRealizedQuery();
		QueryId qid = new QueryId(context,	q.getIdentifier());
		if(!this.fDataEngine.isQueryRegistered(qid)) {
			this.fDataEngine.addQuery(qid, q, dvc);
		} else {
			this.fDataEngine.addQueryListener(qid, dvc);
		}

		// register query with query realizer if in a live session
		if(fQueryRealizer != null) {
			try {
				this.fQueryRealizer.realizeQuery(context, dvc.getDataView().getQueryDef(), null);
			}catch(Exception e) {
				logger.error(e);
				// TODO localize
				fControlContext.getViewContainer()
					.getAppStatusBar().setErrorMessage("Failed to realize query " + qid, e);
			}
		}

		// add to board
		addControl(dvc);
	}

	/**
	 * @return a descriptor for this board.
	 */
	public DataViewBoardDescriptor getDescriptor() {
	    List<DataViewControlDescriptor> descriptors = new LinkedList<DataViewControlDescriptor>();
	    for(Iterator<DataViewControl> iter = fControls.iterator(); iter.hasNext();) {
	    	DataViewControl dvc = iter.next();
            descriptors.add(dvc.getDescriptor());
        }
	    return new DataViewBoardDescriptor(descriptors,
	            getClass().getName(),
	            getLocation(), getSize(), getTitle());
	}

	/**
	 * Sets up the board with info from descriptor.
	 * @param desc
	 * @throws FailedToCreateControl
	 */
	public void setUpFromDescriptor(DataViewBoardDescriptor desc) throws FailedToCreateControl {
	    Point p = desc.getLocation();
	    if(p != null) {
	        setLocation(p);
	    }
	    Dimension d = desc.getDimension();
	    if(d != null) {
	        setSize(d);
	    }
	    String title = desc.getTitle();
	    if(title == null) {
	        title = "";
	    }
	    setTitle(title);
	    List<DataViewControlDescriptor> descriptors = desc.getControlDescriptors();
	    for(Iterator<DataViewControlDescriptor> iter = descriptors.iterator(); iter.hasNext();) {
	    	DataViewControlDescriptor cd = iter.next();
            if(cd.getDataViewId() != null){
                // data view
                DataViewId dvid = cd.getDataViewId();
                try {
                	// use locator to find DataView
                	SessionDataViewInfo pdv = fLocator.getDataViewInfo(dvid);
                	if(pdv == null) {
                		// might happen if the data view with this id has been
                		// removed
                		// TODO: localize
                		throw new FailedToPlotView("Could not find data view " + dvid);
                	}
                    plot(dvid.getContext(), pdv.getDataView(), cd);
                } catch (FailedToPlotView e) {
                	// report non-critical error
                    fControlContext.getViewContainer().getAppStatusBar().setErrorMessage(e.getLocalizedMessage(), null);
                }
            } else if(cd.getDataView() != null) {
            	// on the fly data view
                try {
                    plot(cd.getDataViewContext(), cd.getDataView(), cd);
                } catch (FailedToPlotView e) {
                	// report non-critical error
                    fControlContext.getViewContainer().getAppStatusBar().setErrorMessage(e.getLocalizedMessage(), null);
                }
            }
        }
	}

	/**
	 * Removes a control from the board.
	 * @param dvc
	 */
	public void removeControl(DataViewControl dvc) {
		// unregister first with the data engine
		QueryId qid = new QueryId(dvc.getContextId(),
				dvc.getRealizedQuery().getIdentifier());
		this.fDataEngine.removeQueryListener(qid, dvc);

		// unregister with the query realizer if in a live session
		if(fQueryRealizer != null) {
			try {
				this.fQueryRealizer.unrealizeQuery(qid, false);
			}catch(Exception e) {
				logger.error("Failed to unrealize query " + qid);
			}
		}

		// remove the control component
		fBoard.remove(dvc);
		fControls.remove(dvc);
		validate();
		repaint();

		// fire event
		fireControlRemoved(dvc);
	}

    /**
     * @return true if this board has the maximum number of controls plotted on it;
     * the default implementation returns always false
     */
    public boolean reachedMaximumControls() {
        return false;
    }

	/**
	 * Overriden to unregister all controls before disposing.
	 * @see javax.swing.JInternalFrame#dispose()
	 */
	@SuppressWarnings("unchecked")
	public void dispose() {
		// unregister controls first
		DataViewControl dvc;
		// need to operate on a clone of the exisiting list
		// to be able to reuse removeControl(dvc)
		for(Iterator<DataViewControl> iter = ((List<DataViewControl>)fControls.clone()).iterator(); iter.hasNext();) {
			dvc = iter.next();
			removeControl(dvc);
		}
		super.dispose();
	}

	/**
	 * Adds a control to the board.
	 * @param dvc
	 */
	protected void addControl(DataViewControl dvc) {
		fBoard.add(dvc);
		fControls.add(dvc);
		revalidate();
		repaint();
	}

	/**
	 * Fires the control removed event.
	 * @param control
	 */
	protected void fireControlRemoved(DataViewControl control) {
		if(this.fListener == null) {
			return;
		}
		this.fListener.controlRemoved(control);
	}

	/**
	 * Fires the control in focus event.
	 * @param control
	 */
	protected void fireControlInFocus(DataViewControl control) {
		if(this.fListener == null) {
			return;
		}
		this.fListener.controlInFocus(control);
	}

	/**
	 * Fires the control out of focus event.
	 * @param control
	 */
	protected void fireControlOutOfFocus(DataViewControl control) {
		if(this.fListener == null) {
			return;
		}
		this.fListener.controlOutOfFocus(control);
	}

	/**
	 * @param control
	 */
	protected void handleControlInFocus(DataViewControl control) {
		if(this.fControlInFocus != null && this.fControlInFocus != control) {
			fireControlOutOfFocus(this.fControlInFocus);
		}
		this.fControlInFocus = control;
		fireControlInFocus(control);
	}

	/**
	 * Subclasses must return their specific controls here.
	 * @param context
	 * @param resourceContext
	 * @param view
	 * @return
	 * @throws FailedToCreateControl
	 */
	protected abstract DataViewControl createControl(
			DataViewControlContext context,
	        ResourceId resourceContext,
	        DataView view) throws FailedToCreateControl;

	/**
	 * Subclasses must return a data view for the given counter.
	 * @param counter
	 * @param name the name of the data view
	 * @param desc the description of the data view
	 * @param style the style to assign to the data view
	 * @return
	 */
	protected abstract DataView createDataViewForCounter(
			ResourceId counter, String name, String desc, Style style);

    /**
     * Subclasses must return a data view for the given counters.
     * @param context
     * @param counters in parameter
     * @param viewName the name of the newly created view
     * @param rejected a set of counters that where rejected; out parameter
     * @return
     */
    protected abstract DataView createDataViewForCounters(
    		ResourceId context, List<ResourceId> counters, String viewName, List<ResourceId> rejected);

	/**
	 * @param qr
	 * @param des
	 * @param rls
	 * @param model
	 */
	private void init(QueryRealizer qr, DataEngineService des, ReactionLogService rls, SessionArtefactInfoLocator locator) {
		this.fQueryRealizer = qr;
		this.fDataEngine = des;
		this.fReactionLog = rls;
		this.fLocator = locator;
		this.fEventHandler = new EventHandler();
		this.fControls = new LinkedList<DataViewControl>();
		this.fTitleBarPopupMenu = UIFactoryMgr.createPopupMenu();
		
		InternalFrameUI ui = getUI();
		if(ui instanceof BasicInternalFrameUI) {
			BasicInternalFrameUI bui = (BasicInternalFrameUI)ui;
			bui.getNorthPane().addMouseListener(fEventHandler);
		}
	}

	/**
	 * Resets all controls.
	 */
	public void reset() {
		for(DataViewControl dvc : fControls) {
			dvc.reset();
		}
	}

	/**
	 * @throws IOException
	 * @see com.ixora.rms.ui.exporter.HTMLProvider#toHTML(java.lang.StringBuilder, java.io.File)
	 */
	public void toHTML(StringBuilder buff, File root) throws IOException {
		buff.append("<div class='board'><p class='boardTitle'>")
			.append(getTitle()).append("</p>");
		buff.append("<table>");
		int i = 0;
		for(DataViewControl control : fControls) {
			if(i % 3 == 0) {
				buff.append("<tr>");
			}
			buff.append("<td>");
			control.toHTML(buff, root);
			buff.append("</td>");
			++i;
			if(i % 3 == 0) {
				buff.append("</tr>");
			}
		}
		buff.append("</table>");
		buff.append("</div>");
	}
}
