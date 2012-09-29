
package zoo.themenroute;

import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.OverlayItem;
import zoo.themenroute.R;

import android.graphics.drawable.Drawable;

/**
 * An <code>Animal</code> is an <code>OverlayItem</code> which provides several 
 * attributes to an animal species like name, description and the coordinates 
 * it has in the zoo.
 * 
 * @version 1.0
 * @author Nina Manzke
 * @date 26.09.2011
 */
public class Animal extends OverlayItem {
	
	private static int id_counter = 0;
	private int id;
	private String animal_id;
	private double lat;
	private double lon;
	private String name;
	private String description;
	private String distribution;
	private String things_to_know;
	
	/**
	 * Constructs an <code>Animal</code> with the given properties.
	 * @param lat
	 * @param lon
	 * @param name
	 * @param description
	 * @param distribution
	 * @param things_to_know
	 */
	public Animal(String animal_id, double lat, double lon, String name, String description, 
				  String distribution, String things_to_know) {
		
		super(name, description, new GeoPoint(lat, lon));
		
		this.id = ++id_counter;
		this.animal_id = animal_id;
		this.lat = lat;
		this.lon = lon;
		this.name = name;
		this.description = description;
		if (things_to_know.equals("") || things_to_know.equals("\"")) {
			this.things_to_know = " - ";
		}
		this.things_to_know = things_to_know;
		this.distribution = distribution;
		
		setMarkerHotspot(HotspotPlace.CENTER);
	}
	
	/**
	 * Constructs an <code>Animal</code> by reading the given 
	 * <code>JSONObject</code>, which should contain all properties.
	 * @param data
	 * @throws JSONException
	 */
	public Animal(JSONObject data) throws JSONException {
		
		this(data.getString("animal_id"),
			 data.getDouble("lat"), 
			 data.getDouble("lon"), 
			 data.getString("name"), 
			 data.getString("description"),
			 data.getString("distribution"),
			 data.getString("things_to_know"));
	}
	
	/**
	 * Defines the given <code>Drawable</code> as the entity marker in map.
	 * @param marker a drawable that represents the animal in map
	 */
	@Override
	public void setMarker(Drawable marker) {
		super.setMarker(marker);
	}
	
	/**
	 * This method puts all properties of the <code>Animal</code> into one 
	 * <code>JSONObject</code>.
	 * @return the animal as a <code>JSONObject</code>
	 */
	public JSONObject toJSONObject() {
		
		JSONObject jo = new JSONObject();
		try {
			jo.put("id", this.id);
			jo.put("animal_id", this.animal_id);
			jo.put("lat", this.lat);
			jo.put("lon", this.lon);
			jo.put("name", this.name);
			jo.put("description", this.description);
			jo.put("distribution", this.distribution);
			jo.put("things_to_know", this.things_to_know);
			
		} catch (JSONException e) {
		}
		return jo;
	}
	
	/**
	 * Returns the <code>Animal</code>'s ID.
	 * @return the ID
	 */
	public int getId() {
		return this.id;
	}
	
	public String getAnimalId() {
		return this.animal_id;
	}
	
	/**
	 * Returns the latitude of the <code>Animal</code>'s Geopoint.
	 * @return latitude
	 */
	public double getLat() {
		return this.lat;
	}
	
	/**
	 * Returns the longitude of the <code>Animal</code>'s Geopoint.
	 * @return longitude
	 */
	public double getLon() {
		return this.lon;
	}
	
	/**
	 * Returns the <code>Animal</code>'s name.
	 * @return the name
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Returns the <code>Animal</code>'s description.
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * Returns the <code>Animal</code>'s distribution.
	 * @return the distribution
	 */
	public String getDistribution() {
		return this.distribution;
	}
	
	/**
	 * Returns 'things to know' of the <code>Animal</code>.
	 * @return things to know
	 */
	public String getThingsToKnow() {
		return this.things_to_know;
	}
}
