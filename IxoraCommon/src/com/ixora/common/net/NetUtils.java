package com.ixora.common.net;

import java.io.IOException;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

import com.ixora.common.utils.Utils;

/**
 * Net utils.
 * @author Daniel Moraru
 */
public final class NetUtils {
    /** Ip address of the localhost */
    public static final String LOCALHOST_IP = "127.0.0.1";

    /**
	 * Constructor for NetUtils.
	 */
	private NetUtils() {
		super();
	}

	/**
	 * Sends an email message.
	 * @param server The name of the SMTP server to use
	 * @param port The port of the SMTP server
	 * @param to The value of the To: field
	 * @param from The value of the From: field
	 * @param subject The subject of the message
	 * @param message The message
	 */
	public static void sendEmail(
					String server, int port,
					String to, String from,
					String subject, String message) throws MessagingException {
		Properties props = new Properties();
		props.put("mail.smtp.host", server);
		props.put("mail.smtp.port", String.valueOf(port));
		Session session = Session.getInstance(props, null);
	    Message msg = new MimeMessage(session);
	    msg.setFrom(new InternetAddress(from));
	    InternetAddress[] address = {new InternetAddress(to)};
	    msg.setRecipients(Message.RecipientType.TO, address);
	    msg.setSubject(subject);
	    msg.setSentDate(new Date());
	    msg.setText(message);
	    Transport.send(msg);
	}

	/**
	 * Post the given data using a POST HTTP request.
	 * @param url
	 * @param dataInBody Parameters sent in the body of the POST request
	 * @param parameters Parameters sent in the URL of the POST request
	 * @throws IOException
	 * @throws HttpException
	 * @return the PostMethod
	 */
	public static PostMethod postHttpForm(URL url, NameValuePair[] dataInBody, NameValuePair[] parameters) throws HttpException, IOException {
		HttpClient client = new HttpClient();
	    PostMethod httppost = new PostMethod(url.toString());
	    httppost.setRequestHeader("User-Agent", "redbox-agent");
	    httppost.setRequestBody(dataInBody);
	    if(!Utils.isEmptyArray(parameters)) {
	    	for(NameValuePair nvp : parameters) {
	    		httppost.addParameter(nvp);
	    	}
	    }
	    try {
	        client.executeMethod(httppost);
	        return httppost;
	    } finally {
	        httppost.releaseConnection();
	    }
	}

    /**
     * @return all ip addresses assigned to all network interfaces on this host
     * @throws SocketException
     */
    public static List<InetAddress> getAllIpAddresses() throws SocketException {
        Enumeration<NetworkInterface> nis = NetworkInterface.getNetworkInterfaces();
        if(nis == null) {
            return null;
        }
        List<InetAddress> ips = new LinkedList<InetAddress>();
        while(nis.hasMoreElements()) {
            NetworkInterface ni = nis.nextElement();
            ips.addAll(Collections.list(ni.getInetAddresses()));
        }
        return ips;
    }

    /**
     * @return
     * @throws SocketException
     */
    public static List<InetAddress> getAllPublicIpAddresses() throws SocketException {
        List<InetAddress> ips = getAllIpAddresses();
        if(Utils.isEmptyCollection(ips)) {
            return null;
        }
        for(Iterator<InetAddress> iter = ips.iterator(); iter.hasNext();) {
            if(iter.next().getHostAddress().indexOf(LOCALHOST_IP) != -1) {
                iter.remove();
            }
        }
        return ips;
    }
}
