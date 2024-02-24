//Stores user preferences and user details in app's private space

package com.android.cryptpay;

import android.content.Context;
import android.content.SharedPreferences;

public class SharedPreferenceManager {
    private static final String PREFERENCE_NAME = "UserPreference";
    private final Context context;

    public SharedPreferenceManager(Context context) {
        this.context = context;
    }

    public <T> void putValue(String key, T value) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        if (value instanceof String) {
            editor.putString(key, (String) value);
        } else if (value instanceof Integer) {
            editor.putInt(key, (Integer) value);
        } else if (value instanceof Boolean) {
            editor.putBoolean(key, (Boolean) value);
        } else if (value instanceof Float) {
            editor.putFloat(key, (Float) value);
        } else if (value instanceof Long) {
            editor.putLong(key, (Long) value);
        }

        editor.apply();
    }

    public String getStringValue(String key, String defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, defaultValue);
    }

    public int getIntValue(String key, int defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, defaultValue);
    }

    public boolean getBooleanValue(String key, boolean defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    public float getFloatValue(String key, float defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(key, defaultValue);
    }

    public long getLongValue(String key, long defaultValue) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sharedPreferences.getLong(key, defaultValue);
    }

    public void clearData() {
        SharedPreferences.Editor editor = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE).edit();
        editor.clear();
        editor.apply();
    }



}
