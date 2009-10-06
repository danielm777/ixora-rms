package com.ixora.rms.dataengine.reactions;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.codehaus.janino.ScriptEvaluator;
import org.codehaus.janino.Java.CompileException;
import org.codehaus.janino.Parser.ParseException;

import com.ixora.rms.ResourceId;
import com.ixora.common.exception.AppRuntimeException;
import com.ixora.common.utils.Utils;
import com.ixora.rms.client.locator.SessionArtefactInfoLocator;
import com.ixora.rms.data.CounterValue;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.data.CounterValueObject;
import com.ixora.rms.data.ValueObject;
import com.ixora.rms.dataengine.Analyzer;
import com.ixora.rms.dataengine.Resource;
import com.ixora.rms.dataengine.Style;
import com.ixora.rms.dataengine.definitions.ReactionDef;
import com.ixora.rms.exception.NonExistentResourceIdInReactionDef;
import com.ixora.rms.exception.RMSException;
import com.ixora.rms.reactions.ReactionDispatcher;
import com.ixora.rms.reactions.ReactionEvent;
import com.ixora.rms.reactions.ReactionId;

/**
 * Reaction.
 * @author Daniel Moraru
 */
public class Reaction implements Analyzer {
	public interface Listener {
		void reactionsArmed(ReactionId[] rids);
		void reactionsDisarmed(ReactionId[] rids);
		void reactionsFired(ReactionId[] rids);
	}
	// the following are parameters which are set prior to cloning
	/** A reaction can operate on one or more resources or functions, i.e. query results */
	private List<Resource> fParams;
	/** A reaction can operate on one or more counters; this are the regex ones */
	private List<ResourceId> fParamsIdRegex;
	/** Variable names for all resources */
    private String[] fVars;
    /** Types for the variables above */
    private Class[] fTypes;
    /** Identifier */
    private String fIdentifier;
    /** True if in cooling off interval */
    private boolean fCoolingOff;
    /** The start of the current cooling off period */
    private long fCoolingOffStartTime;
	/** Definition */
	private ReactionDef fDef;

   // stuff from the definition
    private String fCodeArm;
    private String fCodeDisarm;
    private String fCodeFire;
    private String fCodeMessage;
    private String fCodeAdvise;
    private String fCodeDelivery;
	private String fSeverity;


	// the following must be recreated after cloning
	/** Script evaluator */
    private transient ScriptEvaluator fScriptEvaluatorArm;
	/** Script evaluator */
    private transient ScriptEvaluator fScriptEvaluatorDisarm;
	/** Script evaluator */
    private transient ScriptEvaluator fScriptEvaluatorFire;
	/** Script evaluator */
    private transient ScriptEvaluator fScriptEvaluatorMessage;
	/** Script evaluator */
    private transient ScriptEvaluator fScriptEvaluatorAdvise;
	/** Script evaluator */
    private transient ScriptEvaluator fScriptEvaluatorDelivery;
    /** Environment for code fragments */
	private transient ReactionEnvironmentImpl fEnvironment;
	/** Reaction cool off period */
	private transient int fCoolOffPeriod;
	/** Listener */
	private transient Listener fListener;

	/**
	 * @param rd
	 * @param resources
	 * @param ridContext
	 */
	public Reaction(ReactionDef rd, List<Resource> resources, ResourceId ridContext) {
		super();
		fDef = rd;
		fIdentifier = rd.geIdentifier();
		fParams = new LinkedList<Resource>();
		fParamsIdRegex = new LinkedList<ResourceId>();
		List<String> varNames = new LinkedList<String>();
		// create parameters and complete relative ResourceIDs
		for(String param : rd.getParameters()) {
		    // look for the resource with ID specified by this parameter
			for(Resource resource : resources){
				String id = resource.getStyle().getID();
		        if(param.equals(id)) {
		            // add parameter
		    		ResourceId resourceID = resource.getResourceID().complete(ridContext);
					fParams.add(resource);
					fParamsIdRegex.add(resourceID);
					varNames.add(id);
		            break;
		        }
		    }
		}

		fCodeArm = processCode(varNames, rd.getArmCode());
		fCodeFire = processCode(varNames, rd.getFireCode());
		fCodeDisarm = processCode(varNames, rd.getDisarmCode());
		fCodeMessage = rd.getMessageCode();
		if(fCodeMessage != null) {
			fCodeMessage = processCode(varNames, fCodeMessage);
		}
		fCodeAdvise = rd.getAdviseCode();
		if(fCodeAdvise != null) {
			fCodeAdvise = processCode(varNames, fCodeAdvise);
		}
		fCodeDelivery = rd.getDeliveryCode();
		if(fCodeDelivery != null) {
			fCodeDelivery = processCode(varNames, fCodeDelivery);
		}
		fSeverity = rd.getSeverity();

		int size = fParams.size();
		// make way for ReactionEnvironment
		fVars = new String[size + 1];
        fTypes = new Class[size + 1];
        int i = 0;
        for(Resource resource : fParams) {
        	Style style = resource.getStyle();
            fVars[i] = style.getID();
            String type = style.getType();
            // determine variable type
            if(type == null) {
            	fTypes[i] = Double.TYPE;
            } else if (Style.TYPE_STRING.equalsIgnoreCase(type)) {
                fTypes[i] = String.class;
            } else if (Style.TYPE_NUMBER.equalsIgnoreCase(type)) {
                fTypes[i] = Double.TYPE;
            } else if (Style.TYPE_DATE.equalsIgnoreCase(type)) {
                fTypes[i] = Date.class;
            } else if (Style.TYPE_OBJECT.equalsIgnoreCase(type)) {
            	fTypes[i] = ValueObject.class;
            } else {
                fTypes[i] = Double.TYPE;
            }
            i++;
        }
	}

	/**
	 * @param codeMessage
	 * @return
	 */
	private String processCode(List<String> varNames, String codeMessage) {
		int i = 0;
		String ret = codeMessage;
		for(String varName : varNames) {
			String rid = fParamsIdRegex.get(i).toString();
			ret = ret.replaceAll("getPath[\\s]*\\([\\s]*" + varName + "[\\s]*\\)", "getPath(\"" + rid + "\")");
			ret = ret.replaceAll("getHost[\\s]*\\([\\s]*" + varName + "[\\s]*\\)", "getHost(\"" + rid + "\")");
			ret = ret.replaceAll("getAgent[\\s]*\\([\\s]*" + varName + "[\\s]*\\)", "getAgent(\"" + rid + "\")");
			ret = ret.replaceAll("getCounter[\\s]*\\([\\s]*" + varName + "[\\s]*\\)", "getCounter(\"" + rid + "\")");
			ret = ret.replaceAll("getEntityPath[\\s]*\\([\\s]*" + varName + "[\\s]*\\)", "getEntityPath(\"" + rid + "\")");
			ret = ret.replaceAll("getEntityPart[\\s]*\\([\\s]*" + varName + "[\\s]*,", "getEntityPart(\"" + rid + "\",");
			++i;
		}
		return ret;
	}

	/**
	 *
	 */
	private void initScriptEvaluators() {
        // compile the code
    	try {
    		fScriptEvaluatorArm = new ScriptEvaluator(fCodeArm, Boolean.TYPE, fVars, fTypes);
		} catch(CompileException e) {
			throw new AppRuntimeException("Compilation problem in arm code: " + e.getMessage());
		} catch(ParseException e) {
			throw new AppRuntimeException("Code parsing problem in arm code: " + e.getMessage());
		} catch(Exception e) {
			throw new AppRuntimeException("Error in arm code", e);
		}
		try {
			fScriptEvaluatorDisarm = new ScriptEvaluator(fCodeDisarm, Boolean.TYPE, fVars, fTypes);
		} catch(CompileException e) {
			throw new AppRuntimeException("Compilation problem in disarm code: " + e.getMessage());
		} catch(ParseException e) {
			throw new AppRuntimeException("Code parsing problem in disarm code: " + e.getMessage());
		} catch(Exception e) {
			throw new AppRuntimeException("Error in disarm code", e);
		}

		try {
			fScriptEvaluatorFire = new ScriptEvaluator(fCodeFire, Boolean.TYPE, fVars, fTypes);
		} catch(CompileException e) {
			throw new AppRuntimeException("Compilation problem in fire code: " + e.getMessage());
		} catch(ParseException e) {
			throw new AppRuntimeException("Code parsing problem in fire code: " + e.getMessage());
		} catch(Exception e) {
			throw new AppRuntimeException("Error in fire code", e);
		}


		// message is optional
		if(!Utils.isEmptyString(fCodeMessage)) {
			try {
				fScriptEvaluatorMessage = new ScriptEvaluator(fCodeMessage, String.class, fVars, fTypes);
    		} catch(CompileException e) {
    			throw new AppRuntimeException("Compilation problem in message code: " + e.getMessage());
    		} catch(ParseException e) {
    			throw new AppRuntimeException("Code parsing problem in message code: " + e.getMessage());
    		} catch(Exception e) {
    			throw new AppRuntimeException("Error in message code", e);
    		}
		}
		// delivery is optional
		if(!Utils.isEmptyString(fCodeDelivery)) {
			try {
				fScriptEvaluatorDelivery = new ScriptEvaluator(fCodeDelivery, Void.TYPE, fVars, fTypes);
    		} catch(CompileException e) {
    			throw new AppRuntimeException("Compilation problem in delivery code: " + e.getMessage());
    		} catch(ParseException e) {
    			throw new AppRuntimeException("Code parsing problem in delivery code: " + e.getMessage());
    		} catch(Exception e) {
    			throw new AppRuntimeException("Error in delivery code", e);
    		}
		}
		// advise if optional
		if(!Utils.isEmptyString(fCodeAdvise)) {
			try {
				fScriptEvaluatorAdvise = new ScriptEvaluator(fCodeAdvise, String.class, fVars, fTypes);
    		} catch(CompileException e) {
    			throw new AppRuntimeException("Compilation problem in advise code: " + e.getMessage());
    		} catch(ParseException e) {
    			throw new AppRuntimeException("Code parsing problem in advise code: " + e.getMessage());
    		} catch(Exception e) {
    			throw new AppRuntimeException("Error in advise code", e);
    		}
		}
	}

	/**
	 * @see com.ixora.rms.dataengine.Analyzer#getAnalyzedResources()
	 */
	public List<ResourceId> getAnalyzedResources() {
		return Collections.unmodifiableList(fParamsIdRegex);
	}

	/**
	 * @throws InvocationTargetException
	 * @see com.ixora.rms.dataengine.Analyzer#analyze(java.util.List)
	 */
	public void analyze(List<CounterValue> values) throws InvocationTargetException {
		// if in cooling off period, skip this entire processing
		if(!fCoolingOff) {
			// prepare parameters, make way for ReactionEnvironment
			Object[] params = new Object[values.size() + 1];
			int i = 0;
			for(CounterValue val : values) {
				Class type = fTypes[i];
				if(val instanceof CounterValueDouble ) {
					params[i] = new Double(val.getDouble());
					if(type == Date.class) {
						params[i] = new Date((long)val.getDouble());
					}
				} else {
					if(val instanceof CounterValueObject
							&& type == ValueObject.class) {
						params[i] = ((CounterValueObject)val).getValue();
					} else {
						params[i] = val.toString();
					}
				}
				++i;
			}
			params[i] = this.fEnvironment;
			if(fEnvironment.isArmed()) {
				Boolean disarmed = (Boolean)fScriptEvaluatorDisarm.evaluate(params);
				if(disarmed) {
					ReactionId[] rids = fEnvironment.getReactionIds();
					fEnvironment.disarmed();
					if(!Utils.isEmptyArray(rids)) {
						fListener.reactionsDisarmed(rids);
					}
				} else {
					Boolean fire = (Boolean)fScriptEvaluatorFire.evaluate(params);
					if(fire) {
						ReactionId[] rids = fEnvironment.getReactionIds();
						fEnvironment.fired();
						fCoolingOff = true;
						fCoolingOffStartTime = System.currentTimeMillis();
						if(!Utils.isEmptyArray(rids)) {
							fListener.reactionsFired(rids);
						}
					}
				}
			}
			if(!fCoolingOff) {
				Boolean armed = (Boolean)fScriptEvaluatorArm.evaluate(params);
				if(armed) {
					if(!fEnvironment.isArmed()) {
						// evaluate arm stuff just once
						String message = null;
						if(fScriptEvaluatorMessage != null) {
							message = (String)fScriptEvaluatorMessage.evaluate(params);
							ReactionEvent event = new ReactionEvent(message, null, fSeverity);
							// set the event before invoking delivery
							fEnvironment.setReactionEvent(event);
							// this will deliver reactions
							if(fScriptEvaluatorDelivery != null) {
								fScriptEvaluatorDelivery.evaluate(params);
							}
						}
						// handle advise
						if(fScriptEvaluatorAdvise != null) {
							String advice = (String)fScriptEvaluatorAdvise.evaluate(params);
							if(message != null) {
								advice = message + " - " + advice;
							}
							if(!Utils.isEmptyString(advice)) {
								fEnvironment.advise(new ReactionEvent(advice, null, null));
							}
						}
						fEnvironment.armed();
						ReactionId[] rids = fEnvironment.getReactionIds();
						if(!Utils.isEmptyArray(rids)) {
							fListener.reactionsArmed(rids);
						}
					} else {
						// environment already armed, just needs a trigger to update itself
						fEnvironment.armed();
					}
				}
			}
		} else {
			// in cooling off; is that finished?
			if(System.currentTimeMillis() - fCoolingOffStartTime > fCoolOffPeriod) {
				fCoolingOff = false;
				fCoolingOffStartTime = 0;
			}
		}
	}

	/**
	 * @param matchedRids
	 * @param listener
	 * @param dispatcher
	 * @param locator
	 * @param coolOff
	 */
	public void initializeInstance(
			List<ResourceId> matchedRids,
			Listener listener,
			ReactionDispatcher dispatcher,
			SessionArtefactInfoLocator locator,
			int coolOff) {
		fListener = listener;
		fEnvironment = new ReactionEnvironmentImpl(matchedRids, dispatcher, locator);
        fVars[fVars.length - 1] = "env";
        fTypes[fTypes.length - 1] = ReactionEnvironment.class;
        fCoolOffPeriod = coolOff * 1000; // need this in ms
        initScriptEvaluators();
	}

	/**
	 * @see com.ixora.rms.dataengine.Analyzer#getID()
	 */
	public String getID() {
		return fIdentifier;
	}

	/**
	 * @see com.ixora.rms.dataengine.Analyzer#initializeInstance(List)
	 */
	public void initializeInstance(List<ResourceId> matchedRids) {
		; // no need to be implemented for Reactions as they are treated
		// in particular and initializeInstance(ReactionDispatcher, SessionArtefactInfoLocator...)
		// will be invoked instead
	}

	/**
	 *
	 * @param rids
	 * @throws RMSException
	 */
	public void test(Set<String> rids) throws RMSException {
		// check firs that the params are in the given rids set
		Set<String> ps = fDef.getParameters();
		for(String p : ps) {
			if(!rids.contains(p)) {
				throw new NonExistentResourceIdInReactionDef(p);
			}
		}
		// init scripts
		initializeInstance(null, null, null, null, 0);
	}
}
