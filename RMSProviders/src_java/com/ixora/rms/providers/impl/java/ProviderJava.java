/*
 * Created on 15-Mar-2005
 */
package com.ixora.rms.providers.impl.java;

import com.ixora.rms.exception.RMSException;
import com.ixora.rms.providers.ProviderConfiguration;
import com.ixora.rms.providers.exception.InvalidProviderConfiguration;
import com.ixora.rms.providers.impl.AbstractProvider;
import com.ixora.rms.providers.impl.java.exception.ImplementationClassNotFound;

/**
 * This class must provide a thread safe environment for its JavaProviderImplementation.
 * @author Daniel Moraru
 */
public class ProviderJava extends AbstractProvider {
	/** Implementation instance */
	private JavaProviderImplementation fImpl;

	/**
	 * Context for implementation classes.
	 */
	private final class ImplContext implements JavaProviderImplementationContext {
		/**
		 * @see com.ixora.rms.providers.impl.java.JavaProviderImplementationContext#error(java.lang.String, java.lang.Throwable)
		 */
		public void error(String error, Throwable t) {
			fireError(error, t);
		}
	}

	/**
	 * Constructor.
	 */
	public ProviderJava() {
		super(false);
	}

	/**
	 * @see com.ixora.rms.providers.Provider#configure(com.ixora.rms.providers.ProviderConfiguration)
	 */
	public void configure(ProviderConfiguration conf)
			throws InvalidProviderConfiguration, RMSException, Throwable {
		super.configure(conf);
		// synch with collector
		synchronized(fCollectionLock) {
			Configuration custom = (Configuration)fConfiguration.getProviderCustomConfiguration();
			String clazz = custom.getString(Configuration.IMPLEMENTATION_CLASS);

			// remember: all execptions other then RMSException will be
			// marked as application error, here the provider should try and throw
			// a meaningful error
			try {
				fImpl = (JavaProviderImplementation)Class.forName(clazz).newInstance();
			}catch(InstantiationException e) {
				throw new RMSException(e);
			}catch(IllegalAccessException e) {
				throw new RMSException(e);
			}catch(ClassNotFoundException e) {
				throw new ImplementationClassNotFound(clazz);
			}
			String[] params = custom.getParameters();
			try {
				fImpl.initialize(params, new ImplContext());
			} catch(Exception e) {
				// make sure it wan't be reported as
				// an internal app error as it's external code
				throw new RMSException(e);
			}
		}
	}

	/**
	 * @throws Exception
	 * @see com.ixora.rms.providers.Provider#collectData()
	 */
	public void collectData() throws Exception {
		String[][] table = null;
		// synch with configure
		synchronized(fCollectionLock) {
			if(fImpl != null) {
				try {
					table = fImpl.getValues();
				} catch(Exception e) {
					// make sure it wan't be reported as
					// an internal app error as it's external code
					throw new RMSException(e);
				}
			}
		}
		if(table != null) {
			fireData(table);
		}
	}

	/**
	 * @throws RMSException
	 * @see com.ixora.rms.providers.Provider#cleanup()
	 */
	public void cleanup() throws RMSException {
		synchronized(fCollectionLock) {
			if(fImpl != null) {
				try {
					fImpl.cleanup();
				} catch(Exception e) {
					// make sure it wan't be reported as
					// an internal app error as it's external code
					throw new RMSException(e);
				}
			}
		}
	}
}
