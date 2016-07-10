package com.paad.earthquake;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.concurrent.ConcurrentHashMap;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class EarthquakeAdapter extends BaseAdapter implements AdapterView.OnItemLongClickListener {

	private ConcurrentHashMap<Integer, Quake> mQuakeMap 
		= new ConcurrentHashMap<Integer, Quake>();
	private final static String TAG = "EarthquakeAdapter";
	public EarthquakeAdapter() {
		super();
		
	}
	@Override
	public int getCount() {
		
		return mQuakeMap.size();
	}

	@Override
	public Object getItem(int position) {
		return mQuakeMap.get(position);
	}

	@Override
	public long getItemId(int position) {
		int id = ((Quake)mQuakeMap.get(position)).getId();
		return id;
	}

	private static final
	SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		RelativeLayout quakeListRecView=null;
		try {
			if(convertView == null)
			{
				quakeListRecView = new RelativeLayout(parent.getContext());
				LayoutInflater li = (LayoutInflater)parent.getContext()
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				li.inflate(R.layout.quake_list_item,quakeListRecView,true);
			}else{
					quakeListRecView = (RelativeLayout)convertView;
			}
			final TextView quakeListId = (TextView)quakeListRecView
					.findViewById(R.id.QuakeListId);
			final TextView quakeListDate = (TextView)quakeListRecView
					.findViewById(R.id.QuakeListDate);
			final TextView quakeListSummary = (TextView)quakeListRecView
					.findViewById(R.id.QuakeListSummary);
			
			Object o = getItem(position);
			Quake q = null;
			if(o instanceof Quake)
			{
				q = (Quake)o;
			}else{
				Log.e(TAG, "getView: object did not covert to Quake");
				return quakeListRecView;
			}
			
			quakeListId.setText(Integer.toString(q.getId()));
			String dateStr = mDateFormat.format(q.getDate());
			quakeListDate.setText(dateStr);
			quakeListSummary.setText(q.getDetails());
		} catch (Exception e) {
			Log.e(TAG, "getView err:"+e);
		}
		return quakeListRecView;
	}
	
	public void swapCursor(Cursor _Cursor)
	{
		try {
			Quake q;
			int key=0;
            java.util.Date d;
            String dateStr;
            String info;
            int db = 0;
            int idx = 0;
            int id =0;
            String lat;
            String lon;
            Location loc = new Location("");
            mQuakeMap.clear();
			while(_Cursor.moveToNext())
			{
				try {
					idx =	_Cursor.getInt(0);
					db++;
					dateStr = _Cursor.getString(1);
					
					d = mDateFormat.parse(dateStr);
					db++;
					info = _Cursor.getString(2);
					lat = _Cursor.getString(3);
					lon = _Cursor.getString(4);
					loc.setLatitude(Double.parseDouble(lat));
					loc.setLongitude(Double.parseDouble(lon));
					db++;
					q = new Quake(idx,d,info,loc);
					db++;
					mQuakeMap.put(key, q);
					key++;;					
				} catch (Exception e) {
					Log.e(TAG, "swapCursor parsing data debug="+db+" err:"+e);
				}
			}
			notifyDataSetChanged();
		} catch (Exception e) {
			Log.e(TAG, "swapCursor err:"+e);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
		return false;
	}
}
