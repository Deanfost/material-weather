package dean.weather;

import android.Manifest;
import android.app.Dialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Dean on 12/23/2016.
 */

public class settingsActivity extends PreferenceActivity{
    SharedPreferences prefs;
    Preference followMePref;
    SwitchPreference ongoingNotif;
    SwitchPreference summaryNotif;
    Preference timePickerPref;
    boolean performChecksReturn;

    //Lifecycle and preference listeners
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        FirebaseApp.initializeApp(this);
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);

        //Preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        followMePref = findPreference(getString(R.string.follow_me_key));
        ongoingNotif = (SwitchPreference) findPreference(getString(R.string.ongoing_notif_key));
        summaryNotif = (SwitchPreference) findPreference(getString(R.string.summary_notif_key));
        timePickerPref = findPreference(getString(R.string.summary_time_key));

        //Enable the timePickerPref?
        if(prefs.getBoolean(getString(R.string.summary_notif_key), false)){
            timePickerPref.setEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        //Register preference listeners

        //Follow me pref
        followMePref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.i("followMePref", "Value changed");
                if(!(boolean) newValue){
                    Log.i("followMePref", "Changed to false, turning off services");
                    //Kill the ongoing notif if running
                    if(prefs.getBoolean(getString(R.string.ongoing_notif_key), false)){
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean(getString(R.string.ongoing_notif_key), false);
                        editor.commit();
                        ongoingNotif.setChecked(false);

                        //Kill the ongoing service if running
                        Intent stopService = new Intent(settingsActivity.this, alarmInterfaceService.class);
                        stopService.putExtra("repeatNotif", false);
                        startService(stopService);

                        //Clear the notif
                        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(MainActivity.FOLLOW_NOTIF_ID);
                    }
                    //Kill the summary notif if running
                    if(prefs.getBoolean(getString(R.string.summary_notif_key), false)){
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean(getString(R.string.summary_notif_key), false);
                        editor.commit();
                        summaryNotif.setChecked(false);
                        timePickerPref.setEnabled(false);

                        //Kill the ongoing service if running
                        Intent stopService = new Intent(settingsActivity.this, alarmInterfaceService.class);
                        stopService.putExtra("summaryNotif", false);
                        startService(stopService);

                        //Clear the notif
                        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                        notificationManager.cancel(MainActivity.SUMMARY_NOTIF_ID);
                    }
                }
                return true;
            }
        });

        //Ongoing notification pref
        ongoingNotif.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.i("ongoingNotif", "Pref changed");
                Boolean ongoingNotifValue = (Boolean) newValue;
                Log.i("ongoingNotif", ongoingNotifValue.toString());
                //Start the notif service
                if(ongoingNotifValue){
                    //Check to see if the user selected "follow me"
                    if(prefs.getBoolean(getString(R.string.follow_me_key),false)){
                        //Check if we are able to use current location
                        if(performChecks()){
                            //Start it, everything is looking good
                            Log.i("ongoingNotifPref", "Looks good, starting service");
                            Intent serviceIntent = new Intent(settingsActivity.this, alarmInterfaceService.class);
                            serviceIntent.putExtra("repeatNotif", true);
                            startService(serviceIntent);
                            return true;
                        }
                        else{
                            //Don't save the value, conditions not met
                            return false;
                        }
                    }
                    //The user selected a location
                    else{
                        //TODO - FINISH THIS
                        Log.i("ongoingNotif", "location selected");
                        return false;
                    }
                }
                else{
                    //Stop the notif service
                    Log.i("ongoingNotifPref", "stoppingService");
                    Intent stopService = new Intent(settingsActivity.this, alarmInterfaceService.class);
                    stopService.putExtra("repeatNotif", false);
                    startService(stopService);

                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(MainActivity.FOLLOW_NOTIF_ID);
                    return true;
                }
            }
        });

        //TODO - CREATE A LISTENER FOR THE LIST ITEMS CONTAINING THE LOCATIONS

        //Summary notification pref
        summaryNotif.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.i("summaryNotif", newValue.toString());
                //Start the summary service
                if((Boolean) newValue){
                    //Start with current location
                    if(prefs.getBoolean(getString(R.string.follow_me_key),false)){
                        //Check to see if we can do this
                        Log.i("summaryNotif", "Follow me selected");
                        if(performChecks()){
                            //Start it, everything is looking good
                            Log.i("summaryNotifPref", "Looks good, starting service");
                            Intent summaryService = new Intent(settingsActivity.this, alarmInterfaceService.class);
                            summaryService.putExtra("summaryNotif", true);
                            summaryService.putExtra("alarmTime", prefs.getLong(getString(R.string.summary_time_key), System.currentTimeMillis()));
                            startService(summaryService);
                            timePickerPref.setEnabled(true);

                            //Notify the user
                            Long alarmTime = prefs.getLong(getString(R.string.summary_time_key), System.currentTimeMillis());
                            String dateFormatted = null;

                            //Detect if the user is using 24 hour time or not
                            //They aren't
                            if(!android.text.format.DateFormat.is24HourFormat(settingsActivity.this)){
                                Date date = new Date(alarmTime);
                                DateFormat formatter = new SimpleDateFormat("HH:mm aa");
                                dateFormatted = formatter.format(date);
                                Integer hh = Integer.valueOf(dateFormatted.substring(0, 2));
                                String restOfString = dateFormatted.substring(2);
                                //TODO - FIX THIS
                                if(hh > 12){
                                    hh -= 12;
                                    Toast.makeText(settingsActivity.this, "Alarm set for " + hh.toString() + restOfString, Toast.LENGTH_SHORT).show();
                                }
                                else if (hh < 12 && hh > 0){
                                    Toast.makeText(settingsActivity.this, "Alarm set for " + dateFormatted, Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(settingsActivity.this, "Alarm set for " + 12 + restOfString, Toast.LENGTH_SHORT).show();
                                }
                            }
                            //They are
                            else{
                                Date date = new Date(alarmTime);
                                DateFormat formatter = new SimpleDateFormat("HH:mm");
                                dateFormatted = formatter.format(date);
                                Toast.makeText(settingsActivity.this, "Alarm set for " + dateFormatted, Toast.LENGTH_SHORT).show();
                            }
                            Log.i("alarmTime", dateFormatted);
                            return true;
                        }
                        else{
                            //Don't save the value, conditions not met
                            return false;
                        }
                    }
                    //Start with a selected location
                    else{
                        //TODO - FINISH THIS
                        Log.i("summaryNotif", "Location selected");
                        return false;
                    }
                }
                //Stop the summary notifs
                else{
                    Log.i("summaryNotifPref", "stoppingService");
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("summaryNotif", false);
                    editor.commit();
                    summaryNotif.setChecked(false);
                    timePickerPref.setEnabled(false);

                    //Kill the alarm
                    Intent stopService = new Intent(settingsActivity.this, alarmInterfaceService.class);
                    stopService.putExtra("summaryNotif", false);
                    startService(stopService);

                    //Clear the notif
                    NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.cancel(MainActivity.SUMMARY_NOTIF_ID);
                    return true;
                }
            }
        });

        //Summary time picker pref
        timePickerPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Log.i("timePicker", "Changed");
                if(prefs.getBoolean(getString(R.string.follow_me_key),false)){
                    //Check to see if we can do this
                    Log.i("summaryNotif", "Follow me selected");
                    if(performChecks()){
                        //Start it, everything is looking good
                        Log.i("timePickerPref", "Looks good, starting service");
                        Intent summaryService = new Intent(settingsActivity.this, alarmInterfaceService.class);
                        summaryService.putExtra("summaryNotif", true);
                        summaryService.putExtra("alarmTime", (Long) newValue);
                        startService(summaryService);

                        //Notify the user
                        Long alarmTime = (Long) newValue;
                        String dateFormatted;

                        //Detect if the user is using 24 hour time or not
                        //They aren't
                        if(!android.text.format.DateFormat.is24HourFormat(settingsActivity.this)){
                            Date date = new Date(alarmTime);
                            DateFormat formatter = new SimpleDateFormat("HH:mm aa");
                            dateFormatted = formatter.format(date);
                            Integer hh = Integer.valueOf(dateFormatted.substring(0, 2));
                            String restOfString = dateFormatted.substring(2);
                            if(hh > 12){
                                hh -= 12;
                                Toast.makeText(settingsActivity.this, "Alarm set for " + hh.toString() + restOfString, Toast.LENGTH_SHORT).show();
                            }
                            else if(hh < 12 && hh > 0   ){
                                Toast.makeText(settingsActivity.this, "Alarm set for " + dateFormatted, Toast.LENGTH_SHORT).show();
                            }
                            else{
                                Toast.makeText(settingsActivity.this, "Alarm set for " + 12 + restOfString, Toast.LENGTH_SHORT).show();
                            }
                        }
                        //They are
                        else{
                            Date date = new Date(alarmTime);
                            DateFormat formatter = new SimpleDateFormat("HH:mm");
                            dateFormatted = formatter.format(date);
                            Toast.makeText(settingsActivity.this, "Alarm set for " + dateFormatted, Toast.LENGTH_SHORT).show();
                        }
                        Log.i("alarmTime", dateFormatted);
                        return true;
                    }
                    else{
                        //Turn off the pref, and change the value
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.putBoolean(getString(R.string.summary_notif_key), false);
                        editor.commit();
                        summaryNotif.setChecked(false);
                        timePickerPref.setEnabled(false);

                        //Kill the alarm
                        Intent stopService = new Intent(settingsActivity.this, alarmInterfaceService.class);
                        stopService.putExtra("summaryNotif", false);
                        startService(stopService);
                        return true;
                    }
                }
                //Start with a selected location
                else{
                    //TODO - FINISH THIS
                    Log.i("summaryNotif", "Location selected");
                    return true;
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //Un-register click listeners
        followMePref.setOnPreferenceChangeListener(null);
        ongoingNotif.setOnPreferenceClickListener(null);
        summaryNotif.setOnPreferenceChangeListener(null);
        timePickerPref.setOnPreferenceChangeListener(null);
    }

    //Checks and callbacks
    /**
     * Checks to see if both location permission and services are enabled.
     * @return performChecksReturn
     */
    private boolean performChecks(){
        //Check to see if location permissions and services are enabled
        int locationPermissionCheck = ContextCompat.checkSelfPermission(settingsActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION);
        if(locationPermissionCheck == PackageManager.PERMISSION_GRANTED){
            Log.i("permissionsCheck", "Granted");
            //Check to see if location services are enabled
            if(isLocationServicesEnabled()){
                //Permissions granted, and services are on, start the service
                performChecksReturn = true;
            }
            else{
                //Show a toast asking the user to enable location services
                Toast.makeText(this, "Please enable location services to access this feature", Toast.LENGTH_SHORT).show();
                performChecksReturn = false;
            }
        }
        //Permission has not been granted
        else{
            //Request permission access
            ActivityCompat.requestPermissions(settingsActivity.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 22);
            performChecksReturn = false;
        }
        Log.i("checksReturn", performChecksReturn + "");
        return performChecksReturn;
    }

    /**
     * Checks to see if location services are enabled.
     * @return
     */
    private boolean isLocationServicesEnabled(){
        int locationMode;
        try {
            locationMode = Settings.Secure.getInt(this.getContentResolver(), Settings.Secure.LOCATION_MODE);

        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        //True if location services are on and not set to device only
        Log.i("locServices", (locationMode != Settings.Secure.LOCATION_MODE_OFF && locationMode != Settings.Secure.LOCATION_MODE_SENSORS_ONLY) + "" );
        return locationMode != Settings.Secure.LOCATION_MODE_OFF && locationMode != Settings.Secure.LOCATION_MODE_SENSORS_ONLY;
    }

    /**
     * Receives permission request result callback.
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 22: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //Permission granted
                    Log.i("permissionResult", "granted");
                }
                else {
                    //Permission denied, do nothing
                    Log.i("permissionResult", "withheld");
                }
                return;
            }
        }
    }

    //Configurations
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        LinearLayout root = (LinearLayout)findViewById(android.R.id.list).getParent().getParent().getParent();
        Toolbar bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar, root, false);
        root.addView(bar, 0); // insert at top
        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        super.onPreferenceTreeClick(preferenceScreen, preference);

        // If the user has clicked on a preference screen, set up the screen
        if (preference instanceof PreferenceScreen) {
            setUpNestedScreen((PreferenceScreen) preference);
        }

        return false;
    }

    public void setUpNestedScreen(PreferenceScreen preferenceScreen) {
        final Dialog dialog = preferenceScreen.getDialog();

        Toolbar bar;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            LinearLayout root = (LinearLayout) dialog.findViewById(android.R.id.list).getParent();
            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar, root, false);
            root.addView(bar, 0); // insert at top
        } else {
            ViewGroup root = (ViewGroup) dialog.findViewById(android.R.id.content);
            ListView content = (ListView) root.getChildAt(0);

            root.removeAllViews();

            bar = (Toolbar) LayoutInflater.from(this).inflate(R.layout.toolbar, root, false);

            int height;
            TypedValue tv = new TypedValue();
            if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true)) {
                height = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
            }else{
                height = bar.getHeight();
            }

            content.setPadding(0, height, 0, 0);

            root.addView(content);
            root.addView(bar);
        }

        bar.setTitle(preferenceScreen.getTitle());

        bar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}