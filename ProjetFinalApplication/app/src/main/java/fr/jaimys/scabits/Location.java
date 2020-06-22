package fr.jaimys.scabits;

import java.io.Serializable;

/**
 * Class that contains the location informations. Used to be stored easier in the database.
 */
public class Location implements Serializable {

    //_________________________________________fields_______________________________________________
    /**
     * The longitude of the location.
     */
    private Double longitude;
    /**
     * The latitude of the location.
     */
    private Double latitude;

    //_________________________________________constructors_________________________________________
    /**
     * Instanciate a Location object with all the fields equal to 0.
     */
    public Location() {
        this(0d,0d);
    }

    /**
     * Instanciate a Location object with all the fields equal to params.
     */
    public Location(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    //_________________________________________methods______________________________________________
    /**
     * Get the longitude of the location.
     * @return the longitude.
     */
    public Double getLongitude() {
        return longitude;
    }
    /**
     * Change the longitude with the new value passed as parameter.
     * @param longitude the new longitude.
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Get the latitude of the location.
     * @return the latitude.
     */
    public Double getLatitude() {
        return latitude;
    }
    /**
     * Change the latitude with the new value passed as parameter.
     * @param latitude the new latitude.
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * Compute the distance between two location and return the meters that separate locations.
     * @param location the location that we have to determine the distance with.
     * @return the distance between locations.
     */
    public double distBetween(Location location) {
        double earthRadius = 6371000; //meters
        double distLatitude = Math.toRadians(latitude-location.getLatitude());
        double distLongitude = Math.toRadians(longitude-location.getLongitude());
        double a = Math.sin(distLatitude/2) * Math.sin(distLatitude/2) +
                Math.cos(Math.toRadians(distLatitude)) * Math.cos(Math.toRadians(distLatitude)) *
                        Math.sin(distLongitude/2) * Math.sin(distLongitude/2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        return earthRadius * c;
    }

    /**
     * Compare two location and check if the distance between thoses is bigger than 100 meters.
     * @param obj the location compared.
     * @return True if there are closer than 100 meters, otherwise false.
     */
    @Override
    public boolean equals(Object obj) {
        boolean isEqual = false;
        Location location = (Location)obj;
        double dist = distBetween(location);
        if (-100 < dist && dist < 100) {
            isEqual = true;
        }
        return isEqual;
    }
}
