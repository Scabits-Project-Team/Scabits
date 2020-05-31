package fr.jaimys.scabits;

import java.io.Serializable;

public class Location implements Serializable {

    //_________________________________________fields_______________________________________________
    private Double longitude;
    private Double latitude;

    //_________________________________________constructors_________________________________________
    public Location() {
        this(0d,0d);
    }

    public Location(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    //_________________________________________methods______________________________________________
    public Double getLongitude() {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getLatitude() {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

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
