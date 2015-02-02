package com.apps.ajay.wifiboy;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.widget.Toast;

import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;

public class WifiBoyReceiver extends BroadcastReceiver {

    private final String TAG = "WifiBoyReceiver";

    public WifiBoyReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(NetworkConnectionManager.isWifiEnabled(context) || NetworkConnectionManager.isDataEnabled(context)){
            Log.d(TAG, "Wifi/Data already active");
            return;
        }

        final Context currentContext = context;

        new Thread(new Runnable() {

            @Override
            public void run() {
                Log.d(TAG, "Enabling wifi");
                WifiManager wifiManager = (WifiManager) currentContext.getSystemService(Context.WIFI_SERVICE);
                wifiManager.setWifiEnabled(true);
                try{
                    Thread.sleep(PreferenceConstants.WIFI_WAIT_TIME_IN_SECONDS * 1000);
                    if(NetworkConnectionManager.isWifiEnabled(currentContext)){
                        Log.d(TAG, "Wifi is enabled");
                    }
                    else{
                        Log.e(TAG, "Wifi still disabled falling back to mobile data");
                        NetworkConnectionManager.setMobileDataEnabled(currentContext, true);
                    }
                    scheduleAlarmToShutdownWifi(currentContext);
                }
                catch (Exception ex){
                 Log.e(TAG, ex.getMessage());
                }
            }
        }).start();

    }

    private void scheduleAlarmToShutdownWifi(Context currentContext) {
        AlarmManager alarmManager = (AlarmManager) currentContext.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(currentContext, WifiShutdownReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(currentContext, 2, intent, 0);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, 2);

        alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
    }

}
