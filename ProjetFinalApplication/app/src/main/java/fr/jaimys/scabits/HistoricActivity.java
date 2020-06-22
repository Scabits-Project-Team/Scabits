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

/**
 * Activity that show every data collection and survey send to the user on his activities.
 * @see AccountActivity
 */
public class HistoricActivity extends AppCompatActivity {

    //_________________________________________fields_______________________________________________
    /**
     * The list of data collection.
     */
    private List<DataHabits> habitsArrayList;
    /**
     * The adapter to load items.
     */
    private DataHabitsAdapter habitsAdapter;
    /**
     * The instance of the database (Firebase).
     */
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    /**
     * The reference of the root in the database.
     */
    private DatabaseReference referenceData = database.getReference();
    /**
     * The login of the user.
     */
    private String pseudo;
    /**
     * The progress bar showed until items are loaded.
     */
    private ProgressBar loading;
    /**
     * The text to inform the user that no data collection has been done yet.
     */
    private TextView noItemText;
    /**
     * Event that load items in the list by checking in the database.
     */
    private ValueEventListener valueEventListener = new ValueEventListener() {
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


    //_________________________________________methods______________________________________________
    /**
     * Create the fields required. Set adapter and recycler view and call the filling of the list.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                           down then this Bundle contains the data it most recently supplied
     *                           in onSaveInstanceState(Bundle).
     */
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

    /**
     * Hide the loading bar.
     */
    private void hideLoading() {
        this.loading.setVisibility(View.INVISIBLE);
    }

    /**
     * Get back to the Account Activity.
     */
    public void backAccountPage() {
        //Launch Account Activity and pass the pseudo just in case
        getIntent().putExtra("pseudo",  this.pseudo);
        finish();
    }
}
