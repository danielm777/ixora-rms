/*
 * Created on 01-Jun-2005
 */
package com.ixora.common.ui;



import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.StyleSheet;

import com.ixora.common.ui.actions.ActionClose;

public final class NonFatalErrorViewerDialog extends AppDialog {
	private static final long serialVersionUID = 3740939825330332494L;
	private JPanel fPanel;
    private JTextPane fTextPane;
    /**
     * Constructor.
     * @param parent
     */
    public NonFatalErrorViewerDialog(Frame parent) {
        super(parent, VERTICAL);
        init();
    }

    /**
     * Sets the data to show
     * @param logs
     */
    public void setData(NonFatalErrorBuffer logs) {
        StringBuffer sb = new StringBuffer();
        sb.append("<html>");
        List<NonFatalErrorBuffer.LogEntry> entries = logs.getLogEntries();
        for(NonFatalErrorBuffer.LogEntry le : entries) {
            sb.append(le.toHtmlString());
            sb.append("<br>");
        }
        sb.append("</html>");
        fTextPane.setText(sb.toString());
    }

    /**
     * @param text
     * @param title
     */
    private void init() {
        // TODO localize
        setTitle("Error log");
        setModal(false);
        fPanel = new JPanel(new BorderLayout());
        JScrollPane sp = UIFactoryMgr.createScrollPane();
        fTextPane = UIFactoryMgr.createTextPane();
        fTextPane.setContentType("text/html");
        Font font = MetalLookAndFeel.getUserTextFont();
        HTMLEditorKit editor = (HTMLEditorKit)fTextPane.getEditorKit();
        StyleSheet ss = editor.getStyleSheet();
        ss.addRule("body {font-size: "
                + font.getSize() + "pt; font-family: "
                + font.getFamily() +"}");
        sp.setViewportView(fTextPane);
        fPanel.add(sp, BorderLayout.CENTER);
        sp.setPreferredSize(new Dimension(350, 350));
        buildContentPane();
    }
    /**
     * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
     */
    protected Component[] getDisplayPanels() {
        return new Component[] {fPanel};
    }
    /**
     * @see com.ixora.common.ui.AppDialog#getButtons()
     */
    @SuppressWarnings("serial")
	protected JButton[] getButtons() {
        return new JButton[] {
            UIFactoryMgr.createButton(new ActionClose() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }})
        };
    }
}
