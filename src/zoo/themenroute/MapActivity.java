
package zoo.themenroute;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedIconOverlay.OnItemGestureListener;
import org.osmdroid.views.overlay.PathOverlay;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.SimpleLocationOverlay;
import org.osmdroid.views.util.constants.MapViewConstants;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

/**
 * This <code>Activity</code> shows a <code>MapView</code>, that is zoomed to 
 * the Zoo of Osnabrueck by default. If the device has GPS disabled, the user 
 * gets a message and can switch to the GPS-settings to enable GPS. If the 
 * user's position is located as a point within the zoo area, there is a marker 
 * at that position in the map. The selected track is shown as a blue line. 
 * 
 * @version 1.0
 * @author Nina Manzke
 * @date 26.09.2011
 */
public class MapActivity extends Activity 
						 implements MapViewConstants, 
						 		    OnItemGestureListener<Animal> {

	private TextView 					tv_title;
	private Button 						btn_change_topic;
	private Spinner						spinner;
	private Button						btn_example;
	private ImageView					iv_animal_dialog;
	private MapView 					mapview;
	private MapController				map_controller;
	private ScaleBarOverlay				scalebar_overlay;
	private SimpleLocationOverlay		location_overlay;
	private static LocationManager		location_manager;
	private PathOverlay					path_overlay;
	private ItemizedIconOverlay<Animal>	point_overlay;
	private ProgressDialog 				progress_dialog;
	private LocationListener 			location_listener;
	private Handler 					handler;
	private Thread 						thread;
	
	private GeoPoint 	location = null;
	private boolean		asked_for_gps = false;
	private boolean 	is_registered = false;
	
	public static final int			LOAD_ERROR = -3;
	private static final int		DIALOG_ANIMAL_INFO = 0;
	private static final int		DIALOG_GPS_DISABLED = 1;
	private static final int		DIALOG_ERROR = 2;
	private static final int		DIALOG_QUIT = 3;
	private static final int		DIALOG_CHOOSE_TOPIC = 4;
	private static final String		ALERT_PACKAGE = "zoo.themenroute.ProxAlert.";
	private static final String 	TOPICS_URL = "http://www.zoowiso-os.de/zoo_app/get_topics.php";
	private final CharSequence[] 	TOPICS = {"Takamanda",
			  								  "Samburu",
			  								  "kein Thema"};

	private String 					animal_data = "nix";
	private String[]				animal_ids;
	private int 					topic_id = -1;
	private int 					tmp_topic_id = -1;
	private String					curr_animal_name;
	private String					curr_animal_all;
	private String 					curr_animal_id = "";
	private String 					topics = null;
	private int						radius = 35;
	
	private ProximityAlertReceiver 	receiver;
	private ProximityAlertHandler 	pa_handler;
	private ImageHandler			img_handler;		
	
	/* *************** Activity Lifecycle Methods *************************/ 
	
	/** 
	 * Called on activity's first creation.
	 * @param Bundle savedInstanceState 
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map);
		tv_title = (TextView) findViewById(R.id.map_title);
		btn_change_topic = (Button) findViewById(R.id.map_btn_change_topic);
		iv_animal_dialog = (ImageView) findViewById(R.id.iv_animal_dialog);
		
		pa_handler = ProximityAlertHandler.getInstance();
		img_handler = new ImageHandler();
		
		
		
		//TODO ---------- nur zum Test -------------------------------------
//		btn_example = (Button) findViewById(R.id.btn_example);
		spinner = (Spinner) findViewById(R.id.spinner);
		ArrayAdapter<String> s_adapter = new ArrayAdapter<String>(
				this, android.R.layout.simple_spinner_item, 
				new String[]{"Warzenschwein", "Weißscheitelmangabe", 
				"Mantelpavian", "Zebramanguste", "Hausesel", "Pinselohrschwein",
				"Großer Kudu", "Goldschakal", "Serval", "Schimpanse", 
				"Tüpfelhyäne", "Afrikanische Zwergziege"});
		s_adapter.setDropDownViewResource(
	    		android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(s_adapter);
		spinner.setOnItemSelectedListener(new OnItemSelectedListener() {
			 public void onItemSelected(AdapterView<?> parent,
				        View view, int pos, long id) {
				 if (!animal_data.equals("nix")) {
					JSONArray ja_items;
					Animal first_animal;
					try {
						ja_items = new JSONArray(animal_data);	
						first_animal = new Animal(ja_items.getJSONObject(pos));
						curr_animal_all = ja_items.getJSONObject(pos).toString();
						curr_animal_name = first_animal.getName();
						curr_animal_id = first_animal.getAnimalId();
						showDialog(DIALOG_ANIMAL_INFO);
					} catch (JSONException e) {
				    	Log.e("JSON Error", animal_data.toString());
					}
					
				}
			 }
			 public void onNothingSelected(AdapterView<?> arg0) { }
		});

		SeekBar sb = (SeekBar) findViewById(R.id.seekBar);
		sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

			public void onProgressChanged(SeekBar seekbar, 
										  int progress, boolean from_user) {
				TextView tv_radius = (TextView) findViewById(R.id.tv_radius);
				if (from_user) {
					tv_radius.setText(progress + "");
					radius = progress;
				}
			}

			public void onStartTrackingTouch(SeekBar seekbar) {
				
				
			}

			public void onStopTrackingTouch(SeekBar seekbar) {
				
				
			}
			
		});
		
		//------------------------------------------------------------------
		
	    buildMap();
	    updateTopic();
	} 
	
	/**
	 * Called if the activity gets resumed.
	 */
	@Override
	public void onResume() {
		
		super.onResume();
		
		handleReceiverRegistration(true);
		handleGPSAndLocation();
	}

	/**
	 * Called if the activity is set into paused-state.
	 */
	@Override
	public void onPause() {
		
		super.onPause();
		
		handleReceiverRegistration(false);
		stopLocationUpdates();
	}
	
	/**
	 * Called on activity's destruction.
	 */
	@Override
	public void onDestroy() {
		
		super.onDestroy();
		
		((ZooApp)getApplicationContext()).asked_for_gps = false;
		pa_handler.removeProximityAlerts(location_manager);
		img_handler.clear();
		handleReceiverRegistration(false);
	}
	
	/* *************** Map-building methods ******************************/

	/**
	 * Initializes the <code>MapView</code> by setting the tile source and the 
	 * zoom. A <code>ScalebarOverlay</code> and a <code>LocationOverlay</code>
	 * are added.
	 */
	private void buildMap() {
		
		mapview = (MapView) findViewById(R.id.map_mapview);
		mapview.setTileSource(TileSourceFactory.MAPNIK);
		
		mapview.setBuiltInZoomControls(true);
	    mapview.setMultiTouchControls(true);
	    
	    mapview.invalidate();
	    
	    map_controller = mapview.getController();
	    setZoomToZooExtend();
	    
	    location_manager = (LocationManager) 
		   					this.getSystemService(Context.LOCATION_SERVICE);

	    buildScalebarOverlay();
	    buildLocationOverlay();
	}
	

	/**
	 * This method builds a new <code>ScalebarOverlay</code> and adds it to the 
	 * <code>MapView</code>.
	 */
	private void buildScalebarOverlay() {
		
		scalebar_overlay = new ScaleBarOverlay(this);
	    scalebar_overlay.drawLatitudeScale(true);
	    scalebar_overlay.drawLongitudeScale(false);
	    scalebar_overlay.setScaleBarOffset(3.0f, 3.0f);
	    mapview.getOverlays().add(scalebar_overlay);
	}
	
	/**
	 * This method builds a new <code>PathOverlay</code> and adds it to the 
	 * <code>MapView</code>. An already existing <code>PathOverlay</code> is 
	 * removed.
	 * @param points all waypoints that build the path 
	 */
	private void replacePathOverlay(ArrayList<GeoPoint> points) {
		
		if (path_overlay != null) {
			
			mapview.getOverlays().remove(path_overlay);
		}
		path_overlay = new PathOverlay(Color.BLUE, this);
		
		if (points.size() > 0) {
			
			Paint path_style = new Paint();
			path_style.setColor(Color.BLUE);
			path_style.setStyle(Style.STROKE);
			path_style.setAntiAlias(true);
			path_style.setStrokeWidth(2.0f);
			path_style.setShadowLayer(2.0f, 0.5f, 0.5f, Color.BLUE);
			
			path_overlay.setPaint(path_style);
			
			for (int i = 0; i < points.size(); i++) {
				path_overlay.addPoint(points.get(i));
			}
			mapview.getOverlays().add(path_overlay);
			setLocationOverlayOnTop();
		}
	}
	
	/**
	 * Builds a <code>LocationOverlay</code>.
	 */
	private void buildLocationOverlay() {

	    location_overlay = new SimpleLocationOverlay(this);
	    
	    location_listener = new LocationListener() {
	    	
	    	public void onLocationChanged(Location loc) {
	    		
	    		if (loc != null) {
		    		location = new GeoPoint(loc);
		        	location_overlay.setLocation(location); 
	    		}
	    		map_controller.setCenter(location);
	        }

	        public void onStatusChanged(String provider, int status, 
	        							Bundle extras) {
	        	// do nothing
	        }

	        public void onProviderEnabled(String provider) {
	        	// do nothing
	        }

	        public void onProviderDisabled(String provider) {
	        	// do nothing
	        }
	    };
		mapview.getOverlays().add(location_overlay);
	}
	
	/**
	 * This method starts requesting location updates from GPS-provider.
	 */
	private void startLocationUpdates() {
		
		location_manager.requestLocationUpdates(
				 LocationManager.GPS_PROVIDER, 
				 5000, 5.0f, location_listener);
	}
	
	/**
	 * This method removes location updates from the 
	 * <code>LocationManager</code>.
	 */
	private void stopLocationUpdates() {
		
		location_manager.removeUpdates(location_listener);
	}

	/* *************** Progress Dialogs **********************************/
	
	/**
	 * This method is called to show a progress dialog while waiting for the 
	 * location determination.
	 */
	private void showProgressGPS() {
		
		if (location == null) {
			
			handler = new Handler();
	    	
	    	thread = new Thread(new Runnable() {
	    		
	    		public void run() {
	    			
	    			// wait for location to be not null
	    			while (location == null) {
	    				if(Thread.interrupted()) {
	                        break;
	    				}
	    		    }
	    			progress_dialog.dismiss();
	    		}
	    	});
	    	
	    	progress_dialog = ProgressDialog.show(this, "", getResources().getText(R.string.dialog_wait_gps), 
	    										  true, false);
	    	progress_dialog.setCancelable(true);
	    	
	    	thread.start();
		}
	}
	
	/**
	 * This method is called to show a progress dialog while waiting for the 
	 * location determination.
	 */
	private void showProgressLoadTopics() {
		
			
		handler = new Handler();
	    	
	    thread = new Thread(new Runnable() {
	    		
    		public void run() {
    			
    			HTTPRequestHandler http = new HTTPRequestHandler();
    			try {
    				topics = http.sendGet("get_topics.php");
    			} catch (HTTPRequestException e) {
    				Log.e("Download Error", "-------------");
    			}
    			http.close();
    			progress_dialog.dismiss();
    			
    			handler.post(new Runnable() { 
    				public void run() {

    					if (topics != null) {
    	    				
    						Intent intent = new Intent(MapActivity.this, 
 								   					   TopicsActivity.class);
    						intent.putExtra("data", topics);
    						MapActivity.this.startActivityForResult(intent, 0);
    	    			} else {
    	    				showDialog(DIALOG_ERROR);
    	    			}
    		        }
				});
    		}
    	});
    	
    	progress_dialog = ProgressDialog.show(this, "", 
    							getResources().getText(R.string.dialog_loading), 
    										  true, false);
    	progress_dialog.setCancelable(false);
    	
    	thread.start();
	}
	
	/**
	 * This method is called to show a progress dialog while loading data from 
	 * the database. A HTTP request is performed and after success the topic is 
	 * updated.
	 */
	private void showProgressLoadData() {
		
		handler = new Handler();
    	thread = new Thread(new Runnable() {
    		
    		public void run() {
    			
//    			CopyOfHTTPRequestHandler http = new CopyOfHTTPRequestHandler();
    			HTTPRequestHandler http = new HTTPRequestHandler();
    			try {
    				animal_data = http.sendGet("alt/get_animals.php?topic_id=" 
    											+ topic_id);
    			} catch (HTTPRequestException e) {
    				topic_id = -1;
    				e.printStackTrace();
    			}
    			http.close();
    			img_handler.storeImages(topic_id);
    			progress_dialog.dismiss();
    			
    			handler.post(new Runnable() { 
    				public void run() {

    	    			updateTopic();
    					if (topic_id == -1 || animal_data.equals("nix")) {
    	    				showDialog(DIALOG_ERROR);
    	    			} else {
    	    				replaceTopicData(animal_data);
    	    				setZoomToZooExtend();
    	    				mapview.postInvalidate();
    	    				
    	    				if (!isGPSEnabled()) {
    	    					showDialog(DIALOG_GPS_DISABLED);
    	    				} else {
    	    					showProgressGPS();
    	    				}
    	    			}
    		        }
				});
    		}
    	});
    	progress_dialog = ProgressDialog.show(this, "", 
    						getResources().getText(R.string.dialog_loading_data), 
    										  true, false);
    	progress_dialog.setCancelable(true);
    	
    	thread.start();
	}

	/* *************** Dialogs *******************************************/

	/**
	 * In this method several <code>AlertDialogs</code> are defined.
	 * @param id the ID of the certain dialog
	 */
	@Override
	protected Dialog onCreateDialog(int id) {
		
		AlertDialog dialog;
		
		switch(id) {
		// dialog that is shown, if GPS is disabled
		case DIALOG_GPS_DISABLED:
			dialog = new AlertDialog.Builder(this)
					.setTitle(R.string.dialog_no_gps)
					.setMessage(R.string.dialog_gps_msg)
					.setCancelable(false)
					.setPositiveButton(R.string.dialog_set_gps, 
					 new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
	
							Intent intent = new Intent(android.provider.Settings
           									.ACTION_LOCATION_SOURCE_SETTINGS);
							startActivityForResult(intent, 0);
						}
					})
			       .setNegativeButton(R.string.cancel, 
				    		new DialogInterface.OnClickListener() {
			           	public void onClick(DialogInterface dialog, int id) {
			           		dialog.cancel();
			           	}
					}).create();
			break;
		// dialog that is shown when the user enters an area of an animal 
		// species or when the user has tapped on a marker
		case DIALOG_ANIMAL_INFO:
//			LayoutInflater inflater = (LayoutInflater) getApplicationContext()
//			 						.getSystemService(LAYOUT_INFLATER_SERVICE);
//			final View image = inflater.inflate(R.layout.animal_dialog, 
//												   null);
			dialog = new AlertDialog.Builder(this)
					.setMessage(R.string.dialog_animal_info)
//					.setView(image)
					.setCancelable(true)
					.setPositiveButton(R.string.dialog_details, 
					 new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
		
							Intent intent = new Intent(MapActivity.this, 
												AnimalDetailsActivity.class);
							intent.putExtra("data", curr_animal_all);
							intent.putExtra("img", img_handler.getImage(curr_animal_id));
							Log.e("III", curr_animal_id);
							MapActivity.this.startActivity(intent);
						}
					}).create();
			break;
		// dialog that is shown when an error occurs
		case DIALOG_ERROR:
			dialog = new AlertDialog.Builder(this)
					.setMessage(R.string.dialog_connect_error)
					.setCancelable(true)
					.setPositiveButton("OK", 
					 new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					}).create();
			break;
		// dialog that is shown when the user presses the back key
		case DIALOG_QUIT:
			dialog = new AlertDialog.Builder(this)
					.setTitle(R.string.dialog_exit)
					.setMessage(R.string.dialog_exit_msg)
					.setCancelable(true)
					.setPositiveButton(R.string.yes, 
					 new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
		
							MapActivity.this.finish();
						}
					})
					.setNegativeButton(R.string.no, 
					 new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					
					}).create();
			break;
		// dialog that is shown when the user taps on the topic choose button
		case DIALOG_CHOOSE_TOPIC:
			dialog = new AlertDialog.Builder(this)
					.setTitle("Wähle ein Thema aus")
					.setCancelable(true)
					.setSingleChoiceItems(TOPICS, topic_id-1,  
					 new DialogInterface.OnClickListener() {
					    public void onClick(DialogInterface dialog, int item) {
					        tmp_topic_id = item;
					        if (tmp_topic_id > -1) tmp_topic_id++;
					    }
					})
					.setPositiveButton("Übernehmen", 
					 new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							topic_id = tmp_topic_id;
							if (topic_id > -1) {
								if (topic_id == 3) {
									updateTopic();
								} else {
									if (Network.isAvailable(
												  getApplicationContext())) {
										showProgressLoadData();
									} else {
										Toast.makeText(getApplicationContext(), 
											getResources()
											.getText(R.string.no_network), 
											Toast.LENGTH_LONG).show();
									}
								}
							}
						}
					})
					.setNegativeButton("Abbrechen", 
					 new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {}
					}).create();
			break;
		default:
			dialog = null;
		}
		
		return dialog;
	}
	
	/**
	 * 
	 */
	protected void onPrepareDialog(int id, Dialog dialog) {
		
		switch(id) {
		case DIALOG_ANIMAL_INFO:
			((AlertDialog)dialog).setMessage(curr_animal_name);
			
			if (img_handler != null && iv_animal_dialog != null) {
				iv_animal_dialog.setImageBitmap(img_handler.getImage(curr_animal_id));
				LayoutInflater inflater = (LayoutInflater) getApplicationContext()
					.getSystemService(LAYOUT_INFLATER_SERVICE);
				final View image = inflater.inflate(R.layout.animal_dialog, 
							   null);
				((AlertDialog)dialog).setView(image);
			}
			break;
		}
	}
	
	/* *************** Proximity Alerts **********************************/

	
	
	/**
	 * In this method the topic data is read from the <code>JSONArray</code>
	 * which is created from the given <code>string</code>. The 
	 * <code>JSONArray</code> should contain data of the animal species that 
	 * belongs to the chosen topic and another <code>array</code> that contains 
	 * the waypoints building the path.
	 * @param prox_alerts 
	 */
	private void replaceTopicData(String prox_alerts) {
		
		JSONArray ja_items;
		JSONArray ja_route;
		ArrayList<GeoPoint> path_points = new ArrayList<GeoPoint>();
		
		try {
			switch(topic_id) {
			case 1: case 2:
				
				ja_items = new JSONArray(prox_alerts);	
				ja_route = ja_items.getJSONObject(ja_items.length()-1)
				   				   .getJSONArray("route");

				for (int i = 0; i < ja_route.length(); i++) {

					JSONArray coord_pair = ja_route.getJSONArray(i);
					path_points.add(new GeoPoint(coord_pair.getDouble(1),
							 	 coord_pair.getDouble(0)));
				}
				replacePathOverlay(path_points);
				break;
			default:
				ja_items = new JSONArray();
				mapview.getOverlays().remove(path_overlay);
				break;
			}
			pa_handler.replaceProximityAlerts(location_manager, 
					  						  getApplicationContext(), 
					  						  ja_items,
					  						  radius);
			replacePointOverlay();
			
		} catch (JSONException e) {
		}
		
	}
	
	/* *************** Helper Methods ************************************/

	/**
	 * This method checks whether the GPS provider is enabled.
	 * @return Returns true, if GPS is enabled, false else.
	 */
	private boolean isGPSEnabled() {
		
		return location_manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
	}

	/**
	 * Updates the current topic by changing the title in the title bar and 
	 * handle the button text. 
	 */
	private void updateTopic() {
		
		switch(topic_id) {
		case 1:
			tv_title.setText(TOPICS[0]);
			btn_change_topic.setText(R.string.change);
			spinner.setVisibility(View.VISIBLE);
//			btn_example.setVisibility(View.VISIBLE);
			break;
		case 2:
			tv_title.setText(TOPICS[1]);
			btn_change_topic.setText(R.string.change);
			spinner.setVisibility(View.INVISIBLE);
			btn_example.setVisibility(View.INVISIBLE);
			break;
		case 3:
			clearTopic();
			spinner.setVisibility(View.INVISIBLE);
			btn_example.setVisibility(View.INVISIBLE);
			break;
		default:
			clearTopic();
			break;
		}
	}
	
	/**
	 * This method is used to remove all topic related preferences. All set 
	 * <code>ProximityAlerts</code> are removed as well as Point- and 
	 * PathOverlay.
	 */
	private void clearTopic() {
		pa_handler.removeProximityAlerts(location_manager);
		mapview.getOverlays().remove(point_overlay);
		mapview.getOverlays().remove(path_overlay);
		mapview.invalidate();
		tv_title.setText("");
		btn_change_topic.setText(R.string.choose_topic);
		spinner.setVisibility(View.INVISIBLE);
	}
	
	/**
	 * Checks whether GPS-provider is enabled and, depending on that, shows an
	 * <code>AlertDialog</code> or calls for starting location updates and 
	 * showing the location progress dialog.
	 */
	private void handleGPSAndLocation() {
		
		asked_for_gps = ((ZooApp)getApplicationContext()).asked_for_gps;
		
		if (!isGPSEnabled()) {
			
			if (!asked_for_gps) {
				
				showDialog(DIALOG_GPS_DISABLED);
				((ZooApp)getApplicationContext()).asked_for_gps = true;
			}
	    } else {
	    	startLocationUpdates();
	    	showProgressGPS();
	    }
	}
	
	/**
	 * Registers or unregisters the <code>BroadcastReceiver</code>.
	 * @param register true, if a new <code>BroadcastReceiver</code> should be
	 * 				   registered, false, if it should be unregistered.
	 */
	private void handleReceiverRegistration(boolean register) {
		
		if (register) {
			if (!is_registered) {
				IntentFilter intent_filter = new IntentFilter(ALERT_PACKAGE);
				receiver = new ProximityAlertReceiver();
				registerReceiver(receiver, intent_filter);
				is_registered = true;
			}
		} else {
			if (is_registered) {
				unregisterReceiver(receiver);
				is_registered = false;
				
			}
		}
	}
	
	/**
	 * Removes the current PointOverlay and creates a new, empty one.
	 */
	private void replacePointOverlay() {
		
		if (point_overlay != null) {
			mapview.getOverlays().remove(point_overlay);
		}
		
		point_overlay = new ItemizedIconOverlay<Animal>(this, 
														new ArrayList<Animal>(), 
														this);
		mapview.getOverlays().add(point_overlay);
		setLocationOverlayOnTop();
	}
	
	/**
	 * Sets the <code>LocationOverlay</code> on top of the <code>MapView</code>.
	 */
	private void setLocationOverlayOnTop() {
		
		if (location_overlay != null) {
			mapview.getOverlays().remove(location_overlay);
			mapview.getOverlays().add(location_overlay);
		}
	}
	
	/**
	 * Sets the zoom of the <code>MapView</code> to the Zoo of Osnabrueck.
	 */
	private void setZoomToZooExtend() {
		
		GeoPoint center_zoo;
		switch(topic_id) {
			case 1:
				center_zoo = new GeoPoint(52.24965, 8.07440);
				break;
			case 2: case -1:
				center_zoo = new GeoPoint(52.2508, 8.0720);
				break;
			case 3: 
				center_zoo = new GeoPoint(52.2508, 8.0720);
				break;
			case 4: 
				center_zoo = new GeoPoint(52.28534, 8.02283);
				break;
			default:
				center_zoo = new GeoPoint(52.2508, 8.0720);
				break;
		}
	    map_controller.setZoom(16);
	    map_controller.setCenter(center_zoo);
	}
	
	/* *************** Key Event *****************************************/

	/**
	 * Called on key events. If the back key is pressed and no progress dialog
	 * is shown, the <code>AlertDialog</code> for exiting the application 
	 * occurs. 
	 */
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if (keyCode == KeyEvent.KEYCODE_BACK) {
	    	if (progress_dialog != null  && progress_dialog.isShowing()) {
		    	if (thread != null) {
		    		thread.interrupt();
		    	}
	    	} else {
	    		showDialog(DIALOG_QUIT);
	    	}
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	/* *************** getting Result ************************************/
	
	/**
	 * 
	 */
	protected void onActivityResult(int request_code, 
								 	int result_code, 
								 	Intent intent) {
		// check request- and result-code
		if (request_code == 0) {
			
			if (intent != null && intent.hasExtra("topic")) {
				
				topic_id = intent.getExtras().getInt("topic");
				
				if (result_code == RESULT_OK) {
				
					if (topic_id > -1) {
						
						if (topic_id == 0) {
							updateTopic();
						} else {
							if (Network.isAvailable(
										  getApplicationContext())) {
								showProgressLoadData();
							} else {
								Toast.makeText(getApplicationContext(), 
									getResources().getText(R.string.no_network), 
									Toast.LENGTH_LONG).show();
							}
						}
					} 
				} else if (result_code == RESULT_CANCELED) {
					
					if (topic_id == LOAD_ERROR) {
						showDialog(DIALOG_ERROR);
					}
				}
			} 
		}
	}
	
	
	/* *************** OnClick-Methods for Buttons ***********************/
	
	/**
	 * OnClick-method for the 'zoom'-button, called when the user taps on it.
	 * @param view 
	 */
	public void zoomToExtend(View view) {
		setZoomToZooExtend();
	}
	
	/**
	 * OnClick-method for the 'change topic'-button, called when the user taps 
	 * on it.
	 * @param view
	 */
	public void changeTopic(View view) {
		
		//showDialog(DIALOG_CHOOSE_TOPIC);
		
		if (Network.isAvailable(getApplicationContext())) {
			showProgressLoadTopics();
		} else {
			Toast.makeText(getApplicationContext(), 
						   "Keine Internetverbindung " +
						   "verfügbar.", 
						   Toast.LENGTH_LONG).show();
		}
	}
	
//	public void showExample(View view) {
//		
//		 if (!animal_data.equals("nix")) {
//			JSONArray ja_items;
//			Animal animal;
//			try {
//				ja_items = new JSONArray(animal_data);	
//				animal = new Animal(ja_items.getJSONObject(3));
//				curr_animal_id = animal.getAnimalId();
//				curr_animal_all = animal.toJSONObject().toString();
//				curr_animal_name = animal.getName();
//				iv_animal_dialog.setImageBitmap(img_handler.getImage(curr_animal_id));
//				showDialog(DIALOG_ANIMAL_INFO);
//			} catch (JSONException e) {
//						Toast.makeText(getApplicationContext(), 
//			    				animal_data.toString(), Toast.LENGTH_LONG).show();
//			}
//			
//		}
//	}
	
	
	/* *************** OnClick Methods for OverlayItems ******************/

	/**
	 * Called by a long press on an <code>Animal</code> item. Updates the 
	 * current animal data and shows the animal info <code>AlertDialog</code>.
	 * @param arg0
	 * @param animal the tapped <code>Animal</code>
	 */
	public boolean onItemLongPress(int arg0, Animal animal) {
		
		curr_animal_name = animal.getName();
		curr_animal_id = animal.getAnimalId();
		curr_animal_all = animal.toJSONObject().toString();
		iv_animal_dialog.setImageBitmap(img_handler.getImage(curr_animal_id));
		showDialog(DIALOG_ANIMAL_INFO);
		
		return false;
	}

	/**
	 * Called by a single tap on an <code>Animal</code> item. Updates the 
	 * current animal data and shows the animal info <code>AlertDialog</code>.
	 * @param arg0
	 * @param animal the tapped <code>Animal</code>
	 */
	public boolean onItemSingleTapUp(int arg0, Animal animal) {
		
		curr_animal_name = animal.getName();
		curr_animal_id = animal.getAnimalId();
		curr_animal_all = animal.toJSONObject().toString();
		iv_animal_dialog.setImageBitmap(img_handler.getImage(curr_animal_id));
		showDialog(DIALOG_ANIMAL_INFO);
		
		return false;
	}
    
	
	/* *************** Menu **********************************************/

	/**
	 * Creates an options menu with the appropriate layout.
	 * @param menu
	 */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main_menu, menu);    	
    	
    	return true;
    }
    
    /**
     * Called on menu item selection. Starts a new <code>Activity</code> 
     * depending on the selected item.
     * @param item the selected menu item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        
        switch (item.getItemId()) {
        
        	case R.id.main_menu_item1:
        		
        		Intent intent1 = new Intent(MapActivity.this, 
        									HelpActivity.class);
        		MapActivity.this.startActivity(intent1);

        		return true;
        		
        	case R.id.main_menu_item2:
        		Intent intent2 = new Intent(MapActivity.this, 
        									AboutActivity.class);
				MapActivity.this.startActivity(intent2);

        		return true;
        		
        	default:
        		return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This inner class represents a <code>BroadcastReceiver</code> that 
     * should receive system messages which are sent if the user enters a 
     * <code>ProximityAlert</code> area.
     */
	public class ProximityAlertReceiver extends BroadcastReceiver {
		
		/**
		 * Called when a system message is received. The data to the current 
		 * animal species is extracted from the delivered intent. An info dialog
		 * is shown and a marker is put on the map. Also the vibration alarm 
		 * of the device is activated for some milliseconds.
		 * @param context the application context
		 * @param intent the delivered intent
		 */
		@Override
		public void onReceive(Context context, Intent intent) {
			
			
			Bundle extras = intent.getExtras();
			String str_data = "";
			Animal animal = null;
			
			if (extras.getBoolean(LocationManager.KEY_PROXIMITY_ENTERING)) {
				try {
					str_data = extras.getString("animal");
					animal = new Animal(new JSONObject(str_data));
					
				} catch(JSONException e) {
				}
				
				if (animal != null) {
					curr_animal_name = animal.getName();
					curr_animal_all = str_data;
					curr_animal_id = animal.getAnimalId();
					showDialog(DIALOG_ANIMAL_INFO);
					
					animal.setMarker(getResources().getDrawable(
												R.drawable.ic_map_marker_blau));
					point_overlay.addItem(animal);
					
				} else {
					Toast.makeText(getApplicationContext(), 
						"Es liegen keine Daten für diesen Punkt vor!", 
						Toast.LENGTH_LONG).show();
				}
				
				Vibrator vib = (Vibrator) getSystemService(
													Context.VIBRATOR_SERVICE);
				vib.vibrate(200);
			} 
		}
	}
}
