package fr.jaimys.scabits;

import java.util.HashMap;
import java.util.List;

public class ActivityCheck {

    //_________________________________________fields_______________________________________________
    private long time;
    private List<SensorData> sensorsInformations;
    private String realActivity;
    private String expectedActivity;

    //_________________________________________constructors_________________________________________
    public ActivityCheck() {
        this(0,null,null,null);
    }

    public ActivityCheck(long time, List<SensorData> sensorsInformations,
                         String realActivity, String expectedActivity) {
        this.time = time;
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

    public List<SensorData> getSensorsInformations() {
        return sensorsInformations;
    }
    public void setSensorsInformations(List<SensorData> sensorsInformations) {
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
