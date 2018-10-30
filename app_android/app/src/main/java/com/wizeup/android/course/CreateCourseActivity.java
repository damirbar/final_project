package com.wizeup.android.course;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.wizeup.android.R;
import com.wizeup.android.model.Course;
import com.wizeup.android.network.RetrofitRequests;
import com.wizeup.android.network.ServerResponse;
import com.wizeup.android.utils.Constants;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.wizeup.android.utils.Validation.validateEmail;
import static com.wizeup.android.utils.Validation.validateFields;

public class CreateCourseActivity extends AppCompatActivity {

    private EditText mEditTextName;
    private EditText mTeacherName;
    private EditText mEditTextLoc;
    private EditText mEditTextDepartment;
    private EditText mEditTextPoints;

    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_course);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this);
        mServerResponse = new ServerResponse(findViewById(R.id.scroll_view));
        initViews();
        initSharedPreferences();

    }

    private void initViews() {
        Button mBtLogin = findViewById(R.id.save_button);
        mEditTextName = findViewById(R.id.edit_text_name);
        mTeacherName = findViewById(R.id.edit_text_teacher_name);
        mEditTextLoc = findViewById(R.id.edit_text_loc);
        mEditTextDepartment = findViewById(R.id.edit_text_department);
        mEditTextPoints = findViewById(R.id.edit_text_points);


        Button buttonBack = findViewById(R.id.cancel_button);
        mBtLogin.setOnClickListener(view -> login());
        buttonBack.setOnClickListener(view -> finish());

    }

    private void initSharedPreferences() {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String email = mSharedPreferences.getString(Constants.EMAIL, "");
        mTeacherName.setText(email);
    }


    private void createCourse(Course course) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().createCourse(course)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseCreate, i -> mServerResponse.handleError(i)));
    }

    private void handleResponseCreate(Course course) {
        Intent intent = new Intent(getBaseContext(),CourseActivity.class);
        intent.putExtra("cid", course.getCid());
        startActivity(intent);
        finish();
    }



    private void login() {

        String loc = mEditTextLoc.getText().toString().trim();
        String name = mEditTextName.getText().toString().trim();
        String teacher = mTeacherName.getText().toString().trim();
        String department = mEditTextDepartment.getText().toString().trim();
        String points = mEditTextPoints.getText().toString().trim();



        if (!validateFields(name)) {
            mServerResponse.showSnackBarMessage("Name should not be empty.");
            return;

        }

        if (!validateEmail(teacher)) {
            mServerResponse.showSnackBarMessage(getString(R.string.email_should_be_valid));
            return;

        }

        if (!validateFields(loc)) {
            mServerResponse.showSnackBarMessage("Location should not be empty.");
            return;

        }
        if (!validateFields(department)) {
            mServerResponse.showSnackBarMessage("Department should not be empty.");
            return;

        }
        if (!validateFields(points)) {
            mServerResponse.showSnackBarMessage("Points should not be empty.");
            return;

        }


        Course course = new Course();
            course.setName(name);
            course.setLocation(loc);
            course.setTeacher(teacher);
            course.setDepartment(department);
            course.setPoints(Double.parseDouble(points));
            createCourse(course);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }



}
