package com.ariel.wizeup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ariel.wizeup.entry.EntryActivity;
import com.ariel.wizeup.session.GraphActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        Intent intent = new Intent(this, EntryActivity.class);
        Intent intent = new Intent(this, GraphActivity.class);//rm

        startActivity(intent);
        finish();
    }
}
