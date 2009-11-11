package com.ixora.common.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * @author Daniel Moraru
 */
public class LayoutUtils {
	/**
	 * @param owner must have a BorderLayout
	 * @param comps
	 * @param direction one of BorderLayout.EAST, BorderLayout.WEST
	 */
	public static void alignVerticallyInPanelWithBorderLayout(
			JPanel owner, Component[] comps, String direction) {
		allignInPanelWithBorderLayout(SwingConstants.VERTICAL, owner, comps, direction);
	}

	/**
	 * @param owner must have a BorderLayout
	 * @param comps
	 * @param direction one of BorderLayout.NORTH, BorderLayout.SOUTH
	 */
	public static void alignHorizontallyInPanelWithBorderLayout(
			JPanel owner, Component[] comps, String direction) {
		allignInPanelWithBorderLayout(SwingConstants.HORIZONTAL, owner, comps, direction);
	}

	/**
	 * @param orientation one of SwingConstants.HORIZONTAL or VERTICAL
	 * @param owner
	 * @param comps
	 * @param direction
	 */
	private static void allignInPanelWithBorderLayout(
			int orientation, JPanel owner, Component[] comps, String direction) {
		int gap = UIConfiguration.getPanelPadding();
		GridLayout layout;
		if(orientation == SwingConstants.HORIZONTAL) {
			layout = new GridLayout(1, comps.length, gap, 0);
		} else {
			layout = new GridLayout(comps.length, 1, 0, gap);
		}
		JPanel panelComps = new JPanel(layout);
		for (int i = 0; i < comps.length; i++) {
			panelComps.add(comps[i]);
		}
		JPanel panelCompsExt = new JPanel(new BorderLayout());
		panelCompsExt.setBorder(BorderFactory.createEmptyBorder(0, gap, 0, gap));
		panelCompsExt.add(panelComps,
				orientation == SwingConstants.HORIZONTAL ? BorderLayout.EAST : BorderLayout.NORTH);
		owner.add(panelCompsExt, direction);
	}
}
