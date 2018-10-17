package com.ariel.wizeup;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ariel.wizeup.entry.EntryActivity;
import com.ariel.wizeup.liveStream.RtcActivity;
import com.ariel.wizeup.liveStream.WebRtcClient;
import com.ariel.wizeup.session.GraphActivity;
import com.ariel.wizeup.session.QuizActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(this, EntryActivity.class);
//        Intent intent = new Intent(this, RtcActivity.class);//rm

        startActivity(intent);
        finish();
    }
}
