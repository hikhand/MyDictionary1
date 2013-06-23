package com.hister.mydictionary;

import android.app.Activity;
import android.os.Bundle;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
<<<<<<< HEAD:src/main/java/com/hister/mydictionary/Preferences.java
<<<<<<< HEAD:src/main/java/com/hister/mydictionarypro/Preferences.java

import com.hister.mydictionarypro.R;
=======
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.Toast;
>>>>>>> parent of 739692b... 2.0.1:src/main/java/com/hister/mydictionary/Preferences.java
=======
import android.preference.Preference.OnPreferenceClickListener;
import android.widget.Toast;
>>>>>>> parent of 739692b... 2.0.1:src/main/java/com/hister/mydictionary/Preferences.java

public class Preferences extends PreferenceActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.layout.preferences);

        // Get the custom preference
//        Preference customPref = (Preference) findPreference("customPref");
//        assert customPref != null;
//        customPref
//                .setOnPreferenceClickListener(new OnPreferenceClickListener() {
//
//                    public boolean onPreferenceClick(Preference preference) {
//                        Toast.makeText(getBaseContext(),
//                                "The custom preference has been clicked",
//                                Toast.LENGTH_LONG).show();
//                        SharedPreferences customSharedPreference = getSharedPreferences(
//                                "myCustomSharedPrefs", Activity.MODE_PRIVATE);
//                        SharedPreferences.Editor editor = customSharedPreference
//                                .edit();
//                        editor.putString("myCustomPref",
//                                "The preference has been clicked");
//                        editor.commit();
//                        return true;
//                    }
//
//                });
    }
}
