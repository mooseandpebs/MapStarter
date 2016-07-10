package com.paad.earthquake;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.location.Location;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;

public class EarthquakeUpdateService extends IntentService {

  public static String TAG = "EARTHQUAKE_UPDATE_SERVICE";
  
  public EarthquakeUpdateService() {
    super("EarthquakeUpdateService");
  }

  public EarthquakeUpdateService(String name) {
    super(name);
  }
  
  @Override
  protected void onHandleIntent(Intent intent) {
	try{
		refreshEarthquakes();
	}catch(Exception e)
	{
		Log.e(TAG,"onHandleIntent refreshEarthquakes err:"+e);
	}
	if(intent.hasExtra(Earthquake.UPDATE_EARTHQUAKE_SERVICE_EXTRA_KEY))
	{
		try{
			startOrStopAlarm();
		}catch(Exception e)
		{
			Log.e(TAG,"onHandleIntent startOrStopAlarm err:"+e);
		}
	}
	Log.i(TAG, " EartquakeUpdateService Running");
  }

  @Override
  public IBinder onBind(Intent intent) {
    return null;
  }
	private static final
	SimpleDateFormat mDateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
  
  private void addNewQuake(Quake quake) {
	  
    try {
		ContentResolver cr = getContentResolver();

		// Construct a where clause to make sure we don't already have this
		// earthquake in the provider.
		String w = EarthquakeProvider.KEY_DATE + " = " +"'"+mDateFormat.format(quake.getDate())+"'";
		
		// If the earthquake is new, insert it into the provider.
		Cursor query = cr.query(EarthquakeProvider.CONTENT_URI, null, w, null, null);
		
		if (query.getCount()==0) {
		  ContentValues values = new ContentValues();

		  //values.put(EarthquakeProvider.KEY_DATE, quake.getDate().getTime());
		  values.put(EarthquakeProvider.KEY_DATE, mDateFormat.format(quake.getDate()));
		  values.put(EarthquakeProvider.KEY_DETAILS, quake.getDetails());   
		  values.put(EarthquakeProvider.KEY_SUMMARY, quake.toString());

		  double lat = quake.getLocation().getLatitude();
		  double lng = quake.getLocation().getLongitude();
		  values.put(EarthquakeProvider.KEY_LOCATION_LAT, lat);
		  values.put(EarthquakeProvider.KEY_LOCATION_LNG, lng);
		  values.put(EarthquakeProvider.KEY_LINK, quake.getLink());
		  values.put(EarthquakeProvider.KEY_MAGNITUDE, quake.getMagnitude());

		  cr.insert(EarthquakeProvider.CONTENT_URI, values);
		}
		query.close();
	} catch (Exception e) {
		Log.e(TAG, "addNewQuake err:"+e);
	}
  }

  public void refreshEarthquakes() {
    // Get the XML
    URL url;
    try {
      String quakeFeed = getString(R.string.quake_feed);
      url = new URL(quakeFeed);

      URLConnection connection;
      connection = url.openConnection();

      HttpURLConnection httpConnection = (HttpURLConnection)connection;
      int responseCode = httpConnection.getResponseCode();

      if (responseCode == HttpURLConnection.HTTP_OK) {
        InputStream in = httpConnection.getInputStream();

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();

        // Parse the earthquake feed.
        Document dom = db.parse(in);
        Element docEle = dom.getDocumentElement();

        // Get a list of each earthquake entry.
        String title = "";
        NodeList nl = docEle.getElementsByTagName("event");
        int dbg =0;
        try{
        	
	        if (nl != null && nl.getLength() > 0) {
	          
	          for (int i = 0 ; i < nl.getLength(); i++) {
	        	dbg = 0;
	            Element entry = (Element)nl.item(i);
	            Element titleE = (Element)entry.getElementsByTagName("description").item(0);
	            Element titleTxtE = (Element)titleE.getElementsByTagName("text").item(0);
	            title = titleTxtE.getTextContent();
	            dbg++;
	            Element magStrE=null;
	            Element g=null;
	            try{
		            g = (Element)entry.getElementsByTagName("origin").item(0);
		            magStrE =  ((Element)((Element)entry.getElementsByTagName("magnitude").item(0))
		            		.getElementsByTagName("mag").item(0));
	            }catch(Exception ee)
	            {
	            	Log.e(TAG, "err:"+ee);
	            }
	            dbg++;
	            String magStr ="";
	            String maxSTD = "";
	            try{
	            	
		            Element magStrValueE = (Element)magStrE.getElementsByTagName("value").item(0);
		            Element magStrSTDE = (Element)magStrE.getElementsByTagName("uncertainty").item(0);
		            if(magStrValueE != null)
		            {
		            	magStr = magStrValueE.getTextContent();
		            }else{
		            	magStr = "NA";
		            }
		            
		            if(magStrSTDE != null){
			            maxSTD = magStrSTDE.getTextContent();		            	
		            }else{
		            	maxSTD = "NA";
		            }
	            }catch(Exception eee){
	            	Log.e(TAG, "err:"+eee);
	            }
	            dbg++;
	            /*
	            Element when = (Element)entry.getElementsByTagName("updated").item(0);
	            Element link = (Element)entry.getElementsByTagName("link").item(0);
	            */
	            
	            NodeList gnl = g.getChildNodes();
	            dbg++;
	            
	            String linkString = g.getAttribute("publicID");
	            String dt = gnl.item(0).getTextContent();
	            String[] location = new String []{gnl.item(2).getTextContent(),gnl.item(1).getTextContent()};
	            dbg++;
	
	            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.sss'Z'");
	            Date qdate = new GregorianCalendar(0,0,0).getTime();
	            try {
	              qdate = sdf.parse(dt);
	            } catch (ParseException e) {
	              Log.d(TAG, "Date parsing exception.", e);
	            }
	            dbg++;
	
	            Location l = new Location("dummyGPS");
	            l.setLatitude(Double.parseDouble(location[0]));
	            l.setLongitude(Double.parseDouble(location[1]));
	            dbg++;
	            l.setTime(qdate.getTime());
	            double magnitude = Double.parseDouble(magStr);
	            Quake quake = new Quake(qdate, title, l, magnitude, linkString);
	
	            // Process a newly found earthquake
	            addNewQuake(quake);
	          }
	        }
        }catch (Exception e) {
        	Log.e(TAG, "refreshEarthquakes title:"+title+" err:"+e);
        }
      }
    } catch (MalformedURLException e) {
      Log.e(TAG, "Malformed URL Exception", e);
    } catch (IOException e) {
      Log.e(TAG, "IO Exception", e);
    } catch (ParserConfigurationException e) {
      Log.e(TAG, "Parser Configuration Exception", e);
    } catch (SAXException e) {
      Log.e(TAG, "SAX Exception", e);
    }
    finally {
    }
  }
  
  
  public void startOrStopAlarm() {
    // Retrieve the shared preferences
    Context context = getApplicationContext();
    SharedPreferences prefs = 
      PreferenceManager.getDefaultSharedPreferences(context);
    String updateFreqKey = getResources().getString(R.string.update_freq_key);
    String autoUpdateFreqKey = getResources().getString(R.string.auto_update_key);
    int updateFreq = 
      Integer.parseInt(prefs.getString(updateFreqKey, "60"));

    mAlarmIntent = new Intent(context,com.paad.earthquake.EarthquakeAlarmReceiver.class);
    alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

    boolean autoUpdateChecked = 
      prefs.getBoolean(autoUpdateFreqKey, false);
    PendingIntent alarmPendingIntent = PendingIntent.getBroadcast(context, mAlarmID
    		,mAlarmIntent
    		,PendingIntent.FLAG_NO_CREATE);
    
    
    if(alarmPendingIntent != null)
    {
        alarmPendingIntent = PendingIntent.getBroadcast(context, mAlarmID
        		,mAlarmIntent
        		,PendingIntent.FLAG_UPDATE_CURRENT);
    }
    else
    {
        alarmPendingIntent = PendingIntent.getBroadcast(context, mAlarmID
        		,mAlarmIntent
        		,0);    	
    }
    if (autoUpdateChecked) {
      int alarmType = AlarmManager.ELAPSED_REALTIME_WAKEUP;
      long timeToRefresh = SystemClock.elapsedRealtime() +
                           updateFreq*60*1000;
      
      alarmManager.setInexactRepeating(alarmType, timeToRefresh,
                                       updateFreq*60*1000, alarmPendingIntent); 
      Log.i(TAG, "Alarm started update Freq="+updateFreq);
    }
    else if (!autoUpdateChecked)
    {
      alarmManager.cancel(alarmPendingIntent);
      alarmPendingIntent.cancel();
      Log.i(TAG, "Alarm Stopped");
    }
  };


  private AlarmManager alarmManager;
  private PendingIntent mPendingAlarmIntent;
  private static final int mAlarmID = 69;
  private Intent mAlarmIntent;
  @Override
  public void onCreate() {
    super.onCreate();
  }

}