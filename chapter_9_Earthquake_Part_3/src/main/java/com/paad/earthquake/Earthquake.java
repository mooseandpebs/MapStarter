package com.paad.earthquake;
import android.support.v4.app.FragmentActivity;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.SearchView;
import com.google.android.gms.*;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;


public class Earthquake extends FragmentActivity implements
		EarthquakeListFragment.Callback, OnMapReadyCallback {

	private final static String TAG = "Earthquake";
	/**
	 * ATTENTION: This was auto-generated to implement the App Indexing API.
	 * See https://g.co/AppIndexing/AndroidStudio for more information.
	 */
	private GoogleApiClient client;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		// Use the Search Manager to find the SearchableInfo related to this
		// Activity.
		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
	}

	static final private int MENU_PREFERENCES = Menu.FIRST + 1;
	static final private int MENU_UPDATE = Menu.FIRST + 2;
	private static final int SHOW_PREFERENCES = 1;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// super.onCreateOptionsMenu(menu);
		try {
			getMenuInflater().inflate(R.menu.preferences, menu);
			updateFromPreferences();
			SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
			SearchView searchView = (SearchView) menu.findItem(R.id.search_view_id).getActionView();
			searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
			handleIntent(getIntent());
		} catch (Exception e) {
			Log.e(TAG, "err:" + e);
		}
		return true;
	}

	@Override
	public View onCreateView(String name, Context context, AttributeSet attrs) {
		return super.onCreateView(name, context, attrs);
	}

	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onNewIntent(Intent intent) {
		try {
			super.onNewIntent(intent);
			setIntent(intent);
			handleIntent(intent);
		} catch (Exception e) {
			Log.e(TAG, "onNewIntent err:" + e);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}

	public static final String UPDATE_EARTHQUAKE_SERVICE_EXTRA_KEY = "UPDATE_EARTHQUAKE_SERVICE_EXTRA_KEY";

	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case R.id.settings:
				launchOptionSettings();
				return true;
			case R.id.start_update_service:
				Intent intent = new Intent(this, EarthquakeUpdateService.class);
				intent.putExtra(UPDATE_EARTHQUAKE_SERVICE_EXTRA_KEY, true);
				startService(intent);
				return true;
			case R.id.stop_update_service:
				stopService(new Intent(this, EarthquakeUpdateService.class));
				return true;
			case R.id.start_auto_update_service:
				startAutoUpdate();
				return true;
			case R.id.stop_auto_update_service:
				stopAutoUpdate();
				return true;
		}
		return false;
	}

	public int minimumMagnitude = 0;
	public boolean autoUpdateChecked = false;
	public int updateFreq = 0;

	private void updateFromPreferences() {
		Context context = getApplicationContext();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);

		minimumMagnitude = Integer.parseInt(prefs.getString(PreferencesActivity.PREF_MIN_MAG, "3"));
		updateFreq = Integer.parseInt(prefs.getString(PreferencesActivity.PREF_UPDATE_FREQ, "60"));

		autoUpdateChecked = prefs.getBoolean(PreferencesActivity.PREF_AUTO_UPDATE, false);
	}

	static final String PREF_FRAGMENT = "JsonPreferencesFragment";

	void launchOptionSettings() {
		try {
			getFragmentManager().beginTransaction().replace(R.id.container, new EarthquakePreferencesFragment())
					.addToBackStack(PREF_FRAGMENT).commit();
		} catch (Exception e) {
			Log.e(TAG, "launchOptionSettings err:" + e);
		}

	}

	public void handleIntent(Intent intent) {
		try {
			if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
				parseIntent(intent);
			}
		} catch (Exception e) {
			Log.e(TAG, "handleIntent err:" + e);
		}
	}

	public void startAutoUpdate() {
		try {

		} catch (Exception e) {
			Log.e(TAG, "startAutoUpdate err:" + e);
		}

	}

	public void stopAutoUpdate() {
		try {

		} catch (Exception e) {
			Log.e(TAG, "stopAutoUpdate err:" + e);
		}

	}

	private static String QUERY_EXTRA_KEY = "QUERY_EXTRA_KEY";

	private String EARTHQUAKESEARCHRESULTSFRAGMENT = "EarthquakeSearchResultsFragment";

	private void parseIntent(Intent intent) {
		// If the Activity was started to service a Search request,
		// extract the search query.
		try {
			if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
				String searchQuery = intent.getStringExtra(SearchManager.QUERY);
				//EarthquakeSearchResultsFragment frag;
				EarthquakeListFragment frag
						= (EarthquakeListFragment) getFragmentManager()
						.findFragmentByTag(EARTHQUAKESEARCHRESULTSFRAGMENT);
				if (frag == null) {
					frag = new EarthquakeListFragment();
				}
				frag.setQuery(searchQuery);
				if(mMapFragment != null){
					android.support.v4.app.Fragment mFrag = mMapFragment.getFragmentManager()
							.findFragmentById(R.id.EarthQuake_Map_Fragment);
					if(mFrag != null) {
						mMapFragment.getFragmentManager().beginTransaction().hide(mFrag).commit();
					}
				}
				getFragmentManager().beginTransaction().replace(R.id.container, frag,
						EARTHQUAKESEARCHRESULTSFRAGMENT)
						.addToBackStack(EARTHQUAKESEARCHRESULTSFRAGMENT).commit();

			}
		} catch (Exception e) {
			Log.e(TAG, "parseIntent" + e);
		}
	}

	private static final String MAPFRAGMNET = "MapFragment";

	SupportMapFragment mMapFragment;
	Quake mQuakeData;
	public void positionToMapCalled(Quake _Quakedata) {
		try {
			mQuakeData = _Quakedata;
			FrameLayout cont = (FrameLayout)findViewById(R.id.container);
			if(mMapFragment == null) {
				getLayoutInflater().inflate(R.layout.earthquake_map, cont);
				mMapFragment = (SupportMapFragment) getSupportFragmentManager()
						.findFragmentById(R.id.EarthQuake_Map_Fragment);
			}else{
				android.support.v4.app.Fragment mFrag = mMapFragment.getFragmentManager()
						.findFragmentById(R.id.EarthQuake_Map_Fragment);
				if(mFrag != null) {
					mMapFragment.getFragmentManager().beginTransaction()
							.show(mFrag).commit();

				}
				setMapProperties();
			}

			mMapFragment.getMapAsync(this);


		} catch (Exception e) {
			Log.e(TAG, "positionToMapCalled err:" + e);
		}
	}

	static final String MAP_FRAGMENT = "MAP_FRAGMENT";
	private GoogleMap mMap;
	@Override
	public void onMapReady(GoogleMap googleMap) {
		try {
			//mMapFragment.getFragmentManager().beginTransaction().replace(mMapFragment.getView(),mMapFragment)
			//		.addToBackStack(MAP_FRAGMENT).commit();
			mMap = googleMap;
			mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
				@Override
				public void onMapClick(LatLng latLng) {

				}
			});
			setMapProperties();
		} catch (Exception e) {
			Log.e(TAG, "onMapReady err:" + e);
		}
	}
	void setMapProperties()
	{
		try{
			if((mQuakeData == null) || (mMap == null))
			{
				Log.e(TAG,"setMapProperties mQuakeData or/and mMap is null");
				return;
			}
			LatLng quakeLoc = new LatLng(mQuakeData.getLocation().getLatitude(),
					mQuakeData.getLocation().getLongitude());
			Log.d(TAG,"Details:"+mQuakeData.getDetails());
			Log.d(TAG,"Lat:"+mQuakeData.getLocation().getLatitude()+" Long:"
					+mQuakeData.getLocation().getLongitude());
			//quakeLoc = new LatLng(34,100);

			mMap.addMarker(new MarkerOptions().position(quakeLoc).title(mQuakeData.getDetails()));
			mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(quakeLoc,10.0f));

		} catch (Exception e) {
			Log.e(TAG, "setMapProperties err:" + e);
		}
	}


	@Override
	public void onStart() {
		super.onStart();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		client.connect();
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Earthquake Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://com.paad.earthquake/http/host/path")
		);
		AppIndex.AppIndexApi.start(client, viewAction);
	}

	@Override
	public void onStop() {
		super.onStop();

		// ATTENTION: This was auto-generated to implement the App Indexing API.
		// See https://g.co/AppIndexing/AndroidStudio for more information.
		Action viewAction = Action.newAction(
				Action.TYPE_VIEW, // TODO: choose an action type.
				"Earthquake Page", // TODO: Define a title for the content shown.
				// TODO: If you have web page content that matches this app activity's content,
				// make sure this auto-generated web page URL is correct.
				// Otherwise, set the URL to null.
				Uri.parse("http://host/path"),
				// TODO: Make sure this auto-generated app URL is correct.
				Uri.parse("android-app://com.paad.earthquake/http/host/path")
		);
		AppIndex.AppIndexApi.end(client, viewAction);
		client.disconnect();
	}
}