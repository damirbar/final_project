package com.ariel.wizeup.settings;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;

import com.ariel.wizeup.R;
import com.ariel.wizeup.base.BaseActivity;
import com.ariel.wizeup.model.Language;
import com.ariel.wizeup.utils.Constants;

import java.util.ArrayList;


public class SettingsActivity extends AppCompatActivity {

    private ListView langListView;
    private LanguageAdapter mAdapter;
    private String currentLang;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initSharedPreferences();
        initViews();
        initListView();

        langListView.setOnItemClickListener((parent, view1, position, id) -> {
            Language l = mAdapter.getItem(position);
            ChangeLanguage changeLanguage = new ChangeLanguage(this);
            if (l != null && !(l.getKey().equalsIgnoreCase(currentLang))) {
                changeLanguage.setLocale(l.getKey());
                Intent settings = new Intent(this,SettingsActivity.class);
                Intent base = new Intent(SettingsActivity.this, BaseActivity.class);
                base.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(base);
                startActivity(settings);
            }
        });

    }

    private void initViews() {
        ImageButton buttonBack = findViewById(R.id.image_Button_back);
        langListView = findViewById(R.id.lv_items);
        langListView.setVisibility(View.GONE);
        Button langBtn = findViewById(R.id.lang_button);
        Button passBtn = findViewById(R.id.pass_button);
        Button reportBtn = findViewById(R.id.report_button);
        reportBtn.setOnClickListener(view -> OpenReport());
        passBtn.setOnClickListener(view -> OpenChangePass());
        buttonBack.setOnClickListener(view -> finish());
        langBtn.setOnClickListener(view -> OpenCloseList());
    }

    private void OpenReport() {
        Intent i = new Intent(this,FeedbackActivity.class);
        startActivity(i);
    }

    private void OpenChangePass() {
        Intent i = new Intent(this,ChangePasswordActivity.class);
        startActivity(i);
    }

    private void OpenCloseList() {
        if (langListView.getVisibility() == View.VISIBLE) {
            langListView.setVisibility(View.GONE);
        } else
            langListView.setVisibility(View.VISIBLE);
    }

    private void initListView() {
        final ArrayList<Language> saveLanguages = new ArrayList<>();
        saveLanguages.add(new Language("English", "en",getString(R.string.english)));
        saveLanguages.add(new Language("עברית", "iw",getString(R.string.hebrew)));
        mAdapter = new LanguageAdapter(this, new ArrayList<>(saveLanguages));
        langListView.setAdapter(mAdapter);
    }

    private void initSharedPreferences() {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        currentLang = mSharedPreferences.getString(Constants.LANG,"");
    }



}
