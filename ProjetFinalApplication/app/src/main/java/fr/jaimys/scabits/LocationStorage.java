package fr.jaimys.scabits;

import android.location.LocationManager;

/**
 * Class that contains the locationManager instance with the mainActivity instance where the locationManager is located.
 * This class allow us to have access to the locationManager instance in other classes so that we can remove the listener of location
 * in another class.
 */
public class LocationStorage {
    /**
     * The LocationManager coming from MainActivity.
     */
    private LocationManager locationManager = null;
    /**
     * The MainActivity where the locationManager instance is located.
     */
    private MainActivity mainActivity = null;

    /**
     * Empty constructor.
     */
    public LocationStorage() {}

    /**
     * Return the locationManager.
     * @return the locationManager
     */
    public LocationManager getLocationManager() {
        return locationManager;
    }

    /**
     * Change the value of locationManager with the new value passed as parameter.
     * @param locationManager the new value of locationManager.
     */
    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    /**
     * Return the mainActivity.
     * @return the mainActivity.
     */
    public MainActivity getMainActivity() {
        return mainActivity;
    }

    /**
     * Change the value of mainActivity with the new value passed as parameter.
     * @param mainActivity the new value of mainActivity.
     */
    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
}
