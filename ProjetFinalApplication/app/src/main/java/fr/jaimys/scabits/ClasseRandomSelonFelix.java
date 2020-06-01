package fr.jaimys.scabits;

import android.location.LocationManager;

public class ClasseRandomSelonFelix {
    private LocationManager locationManager = null;
    private MainActivity mainActivity = null;

    public ClasseRandomSelonFelix() {}

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
