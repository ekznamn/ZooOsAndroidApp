
package zoo.themenroute;

import org.json.JSONException;
import org.json.JSONObject;
import zoo.themenroute.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This <code>Activity</code> provides some details to an animal species like 
 * the name, a short description, the distribution and things to know.
 * 
 * @version 1.0
 * @author Nina Manzke
 * @date 26.09.2011
 */
public class AnimalDetailsActivity extends Activity {
	
	private int animal_id;
	private String title = "Keine Angabe";
	private String description = "Keine Angabe";
	private String distribution = "Keine Angabe";
	private String things_to_know = "Keine Angabe";
//	private Drawable image = null;
	private Bitmap image = null;
	
	/** 
	 * Called on activity's first creation.
	 * @param savedInstanceState 
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.animal_details);
		
		if (getIntent().hasExtra("data")) {
			
			JSONObject jo_data;
			
			String data = getIntent().getExtras().getString("data");
			
			try {
				
				jo_data = new JSONObject(data);
				animal_id = jo_data.getInt("animal_id");
				title = jo_data.getString("name");
				description = jo_data.getString("description");
				distribution = jo_data.getString("distribution");
				things_to_know = jo_data.getString("things_to_know");
//				image = (Drawable)jo_data.get("img");
				
			} catch(JSONException e) {
			}
		}
		
		if (getIntent().hasExtra("img")) {
			image = (Bitmap)getIntent().getParcelableExtra("img");
		}

		ImageView iv_image = (ImageView) findViewById(R.id.animal_image);
		TextView tv_title = (TextView) findViewById(R.id.animal_title);
		TextView tv_desc = (TextView) findViewById(R.id.animal_description);
		TextView tv_distr = (TextView) findViewById(R.id.animal_distribution);
		TextView tv_know = (TextView) findViewById(R.id.animal_know);
		
//		HTTPRequestHandler http = new HTTPRequestHandler();
//		Bitmap image = http.getImage(animal_id);
//		iv_image.setImageBitmap(image);
//		http.close();
		tv_title.setText(title);
		tv_desc.setText(description);
		tv_distr.setText(distribution);
		tv_know.setText(things_to_know);
		if (image != null) {
			iv_image.setImageBitmap(image);
		}
	}
	
}
