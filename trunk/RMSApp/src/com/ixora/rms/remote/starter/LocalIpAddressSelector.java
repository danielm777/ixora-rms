/**
 * 18-Oct-2005
 */
package com.ixora.rms.remote.starter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.util.List;

import com.ixora.common.MessageRepository;
import com.ixora.common.net.NetUtils;
import com.ixora.common.utils.Utils;
import com.ixora.remote.messages.Msg;

/**
 * A utility class that interacts with the user using the standard streams to select
 * one local IP address.
 * @author Daniel Moraru
 */
class LocalIpAddressSelector {

	/**
	 *
	 */
	private LocalIpAddressSelector() {
		super();
	}

	/**
	 * @param args
	 * @throws IOException
	 */
	public static InetAddress select() throws IOException {
        int ipAddressIdxParam = -1;
        List<InetAddress> ips = NetUtils.getAllIpAddresses();
        if(Utils.isEmptyCollection(ips)) {
            System.err.println(MessageRepository.get(
                    Msg.HOST_MANAGER_ERROR_NO_NETWORK_INTERFACES));
            return null;
        }

		InetAddress selectedIpAddress = null;
        if(ips.size() > 1) {
            System.out.println(MessageRepository.get(
                    Msg.HOST_MANAGER_MULTIPLE_IP_ADDRESSES_DETECTED));
            int idx = 0;
            for(InetAddress ip : ips) {
                idx++;
                System.out.println("[" + idx + "] " + ip.toString());
            }
            // run on all
            idx++;
            System.out.println("[" + idx + "] all"); // TODO localize
            int maxIdx = ips.size() + 1;
            if(ipAddressIdxParam <= 0 || ipAddressIdxParam > maxIdx) {
            	// ask user to select ip address index
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                do {
                    System.out.println(MessageRepository.get(
                            Msg.HOST_MANAGER_SELECT_IP_ADDRESS));
                    String input = reader.readLine();
                    try {
                        if(input.toLowerCase().equals("x")) {
                            System.exit(0);
                        }
                        idx = Integer.parseInt(input);
                        if(idx > 0 && idx <= maxIdx) {
                            break;
                        } else {
                            System.err.println(MessageRepository.get(
                                    Msg.HOST_MANAGER_ERROR_INVALID_ENTRY));
                        }
                    } catch(Exception e) {
                        System.err.println(MessageRepository.get(
                                Msg.HOST_MANAGER_ERROR_INVALID_ENTRY));
                    }
                } while(true);
            } else {
            	idx = ipAddressIdxParam;
            }
            idx = --idx;
            if(idx == ips.size()) {
            	// run on all
            	return null;
            }
            selectedIpAddress = ips.get(idx);
        }
        return selectedIpAddress;
	}
}
