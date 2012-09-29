
package zoo.themenroute;

import android.content.Context;
import android.net.ConnectivityManager;
import zoo.themenroute.R;

/**
 * The <code>Network</code> class is used to check whether a network connection 
 * is available.
 * 
 * @version 1.0
 * @author Nina Manzke
 * @date 26.09.2011
 */
public class Network {
	
	/**
	 * Checks whether a connection to the network exists or not.
	 * @param context the application context
	 * @return true, if a network connection is available, false else
	 */
	public static boolean isAvailable(Context context) {
		
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(
												Context.CONNECTIVITY_SERVICE);
		 
		 if (cm.getActiveNetworkInfo() != null) {
			 return cm.getActiveNetworkInfo().isConnectedOrConnecting();			 
		 } else {
			 return false;
		 }		 
	}
}
