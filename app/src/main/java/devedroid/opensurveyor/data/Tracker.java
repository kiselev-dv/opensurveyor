package devedroid.opensurveyor.data;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dkiselev on 24.06.15.
 */
public class Tracker {

    private List<LocationData> track = new ArrayList<LocationData>();

    public void addPoint(LocationData p) {
        track.add(p);
    }

}
