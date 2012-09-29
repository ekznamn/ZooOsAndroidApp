
package zoo.themenroute;

import android.app.Activity;
import android.os.Bundle;
import zoo.themenroute.R;

/**
 * The Help-Activity provides some frequently asked questions with corresponding
 * answers.
 * 
 * @version 1.0
 * @author Nina Manzke
 * @date 26.09.2011
 */
public class HelpActivity extends Activity {
	
	/** 
	 * Called on activity's first creation.
	 * @param savedInstanceState 
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.help);
	}
}