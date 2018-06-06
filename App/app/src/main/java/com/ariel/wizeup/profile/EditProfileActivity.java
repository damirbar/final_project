package com.ariel.wizeup.profile;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.ariel.wizeup.R;
import com.ariel.wizeup.dialogs.MyDateDialog;
import com.ariel.wizeup.imageCrop.ImageCropActivity;
import com.ariel.wizeup.imageCrop.IntentExtras;
import com.ariel.wizeup.imageCrop.PicModeSelectDialogFragment;
import com.ariel.wizeup.imageCrop.PicModes;
import com.ariel.wizeup.model.Response;
import com.ariel.wizeup.model.User;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;
import com.ariel.wizeup.utils.Constants;
import com.squareup.picasso.Picasso;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ariel.wizeup.network.RetrofitRequests.getBytes;
import static com.ariel.wizeup.utils.Validation.validateFields;

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
    private Bitmap myBitmap;
    private String imagePath;
    private int actions;
    private User startUser;
    private ScrollView layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.layout));
        initSharedPreferences();
        initViews();
        loadProfile();
    }

    private void initSharedPreferences() {
        mId = mRetrofitRequests.getSharedPreferences().getString(Constants.ID, "");
    }

    private void initViews() {
        layout = findViewById(R.id.scroll);
        layout.setVisibility(View.GONE);
        mETFirstName = (EditText) findViewById(R.id.eTFirstName);
        mETLastName = (EditText) findViewById(R.id.eTLastName);
        mETGender = (EditText) findViewById(R.id.eTGender);
        mETDisplayName = (EditText) findViewById(R.id.eTDisplay_Name);
        mETCountry = (EditText) findViewById(R.id.eTCountry);
        mETAddress = (EditText) findViewById(R.id.eTAddress);
        mETAge = (EditText) findViewById(R.id.eTAge);
        mETAboutMe = (EditText) findViewById(R.id.eTAboutMe);
        mProfileChange = (TextView) findViewById(R.id.user_profile_change);
        image = (ImageView) findViewById(R.id.user_profile_photo);
        Button mBSave = (Button) findViewById(R.id.save_button);
        Button mBCancel = (Button) findViewById(R.id.cancel_button);
        mBSave.setOnClickListener(view -> saveButton());
        mBCancel.setOnClickListener(view -> finish());
        mETGender.setOnClickListener(view -> genderViewClick());
        mProfileChange.setOnClickListener(view -> showAddProfilePicDialog());
        image.setOnClickListener(view -> showAddProfilePicDialog());
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
        String date = mETAge.getText().toString().trim();
        if (!(date.isEmpty())) {
            Bundle bundle = new Bundle();
            bundle.putString("date", mETAge.getText().toString().trim());
            newFragment.setArguments(bundle);
        }
        newFragment.show(getSupportFragmentManager(), MyDateDialog.TAG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
        if (requestCode == REQUEST_CODE_UPDATE_PIC) {
            if (resultCode == RESULT_OK) {
                imagePath = result.getStringExtra(IntentExtras.IMAGE_PATH);
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
        final String[] items = {"Male", "Female", "Not Specified"};
        AlertDialog.Builder builder = new AlertDialog.Builder(EditProfileActivity.this, R.style.Theme_Report_Dialog_Alert);
        builder.setTitle("choose gender");
        builder.setItems(items, (dialog, item) -> {
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
        });
        builder.show();
    }


    private void saveButton() {


        String first_name = mETFirstName.getText().toString().trim();
        String last_name = mETLastName.getText().toString().trim();
        String gender = mETGender.getText().toString().trim();
        mDisplayName = mETDisplayName.getText().toString().trim();
        String country = mETCountry.getText().toString().trim();
        String TAddress = mETAddress.getText().toString().trim();
        String Age = mETAge.getText().toString().trim();
        String AboutMe = mETAboutMe.getText().toString().trim();


        if (!validateFields(first_name)) {

            mServerResponse.showSnackBarMessage("First Name should not be empty.");
            return;
        }

        if (!validateFields(last_name)) {

            mServerResponse.showSnackBarMessage("Last Name should not be empty.");
            return;
        }

        if (gender.equalsIgnoreCase("Not Specified")) {

            mServerResponse.showSnackBarMessage("Gender should not be empty.");
            return;
        }

        User user = new User();
        user.setFname(first_name);
        user.setLname(last_name);
        user.setGender(gender);
        user.setDisplay_name(mDisplayName);
        user.setCountry(country);
        user.setAddress(TAddress);
        user.setAbout_me(AboutMe);


        DateFormat format = new SimpleDateFormat("d MMM yyyy");
        try {
            Date date = format.parse(Age);
            user.setBirthday(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        boolean newUser = false;
        if (!(startUser.equals(user))) {
            actions++;
            newUser = true;
        }


        if (imagePath != null && !(imagePath.isEmpty())) {
            actions++;
            try {
                InputStream is = new FileInputStream(imagePath);
                tryUploadPic(getBytes(is));
                is.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (newUser)
            updateProfile(user);

        if (actions == 0)
            finish();
    }


    private void loadProfile() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getProfile(mId)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseProfile, i -> mServerResponse.handleError(i)));
    }

    private void handleResponseProfile(User user) {

        mETFirstName.setText(user.getFname());
        mETLastName.setText(user.getLname());
        mETDisplayName.setText(user.getDisplay_name());
        mETCountry.setText(user.getCountry());
        mETAddress.setText(user.getAddress());
        mETAboutMe.setText(user.getAbout_me());


        //Date
        Date date = user.getBirthday();
        if (date != null) {
            Format formatter = new SimpleDateFormat("d MMM yyyy");
            String s = formatter.format(date);
            mETAge.setText(s);
        } else {
            Date now = new Date();
            Format formatterNow = new SimpleDateFormat("d MMM yyyy");
            String sNow = formatterNow.format(now);
            try {
                DateFormat format = new SimpleDateFormat("d MMM yyyy");
                now = format.parse(sNow);
                user.setBirthday(now);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        String g = user.getGender();
        if (g == null || g.isEmpty()) {
            mETGender.setText("Not Specified");
        } else
            mETGender.setText(user.getGender());

        String pic = user.getProfile_img();
        if (pic != null && !(pic.isEmpty()))
            Picasso.get()
                    .load(pic)
                    .error(R.drawable.default_user_image)
                    .into(image);

        layout.setVisibility(View.VISIBLE);
        startUser = user;
    }

    private void updateProfile(User user) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().updateProfile(user)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseUpdate, i -> mServerResponse.handleError(i)));
    }

    private void handleResponseUpdate(Response response) {
        if (actions - 1 == 0) {
            finish();
        } else
            actions -= 1;
    }

    private void tryUploadPic(byte[] bytes) {
        String filename = imagePath.substring(imagePath.lastIndexOf("/") + 1);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), bytes);
        MultipartBody.Part body = MultipartBody.Part.createFormData("recfile", filename, requestFile);
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().uploadProfileImage(body)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseUpdate, i -> mServerResponse.handleError(i)));
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
