package fr.jaimys.scabits;

/**
 * Class that contains some of ActivityCheck Object informations but reorganized.
 */
public class DataHabits {

    //_________________________________________fields_______________________________________________
    /**
     * The activity that the user chose with the survey.
     */
    private String realActivity;
    /**
     * The activity we expected it the real one.
     */
    private String expectedActivity;
    /**
     * The tag to restore the image icon of the real activity.
     */
    private String tagIcon;
    /**
     * The infos on with sensors have been used.
     */
    private String sensorsUsed;
    /**
     * The time of the collect.
     */
    private String time;


    //_________________________________________constructors_________________________________________
    /**
     * Instanciate a DataHabits object with all the fields equal to null.
     */
    public DataHabits() {
        this("None", "None",
                "null_icon", "None", "None");
    }

    /**
     * Instanciate a DataHabits object with all the fields equal to params.
     */
    public DataHabits(String realActivity, String expectedActivity, String tagIcon,
                      String sensorsUsed, String time) {
        this.realActivity = realActivity;
        this.expectedActivity = expectedActivity;
        this.tagIcon = tagIcon;
        this.sensorsUsed = sensorsUsed;
        this.time = time;
    }


    //_________________________________________methods______________________________________________
    /**
     * Get the real activity chose by the user in the survey.
     * @return the real activity.
     */
    public String getRealActivity() {
        return realActivity;
    }
    /**
     * Change the real activity chose by the user with the new value passed as parameter.
     * @param realActivity the new real activity.
     */
    public void setRealActivity(String realActivity) {
        this.realActivity = realActivity;
    }

    /**
     * Get the activity that we expect to be
     * @return the activity.
     */
    public String getExpectedActivity() {
        return expectedActivity;
    }
    /**
     * Change the expected activity with the new value passed as parameter.
     * @param expectedActivity the new activity that we expect to be.
     */
    public void setExpectedActivity(String expectedActivity) {
        this.expectedActivity = expectedActivity;
    }

    /**
     * Get the tag that correspond to the icon image of the real activity.
     * @return the tag.
     */
    public String getTagIcon() {
        return tagIcon;
    }
    /**
     * Change the tag with the new value passed as parameter to correspond to the real activity.
     * @param tagIcon the new tag.
     */
    public void setTagIcon(String tagIcon) {
        this.tagIcon = tagIcon;
    }

    /**
     * Get the informations on which sensors are used.
     * @return the informations on sensors.
     */
    public String getSensorsUsed() {
        return sensorsUsed;
    }
    /**
     * Change the informations on sensors used with the new value passed as parameter.
     * @param sensorsUsed the new informations on sensors used.
     */
    public void setSensorsUsed(String sensorsUsed) {
        this.sensorsUsed = sensorsUsed;
    }

    /**
     * Get the time of the collect.
     * @return the time.
     */
    public String getTime() {
        return time;
    }
    /**
     * Change the time with the new value passed as parameter.
     * @param time the new time.
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Write the real activity for making display more clear if we call 'System.out.print'.
     * @return the real activity.
     */
    @Override
    public String toString() {
        return this.realActivity;
    }
}
