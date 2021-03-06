package devedroid.opensurveyor.data;

import android.util.Xml;
import android.widget.Toast;

import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import devedroid.opensurveyor.Utils;

public class Session implements Serializable {

	private long startTime, endTime=-1;
	private int externalCount;
	
	private List<Marker> markers = new ArrayList<Marker>();

	private List<TrackPoint> track = new ArrayList<TrackPoint>();
	
	public Session() {
		startTime = System.currentTimeMillis();
	}
	
	public void addMarker(Marker poi) {
		markers.add(poi);
		if(poi instanceof MarkerWithExternals) externalCount++;
	}
	
	public void finish() {
		endTime = System.currentTimeMillis();
	}
	
	public boolean isRunning() {
		return endTime==-1;		
	}
	
	public static final String FILE_EXT = ".svx";
	public static final String FILE_EXT_ARCHIVE = ".svp";
	
	public void exportArchive(File file) throws IOException {
		FileOutputStream fo = new FileOutputStream(file);
		ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(fo));
		ZipEntry ee = new ZipEntry("survey"+FILE_EXT);
		out.putNextEntry(ee);
		writeTo(new OutputStreamWriter(out));
		out.closeEntry();
		for(Marker p: markers) {
			if(p instanceof MarkerWithExternals) {
				((MarkerWithExternals)p).getExternals().saveExternals(out);
			}
		}
		out.close();
	}

	public void trackLocation(LocationData ld) {
		if(isRunning()) {
			track.add(new TrackPoint(ld));
		}
	}
	
	public boolean hasExternals() {
		return externalCount!=0;
	}
	
	public void writeTo(Writer os) throws IOException {

		XmlSerializer xmlSerializer = Xml.newSerializer();
		xmlSerializer.setOutput(os);

		try {

			// xml header
			xmlSerializer.startDocument("utf-8", true);

			// root survey element
			xmlSerializer.startTag(null, "survey");

			// start and end time
			xmlSerializer.attribute(null, "start", Utils.formatISOTime(new Date(startTime)));
			xmlSerializer.attribute(null, "end", Utils.formatISOTime(new Date(endTime)));

			for(Marker p: markers) {
				p.writeXML(xmlSerializer);
			}

			writeTrack(xmlSerializer);

			xmlSerializer.endTag(null, "survey");
			xmlSerializer.endDocument();
			xmlSerializer.flush();
		}
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		}

	}

	private void writeTrack(XmlSerializer xmlSerializer) throws IOException{
		if(!track.isEmpty()) {
			xmlSerializer.startTag("gpx", "gpx");

			xmlSerializer.attribute("gpx", "version", "1.1");
			xmlSerializer.attribute("gpx", "creator", "OpenSurveyor");
			xmlSerializer.attribute("gpx", "creator-version", "1.0b");

			xmlSerializer.startTag("gpx", "trk");
			xmlSerializer.startTag("gpx", "trkseg");

			for(TrackPoint tp : track) {
				LocationData p = tp.getLocation();
				Date time = tp.getTime();

				xmlSerializer.startTag("gpx", "trkpt");

				xmlSerializer.attribute("gpx", "lat", String.format(Locale.US, "%.6f", p.lat));
				xmlSerializer.attribute("gpx", "lon", String.format(Locale.US, "%.6f", p.lon));

				xmlSerializer.startTag("gpx", "time");
				xmlSerializer.text(Utils.formatISOTime(time));
				xmlSerializer.endTag("gpx", "time");

				if(p.hasHeading()) {
					xmlSerializer.startTag("gpx", "magvar");
					xmlSerializer.text(String.format(Locale.US, "%.2f", p.heading));
					xmlSerializer.endTag("gpx", "magvar");
				}

				if(p.hasAltitude()) {
					xmlSerializer.startTag("gpx", "ele");
					xmlSerializer.text(String.format(Locale.US, "%.2f", p.alt));
					xmlSerializer.endTag("gpx", "ele");
				}

				xmlSerializer.endTag("gpx", "trkpt");
			}

			xmlSerializer.endTag("gpx", "trkseg");
			xmlSerializer.endTag("gpx", "trk");

			xmlSerializer.endTag("gpx", "gpx");
		}
	}

	public Iterable<Marker> getMarkers() {
		return markers;
	}
	
	public int markerCount() {
		return markers.size();
	}
	
	public Marker getMarker(int index) {
		return markers.get(index);
	}
	/**
	 * 
	 * @throws IOException when marker contains externals and there was an error cleaning up
	 */
	public Marker deleteMarker(int index) throws IOException {
		Marker m = markers.remove(index);
		if(m instanceof MarkerWithExternals) {
			externalCount--;
			((MarkerWithExternals)m).deleteExternals();
		}
		return m;
	}
	public void deleteMarker(Marker m) {
		markers.remove(m);
	}

}
