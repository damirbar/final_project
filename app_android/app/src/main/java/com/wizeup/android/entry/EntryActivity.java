package com.wizeup.android.entry;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.wizeup.android.R;
import com.wizeup.android.dialogs.ResetPasswordDialog;
import com.wizeup.android.network.ServerResponse;

public class EntryActivity extends AppCompatActivity implements ResetPasswordDialog.Listener {

    public static final String TAG = EntryActivity.class.getSimpleName();

    private LoginFragment mLoginFragment;
    private ResetPasswordDialog mResetPasswordDialog;
    private ServerResponse mServerResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_entry);
        mServerResponse = new ServerResponse(findViewById(R.id.activity_entry));
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        if (savedInstanceState == null) {

            loadFragment();
        }
    }

    private void loadFragment(){

        if (mLoginFragment == null) {

            mLoginFragment = new LoginFragment();
        }
        getFragmentManager().beginTransaction().replace(R.id.fragmentFrame,mLoginFragment,LoginFragment.TAG).commit();
    }

//    @Override
////    protected void onNewIntent(Intent intent) {
////        super.onNewIntent(intent);
////
////        String data = intent.getData().getLastPathSegment();
////        Log.d(TAG, "onNewIntent: "+data);
////
////        mResetPasswordDialog = (ResetPasswordDialog) getFragmentManager().findFragmentByTag(ResetPasswordDialog.TAG);
////
////        if (mResetPasswordDialog != null)
////            mResetPasswordDialog.setToken(data);
////    }

    @Override
    public void onPasswordReset(String message) {

        mServerResponse.showSnackBarMessage(message);
    }


}
