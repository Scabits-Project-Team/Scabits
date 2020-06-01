package fr.jaimys.scabits;

public class ParamSensors {

    //_________________________________________fields_______________________________________________
    private float light;
    private float proximity;
    private float accelX;
    private float accelY;

    //_________________________________________constructor_________________________________________
    public ParamSensors() {
        this.light = 0;
        this.proximity = 0;
        this.accelX = 0;
        this.accelY = 0;
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
}
