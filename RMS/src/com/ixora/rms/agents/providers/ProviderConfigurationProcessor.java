/*
 * Created on 26-Jan-2005
 */
package com.ixora.rms.agents.providers;

import java.util.Map;

import com.ixora.common.MessageRepository;
import com.ixora.common.typedproperties.PropertyEntry;
import com.ixora.common.typedproperties.PropertyEntryString;
import com.ixora.common.typedproperties.TypedProperties;
import com.ixora.common.utils.Utils;
import com.ixora.rms.EntityConfiguration;
import com.ixora.rms.agents.AgentConfiguration;
import com.ixora.rms.exception.ProviderConfigurationTokenReplacementError;
import com.ixora.rms.providers.ProviderCustomConfiguration;
import com.ixora.rms.providers.ProvidersComponent;

/**
 * @author Daniel Moraru
 */
public final class ProviderConfigurationProcessor {
//	 provider configuration substitution tokens
	private static final String PROVIDER_CONFIG_TOKEN_AGENT = "{agent}";
	private static final String PROVIDER_CONFIG_TOKEN_AGENT_BEGIN = "{agent";
	private static final String PROVIDER_CONFIG_TOKEN_TICK = "{tick}";
	private static final String PROVIDER_CONFIG_TOKEN_TICK_MS = "{tick.ms}";
	private static final String PROVIDER_CONFIG_TOKEN_HOST = "{host}";
	private static final String PROVIDER_CONFIG_APPLICATION_HOME = "{home}";

	/**
	 * Constructor.
	 */
	private ProviderConfigurationProcessor() {
		super();
	}

	/**
	 * @param aConf the full configuration for the agent; aConf is never null
	 * @param pConf the full configuration for the entity; eConf can be null
	 * @throws ProviderConfigurationTokenReplacementError
	 */
	public static void process(AgentConfiguration aConf, EntityConfiguration eConf, ProviderCustomConfiguration pConf) throws ProviderConfigurationTokenReplacementError {
		if(pConf == null) {
			return;
		}
		// replace configuration tokens in the custom provider configuration
		Map<String, PropertyEntry> props = pConf.getEntries();
		for(PropertyEntry pe : props.values()) {
			if(pe instanceof PropertyEntryString) {
				String value = (String)pe.getValue();
				if(value != null) {
					boolean found;
					do {
						found = false;
						if(value.indexOf(PROVIDER_CONFIG_TOKEN_TICK) >= 0) {
							value = Utils.replace(value, PROVIDER_CONFIG_TOKEN_TICK,
									eConf == null ?
											aConf.getSamplingInterval().toString() : eConf.getSamplingInterval().toString());
							pConf.setString(pe.getProperty(), value);
							found = true;
						}
						if(value.indexOf(PROVIDER_CONFIG_TOKEN_TICK_MS) >= 0) {
							// get tick in milliseconds
							int tick = (eConf == null ?
									aConf.getSamplingInterval().intValue() : eConf.getSamplingInterval().intValue());
							tick *= 1000;
							value = Utils.replace(value, PROVIDER_CONFIG_TOKEN_TICK_MS, String.valueOf(tick));
							pConf.setString(pe.getProperty(), value);
							found = true;
						}
						int idx = value.indexOf(PROVIDER_CONFIG_TOKEN_AGENT_BEGIN);
						if(idx >= 0) {
							int idx2 = value.indexOf('}', idx);
							TypedProperties aProp = aConf.getAgentCustomConfiguration();
							if(idx2 < 0 || aProp == null) {
								throw new ProviderConfigurationTokenReplacementError(
										PROVIDER_CONFIG_TOKEN_AGENT,
										MessageRepository.get(ProvidersComponent.NAME, pe.getProperty()));
							}

							String agentProperty = null;
							String tokenToReplace = null;
							// find the name of the property required
							// {agent.property} or {agent}
							int idx3 = idx + PROVIDER_CONFIG_TOKEN_AGENT_BEGIN.length();
							char ch = value.charAt(idx3);
							if(ch != '.') {
								if(ch != '}') {
									throw new ProviderConfigurationTokenReplacementError(
											PROVIDER_CONFIG_TOKEN_AGENT,
											MessageRepository.get(ProvidersComponent.NAME, pe.getProperty()));
								} else {
									// get the agent configuration parameter with the same name
									agentProperty = pe.getProperty();
									tokenToReplace = PROVIDER_CONFIG_TOKEN_AGENT;
								}
							} else {
								// get the property name
								agentProperty = value.substring(idx3 + 1, idx2);
								tokenToReplace = PROVIDER_CONFIG_TOKEN_AGENT_BEGIN + "." + agentProperty + "}";
							}
							if(agentProperty != null) {
								Object aObj = aProp.getObject(agentProperty);
								if(aObj == null) {
									throw new ProviderConfigurationTokenReplacementError(
											PROVIDER_CONFIG_TOKEN_AGENT,
											MessageRepository.get(ProvidersComponent.NAME, pe.getProperty()));
								}
								value = Utils.replace(value, tokenToReplace, aObj.toString());
								pConf.setString(pe.getProperty(), value);
								found = true;
							}
						}
						if(value.indexOf(PROVIDER_CONFIG_TOKEN_HOST) >= 0) {
							value = Utils.replace(value, PROVIDER_CONFIG_TOKEN_HOST, aConf.getMonitoredHost());
							pConf.setString(pe.getProperty(), value);
							found = true;
						}
						if(value.indexOf(PROVIDER_CONFIG_APPLICATION_HOME) >= 0) {
							value = Utils.replace(value, PROVIDER_CONFIG_APPLICATION_HOME, Utils.getPath("/"));
							pConf.setString(pe.getProperty(), value);
							found = true;
						}
					} while(found);
				}
			}
		}
	}
}
