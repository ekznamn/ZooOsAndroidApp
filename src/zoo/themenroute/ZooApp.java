
package zoo.themenroute;

import android.app.Application;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CheckBox;

/**
 * This class is used to save states during the lifetime of the application.
 * 
 * @version 1.0
 * @author Nina Manzke
 * @date 26.09.2011
 */
public class ZooApp extends Application {
	
	public boolean asked_for_gps = false;
	
	public void storePrefs(View view) {
		SharedPreferences prefs = getSharedPreferences("Zoo", MODE_PRIVATE);
	    SharedPreferences.Editor editor = prefs.edit();
	    editor.putBoolean("startscreen", !((CheckBox) view).isChecked());
	    editor.commit();
	}
}
