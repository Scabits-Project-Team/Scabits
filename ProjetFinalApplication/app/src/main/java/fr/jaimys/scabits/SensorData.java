package fr.jaimys.scabits;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;

/**
 * Class that contains the sensors' data.
 */
public class SensorData implements Serializable {

    //_________________________________________fields_______________________________________________
    /**
     * Boolean that shows if the user has been used to determine which activity.
     */
    private boolean used;
    /**
     * The sensors' data.
     */
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
    /**
     * Instanciate a SensorData object with all the fields equal to null.
     */
    public SensorData() {
        this(false, null);
    }

    /**
     * Instanciate a SensorData object with all the fields equal to params.
     */
    public SensorData(boolean used, List<HashMap<String, Float>> values) {
        this.used = used;
        this.values = values;
    }

    //_________________________________________methods______________________________________________
    /**
     * Check the state of the sensor, if it's used or not
     * @return True if the sensor has been used for this.
     */
    public boolean isUsed() {
        return used;
    }
    /**
     * Change the state with the new value passed as parameter.
     * @param used the new state of usage.
     */
    public void setUsed(boolean used) {
        this.used = used;
    }

    /**
     * Get the list of values of each sensors.
     * @return the list of values of each sensors.
     */
    public List<HashMap<String, Float>> getValues() {
        return values;
    }
    /**
     * Change sensors' data with the new values passed as parameter.
     * @param values the new values.
     */
    public void setValues(List<HashMap<String, Float>> values) {
        this.values = values;
    }
}
