/*
 * Created on 21-Aug-2005
 */
package com.ixora.rms.agents.snmp;

import java.util.List;
import java.util.Vector;

import net.percederberg.mibble.MibType;
import net.percederberg.mibble.MibTypeTag;

import org.snmp4j.AbstractTarget;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.ScopedPDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.UserTarget;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.AuthMD5;
import org.snmp4j.security.AuthSHA;
import org.snmp4j.security.PrivAES128;
import org.snmp4j.security.PrivAES192;
import org.snmp4j.security.PrivAES256;
import org.snmp4j.security.PrivDES;
import org.snmp4j.security.SecurityLevel;
import org.snmp4j.security.SecurityModels;
import org.snmp4j.security.SecurityProtocols;
import org.snmp4j.security.USM;
import org.snmp4j.security.UsmUser;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.Counter32;
import org.snmp4j.smi.Counter64;
import org.snmp4j.smi.Gauge32;
import org.snmp4j.smi.GenericAddress;
import org.snmp4j.smi.Integer32;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.SMIConstants;
import org.snmp4j.smi.TimeTicks;
import org.snmp4j.smi.Variable;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.impl.Counter;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.agents.impl.RootEntity;
import com.ixora.rms.agents.snmp.exceptions.SNMPAgentException;
import com.ixora.rms.agents.snmp.exceptions.SNMPServerUnavailableException;
import com.ixora.rms.agents.snmp.v1.ConfigurationV1;
import com.ixora.rms.agents.snmp.v2c.ConfigurationV2c;
import com.ixora.rms.agents.snmp.v3.ConfigurationV3;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.data.CounterValueString;

/**
 * SNMPRootEntity
 */
public class SNMPRootEntity extends RootEntity {
	private static final long serialVersionUID = -3982291098399860921L;
	private MIBTree fMIBTree;
	private Snmp	fSNMP;
	private AbstractTarget fTargetAddress;

	/**
	 * @return A CounterType mapped from the SNMP type
	 */
	public static CounterType getTypeFromMibType(MibType mibType) {
		if (mibType == null || mibType.getTag() == null) {
			return CounterType.DOUBLE;
		}
		if (mibType.getTag().equals(MibTypeTag.BOOLEAN) ||
			mibType.getTag().equals(MibTypeTag.INTEGER) ||
			mibType.getTag().equals(MibTypeTag.REAL)) {
			return CounterType.DOUBLE;
		} else if (mibType.getTag().equals(MibTypeTag.OCTET_STRING) ||
				mibType.getTag().equals(MibTypeTag.BIT_STRING) ||
				mibType.getTag().equals(MibTypeTag.OBJECT_IDENTIFIER)) {
			return CounterType.STRING;
		} else { // other unknown types
			// MibTypeTag.NULL, MibTypeTag.SET, MibTypeTag.SEQUENCE,
			return CounterType.DOUBLE;
		}
	}

	/**
	 * SNMPCounter
	 */
	private class SNMPCounter extends Counter {
		private static final long serialVersionUID = -76356293704395590L;

		/**
		 * Initializes a SNMPCounter from a MIBNode
		 * @param node
		 */
		public SNMPCounter(MIBNode node) {
			super(node.getName(), node.getDescription(), getTypeFromMibType(node.getType()));
		}

		/**
		 * Updates a counter in place
		 * @param node
		 */
		public void updateData(MIBNode node) {
			fDescription = node.getDescription();
			fType = getTypeFromMibType(node.getType());
		}

		/**
		 * Extracts data from a SNMP Variable and puts it in this counter
		 * @param varValue
		 */
		public void updateValue(Variable varValue) {
			if (varValue == null) {
				return;
			}

			// If we created this counter as a STRING, then regardless of the variable type
			// we will call toString() on it. Otherwise (we have a DOUBLE) we'll determine
			// the variable type and extract a number from it.
			if (getType().equals(CounterType.STRING)) {
				dataReceived(new CounterValueString(varValue.toString()));
			} else {
				// Determine the type of variable received
				switch (varValue.getSyntax()) {
					case SMIConstants.SYNTAX_INTEGER: {
						Integer32 value = (Integer32)varValue;
						dataReceived(new CounterValueDouble(value.getValue()));
						break;
					}
					case SMIConstants.SYNTAX_GAUGE32: {
						Gauge32 value = (Gauge32)varValue;
						dataReceived(new CounterValueDouble(value.getValue()));
						break;
					}
					case SMIConstants.SYNTAX_COUNTER32: {
						Counter32 value = (Counter32)varValue;
						dataReceived(new CounterValueDouble(value.getValue()));
						break;
					}
					case SMIConstants.SYNTAX_COUNTER64: {
						Counter64 value = (Counter64)varValue;
						dataReceived(new CounterValueDouble(value.getValue()));
						break;
					}
					case SMIConstants.SYNTAX_TIMETICKS: {
						// This is in 1/100 of a second, convert to milliseconds
						TimeTicks value = (TimeTicks)varValue;
						dataReceived(new CounterValueDouble(10*value.getValue()));
						break;
					}
					case SMIConstants.SYNTAX_OCTET_STRING: // OctetString value = (OctetString)varValue;
					case SMIConstants.SYNTAX_NULL:
					case SMIConstants.EXCEPTION_END_OF_MIB_VIEW:
					case SMIConstants.EXCEPTION_NO_SUCH_INSTANCE:
					case SMIConstants.EXCEPTION_NO_SUCH_OBJECT: // Null value = (Null)varValue;
					case SMIConstants.SYNTAX_OPAQUE: // Opaque value = (Opaque)varValue;
					case SMIConstants.SYNTAX_IPADDRESS:  // IpAddress value = (IpAddress)varValue;
					case SMIConstants.SYNTAX_OBJECT_IDENTIFIER:  // OID value = (OID)varValue;
					default: {
						// for incompatible types and all other cases: just add a zero.
						dataReceived(new CounterValueDouble(0));
					}
				}
			}
		}
	}

	/**
	 * SNMPEntity
	 */
	private class SNMPEntity extends Entity {
		private static final long serialVersionUID = -5645850176383894773L;
		private MIBNode	fNode;
		private SNMPCounter fValueCounter;
		/**
		 * Constructor
		 * @param parentEntity
		 * @param name
		 * @param node
		 * @param ctx
		 */
		public SNMPEntity(EntityId parentEntity, String name,
				MIBNode node, AgentExecutionContext ctx) {
			super(new EntityId(parentEntity, name),//node.getRelativeOID()),
					name, node.getDescription(), ctx);
			updateData(node);
		}

		/**
		 * Update the node in place
		 * @param node
		 */
		public void updateData(MIBNode node) {
			setTouchedByUpdate(true);
			this.fHasChildren = node.getChildCount() > 0;
			this.fNode = node;
			// One counter always
			if (fValueCounter == null) {
				fValueCounter = new SNMPCounter(node);
				addCounter(fValueCounter);
			} else {
				fValueCounter.updateData(node);
			}
		}

		/**
		 * Uses the cached MIBNode to build children
		 * @see com.ixora.rms.agents.impl.Entity#updateChildrenEntities(boolean)
		 */
		public void updateChildrenEntities(boolean recursive) throws Throwable {
			resetTouchedByUpdateForChildren();
			List<MIBNode> listNodes = fNode.getChildren();
			for (MIBNode node : listNodes) {
				SNMPEntity oldEntity = (SNMPEntity)getChildEntity(
						new EntityId(getId(), node.getName()));
				if(oldEntity == null) {
					addChildEntity(new SNMPEntity(getId(), node.getName(), node, fContext));
				} else {
					oldEntity.updateData(node);
				}
			}
			removeStaleChildren();
		}

		/**
		 * This is where the actual SNMP retrieval is done
		 * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
		 */
		@SuppressWarnings("unchecked")
		protected void retrieveCounterValues() throws Throwable {

			// Create a GET command PDU
			PDU pdu = null;
			if (fTargetAddress.getVersion() == SnmpConstants.version3) {
				ScopedPDU scopedPDU = new ScopedPDU();
				scopedPDU.add(new VariableBinding(new OID(fNode.getOid() + ".0")));
				scopedPDU.setType(PDU.GET);
				pdu = scopedPDU;
			} else {
				pdu = new PDU();
				pdu.add(new VariableBinding(new OID(fNode.getOid() + ".0")));
				pdu.setType(PDU.GET);
			}

			// Send command and wait for response
			ResponseEvent responseEvent = fSNMP.send(pdu, fTargetAddress);
			PDU responsePDU = responseEvent.getResponse();
			if (responsePDU != null) {
				// We only requested one, so this vector has one element
				Vector varBinds = responsePDU.getVariableBindings();
				if (varBinds != null && varBinds.size() > 0) {
					// Get the first one and update our counter
					VariableBinding varValue = (VariableBinding)varBinds.get(0);
					fValueCounter.updateValue(varValue.getVariable());
				}
			} else {
				throw new SNMPServerUnavailableException();
			}
		}
	}

	/**
	 * Constructor
	 * @param ctxt
	 * @throws SNMPAgentException
	 */
	public SNMPRootEntity(AgentExecutionContext ctxt) throws SNMPAgentException {
		super(ctxt);
	}

	/**
	 * @see com.ixora.rms.agents.impl.RootEntity#updateChildrenEntities(boolean)
	 */
	public void updateChildrenEntities(boolean recursive) throws Throwable {
		if (this.getChildrenCount() == 0) {
			// Read the compiled MIB tree (first level). Also merge with some
			// MIBs that user dropped in some predefined folder
			fMIBTree = MIBTree.loadCompiledMIB(fContext);
			// Create entities from the tree. As user expands entities, the same
			// compiled tree is used as a source for entities.
			List<MIBNode> listNodes = fMIBTree.getRootNode().getChildren();
			for (MIBNode node : listNodes) {
	            addChildEntity(new SNMPEntity(getId(), node.getName(), node, fContext));
			}
		}
		super.updateChildrenEntities(recursive);
	}

	private static OctetString createOctetString(String s) {
		if (Utils.isEmptyString(s)) {
			return null;
		}
		OctetString octetString;
		if (s.startsWith("0x")) {
			octetString = OctetString.fromHexString(s.substring(2), ':');
		} else {
			octetString = new OctetString(s);
		}
		return octetString;
	}

	/**
	 * Opens a connection to the SNMP agent
	 */
	public void start() throws Throwable {

		Configuration config = (Configuration)fContext.getAgentConfiguration().getAgentCustomConfiguration();
		int snmpPort = config.getInt(Configuration.PORT_NUMBER);
		int portTimeout = config.getInt(Configuration.PORT_TIMEOUT);
		String hostName = fContext.getAgentConfiguration().getMonitoredHost();

		// Create SNMP interface
		TransportMapping transport = new DefaultUdpTransportMapping();
		fSNMP = new Snmp(transport);

		if (config instanceof ConfigurationV1) {
			// Create a community-based target address
	        Address address = GenericAddress.parse("udp:" + hostName + "/" + snmpPort);
	        CommunityTarget comunityTarget = new CommunityTarget();
	        comunityTarget.setCommunity(new OctetString(config.getString(ConfigurationV2c.COMUNITY)));
	        comunityTarget.setAddress(address);
	        comunityTarget.setVersion(SnmpConstants.version1);
	        comunityTarget.setTimeout(portTimeout);
			fTargetAddress = comunityTarget;

		} else if (config instanceof ConfigurationV2c) {
			// Create a community-based target address
	        Address address = GenericAddress.parse("udp:" + hostName + "/" + snmpPort);
	        CommunityTarget comunityTarget = new CommunityTarget();
	        comunityTarget.setCommunity(new OctetString(config.getString(ConfigurationV2c.COMUNITY)));
	        comunityTarget.setAddress(address);
	        comunityTarget.setVersion(SnmpConstants.version2c);
	        comunityTarget.setTimeout(portTimeout);
			fTargetAddress = comunityTarget;
		} else {
			// Add all security protocols
		    SecurityProtocols.getInstance().addDefaultProtocols();
		    USM usm = new USM(SecurityProtocols.getInstance(),
		    		new OctetString(MPv3.createLocalEngineID()), 0);

			SecurityModels.getInstance().addSecurityModel(usm);
			OctetString authoritativeEngineID = createOctetString(config.getString(ConfigurationV3.AUTH_CONTEXT_ENGINE));

			if (authoritativeEngineID != null) {
				fSNMP.setLocalEngine(authoritativeEngineID.getValue(), 0, 0);
			}

			// Add the configured user to the USM
			OID authProtocol = null;
			String authentication = config.getString(ConfigurationV3.AUTHENTICATION);
	        if (authentication.equals("MD5")) {
	        	authProtocol = AuthMD5.ID;
	        } else if (authentication.equals("SHA")) {
	        	authProtocol = AuthSHA.ID;
	        } else {
	        	authProtocol = null;
	        }

	        OID privProtocol = null;
			String privacy = config.getString(ConfigurationV3.USER_PRIVACY);
	        if (privacy.equals("DES")) {
	        	privProtocol = PrivDES.ID;
	        } else if ((privacy.equals("AES128")) || (privacy.equals("AES"))) {
	        	privProtocol = PrivAES128.ID;
	        } else if (privacy.equals("AES192")) {
	        	privProtocol = PrivAES192.ID;
	        } else if (privacy.equals("AES256")) {
	        	privProtocol = PrivAES256.ID;
	        } else {
	        	privProtocol = null;
	        }

	        OctetString authPassphrase = createOctetString(config.getString(ConfigurationV3.AUTH_PASSWORD));
	        OctetString privPassphrase = createOctetString(config.getString(ConfigurationV3.PRIVACY_PASSWORD));
	        OctetString securityName = createOctetString(config.getString(ConfigurationV3.USER_NAME));

			fSNMP.getUSM().addUser(
					securityName,
					new UsmUser(
							createOctetString(config.getString(ConfigurationV3.USER_NAME)),
							authProtocol,
							authPassphrase,
							privProtocol,
							privPassphrase));

			// Create version 3 target
			UserTarget userTarget = new UserTarget();
			if (authPassphrase != null) {
				if (privPassphrase != null) {
					userTarget.setSecurityLevel(SecurityLevel.AUTH_PRIV);
				} else {
					userTarget.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
				}
			} else {
				userTarget.setSecurityLevel(SecurityLevel.NOAUTH_NOPRIV);
			}
	        Address address = GenericAddress.parse("udp:" + hostName + "/" + snmpPort);
			userTarget.setAddress(address);
			userTarget.setVersion(SnmpConstants.version3);
			userTarget.setTimeout(portTimeout);
			userTarget.setSecurityName(securityName);
			fTargetAddress = userTarget;
		}

        fSNMP.listen();
	}

	/**
	 * Closes connection to SNMP agent and frees resources
	 */
	public void stop() throws Throwable {
		if (fSNMP != null) {
			fSNMP.close();
			fSNMP = null;
		}
	}
}
