/*
 * Created on 11-Jan-2004
 */
package com.ixora.rms.ui.session;

import java.awt.Cursor;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileView;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ixora.common.ComponentConfiguration;
import com.ixora.common.ConfigurationMgr;
import com.ixora.common.MessageRepository;
import com.ixora.common.exception.FailedToSaveConfiguration;
import com.ixora.common.exception.ReadOnlyConfiguration;
import com.ixora.common.logging.AppLogger;
import com.ixora.common.logging.AppLoggerFactory;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.jobs.UIWorkerJobDefault;
import com.ixora.common.utils.MRUList;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLUtils;
import com.ixora.common.xml.exception.XMLNodeMissing;
import com.ixora.rms.client.session.MonitoringSessionDescriptor;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.messages.Msg;
import com.ixora.rms.ui.session.exception.FailedToLoadSession;
import com.ixora.rms.ui.session.exception.FailedToSaveSession;

/**
 * @author Daniel Moraru
 */
public final class MonitoringSessionRepositoryImpl
	implements MonitoringSessionRepository {
	/** Logger */
	private static final AppLogger logger = AppLoggerFactory.getLogger(MonitoringSessionRepositoryImpl.class);

	/** Icon for the session files */
	private ImageIcon icon;
	/** ViewContainer */
	private RMSViewContainer vc;
	/** Listener */
	private Listener listener;
	/** Repository folder */
	private String repository;

	/**
	 * File chooser.
	 */
	private static final class SessionFileFilter extends FileFilter {
		/**
		 * @see javax.swing.filechooser.FileFilter#accept(java.io.File)
		 */
		public boolean accept(File f) {
			if(f.isDirectory()) {
				return true;
			}
			String ext = Utils.getFileExtension(f);
			if(ext != null && ext.equalsIgnoreCase(
			        ConfigurationMgr.getString(MonitoringSessionRepositoryComponent.NAME,
			        	MonitoringSessionRepositoryConfigurationConstants.SESSION_FILE_EXTENSION))) {
				return true;
			}
			return false;
		}
		/**
		 * @see javax.swing.filechooser.FileFilter#getDescription()
		 */
		public String getDescription() {
			return MessageRepository.get(Msg.TEXT_SESSION_FILTER_DESCRIPTION);
		}
	}

	/**
	 * File view.
	 */
	private final class SessionFileView extends FileView {
		/**
		 * @see javax.swing.filechooser.FileView#getDescription(java.io.File)
		 */
		public String getDescription(File f) {
			return MessageRepository.get(Msg.TEXT_SESSION_FILTER_DESCRIPTION);
		}
		/**
		 * @see javax.swing.filechooser.FileView#getIcon(java.io.File)
		 */
		public Icon getIcon(File f) {
			String ext = Utils.getFileExtension(f);
			if(ext != null && ext.equalsIgnoreCase(
			        ConfigurationMgr.getString(MonitoringSessionRepositoryComponent.NAME,
				        	MonitoringSessionRepositoryConfigurationConstants.SESSION_FILE_EXTENSION))) {
				return icon;
			}
			return null;
		}
	}

	/**
	 * Constructor.
	 * @param vc
	 * @param schemeFileIcon
	 */
	public MonitoringSessionRepositoryImpl(RMSViewContainer vc, ImageIcon schemeFileIcon, Listener listener) {
		super();
		this.vc = vc;
		this.icon = schemeFileIcon;
		this.listener = listener;
		this.repository = Utils.getPath("config/session/repository");
	}

	/**
	 * @see com.ixora.rms.ui.session.MonitoringSessionRepository#saveSession(com.ixora.rms.ui.session.MonitoringSessionDescriptor, boolean, boolean)
	 */
	public void saveSession(
					MonitoringSessionDescriptor session, boolean asynch, boolean saveAs)
						throws FailedToSaveSession {
	    String name = null;
	    	try {
				File file;
				if(saveAs) {
					session.setName(null);
					session.setLocation(null);
				}
				name = session.getName();
				if(name == null) {
					// unsaved scheme so get a file to save it to
					JFileChooser fc = new JFileChooser(repository);
					fc.setAcceptAllFileFilterUsed(false);
					fc.setFileFilter(new SessionFileFilter());
					fc.setFileView(new SessionFileView());
					int returnVal = fc.showSaveDialog(vc.getAppFrame());

					if(returnVal == JFileChooser.APPROVE_OPTION) {
						file = fc.getSelectedFile();
					} else {
						return;
					}

					name = Utils.getFileName(file);
				} else {
					// the scheme has been saved before
					// so save it in the same place
					file = new File(session.getLocation());
				}

				file = new File(
					session.getLocation() == null ? file.getParentFile() : file,
					name + "." + ConfigurationMgr.getString(MonitoringSessionRepositoryComponent.NAME,
				        	MonitoringSessionRepositoryConfigurationConstants.SESSION_FILE_EXTENSION));

				if(!file.exists()) {
					file.createNewFile();
				}
				session.setLocation(file.getParent());
				session.setName(Utils.getFileName(file));
				saveSession(vc, file, session, asynch, saveAs);
			} catch(Exception e) {
			    if(name != null) {
			        throw new FailedToSaveSession(name, e);
			    } else {
			        throw new FailedToSaveSession(e);
			    }
			}
	}
	/**
	 * @see com.ixora.rms.ui.session.MonitoringSessionRepository#loadSession(java.lang.String)
	 */
	public void loadSession(String session)
		throws FailedToLoadSession {
		loadSession(vc, new File(session));
	}

	/**
	 * @see com.ixora.rms.ui.session.MonitoringSessionRepository#loadSession()
	 */
	public void loadSession() throws FailedToLoadSession {
		JFileChooser fc = new JFileChooser(repository);
		fc.setAcceptAllFileFilterUsed(false);
		fc.setFileFilter(new SessionFileFilter());
		fc.setFileView(new SessionFileView());
		int returnVal = fc.showOpenDialog(vc.getAppFrame());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			loadSession(vc, fc.getSelectedFile());
		}
	}

	/**
	 * @see com.ixora.rms.ui.session.MonitoringSessionRepository#getMostRecentlyUsed()
	 */
	public String[] getMostRecentlyUsed() {
		List<String> ret = ConfigurationMgr.getList(
		      MonitoringSessionRepositoryComponent.NAME,
		      MonitoringSessionRepositoryConfigurationConstants.MOST_RECENTLY_USED);
		if(ret == null) {
			return null;
		}
		return ret.toArray(new String[ret.size()]);
	}

	/**
	 * Loads a session from file.
	 * @param parent
	 * @param session
	 * @throws FailedToLoadSession
	 */
	private void loadSession(
			final RMSViewContainer parent,
			final File session) throws FailedToLoadSession {
		parent.runJob(new UIWorkerJobDefault(
				parent.getAppFrame(),
				Cursor.WAIT_CURSOR,
				MessageRepository.get(
					Msg.TEXT_LOADING_SESSION,
					new String[]{Utils.getFileName(session)})) {

			public void work() throws Exception {
				Document doc = null;
				BufferedInputStream is = null;
				try {
					doc = XMLUtils.read(
					is = new BufferedInputStream(
						new FileInputStream(session)));
				} finally {
					if(is != null) {
						try {
							is.close();
						} catch(Exception e) {
						}
					}
				}
				Node n = XMLUtils.findChild(doc.getFirstChild(), "session");
				if(n == null) {
					throw new XMLNodeMissing("session");
				}
				MonitoringSessionDescriptor ret = new MonitoringSessionDescriptor();
				ret.fromXML(n);
				ret.setLocation(session.getParent());
				setLastUsedSession(session.getAbsolutePath());
				fResult = ret;
			}
            public void finished(Throwable ex) {
				if(ex != null) {
					logger.error(ex);
					UIExceptionMgr.userException(
						new FailedToLoadSession(
						session.getAbsolutePath(), ex));
				} else {
					listener.sessionLoaded((MonitoringSessionDescriptor)fResult);
				}
			}
		});
	}

	/**
	 * Saves the scheme to the given file.
	 * against the parent
	 * @param parent
	 * @param file
	 * @param session
	 * @param saveAs
	 */
	private void saveSession(
				final RMSViewContainer parent,
				final File file,
				final MonitoringSessionDescriptor session,
				boolean asynch, boolean saveAs) {
		parent.runJob(new UIWorkerJobDefault(
				parent.getAppFrame(),
				Cursor.WAIT_CURSOR,
				MessageRepository.get(
					Msg.TEXT_SAVING_SESSION)) {
			public void work() throws Exception {
				Document doc = XMLUtils.createEmptyDocument("rms");
				session.toXML(doc.getDocumentElement());
				BufferedOutputStream os = null;
				try {
					os = new BufferedOutputStream(
							new FileOutputStream(file));
					XMLUtils.write(doc, os);
					setLastUsedSession(file.getAbsolutePath());
				} finally {
					try {
						if(os != null) {
							os.close();
						}
					} catch(Exception e) {
					}
				}
			}
			public void finished(Throwable ex) {
				if(ex != null) {
				    logger.error(ex);
					UIExceptionMgr.userException(
					      new FailedToSaveSession(
					          session.getName(), ex));
					session.setName(null);
					session.setLocation(null);
				}
			}
		}, !asynch);
	}

	/**
	 * @param absolutePath
	 */
	private void setLastUsedSession(String absolutePath) {
	    ComponentConfiguration conf = ConfigurationMgr.get(
	            MonitoringSessionRepositoryComponent.NAME);
		List<String> lst = conf.getList(
		        MonitoringSessionRepositoryConfigurationConstants.MOST_RECENTLY_USED);
		if(lst == null) {
			lst = new LinkedList<String>();
		}
		List<String> mru = new MRUList<String>(
		        conf.getInt(MonitoringSessionRepositoryConfigurationConstants.MOST_RECENTLY_USED_SIZE),
		        lst);
		mru.add(absolutePath);
		conf.setList(
		        MonitoringSessionRepositoryConfigurationConstants.MOST_RECENTLY_USED,
		        mru);
		try {
		    conf.save();
		} catch(ReadOnlyConfiguration e) {
		} catch(FailedToSaveConfiguration e) {
			logger.error(e);
		}
    }
}
