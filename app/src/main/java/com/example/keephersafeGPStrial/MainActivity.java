package com.example.keephersafeGPStrial;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;

import android.telephony.SmsManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    private EditText editText;
    TextView hearrateTV,avgHRV;
    private BluetoothAdapter mBluetoothAdapter;
    ArrayList<String> phoneNumbers;
    SmsManager smsManager;
    SharedPreferences myPref;
    private Button sendSOS;
    private Button viewProfile;
    Switch simpleSwitch;
    Double latitude = 0.0, longitude = 0.0;
    ArrayList<Double> hrvs;
    double hrv;
    int hrIndex = 0;
    double tot = 0, mean, stdDev, scaleOfElimination = 1.5;


    public void toast(String mess) {
        Toast.makeText(this, mess, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sendSOS = findViewById(R.id.SOSbutton);
        viewProfile = findViewById(R.id.profile);
        hearrateTV = findViewById(R.id.hearrateTV);
        avgHRV = findViewById(R.id.avgHRV);
        simpleSwitch = findViewById(R.id.simpleSwitch);

        hrvs = new ArrayList (Collections.nCopies(100, 0.0));



        sendSOS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendManualSOS();
            }
        });

//        viewProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent userIntent = getIntent();
//                String user = userIntent.getStringExtra("user");
//                Intent ProfilePage = new Intent(MainActivity.this,ProfileActivity.class);
//                ProfilePage.putExtra("user",user);
//                startActivity(ProfilePage);
//            }
//        });

        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled())
        {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,1);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Intent i=new Intent(this,  MyBluetoothService.class);
        startService(i);

        Intent serviceIntent  = new Intent(this,LocationService.class);
        ContextCompat.startForegroundService(this,serviceIntent);


        ServiceToActivity serviceReceiver = new ServiceToActivity();
        IntentFilter intentSFilter = new IntentFilter("ServiceToActivityAction");
        registerReceiver(serviceReceiver, intentSFilter);


        ServiceToActivity serviceReceiver1 = new ServiceToActivity();
        IntentFilter intentSFilter1 = new IntentFilter("LocServiceToActivityAction");
        registerReceiver(serviceReceiver1, intentSFilter1);

    }


    public void startBtService(View view) {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if(!bluetoothAdapter.isEnabled())
        {
            Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(intent,1);
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        Intent i=new Intent(this,  MyBluetoothService.class);
        startService(i);
    }

    public boolean isEmergency(){

        tot = 0;
        for(double d : hrvs){
            tot += d;
        }

        mean = tot / hrvs.size();


        double temp = 0;

        for (double a : hrvs) {
            temp += (a - mean) * (a - mean);
        }

        stdDev = Math.sqrt(temp / (hrvs.size() - 1));


        final List<Integer> newList = new ArrayList<>();


        boolean isLessThanLowerBound = hrv < (mean - stdDev * scaleOfElimination);
        boolean isGreaterThanUpperBound = hrv > (mean + stdDev * scaleOfElimination);
        boolean isOutOfBounds = isLessThanLowerBound || isGreaterThanUpperBound;

//        return !(hrv >=63 && hrv <= 87);
        return isOutOfBounds;
    }

    public synchronized void  startCoolDown() {

        EntityModel model = new EntityModel();
        model.prediction = 1;
        model.decision = 1;
        model.id = new Random().nextInt(1000);
        model.latitude = latitude;
        model.longitude = longitude;

        Log.d("LocationService","CoolDown Activated");
        Intent intent = new Intent(this,DangerTrigger.class);
        intent.putExtra("DataPoint",model);
        PendingIntent inte = PendingIntent.getActivity(this,1,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_02";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "My Channel_2",
                    NotificationManager.IMPORTANCE_HIGH);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentIntent(inte)
                    .setContentTitle("CoolDown Mode Activated ")
                    .setContentText("Abnormal HeartRate detected.. Please click here to terminate SOS")
                    .setSmallIcon(R.drawable.ic_baseline_warning_24)
                    .setAutoCancel(true)
                    .build();
            NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

            notificationManager.notify(2, notification);

//            startForeground(2, notification);

        }

    }


    public class ServiceToActivity extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent)
        {

            if(intent.getAction().equals("ServiceToActivityAction"))
            {
                Bundle notificationData = intent.getExtras();
                String newData  = notificationData.getString("ServiceToActivityKey");

                // newData is from the service

                String[]  data = newData.split(";");

                if(data[0].equals("datapoint")){
                    hrv = Double.parseDouble(data[1]);
                    hearrateTV.setText("Heart Rate Detected " + hrv);

                    if(isEmergency() && !simpleSwitch.isChecked()){
//                        sendManualSOS();
                        startCoolDown();
                    }

                    if(simpleSwitch.isChecked())
                    {
                        hrvs.set(hrIndex , Double.parseDouble(data[1]));
                        hrIndex = (hrIndex + 1) % hrvs.size();

                        avgHRV.setText(
                                hrvs.toString() + "\n\n" +
                                        "Average Heart Rate = " + mean  + " \nStdDev =" + stdDev + "\n"
                                        + "LowerBound = " + (mean - stdDev * scaleOfElimination) + "\n"
                                        + "UpperBound = " + (mean + stdDev * scaleOfElimination) + "\n"
                        );

                    }
                }

            }
            if(intent.getAction().equals("LocServiceToActivityAction"))
            {
                Bundle notificationData = intent.getExtras();
                String newData  = notificationData.getString("LocServiceToActivityAction");


                latitude = Double.parseDouble(newData.split(";")[0]);
                longitude = Double.parseDouble(newData.split(";")[1]);
//                toast(latitude + " " + longitude);

            }




        }
    }


    public void startService(View v){

        Intent serviceIntent  = new Intent(this,LocationService.class);
        ContextCompat.startForegroundService(this,serviceIntent);
    }
    public void stopService(View v) {
        Intent serviceIntent = new Intent(this, LocationService.class);
        stopService(serviceIntent);
    }

    @Override
    public int checkPermission(String permission, int pid, int uid) {
        return super.checkPermission(permission, pid, uid);
    }
    public void sendManualSOS() {
        myPref = getSharedPreferences("MySharedPref",MODE_PRIVATE);

        smsManager = SmsManager.getDefault();
        phoneNumbers = new ArrayList<>();
        phoneNumbers.add("9246465080");
        for(int i = 0;i < phoneNumbers.size();i++){
            String tosend = "HELP NEEDED!  "+ hrv+ "\n http://www.google.com/maps/place/" +  latitude + "," + longitude;
            toast(tosend);

//            myPref.getString("Latitude","0.00")
//            smsManager.sendTextMessage(phoneNumbers.get(i),null,"HELP NEEDED!  "+ hrv+ "\n http://www.google.com/maps/place/" + latitude + "," + longitude,null,null);
            startCoolDown();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

//        Intent i=new Intent(this,  MyBluetoothService.class);
//        stopService(i);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // R.menu.actionbar is a reference to an xml file named actionbar.xml which should be inside your res/menu directory. 
        // If you don't have res/menu, just create a directory named "menu" inside res
        getMenuInflater().inflate(R.menu.actionbar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // handle button activities
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        Intent userIntent = getIntent();
        String user = userIntent.getStringExtra("user");
        Intent ProfilePage = new Intent(MainActivity.this,ProfileActivity.class);
        ProfilePage.putExtra("user",user);
        startActivity(ProfilePage);
        return super.onOptionsItemSelected(item);
    }
}