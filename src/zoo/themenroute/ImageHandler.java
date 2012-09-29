
package zoo.themenroute;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;



/**
 * .
 * 
 * @version 1.0
 * @author Nina Manzke
 * @date 26.09.2011
 */
public class ImageHandler {

	private final Map<String, Bitmap> image_map;
//	private final Map<String, Drawable> image_map;
	private DefaultHttpClient http_client = null;
	private static final String URL = "http://www.zoowiso-os.de/zoo_app/alt/";
	private static final String URL_FOTO = "http://igf-srv-maps.igf.uos.de/zoo/foto_video/";
	
	/**
	 * 
	 */
	public ImageHandler() {
        image_map = new HashMap<String, Bitmap>();
//        image_map = new HashMap<String, Drawable>();
    }
	
	/**
	 * 
	 * @param animal_id
	 * @return
	 */
	public Bitmap getImage(String animal_id) {
		
        if (image_map.containsKey(animal_id)) {
        	Log.e("OOO", "ist da!");
            return image_map.get(animal_id);
        } else {
        	return null;
        }
	}
	
	/**
	 * 
	 * @param topic_id
	 */
	public void storeImages(int topic_id) {
		
		String url = "", id = "";
		http_client = new DefaultHttpClient();
//		Drawable img = null;
		Bitmap img = null;
		JSONArray animal_ids = new JSONArray();
		
		try {
			animal_ids = getIds(topic_id);
			
			if (animal_ids != null) {
		
				for (int i=0; i<animal_ids.length(); i++) {
					
					id = animal_ids.getString(i);
					
					if (!image_map.containsKey(id)) {
						
	//					url = URL + "Bilder_Test/kl" + id + ".jpg";
						url = URL_FOTO + id + "_gross.jpg";
						img = loadImage(url);
						image_map.put(id, img);
					}
				}
			} else {
				Log.e("Load Error", "animal_ids failed!");
			}
		} catch (JSONException e) {
			Log.e(this.getClass().getSimpleName(), "construct url failed");
		} catch (MalformedURLException me){
        	Log.e(this.getClass().getSimpleName(), "loadImage failed url");
        } catch (IOException ioe) {
        	Log.e(this.getClass().getSimpleName(), "loadImage failed");
        }
		http_client.getConnectionManager().shutdown();
	}
	
	/**
	 * 
	 * @param imageURL
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private Bitmap loadImage(String image_url) throws MalformedURLException, 
													  IOException {
		HttpGet request = new HttpGet(image_url);
        HttpResponse response = http_client.execute(request);
        InputStream is = response.getEntity().getContent();
        BufferedInputStream buffis = new BufferedInputStream(is);
        
//        return Drawable.createFromStream(is, "img");
		
//		URL url = new URL(image_url);
//        URLConnection conn = url.openConnection();
//        conn.connect();
//        BufferedInputStream buffis = new BufferedInputStream(conn.getInputStream());
       	Bitmap bmp = BitmapFactory.decodeStream(buffis);
       	return bmp;
	}
	
	/**
	 * 
	 * @param tid
	 * @return
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private JSONArray getIds(int tid) throws MalformedURLException, 
											 IOException {
		JSONArray ja_data = null;
		HttpGet request = new HttpGet(URL + "get_animals.php?topic_id=" + 
									  tid + "&mode=1");
		HttpResponse response = http_client.execute(request);
		String data = EntityUtils.toString(response.getEntity());
		data = data.substring(6);
		Log.e("OOO", "------------"+data);
		try {
			ja_data = new JSONArray(data);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return ja_data;
	}
	
	/**
	 * 
	 */
	public void clear() {
		image_map.clear();
	}
}
