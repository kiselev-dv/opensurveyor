package devedroid.opensurveyor.data;

import java.util.Date;

/**
 * Created by dkiselev on 25.06.15.
 */
public class TrackPoint {

    private final LocationData location;
    private final Date time;

    public TrackPoint (LocationData loc) {
        this.location = loc;
        this.time = new Date();
    }

    public Date getTime() {
        return time;
    }

    public LocationData getLocation() {
        return location;
    }
}
