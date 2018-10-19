package com.wizeup.android.course;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baoyz.widget.PullRefreshLayout;
import com.wizeup.android.R;
import com.wizeup.android.model.Course;
import com.wizeup.android.network.RetrofitRequests;
import com.wizeup.android.network.ServerResponse;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class CourseInfoFragment extends Fragment {

    private RetrofitRequests mRetrofitRequests;
    private ServerResponse mServerResponse;
    private CompositeSubscription mSubscriptions;
    private PullRefreshLayout mSwipeRefreshLayout;
    private TextView mTCid;
    private TextView mTName;
    private TextView mTDepartment;
    private TextView mTTeacher;
    private TextView mTPoints;
    private TextView mTCreationDate;
    private TextView mTLoc;
    private TextView mTTeacherEmail;
    private TextView mTStudentsCount;

    private String cid;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_info, container, false);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this.getActivity());
        mServerResponse = new ServerResponse(view.findViewById(R.id.scroll));
        getData();
        initViews(view);

        mSwipeRefreshLayout.setColor(0);
        mSwipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            getCourse();
            mSwipeRefreshLayout.setRefreshing(false);
        }, 0));


        return view;
    }

    private void initViews(View v) {
        mSwipeRefreshLayout = v.findViewById(R.id.activity_main_swipe_refresh_layout);
        mTTeacherEmail = v.findViewById(R.id.tv_teacher_email);
        mTStudentsCount = v.findViewById(R.id.tv_students);
        mTCid = v.findViewById(R.id.tv_cid);
        mTName = v.findViewById(R.id.tv_name);
        mTDepartment = v.findViewById(R.id.tv_department);
        mTTeacher = v.findViewById(R.id.tv_teacher);
        mTPoints = v.findViewById(R.id.tv_points);
        mTCreationDate = v.findViewById(R.id.tv_creation_date);
        mTLoc = v.findViewById(R.id.tv_Loc);
        mTTeacherEmail.setOnClickListener(view -> sendMail());

    }

    private void sendMail() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                "mailto",mTTeacherEmail.getText().toString().trim(), null));
        startActivity(Intent.createChooser(emailIntent, "Send email..."));
    }

    private void getData() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            cid = bundle.getString("cid");
        }
    }


    private void getCourse() {
        mSubscriptions.add(mRetrofitRequests.getTokenRetrofit().getCourseById(cid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse, i -> mServerResponse.handleError(i)));
    }

    private void handleResponse(Course course) {
        mTCid.setText(course.getCid());
        mTName.setText(course.getName());
        mTDepartment.setText(course.getDepartment());
        String teacherFullName = course.getTeacher_fname() + " " + course.getTeacher_lname();
        mTTeacher.setText(teacherFullName);
        String points = Double.toString(course.getPoints());
        mTPoints.setText(points);
        mTLoc.setText(course.getLocation());
        mTTeacherEmail.setText(course.getTeacher_email());
        mTStudentsCount.setText(String.valueOf(course.getStudents().length -1));

        //Date
        Date date = course.getCreation_date();
        if (date != null) {
            Format formatter = new SimpleDateFormat("EEE, d MMM yyyy");
            String s = formatter.format(date);
            mTCreationDate.setText(s);
        }


    }

    @Override
    public void onResume() {
        super.onResume();
        getCourse();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mSubscriptions.unsubscribe();
    }


}