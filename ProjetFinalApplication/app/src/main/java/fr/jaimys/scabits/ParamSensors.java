package fr.jaimys.scabits;

public class ParamSensors {

    //_________________________________________fields_______________________________________________
    private float light;
    private float proximity;
    private float accelX;
    private float accelY;
    private float accelZ;
    private float magnetoX;
    private float magnetoY;
    private float magnetoZ;
    private float gyroX;
    private float gyroY;
    private float gyroZ;

    //_________________________________________constructor_________________________________________
    public ParamSensors() {
        this.light = 0;
        this.proximity = 0;
        this.accelX = 0;
        this.accelY = 0;
        this.accelZ = 0;
        this.magnetoX = 0;
        this.magnetoY = 0;
        this.magnetoZ = 0;
        this.gyroX = 0;
        this.gyroY = 0;
        this.gyroZ = 0;
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

    public float getAccelZ() {
        return accelZ;
    }
    public void setAccelZ(float accelZ) {
        this.accelZ = accelZ;
    }

    public float getMagnetoX() {
        return magnetoX;
    }
    public void setMagnetoX(float magnetoX) {
        this.magnetoX = magnetoX;
    }

    public float getMagnetoY() {
        return magnetoY;
    }
    public void setMagnetoY(float magnetoY) {
        this.magnetoY = magnetoY;
    }

    public float getMagnetoZ() {
        return magnetoZ;
    }
    public void setMagnetoZ(float magnetoZ) {
        this.magnetoZ = magnetoZ;
    }

    public float getGyroX() {
        return gyroX;
    }
    public void setGyroX(float gyroX) {
        this.gyroX = gyroX;
    }

    public float getGyroY() {
        return gyroY;
    }
    public void setGyroY(float gyroY) {
        this.gyroY = gyroY;
    }

    public float getGyroZ() {
        return gyroZ;
    }
    public void setGyroZ(float gyroZ) {
        this.gyroZ = gyroZ;
    }
}
