package fr.jaimys.scabits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class HistoricActivity extends AppCompatActivity {

    //_________________________________________fields_______________________________________________
    private List<DataHabits> habitsArrayList;
    private DataHabitsAdapter habitsAdapter;
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referenceData = database.getReference();
    private String pseudo;
    private ProgressBar loading;
    private TextView noItemText;


    //_________________________________________methods______________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_historic);

        //Set the loading
        this.loading = findViewById(R.id.loadHistoric);
        this.noItemText = findViewById(R.id.no_item_text);

        //Recuperation of the login
        this.pseudo = getIntent().getStringExtra("pseudo");
        //Instantiation of the list
        habitsArrayList = new ArrayList<>();

        //Get RecyclerView set it up
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Create DataHabitsAdapter and set it up
        habitsAdapter = new DataHabitsAdapter(this, habitsArrayList);
        recyclerView.setAdapter(habitsAdapter);

        //Add some data
        referenceData.child(this.pseudo).child("activityChecks").orderByChild("time")
                .addListenerForSingleValueEvent(valueEventListener);

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

                    //Set the icon activity
                    String tag;
                    switch (act.getRealActivity()) {
                        case "Travailler" : tag = "working"; break;
                        case "Dormir" : tag = "sleeping"; break;
                        case "Faire du sport" : tag = "working_out"; break;
                        case "Téléphoner" : tag = "call"; break;
                        case "Regarder la télévision" : tag = "tv"; break;
                        case "Manger" : tag = "eat"; break;
                        case "Magasiner" : tag = "shopping"; break;
                        case "Prendre les transports" : tag = "commuting"; break;
                        case "Jouer à des jeux" : tag = "games"; break;
                        default : tag = "unknow"; break;
                    }

                    //Set the sensors used by getting the number
                    StringBuilder sensors = new StringBuilder();
                    for (String key : act.getSensorsInformations().keySet())
                    {
                        if (Objects.requireNonNull(act.getSensorsInformations().get(key)).isUsed()) {
                            switch (key) {
                                case "S1" : sensors.append("Accélération").append(", "); break;
                                case "S5" : sensors.append("Luminosité").append(", "); break;
                                case "S8" : sensors.append("Proximité").append(", "); break;
                                case "S4" : sensors.append("Gyroscope").append(", "); break;
                                case "S2" : sensors.append("Magnétomètre").append(", "); break;
                                default: sensors.append("Autre").append(", "); break;
                            }
                        }
                    }
                    if (!act.getLocation().equals(new Location(0d,0d))){
                        sensors.append("Localisation").append(", ");
                    }

                    String sensorStr;
                    if (sensors.length() != 0) {
                        sensorStr = sensors.substring(0,sensors.length()-2);
                    }
                    else {
                        sensorStr = "Aucun capteur utilisé";
                    }

                    //Add this item in the list
                    habitsArrayList.add(0,
                             new DataHabits(act.getRealActivity(),act.getExpectedActivity(),
                                   tag,sensorStr, time));
                }
            }
            else {
                noItemText.setVisibility(View.VISIBLE);
            }
            hideLoading();
            habitsAdapter.notifyDataSetChanged();
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            //Never used
        }
    };

    private void hideLoading() {
        this.loading.setVisibility(View.INVISIBLE);
    }

    public void backAccountPage(View view) {
        //Launch Account Activity and pass the pseudo just in case
        getIntent().putExtra("pseudo",  this.pseudo);
        finish();
    }
}
