package fr.jaimys.scabits;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class ActivityCheck implements Serializable {

    //_________________________________________fields_______________________________________________
    private long time;
    private Location location;
    private HashMap<String, SensorData> sensorsInformations;
    private String realActivity;
    private String expectedActivity;

    //_________________________________________constructors_________________________________________
    public ActivityCheck() {
        this(0, null, null,null,null);
    }

    public ActivityCheck(long time, Location location, HashMap<String, SensorData>  sensorsInformations,
                         String realActivity, String expectedActivity) {
        this.time = time;
        this.location = location;
        this.sensorsInformations = sensorsInformations;
        this.realActivity = realActivity;
        this.expectedActivity = expectedActivity;
    }

    //_________________________________________methods______________________________________________
    public long getTime() {
        return time;
    }
    public void setTime(long time) {
        this.time = time;
    }

    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        this.location = location;
    }

    public HashMap<String, SensorData>  getSensorsInformations() {
        return sensorsInformations;
    }
    public void setSensorsInformations(HashMap<String, SensorData>  sensorsInformations) {
        this.sensorsInformations = sensorsInformations;
    }

    public String getRealActivity() {
        return realActivity;
    }
    public void setRealActivity(String realActivity) {
        this.realActivity = realActivity;
    }

    public String getExpectedActivity() {
        return expectedActivity;
    }
    public void setExpectedActivity(String expectedActivity) {
        this.expectedActivity = expectedActivity;
    }
}
