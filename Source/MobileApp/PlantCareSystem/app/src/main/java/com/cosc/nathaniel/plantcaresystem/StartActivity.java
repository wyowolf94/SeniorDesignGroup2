package com.cosc.nathaniel.plantcaresystem;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.util.Calendar;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.Date;

//this is the home screen of the app. from here the user can go to settings, add plant, or view plants
public class StartActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnSettings, btnView, btnAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        btnSettings = (Button) findViewById(R.id.btnSettings);
        btnView = (Button) findViewById(R.id.btnView);
        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnSettings.setOnClickListener(this);
        btnView.setOnClickListener(this);
        btnAdd.setOnClickListener(this);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) { //alarm scheduler requires api 24
            //schedule the specified number of alerts based on preset list of times
            int notifNum = ConnectionMethods.getNotifNum();
            int i = 0;
            if (i < notifNum){ scheduleAlarm(10, 0); }
            i++;
            if (i < notifNum){ scheduleAlarm(18, 0); }
            i++;
            if (i < notifNum){ scheduleAlarm(14, 0); }
            i++;
            if (i < notifNum){ scheduleAlarm(20, 0); }
            i++;
            if (i < notifNum){ scheduleAlarm(12, 0); }
            i++;
            if (i < notifNum){ scheduleAlarm(16, 0); }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.equals(btnSettings)){ //start settings activity
            try {
                Intent in = new Intent(v.getContext(), Settings.class);
                startActivity(in);
            } catch (Exception e) {
                Log.e("DEBUG", "StartActivity: Failed to launch settings activity.");
            }
        }

        else if (ConnectionMethods.isConnected()) { //check whether connection is successful
            if (v.equals(btnAdd)) { //start add plant activity
                try {
                    Intent in = new Intent(v.getContext(), AddPlant.class);
                    startActivity(in);
                } catch (Exception e) {
                    Log.e("DEBUG", "StartActivity: Failed to launch add activity.");
                }
            } else if (v.equals(btnView)) { //start view plants activity
                try {
                    Intent in = new Intent(v.getContext(), MainActivity.class);
                    startActivity(in);
                } catch (Exception e) {
                    Log.e("DEBUG", "StartActivity: Failed to launch view activity.");
                }
            }
        }

        else{ //alert user that connection was unsuccessful
            Toast.makeText(this, "Error connecting to server", Toast.LENGTH_SHORT).show();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void scheduleAlarm(int hours, int minutes) {
        //from http://stackoverflow.com/questions/8139936/set-android-alarm-at-specific-time
        Date dat  = new Date();//initializes to now
        Calendar cal_alarm = Calendar.getInstance();
        Calendar cal_now = Calendar.getInstance();
        cal_now.setTime(dat);
        cal_alarm.setTime(dat);
        cal_alarm.set(Calendar.HOUR_OF_DAY, hours);//set the alarm time
        cal_alarm.set(Calendar.MINUTE, minutes);
        cal_alarm.set(Calendar.SECOND, 0);
        if(cal_alarm.before(cal_now)){//if it's in the past, increment one day
            cal_alarm.add(Calendar.DATE,1);
        }

        //from http://stackoverflow.com/questions/16889775/how-to-schedule-a-task-using-alarm-manager
        //intent to be triggered by alarm
        Intent intentAlarm = new Intent(this, NotificationManager.class);
        //get alarm service
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        //set alarm for established time
        alarmManager.set(AlarmManager.RTC_WAKEUP, cal_alarm.getTimeInMillis(), PendingIntent.getBroadcast(this, 1, intentAlarm, PendingIntent.FLAG_UPDATE_CURRENT));
    }
}
