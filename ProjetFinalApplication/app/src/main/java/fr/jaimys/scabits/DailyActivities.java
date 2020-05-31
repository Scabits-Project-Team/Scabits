package fr.jaimys.scabits;

import java.util.HashMap;
public class DailyActivities {

    //_________________________________________fields_______________________________________________
    private HashMap<String, Integer> activities; //activity - nombre d'occurences

    //_________________________________________constructors_________________________________________
    public DailyActivities() {
        this(null);
    }

    public DailyActivities(HashMap<String, Integer> activities) {
        this.activities = activities;
    }

    //_________________________________________methods______________________________________________
    public HashMap<String, Integer>getActivities() {
        return activities;
    }
    public void setActivities(HashMap<String, Integer> activities) {
        this.activities = activities;
    }
}
