package devedroid.opensurveyor.data;

import org.osmdroid.api.IGeoPoint;
import org.osmdroid.util.GeoPoint;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;

public class LocationData implements Serializable{
	
	public final double lat, lon, heading, alt;
	
	public LocationData(LocationData loc) {
		lat = loc.lat;
		lon = loc.lon;
		heading = loc.heading;
		alt = loc.alt;
		
	}
	
	public LocationData(android.location.Location loc) {
		lat = loc.getLatitude();
		lon = loc.getLongitude();
		if(loc.hasBearing())
			heading = loc.getBearing();
		else
			heading = Double.NaN;
		if(loc.hasAltitude())
			alt = loc.getAltitude();
		else
			alt = Double.NaN;
	}
	
	public LocationData(IGeoPoint loc) {
		lat = loc.getLatitudeE6()/1.0e6;
		lon = loc.getLongitudeE6()/1.0e6;
		heading = Double.NaN;
		if(loc instanceof GeoPoint) {
			alt = ((GeoPoint)loc).getAltitude();
		} else alt = Double.NaN;
	}
	
	public boolean hasHeading() { return !Double.isNaN(heading); }
	
	public boolean hasAltitude() { return !Double.isNaN(alt); }
	
	
	public void writeLocationTag(XmlSerializer xmlSerializer) throws IOException {
		xmlSerializer.startTag(null, "position");
		xmlSerializer.attribute(null, "lat", String.format(Locale.US, "%.6f", lat));
		xmlSerializer.attribute(null, "lon", String.format(Locale.US, "%.6f", lon));

		if(hasHeading()) {
			xmlSerializer.attribute(null, "heading", String.format(Locale.US, "%.2f", heading));
		}

		if(hasAltitude()) {
			xmlSerializer.attribute(null, "alt", String.format(Locale.US, "%.2f", alt));
		}

		xmlSerializer.endTag(null, "position");
	}

	public GeoPoint getGeoPoint() {
		GeoPoint res = new GeoPoint(lat,lon);
		if(hasAltitude()) res.setAltitude((int) alt);
		return res;
	}

}
