
package zoo.themenroute;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/**
 * The About-Activity provides the current App-Version and information about 
 * the development.
 * 
 * @version 1.0
 * @author Nina Manzke
 * @date 26.09.2011
 */
public class AboutActivity extends Activity {
	
	/** 
	 * Called on activity's first creation.
	 * @param savedInstanceState 
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about);
		
		TextView tv_igf = (TextView) findViewById(R.id.about_igfurl);
		tv_igf.setText(Html.fromHtml(
				"<a href=\"http://www.igf.uni-osnabrueck.de/index.php/de/\">" + 
				"www.igf.uni-osnabrueck.de</a>"));
		tv_igf.setMovementMethod(LinkMovementMethod.getInstance());
		
		TextView tv_osm = (TextView) findViewById(R.id.about_osm);
		tv_osm.setText(Html.fromHtml(
				"Karte &#169; <a href=\"http://www.openstreetmap.org\">" +
				"OpenStreetMap</a> - " +
				"<a href=\"http://www.openstreetmap.org/copyright\">" +
				"Terms</a>"));
		tv_osm.setMovementMethod(LinkMovementMethod.getInstance());
		
		TextView tv_zoowiso = (TextView) findViewById(R.id.about_zoowiso);
		tv_zoowiso.setText(Html.fromHtml(
				getResources().getText(R.string.about_zoowiso) +
				" <a href=\"http://www.zoowiso-os.de\">ZooWisO</a>."));
		tv_zoowiso.setMovementMethod(LinkMovementMethod.getInstance());
	}
}