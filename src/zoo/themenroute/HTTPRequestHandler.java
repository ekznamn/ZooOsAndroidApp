
package zoo.themenroute;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import zoo.themenroute.R;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.Toast;


/**
 * A <code>HTTPRequestHandler</code> is used to perform HTTP requests to send 
 * and receive data from an external PHP-script.
 * 
 * @version 1.0
 * @author Nina Manzke
 * @date 26.09.2011
 */
public class HTTPRequestHandler {

	private DefaultHttpClient 	http_client;
	private HttpResponse 		response = null;
	private HttpGet 			http_get = null;
	private static final String URL = "http://www.zoowiso-os.de/zoo_app/";

    /**
	 * Constructor  
	 */
    public HTTPRequestHandler() {
    	
    	HttpParams params = new BasicHttpParams();
    	http_client = new DefaultHttpClient(params);
    }
    
    /**
     * This method sends a GET request to the specified URL with the given 
     * extension and returns the received <code>string</code> from the response.
     * 
     * @param url_ext the query-part of the GET request
     * @return the result as a <code>string</code>
     * @throws HTTPRequestException
     */
    public String sendGet(String url_ext) throws HTTPRequestException {
    	
    	String data = "";
    	
        try {
        	http_get = new HttpGet(URL + url_ext);  
			response = http_client.execute(http_get);
			data = EntityUtils.toString(response.getEntity());
			data = data.trim();
			int start_index = data.indexOf('[');
			if (!(start_index >= 0 && start_index < data.length())) {
				start_index = data.indexOf('{');
			}
			data = data.substring(start_index);
			
			
		} catch (ClientProtocolException e) {
			throw new HTTPRequestException("Ein Fehler ist aufgetreten, der " +
			   							   "Server antwortet nicht.");
		
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			throw new HTTPRequestException("Kodierungsfehler.");
			
		} catch (IOException e) {
			e.printStackTrace();
			throw new HTTPRequestException("Bei der Datenübertragung ist ein " +
			   							   "Fehler aufgetreten.");
		} catch (Exception e) {
			e.printStackTrace();
			throw new HTTPRequestException(e.getMessage());
			
		} finally {
			http_get.abort();
		}
		
		return data;
    }
    
    public Bitmap getImage(int animal_id) {
    	
	    BufferedInputStream buffered_in = null;
	    Bitmap bmp = null;
	
	    try {
	    	URL url = new URL(URL + "Bilder_Test/kl" + animal_id + ".jpg");
	        URLConnection conn = url.openConnection();
	        conn.connect();
	        buffered_in = new BufferedInputStream(conn.getInputStream());
	        bmp = BitmapFactory.decodeStream(buffered_in);
	        
	    } catch (MalformedURLException e) {
	    } catch (IOException e) {
	    }
	    return bmp;
    }
    
    /**
     * Shuts down the connection manager to ensure immediate deallocation of 
     * all system resources.
     */
    public void close() {
    	http_client.getConnectionManager().shutdown();
    } 
}
