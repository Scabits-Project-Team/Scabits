package fr.jaimys.scabits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;

public class HistoricActivity extends AppCompatActivity {

    //_________________________________________fields_______________________________________________
    private List<DataHabits> habitsArrayList;
    private RecyclerView recyclerView;
    private DataHabitsAdapter habitsAdapter;
    private static final String TAG = "HistoricalStatut";
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referenceData = database.getReference();
    private String pseudo;


    //_________________________________________methods______________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic);

        //Recuperation of the login
        this.pseudo = getIntent().getStringExtra("pseudo");

        //Instantiation of the list
        habitsArrayList = new ArrayList<>();

        //Get RecyclerView set it up
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Create DataHabitsAdapter and set it up
        habitsAdapter = new DataHabitsAdapter(this, habitsArrayList);
        recyclerView.setAdapter(habitsAdapter);

        //Add some data
        referenceData.child(this.pseudo).child("activityChecks").orderByChild("time")
                .addListenerForSingleValueEvent(valueEventListener);

        //Show elements
        Log.d(TAG,habitsArrayList.toString());

        //Notify data changed
        habitsAdapter.notifyDataSetChanged();
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            habitsArrayList.clear();
            if (dataSnapshot.exists()) {
                SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
                assert sensorManager != null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    ActivityCheck act = snapshot.getValue(ActivityCheck.class);
                    assert act != null;

                    //Find the exact date and time by passing the timestamp
                    Date date = new Date(act.getTime());
                    @SuppressLint("SimpleDateFormat") DateFormat formatter =
                            new SimpleDateFormat("HH:mm EEEE\ndd MMM yyyy ");
                    formatter.setTimeZone(TimeZone.getDefault());
                    String time = formatter.format(date);

                    //Find the day of the week
                    @SuppressLint("SimpleDateFormat") DateFormat formatter2 =new SimpleDateFormat("EEEE");
                    String day = formatter2.format(date);

                    //Set the icon activity
                    String tag;
                    switch (act.getRealActivity()) {
                        case "Travailler" : tag = "working"; break;
                        case "Dormir" : tag = "sleeping"; break;
                        case "Faire du sport" : tag = "working_out"; break;
                        case "Cuisiner" : tag = "cooking"; break;
                        case "Regarder la télévision" : tag = "tv"; break;
                        case "Magasiner" : tag = "shopping"; break;
                        case "Lire" : tag = "reading"; break;
                        case "Prendre les transports" : tag = "commuting"; break;
                        case "Jouer à des jeux" : tag = "games"; break;
                        default : tag = "unknow"; break;
                    }

                    //Set the sensors used by getting the number
                    StringBuilder sensors = new StringBuilder();
                    int i = 1;
                    for (SensorData sd : act.getSensorsInformations()) {
                        if (sd.isUsed()) {
                            switch (sd.getSensor()) {
                                case 1 : sensors.append("Accélération").append(", "); break;
                                case 5 : sensors.append("Luminosité").append(", "); break;
                                case 8 : sensors.append("Proximité").append(", "); break;
                                case 100 : sensors.append("Localisation").append(", "); break;
                                default: sensors.append("Inconnu").append(", "); break;
                            }
                        }
                    }
                    String sensorStr = sensors.substring(0,sensors.length()-2);

                    //Add this item in the list
                    habitsArrayList.add(0,
                             new DataHabits(act.getRealActivity(),act.getExpectedActivity(),
                                   tag,sensorStr, time));
                }
            }
            habitsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            //Never used
        }
    };

    public void backAccountPage(View view) {
        //Launch Account Activity and pass the pseudo
        Intent intent = new Intent(getApplicationContext(), AccountActivity.class);
        intent.putExtra("pseudo",  this.pseudo);
        startActivity(intent);
        finish();
    }
}
