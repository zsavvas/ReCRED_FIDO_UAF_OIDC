package eu.recred.fidouaf.authenticator;

import android.content.Context;
import android.content.SharedPreferences;

import eu.recred.fidouaf.util.ApplicationContextProvider;

/**
 * Created by sorin.teican on 10/13/2016.
 */
public class PreferenceStorage {

    private static final String AUTHENTICATOR_STORAGE = "AUTHENTICATOR_STORAGE";

    private static SharedPreferences getPreferences() {
        return ApplicationContextProvider.getContext().getSharedPreferences(AUTHENTICATOR_STORAGE, Context.MODE_PRIVATE);
    }

    public static String getValue(String name, String defaultValue) {
        SharedPreferences preferences = getPreferences();
        return preferences.getString(name, defaultValue);
    }

    public static void setValue(String name, String value) {
        SharedPreferences preferences = getPreferences();
        SharedPreferences.Editor editor = preferences.edit();

        editor.putString(name, value);
        editor.commit();
    }
}
