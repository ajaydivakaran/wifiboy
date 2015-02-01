package com.apps.ajay.wifiboy;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class NetworkConnectionManager {

    public static void setMobileDataEnabled(Context context, boolean enabled) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        final ConnectivityManager conman = (ConnectivityManager)  context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final Class conmanClass = Class.forName(conman.getClass().getName());
        final Field connectivityManagerField = conmanClass.getDeclaredField("mService");
        connectivityManagerField.setAccessible(true);
        final Object connectivityManager = connectivityManagerField.get(conman);
        final Class connectivityManagerClass =  Class.forName(connectivityManager.getClass().getName());
        final Method setMobileDataEnabledMethod = connectivityManagerClass.getDeclaredMethod("setMobileDataEnabled", Boolean.TYPE);
        setMobileDataEnabledMethod.setAccessible(true);

        setMobileDataEnabledMethod.invoke(connectivityManager, enabled);
    }

    public static boolean isDataEnabled(Context context){
        boolean dataEnabled = false;

        NetworkInfo activeNetworkInfo = getNetworkInfo(context);
        if(activeNetworkInfo != null && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI){
            dataEnabled = activeNetworkInfo.getType() == ConnectivityManager.TYPE_MOBILE;
        }

        return dataEnabled;
    }

    public static boolean isWifiEnabled(Context context){
        boolean wifiEnabled = false;

        NetworkInfo activeNetworkInfo = getNetworkInfo(context);
        if(activeNetworkInfo != null && activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI){
            wifiEnabled = activeNetworkInfo.getType() == ConnectivityManager.TYPE_WIFI;
        }

        return wifiEnabled;
    }

    private static NetworkInfo getNetworkInfo(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo();
    }
}
