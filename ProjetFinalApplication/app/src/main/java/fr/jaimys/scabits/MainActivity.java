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

/**
 * First activity display. Ask to the user the permission to use the GPS of his smartphone.
 * Allow the user to connect if he already has an account or to create a new account if not.
 */
public class MainActivity extends AppCompatActivity implements LocationListener {

    //_________________________________________fields_______________________________________________
    /**
     * Object that provides access to the system location services.
     */
    private LocationManager locationManager = null;
    /**
     * Request code of the permission request.
     */
    private static final int PERM_REQ_ID = 1234;
    /**
     * Indicate if the user has denied the access to the GPS and select the Never Ask Again option.
     */
    private boolean deniedForever = false;
    /**
     * Object that contains the longitude and latitude values of the current location of the
     * smartphone.
     */
    public static ParamLocation PARAM_LOCATION;
    /**
     * Object that contains the locationManager instance and the MainActivty instance.
     */
    public static LocationStorage LOCATION_STORAGE = null;

    //_________________________________________methods______________________________________________

    /**
     * Instanciate the PARAM_LOCATION attribute.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut
     *                           down then this Bundle contains the data it most recently supplied
     *                           in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Instanciation of ParamLocation
        MainActivity.PARAM_LOCATION = new ParamLocation();
    }

    /**
     * Launch the LoginActivity class and close this activity.
     * @param view the view.
     */
    public void launchLoginActivity(View view) {
        //Redirect to the login page
        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Launch the SignupActivity class and close this activity.
     * @param view the view.
     */
    public void launchSignUpActivity(View view) {
        //Redirect to the singup page
        Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Call the checkPermissions method so that each time the activity comes back in foreground we
     * can check if we still have the permission to use the GPS.
     */
    @Override
    protected void onResume() {
        super.onResume();

        checkPermissions();
    }

    /**
     * Check if we have the permission to use the GPS and if not we call the requestPermission method.
     */
    private void checkPermissions() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION)
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

    /**
     * If the user launch the application for the first time or if he previously denied the
     * permission, it launch a popup dialogue interface to ask him if he want to activate the GPS.
     * If the user previously denied the permission and check the Never Ask Again option it
     * simply display a Toast message to remind him that the application doesn't have access to
     * the GPS.
     */
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
                Toast.makeText(this, "Permissions refusées pour toujours",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Callback for the result from requesting permissions. This method is invoked for every call
     * on requestPermissions. If we have the permission we display a Toast message to remind the
     * user to activate the GPS and restart the application.
     * @param requestCode the request code passed in requestPermissions.
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions which is either
     *                     PERMISSION_GRANTED or PERMISSION_DENIED.
     *                     Never null.
     */
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

    /**
     * Called when the location has changed. We change the values of PARAM_LOCATION with the new
     * values.
     * @param location the updated location. This value cannot be null.
     */
    @Override
    public void onLocationChanged(Location location) {
        MainActivity.PARAM_LOCATION.setLatitude(location.getLatitude());
        MainActivity.PARAM_LOCATION.setLongitude(location.getLongitude());
        Log.d("ALED","OnLocationChange : " + MainActivity.PARAM_LOCATION.getLatitude() +
                                                 " " + MainActivity.PARAM_LOCATION.getLongitude());
    }

    /**
     * This callback will never be invoked on Android Q and above.  Method never changed but it was
     * needed to override it because of the LocationListener interface.
     * @param provider N/A.
     * @param status N/A.
     * @param extras N/A.
     */
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    /**
     * Called when the provider is enabled by the user. Method never changed but it was needed to
     * override it because of the LocationListener interface.
     * @param provider the name of the location provider that has become enabled. This value
     *                 cannot be null.
     */
    @Override
    public void onProviderEnabled(String provider) {

    }

    /**
     * Called when the provider is disabled by the user. If requestLocationUpdates is called on an
     * already disabled provider, this method is called immediately. Method never changed but it
     * was needed to override it because of the LocationListener interface.
     * @param provider the name of the location provider that has become disabled. This value
     *                 cannot be null.
     */
    @Override
    public void onProviderDisabled(String provider) {
    }
}
