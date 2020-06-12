package fr.jaimys.scabits;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import android.annotation.SuppressLint;
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
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;



public class AccountActivity extends AppCompatActivity implements SensorEventListener {

    //_________________________________________fields_______________________________________________
    //Notification fields
    private static final String CANAL = "MyCanal";

    //Time fields
    //public static int TIME_PARENT = 120; //in seconds
    public static int TIME_PARENT = 5; //in hours
    public static int TIME_CHILD = 10; //in seconds

    //Activity search fields
    private HashMap<Integer, HashMap<String, Integer>> weekActivitiesStat;
    private HashMap<Integer, HashMap<String, Integer>> weekEndActivitiesStat;

    //Global fields
    private String pseudo;
    private Button btn_history;

    //Database
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referenceData = database.getReference();
    private User user = null;


    //Sensors fields
    private ParamSensors paramSensors;
    private SensorManager sensorManager;
    private Sensor sensorLight, sensorProximity, sensorAccel, sensorMagneto, sensorGyro;
    private HashMap<String, SensorData>  sensorInfos = new HashMap<>();
    private Location locationInfos = new Location(0d,0d);
    private Location latestLocInfos = new Location(0d,0d);

    //Runnable, handler and timer
    private Button launchCheck;
    private TextView timerCheck;
    private TextView textDataCheck;
    private Handler handler10s = new Handler();
    private Handler handler7h = new Handler();
    private int compteurRegister;
    private boolean register;

    private Runnable schedule7h = new Runnable() {
        @Override
        public void run() {
            Log.d("ALED", "Launched");
            //int time = 120;
            //new RepetAction(TIME_PARENT);
            new RepetAction(TIME_PARENT * 3600);
            compteurRegister = 1;
            register = false;
            handler10s.post(schedule10s);

            //handler7h.postDelayed(schedule7h, TIME_PARENT * 1000 - SystemClock.elapsedRealtime()%1000);
            handler7h.postDelayed(schedule7h, TIME_PARENT * 3600 * 1000 - SystemClock.elapsedRealtime()%1000);
        }
    };

    private Runnable schedule10s = new Runnable() {
        @Override
        public void run() {
            if(compteurRegister <= TIME_CHILD) {
                if(!register){
                    //Register listeners
                    sensorManager.registerListener(AccountActivity.this, sensorLight,
                            SensorManager.SENSOR_DELAY_GAME);
                    sensorManager.registerListener(AccountActivity.this, sensorProximity,
                            SensorManager.SENSOR_DELAY_GAME);
                    sensorManager.registerListener(AccountActivity.this, sensorAccel,
                            SensorManager.SENSOR_DELAY_UI);
                    sensorManager.registerListener(AccountActivity.this, sensorMagneto,
                            SensorManager.SENSOR_DELAY_NORMAL);
                    sensorManager.registerListener(AccountActivity.this, sensorGyro,
                            SensorManager.SENSOR_DELAY_NORMAL);

                    register = true;
                }

                //Data check
                getDataSensors(compteurRegister);

                compteurRegister++;
                handler10s.postDelayed(schedule10s,1000 - SystemClock.elapsedRealtime()%1000);
            }
            else{
                sensorManager.unregisterListener(AccountActivity.this);
            }
        }
    };


    //_________________________________________methods______________________________________________
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        //Set the basic appearance stats for each activity
        intialisationOfActivityStats();

        //Recuperation of the login
        this.pseudo = getIntent().getStringExtra("pseudo");
        assert this.pseudo != null;

        //Instanciation of ParamSensors
        this.paramSensors = new ParamSensors();

        //Recuperation of sensors
        this.sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        assert sensorManager != null;
        this.sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        this.sensorProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        this.sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.sensorMagneto = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        this.sensorGyro = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

        //Setting of the buttons and textview up
        this.timerCheck = findViewById(R.id.timer_next_check);
        this.launchCheck = findViewById(R.id.add_check_button);
        this.textDataCheck = findViewById(R.id.text_datacheck);

        ImageView img_settings = findViewById(R.id.img_settings);
        img_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AccountActivity.this, PasswordActivity.class);
                startActivity(intent);
            }
        });

        Button btn_disconnect = findViewById(R.id.disconnect);
        btn_disconnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handler7h.removeCallbacksAndMessages(null);
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        this.btn_history = findViewById(R.id.history_button);
        this.btn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), HistoricActivity.class);
                intent.putExtra("pseudo",  getPseudo());
                startActivity(intent);
            }
        });

        //Find the user
        referenceData.child(this.pseudo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    setUser(dataSnapshot.getValue(User.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setUser(User value) {
        this.user = value;
    }

    private String getPseudo() {
        return this.pseudo;
    }

    public void notify(String time) {
        //Create an explicit intent for an Activity in your app
        Intent intent = new Intent(this, NotificationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("pseudo",  this.pseudo);
        intent.putExtra("activityCheck", createActivityCheck());

        PendingIntent pendingIntent = PendingIntent.getActivity(
                AccountActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        //Create a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder
                (AccountActivity.this, CANAL);
        builder.setSmallIcon(R.drawable.notification_icon);
        builder.setContentTitle("Activité rappel");
        builder.setContentText("Que faisiez-vous à "+ time +" ?");
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
            NotificationChannel notificationChannel = new NotificationChannel(channelID,
                    channelTitle, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription(channelDesc);
            notificationManager.createNotificationChannel(notificationChannel);
            builder.setChannelId(channelID);
        }

        notificationManager.notify(0, builder.build());
    }

    private void getDataSensors(int compteurRegister) {
        //Get only 3 times each data
        if (compteurRegister % 3 == 0) {
            //Can't reach historic
            this.btn_history.setEnabled(false);

            //Light
            HashMap<String, Float> lightValue = new HashMap<>();
            lightValue.put("luminosity", this.paramSensors.getLight());

            //Proximity
            HashMap<String, Float> proximityValue = new HashMap<>();
            proximityValue.put("proximity", this.paramSensors.getProximity());

            //Accelerometer
            HashMap<String, Float> accelerometerValues = new HashMap<>();
            accelerometerValues.put("X", this.paramSensors.getAccelX());
            accelerometerValues.put("Y", this.paramSensors.getAccelY());
            accelerometerValues.put("Z", this.paramSensors.getAccelZ());

            //Magnetometer
            HashMap<String, Float> magnetometerValues = new HashMap<>();
            magnetometerValues.put("X", this.paramSensors.getMagnetoX());
            magnetometerValues.put("Y", this.paramSensors.getMagnetoY());
            magnetometerValues.put("Z", this.paramSensors.getMagnetoZ());

            //Gyrometer
            HashMap<String, Float> gyrometerValues = new HashMap<>();
            gyrometerValues.put("X", this.paramSensors.getGyroX());
            gyrometerValues.put("Y", this.paramSensors.getGyroY());
            gyrometerValues.put("Z", this.paramSensors.getGyroZ());

            //Add to the sensor info
            switch (compteurRegister / 3) {
                case 1 :
                    sensorInfos.put("S"+Sensor.TYPE_LIGHT,
                            new SensorData(false, lightValue, null, null));
                    sensorInfos.put("S"+Sensor.TYPE_PROXIMITY,
                            new SensorData(false, proximityValue, null, null));
                    sensorInfos.put("S"+Sensor.TYPE_ACCELEROMETER,
                            new SensorData(false, accelerometerValues, null, null));
                    sensorInfos.put("S"+Sensor.TYPE_GYROSCOPE,
                            new SensorData(false, gyrometerValues, null, null));
                    sensorInfos.put("S"+Sensor.TYPE_MAGNETIC_FIELD,
                            new SensorData(false, magnetometerValues, null, null));
                    break;
                case 2 :
                    Objects.requireNonNull(sensorInfos.get("S"+Sensor.TYPE_LIGHT))
                            .setValue2(lightValue);
                    Objects.requireNonNull(sensorInfos.get("S"+Sensor.TYPE_PROXIMITY))
                            .setValue2(proximityValue);
                    Objects.requireNonNull(sensorInfos.get("S"+Sensor.TYPE_ACCELEROMETER))
                            .setValue2(accelerometerValues);
                    Objects.requireNonNull(sensorInfos.get("S"+Sensor.TYPE_GYROSCOPE))
                            .setValue2(gyrometerValues);
                    Objects.requireNonNull(sensorInfos.get("S"+Sensor.TYPE_MAGNETIC_FIELD))
                            .setValue2(magnetometerValues);
                    break;
                case 3 :
                    Objects.requireNonNull(sensorInfos.get("S"+Sensor.TYPE_LIGHT))
                            .setValue3(lightValue);
                    Objects.requireNonNull(sensorInfos.get("S"+Sensor.TYPE_PROXIMITY))
                            .setValue3(proximityValue);
                    Objects.requireNonNull(sensorInfos.get("S"+Sensor.TYPE_ACCELEROMETER))
                            .setValue3(accelerometerValues);
                    Objects.requireNonNull(sensorInfos.get("S"+Sensor.TYPE_GYROSCOPE))
                            .setValue3(gyrometerValues);
                    Objects.requireNonNull(sensorInfos.get("S"+Sensor.TYPE_MAGNETIC_FIELD))
                            .setValue3(magnetometerValues);

                    //Send notification
                    Date dateValue = new Date(System.currentTimeMillis());
                    @SuppressLint("SimpleDateFormat")
                    DateFormat formatter = new SimpleDateFormat("HH:mm");
                    String time = formatter.format(dateValue);
                    AccountActivity.this.notify(time);

                    //Get location only if it's up to date
                    Log.d("ALED", "Latest : lat " + latestLocInfos.getLatitude() +
                                                  "   long " + latestLocInfos.getLongitude());
                    Log.d("ALED", "Now : lat " + MainActivity.PARAM_LOCATION.getLatitude() +
                                               "   long " + MainActivity.PARAM_LOCATION.getLongitude());
                    if (latestLocInfos.getLatitude().equals(MainActivity.PARAM_LOCATION.getLatitude())
                    && latestLocInfos.getLongitude().equals(MainActivity.PARAM_LOCATION.getLongitude())){
                        Log.d("ALED", "C LA MEME");
                        locationInfos.setLongitude(0d);
                        locationInfos.setLatitude(0d);
                    }
                    else {
                        Log.d("ALED", "C PAS LA MEME");
                        locationInfos.setLongitude(MainActivity.PARAM_LOCATION.getLongitude());
                        locationInfos.setLatitude( MainActivity.PARAM_LOCATION.getLatitude());
                    }

                    //Reach historic enable
                    this.btn_history.setEnabled(true);
                    break;
                default:
                    break;
            }
        }

    }


    private ActivityCheck createActivityCheck() {
        //Get the current timestamp
        long timestamp = System.currentTimeMillis();

        //Find the activity thanks to sensors data
        String excpectedActivity = findActivity(sensorInfos, locationInfos, timestamp);

        ActivityCheck act = new ActivityCheck(timestamp, locationInfos, new HashMap<>(sensorInfos),
                "Aucune", excpectedActivity);

        sensorInfos.clear();
        return act;
    }


    @SuppressWarnings("ConstantConditions") @SuppressLint("SimpleDateFormat")
    private String findActivity(HashMap<String, SensorData> sensorInfos, Location location,
                                long timestamp) {
        //Reset HashMap values
        intialisationOfActivityStats();

        //Find the day number (sunday to monday)
        Date date = new Date(timestamp);
        DateFormat formatter = new SimpleDateFormat("EEEE");
        String day = formatter.format(date);

        //Find the hour
        DateFormat formatter2 = new SimpleDateFormat("HH");
        String hour = formatter2.format(date);

        //Change the date for correspond with database values
        switch (day) {
            case "lundi" : day = "Monday";
                break;
            case "mardi" : day = "Tuesday";
                break;
            case "mercredi" : day = "Wednesday";
                break;
            case "jeudi" : day = "Thursday";
                break;
            case "vendredi" : day = "Friday";
                break;
            case "samedi" : day = "Saturday";
                break;
            case "dimanche" : day = "Sunday";
                break;
            default:
                break;
        }


        //Create the list of activities depending on the day and hour
        HashMap<String, Integer> activities;

        int hourValue = Integer.parseInt(hour);
        hourValue = (hourValue/2)*2;

        //If it's the week-end or the middle of the week
        if (day.equals("Sunday") || day.equals("Saturday")) {
            activities = weekEndActivitiesStat.get(hourValue);
        }
        else {
            activities = weekActivitiesStat.get(hourValue);
        }

        Log.d("ALED", activities.toString());

        //Use location
        if (!(location.getLatitude() == 0d && location.getLongitude() == 0d)) {
            if (this.user.getHome().equals(location)) {
                //Activities at home
                HashMap<String, Integer> currentValues = new HashMap<>();
                currentValues.put("Dormir", activities.get("Dormir")+20);
                activities.remove("Dormir");
                currentValues.put("Manger", activities.get("Manger")+20);
                activities.remove("Manger");
                currentValues.put("Regarder la télévision", activities.get("Regarder la télévision")+20);
                activities.remove("Regarder la télévision");
                currentValues.put("Jouer à des jeux", activities.get("Jouer à des jeux")+20);
                activities.remove("Jouer à des jeux");
                activities.putAll(currentValues);
            }
            else {
                //If he is outside, he may not sleep
                int valueSleep = activities.get("Dormir");
                activities.remove("Dormir");
                activities.put("Dormir", valueSleep - 30);

                //Activities he could do at work
                if (this.user.getWork() != null && this.user.getWork().equals(location)) {
                    int valueWork = activities.get("Travailler");
                    activities.remove("Travailler");
                    activities.put("Travailler", valueWork + 40);

                    int valueEat = activities.get("Manger");
                    activities.remove("Manger");
                    activities.put("Manger", valueEat + 10);
                }

                //If he is at a shop place
                else if (this.user.getLocationsShop() != null) {
                    for (Location loc : this.user.getLocationsShop()) {
                        if (loc.equals(location)) {
                            int valueShop = activities.get("Magasiner");
                            activities.remove("Magasiner");
                            activities.put("Magasiner", valueShop + 40);

                            int valueEat = activities.get("Manger");
                            activities.remove("Manger");
                            activities.put("Manger", valueEat + 10);
                        }
                    }
                }

                //If he is at a sport place
                else if (this.user.getLocationsSport() != null) {
                    for (Location loc : this.user.getLocationsSport()) {
                        if (loc.equals(location)) {
                            int valueSport = activities.get("Faire du sport");
                            activities.remove("Faire du sport");
                            activities.put("Faire du sport", valueSport + 40);
                        }
                    }
                }

                //If he is outside, in an unknown place
                else {
                    int valueShop = activities.get("Magasiner");
                    activities.remove("Magasiner");
                    activities.put("Magasiner", valueShop + 15);

                    int valueSport = activities.get("Faire du sport");
                    activities.remove("Faire du sport");
                    activities.put("Faire du sport", valueSport + 15);

                    int valueCommut = activities.get("Prendre les transports");
                    activities.remove("Prendre les transports");
                    activities.put("Prendre les transports", valueCommut + 20);
                }
            }
        }


        //Use sensors
        float averageLuminosity =
                (sensorInfos.get("S5").getValue1().get("luminosity")
                        + sensorInfos.get("S5").getValue2().get("luminosity")
                        + sensorInfos.get("S5").getValue3().get("luminosity")) /3;

        float averageProximity =
                (sensorInfos.get("S8").getValue1().get("proximity")
                        + sensorInfos.get("S8").getValue2().get("proximity")
                        + sensorInfos.get("S8").getValue3().get("proximity")) / 3;

        float accXValue1 = sensorInfos.get("S1").getValue1().get("X");
        float accXValue2 = sensorInfos.get("S1").getValue2().get("X");
        float accXValue3 = sensorInfos.get("S1").getValue3().get("X");

        float accYValue1 = sensorInfos.get("S1").getValue1().get("Y");
        float accYValue2 = sensorInfos.get("S1").getValue2().get("Y");
        float accYValue3 = sensorInfos.get("S1").getValue3().get("Y");

        float accZValue1 = sensorInfos.get("S1").getValue1().get("Z");
        float accZValue2 = sensorInfos.get("S1").getValue2().get("Z");
        float accZValue3 = sensorInfos.get("S1").getValue3().get("Z");

        float averageAccelerometer = (accZValue1 + accZValue2 + accZValue3) / 3;

        Log.d("ALED", "Proximity : " + averageProximity);
        Log.d("ALED", "Luminosity : " + averageLuminosity);
        Log.d("ALED", "Acc : " + averageAccelerometer);

        //&& averageLuminosity > 0f can't relies on because smartphone change a lot

        if(averageAccelerometer <= 5f && averageAccelerometer > -3.0f && averageProximity == 0f){
            int nbr = activities.get("Téléphoner");
            activities.remove("Téléphoner");
            activities.put("Téléphoner", nbr + 60);
            sensorInfos.get("S1").setUsed(true);
            sensorInfos.get("S8").setUsed(true);
        }

        if (averageLuminosity < 10f && averageProximity == 0f) {
            int nbr = activities.get("Dormir");
            activities.remove("Dormir");
            activities.put("Dormir",nbr + 15);
            sensorInfos.get("S5").setUsed(true);
            sensorInfos.get("S8").setUsed(true);
        }

        if(averageLuminosity > 1000f) {
            int nbr = activities.get("Faire du sport");
            activities.remove("Faire du sport");
            activities.put("Faire du sport", nbr + 5);
            sensorInfos.get("S5").setUsed(true);
        }

        float test = Math.max(Math.max(accXValue1, accXValue2), accXValue3);
        float test2 = Math.min(Math.min(accXValue1, accXValue2), accXValue3);

        Log.d("ALED", "max : " + test + ", min : " + test2);

        float valueX = Math.abs(test - test2);
        float valueY = Math.abs(Math.max(Math.max(accYValue1, accYValue2), accYValue3)
                        - Math.min(Math.min(accYValue1, accYValue2), accYValue3));
        float valueZ = Math.abs(Math.max(Math.max(accZValue1, accZValue2), accZValue3)
                        - Math.min(Math.min(accZValue1, accZValue2), accZValue3));

        Log.d("ALED", "ValueX : " + valueX + ", ValueY : " + valueY + ", ValueZ : " + valueZ);

        if(valueX > 2f && valueY > 2f && valueZ > 2f)
        {
            int nbr = activities.get("Faire du sport");
            activities.remove("Faire du sport");
            activities.put("Faire du sport", nbr + 10);
            sensorInfos.get("S1").setUsed(true);
        }


        //Use users' habits
        if ((this.user.getBasicWeek() != null) && (this.user.getBasicWeek().containsKey(day))  &&
            (Objects.requireNonNull(this.user.getBasicWeek().get(day)).containsKey(hour))  )
        {
            HashMap<String, Integer> habitsActivities = Objects.requireNonNull(Objects.requireNonNull(
                    this.user.getBasicWeek().get(day)).get(hour)).getActivities();

            int sum = 0;
            for (Integer nbrOccurence: habitsActivities.values()) {
                sum += nbrOccurence;
            }

            //If we don't have enought data, we can't relie on this
            if (sum >= 5) {
                assert activities != null;
                for (String act: habitsActivities.keySet()) {
                    int occActivity = habitsActivities.get(act);
                    int nbrActivity = activities.get(act);
                    activities.remove(act);
                    activities.put(act, nbrActivity + (occActivity*50/sum));
                }
            }
        }

        assert activities != null;
        for (String act : activities.keySet()) {
            Log.d("ALED", act + " : " + activities.get(act));
        }

        //Get the activity with maximum value
        String expectedActivity = "Travailler";
        int nbMax = activities.get(expectedActivity);
        for (String act: activities.keySet()) {
            if (nbMax < activities.get(act)) {
                expectedActivity = act;
                nbMax = activities.get(act);
            }
        }

        //Reset initialisation
        intialisationOfActivityStats();

        return expectedActivity;
    }


    @Override
    protected void onResume() {
        super.onResume();

        //Find the user
        referenceData.child(this.pseudo).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    setUser(dataSnapshot.getValue(User.class));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /*if(!handlerLance){
            handler7h.post(schedule7h);
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();

        Log.d("ALED", "Latest CHANGEEEEED : lat " + latestLocInfos.getLatitude() +
                "   long " + latestLocInfos.getLongitude());

        //If the user left, store last location
        latestLocInfos.setLatitude(MainActivity.PARAM_LOCATION.getLatitude());
        latestLocInfos.setLongitude(MainActivity.PARAM_LOCATION.getLongitude());
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_LIGHT :
                this.paramSensors.setLight(event.values[0]);
                break;
            case Sensor.TYPE_PROXIMITY :
                this.paramSensors.setProximity(event.values[0]);
                break;
            case Sensor.TYPE_ACCELEROMETER :
                this.paramSensors.setAccelX(event.values[0]);
                this.paramSensors.setAccelY(event.values[1]);
                this.paramSensors.setAccelZ(event.values[2]);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD :
                this.paramSensors.setMagnetoX(event.values[0]);
                this.paramSensors.setMagnetoY(event.values[1]);
                this.paramSensors.setMagnetoZ(event.values[2]);
                break;
            case Sensor.TYPE_GYROSCOPE :
                this.paramSensors.setGyroX(event.values[0]);
                this.paramSensors.setGyroY(event.values[1]);
                this.paramSensors.setGyroZ(event.values[2]);
                break;
            default :
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void startRunnable(View view) {
        this.handler7h.post(this.schedule7h);
        this.textDataCheck.setText(R.string.analysis);
        this.launchCheck.setEnabled(false);
        this.launchCheck.setVisibility(View.INVISIBLE);
        this.timerCheck.setEnabled(true);
        this.timerCheck.setVisibility(View.VISIBLE);
    }

    public class RepetAction {
        Timer t;

        public RepetAction(int time) {
            t = new Timer();
            t.schedule(new MyAction(time), 0, 1000);
        }

        class MyAction extends TimerTask {
            int timeLeft;

            public MyAction(int time) {
                timeLeft = time;
            }

            public void run() {
                if (timeLeft > 0) {
                    String hour = "";
                    if (timeLeft /3600 < 10) {
                        hour = "0";
                    }
                    hour = hour + timeLeft / 3600;

                    String min = "";
                    if ((timeLeft %3600)/60 < 10) {
                        min = "0";
                    }
                    min = min + (timeLeft % 3600) / 60;

                    String sec = "";
                    if ((timeLeft %3600)%60 < 10) {
                        sec = "0";
                    }
                    sec = sec + (timeLeft % 3600) % 60;

                    final String time = hour + " : " + min + " : " + sec;

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            timerCheck.setText(time);
                        }
                    });
                    timeLeft--;
                } else {
                    t.cancel();
                }
            }
        }
    }




    private void intialisationOfActivityStats() {
        //Creation of the classique stats of activities by day

        //Week
        this.weekActivitiesStat = new HashMap<>();

        //__________________________________ 00h -> 02h ____________________________________________
        HashMap<String, Integer> zero = new HashMap<>();
        zero.put("Travailler",5);         zero.put("Manger",5);
        zero.put("Jouer à des jeux",15);   zero.put("Prendre les transports",5);
        zero.put("Téléphoner",5);         zero.put("Regarder la télévision",15);
        zero.put("Magasiner",5);          zero.put("Faire du sport",5);
        zero.put("Dormir",60);

        //__________________________________ 02h -> 04h ____________________________________________
        HashMap<String, Integer> two = new HashMap<>();
        two.put("Travailler",5);         two.put("Manger",5);
        two.put("Jouer à des jeux",5);   two.put("Prendre les transports",5);
        two.put("Téléphoner",5);         two.put("Regarder la télévision",5);
        two.put("Magasiner",5);          two.put("Faire du sport",5);
        two.put("Dormir",80);

        //__________________________________ 04h -> 06h ____________________________________________
        HashMap<String, Integer> four = new HashMap<>();
        four.put("Travailler",5);         four.put("Manger",5);
        four.put("Jouer à des jeux",5);   four.put("Prendre les transports",5);
        four.put("Téléphoner",5);         four.put("Regarder la télévision",5);
        four.put("Magasiner",5);          four.put("Faire du sport",5);
        four.put("Dormir",80);

        //__________________________________ 06h -> 08h ____________________________________________
        HashMap<String, Integer> size = new HashMap<>();
        size.put("Travailler",15);         size.put("Manger",80);
        size.put("Jouer à des jeux",10);   size.put("Prendre les transports",40);
        size.put("Téléphoner",10);         size.put("Regarder la télévision",10);
        size.put("Magasiner",10);          size.put("Faire du sport",10);
        size.put("Dormir",20);

        //__________________________________ 08h -> 10h ____________________________________________
        HashMap<String, Integer> eight = new HashMap<>();
        eight.put("Travailler",40);         eight.put("Manger",30);
        eight.put("Jouer à des jeux",10);   eight.put("Prendre les transports",40);
        eight.put("Téléphoner",10);         eight.put("Regarder la télévision",10);
        eight.put("Magasiner",10);          eight.put("Faire du sport",10);
        eight.put("Dormir",5);

        //__________________________________ 10h -> 12h ____________________________________________
        HashMap<String, Integer> ten = new HashMap<>();
        ten.put("Travailler",80);         ten.put("Manger",15);
        ten.put("Jouer à des jeux",5);   ten.put("Prendre les transports",15);
        ten.put("Téléphoner",10);         ten.put("Regarder la télévision",5);
        ten.put("Magasiner",10);          ten.put("Faire du sport",5);
        ten.put("Dormir",5);

        //__________________________________ 12h -> 14h ____________________________________________
        HashMap<String, Integer> twelwe = new HashMap<>();
        twelwe.put("Travailler",40);         twelwe.put("Manger",80);
        twelwe.put("Jouer à des jeux",5);   twelwe.put("Prendre les transports",15);
        twelwe.put("Téléphoner",10);         twelwe.put("Regarder la télévision",5);
        twelwe.put("Magasiner",10);          twelwe.put("Faire du sport",5);
        twelwe.put("Dormir",5);

        //__________________________________ 14h -> 16h ____________________________________________
        HashMap<String, Integer> fourteen = new HashMap<>();
        fourteen.put("Travailler",80);         fourteen.put("Manger",15);
        fourteen.put("Jouer à des jeux",5);   fourteen.put("Prendre les transports",15);
        fourteen.put("Téléphoner",10);         fourteen.put("Regarder la télévision",5);
        fourteen.put("Magasiner",10);          fourteen.put("Faire du sport",10);
        fourteen.put("Dormir",5);

        //__________________________________ 16h -> 18h ____________________________________________
        HashMap<String, Integer> sizeteen = new HashMap<>();
        sizeteen.put("Travailler",40);         sizeteen.put("Manger",5);
        sizeteen.put("Jouer à des jeux",5);   sizeteen.put("Prendre les transports",40);
        sizeteen.put("Téléphoner",10);         sizeteen.put("Regarder la télévision",5);
        sizeteen.put("Magasiner",20);          sizeteen.put("Faire du sport",30);
        sizeteen.put("Dormir",5);

        //__________________________________ 18h -> 20h ____________________________________________
        HashMap<String, Integer> eighteen = new HashMap<>();
        eighteen.put("Travailler",15);         eighteen.put("Manger",60);
        eighteen.put("Jouer à des jeux",40);   eighteen.put("Prendre les transports",40);
        eighteen.put("Téléphoner",10);         eighteen.put("Regarder la télévision",60);
        eighteen.put("Magasiner",20);          eighteen.put("Faire du sport",40);
        eighteen.put("Dormir",5);

        //__________________________________ 20h -> 22h ____________________________________________
        HashMap<String, Integer> twenty = new HashMap<>();
        twenty.put("Travailler",5);         twenty.put("Manger",60);
        twenty.put("Jouer à des jeux",40);   twenty.put("Prendre les transports",5);
        twenty.put("Téléphoner",10);         twenty.put("Regarder la télévision",60);
        twenty.put("Magasiner",10);          twenty.put("Faire du sport",30);
        twenty.put("Dormir",5);

        //__________________________________ 22h -> 00h ____________________________________________
        HashMap<String, Integer> twentytwo = new HashMap<>();
        twentytwo.put("Travailler",5);         twentytwo.put("Manger",15);
        twentytwo.put("Jouer à des jeux",20);   twentytwo.put("Prendre les transports",5);
        twentytwo.put("Téléphoner",5);         twentytwo.put("Regarder la télévision",30);
        twentytwo.put("Magasiner",5);          twentytwo.put("Faire du sport",5);
        twentytwo.put("Dormir",20);


        weekActivitiesStat.put(0, zero);weekActivitiesStat.put(12, twelwe);
        weekActivitiesStat.put(2, two);weekActivitiesStat.put(14, fourteen);
        weekActivitiesStat.put(4, four);weekActivitiesStat.put(16, sizeteen);
        weekActivitiesStat.put(6, size);weekActivitiesStat.put(18, eighteen);
        weekActivitiesStat.put(8, eight);weekActivitiesStat.put(20, twenty);
        weekActivitiesStat.put(10, ten);weekActivitiesStat.put(22, twentytwo);


        //Weekend
        this.weekEndActivitiesStat = new HashMap<>();

        //__________________________________ 00h -> 02h ____________________________________________
        HashMap<String, Integer> zeroWE = new HashMap<>();
        zeroWE.put("Travailler",5);         zeroWE.put("Manger",5);
        zeroWE.put("Jouer à des jeux",15);   zeroWE.put("Prendre les transports",5);
        zeroWE.put("Téléphoner",5);         zeroWE.put("Regarder la télévision",15);
        zeroWE.put("Magasiner",5);          zeroWE.put("Faire du sport",5);
        zeroWE.put("Dormir",60);

        //__________________________________ 02h -> 04h ____________________________________________
        HashMap<String, Integer> twoWE = new HashMap<>();
        twoWE.put("Travailler",5);         twoWE.put("Manger",5);
        twoWE.put("Jouer à des jeux",5);   twoWE.put("Prendre les transports",5);
        twoWE.put("Téléphoner",5);         twoWE.put("Regarder la télévision",5);
        twoWE.put("Magasiner",5);          twoWE.put("Faire du sport",5);
        twoWE.put("Dormir",80);

        //__________________________________ 04h -> 06h ____________________________________________
        HashMap<String, Integer> fourWE = new HashMap<>();
        fourWE.put("Travailler",5);         fourWE.put("Manger",5);
        fourWE.put("Jouer à des jeux",5);   fourWE.put("Prendre les transports",5);
        fourWE.put("Téléphoner",5);         fourWE.put("Regarder la télévision",5);
        fourWE.put("Magasiner",5);          fourWE.put("Faire du sport",5);
        fourWE.put("Dormir",80);

        //__________________________________ 06h -> 08h ____________________________________________
        HashMap<String, Integer> sizeWE = new HashMap<>();
        sizeWE.put("Travailler",5);         sizeWE.put("Manger",40);
        sizeWE.put("Jouer à des jeux",15);   sizeWE.put("Prendre les transports",15);
        sizeWE.put("Téléphoner",10);         sizeWE.put("Regarder la télévision",15);
        sizeWE.put("Magasiner",20);          sizeWE.put("Faire du sport",10);
        sizeWE.put("Dormir",50);

        //__________________________________ 08h -> 10h ____________________________________________
        HashMap<String, Integer> eightWE = new HashMap<>();
        eightWE.put("Travailler",10);         eightWE.put("Manger",70);
        eightWE.put("Jouer à des jeux",15);   eightWE.put("Prendre les transports",15);
        eightWE.put("Téléphoner",10);         eightWE.put("Regarder la télévision",15);
        eightWE.put("Magasiner",30);          eightWE.put("Faire du sport",10);
        eightWE.put("Dormir",20);

        //__________________________________ 10h -> 12h ____________________________________________
        HashMap<String, Integer> tenWE = new HashMap<>();
        tenWE.put("Travailler",10);         tenWE.put("Manger",30);
        tenWE.put("Jouer à des jeux",15);   tenWE.put("Prendre les transports",15);
        tenWE.put("Téléphoner",10);         tenWE.put("Regarder la télévision",15);
        tenWE.put("Magasiner",40);          tenWE.put("Faire du sport",20);
        tenWE.put("Dormir",10);

        //__________________________________ 12h -> 14h ____________________________________________
        HashMap<String, Integer> twelweWE = new HashMap<>();
        twelweWE.put("Travailler",5);         twelweWE.put("Manger",80);
        twelweWE.put("Jouer à des jeux",15);   twelweWE.put("Prendre les transports",15);
        twelweWE.put("Téléphoner",10);         twelweWE.put("Regarder la télévision",15);
        twelweWE.put("Magasiner",20);          twelweWE.put("Faire du sport",20);
        twelweWE.put("Dormir",5);

        //__________________________________ 14h -> 16h ____________________________________________
        HashMap<String, Integer> fourteenWE = new HashMap<>();
        fourteenWE.put("Travailler",10);         fourteenWE.put("Manger",15);
        fourteenWE.put("Jouer à des jeux",15);   fourteenWE.put("Prendre les transports",15);
        fourteenWE.put("Téléphoner",10);         fourteenWE.put("Regarder la télévision",15);
        fourteenWE.put("Magasiner",20);          fourteenWE.put("Faire du sport",30);
        fourteenWE.put("Dormir",5);

        //__________________________________ 16h -> 18h ____________________________________________
        HashMap<String, Integer> sizeteenWE = new HashMap<>();
        sizeteenWE.put("Travailler",10);         sizeteenWE.put("Manger",5);
        sizeteenWE.put("Jouer à des jeux",15);   sizeteenWE.put("Prendre les transports",15);
        sizeteenWE.put("Téléphoner",10);         sizeteenWE.put("Regarder la télévision",15);
        sizeteenWE.put("Magasiner",20);          sizeteenWE.put("Faire du sport",30);
        sizeteenWE.put("Dormir",5);

        //__________________________________ 18h -> 20h ____________________________________________
        HashMap<String, Integer> eighteenWE = new HashMap<>();
        eighteenWE.put("Travailler",5);         eighteenWE.put("Manger",60);
        eighteenWE.put("Jouer à des jeux",40);   eighteenWE.put("Prendre les transports",15);
        eighteenWE.put("Téléphoner",10);         eighteenWE.put("Regarder la télévision",60);
        eighteenWE.put("Magasiner",20);          eighteenWE.put("Faire du sport",40);
        eighteenWE.put("Dormir",5);

        //__________________________________ 20h -> 22h ____________________________________________
        HashMap<String, Integer> twentyWE = new HashMap<>();
        twentyWE.put("Travailler",5);         twentyWE.put("Manger",60);
        twentyWE.put("Jouer à des jeux",40);   twentyWE.put("Prendre les transports",15);
        twentyWE.put("Téléphoner",10);         twentyWE.put("Regarder la télévision",60);
        twentyWE.put("Magasiner",10);          twentyWE.put("Faire du sport",30);
        twentyWE.put("Dormir",5);

        //__________________________________ 22h -> 00h ____________________________________________
        HashMap<String, Integer> twentytwoWE = new HashMap<>();
        twentytwoWE.put("Travailler",5);         twentytwoWE.put("Manger",15);
        twentytwoWE.put("Jouer à des jeux",20);   twentytwoWE.put("Prendre les transports",5);
        twentytwoWE.put("Téléphoner",5);         twentytwoWE.put("Regarder la télévision",30);
        twentytwoWE.put("Magasiner",5);          twentytwoWE.put("Faire du sport",5);
        twentytwoWE.put("Dormir",20);


        weekEndActivitiesStat.put(0, zeroWE);weekEndActivitiesStat.put(12, twelweWE);
        weekEndActivitiesStat.put(2, twoWE);weekEndActivitiesStat.put(14, fourteenWE);
        weekEndActivitiesStat.put(4, fourWE);weekEndActivitiesStat.put(16, sizeteenWE);
        weekEndActivitiesStat.put(6, sizeWE);weekEndActivitiesStat.put(18, eighteenWE);
        weekEndActivitiesStat.put(8, eightWE);weekEndActivitiesStat.put(20, twentyWE);
        weekEndActivitiesStat.put(10, tenWE);weekEndActivitiesStat.put(22, twentytwoWE);
    }

    public static void setTimeParent(int timeParent) {
        TIME_PARENT = timeParent;
    }

    public static void setTimeChild(int timeChild) {
        TIME_CHILD = timeChild;
    }
}

