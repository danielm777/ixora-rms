/*
 * Created on Feb 8, 2004
 */
package com.ixora.rms.ui.dataviewboard;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.border.Border;

import com.ixora.common.MessageRepository;
import com.ixora.common.ui.ToolBarComponent;
import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.rms.ui.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class DataViewScreenPanel
		extends JPanel implements ToolBarComponent, Observer {
	private static final long serialVersionUID = 7266361362211389837L;
	private JComboBox fComboBoxPanes;
	private JButton fButtonAddScreen;
	private JButton fButtonRemoveScreen;
	private JButton fButtonRenameScreen;
	private ActionAddScreen fActionAddScreen;
	private ActionRemoveScreen fActionRemoveScreen;
	private ActionRenameScreen fActionRenameScreen;
	private DataViewBoardHandler fBoardHandler;
	private boolean fUpdatingComboBox;

	/**
	 * Add screen action.
	 */
	private final class ActionAddScreen extends AbstractAction {
		private static final long serialVersionUID = -1436089220951695986L;
		public ActionAddScreen() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_ADD_DATA_VIEW_SCREEN), this);
			ImageIcon icon = UIConfiguration.getIcon("add_screen.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleAddScreen();
		}
	}

	/**
	 * Remove folder action.
	 */
	private final class ActionRemoveScreen extends AbstractAction {
		private static final long serialVersionUID = -6953498378491916281L;
		public ActionRemoveScreen() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_REMOVE_DATA_VIEW_SCREEN), this);
			ImageIcon icon = UIConfiguration.getIcon("remove_screen.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleRemoveScreen();
		}
	}

	/**
	 * Rename folder action.
	 */
	private final class ActionRenameScreen extends AbstractAction {
		private static final long serialVersionUID = 7914547788613352622L;
		public ActionRenameScreen() {
			super();
			UIUtils.setUsabilityDtls(MessageRepository.get(Msg.ACTIONS_RENAME_DATA_VIEW_SCREEN), this);
			ImageIcon icon = UIConfiguration.getIcon("rename_screen.gif");
			if(icon != null) {
				putValue(Action.SMALL_ICON, icon);
			}
		}
		/**
		 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
		 */
		public void actionPerformed(ActionEvent arg0) {
			handleRenameScreen();
		}
	}

	/**
	 * Constructor.
	 * @param bh
	 */
	public DataViewScreenPanel(DataViewBoardHandler bh) {
		super();
		fBoardHandler = bh;
		fBoardHandler.addObserver(this);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		//super.setBorder(BorderFactory.createEtchedBorder());
		fComboBoxPanes = UIFactoryMgr.createComboBox();
		setComboBoxData();
		// override size
		Dimension d = new Dimension(170, 20);
		fComboBoxPanes.setMinimumSize(d);
		fComboBoxPanes.setMaximumSize(d);
		fComboBoxPanes.setPreferredSize(d);
		add(fComboBoxPanes);

		fActionAddScreen = new ActionAddScreen();
		fButtonAddScreen = UIFactoryMgr.createButton(fActionAddScreen);
		fButtonAddScreen.setText(null);
		add(fButtonAddScreen);
		//add(Box.createHorizontalStrut(UIConfiguration.getPanelPadding()));

		fActionRemoveScreen = new ActionRemoveScreen();
		fButtonRemoveScreen = UIFactoryMgr.createButton(fActionRemoveScreen);
		fButtonRemoveScreen.setText(null);
		add(fButtonRemoveScreen);
		//add(Box.createHorizontalStrut(UIConfiguration.getPanelPadding()));

		fActionRenameScreen = new ActionRenameScreen();
		fButtonRenameScreen = UIFactoryMgr.createButton(fActionRenameScreen);
		fButtonRenameScreen.setText(null);
		add(fButtonRenameScreen);

		fComboBoxPanes.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				handleItemStateChanged();
			}
		});
	}

	/**
	 * Sets the data for the screens combo box.
	 */
	private void setComboBoxData() {
		fUpdatingComboBox = true;
		DefaultComboBoxModel model = (DefaultComboBoxModel)fComboBoxPanes.getModel();
		model.removeAllElements();
		String[] screens = fBoardHandler.getScreenNames();
		for(String s : screens) {
			model.addElement(s);
		}
		model.setSelectedItem(fBoardHandler.getScreenName());
		fUpdatingComboBox = false;
	}

	/**
	 * Sets the current screen.
	 */
	private void handleItemStateChanged() {
		try {
			if(fUpdatingComboBox) {
				return;
			}
			String screenName = (String)fComboBoxPanes.getSelectedItem();
			if(screenName != null) {
				fBoardHandler.setCurrentScreen(screenName);
			}
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * Renames the current screen.
	 */
	private void handleRenameScreen() {
		try {
			fBoardHandler.renameScreen();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
	/**
	 * Removes the current screen.
	 */
	private void handleRemoveScreen() {
		try {
			fBoardHandler.removeScreen();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
	/**
	 * Creates a new screen.
	 */
	private void handleAddScreen() {
		try {
			fBoardHandler.addNewScreen();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @see com.ixora.common.ui.ToolBarComponent#setMargin(java.awt.Insets)
	 */
	public void setMargin(Insets insets) {
		fButtonAddScreen.setMargin(insets);
		fButtonRemoveScreen.setMargin(insets);
		fButtonRenameScreen.setMargin(insets);
	}

	/**
	 * @see com.ixora.common.ui.ToolBarComponent#setRolloverEnabled(boolean)
	 */
	public void setRolloverEnabled(boolean rollover) {
		fButtonAddScreen.setRolloverEnabled(rollover);
		fButtonRemoveScreen.setRolloverEnabled(rollover);
		fButtonRenameScreen.setRolloverEnabled(rollover);
	}

	/**
	 * @see com.ixora.common.ui.ToolBarComponent#setBorder(Border)
	 */
	public void setBorder(Border border) {
		if(fButtonAddScreen != null) {
			fButtonAddScreen.setBorder(border);
		}
		if(fButtonRemoveScreen != null) {
			fButtonRemoveScreen.setBorder(border);
		}
		if(fButtonRenameScreen != null) {
			fButtonRenameScreen.setBorder(border);
		}
	}

	/**
	 * @see com.ixora.common.ui.ToolBarComponent#setText(java.lang.String)
	 */
	public void setText(String text) {
		; // ignore this
	}

	/**
	 * @see com.ixora.common.ui.ToolBarComponent#setIcon(javax.swing.Icon)
	 */
	public void setIcon(Icon icon) {
		; // ignore this
	}

	/**
	 * @see com.ixora.common.ui.ToolBarComponent#setMnemonic(int)
	 */
	public void setMnemonic(int m) {
		; // ignore this
	}

	/**
	 * @see java.util.Observer#update(java.util.Observable, java.lang.Object)
	 */
	public void update(Observable o, Object arg) {
		try {
			setComboBoxData();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}
}
