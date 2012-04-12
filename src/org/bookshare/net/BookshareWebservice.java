package org.bookshare.net;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;

/**
 * This class provides the services needed for getting
 * data from Bookshare's Webservice API.
 *
 */
final public class BookshareWebservice {

	// endpoint for bookshare API calls
	private static String URL = "api.bookshare.org";

    /*
     * Default constructor
     */
    public BookshareWebservice() {

    }

    /*
     * Constructor that allows setting of api host
     * @param apiHost String api host
     */
    public BookshareWebservice(String apiHost) {
        URL = apiHost;
    }

    /**
	 * Utility method that returns a MD5 encryption of a String.
	 * @param str String to be be encrypted.
	 * @return MD5 encrypted String.
     * @throws java.io.UnsupportedEncodingException
	 */

	public String md5sum(String str) throws UnsupportedEncodingException {
		byte[] md5sum = null;
		try{
			MessageDigest md = MessageDigest.getInstance("MD5");
			md5sum = md.digest(str.getBytes("UTF-8"));
		}
		catch(NoSuchAlgorithmException e){
			System.out.println(e);
		}

        return toHex(md5sum);
	}

    private String toHex(byte[] bytes) {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "X", bi);
    }

	/**
	 * Converts the InputStream to a String.
	 * @param inputStream InputStream.
	 * @return String representation of the InputStream data.
	 */
	public String convertStreamToString(InputStream inputStream) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 */
		// Construct a BufferedReader for the inputStream
		BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			// Read each line and append a newline character at the end
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				// Close the stream irrespective of whether the read was successful or not
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		// Return the trimmed response String
		return sb.toString().trim();
	}
	
	/**
	 * Retrieves the InputStream in response to a requested URI. Method also takes care of 
	 * user authentication.
	 * This InputStream needs to be used in appropriate way to handle the response
	 * depending on the response.
	 * E.g. If the requested URI is for downloading a file, read data
	 * from this InputStream and write to a local file using OutputStream.
	 * E.g. If the requested URI returns a XML or JSON response, make use of
	 * corresponding parsers to handle the data.
	 * 
	 * @param wsPassword password of the Bookshare's web service account.
	 * @param requestUri The request URI.
	 * @return InputStream representing the response.
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	public InputStream getResponseStream(String wsPassword, String requestUri) throws
	URISyntaxException, IOException{
		HttpEntity entity;
		
		HttpResponse response = getHttpResponse(wsPassword, requestUri);

		// Get hold of the response entity
		entity = response.getEntity();
		return entity.getContent();
	}
	
	/**
	 * Retrieves an HttpResponse for a requested URI.
	 * @param wsPassword password of the Bookshare web service account.
	 * @param requestUri The request URI.
	 * @return HttpResponse A HttpResponse object.
	 * @throws URISyntaxException
	 * @throws IOException
	 */
	// Method for retrieving an HttpResponse
	public HttpResponse getHttpResponse(String wsPassword, String requestUri) throws
	URISyntaxException, IOException{
		DefaultHttpClient httpclient = new DefaultHttpClient();



		HttpHost targetHost = new HttpHost(URL);



		URI uri = new URI(requestUri);

		// Prepare a HTTP GET Request
		HttpGet httpget = new HttpGet(uri);

		// Execute the request
		HttpResponse response;
		
		if(wsPassword != null){
			Header header = new BasicHeader("X-password",md5sum(wsPassword));
			httpget.setHeader(header);
		}
		
		// Get the HttpResponse. Earlier the localcontext was used for the basic authentication.
		// Now basic authentication is not needed as the developer key is appended to the request
		//response = httpclient.execute(targetHost, httpget, localcontext);
		response = httpclient.execute(targetHost, httpget);
		
		return response;
	}

}
