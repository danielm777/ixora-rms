/**
 * 25-Dec-2005
 */
package com.ixora.rms.agents.url;

import java.net.URL;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import com.ixora.common.utils.Utils;
import com.ixora.rms.CounterId;
import com.ixora.rms.CounterType;
import com.ixora.rms.EntityId;
import com.ixora.rms.agents.AgentExecutionContext;
import com.ixora.rms.agents.impl.Counter;
import com.ixora.rms.agents.impl.Entity;
import com.ixora.rms.agents.url.messages.Msg;
import com.ixora.rms.data.CounterValueDouble;
import com.ixora.rms.data.CounterValueString;
import com.ixora.rms.exception.RMSException;

/**
 * @author Daniel Moraru
 */
public class URLEntity extends Entity {
	private static final long serialVersionUID = 2896158725798301422L;
	private HttpClient fClient;
	private HttpMethod fHttpMethod;

	/**
	 * @param id
	 * @param file
	 * @param c
	 */
	public URLEntity(EntityId parent, URL url, AgentExecutionContext c) {
		super(new EntityId(parent, url.toExternalForm()), c);
		fClient = new HttpClient();
		addCounter(new Counter(Msg.COUNTER_RESPONSE_TIME,
				Msg.COUNTER_RESPONSE_TIME + ".description", CounterType.LONG));
		addCounter(new Counter(Msg.COUNTER_RESPONSE_SIZE,
				Msg.COUNTER_RESPONSE_SIZE + ".description", CounterType.LONG));
		addCounter(new Counter(Msg.COUNTER_RESPONSE_CONTENT,
				Msg.COUNTER_RESPONSE_CONTENT + ".description", CounterType.STRING));
		addCounter(new Counter(Msg.COUNTER_RESPONSE_STATUS_CODE,
				Msg.COUNTER_RESPONSE_STATUS_CODE + ".description", CounterType.LONG));
	}

	/**
	 * @see com.ixora.rms.agents.impl.Entity#retrieveCounterValues()
	 */
	protected void retrieveCounterValues() throws Throwable {
		if(fHttpMethod == null) {
			initFromAgentConfig();
		}
		long responseTime = 0;
		int responseSize = 0;
		String responseContent = "";
		int responseStatusCode = 0;
	    try {
	    	long time = System.currentTimeMillis();
	        fClient.executeMethod(fHttpMethod);
	        responseStatusCode = fHttpMethod.getStatusCode();
	        if(responseStatusCode == HttpStatus.SC_OK) {
	            responseContent = fHttpMethod.getResponseBodyAsString();
	        }
	        byte[] bytes = fHttpMethod.getResponseBody();
	        if(bytes != null) {
	        	responseSize = bytes.length;
	        }
	        responseTime = System.currentTimeMillis() - time;
	    } finally {
	        fHttpMethod.releaseConnection();
	    }

	    Counter counter = getCounter(new CounterId(Msg.COUNTER_RESPONSE_TIME));
		if(counter.isEnabled()) {
			counter.dataReceived(new CounterValueDouble(responseTime));
		}
		counter = getCounter(new CounterId(Msg.COUNTER_RESPONSE_SIZE));
		if(counter.isEnabled()) {
			counter.dataReceived(new CounterValueDouble(responseSize));
		}
		counter = getCounter(new CounterId(Msg.COUNTER_RESPONSE_STATUS_CODE));
		if(counter.isEnabled()) {
			counter.dataReceived(new CounterValueDouble(responseStatusCode));
		}
		counter = getCounter(new CounterId(Msg.COUNTER_RESPONSE_CONTENT));
		if(counter.isEnabled()) {
			counter.dataReceived(new CounterValueString(responseContent));
		}
	}

	/**
	 * @throws RMSException
	 */
	private void initFromAgentConfig() throws RMSException {
		if(fHttpMethod == null) {
			Configuration conf = (Configuration)fContext.getAgentConfiguration().getAgentCustomConfiguration();
			String url = conf.getString(Configuration.URL);
			String method = conf.getString(Configuration.HTTP_METHOD);
			if(Configuration.HTTP_METHOD_POST.equals(method)) {
				fHttpMethod = new PostMethod(url);
			} else if(Configuration.HTTP_METHOD_GET.equals(method)) {
				fHttpMethod = new GetMethod(url);
			} else {
				RMSException ex = new RMSException("Unsupported HTTP method type: " + method);
				ex.setIsInternalAppError();
			}
			fHttpMethod.setRequestHeader("User-Agent", "IxoraRMS");
			RequestParameters params = (RequestParameters)conf.getObject(Configuration.HTTP_PARAMETERS);
			if(params != null) {
				List<RequestParameters.NameValue> lst = params.getParameters();
				if(!Utils.isEmptyCollection(lst)) {
					NameValuePair[] nv = new NameValuePair[lst.size()];
					int i = 0;
					for(RequestParameters.NameValue rnv : lst) {
						nv[i] = new NameValuePair(rnv.getName(), rnv.getValue());
						++i;
					}
					if(fHttpMethod instanceof PostMethod) {
						((PostMethod)fHttpMethod).setRequestBody(nv);
					} else if(fHttpMethod instanceof GetMethod) {
						((GetMethod)fHttpMethod).setQueryString(nv);
					}
				}
			}
		}
	}
}
