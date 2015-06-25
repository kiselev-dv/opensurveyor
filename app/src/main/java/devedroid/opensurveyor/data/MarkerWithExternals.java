package devedroid.opensurveyor.data;

import java.io.File;
import java.io.IOException;
import java.io.Writer;

import devedroid.opensurveyor.presets.AudioRecordPreset;
import devedroid.opensurveyor.presets.BasePreset;

import android.content.res.Resources;

import org.xmlpull.v1.XmlSerializer;

public abstract class MarkerWithExternals extends Marker {
	protected String fileName;

	public abstract String getExternalType();

	public MarkerWithExternals(BasePreset t) {
		super(t);
	}

	
	public void setFileName(String f) {
		fileName = f;
	}

	public void deleteExternals() throws IOException { 
		(new File(fileName)).delete();
	}
	
	public abstract ExternalPackage getExternals() ;

	@Override
	protected void writeDataPart(XmlSerializer xmlSerializer) throws IOException {
		xmlSerializer.startTag("", "attachment");

		xmlSerializer.attribute("", "type", getExternalType());
		xmlSerializer.attribute("", "src", new File(fileName).getName());

		xmlSerializer.endTag("", "attachment");
	}

}
