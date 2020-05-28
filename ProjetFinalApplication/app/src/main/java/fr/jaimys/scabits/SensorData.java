package fr.jaimys.scabits;

import java.util.HashMap;

public class SensorData {

    //_________________________________________fields_______________________________________________
    private int sensor;
    private boolean used;
    private HashMap<String, Float> value1;
    private HashMap<String, Float> value2;
    private HashMap<String, Float> value3;

    //_________________________________________constructors_________________________________________
    public SensorData() {
        this(0, false, null,null,null);
    }

    public SensorData(int sensor, boolean used, HashMap<String, Float> value1,
                      HashMap<String, Float> value2, HashMap<String, Float> value3) {
        this.sensor = sensor;
        this.used = used;
        this.value1 = value1;
        this.value2 = value2;
        this.value3 = value3;
    }

    //_________________________________________methods______________________________________________
    public int getSensor() {
        return sensor;
    }
    public void setSensor(int sensor) {
        this.sensor = sensor;
    }

    public boolean isUsed() {
        return used;
    }
    public void setUsed(boolean used) {
        this.used = used;
    }

    public HashMap<String, Float> getValue1() {
        return value1;
    }
    public void setValue1(HashMap<String, Float> value1) {
        this.value1 = value1;
    }

    public HashMap<String, Float> getValue2() {
        return value2;
    }
    public void setValue2(HashMap<String, Float> value2) {
        this.value2 = value2;
    }

    public HashMap<String, Float> getValue3() {
        return value3;
    }
    public void setValue3(HashMap<String, Float> value3) {
        this.value3 = value3;
    }
}
