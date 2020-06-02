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
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements LocationListener {

    //_________________________________________fields_______________________________________________
    private LocationManager locationManager = null;
    private static final int PERM_REQ_ID = 1234;
    private boolean deniedForever = false;
    public static ParamLocation PARAM_LOCATION;
    public static LocationStorage LOCATION_STORAGE = null;

    //_________________________________________methods______________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instanciation of ParamLocation
        MainActivity.PARAM_LOCATION = new ParamLocation();


    }

    public void launchLoginActivity(View view) {
        //Redirect to the login page
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    public void launchSignUpActivity(View view) {
        //Redirect to the singup page
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();

        checkPermissions();
    }

    @Override
    protected void onPause() {
        super.onPause();

        /*if(this.locationManager != null){
            this.locationManager.removeUpdates(this);
        }*/
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

            MainActivity.LOCATION_STORAGE = new LocationStorage();
            MainActivity.LOCATION_STORAGE.setLocationManager(this.locationManager);
            MainActivity.LOCATION_STORAGE.setMainActivity(this);

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
                    .setTitle("Demande de permission")
                    .setMessage("La localisation GPS est nécessaire à l'utilisation de cette " +
                            "application, voulez-vous l'activer ?")
                    .setPositiveButton("Oui", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[] {
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                            }, PERM_REQ_ID);
                        }
                    })
                    .setNegativeButton("Non", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "Permissions refusées",
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
                Toast.makeText(this, "Permissions refusées pour toujours", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == PERM_REQ_ID){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this, "N'oubliez pas d'activer votre GPS et de " +
                        "relancer l'application s'il n'était pas déjà prêt", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        MainActivity.PARAM_LOCATION.setLatitude(location.getLatitude());
        MainActivity.PARAM_LOCATION.setLongitude(location.getLongitude());
        Log.d("ALED","OnLocationChange : " + MainActivity.PARAM_LOCATION.getLatitude() + " " +
                                                MainActivity.PARAM_LOCATION.getLongitude());
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
