/**
 * 07-Mar-2006
 */
package com.ixora.http;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.PostMethod;

/**
 * @author Daniel Moraru
 */
public class HttpClientTest {

	/**
	 *
	 */
	public HttpClientTest() {
		super();
	}

	public static void main(String[] args) {
		try {
			HttpClient client = new HttpClient();
		    PostMethod httppost = new PostMethod("http://localhost:8080/jobs/daniel.php");
		    StringBuffer error = new StringBuffer("    *  Annotation of existing resources\n" +
		    		"* Posting a message to a bulletin board, newsgroup, mailing list, or similar group of articles.\n" +
		    		"* Providing a block of data, such as the result of submitting a form, to a data-handling process.\n");
		    NameValuePair[] data = {
		            new NameValuePair("error", error.toString()),
		            new NameValuePair("lic", "bloggs")
		          };
		    httppost.setRequestBody(data);
		    try {
		        client.executeMethod(httppost);
		        if (httppost.getStatusCode() == HttpStatus.SC_OK) {
		            System.out.println(httppost.getResponseBodyAsString());
		        } else {
		          System.out.println("Unexpected failure: " + httppost.getStatusLine().toString());
		        }
		    } finally {
		        httppost.releaseConnection();
		    }
	    } catch (Exception e) {
			e.printStackTrace();
		}
	}
}
