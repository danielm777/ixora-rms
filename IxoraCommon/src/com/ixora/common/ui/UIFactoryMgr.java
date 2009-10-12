/*
 * Created on Feb 22, 2004
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
import javax.swing.SpinnerModel;
import javax.swing.border.TitledBorder;
import javax.swing.text.DefaultFormatter;

/**
 * @author Daniel Moraru
 */
public final class UIFactoryMgr {
	/** UIFactory */
	private static UIFactory uiFactory = new UIFactoryDefault();

	/**
	 * Private constructor.
	 */
	private UIFactoryMgr() {
		super();
	}

	/**
	 * Installs a UI factory. Should only be call once before any of
	 * the UI utils classes are used.
	 * @param uif
	 */
	public static void installUIFactory(UIFactory uif) {
		if(uiFactory == null) {
			throw new IllegalArgumentException("null ui factory");
		}
		uiFactory = uif;
	}

	/**
	 * @see com.ixora.common.ui.UIFactory#createButton()
	 */
	public static JButton createButton() {
		return uiFactory.createButton();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createButton(javax.swing.Action)
	 */
	public static JButton createButton(Action action) {
		return uiFactory.createButton(action);
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createCheckBoxMenuItem()
	 */
	public static JCheckBoxMenuItem createCheckBoxMenuItem() {
		return uiFactory.createCheckBoxMenuItem();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createCheckBoxMenuItem(javax.swing.Action)
	 */
	public static JCheckBoxMenuItem createCheckBoxMenuItem(Action action) {
		return uiFactory.createCheckBoxMenuItem(action);
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createLabel(String)
	 */
	public static JLabel createLabel(String txt) {
		return uiFactory.createLabel(txt);
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createMenu()
	 */
	public static JMenu createMenu() {
		return uiFactory.createMenu();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createMenu(javax.swing.Action)
	 */
	public static JMenu createMenu(Action action) {
		return uiFactory.createMenu(action);
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createMenuBar()
	 */
	public static JMenuBar createMenuBar() {
		return uiFactory.createMenuBar();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createMenuItem()
	 */
	public static JMenuItem createMenuItem() {
		return uiFactory.createMenuItem();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createMenuItem(javax.swing.Action)
	 */
	public static JMenuItem createMenuItem(Action action) {
		return uiFactory.createMenuItem(action);
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createPopupMenu()
	 */
	public static JPopupMenu createPopupMenu() {
		return uiFactory.createPopupMenu();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createProgressBar()
	 */
	public static JProgressBar createProgressBar() {
		return uiFactory.createProgressBar();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createSlider()
	 */
	public static JSlider createSlider() {
		return uiFactory.createSlider();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createSpinner()
	 */
	public static JSpinner createSpinner() {
		return uiFactory.createSpinner();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createSpinner(javax.swing.SpinnerModel)
	 */
	public static JSpinner createSpinner(SpinnerModel model) {
		return uiFactory.createSpinner(model);
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createSplitPane()
	 */
	public static JSplitPane createSplitPane() {
		return uiFactory.createSplitPane();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createSplitPane(String)
	 */
	public static JSplitPane createSplitPane(String name) {
		return uiFactory.createSplitPane(name);
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createTextArea()
	 */
	public static JTextArea createTextArea() {
		return uiFactory.createTextArea();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createEditorPane()
	 */
	public static JEditorPane createEditorPane() {
		return uiFactory.createEditorPane();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createHtmlPane()
	 */
	public static JEditorPane createHtmlPane() {
		return uiFactory.createHtmlPane();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createTitledBorder(java.lang.String)
	 */
	public static TitledBorder createTitledBorder(String title) {
		return uiFactory.createTitledBorder(title);
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createToggleButton()
	 */
	public static JToggleButton createToggleButton() {
		return uiFactory.createToggleButton();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createToggleButton(javax.swing.Action)
	 */
	public static JToggleButton createToggleButton(Action action) {
		return uiFactory.createToggleButton(action);
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createToolBar()
	 */
	public static JToolBar createToolBar() {
		return uiFactory.createToolBar();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createSrollPane()
	 */
	public static JScrollPane createScrollPane() {
		return uiFactory.createScrollPane();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createTextField()
	 */
	public static JTextField createTextField() {
		return uiFactory.createTextField();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createPasswordField()
	 */
	public static JTextField createPasswordField() {
		return uiFactory.createPasswordField();
	}
	/**
	 * @return JFormattedTextField
	 */
	public static final JFormattedTextField createFormattedTextField(DefaultFormatter f) {
		return uiFactory.createFormattedTextField(f);
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createCheckBox()
	 */
	public static JCheckBox createCheckBox() {
		return uiFactory.createCheckBox();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createTextPane()
	 */
	public static JTextPane createTextPane() {
		return uiFactory.createTextPane();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createComboBox()
	 */
	public static JComboBox createComboBox() {
		return uiFactory.createComboBox();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createTabbedPane()
	 */
	public static JTabbedPane createTabbedPane() {
		return uiFactory.createTabbedPane();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createTree()
	 */
	public static JTree createTree() {
		return uiFactory.createTree();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createList()
	 */
	public static JList createList() {
		return uiFactory.createList();
	}

	/**
	 * com.ixora.common.ui.UIFactory#createTable()
	 */
	public static JTable createTable() {
		return uiFactory.createTable();
	}

	/**
	 * @return
	 */
	public static JRadioButton createRadioButton() {
		return uiFactory.createRadioButton();
	}

	/**
	 * @param orientation
	 * @param continuosLayout
	 * @return
	 */
	public static JSplitPane createSplitPane(int orientation, boolean continuosLayout) {
		return uiFactory.createSplitPane(orientation, continuosLayout);
	}
	
	/**
	 * @param name
	 * @param orientation
	 * @param continuosLayout
	 * @return
	 */
	public static JSplitPane createSplitPane(String name, int orientation, boolean continuosLayout) {
		return uiFactory.createSplitPane(name, orientation, continuosLayout);
	}	
}
