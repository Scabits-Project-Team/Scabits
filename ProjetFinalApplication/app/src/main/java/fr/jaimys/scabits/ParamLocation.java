package fr.jaimys.scabits;

import android.util.Log;

/**
 * Class that contains the location values of the GPS, like the latitude and the longitude.
 */
public class ParamLocation {
    /**
     * Latitude of the GPS location.
     */
    private double latitude;
    /**
     * Longitude of the GPS location.
     */
    private double longitude;

    /**
     * Instanciate a ParamLocation object with a latitude and a longitude equal to zero.
     */
    public ParamLocation(){
        this.latitude = 0;
        this.longitude = 0;
    }

    /**
     * Return the latitude.
     * @return the latitude.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Change the value of the latitude with the new value passed as parameter.
     * @param latitude the new value of latitude.
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Return the longitude.
     * @return the longitude.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Change the value of the longitude with the new value passed as parameter.
     * @param longitude the new value of longitude.
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
