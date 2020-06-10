package fr.jaimys.scabits;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

public class SensorData implements Serializable {

    //_________________________________________fields_______________________________________________
    private boolean used;
    private List<HashMap<String, Float>> values;
    /*
    Index - name : value
    Example 1 :
        0 - luminosity : 45
        1 - luminosity : 67
        2 - luminosity : 52

    Example 2 :
        0 - X : -5.1617
            Y :  1.7811
            Z : -0.9630
        1 - X : -0.5257
            Y :  1.1571
            Z : -1.5281
        2 - X : -2.1477
            Y :  2.0643
            Z : -6.1388
    */


    //_________________________________________constructors_________________________________________
    public SensorData() {
        this(false, null);
    }

    public SensorData(boolean used, List<HashMap<String, Float>> values) {
        this.used = used;
        this.values = values;
    }

    //_________________________________________methods______________________________________________
    public boolean isUsed() {
        return used;
    }
    public void setUsed(boolean used) {
        this.used = used;
    }

    public List<HashMap<String, Float>> getValues() {
        return values;
    }
    public void setValues(List<HashMap<String, Float>> values) {
        this.values = values;
    }
}
