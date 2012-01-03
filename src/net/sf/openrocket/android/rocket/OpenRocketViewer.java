package net.sf.openrocket.android.rocket;


import java.io.File;

import net.sf.openrocket.R;
import net.sf.openrocket.android.Application;
import net.sf.openrocket.android.PreferencesActivity;
import net.sf.openrocket.android.motor.MotorHierarchicalBrowser;
import net.sf.openrocket.android.simulation.SimulationViewer;
import net.sf.openrocket.document.OpenRocketDocument;
import net.sf.openrocket.document.Simulation;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class OpenRocketViewer extends Activity {

	private static final String TAG = "OpenRocketViewer";

	TextView header;
	ListView simulationList;
	
	Application app;


	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Log.d(TAG,"In onCreate");
		
		app = (Application) this.getApplication();

		setContentView(R.layout.openrocketviewer);
		
		header = (TextView) findViewById(R.id.heading);
		simulationList = (ListView) findViewById(R.id.rocketSimulations);

		Intent i = getIntent();
		Uri file = i.getData();
		String path = file.getPath();
		Log.d(TAG,"Use ork file: " + file);
		File orkFile = new File(path);
		final OpenRocketLoaderTask task = new OpenRocketLoaderTask() {

			/* (non-Javadoc)
			 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
			 */
			@Override
			protected void onPostExecute(OpenRocketDocument result) {
				super.onPostExecute(result);
				app.setRocketDocument( result );
				updateContents();
			}
			
		};
		
		task.execute(orkFile);
	}

	private void updateContents() {
		
		OpenRocketDocument rocket = app.getRocketDocument();
		header.setText( rocket.getRocket().getName());
		
		ArrayAdapter<Simulation> sims = new ArrayAdapter<Simulation>(this,android.R.layout.simple_list_item_1,rocket.getSimulations()) {

			@Override
			public View getView(int position, View convertView,
					ViewGroup parent) {
				View v = convertView;
				if ( v == null ) {
					LayoutInflater li = getLayoutInflater();
					v = li.inflate(android.R.layout.simple_list_item_1,null);
				}
				Simulation sim = this.getItem(position);
				((TextView)v.findViewById(android.R.id.text1)).setText( sim.getName() );
				return v;
			}

		};
		simulationList.setOnItemClickListener( new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView l, View v, int position, long id) {
				Intent i = new Intent(OpenRocketViewer.this, SimulationViewer.class);
				Log.d(TAG,"onItemClick simulation number " + id );
				i.putExtra("Simulation",(int)id);
				startActivityForResult(i, 1/*magic*/);
			}
			
		});
		simulationList.setAdapter(sims);

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.rocket_viewer_option_menu, menu);
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		Log.d(TAG,"onMenuItemSelected" + item.getItemId());
		switch(item.getItemId()) {
		case R.id.motor_list_menu_option:
			startMotorBrowser();
			return true;
		case R.id.preference_menu_option:
			Intent intent = new Intent().setClass(this, PreferencesActivity.class);
			this.startActivity(intent);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	public void startMotorBrowser() {
		Log.d(TAG,"motorBrowserButton clicked");
    	Intent i = new Intent(OpenRocketViewer.this, MotorHierarchicalBrowser.class);
    	startActivity(i);
    }



}
