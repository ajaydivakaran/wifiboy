package com.apps.ajay.wifiboy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.Calendar;


public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainWifiBoyActivity";

    EditText pollingDurationEditText;
    EditText enabledDurationEditText;
    Switch checker;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences = getSharedPreferences(PreferenceConstants.PREFERENCES_NAME, MODE_PRIVATE);

        boolean wifiBoyEnabled = preferences.getBoolean(PreferenceConstants.WIFI_BOY_ENABLED, false);
        int intervalDuration = preferences.getInt(PreferenceConstants.INTERVAL_DURATION,
                PreferenceConstants.DEFAULT_INTERVAL_DURATION_IN_MINUTES);
        int enabledDuration = preferences.getInt(PreferenceConstants.ENABLED_DURATION,
                PreferenceConstants.DEFAULT_ENABLED_DURATION_IN_MINUTES);

        pollingDurationEditText = (EditText) findViewById(R.id.pollingTime);
        pollingDurationEditText.setText(Integer.toString(intervalDuration));
        enabledDurationEditText = (EditText) findViewById(R.id.enabledTime);
        enabledDurationEditText.setText(Integer.toString(enabledDuration));

        checker = (Switch) findViewById(R.id.checker);
        checker.setChecked(wifiBoyEnabled);

        Button saveButton = (Button) findViewById(R.id.save);
        saveButton.setOnClickListener(saveButtonListener(preferences));
    }

    private View.OnClickListener saveButtonListener(final SharedPreferences preferences) {
        return new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = preferences.edit();
                boolean checked = checker.isChecked();

                editor.putBoolean(PreferenceConstants.WIFI_BOY_ENABLED, checked);
                int intervalDurationInMinutes = Integer.parseInt(pollingDurationEditText.getText().toString());
                editor.putInt(PreferenceConstants.INTERVAL_DURATION, intervalDurationInMinutes);
                editor.putInt(PreferenceConstants.ENABLED_DURATION,
                        Integer.parseInt(enabledDurationEditText.getText().toString()));

                if (checked) {
                    scheduleRepeatingAlarm(intervalDurationInMinutes);
                } else {
                    cancelRepeatingAlarm();
                }
            }
        };
    }

    private void scheduleRepeatingAlarm(int intervalDurationInMinutes) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = getWifiBoyReceiverPendingIntent();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 1);
        long interval = intervalDurationInMinutes * 60 * 1000;

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                interval, pendingIntent);

        Log.d(TAG, "Enabled checker");
    }

    private void cancelRepeatingAlarm() {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = getWifiBoyReceiverPendingIntent();

        alarmManager.cancel(pendingIntent);

        Log.d(TAG, "Disabled checker");

    }

    private PendingIntent getWifiBoyReceiverPendingIntent() {
        Intent intent = new Intent(this, WifiBoyReceiver.class);
        return PendingIntent.getBroadcast(MainActivity.this, 1, intent, 0);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
