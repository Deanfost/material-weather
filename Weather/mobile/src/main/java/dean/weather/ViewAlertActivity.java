package dean.weather;

import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.firebase.FirebaseApp;

/**
 * Created by Dean Foster on 1/22/2017.
 */

public class ViewAlertActivity extends AppCompatActivity {
    Typeface robotoLight;
    TextView titleView;
    TextView descView;
//    TextView srcView;
    String alertTitle;
    String alertDesc;
    String alertSrc;
    Integer setID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);
        setID = getIntent().getExtras().getInt("setID");
        alertTitle = getIntent().getExtras().getString("alertTitle");
        alertDesc = getIntent().getExtras().getString("alertDesc");
        alertSrc = getIntent().getExtras().getString("alertSrc");

        setContentView(R.layout.activity_view_alert);

        //Setup app bar
        Toolbar viewAlertToolbar = (Toolbar) findViewById(R.id.viewAlertToolbar);
        setSupportActionBar(viewAlertToolbar);
        assert getSupportActionBar() != null;
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Weather Alert");

        //References
        robotoLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        titleView = (TextView) findViewById(R.id.alertViewTitle);
        descView = (TextView) findViewById(R.id.alertViewDesc);
//        srcView = (TextView) findViewById(R.id.alertViewSrc);

        //Set the color of the layout
        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);

        switch (setID){
            case 0:
                this.setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), icon, getResources().getColor(R.color.colorYellowDark)));
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorYellowDark)));
                window.setStatusBarColor(getResources().getColor(R.color.colorYellowDark));
                break;
            case 1:
                this.setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), icon, getResources().getColor(R.color.colorBlueDark)));
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorBlueDark)));
                window.setStatusBarColor(getResources().getColor(R.color.colorBlueDark));
                break;
            case 2:
                this.setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), icon, getResources().getColor(R.color.colorOrangeDark)));
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorOrangeDark)));
                window.setStatusBarColor(getResources().getColor(R.color.colorOrangeDark));
                break;
            case 3:
                this.setTaskDescription(new ActivityManager.TaskDescription(getResources().getString(R.string.app_name), icon, getResources().getColor(R.color.colorPurpleDark)));
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.colorPurpleDark)));
                window.setStatusBarColor(getResources().getColor(R.color.colorPurpleDark));
                break;
        }

        //Display alert
        titleView.setText(alertTitle);
        descView.setTypeface(robotoLight);
        descView.setText(alertDesc);
//        srcView.setTypeface(robotoLight);
//        srcView.setText(alertSrc);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("View Alert Activity", "Finishing");
    }
}
