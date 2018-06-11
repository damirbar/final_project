package com.ariel.wizeup.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import com.ariel.wizeup.R;
import com.ariel.wizeup.utils.Constants;

import java.util.Locale;

import static com.ariel.wizeup.utils.Constants.LANG;

public class ChangeLanguage {
    private Activity activity;

    public ChangeLanguage(Activity _activity) {
        activity = _activity;
    }

    public void setLocale(String lang) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
//            activity.getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
//        }
//

        Locale locale = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(config, activity.getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(LANG, lang);
        editor.apply();
    }

}
