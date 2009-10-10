/**
 * 03-Sep-2005
 */
package com.ixora.rms.agents.impl.jmx;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;

/**
 * Proxy for objects implementing MBeanServer methods.
 * @author Daniel Moraru
 */
public class MBeanServerProxy implements InvocationHandler {
	/** Proxied server instance */
	private Object fServer;

	/**
	 *
	 */
	public MBeanServerProxy(Object server) {
		super();
		fServer = server;
	}

	/**
	 * @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[])
	 */
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> serverClass = fServer.getClass();
        Method m = serverClass.getMethod(method.getName(), (Class[])method.getParameterTypes());
        return m.invoke(fServer, args);
	}

	/**
	 * @param server
	 * @return
	 */
    public static MBeanServer buildProxy(Object server) {
        Object proxy = Proxy.newProxyInstance(
        		MBeanServerProxy.class.getClassLoader(),
        		new Class[]{javax.management.MBeanServer.class},
        		new MBeanServerProxy(server));
        return (MBeanServer)proxy;
    }

	/**
	 * @param server
	 * @param cl
	 * @return
	 */
    public static MBeanServerConnection buildProxy(Object server, ClassLoader cl) {
        Object proxy = Proxy.newProxyInstance(
        		cl,
        		new Class[]{MBeanServerConnection.class},
        		new MBeanServerProxy(server));
        return (MBeanServerConnection)proxy;
    }
}
