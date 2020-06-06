package fr.jaimys.scabits;

import java.util.HashMap;
import java.util.List;

public class User {

    //_________________________________________fields_______________________________________________
    private String password;
    private String model;

    //List of each data collection done
    private HashMap<String, ActivityCheck> activityChecks;
    private Location home; //Home location
    private Location work; //Work location

    //Location of shopping already registered by the apps
    private List<Location> locationsSport;

    //Location of sport already registered by the apps
    private List<Location> locationsShop;

    //Agenda of the user : Activity each day, each hour
    private HashMap<String, HashMap<String, DailyActivities>> basicWeek;

    //_________________________________________constructors_________________________________________
    public User() {
        this("null", "null", null, null, null,
                null, null, null);
    }

    public User(String password, String model, HashMap<String, ActivityCheck> activityChecks,
                Location home, Location work, List<Location> locationsSport,  List<Location>
                locationsShop, HashMap<String, HashMap<String, DailyActivities>> basicWeek) {
        this.password = password;
        this.model = model;
        this.activityChecks = activityChecks;
        this.home = home;
        this.work = work;
        this.locationsSport = locationsSport;
        this.locationsShop = locationsShop;
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

    public Location  getHome() {
        return home;
    }
    public void setHome(Location  home) {
        this.home = home;
    }

    public Location  getWork() {
        return work;
    }
    public void setWork(Location work) {
        this.work = work;
    }

    public List<Location> getLocationsSport() {
        return locationsSport;
    }
    public void setLocationsSport(List<Location> locationsSport) {
        this.locationsSport = locationsSport;
    }

    public List<Location> getLocationsShop() {
        return locationsShop;
    }
    public void setLocationsShop(List<Location> locationsShop) {
        this.locationsShop = locationsShop;
    }

    public HashMap<String, HashMap<String, DailyActivities>> getBasicWeek() {
        return basicWeek;
    }
    public void setBasicWeek(HashMap<String, HashMap<String, DailyActivities>> basicWeek) {
        this.basicWeek = basicWeek;
    }
}
