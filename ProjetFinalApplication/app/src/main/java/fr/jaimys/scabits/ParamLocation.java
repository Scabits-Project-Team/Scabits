package fr.jaimys.scabits;

public class ParamLocation {
    private double latitude;
    private double longitude;

    public ParamLocation(){
        this.latitude = 0;
        this.longitude = 0;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
