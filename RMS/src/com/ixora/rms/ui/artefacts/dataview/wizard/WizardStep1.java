/**
 * 02-Jan-2006
 */
package com.ixora.rms.ui.artefacts.dataview.wizard;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import com.ixora.rms.ResourceId;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.UIUtils;
import com.ixora.common.ui.wizard.WizardStep;

/**
 * @author Daniel Moraru
 */
public class WizardStep1 extends WizardStep {
	private ResourceId fContext;

	public static class Data {
		enum Type {ENTITY_REGEX, DISTINCT, HOST_REGEX}
		Type fType;

		Data(Type type) {
			fType = type;
		}
	}

	private JRadioButton fRadioEntityRegex;
	private JRadioButton fRadioDistinct;
	private JRadioButton fRadioHostRegex;

	public WizardStep1(ResourceId context) {
		super("Data View Type", "Select the type of data view that you would like to create");
		fContext = context;
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		fRadioEntityRegex = UIFactoryMgr.createRadioButton();
		fRadioEntityRegex.setText("Homogeneous data view");
		JLabel entityRegexLabel = UIFactoryMgr.createLabel(
				UIUtils.getMultilineHtmlText(
						"Select this option to create a data view based on regular expressions that requires counters from multiple similar entities (for instance CPU usage for certain processes).",
						100));
		entityRegexLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		fRadioDistinct = UIFactoryMgr.createRadioButton();
		fRadioDistinct.setText("Heterogeneous data view");
		JLabel distinctLabel = UIFactoryMgr.createLabel(
				UIUtils.getMultilineHtmlText(
						"Select this option to create a data view that requires counters from different entities (for instance CPU, Memory, Disk...).",
						100));
		distinctLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		fRadioHostRegex = UIFactoryMgr.createRadioButton();
		fRadioHostRegex.setText("Multi-host heterogeneous data view");
		JLabel regexHostLabel = UIFactoryMgr.createLabel(
				UIUtils.getMultilineHtmlText(
						"Select this option to create a data view that requires counters from different entities (for instance CPU, Memory, Disk...) accross multiple hosts.",
						100));
		regexHostLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		ButtonGroup group = new ButtonGroup();
		group.add(fRadioEntityRegex);
		group.add(fRadioDistinct);
		group.add(fRadioHostRegex);

		add(fRadioEntityRegex);
		add(entityRegexLabel);
		add(fRadioDistinct);
		add(distinctLabel);
		add(fRadioHostRegex);
		add(regexHostLabel);

		fRadioEntityRegex.setSelected(true);

		if(fContext != null) {
			fRadioHostRegex.setEnabled(false);
			regexHostLabel.setText(
					UIUtils.getMultilineHtmlText(
						"In order to create a data view that uses data from multiple hosts you must start the wizard at the monitoring session level (the first node in the tree).",
						100));
		}
	}

	/**
	 * @see com.ixora.common.ui.wizard.WizardStep#activate(java.lang.Object)
	 */
	public void activate(Object obj) throws Exception {
	}

	/**
	 * @see com.ixora.common.ui.wizard.WizardStep#deactivate()
	 */
	public Object deactivate() throws Exception {
		if(fRadioEntityRegex.isSelected()) {
			return new Data(Data.Type.ENTITY_REGEX);
		} else if(fRadioDistinct.isSelected()) {
			return new Data(Data.Type.DISTINCT);
		} else if(fRadioHostRegex.isSelected()) {
			return new Data(Data.Type.HOST_REGEX);
		}
		return null;
	}
}
