/**
 * 04-Feb-2006
 */
package com.ixora.rms.ui.exporter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.SwingUtilities;

import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.history.HistoryMgr;
import com.ixora.rms.ui.RMSViewContainer;
import com.ixora.rms.ui.dataviewboard.handler.DataViewBoardHandler;

/**
 * @author Daniel Moraru
 */
public class HTMLGenerator {
	private static final String FILE_NAME = "index.htm";
	private static final String CSS_FILE_NAME = "style.css";
	private DataViewBoardHandler fDataViewBoardHandler;
	private boolean fGenerating;
	private Timer fTimer;
	private RMSViewContainer fViewContainer;
	private Listener fListener;
	private File fStyleFile;

	public interface Listener {
		/**
		 * Invoked when the HTML generation started.
		 */
		void startedHTMLGeneration();
		/**
		 * Invoked when HTML generation has finished.
		 * @param e
		 */
		void finishedHTMLGeneration(Exception e);
		/**
		 * Invoked when the HTML generation was cancelled.
		 */
		void cancelledHTMLGeneration();
	}

	/**
	 * @param vc
	 * @param dvh
	 * @param listener
	 */
	public HTMLGenerator(RMSViewContainer vc, DataViewBoardHandler dvh, Listener listener) {
		super();
		fViewContainer = vc;
		fDataViewBoardHandler = dvh;
		fListener = listener;
		fStyleFile = new File(Utils.getPath("config/htmlgen/" + CSS_FILE_NAME));
	}

	/**
	 * @return
	 */
	public boolean isStarted() {
		return fGenerating;
	}

	/**
	 *
	 */
	public void stop() {
		fGenerating = false;
		if(fTimer != null) {
			fTimer.cancel();
			fListener.finishedHTMLGeneration(null);
		}
	}

	/**
	 *
	 */
	public void start() throws IOException {
		HTMLGeneratorSettings settings = (HTMLGeneratorSettings)HistoryMgr
			.getMostRecent("htmlgen");
		HTMLGeneratorDialog dlg = new HTMLGeneratorDialog(fViewContainer.getAppFrame(),
				settings);
		UIUtils.centerDialogAndShow(fViewContainer.getAppFrame(), dlg);
		final HTMLGeneratorSettings fsettings = dlg.getResult();
		if(fsettings != null) {
			HistoryMgr.add("htmlgen", fsettings);
			boolean repeat = (fsettings.getRefreshInterval() > 0);

			fGenerating = true;
			fListener.startedHTMLGeneration();

			fTimer = new Timer(true);
			if(repeat) {
				fTimer.schedule(new TimerTask(){
					public void run() {
						handleTimerAction(fsettings, true);
					}
				}, 0, 1000 * fsettings.getRefreshInterval());
			} else {
				fTimer.schedule(new TimerTask(){
					public void run() {
						handleTimerAction(fsettings, false);
					}
				}, new Date());
			}
		} else {
			fListener.cancelledHTMLGeneration();
		}
	}

	/**
	 * @param root
	 * @throws IOException
	 */
	private void deleteGeneratedFiles(File root) throws IOException {
		File[] files = root.listFiles(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return name.endsWith(".png")
				|| name.endsWith(".htm")
				|| name.endsWith(".css");
			}
		});
		if(!Utils.isEmptyArray(files)) {
			for(File f : files) {
				f.delete();
			}
		}
		// add back the stylesheet file
		Utils.copyFile(fStyleFile, new File(root, CSS_FILE_NAME));
	}

	/**
	 * @param settings
	 * @param repeat
	 */
	private void handleTimerAction(final HTMLGeneratorSettings settings, boolean repeat) {
		Exception error = null;
		try {
			deleteGeneratedFiles(settings.getSiteRoot());

			final StringBuilder buff = new StringBuilder(200);

			final Exception[] errorArr = new Exception[1];
			Runnable runnable = new Runnable(){
				public void run() {
					try {
						buff.append("<html><header><link href='")
							.append(CSS_FILE_NAME).append("'")
							.append("rel='stylesheet' type='text/css'/></header><body>");
						fDataViewBoardHandler.toHTML(buff, settings.getSiteRoot());
						buff.append("</body></html>");
					} catch(Exception e) {
						errorArr[0] = e;
					}
				}
			};
			// invoke this on the event dispatching thread and wait
			SwingUtilities.invokeAndWait(runnable);
			error = errorArr[0];
			if(error == null) {
				File out = new File(settings.getSiteRoot(), FILE_NAME);
				BufferedWriter os = null;
				try {
					os = new BufferedWriter(new FileWriter(out));
					os.write(buff.toString());
				} catch(Exception e) {
					throw e;
				} finally {
					if(os != null) {
						try {
							os.close();
						} catch(Exception e) {
						}
					}
				}
			}
		} catch(Exception e) {
			error = e;
		} finally {
			if(error != null) {
				UIExceptionMgr.exception(error);
				fTimer.cancel();
			}
			if(!repeat || error != null) {
				fGenerating = false;
				fListener.finishedHTMLGeneration(error);
			}
		}
	}
}
