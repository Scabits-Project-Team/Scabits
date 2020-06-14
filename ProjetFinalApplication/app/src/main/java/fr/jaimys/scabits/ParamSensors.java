package fr.jaimys.scabits;

/**
 * Class that contains the sensor values of smartphones.
 */
public class ParamSensors {

    //_________________________________________fields_______________________________________________
    /**
     * The value returned by the light sensor.
     */
    private float light;
    /**
     * The value returned by the proximity sensor.
     */
    private float proximity;
    /**
     * The X value returned by the accelerometer sensor.
     */
    private float accelX;
    /**
     * The Y value returned by the accelerometer sensor.
     */
    private float accelY;
    /**
     * The Z value returned by the accelerometer sensor.
     */
    private float accelZ;
    /**
     * The X value returned by the magnetometer sensor.
     */
    private float magnetoX;
    /**
     * The Y value returned by the magnetometer sensor.
     */
    private float magnetoY;
    /**
     * The Z value returned by the magnetometer sensor.
     */
    private float magnetoZ;
    /**
     * The X value returned by the gyrometer sensor.
     */
    private float gyroX;
    /**
     * The Y value returned by the gyrometer sensor.
     */
    private float gyroY;
    /**
     * The Z value returned by the gyrometer sensor.
     */
    private float gyroZ;

    //_________________________________________constructor_________________________________________

    /**
     * Instanciate a ParamSensors object with all the fields equal to zero.
     */
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

    /**
     * Return the light value.
     * @return the light value.
     */
    public float getLight() {
        return light;
    }

    /**
     * Change the value of light with the new value passed as parameter.
     * @param light the new value of light.
     */
    public void setLight(float light) {
        this.light = light;
    }

    /**
     * Return the proximity value.
     * @return the proximity value.
     */
    public float getProximity() {
        return proximity;
    }
    /**
     * Change the value of proximity with the new value passed as parameter.
     * @param proximity the new value of proximity.
     */
    public void setProximity(float proximity) {
        this.proximity = proximity;
    }

    /**
     * Return the accelX value.
     * @return the accelX value.
     */
    public float getAccelX() {
        return accelX;
    }
    /**
     * Change the value of accelX with the new value passed as parameter.
     * @param accelX the new value of accelX.
     */
    public void setAccelX(float accelX) {
        this.accelX = accelX;
    }

    /**
     * Return the accelY value.
     * @return the accelY value.
     */
    public float getAccelY() {
        return accelY;
    }
    /**
     * Change the value of accelY with the new value passed as parameter.
     * @param accelY the new value of accelY.
     */
    public void setAccelY(float accelY) {
        this.accelY = accelY;
    }

    /**
     * Return the accelZ value.
     * @return the accelZ value.
     */
    public float getAccelZ() {
        return accelZ;
    }
    /**
     * Change the value of accelZ with the new value passed as parameter.
     * @param accelZ the new value of accelZ.
     */
    public void setAccelZ(float accelZ) {
        this.accelZ = accelZ;
    }

    /**
     * Return the magnetoX value.
     * @return the magnetoX value.
     */
    public float getMagnetoX() {
        return magnetoX;
    }
    /**
     * Change the value of magnetoX with the new value passed as parameter.
     * @param magnetoX the new value of magnetoX.
     */
    public void setMagnetoX(float magnetoX) {
        this.magnetoX = magnetoX;
    }

    /**
     * Return the magnetoY value.
     * @return the magnetoY value.
     */
    public float getMagnetoY() {
        return magnetoY;
    }
    /**
     * Change the value of magnetoY with the new value passed as parameter.
     * @param magnetoY the new value of magnetoY.
     */
    public void setMagnetoY(float magnetoY) {
        this.magnetoY = magnetoY;
    }

    /**
     * Return the magnetoZ value.
     * @return the magnetoZ value.
     */
    public float getMagnetoZ() {
        return magnetoZ;
    }
    /**
     * Change the value of magnetoZ with the new value passed as parameter.
     * @param magnetoZ the new value of magnetoZ.
     */
    public void setMagnetoZ(float magnetoZ) {
        this.magnetoZ = magnetoZ;
    }

    /**
     * Return the gyroX value.
     * @return the gyroX value.
     */
    public float getGyroX() {
        return gyroX;
    }
    /**
     * Change the value of gyroX with the new value passed as parameter.
     * @param gyroX the new value of gyroX.
     */
    public void setGyroX(float gyroX) {
        this.gyroX = gyroX;
    }

    /**
     * Return the gyroY value.
     * @return the gyroY value.
     */
    public float getGyroY() {
        return gyroY;
    }
    /**
     * Change the value of gyroY with the new value passed as parameter.
     * @param gyroY the new value of gyroY.
     */
    public void setGyroY(float gyroY) {
        this.gyroY = gyroY;
    }

    /**
     * Return the gyroZ value.
     * @return the gyroZ value.
     */
    public float getGyroZ() {
        return gyroZ;
    }
    /**
     * Change the value of gyroZ with the new value passed as parameter.
     * @param gyroZ the new value of gyroZ.
     */
    public void setGyroZ(float gyroZ) {
        this.gyroZ = gyroZ;
    }
}
