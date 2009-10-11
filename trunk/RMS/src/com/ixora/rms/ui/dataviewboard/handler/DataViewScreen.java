/*
 * Created on 07-Jan-2004
 */
package com.ixora.rms.ui.dataviewboard.handler;

import java.awt.Component;
import java.awt.Dimension;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import com.ixora.common.MessageRepository;
import com.ixora.common.utils.Utils;
import com.ixora.rms.ui.dataviewboard.DataViewBoard;
import com.ixora.rms.ui.dataviewboard.DataViewBoardDescriptor;
import com.ixora.rms.ui.exporter.HTMLProvider;
import com.ixora.rms.ui.messages.Msg;

/**
 * ChartsDesktopPane.
 * @author Daniel Moraru
 */
public final class DataViewScreen extends JDesktopPane implements HTMLProvider {
	private static final long serialVersionUID = 6205757471079968839L;
	/** Offset of the newly added frames relative to the last added frame */
	private static final int OFFSET = 40;
	private EventHandler fEventHandler;

	/**
	 * Event handler.
	 * @author Daniel Moraru
	 */
	private final class EventHandler extends InternalFrameAdapter {
	    /**
	     * @see javax.swing.event.InternalFrameListener#internalFrameActivated(javax.swing.event.InternalFrameEvent)
	     */
		public void internalFrameActivated(InternalFrameEvent e) {
			// need this as to go around an issue with internal frames where
			// the focus is not obtained when it becomes active, might have been fixed
			// in 1.5
	        JInternalFrame fr = (JInternalFrame)e.getSource();
	        int count = fr.getContentPane().getComponentCount();
	        if(count == 0) {
	        	return;
	        }
	        Component c = fr.getContentPane().getComponent(0);
	        if(c != null) {
	          c.requestFocus();
	        }
	      }
	}

	/**
	 * Constructor.
	 */
	public DataViewScreen() {
		super();
		this.fEventHandler = new EventHandler();
	}

	/**
	 * Adds an internal frame.
	 * @param frame
	 */
	public void addBoard(DataViewBoard frame) {
		super.add(frame, 1);
		frame.addInternalFrameListener(this.fEventHandler);
		int frames = getComponentCount();
		frame.setLocation(frames * OFFSET, frames * OFFSET);
		frame.setVisible(true);
		setSelectedFrame(frame);
		frame.setTitle(
			MessageRepository.get(Msg.TITLE_VIEWBOARD) + " " + frames);
		frames++;
	}

	/**
	 * @return the selected data view board.
	 */
	public DataViewBoard getSelectedBoard() {
		DataViewBoard dvb = (DataViewBoard)getSelectedFrame();
		// TODO JRE BUG
		if(dvb != null && !dvb.isVisible()) {
			return null;
		}
		return dvb;
	}

	/**
	 * @return all data view boards.
	 */
	public Collection<DataViewBoard> getBoards() {
		List<DataViewBoard> ret = new LinkedList<DataViewBoard>();
		JInternalFrame[] frames = getAllFrames();
		for(JInternalFrame frame : frames) {
			if(frame instanceof DataViewBoard) {
				ret.add((DataViewBoard)frame);
			}
		}
		return ret;
	}

    /**
     * Tiles the boards.
     */
    public void tileBoards() {
        Dimension size = this.getSize();
        JInternalFrame[] frames = getAllFrames();
        int total = frames.length;
        if(total == 0) {
            return;
        }
        Dimension dim = calculateOptimumSize(size.width, size.height, total);
        int width = size.width / dim.width;
        int height = size.height / dim.height;
        int locationx = 0;
        int locationy = 0;
        JInternalFrame f;
        for(int i = 0; i < total; i++) {
            f = frames[i];
            f.setLocation(locationx, locationy);
            f.setSize(width, height);
            getDesktopManager().deiconifyFrame(f);
            locationx += width;
            if((i + 1) % dim.width == 0) {
                locationx = 0;
                locationy += height;
            }
        }
    }

    /**
     * Calculates the optimum number of rectangular panels on horizontal and
     * vertical that uses the most space on a canvas with the dimensions of h and v
     * while keeping each panel as square as possible.
     * @param h the width of the canvas
     * @param v the height of the canvas
     * @param n the total number of panels
     * @return x is the number of panels on horizontal, y the same on vertical
     */
    private Dimension calculateOptimumSize(int h, int v, int n) {
        double sqr = Math.sqrt(n);
        int isqr = (int)sqr;
        if(sqr - isqr == 0) {
            return new Dimension(isqr, isqr);
        }
        if(n % 2 == 1) {
            n++;
        }
        isqr = (int)Math.sqrt(n);
        int f1;
        while(true) {
            if(n % isqr == 0) {
                f1 = isqr;
                break;
            }
            --isqr;
        }
        int f2 = n/f1;
        if(h >= v) {
            return new Dimension(Math.max(f1, f2), Math.min(f1, f2));
        } else {
            return new Dimension(Math.min(f1, f2), Math.max(f1, f2));
        }
    }

    /**
     * @return descriptors for all the boards
     */
    public Collection<DataViewBoardDescriptor> getBoardDescriptors() {
        Collection<DataViewBoard> boards = getBoards();
        List<DataViewBoardDescriptor> ret = new ArrayList<DataViewBoardDescriptor>(boards.size());
        for(Iterator<DataViewBoard> iter = boards.iterator(); iter.hasNext();) {
            ret.add((iter.next()).getDescriptor());
        }
        return ret;
    }

	/**
	 * Closes all data view boards.
	 */
	public void close() {
		Collection<DataViewBoard> cols = getBoards();
		if(cols != null) {
			for(DataViewBoard board : cols) {
				board.dispose();
			}
		}
	}

	/**
	 * Resets all data view boards.
	 */
	public void reset() {
		Collection<DataViewBoard> cols = getBoards();
		if(cols != null) {
			for(DataViewBoard board : cols) {
				board.reset();
			}
		}
	}

	/**
	 * @throws IOException
	 * @see com.ixora.rms.ui.exporter.HTMLProvider#toHTML(java.lang.StringBuilder, java.io.File)
	 */
	public void toHTML(StringBuilder buff, File root) throws IOException {
		Collection<DataViewBoard> boards = getBoards();
		if(!Utils.isEmptyCollection(boards)) {
			for(DataViewBoard board : boards) {
				board.toHTML(buff, root);
			}
		}
	}
}
