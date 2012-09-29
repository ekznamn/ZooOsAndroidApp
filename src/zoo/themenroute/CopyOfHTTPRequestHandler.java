
package zoo.themenroute;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;


/**
 * A <code>HTTPRequestHandler</code> is used to perform HTTP requests to send 
 * and receive data from an external PHP-script.
 * 
 * @version 1.0
 * @author Nina Manzke
 * @date 26.09.2011
 */
public class CopyOfHTTPRequestHandler {


	private static final String URL = "http://www.zoowiso-os.de/zoo_app/";
    
    /**
     * This method sends a GET request to the specified URL with the given 
     * extension and returns the received <code>string</code> from the response.
     * 
     * @param url_ext the query-part of the GET request
     * @return the result as a <code>string</code>
     * @throws HTTPRequestException
     */
    public String sendGet(String url_ext) throws HTTPRequestException {
    	
    	InputStream is = null;
    	HttpURLConnection conn = null;
    	
    	
    	try {
	        try {
	            URL url = new URL(URL + url_ext);
	            conn = (HttpURLConnection) url.openConnection();
	//            conn.setReadTimeout(10000 /* milliseconds */);
	//            conn.setConnectTimeout(15000 /* milliseconds */);
	            conn.setRequestMethod("GET");
	            conn.setDoInput(true);
	            
	            conn.connect();
	            is = conn.getInputStream();
	
	            String data = convertResponse(is);
	            Log.w("DataTest", "-------------" + data); 
	            
	            return data;
	         
	        } finally {
	            if (is != null) {
	                is.close();
	            } 
	        }
	        
	    } catch (IOException e) {
	    	throw new HTTPRequestException("Bei der Datenübertragung ist ein " +
					   					   "Fehler aufgetreten.");
	    } finally { 
	    	if (conn != null) {
                conn.disconnect();
            }
        }
    	

    }
    
    public String convertResponse(InputStream stream) throws IOException, UnsupportedEncodingException {
    	
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8"); 
        char[] buffer = new char[100000];
        reader.read(buffer);
        return new String(buffer);
    }

//    public Bitmap getImage(int animal_id) {
//    	
//	    BufferedInputStream buffered_in = null;
//	    Bitmap bmp = null;
//	
//	    try {
//	    	URL url = new URL(URL + "Bilder_Test/kl" + animal_id + ".jpg");
//	        URLConnection conn = url.openConnection();
//	        conn.connect();
//	        buffered_in = new BufferedInputStream(conn.getInputStream());
//	        bmp = BitmapFactory.decodeStream(buffered_in);
//	        
//	    } catch (MalformedURLException e) {
//	    } catch (IOException e) {
//	    }
//	    return bmp;
//    } 
}
