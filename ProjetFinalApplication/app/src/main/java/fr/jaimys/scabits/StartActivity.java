package fr.jaimys.scabits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

public class StartActivity extends AppCompatActivity{

    //_________________________________________fields_______________________________________________
    private final int SPLASH_SCREEN_TIMEOUT = 3000;

    //_________________________________________methods______________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //Change the font text
        TextView slogan = findViewById(R.id.catch_phrase);
        slogan.setTypeface(Typeface.createFromAsset(getAssets(), "summit_attack.ttf"));

        //Redirect to the main activity after a slight delay
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //Start the activity and close this one
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        };

        //Handler post delayed
        new Handler().postDelayed(runnable, SPLASH_SCREEN_TIMEOUT);
    }
}
