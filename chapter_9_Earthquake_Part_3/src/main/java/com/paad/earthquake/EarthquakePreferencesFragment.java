package com.paad.earthquake;

import java.util.List;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

public class EarthquakePreferencesFragment extends PreferenceFragment 
implements OnSharedPreferenceChangeListener {
	static final String TAG = "EarthquakePreferencesFragment";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.userpreferences);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		view.setBackgroundColor(Color.parseColor("#ffd480"));
		super.onViewCreated(view, savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return super.onCreateView(inflater, container, savedInstanceState);
	}
	
	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		String autoUpdateKey = getResources().getString(R.string.auto_update_key);
		Boolean autoUpdate = null;
		
		if(key.equals(autoUpdateKey))
		{
			autoUpdate = sharedPreferences.getBoolean(key, false);
		}
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getPreferenceManager().getSharedPreferences()
		.registerOnSharedPreferenceChangeListener(this);

		boolean update =
		getPreferenceManager().getSharedPreferences() 
			.getBoolean(getResources().getString(R.string.auto_update_key), false);
		getPreferenceManager();
		boolean update2 =
		PreferenceManager.getDefaultSharedPreferences(getActivity()) 
			.getBoolean(getResources().getString(R.string.auto_update_key), false);
		if(update2){
			Log.i(TAG, "on Resume set");
		}
		
	}
	
}