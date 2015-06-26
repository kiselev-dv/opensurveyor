package devedroid.opensurveyor.data;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources;
import android.location.Location;

import org.xmlpull.v1.XmlSerializer;

import devedroid.opensurveyor.R;
import devedroid.opensurveyor.presets.BasePreset;
import devedroid.opensurveyor.presets.POIPreset;

public class POI extends Marker {

	protected String type;

	protected Map<PropertyDefinition, String> props;

	public POI(POIPreset prs) {
		super(prs);
		this.type = prs.type;
		props = new HashMap<PropertyDefinition, String>();
	}

	private POI(String type) {
		this(null, System.currentTimeMillis(), type);
	}

	private POI(Location location, String type) {
		this(location, System.currentTimeMillis(), type);
	}

	private POI(Location location, long timeStamp, String type) {
		super(location, timeStamp);
		setType(type);
		props = new HashMap<PropertyDefinition, String>();
	}

	@Override
	public void addProperty(PropertyDefinition key, String value) {
		props.put(key, value);
	}

	@Override
	public String getProperty(PropertyDefinition name) {
		return props.get(name);
	}

	public boolean isPOI() {
		return type != null;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	protected void writeDataPart(XmlSerializer xmlSerializer) throws IOException {

		xmlSerializer.startTag(null, "poi");
		xmlSerializer.attribute(null, "type", getType());
		xmlSerializer.endTag(null, "poi");

		if (generatedText != null) {
			xmlSerializer.startTag(null, "text");
			xmlSerializer.attribute(null, "generated", "yes");
			xmlSerializer.text(generatedText);
			xmlSerializer.endTag(null, "text");
		}

		writeProperties(xmlSerializer);
	}

	private void writeProperties(XmlSerializer xmlSerializer) throws IOException {
		for (Map.Entry<PropertyDefinition, String> e : props.entrySet()) {
			xmlSerializer.startTag(null, "property");
			xmlSerializer.attribute(null, "k", e.getKey().key);
			xmlSerializer.attribute(null, "v", e.getValue());
			xmlSerializer.endTag(null, "property");
		}
	}

	@Override
	public String getDesc(Resources res) {
		String misc = "";// (hasDirection() ? " "+dir.dirString() : "");
		// StringBuilder misc = new StringBuilder();
		if (!props.isEmpty()) {
			misc += " (";
			for (PropertyDefinition p : props.keySet()) {
				if (p.equals(BasePreset.PROP_LINEAR)) {
					misc += res
							.getString(props.get(p).equals("start") ? R.string.linear_start
									: R.string.linear_end);
				} else
					misc += p.title.toLowerCase() + ": "
							+ p.formatValue(props.get(p), res);
				misc += "; ";
			}
			misc = misc.substring(0, misc.length() - 2) + ")";
		}
		return (generatedText == null ? type : generatedText) + misc;
	}

}
