package com.killvetrov.grocerylist.data;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Killvetrov on 06-Jul-15.
 */
public class PreferencesManager {

    private final static String SAVE_DATA_PREFS = "GROCERYLIST_PREFS";
    private final static String JSON_KEY = "JSON_TEXT";

    public static void saveText(Context context, String text) {
        SharedPreferences sp = context.getSharedPreferences(SAVE_DATA_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(JSON_KEY, text);
        editor.commit();
    }

    public static String readText(Context context) {
        SharedPreferences sp = context.getSharedPreferences(SAVE_DATA_PREFS, Context.MODE_PRIVATE);
        return sp.getString(JSON_KEY, "");
    }

}
