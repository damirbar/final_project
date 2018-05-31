package com.ariel.wizer.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ariel.wizer.R;
import com.ariel.wizer.dialogs.MyDateDialog;
import com.ariel.wizer.imageCrop.ImageCropActivity;
import com.ariel.wizer.imageCrop.IntentExtras;
import com.ariel.wizer.imageCrop.PicModeSelectDialogFragment;
import com.ariel.wizer.imageCrop.PicModes;
import com.ariel.wizer.model.Response;
import com.ariel.wizer.model.User;
import com.ariel.wizer.network.RetrofitRequests;
import com.ariel.wizer.network.ServerResponse;
import com.ariel.wizer.utils.Constants;
import com.squareup.picasso.Picasso;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ariel.wizer.utils.Validation.validateFields;

public class EditProfileActivity extends AppCompatActivity implements MyDateDialog.OnCallbackReceived, PicModeSelectDialogFragment.IPicModeSelectListener {

    private EditText mETFirstName;
    private EditText mETLastName;
    private EditText mETDisplayName;
    private EditText mETCountry;
    private EditText mETAddress;
    private EditText mETAge;
    private EditText mETGender;
    private EditText mETAboutMe;
    private TextView mProfileChange;
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private String mId;
    private String mDisplayName;

    public static final String TEMP_PHOTO_FILE_NAME = "temp_photo.jpg";
    public static final int REQUEST_CODE_UPDATE_PIC = 0x1;
    public static final int REQUEST_CODE_UPDATE_BIO = 0x2;


    private ImageView image;
    private Button mBSave;
    private Button mBcancel;
    private Bitmap myBitmap;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.layout));
        initSharedPreferences();
        initViews();
        loadProfile();
        }

    private void initSharedPreferences() {
        mId = mRetrofitRequests.getmSharedPreferences().getString(Constants.ID,"");
    }

    private void initViews() {
        mETFirstName = (EditText) findViewById(R.id.eTFirstName);
        mETLastName = (EditText) findViewById(R.id.eTLastName);
        mETGender = (EditText) findViewById(R.id.eTGender);

        mETDisplayName = (EditText) findViewById(R.id.eTDisplay_Name);
        mETCountry = (EditText) findViewById(R.id.eTCountry);
        mETAddress = (EditText) findViewById(R.id.eTAddress);
        mETAge = (EditText) findViewById(R.id.eTAge);
        mETAboutMe = (EditText) findViewById(R.id.eTAboutMe);

        mProfileChange = (TextView) findViewById(R.id.user_profile_change);
        image = (ImageView)findViewById(R.id.user_profile_photo);
        mBSave = (Button) findViewById(R.id.save_button);
        mBcancel = (Button) findViewById(R.id.cancel_button);
        mBSave.setOnClickListener(view -> saveButton());
        mBcancel.setOnClickListener(view -> finish());
        mETGender.setOnClickListener(view -> genderViewClick());
        mProfileChange.setOnClickListener(view -> showAddProfilePicDialog());
        mETAge.setOnClickListener(view -> showDialog());

        mETAboutMe.setOnClickListener(view -> setBio());

    }

    private void setBio() {
        Intent i = new Intent(this, BioActivity.class);
        String bio = mETAboutMe.getText().toString().trim();
        Bundle extra = new Bundle();
        extra.putString("bio", bio);
        i.putExtras(extra);
        startActivityForResult(i, REQUEST_CODE_UPDATE_BIO);
    }

    private void showDialog() {
        MyDateDialog newFragment = new MyDateDialog();
        newFragment.show(getSupportFragmentManager(), MyDateDialog.TAG);
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
        if (requestCode == REQUEST_CODE_UPDATE_BIO) {
            if (resultCode == RESULT_OK) {
                Bundle extra = result.getExtras();
                String bio = extra.getString("bio");
                mETAboutMe.setText(bio);
            } else if (resultCode == RESULT_CANCELED) {

            } else {
                String errorMsg = result.getStringExtra(ImageCropActivity.ERROR_MSG);
                Toast.makeText(this, errorMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    private void showCroppedImage(String mImagePath) {
        if (mImagePath != null) {
            myBitmap = BitmapFactory.decodeFile(mImagePath);
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

    private void setError() {
        mETFirstName.setError(null);
        mETLastName.setError(null);
        mETGender.setError(null);
    }


    private void saveButton(){

        setError();


        String first_name = mETFirstName.getText().toString().trim();
        String last_name = mETLastName.getText().toString().trim();
        String gender = mETGender.getText().toString().trim();
        mDisplayName = mETDisplayName.getText().toString().trim();
        String country = mETCountry.getText().toString().trim();
        String TAddress = mETAddress.getText().toString().trim();
        String Age = mETAge.getText().toString().trim();
        String AboutMe = mETAboutMe.getText().toString().trim();


        int err = 0;

        if (!validateFields(first_name)) {

            err++;
            mETFirstName.setError("First Name should not be empty !");
        }

        if (!validateFields(last_name)) {

            err++;
            mETLastName.setError("Last Name should not be empty !");
        }

        if (gender.equalsIgnoreCase("Not Specified")) {

            err++;
            mETGender.setError("Gender should not be empty !");
        }


        if (err == 0) {


            User user = new User();
            user.setFname(first_name);
            user.setLname(last_name);
            user.setGender(gender);
            user.setDisplay_name(mDisplayName);
            user.setCountry(country);
            user.setAddress(TAddress);
            user.setAge(Integer.parseInt(Age));
            user.setAbout_me(AboutMe);

//        String g = getImageUri(this,myBitmap).toString();
//        String pic [] ={g};
//        user.setPhotos(pic);


            updateProfile(user);
        }

    }

//    public Uri getImageUri(Context inContext, Bitmap inImage) {
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
//        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
//        return Uri.parse(path);
//    }

    private void loadProfile() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getProfile(mId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseProfile,i -> mServerResponse.handleError(i)));
    }

    private void handleResponseProfile(User user) {
        mETFirstName.setText(user.getLname());
        mETLastName.setText(user.getFname());
        mETDisplayName.setText(user.getDisplay_name());
        mETCountry.setText(user.getCountry());
        mETAddress.setText(user.getAddress());
        mETAge.setText(String.valueOf(user.getAge()));
        mETAboutMe.setText(user.getAbout_me());

        String g = user.getGender();
        if(g == null || g.isEmpty()){
            mETGender.setText("Not Specified");
        }
        else
            mETGender.setText(user.getGender());

        String pic = user.getProfile_img();
        if(pic!=null&&!(pic.isEmpty()))
            Picasso.get().load(pic).into(image);


    }

    private void updateProfile(User user) {

        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().updateProfile(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseUpdate,i -> mServerResponse.handleError(i)));
    }

    private void handleResponseUpdate(Response response) {
        SharedPreferences.Editor editor = mRetrofitRequests.getmSharedPreferences().edit();
        editor.putString(Constants.USER_NAME,mDisplayName);
//        editor.putString(Constants.PROFILE_IMG,mEmail);
        editor.apply();
        finish();
        }

    public void Update(String date) {
        mETAge.setText(date);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();

    }

}
