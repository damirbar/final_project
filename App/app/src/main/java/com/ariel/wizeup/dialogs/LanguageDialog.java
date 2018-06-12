package com.ariel.wizeup.dialogs;

import android.app.DialogFragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;

import com.ariel.wizeup.R;
import com.ariel.wizeup.settings.ChangeLanguage;
import com.ariel.wizeup.utils.Constants;


public class LanguageDialog extends DialogFragment {

    private NumberPicker mNumberPicker;
    private ChangeLanguage changeLanguage;
    private String lang;

    public static final String TAG = LanguageDialog.class.getSimpleName();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_language_dialog, container, false);
        changeLanguage = new ChangeLanguage(getActivity());
        initViews(view);
        initPicker();
        initSharedPreferences();

        return view;
    }

    private void initPicker() {
        String[] data = new String[]{getActivity().getString(R.string.english), getActivity().getString(R.string.hebrew)};
        mNumberPicker.setMinValue(0);
        mNumberPicker.setMaxValue(data.length - 1);
        mNumberPicker.setDisplayedValues(data);

    }

    private void initSharedPreferences() {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        lang = mSharedPreferences.getString(Constants.LANG, "");
        int i = 0;
        if (lang.equalsIgnoreCase("iw"))
            i = 1;
        mNumberPicker.setValue(i);

    }


    private void initViews(View v) {
        mNumberPicker = v.findViewById(R.id.number_picker);
        Button mBtSetLang = v.findViewById(R.id.button_ok);
        mBtSetLang.setOnClickListener(view -> onLangSet());
        Button mBtCancel = v.findViewById(R.id.button_cancel);
        mBtCancel.setOnClickListener(view -> dismiss());
    }

    public void onLangSet() {
        int item = mNumberPicker.getValue();
        if (item == 0 && !(lang.equalsIgnoreCase("en"))) {
            changeLanguage.setLocale("en");
            getActivity().recreate();
        } else if (item == 1 && !(lang.equalsIgnoreCase("iw"))) {
            changeLanguage.setLocale("iw");
            getActivity().recreate();
        }
        dismiss();
    }


}