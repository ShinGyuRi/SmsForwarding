package com.abercompany.smsforwarding.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.abercompany.smsforwarding.model.Broker;
import com.abercompany.smsforwarding.model.Resident;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class PrefUtil implements SharedPreferences.OnSharedPreferenceChangeListener{
    private SharedPreferences prefs;
    private SharedPreferences.Editor prefEditor;

    private static PrefUtil instance = null;

    public final static String SHARED_PREF = "SHARED_PREFERENCE";

    public static PrefUtil getInstance() {
        if (instance == null) {
            instance = new PrefUtil();
        }
        return instance;
    }

    private PrefUtil() {
        prefs = BaseApplication.getContext().getSharedPreferences(SHARED_PREF, Context.MODE_PRIVATE);
        prefEditor = prefs.edit();
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }

    //put
    public void putPreference(String key, boolean value) {
        prefEditor.putBoolean(key, value);
        prefEditor.commit();
    }

    public void putPreference(String key, String value) {
        prefEditor.putString(key, value);
        prefEditor.commit();
    }

    public void putPreference(String key, int value) {
        prefEditor.putInt(key, value);
        prefEditor.commit();
    }

    public void putPreference(String key, long value) {
        prefEditor.putLong(key, value);
        prefEditor.commit();
    }

    public void putPreference(String key, Set<String> value) {
        prefEditor.putStringSet(key, value);
        prefEditor.commit();
    }

    public void putPreference(String key, List<Resident> value) {
        Gson gson = new Gson();
        String json = gson.toJson(value);

        prefEditor.putString(key, json);
        prefEditor.commit();
    }

    public void putPreference(List<Broker> value, String key)    {
        Gson gson = new Gson();
        String json = gson.toJson(value);

        prefEditor.putString(key, json);
        prefEditor.commit();

    }

    //get
    public boolean getBooleanPreference(String key){
        return prefs.getBoolean(key, false);
    }

    public String getStringPreference(String key){
        return prefs.getString(key, "");
    }

    public int getIntPreference(String key){
        if(prefs.contains(key))
            return prefs.getInt(key, 0);
        else
            return 0;
    }
    public long getLongPreference(String key){
        return prefs.getLong(key,0);
    }

    public Set<String> getStringSetPreference(String key) {
        return prefs.getStringSet(key, null);
    }

    public List<Resident> getResidentPreference(String key) {
        List<Resident> residents;
        String serializedObject = prefs.getString(key, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Resident>>() {
            }.getType();
            residents = gson.fromJson(serializedObject, type);
            return residents;
        } else {
            return null;
        }

    }

    public List<Broker> getBrokerPreference(String key) {
        List<Broker> brokers;
        String serializedObject = prefs.getString(key, null);
        if (serializedObject != null) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<Broker>>() {
            }.getType();
            brokers = gson.fromJson(serializedObject, type);
            return brokers;
        } else {
            return null;
        }

    }

    public void removePreference(String key){
        prefEditor.remove(key);
        prefEditor.commit();
    }
}
