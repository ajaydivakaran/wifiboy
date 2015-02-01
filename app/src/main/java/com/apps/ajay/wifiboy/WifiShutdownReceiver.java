package com.apps.ajay.wifiboy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;

public class WifiShutdownReceiver extends BroadcastReceiver {

    private static final String TAG = "WifiShutdownReceiver";

    public WifiShutdownReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        disableWifi(context);
        disabledMobileData(context);
    }

    private void disabledMobileData(Context context) {
        try {
            if(NetworkConnectionManager.isDataEnabled(context)){
                NetworkConnectionManager.setMobileDataEnabled(context, false);
                Log.d(TAG, "Disabling mobile data");
                return;
            }
            Log.d(TAG, "Mobile data already disabled");
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void disableWifi(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        int wifiState = wifiManager.getWifiState();

        if(wifiState == WifiManager.WIFI_STATE_DISABLED || wifiState == WifiManager.WIFI_STATE_DISABLING){
            Log.d(TAG, "Wifi already disabled/disabling");
            return;
        }

        Log.d(TAG, "Disabling wifi");
        wifiManager.setWifiEnabled(false);
    }
}
