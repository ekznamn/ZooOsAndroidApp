
package zoo.themenroute;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

/**
 * The About-Activity provides the current App-Version and information about 
 * the development.
 * 
 * @version 1.0
 * @author Nina Manzke
 * @date 26.09.2011
 */
public class TopicsActivity extends Activity {
	
	private RadioGroup 	rg_topics;
	private TextView 	tv_t1;
	private TextView 	tv_t2;
	private TextView 	tv_t3;
	private TextView 	tv_t4;
	private TextView 	tv_t5;
	private TextView 	tv_t6;
	private RadioButton rb_t1;
	private RadioButton rb_t2;
	private RadioButton rb_t3;
	private RadioButton rb_t4;
	private RadioButton rb_t5;
	private RadioButton rb_t6;
	private View		d1;
	private View		d2;
	private View		d3;
	private View		d4;
	private View		d5;
	private View		d6;
	
	
	private int 		topic_id = -1;
	private String 		topic = "";
	
	
	/** 
	 * Called on activity's first creation.
	 * @param savedInstanceState 
	 */
	@Override
    public void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.topics);
		
		rg_topics = (RadioGroup) findViewById(R.id.topics_radios);
		
		tv_t1 = (TextView) findViewById(R.id.topics_tv1);
		tv_t2 = (TextView) findViewById(R.id.topics_tv2);
		tv_t3 = (TextView) findViewById(R.id.topics_tv3);
		tv_t4 = (TextView) findViewById(R.id.topics_tv4);
		tv_t5 = (TextView) findViewById(R.id.topics_tv5);
		tv_t6 = (TextView) findViewById(R.id.topics_tv6);
		
		rb_t1 = (RadioButton) findViewById(R.id.topics_1);
		rb_t2 = (RadioButton) findViewById(R.id.topics_2);
		rb_t3 = (RadioButton) findViewById(R.id.topics_3);
		rb_t4 = (RadioButton) findViewById(R.id.topics_4);
		rb_t5 = (RadioButton) findViewById(R.id.topics_5);
		rb_t6 = (RadioButton) findViewById(R.id.topics_6);
		
		d1 = (View) findViewById(R.id.div_1);
		d2 = (View) findViewById(R.id.div_2);
		d3 = (View) findViewById(R.id.div_3);
		d4 = (View) findViewById(R.id.div_4);
		d5 = (View) findViewById(R.id.div_5);
		d6 = (View) findViewById(R.id.div_6);
		
		if (getIntent().hasExtra("data")) {
			
//			JSONObject jo_topic;
//			JSONObject jo_data;
			final JSONArray ja_data;
			
			String data = getIntent().getExtras().getString("data");
			
			try {
				ja_data = new JSONArray(data);
				
				switch (ja_data.length()) {
				
				case 1:
					rb_t1.setVisibility(RadioButton.VISIBLE);
					d1.setVisibility(View.VISIBLE);
					rb_t1.setText(ja_data.getJSONObject(0).getString("topic"));
					tv_t1.setText(ja_data.getJSONObject(0).getString("description"));
					break;
				case 2:
					rb_t1.setVisibility(RadioButton.VISIBLE);
					rb_t2.setVisibility(RadioButton.VISIBLE);
					d1.setVisibility(View.VISIBLE);
					d2.setVisibility(View.VISIBLE);
					rb_t1.setText(ja_data.getJSONObject(0).getString("topic"));
					rb_t2.setText(ja_data.getJSONObject(1).getString("topic"));
					tv_t1.setText(ja_data.getJSONObject(0).getString("description"));
					tv_t2.setText(ja_data.getJSONObject(1).getString("description"));
					break;
				case 3:
					rb_t1.setVisibility(RadioButton.VISIBLE);
					rb_t2.setVisibility(RadioButton.VISIBLE);
					rb_t3.setVisibility(RadioButton.VISIBLE);
					d1.setVisibility(View.VISIBLE);
					d2.setVisibility(View.VISIBLE);
					d3.setVisibility(View.VISIBLE);
					rb_t1.setText(ja_data.getJSONObject(0).getString("topic"));
					rb_t2.setText(ja_data.getJSONObject(1).getString("topic"));
					rb_t3.setText(ja_data.getJSONObject(2).getString("topic"));
					tv_t1.setText(ja_data.getJSONObject(0).getString("description"));
					tv_t2.setText(ja_data.getJSONObject(1).getString("description"));
					tv_t3.setText(ja_data.getJSONObject(2).getString("description"));
					break;
				case 4:
					rb_t1.setVisibility(RadioButton.VISIBLE);
					rb_t2.setVisibility(RadioButton.VISIBLE);
					rb_t3.setVisibility(RadioButton.VISIBLE);
					rb_t4.setVisibility(RadioButton.VISIBLE);
					d1.setVisibility(View.VISIBLE);
					d2.setVisibility(View.VISIBLE);
					d3.setVisibility(View.VISIBLE);
					d4.setVisibility(View.VISIBLE);
					rb_t1.setText(ja_data.getJSONObject(0).getString("topic"));
					rb_t2.setText(ja_data.getJSONObject(1).getString("topic"));
					rb_t3.setText(ja_data.getJSONObject(2).getString("topic"));
					rb_t4.setText(ja_data.getJSONObject(3).getString("topic"));
					tv_t1.setText(ja_data.getJSONObject(0).getString("description"));
					tv_t2.setText(ja_data.getJSONObject(1).getString("description"));
					tv_t3.setText(ja_data.getJSONObject(2).getString("description"));
					tv_t4.setText(ja_data.getJSONObject(3).getString("description"));
					break;
				case 5:
					rb_t1.setVisibility(RadioButton.VISIBLE);
					rb_t2.setVisibility(RadioButton.VISIBLE);
					rb_t3.setVisibility(RadioButton.VISIBLE);
					rb_t4.setVisibility(RadioButton.VISIBLE);
					rb_t5.setVisibility(RadioButton.VISIBLE);
					d1.setVisibility(View.VISIBLE);
					d2.setVisibility(View.VISIBLE);
					d3.setVisibility(View.VISIBLE);
					d4.setVisibility(View.VISIBLE);
					d5.setVisibility(View.VISIBLE);
					rb_t1.setText(ja_data.getJSONObject(0).getString("topic"));
					rb_t2.setText(ja_data.getJSONObject(1).getString("topic"));
					rb_t3.setText(ja_data.getJSONObject(2).getString("topic"));
					rb_t4.setText(ja_data.getJSONObject(3).getString("topic"));
					rb_t5.setText(ja_data.getJSONObject(4).getString("topic"));
					tv_t1.setText(ja_data.getJSONObject(0).getString("description"));
					tv_t2.setText(ja_data.getJSONObject(1).getString("description"));
					tv_t3.setText(ja_data.getJSONObject(2).getString("description"));
					tv_t4.setText(ja_data.getJSONObject(3).getString("description"));
					tv_t5.setText(ja_data.getJSONObject(4).getString("description"));
					break;
				case 6:
					rb_t1.setVisibility(RadioButton.VISIBLE);
					rb_t2.setVisibility(RadioButton.VISIBLE);
					rb_t3.setVisibility(RadioButton.VISIBLE);
					rb_t4.setVisibility(RadioButton.VISIBLE);
					rb_t5.setVisibility(RadioButton.VISIBLE);
					rb_t6.setVisibility(RadioButton.VISIBLE);
					d1.setVisibility(View.VISIBLE);
					d2.setVisibility(View.VISIBLE);
					d3.setVisibility(View.VISIBLE);
					d4.setVisibility(View.VISIBLE);
					d5.setVisibility(View.VISIBLE);
					d6.setVisibility(View.VISIBLE);
					rb_t1.setText(ja_data.getJSONObject(0).getString("topic"));
					rb_t2.setText(ja_data.getJSONObject(1).getString("topic"));
					rb_t3.setText(ja_data.getJSONObject(2).getString("topic"));
					rb_t4.setText(ja_data.getJSONObject(3).getString("topic"));
					rb_t5.setText(ja_data.getJSONObject(4).getString("topic"));
					rb_t6.setText(ja_data.getJSONObject(5).getString("topic"));
					tv_t1.setText(ja_data.getJSONObject(0).getString("description"));
					tv_t2.setText(ja_data.getJSONObject(1).getString("description"));
					tv_t3.setText(ja_data.getJSONObject(2).getString("description"));
					tv_t4.setText(ja_data.getJSONObject(3).getString("description"));
					tv_t5.setText(ja_data.getJSONObject(4).getString("description"));
					tv_t6.setText(ja_data.getJSONObject(5).getString("description"));
					break;
				}
				
				rg_topics.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					public void onCheckedChanged(RadioGroup rg, int checked_id) {
						try {
							switch(checked_id) {
							
							case R.id.topics_1: 
								topic_id = ja_data.getJSONObject(0).getInt("id");
								topic = ja_data.getJSONObject(0).getString("topic");
								tv_t1.setVisibility(TextView.VISIBLE); 
								tv_t2.setVisibility(TextView.GONE); 
								tv_t3.setVisibility(TextView.GONE);
								tv_t4.setVisibility(TextView.GONE);
								tv_t5.setVisibility(TextView.GONE);
								tv_t6.setVisibility(TextView.GONE);
								break;
							case R.id.topics_2:
								topic_id = ja_data.getJSONObject(1).getInt("id");
								topic = ja_data.getJSONObject(1).getString("topic");
								tv_t1.setVisibility(TextView.GONE); 
								tv_t2.setVisibility(TextView.VISIBLE); 
								tv_t3.setVisibility(TextView.GONE);
								tv_t4.setVisibility(TextView.GONE);
								tv_t5.setVisibility(TextView.GONE);
								tv_t6.setVisibility(TextView.GONE);
								break;
							case R.id.topics_3: 
								topic_id = ja_data.getJSONObject(2).getInt("id");
								topic = ja_data.getJSONObject(2).getString("topic");
								tv_t1.setVisibility(TextView.GONE);
								tv_t2.setVisibility(TextView.GONE);
								tv_t3.setVisibility(TextView.VISIBLE);
								tv_t4.setVisibility(TextView.GONE);
								tv_t5.setVisibility(TextView.GONE);
								tv_t6.setVisibility(TextView.GONE);
								break;
							case R.id.topics_4:
								topic_id = ja_data.getJSONObject(3).getInt("id");
								topic = ja_data.getJSONObject(3).getString("topic");
								tv_t1.setVisibility(TextView.GONE); 
								tv_t2.setVisibility(TextView.GONE); 
								tv_t3.setVisibility(TextView.GONE);
								tv_t4.setVisibility(TextView.VISIBLE);
								tv_t5.setVisibility(TextView.GONE);
								tv_t6.setVisibility(TextView.GONE);
								break;
							case R.id.topics_5:
								topic_id = ja_data.getJSONObject(4).getInt("id");
								topic = ja_data.getJSONObject(4).getString("topic");
								tv_t1.setVisibility(TextView.GONE); 
								tv_t2.setVisibility(TextView.GONE); 
								tv_t3.setVisibility(TextView.GONE);
								tv_t4.setVisibility(TextView.GONE);
								tv_t5.setVisibility(TextView.VISIBLE);
								tv_t6.setVisibility(TextView.GONE);
								break;
							case R.id.topics_6: 
								topic_id = ja_data.getJSONObject(5).getInt("id");
								topic = ja_data.getJSONObject(5).getString("topic");
								tv_t1.setVisibility(TextView.GONE); 
								tv_t2.setVisibility(TextView.GONE); 
								tv_t3.setVisibility(TextView.GONE);
								tv_t4.setVisibility(TextView.GONE);
								tv_t5.setVisibility(TextView.GONE);
								tv_t6.setVisibility(TextView.VISIBLE);
								break;
							}
						} catch (JSONException e) {
							Log.e("JSON Error", "Cannot read topic id from JSONArray");
							
							Intent intent = new Intent();
							intent.putExtra("topic_id", MapActivity.LOAD_ERROR);
							setResult(RESULT_CANCELED, intent);
							TopicsActivity.this.finish();
						}
					} 
				});
				
			} catch(JSONException e) {
				Log.e("JSON Error", data);
				
				Intent intent = new Intent();
				intent.putExtra("topic_id", MapActivity.LOAD_ERROR);
				setResult(RESULT_CANCELED, intent);
				TopicsActivity.this.finish();
			}
		}
	}
	
	/**
	 * 
	 * @param view
	 */
	public void submit(View view) {
		
		Intent intent = new Intent();
		intent.putExtra("topic_id", topic_id);
		intent.putExtra("topic", topic);
		setResult(RESULT_OK, intent);
		TopicsActivity.this.finish();
		
	}
	
	/**
	 * 
	 * @param view
	 */
	public void cancel(View view) {
		
		setResult(RESULT_CANCELED);
		TopicsActivity.this.finish();
		
	}
	
	
}