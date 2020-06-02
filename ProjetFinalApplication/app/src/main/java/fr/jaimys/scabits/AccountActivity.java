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
import java.util.concurrent.ThreadLocalRandom;



public class AccountActivity extends AppCompatActivity implements SensorEventListener {

    //_________________________________________fields_______________________________________________
    //Notification fields
    private static final String CANAL = "MyCanal";

    //Activity search fields
    private HashMap<Integer, HashMap<String, Integer>> weekActivitiesStat;
    private HashMap<Integer, HashMap<String, Integer>> weekEndActivitiesStat;

    //Global fields
    private String pseudo;

    //
    private FirebaseDatabase database = FirebaseDatabase.getInstance();
    private DatabaseReference referenceData = database.getReference();
    private User user = null;
    private Button launchCheck;

    //Sensors fields
    private ParamSensors paramSensors;
    private SensorManager sensorManager;
    private Sensor sensorLight, sensorProximity, sensorAccel;
    private HashMap<String, SensorData>  sensorInfos = new HashMap<>();
    private Location locationInfos = new Location(1d,1d);

    //Runnable, handler and timer
    private TextView timerCheck;
    private Handler handler10s = new Handler();
    private Handler handler7h = new Handler();
    private int compteurRegister;
    private boolean handlerLance;
    private boolean register;

    private Runnable schedule7h = new Runnable() {
        @Override
        public void run() {
            Log.d("ALED", "Launched");
            int time = 120;
            new RepetAction(time);
            compteurRegister = 1;
            register = false;
            handler10s.post(schedule10s);

            handler7h.postDelayed(schedule7h, time * 1000 - SystemClock.elapsedRealtime()%1000);
        }
    };

    private Runnable schedule10s = new Runnable() {
        @Override
        public void run() {
            if(compteurRegister <= 10) {
                if(!register){
                    Log.d("ALED", "Register");
                    //Get location
                    locationInfos.setLongitude(MainActivity.PARAM_LOCATION.getLongitude());
                    locationInfos.setLatitude( MainActivity.PARAM_LOCATION.getLatitude());

                    //Register listeners
                    sensorManager.registerListener(AccountActivity.this, sensorLight,
                            SensorManager.SENSOR_DELAY_GAME);
                    sensorManager.registerListener(AccountActivity.this, sensorProximity,
                            SensorManager.SENSOR_DELAY_GAME);
                    sensorManager.registerListener(AccountActivity.this, sensorAccel,
                            SensorManager.SENSOR_DELAY_UI);
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

        this.handlerLance = false;

        //Instanciation of ParamSensors
        this.paramSensors = new ParamSensors();

        //Recuperation of sensors
        this.sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
        assert sensorManager != null;
        this.sensorLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        this.sensorProximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        this.sensorAccel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        //Setting of the buttons and textview up
        this.timerCheck = findViewById(R.id.timer_next_check);
        this.launchCheck = findViewById(R.id.add_check_button);

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

    public void notify(View view) {
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

            //Add to the sensor info
            switch (compteurRegister / 3) {
                case 1 :
                    sensorInfos.put(String.valueOf(Sensor.TYPE_LIGHT),
                            new SensorData(true, lightValue, null, null));
                    sensorInfos.put(String.valueOf(Sensor.TYPE_PROXIMITY),
                            new SensorData(true, proximityValue, null, null));
                    sensorInfos.put(String.valueOf(Sensor.TYPE_ACCELEROMETER),
                            new SensorData(true, accelerometerValues, null, null));
                    break;
                case 2 :
                    Objects.requireNonNull(sensorInfos.get(String.valueOf(Sensor.TYPE_LIGHT)))
                            .setValue2(lightValue);
                    Objects.requireNonNull(sensorInfos.get(String.valueOf(Sensor.TYPE_PROXIMITY)))
                            .setValue2(proximityValue);
                    Objects.requireNonNull(sensorInfos.get(String.valueOf(Sensor.TYPE_ACCELEROMETER)))
                            .setValue2(accelerometerValues);
                    break;
                case 3 :
                    Objects.requireNonNull(sensorInfos.get(String.valueOf(Sensor.TYPE_LIGHT)))
                            .setValue3(lightValue);
                    Objects.requireNonNull(sensorInfos.get(String.valueOf(Sensor.TYPE_PROXIMITY)))
                            .setValue3(proximityValue);
                    Objects.requireNonNull(sensorInfos.get(String.valueOf(Sensor.TYPE_ACCELEROMETER)))
                            .setValue3(accelerometerValues);

                    //Send notification
                    AccountActivity.this.notify(null);
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


    private String findActivity(HashMap<String, SensorData> sensorInfos, Location location,
                                long timestamp) {
        //Find the day number (sunday to monday)
        Date date = new Date(timestamp);
        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("EEEE");
        String day = formatter.format(date);

        //Find the hour
        @SuppressLint("SimpleDateFormat") DateFormat formatter2 = new SimpleDateFormat("HH");
        String hour = formatter2.format(date);


        //Create the list of activities depending on the day and hour
        HashMap<String, Integer> activities;

        if (day.equals("Sunday") || day.equals("Saturday") || day.equals("dimanche")
                || day.equals("samedi")) {
            activities = weekEndActivitiesStat.get(Integer.valueOf(hour));
        }
        else {
            activities = weekActivitiesStat.get(Integer.valueOf(hour));
        }

        //Use location
        if (this.user.getHome().equals(location)) {
            Log.d("ALED", "This is home");
        }
        else if (this.user.getWork() != null && this.user.getWork().equals(location)) {
            Log.d("ALED", "This is work");
        }
        else {
            Log.d("ALED", "This is else");
        }


        //Use sensors
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
                @SuppressWarnings("ConstantConditions")int nbr = activities.get("Dormir");
                activities.remove("Dormir");
                activities.put("Dormir",nbr + 50);
            }
            /*else if (averageLuminosity < 50) {
                //Activité intérieur ou peu lumineux dehors ?
            }*/

            //Activités exterieur ? ou le soir ?

        }



        //Use user habits
        if ((this.user.getBasicWeek() != null) && (this.user.getBasicWeek().containsKey(day))  &&
            (Objects.requireNonNull(this.user.getBasicWeek().get(day)).containsKey(hour))  )
        {
            HashMap<String, Integer> habitsActivities = Objects.requireNonNull(Objects.requireNonNull(
                    this.user.getBasicWeek().get(day)).get(hour)).getActivities();

            int sum = 0;
            for (Integer nbrOccurence: habitsActivities.values()) {
                sum += nbrOccurence;
            }

            assert activities != null;
            for (String act: habitsActivities.keySet()) {
                @SuppressWarnings("ConstantConditions") int occActivity = habitsActivities.get(act);
                activities.put("Jouer à des jeux", occActivity/sum);
            }
        }

        assert activities != null;
        Log.d("ALED", activities.toString());

        //------------------------------------------------------------------------------------------



        String activityReturn;
        int randomNum = ThreadLocalRandom.current().nextInt(1, 9 + 1);
        switch (randomNum) {
            case 1 :
                activityReturn = "Travailler";
                break;
            case 2 :
                activityReturn = "Jouer à des jeux";
                break;
            case 3 :
                activityReturn = "Prendre les transports";
                break;
            case 4 :
                activityReturn = "Manger";
                break;
            case 5 :
                activityReturn = "Dormir";
                break;
            case 6 :
                activityReturn = "Téléphoner";
                break;
            case 7 :
                activityReturn = "Faire du sport";
                break;
            case 8 :
                activityReturn = "Regarder la télévision";
                break;
            case 9 :
                activityReturn = "Magasiner";
                break;
            default:
                activityReturn = "Inconnu";
                break;
        }
        return activityReturn;
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
        this.handlerLance = true;
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
                break;
            default :
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void startRunnable(View view) {
        this.schedule7h.run();
        this.launchCheck.setEnabled(false);
        this.launchCheck.setVisibility(View.INVISIBLE);
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
        zero.put("Travailler",0);         zero.put("Manger",0);
        zero.put("Jouer à des jeux",0);   zero.put("Prendre les transports",0);
        zero.put("Téléphoner",0);         zero.put("Regarder la télévision",0);
        zero.put("Magasiner",0);          zero.put("Faire du sport",0);
        zero.put("Dormir",0);

        //__________________________________ 02h -> 04h ____________________________________________
        HashMap<String, Integer> two = new HashMap<>();
        two.put("Travailler",0);         two.put("Manger",0);
        two.put("Jouer à des jeux",0);   two.put("Prendre les transports",0);
        two.put("Téléphoner",0);         two.put("Regarder la télévision",0);
        two.put("Magasiner",0);          two.put("Faire du sport",0);
        two.put("Dormir",0);

        //__________________________________ 04h -> 06h ____________________________________________
        HashMap<String, Integer> four = new HashMap<>();
        four.put("Travailler",0);         four.put("Manger",0);
        four.put("Jouer à des jeux",0);   four.put("Prendre les transports",0);
        four.put("Téléphoner",0);         four.put("Regarder la télévision",0);
        four.put("Magasiner",0);          four.put("Faire du sport",0);
        four.put("Dormir",0);

        //__________________________________ 06h -> 08h ____________________________________________
        HashMap<String, Integer> size = new HashMap<>();
        size.put("Travailler",0);         size.put("Manger",0);
        size.put("Jouer à des jeux",0);   size.put("Prendre les transports",0);
        size.put("Téléphoner",0);         size.put("Regarder la télévision",0);
        size.put("Magasiner",0);          size.put("Faire du sport",0);
        size.put("Dormir",0);

        //__________________________________ 08h -> 10h ____________________________________________
        HashMap<String, Integer> eight = new HashMap<>();
        eight.put("Travailler",0);         eight.put("Manger",0);
        eight.put("Jouer à des jeux",0);   eight.put("Prendre les transports",0);
        eight.put("Téléphoner",0);         eight.put("Regarder la télévision",0);
        eight.put("Magasiner",0);          eight.put("Faire du sport",0);
        eight.put("Dormir",0);

        //__________________________________ 10h -> 12h ____________________________________________
        HashMap<String, Integer> ten = new HashMap<>();
        ten.put("Travailler",0);         ten.put("Manger",0);
        ten.put("Jouer à des jeux",0);   ten.put("Prendre les transports",0);
        ten.put("Téléphoner",0);         ten.put("Regarder la télévision",0);
        ten.put("Magasiner",0);          ten.put("Faire du sport",0);
        ten.put("Dormir",0);

        //__________________________________ 12h -> 14h ____________________________________________
        HashMap<String, Integer> twelwe = new HashMap<>();
        twelwe.put("Travailler",0);         twelwe.put("Manger",0);
        twelwe.put("Jouer à des jeux",0);   twelwe.put("Prendre les transports",0);
        twelwe.put("Téléphoner",0);         twelwe.put("Regarder la télévision",0);
        twelwe.put("Magasiner",0);          twelwe.put("Faire du sport",0);
        twelwe.put("Dormir",0);

        //__________________________________ 14h -> 16h ____________________________________________
        HashMap<String, Integer> fourteen = new HashMap<>();
        fourteen.put("Travailler",0);         fourteen.put("Manger",0);
        fourteen.put("Jouer à des jeux",0);   fourteen.put("Prendre les transports",0);
        fourteen.put("Téléphoner",0);         fourteen.put("Regarder la télévision",0);
        fourteen.put("Magasiner",0);          fourteen.put("Faire du sport",0);
        fourteen.put("Dormir",0);

        //__________________________________ 16h -> 18h ____________________________________________
        HashMap<String, Integer> sizeteen = new HashMap<>();
        sizeteen.put("Travailler",0);         sizeteen.put("Manger",0);
        sizeteen.put("Jouer à des jeux",0);   sizeteen.put("Prendre les transports",0);
        sizeteen.put("Téléphoner",0);         sizeteen.put("Regarder la télévision",0);
        sizeteen.put("Magasiner",0);          sizeteen.put("Faire du sport",0);
        sizeteen.put("Dormir",0);

        //__________________________________ 18h -> 20h ____________________________________________
        HashMap<String, Integer> eighteen = new HashMap<>();
        eighteen.put("Travailler",0);         eighteen.put("Manger",0);
        eighteen.put("Jouer à des jeux",0);   eighteen.put("Prendre les transports",0);
        eighteen.put("Téléphoner",0);         eighteen.put("Regarder la télévision",0);
        eighteen.put("Magasiner",0);          eighteen.put("Faire du sport",0);
        eighteen.put("Dormir",0);

        //__________________________________ 20h -> 22h ____________________________________________
        HashMap<String, Integer> twenty = new HashMap<>();
        twenty.put("Travailler",0);         twenty.put("Manger",0);
        twenty.put("Jouer à des jeux",0);   twenty.put("Prendre les transports",0);
        twenty.put("Téléphoner",0);         twenty.put("Regarder la télévision",0);
        twenty.put("Magasiner",0);          twenty.put("Faire du sport",0);
        twenty.put("Dormir",0);

        //__________________________________ 22h -> 00h ____________________________________________
        HashMap<String, Integer> twentytwo = new HashMap<>();
        twentytwo.put("Travailler",0);         twentytwo.put("Manger",0);
        twentytwo.put("Jouer à des jeux",0);   twentytwo.put("Prendre les transports",0);
        twentytwo.put("Téléphoner",0);         twentytwo.put("Regarder la télévision",0);
        twentytwo.put("Magasiner",0);          twentytwo.put("Faire du sport",0);
        twentytwo.put("Dormir",0);


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
        zeroWE.put("Travailler",0);         zeroWE.put("Manger",0);
        zeroWE.put("Jouer à des jeux",0);   zeroWE.put("Prendre les transports",0);
        zeroWE.put("Téléphoner",0);         zeroWE.put("Regarder la télévision",0);
        zeroWE.put("Magasiner",0);          zeroWE.put("Faire du sport",0);
        zeroWE.put("Dormir",0);

        //__________________________________ 02h -> 04h ____________________________________________
        HashMap<String, Integer> twoWE = new HashMap<>();
        twoWE.put("Travailler",0);         twoWE.put("Manger",0);
        twoWE.put("Jouer à des jeux",0);   twoWE.put("Prendre les transports",0);
        twoWE.put("Téléphoner",0);         twoWE.put("Regarder la télévision",0);
        twoWE.put("Magasiner",0);          twoWE.put("Faire du sport",0);
        twoWE.put("Dormir",0);

        //__________________________________ 04h -> 06h ____________________________________________
        HashMap<String, Integer> fourWE = new HashMap<>();
        fourWE.put("Travailler",0);         fourWE.put("Manger",0);
        fourWE.put("Jouer à des jeux",0);   fourWE.put("Prendre les transports",0);
        fourWE.put("Téléphoner",0);         fourWE.put("Regarder la télévision",0);
        fourWE.put("Magasiner",0);          fourWE.put("Faire du sport",0);
        fourWE.put("Dormir",0);

        //__________________________________ 06h -> 08h ____________________________________________
        HashMap<String, Integer> sizeWE = new HashMap<>();
        sizeWE.put("Travailler",0);         sizeWE.put("Manger",0);
        sizeWE.put("Jouer à des jeux",0);   sizeWE.put("Prendre les transports",0);
        sizeWE.put("Téléphoner",0);         sizeWE.put("Regarder la télévision",0);
        sizeWE.put("Magasiner",0);          sizeWE.put("Faire du sport",0);
        sizeWE.put("Dormir",0);

        //__________________________________ 08h -> 10h ____________________________________________
        HashMap<String, Integer> eightWE = new HashMap<>();
        eightWE.put("Travailler",0);         eightWE.put("Manger",0);
        eightWE.put("Jouer à des jeux",0);   eightWE.put("Prendre les transports",0);
        eightWE.put("Téléphoner",0);         eightWE.put("Regarder la télévision",0);
        eightWE.put("Magasiner",0);          eightWE.put("Faire du sport",0);
        eightWE.put("Dormir",0);

        //__________________________________ 10h -> 12h ____________________________________________
        HashMap<String, Integer> tenWE = new HashMap<>();
        tenWE.put("Travailler",0);         tenWE.put("Manger",0);
        tenWE.put("Jouer à des jeux",0);   tenWE.put("Prendre les transports",0);
        tenWE.put("Téléphoner",0);         tenWE.put("Regarder la télévision",0);
        tenWE.put("Magasiner",0);          tenWE.put("Faire du sport",0);
        tenWE.put("Dormir",0);

        //__________________________________ 12h -> 14h ____________________________________________
        HashMap<String, Integer> twelweWE = new HashMap<>();
        twelweWE.put("Travailler",0);         twelweWE.put("Manger",0);
        twelweWE.put("Jouer à des jeux",0);   twelweWE.put("Prendre les transports",0);
        twelweWE.put("Téléphoner",0);         twelweWE.put("Regarder la télévision",0);
        twelweWE.put("Magasiner",0);          twelweWE.put("Faire du sport",0);
        twelweWE.put("Dormir",0);

        //__________________________________ 14h -> 16h ____________________________________________
        HashMap<String, Integer> fourteenWE = new HashMap<>();
        fourteenWE.put("Travailler",0);         fourteenWE.put("Manger",0);
        fourteenWE.put("Jouer à des jeux",0);   fourteenWE.put("Prendre les transports",0);
        fourteenWE.put("Téléphoner",0);         fourteenWE.put("Regarder la télévision",0);
        fourteenWE.put("Magasiner",0);          fourteenWE.put("Faire du sport",0);
        fourteenWE.put("Dormir",0);

        //__________________________________ 16h -> 18h ____________________________________________
        HashMap<String, Integer> sizeteenWE = new HashMap<>();
        sizeteenWE.put("Travailler",0);         sizeteenWE.put("Manger",0);
        sizeteenWE.put("Jouer à des jeux",0);   sizeteenWE.put("Prendre les transports",0);
        sizeteenWE.put("Téléphoner",0);         sizeteenWE.put("Regarder la télévision",0);
        sizeteenWE.put("Magasiner",0);          sizeteenWE.put("Faire du sport",0);
        sizeteenWE.put("Dormir",0);

        //__________________________________ 18h -> 20h ____________________________________________
        HashMap<String, Integer> eighteenWE = new HashMap<>();
        eighteenWE.put("Travailler",0);         eighteenWE.put("Manger",0);
        eighteenWE.put("Jouer à des jeux",0);   eighteenWE.put("Prendre les transports",0);
        eighteenWE.put("Téléphoner",0);         eighteenWE.put("Regarder la télévision",0);
        eighteenWE.put("Magasiner",0);          eighteenWE.put("Faire du sport",0);
        eighteenWE.put("Dormir",0);

        //__________________________________ 20h -> 22h ____________________________________________
        HashMap<String, Integer> twentyWE = new HashMap<>();
        twentyWE.put("Travailler",0);         twentyWE.put("Manger",0);
        twentyWE.put("Jouer à des jeux",0);   twentyWE.put("Prendre les transports",0);
        twentyWE.put("Téléphoner",0);         twentyWE.put("Regarder la télévision",0);
        twentyWE.put("Magasiner",0);          twentyWE.put("Faire du sport",0);
        twentyWE.put("Dormir",0);

        //__________________________________ 22h -> 00h ____________________________________________
        HashMap<String, Integer> twentytwoWE = new HashMap<>();
        twentytwoWE.put("Travailler",0);         twentytwoWE.put("Manger",0);
        twentytwoWE.put("Jouer à des jeux",0);   twentytwoWE.put("Prendre les transports",0);
        twentytwoWE.put("Téléphoner",0);         twentytwoWE.put("Regarder la télévision",0);
        twentytwoWE.put("Magasiner",0);          twentytwoWE.put("Faire du sport",0);
        twentytwoWE.put("Dormir",0);


        weekEndActivitiesStat.put(0, zeroWE);weekEndActivitiesStat.put(12, twelweWE);
        weekEndActivitiesStat.put(2, twoWE);weekEndActivitiesStat.put(14, fourteenWE);
        weekEndActivitiesStat.put(4, fourWE);weekEndActivitiesStat.put(16, sizeteenWE);
        weekEndActivitiesStat.put(6, sizeWE);weekEndActivitiesStat.put(18, eighteenWE);
        weekEndActivitiesStat.put(8, eightWE);weekEndActivitiesStat.put(20, twentyWE);
        weekEndActivitiesStat.put(10, tenWE);weekEndActivitiesStat.put(22, twentytwoWE);
    }
}

