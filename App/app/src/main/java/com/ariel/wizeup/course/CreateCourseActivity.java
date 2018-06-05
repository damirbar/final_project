package com.ariel.wizeup.course;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.Course;
import com.ariel.wizeup.model.Response;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static com.ariel.wizeup.utils.Validation.validateFields;

public class CreateCourseActivity extends AppCompatActivity {

    private String cid;///??
    private EditText mEditTextName;
    private EditText mTeacherName;
    private EditText mEditTextLoc;
    private TextInputLayout mTiName;
    private TextInputLayout mTiTeacher;
    private TextInputLayout mTiLoc;

    private Button mBtLogin;
    private CompositeSubscription mSubscriptions;
    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private Button buttonBack;
    private Course course;



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
        mTiName = findViewById(R.id.input_edit_text_name);
        mTiTeacher = findViewById(R.id.input_edit_text_teacher_name);
        mTiLoc  = findViewById(R.id.input_edit_text_loc);
        mBtLogin = findViewById(R.id.save_button);
        mEditTextName = findViewById(R.id.edit_text_name);
        mTeacherName = findViewById(R.id.edit_text_teacher_name);
        mEditTextLoc = findViewById(R.id.edit_text_loc);
        buttonBack = findViewById(R.id.cancel_button);
        mBtLogin.setOnClickListener(view -> login());
        buttonBack.setOnClickListener(view -> finish());

    }

    private void createCourse(Course course) {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().createCourse(course)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponseCreate, i -> mServerResponse.handleError(i)));
    }

    private void handleResponseCreate(Response response) {
        Intent intent = new Intent(getBaseContext(),CourseActivity.class);
        intent.putExtra("cid", cid);///???
        startActivity(intent);
        finish();
    }

    private void setError() {
        mTiName.setError(null);
        mTiTeacher.setError(null);
        mTiLoc.setError(null);
    }


    private void login() {

        setError();

        String loc = mEditTextLoc.getText().toString().trim();
        String name = mEditTextName.getText().toString().trim();
        String teacher = mTeacherName.getText().toString().trim();

        int err = 0;

        if (!validateFields(cid)) {
            err++;
            mTiName.setError("Course Name should not be empty.");
        }

        if (!validateFields(name)) {
            err++;
            mTiTeacher.setError("Teacher Name should not be empty.");
        }

        if (!validateFields(name)) {
            err++;
            mTiLoc.setError("Course Location should not be empty.");
        }

        if (err == 0) {

            Course course = new Course();
            course.setName(name);
            course.setLocation(loc);
            course.setTeacher_id(teacher);

            createCourse(course);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }



}
