package com.example.studentbudget;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

public class MySharedPreferences {

    public static final String PREFERENCE_WEEK_KEY = ".com.example.studentbudget.weekdatakey";
    public static final String PREFERENCE_MONTH_KEY = ".com.example.studentbudget.monthdatakey";
    public static final String PREFERENCE_TERM_KEY = ".com.example.studentbudget.termdatakey";

    public static final String KEY_BUDGET = "budget";
    public static final String KEY_TARGET_SAVINGS = "target_savings";
    public static final String KEY_SAVINGS = "savings";
    public static final String KEY_SPENDING = "spendings";
    public static final String[] KEY_ALL = {KEY_BUDGET, KEY_TARGET_SAVINGS, KEY_SAVINGS, KEY_SPENDING};

    Context mContext;
    String mPreferenceKey;
    /*
        mPreferenceKey - the name of the SharedPreference 'file'
            --> one of the constant strings provided above
    */

    public MySharedPreferences(Context context, String preferenceKey) {
        mContext = context;
        mPreferenceKey = preferenceKey;
    }

    public void writePreferenceData(float[] values, int[] index) {
        /*
        values - the values to be stored in the sharedPreference
         */
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mPreferenceKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < index.length; ++i) {
            Log.d("WRITING TO SP", "Key: " + KEY_ALL[index[i]] + ", Data: " + values[i]);
            editor.putFloat(KEY_ALL[index[i]], values[i]);
            editor.commit();
        }
    }

    public float readPreferenceData(String searchKey) {
        /*
        searchKey - the key being searched for in the SharedPreference 'file'
            --> one of the constant strings provided above
         */
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mPreferenceKey, Context.MODE_PRIVATE);
        return sharedPreferences.getFloat(searchKey, 0);
    }

    public float[] getAllData() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mPreferenceKey, Context.MODE_PRIVATE);
        float[] data = new float[KEY_ALL.length];
        for (int i = 0; i < KEY_ALL.length; ++i) {
            data[i] = sharedPreferences.getFloat(KEY_ALL[i], 10.5f);
            Log.d("READING SP", "Key: " + KEY_ALL[i] + ", Data: " + data[i]);
        }
        return data;
    }

    public void resetSharedPreferenceData() {
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mPreferenceKey, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        for (int i = 0; i < KEY_ALL.length; ++i) {
            editor.putFloat(KEY_ALL[i], 0);
        }
    }

    public void incramentSharedPreferences(String searchKey, float incrementalValue) {
        /*
        searchKey - the key being searched for in the SharedPreference 'file'
            --> one of the constant strings provided above
        incrementalValue - the increase in the value of the key field being searched for
            --> can be negative to decrease value
         */
        SharedPreferences sharedPreferences = mContext.getSharedPreferences(mPreferenceKey, Context.MODE_PRIVATE);
        float originalValue = sharedPreferences.getFloat(searchKey, 0);
        float newValue = originalValue + incrementalValue;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putFloat(searchKey, newValue);
    }

}
