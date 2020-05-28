package fr.jaimys.scabits;

import java.util.List;

public class ActivityDone {

    //_________________________________________fields_______________________________________________
    private String activity;
    private int occurence;


    //_________________________________________constructors_________________________________________
    public ActivityDone() {
        this("None", 0);
    }

    public ActivityDone(String activity, int occurence) {
        this.activity = activity;
        this.occurence = occurence;
    }


    //_________________________________________methods______________________________________________
    public String getActivity() {
        return activity;
    }
    public void setActivity(String activity) {
        this.activity = activity;
    }

    public int getOccurence() {
        return occurence;
    }
    public void setOccurence(int occurence) {
        this.occurence = occurence;
    }
}
