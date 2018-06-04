package com.ariel.wizeup.settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.preference.PreferenceManager;

import java.util.Locale;

import static com.ariel.wizeup.utils.Constants.LANG;

public class ChangeLanguage {
    private Activity activity;

    public ChangeLanguage(Activity _activity) {
        activity = _activity;
    }

    public void setLocale(String lang){

        Locale locale  = new Locale(lang);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        activity.getBaseContext().getResources().updateConfiguration(config,activity.getBaseContext().getResources().getDisplayMetrics());
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity);
        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(LANG,lang);
        editor.apply();
    }

    public void changeLanguageDialog() {
        final String[] listItems ={"Hebrew", "English"};
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
        mBuilder.setTitle("Choose ChangeLanguage...");
        mBuilder.setSingleChoiceItems(listItems, -1, (dialogInterface, i) -> {
            if(i == 0){
                setLocale("iw");
                activity.recreate();
            }
            else if(i == 1){
                setLocale("en");
                activity.recreate();
            }
            dialogInterface.dismiss();
        });
        AlertDialog mDialog = mBuilder.create();
        mDialog.show();
    }

}
