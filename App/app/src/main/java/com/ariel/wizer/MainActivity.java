package com.ariel.wizer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import com.ariel.wizer.fragments.LoginFragment;
import com.ariel.wizer.fragments.ResetPasswordDialog;
import com.ariel.wizer.network.ServerResponse;

public class MainActivity extends AppCompatActivity implements ResetPasswordDialog.Listener {

    public static final String TAG = MainActivity.class.getSimpleName();

    private LoginFragment mLoginFragment;
    private ResetPasswordDialog mResetPasswordDialog;
    private ServerResponse mServerResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mServerResponse = new ServerResponse(findViewById(R.id.activity_main));
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
