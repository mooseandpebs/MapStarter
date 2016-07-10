package com.paad.earthquake;

import android.R.color;
import android.app.Activity;
import android.app.ListFragment;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class EarthquakeListFragment extends ListFragment 
	implements LoaderManager.LoaderCallbacks<Cursor>
{

	EarthquakeAdapter mEarthquakeAdapter;
	Callback mCallback;
	private String mQuery;

	public interface Callback {
		public void positionToMapCalled(Quake _Quakedata);
	}

	public void setQuery(String _Query) {
		mQuery = _Query;
	}

	public String getQuery() {
		return (mQuery);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		try {
			super.onCreate(savedInstanceState);
			mEarthquakeAdapter = new EarthquakeAdapter();
			setListAdapter(mEarthquakeAdapter);
		} catch (Exception e) {
			Log.e(TAG, "onCreate err:"+e);
		}
	}


	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		if (!(activity instanceof Callback)) {
			throw new IllegalStateException("Activity must implement fragments Callback interface");
		}
		mCallback = (Callback) activity;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Create a new Adapter and bind it to the List View
		mEarthquakeAdapter = new EarthquakeAdapter();
		setListAdapter(mEarthquakeAdapter);

		getLoaderManager().initLoader(0, null, this);
		refreshEarthquakes();
/*		Thread t = new Thread(new Runnable() {
			public void run() {
				refreshEarthquakes();
			}
		});
		t.start();
*/
	}

	private static final String TAG = "EarthquakeListFragment";

	Handler handler = new Handler();

	public void refreshEarthquakes() {
		handler.post(new Runnable() {
			public void run() {
				getLoaderManager().restartLoader(0, null, EarthquakeListFragment.this);
			}
		});

		getActivity().startService(new Intent(getActivity(), EarthquakeUpdateService.class));
	}

	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		try{
		String[] projection = { EarthquakeProvider.KEY_ID,EarthquakeProvider.KEY_DATE, 
		        EarthquakeProvider.KEY_SUMMARY
		        ,EarthquakeProvider.KEY_LOCATION_LAT
		        ,EarthquakeProvider.KEY_LOCATION_LNG };
		    String where = EarthquakeProvider.KEY_SUMMARY
		                     + " LIKE \"%" + mQuery + "%\"";
		    String[] whereArgs = null;
		    String sortOrder = EarthquakeProvider.KEY_SUMMARY + " COLLATE LOCALIZED ASC";
		    
		    // Create the new Cursor loader.
		    return new CursorLoader(getActivity(), EarthquakeProvider.CONTENT_URI,
		            projection, where, whereArgs,
		            sortOrder);
		}catch(Exception e){
			Log.e(TAG, "onCreateLoader err:"+e);
		}
		return null;
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
		try{
			mEarthquakeAdapter.swapCursor(cursor);
		}catch(Exception e)
		{
			Log.e(TAG, "onLoadFinished err"+e);
		}
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		mEarthquakeAdapter.swapCursor(null);
	}

	private static String QUERY_EXTRA_KEY = "QUERY_EXTRA_KEY";

	private void sendQuery() {
		// Perform the search, passing in the search query as an argument
		// to the Cursor Loader
		if (!mQuery.isEmpty()) {
			Bundle args = new Bundle();
			args.putString(QUERY_EXTRA_KEY, mQuery);

			// Restart the Cursor Loader to execute the new query.
			getLoaderManager().restartLoader(0, args, this);
		} else {
			Log.e(TAG, "query was empty in send Query");
		}
	}
	ListView rootView;
	LinearLayout mHeader; 

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		try {
			rootView = (ListView)inflater.
					inflate(R.layout.listfragment_view,container,false);
			rootView.setBackgroundColor(color.holo_blue_light);
			rootView.setFooterDividersEnabled(true);
			View hv = inflater.inflate(R.layout.header_view,null);
			mHeader = (LinearLayout)hv.findViewById(R.id.header_view);
			rootView.addHeaderView(mHeader);
			View fv = inflater.inflate(R.layout.footer_views,null);
			rootView.addFooterView(fv);
			rootView.setFooterDividersEnabled(true);
			setListAdapter(mEarthquakeAdapter);
		} catch (Exception e) {
			Log.e(TAG, "onCreateView err:"+e);
		}		
		
		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
		refreshEarthquakes();
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		if(v instanceof RelativeLayout)
		{
			Object o = mEarthquakeAdapter.getItem(position-1);
			if(o instanceof Quake)
			{
				Quake q = (Quake)o;
				Location loc = q.getLocation();
				mCallback.positionToMapCalled(q);
			}
		}
	}

}