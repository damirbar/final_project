package com.ariel.wizer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

public class BioActivity extends AppCompatActivity {

    private EditText mBioText;
    private Button mBSave;
    private Button mBcancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio);
        initViews();
        if (!getData()) {
            finish();
        }

    }

    private void initViews() {
        mBioText = (EditText) findViewById(R.id.bio_text);
        mBSave = (Button) findViewById(R.id.save_button);
        mBcancel = (Button) findViewById(R.id.cancel_button);
        mBSave.setOnClickListener(view -> saveButton());
        mBcancel.setOnClickListener(view -> finish());
    }

    private boolean getData() {
        if (getIntent().getExtras() != null) {
            String _bio = getIntent().getExtras().getString("bio");
            if(_bio != null) {
                mBioText.setText(_bio);
                return true;
            } else
                return false;
        }
        else
            return false;
    }



    public void saveButton() {
        String bio = mBioText.getText().toString().trim();

        Intent i = new Intent();
        Bundle extra = new Bundle();
        extra.putString("bio", bio);
        i.putExtras(extra);
        setResult(Activity.RESULT_OK, i);
        finish();
        }


}
