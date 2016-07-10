package com.paad.earthquake;

import java.util.Date;
import java.text.SimpleDateFormat;
import android.location.Location;

public class Quake {
  private Date date;
  private String details;
  private Location location;
  private double magnitude;
  private String link;
  private int id;
  
  public int getId() {return id;}
  public Date getDate() { return date; }
  public String getDetails() { return details; }
  public Location getLocation() { return location; }
  public double getMagnitude() { return magnitude; }
  public String getLink() { return link; }


  public Quake(int _id, Date _d, String _det,Location _loc) {
	    id = _id;
	    date = _d;
	    details = _det;
	    location = new Location(_loc);
	    magnitude = 0;
	    link = "";
	  }

  public Quake(Date _d, String _det, Location _loc, double _mag, String _link) {
    id = -1;
	date = _d;
    details = _det;
    location = new Location(_loc);
    magnitude = _mag;
    link = _link;
  }

  @Override
  public String toString() {
    SimpleDateFormat sdf = new SimpleDateFormat("HH.mm");
    String dateString = sdf.format(date);
    return dateString + ": " + magnitude + " " + details;
  }

}