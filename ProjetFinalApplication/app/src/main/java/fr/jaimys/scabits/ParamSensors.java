package fr.jaimys.scabits;

public class ParamSensors {

    //_________________________________________fields_______________________________________________
    private float light;
    private float proximity;
    private float accelX;
    private float accelY;
    private double latitude;
    private double longitude;

    //_________________________________________constructors_________________________________________
    public ParamSensors() {
        this(0,0,0,0,0,0);
    }

    public ParamSensors(float light, float proximity, float accelX, float accelY,
                        double latitude, double longitude) {
        this.light = light;
        this.proximity = proximity;
        this.accelX = accelX;
        this.accelY = accelY;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    //_________________________________________methods______________________________________________
    public float getLight() {
        return light;
    }
    public void setLight(float light) {
        this.light = light;
    }

    public float getProximity() {
        return proximity;
    }
    public void setProximity(float proximity) {
        this.proximity = proximity;
    }

    public float getAccelX() {
        return accelX;
    }
    public void setAccelX(float accelX) {
        this.accelX = accelX;
    }

    public float getAccelY() {
        return accelY;
    }
    public void setAccelY(float accelY) {
        this.accelY = accelY;
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
