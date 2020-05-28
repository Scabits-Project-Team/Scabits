package fr.jaimys.scabits;

import java.util.HashMap;
import java.util.List;

public class User {

    //_________________________________________fields_______________________________________________
    private String password;
    private String model;

    //List of each data collection done
    private HashMap<String, ActivityCheck> activityChecks;
    private List<Double> home; //Home location
    private List<Double> work; //Work location

    //Location already registered by the apps
    private HashMap<String, List<Double>> locations;

    //Agenda of the user : Activity each day, each hour
    private HashMap<String, DailyActivities> basicWeek;

    //_________________________________________constructors_________________________________________
    public User() {
        this("null", "null", null, null, null,
                null, null);
    }

    public User(String password, String model, HashMap<String, ActivityCheck> activityChecks,
                List<Double> home, List<Double> work, HashMap<String, List<Double>> locations, HashMap<String,
                DailyActivities> basicWeek) {
        this.password = password;
        this.model = model;
        this.activityChecks = activityChecks;
        this.home = home;
        this.work = work;
        this.locations = locations;
        this.basicWeek = basicWeek;
    }

    //_________________________________________methods______________________________________________
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getModel() {
        return model;
    }
    public void setModel(String model) {
        this.model = model;
    }

    public HashMap<String, ActivityCheck> getActivityChecks() {
        return activityChecks;
    }
    public void setActivityChecks(HashMap<String, ActivityCheck> activityChecks) {
        this.activityChecks = activityChecks;
    }

    public List<Double>  getHome() {
        return home;
    }
    public void setHome(List<Double>  home) {
        this.home = home;
    }

    public List<Double>  getWork() {
        return work;
    }
    public void setWork(List<Double> work) {
        this.work = work;
    }

    public HashMap<String, List<Double> > getLocations() {
        return locations;
    }
    public void setLocations(HashMap<String, List<Double> > locations) {
        this.locations = locations;
    }

    public HashMap<String, DailyActivities> getBasicWeek() {
        return basicWeek;
    }
    public void setBasicWeek(HashMap<String, DailyActivities> basicWeek) {
        this.basicWeek = basicWeek;
    }
}
