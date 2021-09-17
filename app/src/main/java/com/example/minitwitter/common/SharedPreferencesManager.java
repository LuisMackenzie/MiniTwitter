package com.example.minitwitter.common;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferencesManager {

    private static final String APP_SETTINGS_FILE = "SETTINGS";

    private SharedPreferencesManager() {
    }

    private static SharedPreferences  getSharedPreferences() {

        return MyApp.getContext().getSharedPreferences(APP_SETTINGS_FILE, Context.MODE_PRIVATE);
    }

    public static void setStringValue(String dataLabel, String dataValue) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putString(dataLabel, dataValue);
        editor.commit();

    }

    public static void setBooleanValue(String dataLabel, boolean dataValue) {
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.putBoolean(dataLabel, dataValue);
        editor.commit();
    }

    public static String getStringValue(String dataLabel) {
        return getSharedPreferences().getString(dataLabel, null);

    }

    public static boolean getBooleanValue(String dataLabel) {
        return getSharedPreferences().getBoolean(dataLabel, false);
    }

}
