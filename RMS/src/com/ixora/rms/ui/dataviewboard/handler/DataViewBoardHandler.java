/*
 * Created on 06-Sep-2004
 */
package com.ixora.rms.ui.dataviewboard.handler;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javax.swing.ImageIcon;

import com.ixora.rms.ResourceId;
import com.ixora.common.MessageRepository;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterType;
import com.ixora.rms.client.QueryRealizer;
import com.ixora.rms.client.locator.SessionArtefactInfoLocator;
import com.ixora.rms.client.locator.SessionArtefactInfoLocatorImpl;
import com.ixora.rms.client.locator.SessionDashboardInfo;
import com.ixora.rms.client.locator.SessionDataViewInfo;
import com.ixora.rms.client.locator.SessionResourceInfo;
import com.ixora.rms.client.model.CounterInfo;
import com.ixora.rms.client.model.ResourceInfo;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.dataengine.Style;
import com.ixora.rms.repository.DashboardId;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.repository.DataViewBoardInstallationData;
import com.ixora.rms.repository.DataViewId;
import com.ixora.rms.repository.QueryId;
import com.ixora.rms.services.DataEngineService;
import com.ixora.rms.services.DataViewBoardRepositoryService;
import com.ixora.rms.services.DataViewRepositoryService;
import com.ixora.rms.services.ReactionLogService;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.dataviewboard.DataViewBoard;
import com.ixora.rms.ui.dataviewboard.DataViewBoardCounterFilter;
import com.ixora.rms.ui.dataviewboard.DataViewBoardDescriptor;
import com.ixora.rms.ui.dataviewboard.DataViewControl;
import com.ixora.rms.ui.dataviewboard.DataViewControlContext;
import com.ixora.rms.ui.dataviewboard.charts.ChartsBoard;
import com.ixora.rms.ui.dataviewboard.exception.FailedToCreateControl;
import com.ixora.rms.ui.dataviewboard.exception.FailedToPlotView;
import com.ixora.rms.ui.dataviewboard.exception.ScreenNameAlreadyExists;
import com.ixora.rms.ui.dataviewboard.legend.LegendDialog;
import com.ixora.rms.ui.exporter.HTMLProvider;
import com.ixora.rms.ui.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class DataViewBoardHandler extends Observable
		implements DataViewPlotter, DataViewControlContext, HTMLProvider {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(DataViewBoardHandler.class);
	/** The name of the default screen */
	private static final String DEFAULT_SCREEN = "Untitled"; //TODO: localize
    /** View container */
    private RMSViewContainer fViewContainer;
    /** Session model */
    private SessionModel fSessionModel;
    /** Info locator for plotted artefacts */
    private SessionArtefactInfoLocator fPlottedInfoLocator;
    /** Event handler */
	private EventHandler fEventHandler;
	/**
	 * Data engine service used to satisfy data queries.
	 */
	private DataEngineService fDataEngine;
	/**
	 * Reactio log service.
	 */
	private ReactionLogService fReactionLog;
	/**
	 * Data view boards installation repository.
	 */
	private DataViewBoardRepositoryService fDataViewBoardRepository;
	/**
	 * Cached board counter filters for all boards in the repository.
	 */
	private Map<String, DataViewBoardCounterFilter> fBoardCounterFilters;
	/**
	 * Cached board icons.
	 */
	private Map<String, ImageIcon> fBoardIcons;
	/**
	 * Screens.
	 */
	private Map<String, DataViewScreen> fScreens;
	/** Current screen */
	private String fCurrentScreen;
	/** Query realizer */
	private QueryRealizer fQueryRealizer;

	/**
	 * Event handler.
	 */
	private final class EventHandler implements DataViewBoard.Listener {
		/**
		 * @see com.ixora.rms.ui.dataviewboard.DataViewBoard.Listener#controlInFocus(com.ixora.rms.ui.dataviewboard.DataViewControl)
		 */
		public void controlInFocus(DataViewControl control) {
			handleControlInFocus(control);
		}
		/**
		 * @see com.ixora.rms.ui.dataviewboard.DataViewBoard.Listener#controlOutOfFocus(com.ixora.rms.ui.dataviewboard.DataViewControl)
		 */
		public void controlOutOfFocus(DataViewControl control) {
			handleControlOutOfFocus(control);
		}
		/**
		 * @see com.ixora.rms.ui.dataviewboard.DataViewBoard.Listener#controlRemoved(com.ixora.rms.ui.dataviewboard.DataViewControl)
		 */
		public void controlRemoved(DataViewControl control) {
			handleControlRemoved(control);
		}
	}

	/**
     * Constructor.
     * @param vc
     * @param qr query realizer is null when in log playback mode
     * @param des
     * @param rls
     * @param brs
     * @param vrs
     * @param model
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public DataViewBoardHandler(
    		RMSViewContainer vc,
			QueryRealizer qr,
    		DataEngineService des,
    		ReactionLogService rls,
			DataViewBoardRepositoryService brs,
			DataViewRepositoryService vrs,
			SessionModel model) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        super();
        this.fViewContainer = vc;
        this.fQueryRealizer = qr;
        this.fDataEngine = des;
        this.fReactionLog = rls;
        this.fSessionModel = model;
        this.fPlottedInfoLocator = new SessionArtefactInfoLocatorImpl(this.fSessionModel, vrs);
		this.fEventHandler = new EventHandler();
		this.fDataViewBoardRepository = brs;
		this.fScreens = new LinkedHashMap<String, DataViewScreen>();
		this.fScreens.put(DEFAULT_SCREEN, new DataViewScreen());
		this.fCurrentScreen = DEFAULT_SCREEN;
		// load cache with board counter filters
		Map<String, DataViewBoardInstallationData> map = fDataViewBoardRepository.getInstalledDataViewBoards();
		// linked map to preserve board preference order
		this.fBoardCounterFilters = new LinkedHashMap<String, DataViewBoardCounterFilter>(map.size());
		for(DataViewBoardInstallationData bid : map.values()) {
			String fc = bid.getBoardCounterFilterClass();
			if(fc != null) {
				DataViewBoardCounterFilter filter = (DataViewBoardCounterFilter)Class.forName(fc).newInstance();
				this.fBoardCounterFilters.put(bid.getBoardClass(), filter);
			} else {
				this.fBoardCounterFilters.put(bid.getBoardClass(), null);
			}
		}
		this.fBoardIcons = new HashMap<String, ImageIcon>(map.size());
		for(Map.Entry<String, DataViewBoardInstallationData> me : map.entrySet()) {
			this.fBoardIcons.put(me.getValue().getBoardClass(),
					UIConfiguration.getIcon(me.getValue().getBoardIcon()));
		}
	}

    /**
     * @see com.ixora.rms.ui.dataviewboard.DataViewBoard.Listener#controlInFocus(com.ixora.rms.ui.dataviewboard.DataViewControl)
     */
    private void handleControlInFocus(DataViewControl control) {
    	try {
			LegendDialog ld = LegendDialog.getLegendDialog();
			if(ld != null) {
				ld.setLegendPanelDetailed(control.getLegendPanelDetailed());
			} else {
				this.fViewContainer.getStatusBar().setCenterComponent(control.getLegendPanelSimple());
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
    }

    /**
     * @see com.ixora.rms.ui.dataviewboard.DataViewBoard.Listener#controlOutOfFocus(com.ixora.rms.ui.dataviewboard.DataViewControl)
     */
    private void handleControlOutOfFocus(DataViewControl control) {
		try {
			this.fViewContainer.getStatusBar().resetCenterComponent();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
    }

    /**
     * @see com.ixora.rms.ui.dataviewboard.DataViewBoard.Listener#controlRemoved(com.ixora.rms.ui.dataviewboard.DataViewControl)
     */
    private void handleControlRemoved(DataViewControl control) {
    }

	/**
	 * @see com.ixora.rms.ui.dataviewboard.handler.DataViewPlotter#plot(com.ixora.rms.internal.ResourceId, com.ixora.rms.repository.DataView)
	 */
	public void plot(ResourceId context, DataView view) {
		try {
			DataViewBoard board = getBoardForPlot(view.getBoardClass());
			board.plot(context, view);
			getCurrentScreen().moveToFront(board);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @see com.ixora.rms.ui.EntityConfigurationPanel.Callback#plot(com.ixora.rms.ResourceId, java.lang.String, java.lang.String, com.ixora.rms.dataengine.Style)
	 */
    public void plot(ResourceId counter, String name, String desc, Style style) {
		try {
			plotCounter(counter, name, desc, style);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
    }

    /**
     * Plots a counter.
     * @param counter
     * @param name
     * @param desc
     * @param style
     * @throws FailedToPlotView
     * @throws FailedToCreateControl
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private void plotCounter(ResourceId counter, String name, String desc, Style style) throws FailedToPlotView, FailedToCreateControl, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException, IllegalAccessException, InvocationTargetException {
		// get CounterInfo
		ResourceInfo[] ris = fSessionModel.getResourceInfo(counter, true);
		if(Utils.isEmptyArray(ris)) {
			// this should not happen
			FailedToPlotView ex = new FailedToPlotView("Could not find information on counter: " + counter);
			throw ex;
		}
		ResourceInfo counterResourceInfo = ris[0];
		CounterInfo counterInfo = counterResourceInfo.getCounterInfo();
		String viewBoardClassName = counterInfo.getViewboardClassName();
		if(viewBoardClassName == null) {
			DataViewBoard board = findSuitableBoardForCounter(counterInfo.getType());
			if(board == null) {
				// this should not happen
				FailedToPlotView ex = new FailedToPlotView("Could not find any data view board able to plot this counter: " + counter);
				ex.setIsInternalAppError();
				throw ex;
			}
			board.plot(counter, name, desc, style);
			getCurrentScreen().moveToFront(board);
		} else {
			// find the specialized viewboard for this counter
			DataViewBoard board = getBoardForPlot(viewBoardClassName);
			if(board == null) {
				// this should not happen
				FailedToPlotView ex = new FailedToPlotView("Could not find any data view board able to plot this counter: " + counter);
				ex.setIsInternalAppError();
				throw ex;
			}
			board.plot(counter, name, desc, style);
			getCurrentScreen().moveToFront(board);
		}
    }

	/**
	 * Different views of multiple counters will be created and plotted. All numeric counters
	 * will be plotted as one time series chart by default (unless the currently selected board accepts numerical counters)
	 * and all the strings as one property view.
	 * @throws FailedToPlotView
	 * @see com.ixora.rms.ui.EntityConfigurationPanel.Callback#plot(ResourceId, java.util.List)
	 */
	public void plot(ResourceId context, List<ResourceId> counters) {
		try {
            // find a board that accept the first counter in the query
            if(Utils.isEmptyCollection(counters)) {
                return;
            }

            ResourceId cid = counters.iterator().next();
			SessionResourceInfo ri = fPlottedInfoLocator.getResourceInfo(cid);
			if(ri == null || ri.getCounterInfo() == null) {
				// this should not happen
				throw new FailedToPlotView("Counter " + cid
				        + " not found");
			}
			CounterType counterType = ri.getCounterInfo().getDescriptor().getType();

	        DataViewBoard board = findSuitableBoardForCounter(counterType);
			if(board == null) {
				// this should not happen as we have default boards able to handle
                // all types of counters
				FailedToPlotView ex = new FailedToPlotView("Could not find any data view board able to plot this view");
				ex.setIsInternalAppError();
				throw ex;
			}

            // - create a valid view name in the context represented by the entity which
            // this counter belongs to
            // - the context for a set of counters is global
            String viewName = createValidDataViewName(null);
            if(Utils.isEmptyString(viewName)) {
                return;
            }
            List<ResourceId> rejected = board.plot(context, counters, viewName);
            if(rejected != null) {
                // do the same for the rejected counters
                plot(context, rejected);
            }
    		getCurrentScreen().moveToFront(board);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @param type
	 * @return
	 */
	private DataViewBoard findSuitableBoardForCounter(CounterType type) {
		// ask first the selected board
		DataViewBoard board = getCurrentScreen().getSelectedBoard();
		if(board != null) {
			DataViewBoardCounterFilter filter = fBoardCounterFilters.get(board.getClass().getName());
			if(filter == null || filter.accept(type)) {
                if(!board.reachedMaximumControls()) {
                    return board;
                }
			}
		}
	    // ask all existing boards if they support this type of counter
		Collection<DataViewBoard> boards = getCurrentScreen().getBoards();
		if(!Utils.isEmptyCollection(boards)) {
			for(DataViewBoard dvb : boards) {
				DataViewBoardCounterFilter filter = fBoardCounterFilters.get(dvb.getClass().getName());
				if(filter == null || filter.accept(type)) {
                    if(!dvb.reachedMaximumControls()) {
                        return dvb;
                    }
				}
			}
		}
		// else try and find an installed board that accepts it
		for(Map.Entry<String, DataViewBoardCounterFilter> me : fBoardCounterFilters.entrySet()) {
			if(me.getValue() == null || me.getValue().accept(type)) {
			    try {
			        return createBoard(me.getKey(), getCurrentScreen());
			    } catch(Exception e) {
			        logger.error(e);
			    }
			}
		}
		return null;
	}

	/**
	 * @param boardClass
	 * @return a data view board
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IllegalArgumentException
	 */
	private DataViewBoard getBoardForPlot(String boardClass) throws SecurityException, NoSuchMethodException, ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Collection<DataViewBoard> boards = getCurrentScreen().getBoards();
		DataViewBoard theOne = null;
		DataViewBoard board;
		for(Iterator<DataViewBoard> iter = boards.iterator(); iter.hasNext();) {
			board = iter.next();
			if(board.getClass().getName().equals(boardClass)
                    && !board.reachedMaximumControls()) {
				theOne = board;
				break;
			}
		}
		if(theOne == null) {
		    // create new board
		    theOne = createBoard(boardClass, getCurrentScreen());
		}
		return theOne;
	}

    /**
     * @return the current desktop pane.
     */
    public DataViewScreen getScreen() {
        return getCurrentScreen();
    }

    /**
     * @return the desktop pane.
     */
    public String getScreenName() {
        return fCurrentScreen;
    }

    /**
     * @return the desktop pane.
     */
    public String[] getScreenNames() {
        return fScreens.keySet().toArray(new String[fScreens.size()]);
    }

    /**
     * @return
     */
    public Collection<DataViewScreenDescriptor> getScreenDescriptors() {
    	List<DataViewScreenDescriptor> ret = new ArrayList<DataViewScreenDescriptor>(fScreens.size());
    	for(Map.Entry<String, DataViewScreen> entry : fScreens.entrySet()) {
    		DataViewScreenDescriptor desc = new DataViewScreenDescriptor(
    				entry.getKey(),
    				entry.getKey().equals(fCurrentScreen),
					entry.getValue().getBoardDescriptors());
    		ret.add(desc);
    	}
    	return ret;
    }

    /**
     * Creates a new data view board of the same type
     * as the one which is selected in the desktop pane
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    public void addNewBoard() throws SecurityException, IllegalArgumentException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
        DataViewBoard board = getCurrentScreen().getSelectedBoard();
		if(board == null) {
		    // default is chart
		    createBoard(ChartsBoard.class.getName(), getCurrentScreen());
		} else {
		    createBoard(board.getClass().getName(), getCurrentScreen());
		}
		fViewContainer.setSessionDirty(true);
    }

    /**
     * Creates a new data view board of the given type
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalArgumentException
     * @throws SecurityException
     */
    public void addNewBoard(String clazz) throws SecurityException, IllegalArgumentException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
	    createBoard(clazz, getCurrentScreen());
	    fViewContainer.setSessionDirty(true);
    }

    /**
     * @param descs
     * @throws SecurityException
     * @throws IllegalArgumentException
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     * @throws InstantiationException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public void initializeFromScreenDescriptors(Collection<DataViewScreenDescriptor> descs) throws SecurityException, IllegalArgumentException, NoSuchMethodException, ClassNotFoundException, InstantiationException, IllegalAccessException, InvocationTargetException {
    	fScreens.clear();
    	fCurrentScreen = null;
    	for(DataViewScreenDescriptor desc : descs) {
    		if(desc.isSelected()) {
    			fCurrentScreen = desc.getScreenName();
    		}
    		DataViewScreen screen = new DataViewScreen();
    		fScreens.put(desc.getScreenName(), screen);
    		Collection<DataViewBoardDescriptor> boardDescs = desc.getBoardDescriptors();
    		if(!Utils.isEmptyCollection(boardDescs)) {
	    		for(DataViewBoardDescriptor boardDesc : boardDescs) {
	    			try {
		    	        DataViewBoard dvb = createBoard(boardDesc.getBoardClass(), screen);
		    	        dvb.setUpFromDescriptor(boardDesc);
	    			} catch(Exception e) {
	    				UIExceptionMgr.userException(e);
	    			}
	    		}
    		}
    	}
    	// if none selected, select first entry or create the default screen
    	if(fCurrentScreen == null) {
    		if(fScreens.size() > 0) {
    			fCurrentScreen = fScreens.keySet().iterator().next();
    		} else {
    			this.fScreens.put(DEFAULT_SCREEN, new DataViewScreen());
    			this.fCurrentScreen = DEFAULT_SCREEN;
    		}
    	}
    	setCurrentScreen(fCurrentScreen);
    }

    /**
     * Tiles the boards.
     */
    public void tileBoards() {
        getCurrentScreen().tileBoards();
    }

    /**
     * @throws IOException
     * @see com.ixora.rms.ui.exporter.HTMLProvider#toHTML(java.lang.StringBuilder, java.io.File)
     */
    public void toHTML(StringBuilder buff, File root) throws IOException {
    	for(DataViewScreen screen : fScreens.values()) {
    		screen.toHTML(buff, root);
    	}
    }

    /**
     * Renames the selected view board.
     */
    public void renameBoard() {
        if(getCurrentScreen().getComponentCount() == 0) {
            return;
        }
        DataViewBoard dvb = getCurrentScreen().getSelectedBoard();
        if(dvb == null) {
            UIUtils.showError(fViewContainer.getAppFrame(),
                    MessageRepository.get(Msg.TEXT_ERROR),
                    MessageRepository.get(Msg.TEXT_PLEASE_SELECT_VIEWBOARD));
            return;
        }
        String newTitle = UIUtils.getStringInput(fViewContainer.getAppFrame(),
                MessageRepository.get(
                        Msg.TEXT_RENAMING_VIEWBOARD,
                        new String[]{dvb.getTitle()}),
                MessageRepository.get(
                        Msg.TEXT_NEW_VIEWBOARD_NAME)
                );
        if(newTitle != null) {
            dvb.setTitle(newTitle);
        }
    }

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewControlContext#getDataViewPlotter()
	 */
	public DataViewPlotter getDataViewPlotter() {
		return this;
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewControlContext#getViewContainer()
	 */
	public RMSViewContainer getViewContainer() {
		return this.fViewContainer;
	}

	/**
	 * @see com.ixora.rms.ui.dataviewboard.DataViewControlContext#getArtefactInfoLocator()
	 */
	public SessionArtefactInfoLocator getArtefactInfoLocator() {
		return this.fPlottedInfoLocator;
	}

    /**
     * @see com.ixora.rms.ui.dataviewboard.DataViewControlContext#isQueryRegistered(com.ixora.rms.repository.QueryId)
     */
    public boolean isQueryRegistered(QueryId queryId) {
        return this.fDataEngine.isQueryRegistered(queryId);
    }

    /**
     * @see com.ixora.rms.ui.dataviewboard.DataViewControlContext#createValidDataViewName(com.ixora.rms.ResourceId)
     */
    public String createValidDataViewName(ResourceId context) {
        String viewName = null;
        do {
            viewName = UIUtils.getStringInput(
                    fViewContainer.getAppFrame(),
                    MessageRepository.get(Msg.TEXT_INPUTTITLE_VIEW_NAME),
                    MessageRepository.get(Msg.TEXT_VIEW_NAME));
            if(Utils.isEmptyString(viewName)) {
                return null;
            }
            // check if a query with this name is already registered with
            // the data engine
            QueryId queryId = new QueryId(context, viewName);
            if(isQueryRegistered(queryId)) {
                UIUtils.showError(fViewContainer.getAppFrame(),
                        MessageRepository.get(Msg.TEXT_ERROR),
                        MessageRepository.get(Msg.TEXT_QUERY_IS_ALREADY_REGISTERED_RETRY,
                               new String[] {viewName}));
            } else {
                break;
            }
        } while(true);
        return viewName;
    }

	/**
	 * @param boardClass
	 * @param screen
	 * @return
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 * @throws ClassNotFoundException
	 * @throws IllegalArgumentException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private DataViewBoard createBoard(String boardClass, DataViewScreen screen) throws SecurityException, NoSuchMethodException, ClassNotFoundException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
	    Constructor<?> cons = Utils.getClassLoader(getClass()).loadClass(boardClass).getConstructor(
	            new Class[] {QueryRealizer.class, DataEngineService.class, ReactionLogService.class, SessionArtefactInfoLocator.class});
	    DataViewBoard board = (DataViewBoard)cons.newInstance(
	            new Object[] {fQueryRealizer, fDataEngine, fReactionLog, fPlottedInfoLocator});
		board.setListener(this.fEventHandler);
		board.setDataViewControlContext(this);
		board.setFrameIcon(fBoardIcons.get(boardClass));
		screen.addBoard(board);
		return board;
	}

	/**
	 * Closes all data view boards.
	 */
	public void close() {
		// close all screens
		for(DataViewScreen pane : fScreens.values()) {
			pane.close();
		}
	}

	/**
	 * Resets all data view boards.
	 */
	public void reset() {
		// reset all screens
		for(DataViewScreen pane : fScreens.values()) {
			pane.reset();
		}
	}

	/**
	 * Adds a new screen.
	 * @throws ScreenNameAlreadyExists
	 */
	public void addNewScreen() throws ScreenNameAlreadyExists {
        String screenName = UIUtils.getStringInput(fViewContainer.getAppFrame(),
                MessageRepository.get(
                        Msg.TITLE_NEW_SCREEN),
                MessageRepository.get(
                        Msg.TEXT_NEW_SCREEN)
                );
        if(Utils.isEmptyString(screenName)) {
        	return;
        }
        // check if name not already in the list
    	if(fScreens.get(screenName) != null) {
    		throw new ScreenNameAlreadyExists(screenName);
    	}
    	fScreens.put(screenName, new DataViewScreen());
    	setCurrentScreen(screenName);
    	fViewContainer.setSessionDirty(true);
	}

	/**
	 * Removes the current screen.
	 */
	public void removeScreen() {
		// ask for confirmation
		if(!UIUtils.getBooleanOkCancelInput(fViewContainer.getAppFrame(),
				MessageRepository.get(Msg.TITLE_CONFIRM_REMOVE_SCREEN),
				MessageRepository.get(Msg.TEXT_CONFIRM_REMOVE_SCREEN,
						new String[] {fCurrentScreen}))) {
			return;
		}
		DataViewScreen dp = this.fScreens.get(fCurrentScreen);
		dp.close();
		// see if any screens left
		List<String> lst = new ArrayList<String>(fScreens.keySet());
		int idx = lst.indexOf(fCurrentScreen);
		int size = lst.size();
		fScreens.remove(fCurrentScreen);
		int before = idx - 1;
		if(before >= 0) {
			// set current the prev screen
			fCurrentScreen = lst.get(before);
		} else {
			int after = idx + 1;
			if(after < size) {
				// set current the next screen
				fCurrentScreen = lst.get(after);
			} else {
				// create a new default screen
				fScreens.put(DEFAULT_SCREEN, new DataViewScreen());
				fCurrentScreen = DEFAULT_SCREEN;
			}
		}
		setCurrentScreen(fCurrentScreen);
		fViewContainer.setSessionDirty(true);
	}

	/**
	 * Renames the current screen.
	 * @throws ScreenNameAlreadyExists
	 */
	public void renameScreen() throws ScreenNameAlreadyExists {
        String screenName = UIUtils.getStringInput(fViewContainer.getAppFrame(),
                MessageRepository.get(
                        Msg.TITLE_RENAME_SCREEN,
						new String[] {fCurrentScreen}),
                MessageRepository.get(
                        Msg.TEXT_RENAME_SCREEN)
                );
        if(Utils.isEmptyString(screenName)) {
        	return;
        }
        // check if name not already in the list
    	if(fScreens.get(screenName) != null) {
    		throw new ScreenNameAlreadyExists(screenName);
    	}
        DataViewScreen pane = fScreens.get(fCurrentScreen);
        fScreens.remove(fCurrentScreen);
        fScreens.put(screenName, pane);
        setCurrentScreen(screenName);
        fViewContainer.setSessionDirty(true);
	}

	/**
	 * Sets the current screen.
	 * @param screenName
	 */
	public void setCurrentScreen(String screenName) {
		this.fCurrentScreen = screenName;
		setChanged();
		notifyObservers();
	}

	/**
	 * @return the current screen
	 */
	private DataViewScreen getCurrentScreen() {
		return fScreens.get(fCurrentScreen);
	}

	/**
	 * @see com.ixora.rms.ui.artefacts.dataview.DataViewSelectorPanel.Callback#plot(com.ixora.rms.repository.DataViewId)
	 */
	public void plot(DataViewId view) {
		try {
			plotDataView(view);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @param view
	 * @throws FailedToPlotView
	 */
	private void plotDataView(DataViewId view) throws FailedToPlotView {
//		ResourceId context = view.getContext();
		SessionDataViewInfo viewInfo = fPlottedInfoLocator.getDataViewInfo(view);
		if(viewInfo == null) {
			// this would happen if the view has been removed from repository
			// which might happen if a dashboard or a scheme references a stale data view
			// or if a dashboard references a data view which is not in the current monitoring session
			// model (agents with disabled providers are prone to this)
			// TODO localize this error
			FailedToPlotView ex = new FailedToPlotView("Could not find the definition of data view " + view);
			throw ex;
		}
		plot(view.getContext(), viewInfo.getDataView());
		fViewContainer.setSessionDirty(true);
	}

	/**
	 * All the views of a certain type will be plotted on one (existing or new) matching board;
	 * all the counters will be plotted as single counters views that will be time series charts
	 * for numeric counters and properties for string counters.
	 * @see com.ixora.rms.ui.artefacts.dashboard.DashboardSelectorPanel.Callback#plot(com.ixora.rms.repository.DashboardId)
	 */
	public void plot(DashboardId did) {
		try {
			ResourceId context = did.getContext();
			SessionDashboardInfo dInfo = fPlottedInfoLocator.getDashboardInfo(did);
			if(dInfo == null) {
				// this would happen if a scheme references a removed
				// dashboard
				// TODO: localize
				FailedToPlotView ex = new FailedToPlotView("Could not find the definition of dashboard " + did);
				throw ex;
			}
			// plot data views
			DataViewId[] views = dInfo.getDashboard().getViews();
			if(!Utils.isEmptyArray(views)) {
				for(DataViewId vid : views) {
					try {
						plotDataView(vid.complete(context));
					} catch(FailedToPlotView e) {
						// print warning and keep going...
						fViewContainer.setErrorMessage("Dashboard incomplete.", e);
					}
				}
			}
			// plot counters
			ResourceId[] counters = dInfo.getDashboard().getCounters();
			if(!Utils.isEmptyArray(counters)) {
				for(ResourceId cid : counters) {
				    cid = cid.complete(context);
					try {
					    plotCounter(cid, null, null, (Style)null);
					} catch(FailedToPlotView e) {
						// print warning and keep going...
						fViewContainer.setErrorMessage("Dashboard incomplete.", e);
					}
				}
			}
			fViewContainer.setSessionDirty(true);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

    /**
     * Shows the prev screen if any.
     */
    public void showPrevScreen() {
        ArrayList<String> lst = new ArrayList<String>(this.fScreens.keySet());
        int idx = lst.indexOf(fCurrentScreen);
        if(idx > 0) {
            setCurrentScreen(lst.get((idx - 1) % lst.size()));
        } else if(idx == 0) {
            setCurrentScreen(lst.get(lst.size() - 1));
        }
    }

    /**
     * Shows the next screen if any.
     */
    public void showNextScreen() {
        ArrayList<String> lst = new ArrayList<String>(this.fScreens.keySet());
        int idx = lst.indexOf(fCurrentScreen);
        if(idx >= 0) {
            setCurrentScreen(lst.get((idx + 1) % lst.size()));
        }
    }
}
