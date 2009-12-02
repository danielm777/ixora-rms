// Stub class generated by rmic, do not edit.
// Contents subject to change without notice.

package com.ixora.rms.remote.agents;

public final class RemoteAgentManagerImpl_Stub
    extends java.rmi.server.RemoteStub
    implements com.ixora.rms.remote.agents.RemoteAgentManager, java.rmi.Remote
{
    private static final long serialVersionUID = 2;
    
    private static java.lang.reflect.Method $method_activateAgent_0;
    private static java.lang.reflect.Method $method_configureAgent_1;
    private static java.lang.reflect.Method $method_configureAllAgents_2;
    private static java.lang.reflect.Method $method_configureEntity_3;
    private static java.lang.reflect.Method $method_deactivateAgent_4;
    private static java.lang.reflect.Method $method_deactivateAllAgents_5;
    private static java.lang.reflect.Method $method_getAgentPollBuffer_6;
    private static java.lang.reflect.Method $method_getAgentState_7;
    private static java.lang.reflect.Method $method_getEntities_8;
    private static java.lang.reflect.Method $method_initialize_9;
    private static java.lang.reflect.Method $method_shutdown_10;
    private static java.lang.reflect.Method $method_startAgent_11;
    private static java.lang.reflect.Method $method_startAllAgents_12;
    private static java.lang.reflect.Method $method_stopAgent_13;
    private static java.lang.reflect.Method $method_stopAllAgents_14;
    
    static {
	try {
	    $method_activateAgent_0 = com.ixora.rms.remote.agents.RemoteAgentManager.class.getMethod("activateAgent", new java.lang.Class[] {java.lang.String.class, com.ixora.rms.agents.AgentConfiguration.class});
	    $method_configureAgent_1 = com.ixora.rms.remote.agents.RemoteAgentManager.class.getMethod("configureAgent", new java.lang.Class[] {com.ixora.rms.agents.AgentId.class, com.ixora.rms.agents.AgentConfiguration.class});
	    $method_configureAllAgents_2 = com.ixora.rms.remote.agents.RemoteAgentManager.class.getMethod("configureAllAgents", new java.lang.Class[] {com.ixora.rms.agents.AgentConfiguration.class});
	    $method_configureEntity_3 = com.ixora.rms.remote.agents.RemoteAgentManager.class.getMethod("configureEntity", new java.lang.Class[] {com.ixora.rms.agents.AgentId.class, com.ixora.rms.EntityId.class, com.ixora.rms.EntityConfiguration.class});
	    $method_deactivateAgent_4 = com.ixora.rms.remote.agents.RemoteAgentManager.class.getMethod("deactivateAgent", new java.lang.Class[] {com.ixora.rms.agents.AgentId.class});
	    $method_deactivateAllAgents_5 = com.ixora.rms.remote.agents.RemoteAgentManager.class.getMethod("deactivateAllAgents", new java.lang.Class[] {});
	    $method_getAgentPollBuffer_6 = com.ixora.rms.remote.agents.RemoteAgentManager.class.getMethod("getAgentPollBuffer", new java.lang.Class[] {});
	    $method_getAgentState_7 = com.ixora.rms.remote.agents.RemoteAgentManager.class.getMethod("getAgentState", new java.lang.Class[] {com.ixora.rms.agents.AgentId.class});
	    $method_getEntities_8 = com.ixora.rms.remote.agents.RemoteAgentManager.class.getMethod("getEntities", new java.lang.Class[] {com.ixora.rms.agents.AgentId.class, com.ixora.rms.EntityId.class, boolean.class, boolean.class});
	    $method_initialize_9 = com.ixora.remote.RemoteManaged.class.getMethod("initialize", new java.lang.Class[] {java.lang.String.class, com.ixora.remote.RemoteManagedListener.class});
	    $method_shutdown_10 = com.ixora.common.remote.Shutdownable.class.getMethod("shutdown", new java.lang.Class[] {});
	    $method_startAgent_11 = com.ixora.rms.remote.agents.RemoteAgentManager.class.getMethod("startAgent", new java.lang.Class[] {com.ixora.rms.agents.AgentId.class});
	    $method_startAllAgents_12 = com.ixora.rms.remote.agents.RemoteAgentManager.class.getMethod("startAllAgents", new java.lang.Class[] {});
	    $method_stopAgent_13 = com.ixora.rms.remote.agents.RemoteAgentManager.class.getMethod("stopAgent", new java.lang.Class[] {com.ixora.rms.agents.AgentId.class});
	    $method_stopAllAgents_14 = com.ixora.rms.remote.agents.RemoteAgentManager.class.getMethod("stopAllAgents", new java.lang.Class[] {});
	} catch (java.lang.NoSuchMethodException e) {
	    throw new java.lang.NoSuchMethodError(
		"stub class initialization failed");
	}
    }
    
    // constructors
    public RemoteAgentManagerImpl_Stub(java.rmi.server.RemoteRef ref) {
	super(ref);
    }
    
    // methods from remote interfaces
    
    // implementation of activateAgent(String, AgentConfiguration)
    public com.ixora.rms.agents.AgentActivationTuple activateAgent(java.lang.String $param_String_1, com.ixora.rms.agents.AgentConfiguration $param_AgentConfiguration_2)
	throws com.ixora.rms.exception.AgentIsNotInstalled, com.ixora.rms.exception.InvalidAgentState, com.ixora.rms.exception.InvalidConfiguration, com.ixora.rms.exception.RMSException, java.rmi.RemoteException
    {
	try {
	    Object $result = ref.invoke(this, $method_activateAgent_0, new java.lang.Object[] {$param_String_1, $param_AgentConfiguration_2}, -3763306355577235602L);
	    return ((com.ixora.rms.agents.AgentActivationTuple) $result);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (com.ixora.rms.exception.RMSException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of configureAgent(AgentId, AgentConfiguration)
    public com.ixora.rms.agents.AgentConfigurationTuple configureAgent(com.ixora.rms.agents.AgentId $param_AgentId_1, com.ixora.rms.agents.AgentConfiguration $param_AgentConfiguration_2)
	throws com.ixora.rms.exception.InvalidAgentState, com.ixora.rms.exception.InvalidConfiguration, com.ixora.rms.exception.RMSException, java.rmi.RemoteException
    {
	try {
	    Object $result = ref.invoke(this, $method_configureAgent_1, new java.lang.Object[] {$param_AgentId_1, $param_AgentConfiguration_2}, -4786323040615493880L);
	    return ((com.ixora.rms.agents.AgentConfigurationTuple) $result);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (com.ixora.rms.exception.RMSException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of configureAllAgents(AgentConfiguration)
    public com.ixora.rms.agents.AgentConfigurationTuple[] configureAllAgents(com.ixora.rms.agents.AgentConfiguration $param_AgentConfiguration_1)
	throws com.ixora.rms.exception.InvalidConfiguration, com.ixora.rms.exception.RMSException, java.rmi.RemoteException
    {
	try {
	    Object $result = ref.invoke(this, $method_configureAllAgents_2, new java.lang.Object[] {$param_AgentConfiguration_1}, 6437754902674216829L);
	    return ((com.ixora.rms.agents.AgentConfigurationTuple[]) $result);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (com.ixora.rms.exception.RMSException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of configureEntity(AgentId, EntityId, EntityConfiguration)
    public com.ixora.rms.EntityDescriptorTree configureEntity(com.ixora.rms.agents.AgentId $param_AgentId_1, com.ixora.rms.EntityId $param_EntityId_2, com.ixora.rms.EntityConfiguration $param_EntityConfiguration_3)
	throws com.ixora.rms.exception.InvalidAgentState, com.ixora.rms.exception.InvalidConfiguration, com.ixora.rms.exception.InvalidEntity, com.ixora.rms.exception.RMSException, java.rmi.RemoteException
    {
	try {
	    Object $result = ref.invoke(this, $method_configureEntity_3, new java.lang.Object[] {$param_AgentId_1, $param_EntityId_2, $param_EntityConfiguration_3}, 8208631676677200406L);
	    return ((com.ixora.rms.EntityDescriptorTree) $result);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (com.ixora.rms.exception.RMSException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of deactivateAgent(AgentId)
    public void deactivateAgent(com.ixora.rms.agents.AgentId $param_AgentId_1)
	throws com.ixora.rms.exception.RMSException, java.rmi.RemoteException
    {
	try {
	    ref.invoke(this, $method_deactivateAgent_4, new java.lang.Object[] {$param_AgentId_1}, -5263547234363679190L);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (com.ixora.rms.exception.RMSException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of deactivateAllAgents()
    public void deactivateAllAgents()
	throws com.ixora.rms.exception.RMSException, java.rmi.RemoteException
    {
	try {
	    ref.invoke(this, $method_deactivateAllAgents_5, null, 8807807175600483868L);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (com.ixora.rms.exception.RMSException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of getAgentPollBuffer()
    public com.ixora.rms.agents.AgentPollBuffer getAgentPollBuffer()
	throws com.ixora.rms.exception.RMSException, java.rmi.RemoteException
    {
	try {
	    Object $result = ref.invoke(this, $method_getAgentPollBuffer_6, null, -7070409447263113559L);
	    return ((com.ixora.rms.agents.AgentPollBuffer) $result);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (com.ixora.rms.exception.RMSException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of getAgentState(AgentId)
    public com.ixora.rms.agents.AgentState getAgentState(com.ixora.rms.agents.AgentId $param_AgentId_1)
	throws com.ixora.rms.exception.RMSException, java.rmi.RemoteException
    {
	try {
	    Object $result = ref.invoke(this, $method_getAgentState_7, new java.lang.Object[] {$param_AgentId_1}, 9208857603233970617L);
	    return ((com.ixora.rms.agents.AgentState) $result);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (com.ixora.rms.exception.RMSException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of getEntities(AgentId, EntityId, boolean, boolean)
    public com.ixora.rms.EntityDescriptorTree getEntities(com.ixora.rms.agents.AgentId $param_AgentId_1, com.ixora.rms.EntityId $param_EntityId_2, boolean $param_boolean_3, boolean $param_boolean_4)
	throws com.ixora.rms.exception.RMSException, java.rmi.RemoteException
    {
	try {
	    Object $result = ref.invoke(this, $method_getEntities_8, new java.lang.Object[] {$param_AgentId_1, $param_EntityId_2, ($param_boolean_3 ? java.lang.Boolean.TRUE : java.lang.Boolean.FALSE), ($param_boolean_4 ? java.lang.Boolean.TRUE : java.lang.Boolean.FALSE)}, -3215341274639232484L);
	    return ((com.ixora.rms.EntityDescriptorTree) $result);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (com.ixora.rms.exception.RMSException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of initialize(String, RemoteManagedListener)
    public void initialize(java.lang.String $param_String_1, com.ixora.remote.RemoteManagedListener $param_RemoteManagedListener_2)
	throws com.ixora.remote.exception.RemoteManagedListenerIsUnreachable, com.ixora.rms.exception.RMSException, java.rmi.RemoteException
    {
	try {
	    ref.invoke(this, $method_initialize_9, new java.lang.Object[] {$param_String_1, $param_RemoteManagedListener_2}, -9086994201516082088L);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (com.ixora.rms.exception.RMSException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of shutdown()
    public void shutdown()
	throws com.ixora.common.exception.AppException, java.rmi.RemoteException
    {
	try {
	    ref.invoke(this, $method_shutdown_10, null, -7207851917985848402L);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (com.ixora.common.exception.AppException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of startAgent(AgentId)
    public void startAgent(com.ixora.rms.agents.AgentId $param_AgentId_1)
	throws com.ixora.rms.exception.InvalidAgentState, com.ixora.rms.exception.RMSException, java.rmi.RemoteException
    {
	try {
	    ref.invoke(this, $method_startAgent_11, new java.lang.Object[] {$param_AgentId_1}, 1970487471133456205L);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (com.ixora.rms.exception.RMSException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of startAllAgents()
    public void startAllAgents()
	throws com.ixora.rms.exception.RMSException, java.rmi.RemoteException
    {
	try {
	    ref.invoke(this, $method_startAllAgents_12, null, 69502513686436104L);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (com.ixora.rms.exception.RMSException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of stopAgent(AgentId)
    public void stopAgent(com.ixora.rms.agents.AgentId $param_AgentId_1)
	throws com.ixora.rms.exception.InvalidAgentState, com.ixora.rms.exception.RMSException, java.rmi.RemoteException
    {
	try {
	    ref.invoke(this, $method_stopAgent_13, new java.lang.Object[] {$param_AgentId_1}, -4358107364457862284L);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (com.ixora.rms.exception.RMSException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
    
    // implementation of stopAllAgents()
    public void stopAllAgents()
	throws com.ixora.rms.exception.RMSException, java.rmi.RemoteException
    {
	try {
	    ref.invoke(this, $method_stopAllAgents_14, null, -271878342283552514L);
	} catch (java.lang.RuntimeException e) {
	    throw e;
	} catch (java.rmi.RemoteException e) {
	    throw e;
	} catch (com.ixora.rms.exception.RMSException e) {
	    throw e;
	} catch (java.lang.Exception e) {
	    throw new java.rmi.UnexpectedException("undeclared checked exception", e);
	}
    }
}