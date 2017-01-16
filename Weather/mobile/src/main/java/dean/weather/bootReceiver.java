package dean.weather;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;

/**
 * Created by DeanF on 12/11/2016.
 */

public class bootReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        Log.i("bootReceiver", "broadcastReceived");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        //If either the repeat notif, summary notif, or alert notif is enabled
        if(prefs.getBoolean("key_notif_follow", false) || prefs.getBoolean("key_notif_summary", false) || prefs.getBoolean("key_notif_alert", false)) {
            //Tell the alarmInterfaceService class to start the notifService, and to setup an alarm
            //Put the appropriate extras
            //Repeating notif
            if(prefs.getBoolean("key_notif_follow", false)){
                Intent interfaceIntent = new Intent(context, alarmInterfaceService.class);
                interfaceIntent.putExtra("repeatNotif", true);
                Log.i("bootReceiver", "repeatNotif enabled");
                context.startService(interfaceIntent);
            }

//            //Summary notif
//            if(prefs.getBoolean("key_notif_summary", false)){
//                Log.i("bootReceiver", "summaryNotif enabled");
//                //Look at key-value pairs to see if there is an alarm set, and determine if it was supposed to be fired already
//                Long alarmTime = prefs.getLong("key_summary_alarm", 0);
//                Log.i("alarmTime", alarmTime.toString());
//                if(alarmTime < System.currentTimeMillis() && alarmTime != 0){
//                    Log.i("bootReciever", "Alarm overdue");
//                    //Launch the service
//                    Intent summaryServiceIntent = new Intent(context, alertNotifService.class);
//                    context.startService(summaryServiceIntent);
//                    //Reset the alarm
//                    Intent resetAlarmIntent = new Intent(context, alarmInterfaceService.class);
//                    resetAlarmIntent.putExtra("summaryNotif", true);
//                    resetAlarmIntent.putExtra("alarmTime", alarmTime);
//                    context.startService(resetAlarmIntent);
//                }
//                else if(alarmTime > System.currentTimeMillis()){
//                    Log.i("bootReceiver", "Alarm not overdue");
//                    //Reset the alarm
//                    Intent resetAlarmIntent = new Intent(context, alarmInterfaceService.class);
//                    resetAlarmIntent.putExtra("alarmTime", alarmTime);
//                    resetAlarmIntent.putExtra("summaryNotif", true);
//                    context.startService(resetAlarmIntent);
//                }
//                else if( alarmTime == 0){
//                    Log.i("bootReceiver", "No time set");
//                }
//            }
            //Alert notif
//            if(prefs.getBoolean("key_notif_alert", false)){
//                Intent serviceIntent = new Intent(context, alarmInterfaceService.class);
//                serviceIntent.putExtra("alertNotif", true);
//                context.startService(serviceIntent);
//                Log.i("bootReceiver", "alertNotif enabled");
//            }
        }
        else{
            Log.i("bootReceiver", "All services disabled");
        }
    }
}