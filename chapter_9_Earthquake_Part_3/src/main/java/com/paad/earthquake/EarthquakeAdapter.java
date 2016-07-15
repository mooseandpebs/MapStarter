package com.paad.earthquake;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import android.R.integer;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
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
import android.widget.ArrayAdapter;

public class EarthquakeAdapter extends BaseAdapter implements AdapterView.OnItemLongClickListener {

	private List<Quake> mQuakeMap;

	private final static String TAG = "EarthquakeAdapter";

	public EarthquakeAdapter() {
		super();
		mQuakeMap = Collections.synchronizedList(new ArrayList<Quake>());
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
		return mQuakeMap.get(position).getId();
	}

	private static final
	SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
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
			String details = q.getDetails()+ "     ";
			quakeListSummary.setText(details);
			if(q.getMagnitude()>4)
			{
				quakeListDate.setBackgroundColor(Color.RED);
			}else{
				quakeListDate.setBackgroundColor(Color.WHITE);
			}

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
			double mag = 0.0;
            Location loc = new Location("");
            mQuakeMap.clear();
			while(_Cursor.moveToNext())
			{
				try {
					idx =	_Cursor.getInt(0);
					dateStr = _Cursor.getString(1);
					d = mDateFormat.parse(dateStr);
					info = _Cursor.getString(2);
					lat = _Cursor.getString(3);
					lon = _Cursor.getString(4);
					loc.setLatitude(Double.parseDouble(lat));
					loc.setLongitude(Double.parseDouble(lon));
					mag = _Cursor.getDouble(5);
					q = new Quake(idx,d,info,loc,mag);

					mQuakeMap.add(key, q);
					key++;
				} catch (Exception e) {
					Log.e(TAG, "swapCursor parsing data debug="+db+" err:"+e);
				}
			}
            sortByDate();
			notifyDataSetChanged();
		} catch (Exception e) {
			Log.e(TAG, "swapCursor err:"+e);
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
		return false;
	}
    private boolean mSortAscending=true;
    void sortByDate()
    {
        if(mSortAscending) {
            Collections.sort(mQuakeMap, new HeaderDateSortAscending());
            mSortAscending = false;
        }else{
            Collections.sort(mQuakeMap, new HeaderDateSortDescending());
            mSortAscending = true;
        }
        notifyDataSetChanged();
    }
    public class HeaderDateSortDescending implements Comparator<Quake>
    {
        public int compare(Quake lhs,Quake rhs)
        {
            if(lhs.getDate().before(rhs.getDate())) {
                return 1;
            }else if(lhs.getDate().after(rhs.getDate()))
            {
                return -1;
            }
            else
            {
                return 0;
            }
        }
    }
    public class HeaderDateSortAscending implements Comparator<Quake>
    {
        public int compare(Quake lhs,Quake rhs)
        {
            if(lhs.getDate().before(rhs.getDate())) {
                return -1;
            }else if(lhs.getDate().after(rhs.getDate()))
            {
                return 1;
            }
            else
            {
                return 0;
            }
        }
    }
}
