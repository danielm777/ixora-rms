/*
 * Created on 28-Dec-2003
 */
package com.ixora.rms.ui;

import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import com.ixora.common.ui.UIConfiguration;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.utils.Utils;
import com.ixora.rms.MonitoringLevel;
import com.ixora.rms.client.model.EntityNode;

/**
 * Renderer/editor for the entity nodes.
 * @author Daniel Moraru
 */
public class EntityNodeCellRenderer extends DefaultTreeCellRenderer {
	private static final long serialVersionUID = 3293230108236377327L;
	protected ImageIcon monlevelNone;
	protected ImageIcon monlevelLow;
	protected ImageIcon monlevelMed;
	protected ImageIcon monlevelHigh;
	protected ImageIcon monlevelMax;
	/** Original font */
	protected Font originalFont;
	protected int maxLineLengthForToolTips;

	protected SessionModelTreeNodeDecorator nodeDecorator;

	/**
	 * Constructor.
	 */
	public EntityNodeCellRenderer() {
		super();
		this.monlevelNone = UIConfiguration.getIcon("monlevel_none.gif");
		this.monlevelLow = UIConfiguration.getIcon("monlevel_low.gif");
		this.monlevelMed = UIConfiguration.getIcon("monlevel_medium.gif");
		this.monlevelHigh = UIConfiguration.getIcon("monlevel_high.gif");
		this.monlevelMax = UIConfiguration.getIcon("monlevel_max.gif");
		originalFont = this.getFont();
		this.maxLineLengthForToolTips = UIConfiguration.getMaximumLineLengthForToolTipText();
		nodeDecorator = new SessionModelTreeNodeDecoratorImpl();
	}
	/**
	 * @see com.ixora.common.ui.CheckBoxTreeCellRenderer#getTreeCellComponent(javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int, boolean)
	 */
	public Component getTreeCellRendererComponent(
		JTree tree,
		Object value,
		boolean isSelected,
		boolean expanded,
		boolean leaf,
		int row,
		boolean hasFocus) {
		super.getTreeCellRendererComponent(
			tree,
			value,
			isSelected,
			expanded,
			leaf,
			row,
			hasFocus);
		EntityNode en = (EntityNode)value;
		if(originalFont == null) {
		    originalFont = this.getFont();
		}
		if(en.getEntityInfo().uncommittedVisibleCounters()) {
			this.setFont(getFont().deriveFont(Font.BOLD));
			this.setText("*" + this.getText());
		} else {
			if(originalFont != null) {
			    this.setFont(originalFont);
			}
		}
		MonitoringLevel level = en.getEntityInfo().getConfiguration().getMonitoringLevel();
		Icon	itemIcon = null;
		if(level == MonitoringLevel.LOW) {
		    itemIcon = this.monlevelLow;
		} else if(level == MonitoringLevel.MEDIUM) {
		    itemIcon = this.monlevelMed;
		} else if(level == MonitoringLevel.HIGH) {
		    itemIcon = this.monlevelHigh;
		} else if(level == MonitoringLevel.MAXIMUM) {
		    itemIcon = monlevelMax;
		} else if(level == MonitoringLevel.NONE){
		    itemIcon = monlevelNone;
		}
		nodeDecorator.decorate(en, itemIcon, true, this);
		// tool tip is of the form:
		// Name: entityName
		// Description: entityDescription
		String desc = en.getEntityInfo().getTranslatedDescription();
		// TODO localize
		StringBuffer toolTipText = new StringBuffer();
		toolTipText.append("<b>Name: </b>")
			.append(en.getEntityInfo().getTranslatedName());
		if(!Utils.isEmptyString(desc)) {
			toolTipText.append("<br><b>Description: </b>").append(desc);
		}
		int childrenCount = en.getChildCount();
		if(childrenCount > 0) {
			toolTipText.append("<br><b>Children count: </b> ")
				.append(childrenCount);
		}
       	setToolTipText(UIUtils.getMultilineHtmlText(toolTipText.toString(),
       			maxLineLengthForToolTips));
		return this;
	}
}
