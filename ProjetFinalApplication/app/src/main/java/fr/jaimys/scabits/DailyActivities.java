package fr.jaimys.scabits;

import java.util.HashMap;
import java.util.List;

public class DailyActivities {

    //_________________________________________fields_______________________________________________
    private HashMap<String, List<ActivityDone>> activitiesParHour;  //Hour - activitylist List<String>

    //_________________________________________constructors_________________________________________
    public DailyActivities() {
        this(null);
    }

    public DailyActivities(HashMap<String, List<ActivityDone>> activitiesParHour) {
        this.activitiesParHour = activitiesParHour;
    }

    //_________________________________________methods______________________________________________

    public HashMap<String, List<ActivityDone>> getActivitiesParHour() {
        return activitiesParHour;
    }
    public void setActivitiesParHour(HashMap<String, List<ActivityDone>> activitiesParHour) {
        this.activitiesParHour = activitiesParHour;
    }
}
