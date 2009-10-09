package com.ixora.common.remote;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;

/**
 * RegistryClientSocketFactory
 */
public final class ClientSocketFactory implements Serializable,
        RMIClientSocketFactory {
	private static final long serialVersionUID = 6338478596482677418L;
	private InetAddress fAddress;

    /**
     * @param ipaddress
     */
    public ClientSocketFactory(InetAddress ipaddress) {
        super();
        fAddress = ipaddress;
    }

    /**
     * @see java.rmi.server.RMIClientSocketFactory#createSocket(java.lang.String,
     *      int)
     */
    public Socket createSocket(String host, int port) throws IOException {
        return new Socket(fAddress, port);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ClientSocketFactory)) {
            return false;
        }
        ClientSocketFactory that = (ClientSocketFactory) obj;
        return this.fAddress.equals(that.fAddress);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return fAddress.hashCode();
    }
}
