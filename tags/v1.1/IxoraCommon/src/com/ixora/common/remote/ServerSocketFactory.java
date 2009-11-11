package com.ixora.common.remote;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.rmi.server.RMIServerSocketFactory;

import com.ixora.common.utils.Utils;

/**
 * RegistryServerSocketFactory
 */
public final class ServerSocketFactory implements Serializable,
        RMIServerSocketFactory {
	private static final long serialVersionUID = -6002450079719973429L;
	private InetAddress fAddress;

    /**
     * @param ipaddress can be null and in this case the server
     * will accept connections on all ip addresses
     */
    public ServerSocketFactory(InetAddress ipaddress) {
        super();
        fAddress = ipaddress;
    }

    /**
     * @see java.rmi.server.RMIServerSocketFactory#createServerSocket(int)
     */
    public ServerSocket createServerSocket(int port) throws IOException {
        return new ServerSocket(port, 1000, fAddress);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ServerSocketFactory)) {
            return false;
        }
        ServerSocketFactory that = (ServerSocketFactory) obj;
        return Utils.equals(this.fAddress, that.fAddress);
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return fAddress == null ? 13 : fAddress.hashCode();
    }
}