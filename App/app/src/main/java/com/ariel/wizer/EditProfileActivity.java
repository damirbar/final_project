package com.ariel.wizer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ariel.wizer.model.Response;
import com.ariel.wizer.model.User;
import com.ariel.wizer.network.NetworkUtil;
import com.ariel.wizer.utils.Constants;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ariel.wizer.utils.Constants.EMAIL;
import static com.ariel.wizer.utils.Validation.validateEmail;

public class EditProfileActivity extends AppCompatActivity implements PicModeSelectDialogFragment.IPicModeSelectListener {

    private EditText mETFirstName;
    private EditText mETLastName;
    private EditText mETEmail;
    private EditText mETGender;
    private TextView mProfileChange;
    private CompositeSubscription mSubscriptions;
    private SharedPreferences mSharedPreferences;
    private String mToken;
    private String mEmail;

    public static final String TAG = "ImageViewActivity";
    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    public static final int REQUEST_CODE_UPDATE_PIC = 0x1;


    private ImageView image;
    private Button mBSave;
    private Button mBcancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mSubscriptions = new CompositeSubscription();
        initSharedPreferences();
        initViews();
        loadProfile();
    }

    private void initSharedPreferences() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");

    }


    private void initViews() {

        mETFirstName = (EditText) findViewById(R.id.eTFirstName);
        mETLastName = (EditText) findViewById(R.id.eTLastName);
        mETEmail = (EditText) findViewById(R.id.eTEmail);
        mETGender = (EditText) findViewById(R.id.eTGender);
        mProfileChange = (TextView) findViewById(R.id.user_profile_change);
        image = (ImageView)findViewById(R.id.user_profile_photo);
        mBSave = (Button) findViewById(R.id.save_button);
        mBcancel = (Button) findViewById(R.id.cancel_button);

        mBSave.setOnClickListener(view -> saveB());
        mBcancel.setOnClickListener(view -> cancelB());
        mETGender.setOnClickListener(view -> genderViewClick());

        mProfileChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddProfilePicDialog();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == REQUEST_CODE_UPDATE_PIC) {
            if (resultCode == RESULT_OK) {
                String imagePath = result.getStringExtra(Constants.IntentExtras.IMAGE_PATH);
                showCroppedImage(imagePath);
            } else if (resultCode == RESULT_CANCELED) {

            } else {
                String errorMsg = result.getStringExtra(ImageCropActivity.ERROR_MSG);
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showCroppedImage(String mImagePath) {


        if (mImagePath != null) {
            Bitmap myBitmap = BitmapFactory.decodeFile(mImagePath);
            image.setImageBitmap(myBitmap);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                super.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showAddProfilePicDialog() {
        PicModeSelectDialogFragment dialogFragment = new PicModeSelectDialogFragment();
        dialogFragment.setiPicModeSelectListener(this);
        dialogFragment.show(getFragmentManager(), "picModeSelector");
    }

    private void actionProfilePic(String action) {
        Intent intent = new Intent(this, ImageCropActivity.class);
        intent.putExtra("ACTION", action);
        startActivityForResult(intent, REQUEST_CODE_UPDATE_PIC);
    }


    @Override
    public void onPicModeSelected(String mode) {
        String action = mode.equalsIgnoreCase(Constants.PicModes.CAMERA) ? Constants.IntentExtras.ACTION_CAMERA : Constants.IntentExtras.ACTION_GALLERY;
        actionProfilePic(action);
    }







    public void genderViewClick() {
        final String[] items = { "Male", "Female", "Not Specified" };
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                switch (items[item]) {
                    case "Male":
                        mETGender.setText(items[item]);
                        break;
                    case "Female":
                        mETGender.setText(items[item]);
                        break;
                    case "Not Specified":
                        mETGender.setText(items[item]);
                        break;
                }
            }
        });
        builder.show();
    }

    private void saveB(){

        setError();
        String first_name = mETFirstName.getText().toString();
        String last_name = mETLastName.getText().toString();
        String email = mETEmail.getText().toString();
        String gender = mETGender.getText().toString();

        int err = 0;
        if (!validateEmail(email)) {

            err++;
            mETEmail.setError("Email should be valid !");
        }

        if (err == 0) {
            mEmail = email;

            User user = new User();
            user.setFname(first_name);
            user.setLname(last_name);
            user.setEmail(email);
            user.setGender(gender);
            updateProfile(user);

        }
    }

    private void setError() {
        mETEmail.setError(null);

    }


    private void cancelB() {
        Intent intent = new Intent(this, NavBarActivity.class);
        startActivity(intent);
        finish();
    }

    private void updateProfile(User user) {
        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).updateProfile(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseUpdate,this::handleError));
    }

    private void handleResponseUpdate(Response response) {
        showSnackBarMessage(response.getMessage());

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(EMAIL,mEmail);
        editor.apply();

        Intent intent = new Intent(this, NavBarActivity.class);
        startActivity(intent);
        finish();
    }

    private void loadProfile() {
        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).getProfile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }
    private void handleResponse(User user) {
        mETFirstName.setText(user.getLname());
        mETLastName.setText(user.getFname());
        mETEmail.setText(user.getEmail());
        mETGender.setText(user.getGender());
    }

    private void handleError(Throwable error) {

        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                Response response = gson.fromJson(errorBody,Response.class);
                showSnackBarMessage(response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showSnackBarMessage("Network Error !");
        }
    }

    private void showSnackBarMessage(String message) {

        Snackbar.make(findViewById(R.id.edit_profile),message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }


}
