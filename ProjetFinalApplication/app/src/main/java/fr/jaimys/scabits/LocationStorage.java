package fr.jaimys.scabits;

import android.location.LocationManager;

public class LocationStorage {
    private LocationManager locationManager = null;
    private MainActivity mainActivity = null;

    public LocationStorage() {}

    public LocationManager getLocationManager() {
        return locationManager;
    }

    public void setLocationManager(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    public MainActivity getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(MainActivity mainActivity) {
        this.mainActivity = mainActivity;
    }
}
