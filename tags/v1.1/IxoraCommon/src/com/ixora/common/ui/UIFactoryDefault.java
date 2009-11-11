/*
 * Created on 10-Jan-2004
 */
package com.ixora.common.ui;

import java.awt.Font;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.BorderFactory;
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
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import com.ixora.common.typedproperties.exception.PropertyValueNotSet;
import com.ixora.common.ui.preferences.PreferencesConfiguration;

/**
 * @author Daniel Moraru
 */
public class UIFactoryDefault implements UIFactory {

	/**
	 * Helper class that tracks and saves the divider position for split panes.
	 */
	private class SplitPaneDividerPositionTracker implements PropertyChangeListener, ContainerListener {
		private JSplitPane fPane;
		private String fProperty;
		/**
		 * @param pane
		 */
		SplitPaneDividerPositionTracker(JSplitPane pane) {
			fPane = pane;			
			fProperty = "splitpane." + pane.getName() + ".divider_position";
			if(PreferencesConfiguration.get().hasProperty(fProperty)) {
				pane.setDividerLocation(PreferencesConfiguration.get().getInt(fProperty));
			}
			fPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, this);
			fPane.addContainerListener(this);
		}
		public void propertyChange(PropertyChangeEvent evt) {
			if(evt.getSource() == fPane) {
				try {
					if(JSplitPane.DIVIDER_LOCATION_PROPERTY.equals(evt.getPropertyName())) {
						PreferencesConfiguration.get().setInt(
							fProperty,
							(Integer)evt.getNewValue());
					}
				} catch(Exception ex) {
					UIExceptionMgr.exception(ex);
				}
			}
		}
		public void componentAdded(ContainerEvent e) {
			if(e.getSource() == fPane) {
				if(e.getChild() == null) {
					return;
				}
				if(PreferencesConfiguration.get().hasProperty(fProperty)) {
					try {
						int val = PreferencesConfiguration.get().getInt(fProperty);
						fPane.setDividerLocation(val);
					} catch(PropertyValueNotSet ex) {
						; // ignore
					}					
				}
			}
		}
		public void componentRemoved(ContainerEvent e) {
		}
	}

	/**
	 * Constructor.
	 */
	public UIFactoryDefault() {
		super();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createButton()
	 */
	public JButton createButton() {
		JButton ret = new JButton();
		return ret;
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createButton(javax.swing.Action)
	 */
	public JButton createButton(Action action) {
		JButton ret = new JButton(action);
		return ret;
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createCheckBoxMenuItem()
	 */
	public JCheckBoxMenuItem createCheckBoxMenuItem() {
		return new JCheckBoxMenuItem();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createCheckBoxMenuItem(javax.swing.Action)
	 */
	public JCheckBoxMenuItem createCheckBoxMenuItem(Action action) {
		return new JCheckBoxMenuItem(action);
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createLabel(String)
	 */
	public JLabel createLabel(String txt) {
		return new JLabel(txt);
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createMenu()
	 */
	public JMenu createMenu() {
		return new JMenu();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createMenu(javax.swing.Action)
	 */
	public JMenu createMenu(Action action) {
		return new JMenu(action);
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createMenuBar()
	 */
	public JMenuBar createMenuBar() {
		return new JMenuBar();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createMenuItem()
	 */
	public JMenuItem createMenuItem() {
		return new JMenuItem();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createMenuItem(javax.swing.Action)
	 */
	public JMenuItem createMenuItem(Action action) {
		return new JMenuItem(action);
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createPopupMenu()
	 */
	public JPopupMenu createPopupMenu() {
		return new JPopupMenu();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createProgressBar()
	 */
	public JProgressBar createProgressBar() {
		return new JProgressBar();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createSlider()
	 */
	public JSlider createSlider() {
		return new JSlider();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createSpinner()
	 */
	public JSpinner createSpinner() {
		return new JSpinner();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createSpinner(javax.swing.SpinnerModel)
	 */
	public JSpinner createSpinner(SpinnerModel model) {
		return new JSpinner(model);
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createSplitPane()
	 */
	public JSplitPane createSplitPane() {
		JSplitPane ret = new JSplitPane();
		ret.setDividerSize(UIConfiguration.getSplitPaneDividerSize());
		return ret;
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createSplitPane(java.lang.String)
	 */
	public JSplitPane createSplitPane(String name) {
		JSplitPane ret = createSplitPane();
		ret.setName(name);
		new SplitPaneDividerPositionTracker(ret);
		return ret;
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createTextArea()
	 */
	public JTextArea createTextArea() {
		JTextArea ret = new JTextArea();
		TextComponentHandler handler = new TextComponentHandler(ret);
		ret.putClientProperty(TEXTC_COMPONENT_HANDLER, handler);
		return ret;
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createTitledBorder(java.lang.String)
	 */
	public TitledBorder createTitledBorder(String title) {
		return new TitledBorder(title);
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createToggleButton()
	 */
	public JToggleButton createToggleButton() {
		return new JToggleButton();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createToggleButton(javax.swing.Action)
	 */
	public JToggleButton createToggleButton(Action action) {
		return new JToggleButton(action);
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createToolBar()
	 */
	public JToolBar createToolBar() {
		return new JToolBar();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createSrollPane()
	 */
	public JScrollPane createScrollPane() {
		return new JScrollPane();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createTextField()
	 */
	public JTextField createTextField() {
		JTextField ret = new JTextField();
		TextComponentHandler handler = new TextComponentHandler(ret);
		ret.putClientProperty(TEXTC_COMPONENT_HANDLER, handler);
		return ret;
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createPasswordField()
	 */
	public JPasswordField createPasswordField() {
		JPasswordField ret = new JPasswordField();
		TextComponentHandler handler = new TextComponentHandler(ret);
		ret.putClientProperty(TEXTC_COMPONENT_HANDLER, handler);
		return ret;
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createCheckBox()
	 */
	public JCheckBox createCheckBox() {
		return new JCheckBox();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createTabbedPane()
	 */
	public JTabbedPane createTabbedPane() {
		return new JTabbedPane();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createTree()
	 */
	public JTree createTree() {
		return new JTree();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createEditorPane()
	 */
	public JEditorPane createEditorPane() {
		return new JEditorPane();
	}
    /**
     * @see com.ixora.common.ui.UIFactory#createList()
     */
    public JList createList() {
        return new JList();
    }
    /**
     * @see com.ixora.common.ui.UIFactory#createComboBox()
     */
    public JComboBox createComboBox() {
        return new JComboBox();
    }
	/**
	 * @see com.ixora.common.ui.UIFactory#createTextPane()
	 */
	public JTextPane createTextPane() {
        JTextPane ret = new JTextPane();
		TextComponentHandler handler = new TextComponentHandler(ret);
		ret.putClientProperty(TEXTC_COMPONENT_HANDLER, handler);
		return ret;
	}

	/**
	 * @see com.ixora.common.ui.UIFactory#createTable()
	 */
	public JTable createTable() {
		return new JTable();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createHtmlPane()
	 */
	public JEditorPane createHtmlPane() {
        JEditorPane htmlPane = createEditorPane();
        htmlPane.setEditable(false);
        htmlPane.setEditable(false);
        htmlPane.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        htmlPane.setContentType("text/html");
        JButton sample = createButton();
        Font font = sample.getFont();
        HTMLEditorKit editor = (HTMLEditorKit)htmlPane.getEditorKit();
        StyleSheet ss = editor.getStyleSheet();
        ss.addRule("body {font-size: "
        		+ font.getSize() + "pt; font-family: "
				+ font.getFamily() +"}");
        htmlPane.setBackground(sample.getBackground());
        // set the base to the icon folder
        ((HTMLDocument)htmlPane.getDocument())
			.setBase(UIConfiguration.getIconsURL());
        htmlPane.setCaretPosition(0);
        new TextComponentHandler(htmlPane);
        return htmlPane;
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createFormattedTextField(DefaultFormatter)
	 */
	public JFormattedTextField createFormattedTextField(DefaultFormatter f) {
		JFormattedTextField ret = new JFormattedTextField(f);
		new TextComponentHandler(ret);
		return ret;
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createRadioButton()
	 */
	public JRadioButton createRadioButton() {
		return new JRadioButton();
	}
	/**
	 * @see com.ixora.common.ui.UIFactory#createSplitPane(int, boolean)
	 */
	public JSplitPane createSplitPane(int orientation, boolean continuousLayout) {
		JSplitPane ret = new JSplitPane(orientation, continuousLayout);
		ret.setDividerSize(UIConfiguration.getSplitPaneDividerSize());
		return ret;
	}
	
	/**
	 * @see com.ixora.common.ui.UIFactory#createSplitPane(int, boolean)
	 */
	public JSplitPane createSplitPane(String name, int orientation, boolean continuousLayout) {
		JSplitPane ret = createSplitPane(orientation, continuousLayout);
		ret.setName(name);
		new SplitPaneDividerPositionTracker(ret);
		return ret;
	}
	
	/**
	 * @see com.ixora.common.ui.UIFactory#createList(javax.swing.ListModel)
	 */
	public JList createList(ListModel model) {
		return new JList(model);
	}
}
