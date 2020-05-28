package fr.jaimys.scabits;

public class DataHabits {

    //_________________________________________fields_______________________________________________
    private String realActivity;
    private String expectedActivity;
    private String tagIcon;
    private String sensorsUsed;
    private String time;


    //_________________________________________constructors_________________________________________
    public DataHabits() {
        this("None", "None",
                "null_icon", "None", "None");
    }

    public DataHabits(String realActivity, String expectedActivity, String tagIcon,
                      String sensorsUsed, String time) {
        this.realActivity = realActivity;
        this.expectedActivity = expectedActivity;
        this.tagIcon = tagIcon;
        this.sensorsUsed = sensorsUsed;
        this.time = time;
    }


    //_________________________________________methods______________________________________________
    public String getRealActivity() {
        return realActivity;
    }
    public void setRealActivity(String realActivity) {
        this.realActivity = realActivity;
    }

    public String getExpectedActivity() {
        return expectedActivity;
    }
    public void setExpectedActivity(String expectedActivity) {
        this.expectedActivity = expectedActivity;
    }

    public String getTagIcon() {
        return tagIcon;
    }
    public void setTagIcon(String tagIcon) {
        this.tagIcon = tagIcon;
    }

    public String getSensorsUsed() {
        return sensorsUsed;
    }
    public void setSensorsUsed(String sensorsUsed) {
        this.sensorsUsed = sensorsUsed;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return this.realActivity;
    }
}
