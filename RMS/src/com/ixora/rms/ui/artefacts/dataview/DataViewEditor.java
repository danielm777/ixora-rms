/*
 * Created on 03-Jul-2004
 */
package com.ixora.rms.ui.artefacts.dataview;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.StyledDocument;

import com.ixora.rms.ResourceId;
import com.ixora.common.MessageRepository;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.editor.EmbeddedEditorFrame;
import com.ixora.common.ui.editor.SyntaxHighlightXML;
import com.ixora.common.ui.exception.FailedToSaveDocument;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLException;
import com.ixora.rms.client.model.SessionModel;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.repository.AuthoredArtefactHelper;
import com.ixora.rms.repository.DataView;
import com.ixora.rms.repository.DataViewId;
import com.ixora.rms.repository.DataViewMap;
import com.ixora.rms.repository.exception.ArtefactSaveConflict;
import com.ixora.rms.repository.exception.FailedToSaveRepository;
import com.ixora.rms.services.DataViewRepositoryService;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.SessionTreeExplorer;
import com.ixora.rms.ui.artefacts.dataview.exception.SaveConflict;
import com.ixora.rms.ui.artefacts.dataview.messages.Msg;
import com.ixora.rms.ui.dataviewboard.charts.definitions.ChartDef;

/**
 * Editor for data views.
 * @author Daniel Moraru
 */
public final class DataViewEditor {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(DataViewEditor.class);

	/**
	 * Save listener.
	 */
	public interface Listener {
	    /**
	     * Saved data view.
	     * @param context
	     * @param dv
	     */
	    void savedDataView(ResourceId context, DataView dv);
	}

    /** Cached icon for the query editor dialog */
//	private static final ImageIcon iconDataViewEditor =
//			UIConfiguration.getIcon("dataview_editor.gif");
	/** Reference to the repository service */
	private DataViewRepositoryService fDataViewRepository;
	/** Window owing the editor */
	private RMSViewContainer fViewContainer;
	/** Tree explorer */
	private SessionTreeExplorer fTreeExplorer;
	/** Event handler */
	private EventHandler fEventHandler;
	/** Current context */
	private ResourceId fContext;
    /** Agent version for the current context */
    private String fSUOVersion;
	/** Save listener */
	private Listener fListener;
	/** The name of the current in edit data view */
	private String fInEditDataViewName;
	/** The name of the class for the in edit data view */
	private String fInEditDataViewClass;
    /** Original data view */
    private DataView fOriginalDataView;
	/** Session model */
	private SessionModel fSessionModel;
    /** Editor */
    private DataViewContentEditor fContentEditor;

	/**
	 * Event handler.
	 */
	private final class EventHandler
		implements EmbeddedEditorFrame.SaveCallback {
		/**
		 * @see com.ixora.common.ui.editor.EmbeddedEditorFrame.SaveCallback#save(javax.swing.JFrame, javax.swing.text.StyledDocument)
		 */
		public boolean save(JFrame dlg, StyledDocument doc) throws FailedToSaveDocument {
			return handleSaveDataView(dlg, doc);
		}
		/**
		 * @see com.ixora.common.ui.editor.EmbeddedEditorFrame.SaveCallback#saveAs(javax.swing.JFrame, javax.swing.text.StyledDocument)
		 */
		public boolean saveAs(JFrame dlg, StyledDocument doc) throws FailedToSaveDocument {
			return handleSaveDataViewAs(dlg, doc);
		}
	}

	/**
	 * Constructor.
	 * @param vc
	 * @param te
	 * @param dvrs
	 * @param scb
	 */
	public DataViewEditor(
			RMSViewContainer vc,
			SessionTreeExplorer te,
			SessionModel sm,
			DataViewRepositoryService dvrs,
			Listener scb) {
		super();
		this.fViewContainer = vc;
		this.fTreeExplorer = te;
		this.fSessionModel = sm;
		this.fDataViewRepository = dvrs;
		this.fListener = scb;
		this.fEventHandler = new EventHandler();
	}

	/**
	 * Edits the given data view.
	 * @param context the entity to which this data view belongs, if
	 * <code>context</code> is null the query is global, if not null
	 * the data view belongs to the last non null entity (host, agent or entity).
	 * @param view the data view to edit if <code>query</code> is null a new
	 * data view will be created
	 * @param saveEnabled whether or not save will be enabled initially in the editor
	 * @throws XMLException
	 * @throws BadLocationException
	 * @throws IOException
	 */
	public void edit(
			ResourceId context,
			DataView dv, boolean saveEnabled) throws XMLException, BadLocationException, IOException {
		// set the current context
		this.fContext = context;
        this.fSUOVersion = fSessionModel.getAgentVersionInContext(context);
        fOriginalDataView = dv;
		if(dv == null) {
		    // the default view is chart based
		    dv = new ChartDef();
		}

		// get query as XML
		String tmpTag = "rms";
		String text = XMLUtils.toXMLBuffer(null, dv, tmpTag, false).toString();
		text = hideText(text);
		fInEditDataViewClass = dv.getClass().getName();
		DefaultStyledDocument sd = new DefaultStyledDocument();
		sd.insertString(0,
				text,
				null); // no style
        Set<String> allSUOVersions = fSessionModel.getAllAgentVersionsInContext(context);
		fContentEditor = new DataViewContentEditor(
				fViewContainer,
                fTreeExplorer, fSessionModel, context,
                sd, allSUOVersions, dv.getAgentVersions(),
                this.fEventHandler,
				new SyntaxHighlightXML(),
				saveEnabled);
        fContentEditor.setSize(750, 450);
		updateTitle(fContentEditor, context, dv);
		UIUtils.centerFrameAndShow(fViewContainer.getAppFrame(), fContentEditor);
	}

	/**
	 * @param buff
	 * @throws IOException
	 */
	private String hideText(String text) throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(text));
		StringBuffer out = new StringBuffer();
		String line;
		while((line = reader.readLine()) != null) {
            if(line.indexOf("<agentVersion") >= 0 || line.indexOf("</agentVersion") >= 0) {
                continue;
            }
            // hide class name
			if(line.indexOf("<view") >= 0) {
				out.append("  <view>");
				out.append(Utils.getNewLine());
			}
			// hide name
			else if(line.indexOf("<name") < 0) {
				out.append(line);
				out.append(Utils.getNewLine());
			} else {
				// remember the name
				// <name/>
				if(line.indexOf("<name/>") >= 0) {
					fInEditDataViewName = null;
				} else {
					int idxs = line.indexOf("<name>");
					int idxe = line.indexOf("</name>");
					if(idxs < 0 || idxe < 0) {
						fInEditDataViewName = null;
					} else {
						fInEditDataViewName = line.substring(idxs + "<name>".length(), idxe);
					}
				}
			}
		}
		return out.toString();
	}

	/**
	 * @param buff
	 * @param name
	 * @throws IOException
	 */
	private String reinsertHiddenText(String text, String name) throws IOException {
		BufferedReader reader = new BufferedReader(new StringReader(text));
		StringBuffer out = new StringBuffer();
		String line;
		while((line = reader.readLine()) != null) {
			if(line.indexOf("<view") >= 0) {
				out.append("<view class=\"" + fInEditDataViewClass + "\">");
			} else if(line.indexOf("</view") >= 0) {
				out.append("<name>" + name + "</name>");
				out.append(line);
			}
			// ignore any name tag added in the editor
			else if(line.indexOf("<name") < 0) {
				out.append(line);
			}
		}
		return out.toString();
	}

	/**
	 * Updates the title of the given dialog.
	 * @param dlg
	 * @param context
	 * @param dv
	 */
	private static void updateTitle(JFrame dlg, ResourceId context, DataView dv) {
		dlg.setTitle(
				MessageRepository.get(Msg.TITLE_DATAVIEW_EDITOR,
					new String[] {new DataViewId(context, dv == null ? "-" : dv.getName()).toString()}));
	}

	/**
	 * Saves data view as...
	 * @param dlg
	 * @param doc
	 */
	private boolean handleSaveDataViewAs(JFrame dlg, StyledDocument doc) throws FailedToSaveDocument {
		// try and save document
	    DataView dv = null;
	    boolean bSaved = false;
	    try {
			String text = doc.getText(0, doc.getLength());
			// ask for a new name
			String newName = UIUtils.getStringInput(dlg,
		    		MessageRepository.get(Msg.TITLE_SAVEAS_DATAVIEW),
		            MessageRepository.get(Msg.TEXT_SAVEAS_DATAVIEW));
			if(newName == null) {
				// cancel
				return false;
			}
			newName = newName.trim();
			if(Utils.isEmptyString(newName)) {
				throw new FailedToSaveDocument(MessageRepository.get(Msg.TEXT_INVALID_DATAVIEW_NAME));
			}
			text = reinsertHiddenText(text, newName);
			dv = (DataView)XMLUtils.fromXMLBuffer(null, new StringBuffer(text), "view");
            dv.setAgentVersions(fContentEditor.getAgentVersionsForDataView());

            // This will throw an exception if dataview is invalid
            dv.testDataView(fContext);

            // check whether this will overwrite an existing view
			DataViewMap dvm = fDataViewRepository.getDataViewMap(fContext);
			if(dvm != null) {
                // check for overwrite only if the view applies for the current agent version
                // i.e. is global or applies to current version
                Set<String> versions = dv.getAgentVersions();
                if(versions == null || versions.contains(fSUOVersion)) {
    				if(dvm.getForAgentVersion(dv.getName(), fSUOVersion) != null) {
    				    if(!UIUtils.getBooleanOkCancelInput(dlg,
    				    		MessageRepository.get(Msg.TITLE_CONFIRM_OVERWRITE_DATAVIEW),
    				            MessageRepository.get(Msg.TEXT_OVERWRITE_DATAVIEW,
    				                    new String[]{dv.getName()}))) {
    				        return false;
    				    }
    				}
                }
			}

            // now save repository
            saveDataView(fContext, dv, false);
            updateTitle(dlg, fContext, dv);
            // save succesfull, update in edit name
            fInEditDataViewName = dv.getName();
            fOriginalDataView = dv;
            bSaved = true;

		    this.fListener.savedDataView(fContext, dv);
		}catch(FailedToSaveDocument e) {
			throw e;
        }catch(RMSException e) {
            logger.error(e);
            throw new FailedToSaveDocument(
                  Msg.ERROR_FAILED_TO_SAVE_DATAVIEW,
                    new String[] {e.getLocalizedMessage()});
		}catch(Exception e) {
		    logger.error(e);
			throw new FailedToSaveDocument(
			      Msg.ERROR_FAILED_TO_SAVE_DATAVIEW,
					new String[] {e.toString()});
		}

		return bSaved;
	}

	/**
	 * Saves query.
	 * @param dlg
	 * @param doc
	 */
	private boolean handleSaveDataView(JFrame dlg, StyledDocument doc) throws FailedToSaveDocument {
		// try and save the document first
	    boolean bSaved = false;
	    try {
			String text = doc.getText(0, doc.getLength());
			if(fInEditDataViewName == null) {
				// save an untitled data view
				String newName = UIUtils.getStringInput(dlg,
			    		MessageRepository.get(Msg.TITLE_SAVEAS_DATAVIEW),
			            MessageRepository.get(Msg.TEXT_SAVEAS_DATAVIEW));
				if(newName == null) {
					// cancel
					return false;
				}
				newName = newName.trim();
				if(Utils.isEmptyString(newName)) {
					throw new FailedToSaveDocument(MessageRepository.get(Msg.TEXT_INVALID_DATAVIEW_NAME));
				}
				fInEditDataViewName = newName;
			}
			text = reinsertHiddenText(text, fInEditDataViewName);
			DataView dv = (DataView)XMLUtils.fromXMLBuffer(null, new StringBuffer(text), "view");
			dv.setAgentVersions(fContentEditor.getAgentVersionsForDataView());

			// This will throw an exception if dataview is invalid
			dv.testDataView(fContext);

			saveDataView(fContext, dv, true);
			updateTitle(dlg, fContext, dv);
			// update in edit name
			fInEditDataViewName = dv.getName();
            fOriginalDataView = dv;
			bSaved = true;
	    }catch(FailedToSaveDocument e) {
	    	throw e;
	    }catch(RMSException e) {
		    logger.error(e);
			throw new FailedToSaveDocument(
			      Msg.ERROR_FAILED_TO_SAVE_DATAVIEW,
					new String[] {e.getLocalizedMessage()});
	    }catch(Exception e) {
		    logger.error(e);
			throw new FailedToSaveDocument(
			      Msg.ERROR_FAILED_TO_SAVE_DATAVIEW,
					new String[] {e.toString()});
		}
	    return bSaved;
	}

	/**
	 * @param listener
	 * @param context
	 * @param dataViewRepository
	 * @param dv
	 * @param dvOriginal
	 * @throws SaveConflict
	 * @throws FailedToSaveRepository
	 */
	public static final void saveDataView(
			Listener listener,
			ResourceId context,
			DataViewRepositoryService dataViewRepository,
			DataView dv,
			DataView dvOriginal) throws SaveConflict, FailedToSaveRepository {
		// check author info
		AuthoredArtefactHelper.checkArtefact(dv);
		DataViewMap map = dataViewRepository.getDataViewMap(context);
        if(map == null) {
            map = new DataViewMap();
        }
        try {
            map.addOrUpdateWithConflictDetection(dvOriginal, dv);
        } catch(ArtefactSaveConflict e) {
            throw new SaveConflict(dv.getArtefactName());
        }
        dataViewRepository.setDataViewMap(context, map);
        dataViewRepository.save();
        listener.savedDataView(context, dv);
	}

    /**
     * @param context
     * @param dv
     * @param useOriginal
     * @throws SaveConflict
     * @throws FailedToSaveRepository
     */
    private void saveDataView(ResourceId context, DataView dv, boolean useOriginal) throws SaveConflict, FailedToSaveRepository {
    	saveDataView(fListener, context, fDataViewRepository, dv, useOriginal ? fOriginalDataView : null);
    }
}
