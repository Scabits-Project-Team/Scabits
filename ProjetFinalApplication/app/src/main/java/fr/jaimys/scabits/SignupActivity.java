package fr.jaimys.scabits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.sql.Time;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

/**
 * Activity that allow the user to register and his informations are stored in the database.
 * @see LoginActivity
 */
public class SignupActivity extends AppCompatActivity {

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
     * The new login of the user.
     */
    private EditText new_pseudo;
    /**
     * The new password of the user.
     */
    private EditText new_password;
    /**
     * The city where the user live.
     */
    private EditText new_location_city;
    /**
     * The house' number where the user live.
     */
    private EditText new_location_number;
    /**
     * The country where the user live.
     */
    private EditText new_location_country;
    /**
     * The road where the user live.
     */
    private EditText new_location_road;
    /**
     * Event that search in the database is the user already exists. If it's a new one, we verify
     * the address and store the user.
     */
    private ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (!dataSnapshot.exists()) {
                //Creation of the home localisation
                double longitude = -1;
                double latitude = -1;
                Geocoder geocoder = new Geocoder(SignupActivity.this);
                List<Address> addresses;
                try {
                    addresses = geocoder.getFromLocationName(getLocation(), 1);

                    //If the adresse matchs with a real location
                    if(addresses != null && !addresses.isEmpty()){
                        Address location = addresses.get(0);
                        longitude = location.getLongitude();
                        latitude = location.getLatitude();
                    }
                }
                catch (IOException e){
                    e.printStackTrace();
                }

                if ((longitude == -1) && (latitude == -1)) {
                    Toast.makeText(getApplicationContext(), "Cette adresse n'est pas " +
                            "valable, veuillez réessayer", Toast.LENGTH_SHORT).show();
                }
                else {
                    //Home location set
                    Location home = new Location();
                    home.setLongitude(longitude);
                    home.setLatitude(latitude);

                    //Add the new account
                    referenceData.child(getPseudo()).setValue(new User(getPassord(), Build.MODEL,
                            null,  home,  null,  null, null, null));

                    //Notify the user
                    Toast.makeText(getApplicationContext(), getPseudo() + " a été ajouté",
                            Toast.LENGTH_SHORT).show();

                    //Redirect to the login page
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
            else {
                Toast.makeText(getApplicationContext(), "Ce pseudo existe déjà, " +
                        "veuillez réessayer", Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            //Never used
        }
    };


    //_________________________________________methods______________________________________________
    /**
     * Create the fields required. Buttons are setting up and linked to their function.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                           down then this Bundle contains the data it most recently supplied
     *                           in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Recuperation of all EditText
        this.new_password = findViewById(R.id.new_password);
        this.new_pseudo = findViewById(R.id.new_pseudo);
        this.new_location_city = findViewById(R.id.new_location_city);
        this.new_location_number = findViewById(R.id.new_location_number);
        this.new_location_country = findViewById(R.id.new_location_country);
        this.new_location_road = findViewById(R.id.new_location_road);
        //this.loading = findViewById(R.id.progessbar_signup);


        //Setting of the buttons up
        Button btn_validate = findViewById(R.id.validate_signup);
        btn_validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getPseudo().length() < 4) {
                    //Pseudo requires at least 4 characters
                    Toast.makeText(getApplicationContext(), "Vous devez saisir un pseudo " +
                                    "contenant au moins 4 caractères", Toast.LENGTH_SHORT).show();
                }
                else if (getPassord().length() < 4) {
                    //Password requires at least 4 characters
                    Toast.makeText(getApplicationContext(), "Vous devez saisir un mot " +
                            "de passe contenant au moins 4 caractères", Toast.LENGTH_SHORT).show();
                }
                else if (getNumber().isEmpty() || getRoad().isEmpty() || getCity().isEmpty() ||
                         getCountry().isEmpty()) {
                    //Password requires at least 4 characters
                    Toast.makeText(getApplicationContext(), "Vous devez saisir une " +
                            "adresse complète", Toast.LENGTH_SHORT).show();
                }
                else {
                    referenceData.child(getPseudo()).addListenerForSingleValueEvent(valueEventListener);
                }
            }
        });

        Button btn_back_log = findViewById(R.id.return_log);
        btn_back_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Redirect to the home page, where we can signin or signup
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    /**
     * Get the pseudo of the user.
     * @return the pseudo.
     */
    private String getPseudo() {
        return this.new_pseudo.getText().toString();
    }

    /**
     * Get the password of the user.
     * @return the password.
     */
    private String getPassord() {
        return this.new_password.getText().toString();
    }

    /**
     * Get the house'number where the user live.
     * @return the house'number.
     */
    private String getNumber() {
        return this.new_location_number.getText().toString();
    }

    /**
     * Get the road where the user live.
     * @return the road.
     */
    private String getRoad() {
        return this.new_location_road.getText().toString();
    }

    /**
     * Get the city where the user live.
     * @return the city.
     */
    private String getCity() {
        return this.new_location_city.getText().toString();
    }

    /**
     * Get the country where the user live.
     * @return the country.
     */
    private String getCountry() {
        return this.new_location_country.getText().toString();
    }

    /**
     * Get the entire location.
     * @return the entire location.
     */
    private String getLocation() {
        return getNumber() + " " + getRoad() + ", " + getCity() + ", " + getCountry();
    }
}
