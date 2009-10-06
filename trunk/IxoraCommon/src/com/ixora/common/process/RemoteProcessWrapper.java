/**
 * 13-Mar-2006
 */
package com.ixora.common.process;

/**
 * @author Daniel Moraru
 */
public interface RemoteProcessWrapper extends ProcessWrapper {
	public interface Listener extends ProcessWrapper.Listener {
		/**
		 * Because the remote process wrappers tend to have the <code>start()</code> method
		 * implemented asynch, this method provides feedback on errors.
		 */
		void remoteError(Exception ex);
	}
}
