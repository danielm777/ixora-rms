/**
 * 02-Feb-2006
 */
package com.ixora.common.ui.history;

import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.ixora.common.history.HistoryGroupItem;
import com.ixora.common.history.HistoryMgr;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.utils.Utils;
import com.ixora.common.xml.XMLExternalizable;

/**
 * @author Daniel Moraru
 */
public class HistoryPanel extends JPanel {
	private static final long serialVersionUID = -6023072654684822172L;

	/**
	 * Listener.
	 */
	public interface Listener {
		/**
		 * Invoked when an object was selected from the history.
		 * @param obj
		 */
		void selected(XMLExternalizable obj);
	}

	@SuppressWarnings("serial")
	private class ActionNext extends AbstractAction {
		ActionNext() {
			super();
			ImageIcon icon = UIConfiguration.getIcon("next.gif");
			if(icon != null) {
				putValue(SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handleNext();
		}
	}

	@SuppressWarnings("serial")
	private class ActionPrev extends AbstractAction {
		ActionPrev() {
			super();
			ImageIcon icon = UIConfiguration.getIcon("prev.gif");
			if(icon != null) {
				putValue(SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent e) {
			handlePrev();
		}
	}

	private Listener fListener;
	private Action fActionNext;
	private Action fActionPrev;
	private JLabel fLabelDate;
	private List<HistoryGroupItem> fItems;
	private int fCursor;

	/**
	 *
	 */
	public HistoryPanel(Listener listener) {
		super();
		if(listener == null) {
			throw new IllegalArgumentException("No listener");
		}
		fListener = listener;
		fActionNext = new ActionNext();
		fActionPrev = new ActionPrev();
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(fLabelDate = UIFactoryMgr.createLabel(""));
		add(Box.createHorizontalStrut(5));
		add(UIFactoryMgr.createLabel("History"));
		add(Box.createHorizontalStrut(5));
		JButton button = UIFactoryMgr.createButton(fActionPrev);
		button.setMargin(new Insets(0,0,0,0));
		add(button);
		button = UIFactoryMgr.createButton(fActionNext);
		button.setMargin(new Insets(0,0,0,0));
		add(button);

		fItems = new LinkedList<HistoryGroupItem>();
		fActionNext.setEnabled(false);
		fActionPrev.setEnabled(false);
	}

	/**
	 * @param id
	 */
	public void setHistoryGroup(String id) {
		fItems = HistoryMgr.get(id);
		fCursor = -1;
		fLabelDate.setText("");
		fActionPrev.setEnabled(false);
		if(Utils.isEmptyCollection(fItems)) {
			fActionNext.setEnabled(false);
		} else {
			fActionNext.setEnabled(true);
		}

		if(fItems == null) {
			fItems = new LinkedList<HistoryGroupItem>();
		}
	}

	/**
	 *
	 */
	private void handleNext() {
		try {
			if(fItems.size() == 0) {
				return;
			}
			fCursor = (fCursor + 1) % fItems.size();
			HistoryGroupItem item = fItems.get(fCursor);
			fListener.selected(item.getItem());
			if(fCursor == fItems.size() - 1) {
				fActionNext.setEnabled(false);
			} else {
				fActionNext.setEnabled(true);
			}
			if(fCursor == 0) {
				fActionPrev.setEnabled(false);
			} else {
				fActionPrev.setEnabled(true);
			}
			fLabelDate.setText(getTextForDate(item.getDate()));
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 *
	 */
	private void handlePrev() {
		try {
			if(fItems.size() == 0) {
				return;
			}
			fCursor = (fCursor - 1) % fItems.size();
			if(fCursor < 0) {
				fCursor = 0;
			}
			HistoryGroupItem item = fItems.get(fCursor);
			fListener.selected(item.getItem());
			if(fCursor == 0) {
				fActionPrev.setEnabled(false);
			} else {
				fActionPrev.setEnabled(true);
			}
			fActionNext.setEnabled(true);
			fLabelDate.setText(getTextForDate(item.getDate()));
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @param time
	 * @return
	 */
	private String getTextForDate(long time) {
		return "(" + new Date(time).toString() + ")";
	}
}
