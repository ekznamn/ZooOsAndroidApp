
package zoo.themenroute;

import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

/**
 * A <code>ProximityAlertHandler</code> is used to remove already existing 
 * Proximity-Alerts or to set new <code>ProximityAlerts</code>. Because only one
 * instance is necessary, a public constructor doesn't exist and the instance is
 * given by the static class method <code>getInstance()</code>.  
 * 
 * @version 1.0
 * @author Nina Manzke
 * @date 26.09.2011
 */
public class ProximityAlertHandler {
	
	private static ProximityAlertHandler 	instance;
	private HashMap<Animal, PendingIntent> 	proxalerts_intents;

	private static final long 	PROX_RADIUS = 35;
	private static final long 	PROX_EXPIRATION = -1;
	private static final String	ALERT_PACKAGE = "zoo.themenroute.ProxAlert.";
	
	/**
	 * Private Constructor
	 */
	private ProximityAlertHandler() {
		proxalerts_intents = new HashMap<Animal, PendingIntent>();
	}
	
	/**
	 * Returns either the already existing instance or creates a new one which 
	 * is returned.
	 * @return the <code>ProximityAlertHandler</code> instance
	 */
	public static ProximityAlertHandler getInstance() {
		
		if (instance == null) {
			instance = new ProximityAlertHandler();
		}
		return instance;
	}
	
	/**
	 * This method replaces all <code>ProximityAlerts</code> by removing 
	 * existing ones and setting the new ones which are given within the 
	 * <code>JSONArray</code> that contains all <code>Animal</code> data.
	 * @param loc_mgr the <code>LocationManager</code>
	 * @param context the application context
	 * @param ja_items <code>JSONArray</code> containing 
	 * 			<code>JSONObjects</code> that hold values to the animal species
	 * 			that should be set as new <code>ProximityAlerts</code>.
	 */
	public void replaceProximityAlerts(LocationManager loc_mgr, 
									   Context context,
									   JSONArray ja_items,
									   int radius) {
		String str_animal;
		Animal animal;
		
		removeProximityAlerts(loc_mgr);
		
		try {
			Log.i("DataTest", "Radius: " + radius);
			for (int i = 0; i < ja_items.length()-1; i++) {
				
				str_animal = ja_items.getJSONObject(i).toString();
				animal = new Animal(ja_items.getJSONObject(i));
				
				Intent intent = new Intent(ALERT_PACKAGE);
				intent.putExtra("animal", str_animal);
				PendingIntent prox_intent = PendingIntent.getBroadcast(
														context, 
														i+1, intent, 
											PendingIntent.FLAG_CANCEL_CURRENT);
				
				loc_mgr.addProximityAlert(animal.getLat(), 
										  animal.getLon(), 
										  radius, 
										  PROX_EXPIRATION, 
										  prox_intent);
				
				proxalerts_intents.put(animal, prox_intent);
				Log.i("DataTest", "*** prox_alert " + i + " gesetzt");
			}
		} catch(JSONException e) {
		}
	}
	
	/**
	 * Removes all existing <code>ProximityAlerts</code> from the given
	 * <code>LocationManager</code>.
	 * @param loc_mgr the <code>LocationManager</code>
	 */
	public void removeProximityAlerts(LocationManager loc_mgr) {
	
		if (proxalerts_intents.size() != 0) {
			for (PendingIntent curr_intent:this.proxalerts_intents.values()) {
				
				loc_mgr.removeProximityAlert(curr_intent);
			}
			proxalerts_intents.clear();
		}
	}
}
