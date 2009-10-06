/*
 * Created on 01-Jan-2004
 */
package com.ixora.rms.ui;

import java.awt.Dimension;

import javax.swing.DefaultBoundedRangeModel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.SpinnerNumberModel;

import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.rms.MonitoringLevel;

/**
 * @author Daniel Moraru
 */
public final class RMSUIUtils {

	/**
	 * Constructor.
	 */
	private RMSUIUtils() {
		super();
	}

	/**
	 * Creates a monitoring level spinner.
	 * @param selected it can be null and then
	 * <code>MonitoringLevel.MAXIMUM</code> will
	 * be selected
	 * @param allowSelection whether or not to allow selection
	 * of monitoring levels
	 * @return a monitoring level spinner
	 */
	public static JSpinner createMonitoringLevelSpinner(
			MonitoringLevel selected) {
		Object[] model = null;
		model = new Object[] {
				MonitoringLevel.NONE,
				MonitoringLevel.LOW,
				MonitoringLevel.MEDIUM,
				MonitoringLevel.HIGH,
				MonitoringLevel.MAXIMUM
			};
		JSpinner jSpinnerLevel = UIFactoryMgr.createSpinner(
				new SpinnerListModel(model));
		Dimension d = new Dimension(70, 20);
		jSpinnerLevel.setMinimumSize(d);
		jSpinnerLevel.setMaximumSize(d);
		jSpinnerLevel.setPreferredSize(d);
		jSpinnerLevel.setValue(
			selected != null ? selected : MonitoringLevel.MAXIMUM);
		return jSpinnerLevel;
	}

	/**
	 * Creates the sampling interval spinner.
	 * @return
	 */
	public static JSpinner createSamplingIntervalSpinner(int selected) {
		JSpinner jSpinnerInterval = UIFactoryMgr.createSpinner(
				new SpinnerNumberModel(selected, 1, 600, 1));
		Dimension d = new Dimension(40, 20);
		jSpinnerInterval.setMinimumSize(d);
		jSpinnerInterval.setMaximumSize(d);
		jSpinnerInterval.setPreferredSize(d);
		return jSpinnerInterval;
	}

	/**
	 * Creates the log speed replay slider.
	 * @param selected
	 * @param max
	 * @return
	 */
	public static JSlider createReplaySpeedSlider(int selected, int max) {
	    JSlider jSlider = UIFactoryMgr.createSlider();
	    if(selected > max) {
	        max = selected;
	    }
	    jSlider.setModel(new DefaultBoundedRangeModel(selected, 0, 0, max));
	    Dimension d = new Dimension(70, 20);
	    jSlider.setMinimumSize(d);
		jSlider.setMaximumSize(d);
		jSlider.setPreferredSize(d);
		jSlider.setPaintTicks(false);
		jSlider.setPaintLabels(false);
		jSlider.setOrientation(JSlider.HORIZONTAL);
		return jSlider;
	}

	/**
	 * Sets up the configured look and feel.
	 */
	public static void setLookAndFeel()
	{
	    try {
//			UIManager.setLookAndFeel("com.jgoodies.plaf.plastic.PlasticXPLookAndFeel");
//			UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
//			PlasticLookAndFeel.setMyCurrentTheme(new SkyBluerTahoma());
	    }catch (Exception e) {
            // TODO: ignore but report exception: just don't change lnf
        }
	}

}
