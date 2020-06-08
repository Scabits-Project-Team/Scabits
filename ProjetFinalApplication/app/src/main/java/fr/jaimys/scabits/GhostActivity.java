package fr.jaimys.scabits;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class GhostActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ghost);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

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