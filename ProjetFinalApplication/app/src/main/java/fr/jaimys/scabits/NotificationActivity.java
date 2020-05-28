package fr.jaimys.scabits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
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

public class NotificationActivity extends AppCompatActivity {

    //_________________________________________fields_______________________________________________
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referenceData = database.getReference();
    private DatabaseReference referenceUser;
    private String pseudo;
    private View currentView = null;
    private String activity = "Aucune";
    private long timestamp;


    //_________________________________________methods______________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        //Recuperation of the login
        this.pseudo = getIntent().getStringExtra("pseudo");
        assert this.pseudo != null;
        referenceUser = referenceData.child(this.pseudo).child("activityChecks");

        //Set the timestamp (TODO : Recup the timestamp get before)
        this.timestamp = System.currentTimeMillis();

    }

    public void addCheckActivity(View view) {

        //If an activity is selected
        if (this.currentView != null && !this.activity.equals("Aucune")) {

            //Search for sensors
            SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
            assert sensorManager != null;
            int light = Sensor.TYPE_LIGHT;

            //Add Sensors and values TODO : Here check the real values
            List<SensorData> listeSensor = new ArrayList<>();
            HashMap<String, Float> value1 = new HashMap<>();
            value1.put("x", (float) 10); value1.put("y", (float) 11); value1.put("z", (float) 12);
            HashMap<String, Float> value2 = new HashMap<>();
            value2.put("x", (float) 13); value2.put("y", (float) 14); value2.put("z", (float) 15);
            HashMap<String, Float> value3 = new HashMap<>();
            value3.put("x", (float) 16); value3.put("y", (float) 17); value3.put("z", (float) 18);
            listeSensor.add(new SensorData(light, true, value1, value2, value3));
            listeSensor.add(new SensorData(Sensor.TYPE_ACCELEROMETER, true, value1, value2, value3));
            listeSensor.add(new SensorData(Sensor.TYPE_PROXIMITY, false, value1, value2, value3));
            listeSensor.add(new SensorData(100, true, value1, value2, value3));

            String excpectedActivity = "Jouer à des jeux";

            //Add the new checkActivity
            referenceUser.child(Objects.requireNonNull(referenceUser.push().getKey())).setValue(
                    new ActivityCheck(this.timestamp, listeSensor, this.activity, excpectedActivity));


            //Add in the agenda this Activity if doesnt exist yet
            addActivityInAgenda();

            //Notify the user
            Toast.makeText(getApplicationContext(), "Les données ont été ajoutées",
                    Toast.LENGTH_SHORT).show();

            //Return to the account page
            Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
            intent.putExtra("pseudo",  this.pseudo);
            startActivity(intent);
            finish();
        }
        else {
            //Notify the user to choose an activity
            Toast.makeText(getApplicationContext(), "Veuillez choisir une activité",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("RtlHardcoded")
    public void selectItem(View view) {
        //Select and change the item if needed
        LinearLayout linearLayout = (LinearLayout) view;
        if (view.equals(this.currentView)) {
            this.currentView = null;
            view.setBackground(null);
            linearLayout.setGravity(Gravity.CENTER|Gravity.TOP);
            this.activity = "Aucune";
        }
        else {
            if (this.currentView != null) {
                LinearLayout linearLayoutOld = (LinearLayout) this.currentView;
                this.currentView.setBackground(null);
                linearLayoutOld.setGravity(Gravity.CENTER|Gravity.TOP);
            }
            this.currentView = view;
            linearLayout.setBackground(getResources().getDrawable(R.drawable.button_item_select_design));
            linearLayout.setGravity(Gravity.CENTER);
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
                HashMap<String, DailyActivities> basicWeek;
                if (user.getBasicWeek() != null) {
                    basicWeek = user.getBasicWeek();
                }
                else {
                    basicWeek = new HashMap<>();
                }

                //If this day is not aleardy initalised in the database
                if (!basicWeek.containsKey(day)) {

                    //Create a new day, hour and activity
                    HashMap<String, List<ActivityDone>> activitiesParHour = new HashMap<>();
                    List<ActivityDone> activities = new ArrayList<>();
                    activities.add(new ActivityDone(getActivity(),1));
                    activitiesParHour.put(hour, activities);
                    DailyActivities dailyActivities = new DailyActivities(activitiesParHour);
                    basicWeek.put(day, dailyActivities);

                    //Add the day, hour and activity
                    referenceData.child(pseudo).child("basicWeek").setValue(basicWeek);
                }
                else {
                    HashMap<String, List<ActivityDone>> activitiesParHour =
                            Objects.requireNonNull(basicWeek.get(day)).getActivitiesParHour();

                    if (activitiesParHour.containsKey(hour))
                    {
                        //Check if this activity already exists
                        List<ActivityDone> activities = activitiesParHour.get(hour);
                        assert activities != null;
                        int index = -1;
                        for (int i = 0; i < activities.size(); i++) {
                            if (activities.get(i).getActivity().equals(getActivity())) {
                                index = i;
                                break;
                            }
                        }

                        //If the activity is not already in the list
                        if (index == -1) {
                            //Add the activity at the day and hour
                            activities.add(new ActivityDone(getActivity(),1));
                            referenceData.child(pseudo).child("basicWeek").child(day).
                                    child("activitiesParHour").child(hour).
                                    setValue(activities);
                        }
                        else {
                            //Increase the number of time this activity has been done
                            activities.get(index).setOccurence(activities.get(index).getOccurence() + 1);
                            referenceData.child(pseudo).child("basicWeek").child(day).
                                    child("activitiesParHour").child(hour).
                                    setValue(activities);
                        }
                    }
                    else {
                        //Add the activity at the day and create a new hour
                        List<ActivityDone> activities = new ArrayList<>();
                        activities.add(new ActivityDone(getActivity(),1));
                        activitiesParHour.put(hour, activities);
                        referenceData.child(pseudo).child("basicWeek").child(day).
                                child("activitiesParHour").setValue(activitiesParHour);
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
