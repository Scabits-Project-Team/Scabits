package fr.jaimys.scabits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
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

public class StartActivity extends AppCompatActivity implements LocationListener, SensorEventListener {

    //_________________________________________fields_______________________________________________
    //private final int SPLASH_SCREEN_TIMEOUT = 3000;
    private LocationManager locationManager = null;
    private static final int PERM_REQ_ID = 1234;
    private boolean deniedForever = false;
    private ParamSensors paramSensors;
    private SensorManager sensorManager;
    private Sensor sensorLight;
    private Sensor sensorProximity;
    private Sensor sensorAccel;
    private Handler handler10s = new Handler();
    private Handler handler7h = new Handler();
    private int compteurRegister;
    private boolean handlerLance = false;
    private boolean register;

    private Runnable schedule7h = new Runnable() {
        @Override
        public void run() {
            compteurRegister = 1;
            register = false;
            handler10s.post(schedule10s);

            //Recuperation of each data
            Log.d("ALED", "Recup des données");

            //Send notification

            handler7h.postDelayed(schedule7h, 50 * 1000 - SystemClock.elapsedRealtime()%1000);
        }
    };

    private Runnable schedule10s = new Runnable() {
        @Override
        public void run() {
            if(compteurRegister <= 10) {
                if(!register){
                    Log.d("ALED", "Enregistrement");
                    sensorManager.registerListener(StartActivity.this, sensorLight,
                            SensorManager.SENSOR_DELAY_GAME);
                    sensorManager.registerListener(StartActivity.this, sensorProximity,
                            SensorManager.SENSOR_DELAY_GAME);
                    sensorManager.registerListener(StartActivity.this, sensorAccel,
                            SensorManager.SENSOR_DELAY_UI);
                    register = true;
                }
                compteurRegister++;
                handler10s.postDelayed(schedule10s,1000 - SystemClock.elapsedRealtime()%1000);
            }
            else{
                Log.d("ALED", "Désenregistrement");
                sensorManager.unregisterListener(StartActivity.this);
            }
        }
    };



    //_________________________________________methods______________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //
        this.paramSensors = new ParamSensors();

        //
        this.sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        assert sensorManager != null;
        this.sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        this.sensorProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        this.sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Change the font text
        TextView slogan = findViewById(R.id.catch_phrase);
        slogan.setTypeface(Typeface.createFromAsset(getAssets(), "summit_attack.ttf"));
/*
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
        new Handler().postDelayed(runnable, SPLASH_SCREEN_TIMEOUT);  */
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("ALED", "C'est la petite pause ici là");

        //TODO : mettre ce code dans le OnDestroy
        if(this.locationManager != null){
            this.locationManager.removeUpdates(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d("ALED", "C'est le ptit resume ici là");

        //
        if(!handlerLance){
            handlerLance = true;
            handler7h.post(schedule7h);
        }

        //
        checkPermissions();
    }

    private void checkPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
           && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED){

            this.locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
            assert this.locationManager != null;

            if (this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                        5000, 0, this);
            }
            if(this.locationManager.isProviderEnabled(LocationManager.PASSIVE_PROVIDER)){
                this.locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER,
                        5000, 0, this);
            }
            if(this.locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
                this.locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                        5000, 0, this);
            }

        }
        else{
            requestPermission();
        }
    }

    private void requestPermission(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                && ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)){

            new AlertDialog.Builder(this)
                    .setTitle("Permission needed")
                    .setMessage("GPS Location is needed to improve a lot the research of your activities")
                    .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(StartActivity.this, new String[] {
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                            }, PERM_REQ_ID);
                        }
                    })
                    .setNegativeButton("I don't want", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Toast.makeText(StartActivity.this, "Permissions DENIED",
                                    Toast.LENGTH_SHORT).show();
                        }
                    })
                    .create()
                    .show();

        }
        else{
            if(!this.deniedForever){
                ActivityCompat.requestPermissions(this, new String[] {
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                }, PERM_REQ_ID);
                this.deniedForever = true;
            }
            else{
                Toast.makeText(this, "Permissions DENIED FOREVER", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == PERM_REQ_ID){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "Don't forget to activate your GPS and restart" +
                        " the application if so", Toast.LENGTH_LONG).show();
            }
        }
    }


    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_LIGHT :
                this.paramSensors.setLight(event.values[0]);
                Log.d("ALED", this.paramSensors.getLight() + " lx");
                break;
            case Sensor.TYPE_PROXIMITY :
                this.paramSensors.setProximity(event.values[0]);
                Log.d("ALED", this.paramSensors.getProximity() + " cm");
                break;
            case Sensor.TYPE_ACCELEROMETER :
                this.paramSensors.setAccelX(event.values[0]);
                this.paramSensors.setAccelY(event.values[1]);
                //Log.d("ALED", "X : " + this.paramSensors.getAccelX() + ", Y : " + this.paramSensors.getAccelY());
                break;
            default :
                break;
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onLocationChanged(Location location) {
        this.paramSensors.setLatitude(location.getLatitude());
        this.paramSensors.setLongitude(location.getLongitude());
        Log.d("ALED","Params : " + paramSensors.getLatitude() + "  " + paramSensors.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
