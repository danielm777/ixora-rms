/**
 * 02-Jan-2006
 */
package com.ixora.common.ui.wizard;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.util.LinkedList;
import java.util.List;

import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.ixora.common.ui.AppDialog;
import com.ixora.common.ui.UIExceptionMgr;
import com.ixora.common.ui.UIFactoryMgr;
import com.ixora.common.ui.actions.ActionCancel;
import com.ixora.common.ui.actions.ActionFinish;
import com.ixora.common.ui.actions.ActionNext;
import com.ixora.common.ui.actions.ActionPrevious;

/**
 * @author Daniel Moraru
 */
public class Wizard extends AppDialog {
	public interface Listener {
		void finished(Object value);
		void cancelled();
	}

	private List<WizardStep> fSteps;
	private int fCurrent = -1;
	private Listener fListener;
	private JPanel fStepsPanel;
	private JPanel fPanel;
	private Action fActionNext;
	private Action fActionPrevious;
	private Action fActionFinish;
	private JEditorPane fStepNameAndDesc;

	public Wizard(Frame parent, Listener listener, String name) {
		super(parent, VERTICAL);
		init(listener, name);
	}

	/**
	 * @param listener
	 * @param name
	 */
	private void init(Listener listener, String name) {
		setTitle(name);
		this.fListener = listener;
		fSteps = new LinkedList<WizardStep>();
		fStepsPanel = new JPanel(new CardLayout());
		fActionNext = new ActionNext() {
			public void actionPerformed(ActionEvent e) {
				handleNext();
			}
		};
		fActionPrevious = new ActionPrevious() {
			public void actionPerformed(ActionEvent e) {
				handlePrevious();
			}
		};
		fActionFinish = new ActionFinish() {
			public void actionPerformed(ActionEvent e) {
				handleFinish();
			}
		};
		fPanel = new JPanel(new BorderLayout());
		fStepNameAndDesc = UIFactoryMgr.createHtmlPane();
		JPanel top = new JPanel(new BorderLayout());
		JScrollPane sp = UIFactoryMgr.createScrollPane();
		sp.setViewportView(fStepNameAndDesc);
		sp.setPreferredSize(new Dimension(400, 50));
		top.add(sp, BorderLayout.CENTER);
		fPanel.add(top, BorderLayout.NORTH);
		fPanel.add(fStepsPanel, BorderLayout.CENTER);

		fActionPrevious.setEnabled(false);
		fActionFinish.setEnabled(false);
		buildContentPane();
	}

	public Wizard(Dialog parent, Listener listener, String name) {
		super(parent, VERTICAL, false);
		init(listener, name);
	}

	public Wizard addStep(WizardStep step) {
		Dimension dim = fStepsPanel.getPreferredSize();
		Dimension dim1 = step.getPreferredSize();
		Dimension dim2 = new Dimension(Math.max(dim.width, dim1.width),
				Math.max(dim.height, dim1.height));
		fStepsPanel.setPreferredSize(dim2);
		fSteps.add(step);
		return this;
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getDisplayPanels()
	 */
	protected Component[] getDisplayPanels() {
		return new Component[]{fPanel};
	}

	/**
	 * @see com.ixora.common.ui.AppDialog#getButtons()
	 */
	protected JButton[] getButtons() {
		return new JButton[]{
			UIFactoryMgr.createButton(fActionFinish),
			UIFactoryMgr.createButton(fActionPrevious),
			UIFactoryMgr.createButton(fActionNext),
			UIFactoryMgr.createButton(new ActionCancel(){
				public void actionPerformed(ActionEvent e) {
					fListener.cancelled();
					dispose();
				}
			})
		};
	}

	private void handleNext() {
		try {
			activateNextStep();
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	private void handlePrevious() {
		try {
			if(fCurrent == fSteps.size() - 1) {
				fActionFinish.setEnabled(false);
				fActionNext.setEnabled(true);
			}
			fCurrent--;
			if(fCurrent == 0) {
				fActionPrevious.setEnabled(false);
			}
			WizardStep step = fSteps.get(fCurrent);
			activateStep(step, null, false);

			((CardLayout)fStepsPanel.getLayout()).show(fStepsPanel, "step" + fCurrent);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	private void handleFinish() {
		try {
			WizardStep step = fSteps.get(fCurrent);
			Object obj = step.deactivate();
			this.fListener.finished(obj);
		} catch(Exception e) {
			UIExceptionMgr.userException(e);
		}
	}

	/**
	 * @see java.awt.Component#setVisible(boolean)
	 */
	public void setVisible(boolean b) {
		if(b) {
			// initialize
			int i = 0;
			for(WizardStep step : fSteps) {
				fStepsPanel.add(step, "step" + i);
				++i;
			}
			try {
				activateNextStep();
			} catch (Exception e) {
				UIExceptionMgr.userException(e);
			}
		}
		super.setVisible(b);
	}

	private void activateNextStep() throws Exception {
		Object val = null;
		if(fCurrent >= 0) {
			WizardStep current = fSteps.get(fCurrent);
			val = current.deactivate();
		}
		fCurrent++;
		if(fCurrent == fSteps.size() - 1) {
			fActionFinish.setEnabled(true);
			fActionNext.setEnabled(false);
		}
		if(fCurrent > 0) {
			fActionPrevious.setEnabled(true);
		}
		WizardStep step = fSteps.get(fCurrent);
		activateStep(step, val, true);
		((CardLayout)fStepsPanel.getLayout()).show(fStepsPanel, "step" + fCurrent);
	}

	private void activateStep(WizardStep step, Object val, boolean activate) throws Exception {
		String txt = "<b>" + step.getName() + "</b><br>" + step.getDescription();
		fStepNameAndDesc.setText(txt);
		if(activate) {
			step.activate(val);
		}
	}
}
