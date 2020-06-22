package fr.jaimys.scabits;

import java.util.HashMap;

/**
 * Class that contains number of occurrence for each activity stored.
 */
public class DailyActivities {

    //_________________________________________fields_______________________________________________
    /**
     * The list of activites with the occurence for each activity.
     */
    private HashMap<String, Integer> activities; //activity - nombre d'occurences

    //_________________________________________constructors_________________________________________
    /**
     * Instanciate a DailyActivities object with the only field equal to null.
     */
    public DailyActivities() {
        this(null);
    }

    /**
     * Instanciate a DailyActivities object with the only field equal to the param.
     */
    public DailyActivities(HashMap<String, Integer> activities) {
        this.activities = activities;
    }

    //_________________________________________methods______________________________________________
    /**
     * Get the list of activites with the occurence for each activity.
     * @return the list of activites.
     */
    public HashMap<String, Integer>getActivities() {
        return activities;
    }
    /**
     * Change activities with the new values passed as parameter.
     * @param activities the new activities.
     */
    public void setActivities(HashMap<String, Integer> activities) {
        this.activities = activities;
    }
}
