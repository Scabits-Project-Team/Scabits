package fr.jaimys.scabits;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Activity that will always be in background but never close until the user shut down the application.
 * This activity remove the location listener when the user shut down the application.
 */
public class GhostActivity extends AppCompatActivity {

    /**
     * Launch the MainActivity class.
     * @param savedInstanceState If the activity is being re-initialized after previously being shut down then this Bundle contains
     *                           the data it most recently supplied in onSaveInstanceState(Bundle).
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    /**
     * Remove the location listener so that the onLocationChanged will never be called anymore.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d("ALED", "Désenregistrement location");

        if(MainActivity.LOCATION_STORAGE != null){
            MainActivity.LOCATION_STORAGE.getLocationManager()
                    .removeUpdates(MainActivity.LOCATION_STORAGE.getMainActivity());
        }
    }
}