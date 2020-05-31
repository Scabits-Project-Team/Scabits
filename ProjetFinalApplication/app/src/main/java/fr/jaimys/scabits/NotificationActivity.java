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
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class NotificationActivity extends AppCompatActivity {

    //_________________________________________fields_______________________________________________
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referenceData = database.getReference();
    private DatabaseReference referenceUser;
    private String pseudo;
    private View currentView = null;
    private String activity = "Aucune";
    private long timestamp;
    private User user = null;
    private ActivityCheck activityCheck;


    //_________________________________________methods______________________________________________
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
        assert this.pseudo != null;
        referenceUser = referenceData.child(this.pseudo).child("activityChecks");

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

    private void setUser(User value) {
        this.user = value;
    }

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
            //startActivity(getIntent());
            finish();
        }
        else {
            //Notify the user to choose an activity
            Toast.makeText(getApplicationContext(), "Veuillez choisir une activité",
                    Toast.LENGTH_SHORT).show();
        }
    }

    private void addLocation(Location location) {
        //Change the place where the user works
        if (this.activity.equals("Travailler")) {
            referenceData.child(pseudo).child("work").
                    setValue(location);
        }
        //Add a new shopping place if needed
        else if (this.activity.equals("Magasiner")) {
            boolean addLoc = true;
            if (this.user.getLocations() != null) {

                for (Location loc : this.user.getLocations().values()) {
                    if (loc.equals(location)) {
                        addLoc = false;
                        break;
                    }
                }
                if (addLoc) {
                    referenceData.child(pseudo).child("locations").child(Objects.requireNonNull(
                            referenceData.child(pseudo).child("locations").push().getKey())).
                            setValue(location);
                }
            }
            else {
                referenceData.child(pseudo).child("locations").child(Objects.requireNonNull(
                        referenceData.child(pseudo).child("locations").push().getKey())).
                        setValue(location);
            }
        }

    }



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

    private void addActivityInAgenda() {
        referenceData.child(this.pseudo)
                .addListenerForSingleValueEvent(valueEventListener2);
    }

    ValueEventListener valueEventListener2 = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                User user = dataSnapshot.getValue(User.class);

                //Find the day number (sunday to monday)
                Date date = new Date(getTimestamp());
                @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("EEEE");
                String day = formatter.format(date);

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
            else {
                //There is a serious problem is we are here..
                Log.d("ALED", "HELP");
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            //Never used
        }
    };

    private String getActivity() {
        return this.activity;
    }

    private long getTimestamp() {
        return this.timestamp;
    }
}
