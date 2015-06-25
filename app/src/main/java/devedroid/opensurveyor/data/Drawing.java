package devedroid.opensurveyor.data;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.osmdroid.api.IGeoPoint;
import org.xmlpull.v1.XmlSerializer;

import android.content.res.Resources;
import android.location.Location;
import devedroid.opensurveyor.Utils;

public class Drawing extends Marker {

	//protected LocationData location;

	private List<List<IGeoPoint> > data = new ArrayList<List<IGeoPoint>>();

	private int color = 0xFF000000;
	private int width = 4;

	@Override
	public void writeXML(XmlSerializer xmlSerializer)  throws IOException {
		xmlSerializer.startTag("", "drawing");

		xmlSerializer.attribute("", "time", Utils.formatISOTime(new Date(getTimestamp())));
		xmlSerializer.attribute("", "color", Integer.toHexString(color));
		xmlSerializer.attribute("", "thickness", String.valueOf(width));

		for(List<IGeoPoint> segment: data) {
			xmlSerializer.startTag("", "segment");
			for( IGeoPoint pt: segment ) {
				xmlSerializer.startTag("", "pt");

				xmlSerializer.attribute("", "lat", String.format(Locale.US, "%.6f", pt.getLatitudeE6() * 1e-6d));
				xmlSerializer.attribute("", "lon", String.format(Locale.US, "%.6f", pt.getLongitudeE6() * 1e-6d));

				xmlSerializer.endTag("", "pt");
			}
			xmlSerializer.endTag("", "segment");
		}

		xmlSerializer.endTag("", "drawing");
	}

	@Override
	protected void writeDataPart(XmlSerializer xmlSerializer) throws IOException {
		//Do nothing
	}


	public void setData(List<List<IGeoPoint>> data) {
		this.data = data;
		if(!data.isEmpty() && !data.get(0).isEmpty())
			setLocation(data.get(0).get(0));
	}


	@Override
	public String getDesc(Resources res) {
		return "Drawing";
	}


	@Override
	public void addProperty(PropertyDefinition key, String value) {
	}


	@Override
	public String getProperty(PropertyDefinition name) {
		return null;
	}


	public List<List<IGeoPoint>> getData() {
		return data;
	}


	public int getColor() {
		return 0xFF000000 | color;
	}
	
	public int getWidth() {
		return width;
	}

	public void setColor(int color) {
		this.color = color;		
	}

	public void setWidth(int width2) {
		this.width = width2;
	}
}
