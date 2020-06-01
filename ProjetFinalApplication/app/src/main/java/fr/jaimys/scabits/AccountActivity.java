package fr.jaimys.scabits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Objects;

public class AccountActivity extends AppCompatActivity implements SensorEventListener {

    //_________________________________________fields_______________________________________________
    private static final String CANAL = "MyCanal";
    private String pseudo;
    private ParamSensors paramSensors;
    private SensorManager sensorManager;
    private Sensor sensorLight;
    private Sensor sensorProximity;
    private Sensor sensorAccel;
    private Handler handler10s = new Handler();
    private Handler handler7h = new Handler();
    private int compteurRegister;
    private boolean handlerLance;
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
                    sensorManager.registerListener(AccountActivity.this, sensorLight,
                            SensorManager.SENSOR_DELAY_GAME);
                    sensorManager.registerListener(AccountActivity.this, sensorProximity,
                            SensorManager.SENSOR_DELAY_GAME);
                    sensorManager.registerListener(AccountActivity.this, sensorAccel,
                            SensorManager.SENSOR_DELAY_UI);
                    register = true;
                }
                compteurRegister++;
                handler10s.postDelayed(schedule10s,1000 - SystemClock.elapsedRealtime()%1000);
            }
            else{
                Log.d("ALED", "Désenregistrement");
                sensorManager.unregisterListener(AccountActivity.this);
            }
        }
    };

    //_________________________________________methods______________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //Recuperation of the login
        this.pseudo = getIntent().getStringExtra("pseudo");
        assert this.pseudo != null;

        this.handlerLance = false;

        //Instanciation of ParamSensors
        this.paramSensors = new ParamSensors();

        //Recuperation of sensors
        this.sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        assert sensorManager != null;
        this.sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        this.sensorProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        this.sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Setting of the buttons up
        Button btn_disconnect = findViewById(R.id.disconnect);
        btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        Button btn_history = findViewById(R.id.history_button);
        btn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoricActivity.class);
                intent.putExtra("pseudo",  getPseudo());
                startActivity(intent);
                finish();
            }
        });
    }

    private String getPseudo() {
        return this.pseudo;
    }

    public void notify(View view) {
        //Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);//FLAG_ACTIVITY_NEW_TASK | FLAG_ACTIVITY_CLEAR_TASK
        intent.putExtra("pseudo",  this.pseudo);
        intent.putExtra("activityCheck", createActivityCheck());

        PendingIntent pendingIntent = PendingIntent.getActivity(
                AccountActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Create a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (AccountActivity.this, CANAL);
        builder.setSmallIcon(R.drawable.notification_icon);
        builder.setContentTitle("Activité rappel");
        builder.setContentText("Que faites-vous actuellement ?");
        builder.setPriority(NotificationCompat.PRIORITY_DEFAULT);

        //Set the question page that will fire when the user taps the notification
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        //Launch notification
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                Context.NOTIFICATION_SERVICE
        );
        assert notificationManager != null;

        //Create a canal for the android version greater than Oreo
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            String channelID = getString(R.string.canalId);
            String channelTitle = getString(R.string.canalTitle);
            String channelDesc = getString(R.string.canalDesc);
            NotificationChannel notificationChannel = new NotificationChannel(channelID, channelTitle, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(channelDesc);
            notificationManager.createNotificationChannel(notificationChannel);
            builder.setChannelId(channelID);
        }

        notificationManager.notify(0, builder.build());
    }


    private ActivityCheck createActivityCheck() {
        //Get the current timestamp
        long timestamp = System.currentTimeMillis();

        //Search for sensors
        SensorManager sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        assert sensorManager != null;

        //Add Sensors and values TODO : Here check the real values
        HashMap<String, SensorData>  sensorInfos = new HashMap<>();
        HashMap<String, Float> value1 = new HashMap<>(), value2 = new HashMap<>(), value3 = new HashMap<>();
        value1.put("luminosity", (float) 10);
        value2.put("luminosity", (float) 13);
        value3.put("luminosity", (float) 16);

        HashMap<String, Float> value4 = new HashMap<>(), value5 = new HashMap<>(), value6 = new HashMap<>();
        value4.put("proximity", (float) 0);
        value5.put("proximity", (float) 0);
        value6.put("proximity", (float) 8);

        HashMap<String, Float> value7 = new HashMap<>(), value8 = new HashMap<>(), value9 = new HashMap<>();
        value7.put("X", (float) 15); value7.put("Y", (float) 51);
        value8.put("X", (float) 15); value8.put("Y", (float) 51);
        value9.put("X", (float) 14); value9.put("Y", (float) 26);

        sensorInfos.put(String.valueOf(Sensor.TYPE_LIGHT), new SensorData(true,
                value1, value2, value3));
        sensorInfos.put(String.valueOf(Sensor.TYPE_PROXIMITY), new SensorData(true,
                value4, value5, value6));
        sensorInfos.put(String.valueOf(Sensor.TYPE_ACCELEROMETER), new SensorData(true,
                value7, value8, value9));

        //Get Location
        Location location = new Location(-72.0714767,48.4165147);

        //Find the activity thanks to sensors data
        String excpectedActivity = findActivity(sensorInfos, location);

        return new ActivityCheck(timestamp, location, sensorInfos,
                "Aucune", excpectedActivity);
    }


    private String findActivity(HashMap<String, SensorData> sensorInfos, Location location) {
        /*
        HashMap<String, Integer> activities = new HashMap<>();
        activities.put("Travailler",0);
        activities.put("Dormir",0);
        activities.put("Faire du sport",0);
        activities.put("Cuisiner",0);
        activities.put("Regarder la télévision",0);
        activities.put("Magasiner",0);
        activities.put("Lire",0);
        activities.put("Prendre les transports",0);
        activities.put("Jouer à des jeux",0);

        //Find the day number (sunday to monday)
        Date date = new Date(getTimestamp());
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("EEEE");
        String day = formatter.format(date);

        //Find the hour
        @SuppressLint("SimpleDateFormat") DateFormat formatter2 = new SimpleDateFormat("HH");
        String hour = formatter2.format(date);

        if ((this.user.getBasicWeek() != null) && (this.user.getBasicWeek().containsKey(day))  &&
            (Objects.requireNonNull(this.user.getBasicWeek().get(day)).containsKey(hour))  )
        {
            HashMap<String, Integer> habitsActivities = Objects.requireNonNull(Objects.requireNonNull(
                    this.user.getBasicWeek().get(day)).get(hour)).getActivities();

            int sum = 0;
            for (Integer nbrOccurence: habitsActivities.values()) {
                sum += nbrOccurence;
            }

            for (String activity: habitsActivities.keySet()) {
                activities.put("Jouer à des jeux", habitsActivities.get(activity)/sum);
            }
        }

        switch (day) {
            case "lundi" :
                break;
            case "mardi" :
                break;
            case "mercredi" :
                break;
            case "jeudi" :
                break;
            case "vendredi" :
                break;
            case "samedi" :
                break;
            case "dimanche" :
                break;
            default :
                break;
        }

        //If the device is against a surface, the light proximity can't be used
        //noinspection ConstantConditions
        if (sensorInfos.get(String.valueOf(Sensor.TYPE_PROXIMITY)).getValue1().get("proximity") == 0f
            && sensorInfos.get(String.valueOf(Sensor.TYPE_PROXIMITY)).getValue2().get("proximity") == 0f
            && sensorInfos.get(String.valueOf(Sensor.TYPE_PROXIMITY)).getValue3().get("proximity") == 0f)
        {
            Objects.requireNonNull(sensorInfos.get(String.valueOf(Sensor.TYPE_LIGHT))).setUsed(false);
        }
        else {
            //noinspection ConstantConditions
            float averageLuminosity =
                    (sensorInfos.get(String.valueOf(Sensor.TYPE_LIGHT)).getValue1().get("luminosity")
                    + sensorInfos.get(String.valueOf(Sensor.TYPE_LIGHT)).getValue2().get("luminosity")
                    + sensorInfos.get(String.valueOf(Sensor.TYPE_LIGHT)).getValue3().get("luminosity")) /3;

            if (averageLuminosity < 10) {
                int nbr = activities.get("Dormir");
                activities.remove("Dormir");
                activities.put("Dormir",nbr + 50);
            }
            else if (averageLuminosity < 50) {
                //Activité intérieur ou peu lumineux dehors ?
            }
            else {
                //Activités exterieur ? ou le soir ?
            }
        }

        if (this.user.getHome().equals(location)) {
            activities.remove("Prendre les transports");
            activities.remove("Magasiner");
        }
        else if (this.user.getWork() != null && this.user.getWork().equals(location)) {
            int nbr = activities.get("Travailler");
            activities.remove("Travailler");
            activities.put("Travailler", nbr + 50);
        }
        else {
            Log.d("ALED", activities.toString());
        }*/

        return "Travailler";
    }

    @Override
    protected void onResume() {
        super.onResume();

        /*if(!handlerLance){
            handler7h.post(schedule7h);
        }*/

        Log.d("ALED","Params : " + MainActivity.PARAM_LOCATION.getLatitude() + " " +
                MainActivity.PARAM_LOCATION.getLongitude());
    }

    @Override
    protected void onPause() {
        super.onPause();

        this.handlerLance = true;
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
                //Log.d("ALED", this.paramSensors.getProximity() + " cm");
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
}

