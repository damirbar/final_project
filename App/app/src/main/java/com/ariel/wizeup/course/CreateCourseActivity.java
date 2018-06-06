package com.ariel.wizeup.course;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.Course;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ariel.wizeup.utils.Validation.validateFields;

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

        if (!validateFields(teacher)) {
            mServerResponse.showSnackBarMessage("Teacher Name should not be empty.");
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
            course.setPoints(points);
            createCourse(course);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }



}
