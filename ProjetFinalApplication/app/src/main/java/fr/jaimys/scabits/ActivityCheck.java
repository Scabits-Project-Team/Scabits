package fr.jaimys.scabits;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Class that contains the data collection, time and informations on activities.
 */
public class ActivityCheck implements Serializable {

    //_________________________________________fields_______________________________________________
    /**
     * The time of the collect.
     */
    private long time;
    /**
     * The location where the user was during the collect.
     */
    private Location location;
    /**
     * The sensors informations.
     */
    private HashMap<String, SensorData> sensorsInformations;
    /**
     * The activity that the user chose with the survey.
     */
    private String realActivity;
    /**
     * The activity we expected it the real one.
     */
    private String expectedActivity;

    //_________________________________________constructors_________________________________________
    /**
     * Instanciate a ActivityCheck object with all the fields equal to null.
     */
    public ActivityCheck() {
        this(0, null, null,null,
                null);
    }

    /**
     * Instanciate a ActivityCheck object with all the fields equal to params.
     */
    public ActivityCheck(long time, Location location, HashMap<String, SensorData>
            sensorsInformations, String realActivity, String expectedActivity) {
        this.time = time;
        this.location = location;
        this.sensorsInformations = sensorsInformations;
        this.realActivity = realActivity;
        this.expectedActivity = expectedActivity;
    }

    //_________________________________________methods______________________________________________
    /**
     * Get the time of the collect.
     * @return the time.
     */
    public long getTime() {
        return time;
    }
    /**
     * Change the time with the new value passed as parameter.
     * @param time the new time.
     */
    public void setTime(long time) {
        this.time = time;
    }

    /**
     * Get the location when the user where during the collect.
     * @return the location.
     */
    public Location getLocation() {
        return location;
    }
    /**
     * Change the location with the new value passed as parameter.
     * @param location the new location.
     */
    public void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Get the informations for sensors.
     * @return the informations.
     */
    public HashMap<String, SensorData>  getSensorsInformations() {
        return sensorsInformations;
    }
    /**
     * Change the informations for sensors with the new value passed as parameter.
     * @param sensorsInformations the new informations for sensors.
     */
    public void setSensorsInformations(HashMap<String, SensorData>  sensorsInformations) {
        this.sensorsInformations = sensorsInformations;
    }

    /**
     * Get the real activity chose by the user in the survey.
     * @return the real activity.
     */
    public String getRealActivity() {
        return realActivity;
    }
    /**
     * Change the real activity chose by the user with the new value passed as parameter.
     * @param realActivity the new real activity.
     */
    public void setRealActivity(String realActivity) {
        this.realActivity = realActivity;
    }

    /**
     * Get the activity that we expect to be
     * @return the activity.
     */
    public String getExpectedActivity() {
        return expectedActivity;
    }
    /**
     * Change the expected activity with the new value passed as parameter.
     * @param expectedActivity the new activity that we expect to be.
     */
    public void setExpectedActivity(String expectedActivity) {
        this.expectedActivity = expectedActivity;
    }
}
