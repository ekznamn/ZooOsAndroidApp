
package zoo.themenroute;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import zoo.themenroute.R;

/**
 * The About-Activity provides the current App-Version and information about 
 * the development.
 * 
 * @version 1.0
 * @author Nina Manzke
 * @date 26.09.2011
 */
public class StartActivity extends Activity {
	
	private static final int		DIALOG_WELCOME = 1;

	/** 
	 * Called on activity's first creation.
	 * @param savedInstanceState 
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		setContentView(R.layout.startscreen);
		
		SharedPreferences prefs = getSharedPreferences("Zoo", MODE_PRIVATE);
		final boolean startscreen = prefs.getBoolean("startscreen", true);
		
		super.onCreate(savedInstanceState);
		final Handler handler = new Handler();
		Thread splashThread =  new Thread() {
            @Override
            public void run() {
                try {
                    synchronized(this) {
                    	if (startscreen) {
                    		wait(1500);
                    	} else {
                    		wait(3000);
                    	}
                    }
                }
                catch(InterruptedException ex) {}
                handler.post(new Runnable() { 
    				public void run() { 
		        		if (startscreen) {
		        			showDialog(DIALOG_WELCOME);
		        		} else {
		        			Intent intent = new Intent(StartActivity.this, 
		        									   MapActivity.class);
		        			StartActivity.this.startActivity(intent);
		        		}
    				}
                });
            }
        };        
        splashThread.start();
	}
	
	/**
	 * 
	 */
	protected Dialog onCreateDialog(int id) {
		
		AlertDialog dialog;
		
		switch(id) {
		case DIALOG_WELCOME:
			LayoutInflater inflater = (LayoutInflater) getApplicationContext()
									 .getSystemService(LAYOUT_INFLATER_SERVICE);
			final View checkbox = inflater.inflate(R.layout.welcome_dialog, 
												   null);
			dialog = new AlertDialog.Builder(this)
					.setTitle(R.string.welcome)
					
					.setMessage(R.string.welcome_info)
					.setView(checkbox)
					.setCancelable(true)
					.setPositiveButton("OK", 
					 new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							Intent intent = new Intent(StartActivity.this, 
													   MapActivity.class);
							StartActivity.this.startActivity(intent);
						}
					}).create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}
}