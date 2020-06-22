package fr.jaimys.scabits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Activity that allows user to choose which activity he is doing.
 */
public class NotificationActivity extends AppCompatActivity {

    //_________________________________________fields_______________________________________________
    /**
     * The instance of the database (Firebase).
     */
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    /**
     * The reference of the root in the database.
     */
    private DatabaseReference referenceData = database.getReference();
    /**
     * The reference of the user in the database.
     */
    private DatabaseReference referenceUser;
    /**
     * The login of the user.
     */
    private String pseudo;
    /**
     * The view selected.
     */
    private View currentView = null;
    /**
     * The real activity.
     */
    private String activity = "Aucune";
    /**
     * The time when we get data.
     */
    private long timestamp;
    /**
     * The current User' object.
     */
    private User user = null;
    /**
     * The data collection by sensors.
     */
    private ActivityCheck activityCheck;
    /**
     * The current time showed.
     */
    private String time;
    /**
     * Event that add the activity on the agenda stored in the database. Create the branch if
     * needed.
     */
    private ValueEventListener valueEventListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                User user = dataSnapshot.getValue(User.class);

                //Find the day number (sunday to monday)
                Date date = new Date(getTimestamp());
                @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("EEEE");
                String day = formatter.format(date);

                //Translate in base
                switch (day) {
                    case "lundi" : day = "Monday";
                        break;
                    case "mardi" : day = "Tuesday";
                        break;
                    case "mercredi" : day = "Wednesday";
                        break;
                    case "jeudi" : day = "Thursday";
                        break;
                    case "vendredi" : day = "Friday";
                        break;
                    case "samedi" : day = "Saturday";
                        break;
                    case "dimanche" : day = "Sunday";
                        break;
                    default :
                        break;
                }

                //Find the hour
                @SuppressLint("SimpleDateFormat") DateFormat formatter2 = new SimpleDateFormat("HH");
                String hour = formatter2.format(date);

                //Find the day and set the activity if needed
                assert user != null;
                HashMap<String, HashMap<String, DailyActivities>> basicWeek;
                if (user.getBasicWeek() != null) {
                    basicWeek = user.getBasicWeek();
                }
                else {
                    basicWeek = new HashMap<>();
                }

                //If this day is not aleardy initalised in the database
                if (!basicWeek.containsKey(day)) {

                    //Create a new day, hour and activity
                    HashMap<String, DailyActivities> activitiesHashMap = new HashMap<>();
                    HashMap<String, Integer> activities = new HashMap<>();
                    activities.put(getActivity(),1);
                    activitiesHashMap.put(hour, new DailyActivities(activities));
                    basicWeek.put(day, activitiesHashMap);

                    //Add the day, hour and activity
                    referenceData.child(pseudo).child("basicWeek").setValue(basicWeek);
                }
                else {
                    HashMap<String, DailyActivities> activitiesParHour = basicWeek.get(day);
                    assert activitiesParHour != null;
                    if (activitiesParHour.containsKey(hour))
                    {
                        //Check if this activity already exists
                        DailyActivities activities = activitiesParHour.get(hour);
                        assert activities != null;

                        //If the activity is not already in the list
                        if (activities.getActivities().containsKey(getActivity())) {
                            //Increase the number of time this activity has been done
                            Integer nbOccurences = Objects.requireNonNull(activities.getActivities().
                                    get(getActivity()));

                            activities.getActivities().remove(getActivity());
                            activities.getActivities().put(getActivity(), nbOccurences+1);

                        }
                        else {
                            //Add the activity at the day and hour
                            activities.getActivities().put(getActivity(),1);
                        }
                        referenceData.child(pseudo).child("basicWeek").child(day).
                                child(hour).setValue(activities);
                    }
                    else {
                        //Add the activity at the day and create a new hour
                        HashMap<String, Integer> activities = new HashMap<>();
                        activities.put(getActivity(),1);
                        activitiesParHour.put(hour, new DailyActivities(activities));
                        referenceData.child(pseudo).child("basicWeek").child(day).setValue(activitiesParHour);
                    }
                }

            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            //Never used
        }
    };


    //_________________________________________methods______________________________________________
    /**
     * Create the fields required like questions and images. Buttons are setting up and linked
     * to their function.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                           down then this Bundle contains the data it most recently supplied
     *                           in onSaveInstanceState(Bundle).
     */
    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        //Recuperation of the AcivityCheck and timestamp
        this.activityCheck = (ActivityCheck) getIntent().getSerializableExtra("activityCheck");
        assert this.activityCheck != null;
        this.timestamp = this.activityCheck.getTime();

        //Recuperation of the login
        this.pseudo = getIntent().getStringExtra("pseudo");
        this.time = getIntent().getStringExtra("time");
        assert this.pseudo != null;
        referenceUser = referenceData.child(this.pseudo).child("activityChecks");

        //Write the question with the right time
        TextView question = findViewById(R.id.question_notify);
        question.setText("Quelle activité faisiez-vous à " + time + " ?");

        //Set the user profile
        referenceData.child(this.pseudo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    setUser(dataSnapshot.getValue(User.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    /**
     * Change the User' Object with the new value passed as parameter.
     * @param value the new User' Object.
     */
    private void setUser(User value) {
        this.user = value;
    }

    /**
     * Add the activity selected in the database if one of those are selected. It also add the data
     * collection made at the timer.
     * @param view the View linked to this button.
     */
    public void addCheckActivity(View view) {

        //If an activity is selected
        if (this.currentView != null && !this.activity.equals("Aucune")) {

            //Set the real activity
            this.activityCheck.setRealActivity(getActivity());

            //Add the new checkActivity
            referenceUser.child(Objects.requireNonNull(referenceUser.push().getKey())).setValue(
                activityCheck);

            //Add in the agenda this Activity if doesnt exist yet
            addActivityInAgenda();

            //Add location if it's work or shopping
            addLocation(activityCheck.getLocation());

            //Notify the user
            Toast.makeText(getApplicationContext(), "Les données ont été ajoutées",
                    Toast.LENGTH_SHORT).show();

            //Return to the account page
            getIntent().putExtra("pseudo",  this.pseudo);
            finish();
        }
        else {
            //Notify the user to choose an activity
            Toast.makeText(getApplicationContext(), "Veuillez choisir une activité",
                    Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Add the location is the database depending on which activity the user choosed.
     * @param location the location when the check has been done.
     */
    private void addLocation(Location location) {
        switch (this.activity) {
            //Change the place where the user works
            case "Travailler":
                referenceData.child(pseudo).child("work").setValue(location);
                break;

            //Add a new shopping place if it doesnt already exist
            case "Magasiner": {
                if (this.user.getLocationsShop() != null) {
                    boolean addLoc = true;
                    for (Location loc : this.user.getLocationsShop()) {
                        if (loc.equals(location)) {
                            addLoc = false;
                            break;
                        }
                    }
                    if (addLoc) {
                        this.user.getLocationsShop().add(location);
                        referenceData.child(pseudo).child("locationsShop").setValue(this.user.getLocationsShop());
                    }
                }
                else {
                    List<Location> listLocShop = new ArrayList<>();
                    listLocShop.add(location);
                    referenceData.child(pseudo).child("locationsShop").setValue(listLocShop);
                }
                break;
            }

            //Add a new sport place if it doesnt already exist
            case "Faire du sport": {
                if (this.user.getLocationsSport() != null) {
                    boolean addLoc = true;
                    for (Location loc : this.user.getLocationsSport()) {
                        if (loc.equals(location)) {
                            addLoc = false;
                            break;
                        }
                    }
                    if (addLoc) {
                        this.user.getLocationsSport().add(location);
                        referenceData.child(pseudo).child("locationsSport").setValue(this.user.getLocationsSport());
                    }
                }
                {
                    List<Location> listLocSport = new ArrayList<>();
                    listLocSport.add(location);
                    referenceData.child(pseudo).child("locationsSport").setValue(listLocSport);
                }
                break;
            }
            default:
                break;
        }
    }

    /**
     * Select or unselect the activity and change the background of items.
     * @param view the View linked to this button.
     */
    @SuppressLint("RtlHardcoded")
    public void selectItem(View view) {
        //Select and change the item if needed
        LinearLayout linearLayout = (LinearLayout) view;
        if (view.equals(this.currentView)) {
            this.currentView = null;
            view.setBackground(null);
            this.activity = "Aucune";
        }
        else {
            if (this.currentView != null) {
                this.currentView.setBackground(null);
            }
            this.currentView = view;
            linearLayout.setBackground(getResources().getDrawable(R.drawable.button_item_select_design));
            TextView activity = (TextView) linearLayout.getChildAt(1);
            this.activity = activity.getText().toString();
        }

    }

    /**
     * Add the activity is the agenda stored in the database.
     */
    private void addActivityInAgenda() {
        referenceData.child(this.pseudo)
                .addListenerForSingleValueEvent(valueEventListener2);
    }

    /**
     * Get the activity choosed by the user.
     * @return the activity.
     */
    private String getActivity() {
        return this.activity;
    }

    /**
     * Get the timestamp when the data have been collected.
     * @return the timestamp.
     */
    private long getTimestamp() {
        return this.timestamp;
    }

    /**
     * Cancel the data collection and return to the activity that called this one.
     */
    public void cancelCheck() {
        //Notify the user
        Toast.makeText(getApplicationContext(), "Les données n'ont pas été ajoutées",
                Toast.LENGTH_SHORT).show();

        //Return to the account page
        getIntent().putExtra("pseudo",  this.pseudo);
        finish();
    }
}
