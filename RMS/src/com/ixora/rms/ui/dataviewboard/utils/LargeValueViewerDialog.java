/*
 * Created on 01-Jun-2005
 */
package com.ixora.rms.ui.dataviewboard.utils;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionClose;

/**
 * Dialog used to show large values.
 * @author Daniel Moraru
 */
public final class LargeValueViewerDialog extends AppDialog {
    private JPanel fPanel;
    private JTextPane fTextPane;

    /**
     * Constructor.
     * @param parent
     * @param orientation
     */
    public LargeValueViewerDialog(Frame parent, String text, String title) {
        super(parent, VERTICAL);
        init(text, title);
    }
    /**
     * @param text
     * @param title
     */
    private void init(String text, String title) {
        setPreferredSize(new Dimension(400, 400));
        setTitle(title);
        setModal(false);
        fPanel = new JPanel(new BorderLayout());
        JScrollPane sp = UIFactoryMgr.createScrollPane();
        fTextPane = UIFactoryMgr.createTextPane();
        fTextPane.setText(text);
        sp.setViewportView(fTextPane);
        fPanel.add(sp, BorderLayout.CENTER);
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
    protected JButton[] getButtons() {
        return new JButton[] {
            UIFactoryMgr.createButton(new ActionClose() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }})
        };
    }
}
