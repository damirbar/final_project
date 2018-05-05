package com.ariel.wizer;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ariel.wizer.imageCrop.ImageCropActivity;
import com.ariel.wizer.imageCrop.IntentExtras;
import com.ariel.wizer.imageCrop.PicModeSelectDialogFragment;
import com.ariel.wizer.imageCrop.PicModes;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.model.User;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;
import com.ariel.wizer.utils.Constants;

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
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private String mEmail;

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
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.edit_profile));
        initSharedPreferences();
        initViews();
        loadProfile();
    }

    private void initSharedPreferences() {
        mEmail = mRetrofitRequests.getmSharedPreferences().getString(Constants.EMAIL,"");
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
        mBSave.setOnClickListener(view -> saveButton());
        mBcancel.setOnClickListener(view -> cancelButton());
        mETGender.setOnClickListener(view -> genderViewClick());
        mProfileChange.setOnClickListener(view -> showAddProfilePicDialog());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == REQUEST_CODE_UPDATE_PIC) {
            if (resultCode == RESULT_OK) {
                String imagePath = result.getStringExtra(IntentExtras.IMAGE_PATH);
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
        String action = mode.equalsIgnoreCase(PicModes.CAMERA) ? IntentExtras.ACTION_CAMERA : IntentExtras.ACTION_GALLERY;
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

    private void saveButton(){
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

    private void cancelButton() {
        Intent intent = new Intent(this, NavBarActivity.class);
        startActivity(intent);
        finish();
    }

    private void setError() {
        mETEmail.setError(null);
    }

    private void loadProfile() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getProfile()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,i -> mServerResponse.handleError(i)));
    }
    private void handleResponse(User user) {
        mETFirstName.setText(user.getLname());
        mETLastName.setText(user.getFname());
        mETEmail.setText(user.getEmail());
        mETGender.setText(user.getGender());
    }

    private void updateProfile(User user) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().updateProfile(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseUpdate,i -> mServerResponse.handleError(i)));
    }

    private void handleResponseUpdate(Response response) {
        SharedPreferences.Editor editor = mRetrofitRequests.getmSharedPreferences().edit();
        editor.putString(EMAIL,mEmail);
        editor.apply();
        Intent intent = new Intent(this, NavBarActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }
}
