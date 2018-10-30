package com.wizeup.android.profile;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.wizeup.android.R;

public class BioActivity extends AppCompatActivity {

    private EditText mBioText;
    private Button mBSave;
    private TextView mTextCount;
    private final int MAX_COUNT = 150;


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
        mTextCount = findViewById(R.id.count_num);
        mBioText = findViewById(R.id.bio_text);
        mBSave = findViewById(R.id.save_button);
        Button mBCancel = findViewById(R.id.cancel_button);
        mBSave.setOnClickListener(view -> saveButton());
        mBCancel.setOnClickListener(view -> finish());
        mBioText.addTextChangedListener(mTextEditorWatcher);
    }

    private boolean getData() {
        if (getIntent().getExtras() != null) {
            String _bio = getIntent().getExtras().getString("bio");
            if (_bio != null) {
                mBioText.setText(_bio);
                return true;
            } else
                return false;
        } else
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

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int num = MAX_COUNT - s.length();
            mTextCount.setText(String.valueOf(num));
            if (num < 0) {
                mBSave.setEnabled(false);
                mBSave.setTextColor(Color.parseColor("#e7dada"));
                mTextCount.setTextColor(Color.RED);
            }
            else{
                mBSave.setEnabled(true);
                mTextCount.setTextColor(Color.parseColor("#808080"));
                mBSave.setTextColor(Color.WHITE);
            }
        }
        public void afterTextChanged(Editable s) {
        }
    };


}
