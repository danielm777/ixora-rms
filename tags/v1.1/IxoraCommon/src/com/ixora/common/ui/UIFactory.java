/*
 * Created on 10-Jan-2004
 */
package com.ixora.common.ui;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JEditorPane;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.ListModel;
import javax.swing.SpinnerModel;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatter;

/**
 * @author Daniel Moraru
 */
public interface UIFactory {
	/** Client property that allows access to the popup menu for text components */
	public static final String TEXTC_COMPONENT_HANDLER = "popup";

	/**
	 * @return JButton
	 */
	JButton createButton();
	/**
	 * @param action
	 * @return JButton
	 */
	JButton createButton(Action action);
	/**
	 * @return JCheckBoxMenuItem
	 */
	JCheckBoxMenuItem createCheckBoxMenuItem();
	/**
	 * @return JCheckBox
	 */
	JCheckBox createCheckBox();
	/**
	 * @return JComboBox
	 */
	JComboBox createComboBox();
	/**
	 * @param action
	 * @return JCheckBoxMenuItem
	 */
	JCheckBoxMenuItem createCheckBoxMenuItem(Action action);
	/**
	 * @return JLabel
	 */
	JLabel createLabel(String txt);
	/**
	 * @return JMenu
	 */
	JMenu createMenu();
	/**
	 * @param action
	 * @return JMenu
	 */
	JMenu createMenu(Action action);
	/**
	 * @return JMenuBar
	 */
	JMenuBar createMenuBar();
	/**
	 * @return JMenuItem
	 */
	JMenuItem createMenuItem();
	/**
	 * @param action
	 * @return JMenuItem
	 */
	JMenuItem createMenuItem(Action action);
	/**
	 * @return JPopupMenu
	 */
	JPopupMenu createPopupMenu();
	/**
	 * @return JProgressBar
	 */
	JProgressBar createProgressBar();
	/**
	 * @return JSlider
	 */
	JSlider createSlider();
	/**
	 * @return JSpinner
	 */
	JSpinner createSpinner();
	/**
	 * @return JSpinner
	 * @param model
	 */
	JSpinner createSpinner(SpinnerModel model);
	/**
	 * @return JSplitPane
	 */
	JSplitPane createSplitPane();
	/**
	 * @param name
	 * @return JSplitPane
	 */
	JSplitPane createSplitPane(String name);
	/**
	 * @return JTextArea
	 */
	JTextArea createTextArea();
	/**
	 * @return JEditorPane
	 */
	JEditorPane createEditorPane();
	/**
	 * @return JEditorPane
	 */
	JEditorPane createHtmlPane();
	/**
	 * @return JTextPane
	 */
	JTextPane createTextPane();
	/**
	 * @return JTextField
	 */
	JTextField createTextField();
	/**
	 * @return JTextField
	 */
	JPasswordField createPasswordField();
	/**
	 * @return JFormattedTextField
	 */
	JFormattedTextField createFormattedTextField(DefaultFormatter f);
	/**
	 * @param title
	 * @return Border
	 */
	TitledBorder createTitledBorder(String title);
	/**
	 * @return JToggleButton
	 */
	JToggleButton createToggleButton();
	/**
	 * @param action
	 * @return JToggleButton
	 */
	JToggleButton createToggleButton(Action action);
	/**
	 * @return JToolBar
	 */
	JToolBar createToolBar();
	/**
	 * @return JScrollPane
	 */
	JScrollPane createScrollPane();
	/**
	 * @return JTabbedPane
	 */
	JTabbedPane createTabbedPane();
	/**
	 * @return JTree
	 */
	JTree createTree();
	/**
	 * @return JTable
	 */
	JTable createTable();
	/**
	 * @return JList
	 */
	JList createList();
	/**
	 * @return
	 */
	JRadioButton createRadioButton();
	/**
	 * @param orientation
	 * @param continuousLayout
	 * @return
	 */
	JSplitPane createSplitPane(int orientation, boolean continuousLayout);
	/**
	 * This method should only be used when tracking of the divider position is needed.<br>
	 * The last position of the split pane is saved on program exit in the "preferences" component
	 * configuration.
	 * @param name The name used to identify this split pane in the "preferences" configuration.
	 * @param orientation
	 * @param continuousLayout
	 * @return
	 */
	JSplitPane createSplitPane(String name, int orientation, boolean continuousLayout);
	
	/**
	 * @param model
	 * @return
	 */
	JList createList(ListModel model);	
}
