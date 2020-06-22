package fr.jaimys.scabits;

import java.util.HashMap;
import java.util.List;

/**
 * Class that contains the user informations, stored in the database.
 */
public class User {

    //_________________________________________fields_______________________________________________
    /**
     * The password choosed by the user.
     */
    private String password;
    /**
     * The phone' model of the user.
     */
    private String model;
    /**
     * The list of each data collection done.
     */
    private HashMap<String, ActivityCheck> activityChecks;
    /**
     * The home location.
     */
    private Location home;
    /**
     * The work location.
     */
    private Location work;
    /**
     * The shop locations where the user has been during a collect.
     */
    private List<Location> locationsSport;
    /**
     * The sport locations where the user has been during a collect.
     */
    private List<Location> locationsShop;
    /**
     * The user' agenda : Activity done each day, each hour.
     */
    private HashMap<String, HashMap<String, DailyActivities>> basicWeek;

    //_________________________________________constructors_________________________________________
    /**
     * Instanciate a User object with all the fields equal to null.
     */
    public User() {
        this("null", "null", null, null, null,
                null, null, null);
    }

    /**
     * Instanciate a User object with all the fields equal to params.
     */
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
    /**
     * Get the password of the user.
     * @return the password.
     */
    public String getPassword() {
        return password;
    }
    /**
     * Change the password with the new value passed as parameter.
     * @param password the new password.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Get the phone'model of the user.
     * @return the phone'model.
     */
    public String getModel() {
        return model;
    }
    /**
     * Change the phone' model with the new value passed as parameter.
     * @param model the new phone' model.
     */
    public void setModel(String model) {
        this.model = model;
    }

    /**
     * Get the list of each data collection done.
     * @return the list of each data collection done.
     */
    public HashMap<String, ActivityCheck> getActivityChecks() {
        return activityChecks;
    }
    /**
     * Change the list of each data collection done with the new value passed as parameter.
     * @param activityChecks the new list of each data collection done.
     */
    public void setActivityChecks(HashMap<String, ActivityCheck> activityChecks) {
        this.activityChecks = activityChecks;
    }

    /**
     * Get the current location where the user live.
     * @return the home location.
     */
    public Location  getHome() {
        return home;
    }
    /**
     * Change the location where the user live with the new one passed as parameter.
     * @param home the new home location.
     */
    public void setHome(Location  home) {
        this.home = home;
    }

    /**
     * Get the current location where the user work.
     * @return the sport locations.
     */
    public Location  getWork() {
        return work;
    }
    /**
     * Change the location where the user work with the new one passed as parameter.
     * @param work the new work location.
     */
    public void setWork(Location work) {
        this.work = work;
    }

    /**
     * Get the current list locations for sport place.
     * @return the sport locations.
     */
    public List<Location> getLocationsSport() {
        return locationsSport;
    }
    /**
     * Change the list locations for sport place (like Gym) with the new one passed as parameter.
     * @param locationsSport the new sport locations.
     */
    public void setLocationsSport(List<Location> locationsSport) {
        this.locationsSport = locationsSport;
    }

    /**
     * Get the current list locations for shopping place.
     * @return the shop locations.
     */
    public List<Location> getLocationsShop() {
        return locationsShop;
    }
    /**
     * Change the list locations for shopping place with the new one passed as parameter.
     * @param locationsShop the new shop locations.
     */
    public void setLocationsShop(List<Location> locationsShop) {
        this.locationsShop = locationsShop;
    }

    /**
     * Get the current user' agenda.
     * @return the user' agenda.
     */
    public HashMap<String, HashMap<String, DailyActivities>> getBasicWeek() {
        return basicWeek;
    }
    /**
     * Change the user' agenda with the new object passed as parameter.
     * @param basicWeek the new user' agenda.
     */
    public void setBasicWeek(HashMap<String, HashMap<String, DailyActivities>> basicWeek) {
        this.basicWeek = basicWeek;
    }
}
