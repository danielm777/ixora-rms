/*
 * Created on 17-Jan-2004
 */
package com.ixora.rms.agents.ui;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.swing.JPanel;

import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.VetoException;
import com.ixora.common.typedproperties.ui.TypedPropertiesEditor;
import com.ixora.rms.agents.AgentCustomConfiguration;
import com.ixora.rms.exception.RMSException;

/**
 * This is the superclass of all custom configuration panels
 * @author Daniel Moraru
 */
public abstract class AgentCustomConfigurationPanel extends JPanel {
	private static final long serialVersionUID = -8099234876159272115L;
	/** Agent message catalog */
	protected String fAgentMessageCatalog;
	/** Context */
	protected AgentCustomConfigurationPanelContext fContext;
	/** Configuration */
	protected AgentCustomConfiguration fConfiguration;

	/**
	 * Listener.
	 */
	public interface Listener extends TypedPropertiesEditor.Listener {
	}

	/**
	 * Constructor.
	 * @param agentMsgCatalog
	 * @param ctxt
	 */
	protected AgentCustomConfigurationPanel(
			String agentMsgCatalog,
			AgentCustomConfigurationPanelContext ctxt) {
		super(true);
		this.fAgentMessageCatalog = agentMsgCatalog;
		fContext = ctxt;
	}

	/**
	 * @return a new agent custom configuration instance
	 */
	public abstract AgentCustomConfiguration createAgentCustomConfiguration() throws RMSException;

	/**
	 * @param configuration
	 */
	public void setAgentCustomConfiguration(AgentCustomConfiguration configuration) {
		fConfiguration = configuration;
	}

	/**
	 * Applies changes to the edited TypedProperties.
	 * @throws InvalidPropertyValue
	 */
	public abstract void applyChanges() throws InvalidPropertyValue, VetoException;

	/**
	 * Set the listener.
	 * @param listener
	 */
	public abstract void setListener(Listener listener);

	/**
	 * Removes the listener.
	 */
	public abstract void removeListener();

	/**
	 * @param configCustomPanelClass
	 * @param agentMsgCatalog
	 * @param ctxt
	 * @return
	 * @throws ClassNotFoundException
	 * @throws NoSuchMethodException
	 * @throws SecurityException
	 * @throws RMSException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws IllegalArgumentException
	 */
	public static AgentCustomConfigurationPanel createImplementation(
			String configCustomPanelClass,
			String agentMsgCatalog,
			AgentCustomConfigurationPanelContext ctxt)
				throws ClassNotFoundException, SecurityException, NoSuchMethodException, RMSException, IllegalArgumentException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Class<?> clazz = Class.forName(configCustomPanelClass);
		Constructor<?> cons = clazz.getConstructor(new Class[] {String.class, AgentCustomConfigurationPanelContext.class});
		if(cons == null) {
			RMSException e = new RMSException("Invalid configuration panel class");
			e.setIsInternalAppError();
			throw e;
		}
		return (AgentCustomConfigurationPanel)cons.newInstance(
				new Object[] {agentMsgCatalog, ctxt});
	}
}
