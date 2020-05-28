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

public class SignupActivity extends AppCompatActivity {


    //_________________________________________fields_______________________________________________
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referenceData = database.getReference();
    private EditText new_pseudo;
    private EditText new_password;
    private EditText new_location;
    private ProgressBar loading;


    //_________________________________________methods______________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);


        //Recuperation of all EditText
        this.new_password = findViewById(R.id.new_password);
        this.new_pseudo = findViewById(R.id.new_pseudo);
        this.new_location = findViewById(R.id.new_location);
        this.loading = findViewById(R.id.progessbar_signup);


        //Setting of the buttons up
        Button btn_validate = findViewById(R.id.validate_signup);
        btn_validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getPseudo().length() < 4) {
                    //Pseudo requires at least 4 characters
                    Toast.makeText(getApplicationContext(), "Vous devez rentrer un pseudo " +
                                    "contenant au moins 4 caractères", Toast.LENGTH_SHORT).show();
                }
                else if (getPassord().length() < 4) {
                    //Password requires at least 4 characters
                    Toast.makeText(getApplicationContext(), "Vous devez rentrer un mot " +
                            "de passe contenant au moins 4 caractères", Toast.LENGTH_SHORT).show();
                }
                else if (getLocation().isEmpty()) {
                    //Password requires at least 4 characters
                    Toast.makeText(getApplicationContext(), "Vous devez rentrer une " +
                            "adresse postale", Toast.LENGTH_SHORT).show();
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

    ValueEventListener valueEventListener = new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            if (!dataSnapshot.exists()) {
                //Show the loading progress bar
                loading.setVisibility(View.VISIBLE);

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

                    //Hide the loading progress bar
                    loading.setVisibility(View.INVISIBLE);
                }
                else {
                    //Home location set
                    List<Double> home = new ArrayList<>();
                    home.add(longitude);
                    home.add(latitude);

                    //Add the new account
                    referenceData.child(getPseudo()).setValue(new User(getPassord(), Build.MODEL,
                            null,  home,  null,  null, null));

                    //Notify the user
                    Toast.makeText(getApplicationContext(), getPseudo() + " a été ajouté",
                            Toast.LENGTH_SHORT).show();

                    //Hide the loading progress bar
                    loading.setVisibility(View.INVISIBLE);

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

    private String getPseudo() {
        return this.new_pseudo.getText().toString();
    }

    private String getPassord() {
        return this.new_password.getText().toString();
    }

    private String getLocation() {
        return this.new_location.getText().toString();
    }
}
