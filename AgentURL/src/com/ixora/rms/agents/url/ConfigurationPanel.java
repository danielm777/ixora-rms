/*
 * Created on Dec 25, 2005
 */
package com.ixora.rms.agents.url;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import com.ixora.common.typedproperties.TypedProperties;
import com.ixora.common.typedproperties.exception.InvalidPropertyValue;
import com.ixora.common.typedproperties.exception.VetoException;
import com.ixora.common.typedproperties.ui.ExtendedEditorMultilineText;
import com.ixora.common.typedproperties.ui.TypedPropertiesEditor;
import com.ixora.common.typedproperties.ui.list.TypedPropertiesListEditor;
import com.ixora.common.ui.forms.FormPanel;
import com.ixora.common.utils.Utils;
import com.ixora.rms.agents.AgentCustomConfiguration;
import com.ixora.rms.agents.ui.AgentCustomConfigurationPanel;
import com.ixora.rms.agents.ui.AgentCustomConfigurationPanelContext;
import com.ixora.rms.agents.url.RequestParameters.NameValue;
import com.ixora.rms.agents.url.messages.Msg;

/**
 * @author Daniel Moraru
 */
public final class ConfigurationPanel extends AgentCustomConfigurationPanel {
	private static final long serialVersionUID = -2585319696204164109L;
	private static final String LABEL_REQUEST = "HTTP Request";
	private static final String LABEL_REQUEST_PARAMETERS = "HTTP Request Parameters";

	private TypedPropertiesEditor fEditorMain;
	private TypedPropertiesListEditor fEditorParams;
	private TypedPropsParam fPrototype;
	private TypedPropsMain fPropsMain;
	private Listener fListener;
	private EventHandler fEventHandler;
	private FormPanel fForm;

	private class EventHandler implements TableModelListener {
		/**
		 * @see javax.swing.event.TableModelListener#tableChanged(javax.swing.event.TableModelEvent)
		 */
		public void tableChanged(TableModelEvent e) {
			if(fListener != null) {
				fListener.componentStateChanged();
			}
		}
	}

	private static final class TypedPropsMain extends TypedProperties {
		private static final long serialVersionUID = -5170071633000331501L;
		TypedPropsMain() {
			super();
			setProperty(Configuration.URL, TYPE_STRING, true, true);
			setProperty(Configuration.HTTP_METHOD, TYPE_STRING, true, true,
					new String[]{Configuration.HTTP_METHOD_GET, Configuration.HTTP_METHOD_POST});

			setString(Configuration.URL, "http://localhost:8080/index.php");
			setString(Configuration.HTTP_METHOD, Configuration.HTTP_METHOD_POST);
		}
		TypedPropsMain(Configuration conf) {
			this();
			apply(conf);
		}
		/**
		 * @see com.ixora.common.typedproperties.TypedProperties#veto()
		 */
		public void veto() throws VetoException {
			try {
				new URL(getString(Configuration.URL));
			} catch (MalformedURLException e) {
				throw new VetoException("Invalid URL: " + e.getLocalizedMessage());
			}
		}
	}

	private static final class TypedPropsParam extends TypedProperties {
		private static final long serialVersionUID = 2877800893841968159L;
		TypedPropsParam() {
			// TODO localize
			setProperty("Parameter", TYPE_STRING, true, true);
			setProperty("Value", TYPE_STRING, true, true, null, ExtendedEditorMultilineText.class.getName());
		}
		TypedPropsParam(RequestParameters.NameValue nv) {
			this();
			setString("Parameter", nv.getName());
			setString("Value", nv.getValue());
		}
	}

	/**
	 * Constructor.
	 * @param agentId
	 * @param ctxt
	 */
	public ConfigurationPanel(String agentId, AgentCustomConfigurationPanelContext ctxt) {
		super(agentId, ctxt);
		setLayout(new BorderLayout());
		fEventHandler = new EventHandler();
		fPrototype = new TypedPropsParam();
		fPropsMain = new TypedPropsMain();
		fEditorMain = new TypedPropertiesEditor(false);
		fEditorParams = new TypedPropertiesListEditor(fPrototype,
				Msg.AGENT_URL_NAME, null, TypedPropertiesListEditor.BUTTON_NEW_DELETE);
		fEditorParams.getTable().getModel().addTableModelListener(fEventHandler);

		fEditorMain.setPreferredSize(new Dimension(450, 120));
		//fEditorParams.setPreferredSize(new Dimension(450, 300));

		fForm = new FormPanel(FormPanel.VERTICAL2, SwingConstants.TOP);
		fForm.addPairs(
				new String[]{
						LABEL_REQUEST,
						LABEL_REQUEST_PARAMETERS
				},
				new Component[]{
						fEditorMain,
						fEditorParams
				});
		add(fForm, BorderLayout.CENTER);
	}

	/**
	 * @see com.ixora.rms.ui.AgentCustomConfigurationPanel#createAgentCustomConfiguration()
	 */
	public AgentCustomConfiguration createAgentCustomConfiguration() {
		return new Configuration();
	}

	/**
	 * @see com.ixora.rms.agents.ui.AgentCustomConfigurationPanel#setAgentCustomConfiguration(com.ixora.rms.agents.AgentCustomConfiguration)
	 */
	public void setAgentCustomConfiguration(AgentCustomConfiguration configuration) {
		super.setAgentCustomConfiguration(configuration);
		fPropsMain.apply(configuration);
		fEditorMain.setTypedProperties(Msg.AGENT_URL_NAME, fPropsMain);

		Object obj = configuration.getObject(Configuration.HTTP_PARAMETERS);
		if(obj != null) {
			RequestParameters rp = (RequestParameters)obj;
			List<NameValue> lst = rp.getParameters();
			if(!Utils.isEmptyCollection(lst)) {
				List<TypedPropsParam> params = new ArrayList<TypedPropsParam>(lst.size());
				for(NameValue nv : lst) {
					params.add(new TypedPropsParam(nv));
				}
				fEditorParams.getModel().setProperties(params);
			}
		}

	}

	/**
	 * @see com.ixora.rms.agents.ui.AgentCustomConfigurationPanel#applyChanges()
	 */
	public void applyChanges() throws InvalidPropertyValue, VetoException {
		fEditorMain.applyChanges();
		fEditorParams.stopEditing();

		fConfiguration.apply(fPropsMain);

		List<TypedProperties> params = fEditorParams.getModel().getAllProperties();
		if(!Utils.isEmptyCollection(params)) {
			List<RequestParameters.NameValue> lst = new ArrayList<NameValue>(params.size());
			for(TypedProperties tp : params) {
				lst.add(new RequestParameters.NameValue(tp.getString("Parameter"), tp.getString("Value")));
			}
			RequestParameters rp = new RequestParameters(lst);
			fConfiguration.setObject(Configuration.HTTP_PARAMETERS, rp);
		}
	}

	/**
	 * @see java.awt.Component#setEnabled(boolean)
	 */
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		fEditorMain.setEnabled(enabled);
		fEditorParams.setEnabled(enabled);
	}

	/**
	 * @see com.ixora.rms.agents.ui.AgentCustomConfigurationPanel#setListener(com.ixora.rms.agents.ui.AgentCustomConfigurationPanel.Listener)
	 */
	public void setListener(final Listener listener) {
		fListener = listener;
		fEditorMain.setListener(listener);
	}

	/**
	 * @see com.ixora.rms.agents.ui.AgentCustomConfigurationPanel#removeListener()
	 */
	public void removeListener() {
		fListener = null;
		fEditorMain.removeListener();
	}
}
