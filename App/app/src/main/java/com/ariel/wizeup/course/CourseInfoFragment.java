package com.ariel.wizeup.course;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.Course;
import com.ariel.wizeup.network.RetrofitRequests;
import com.ariel.wizeup.network.ServerResponse;

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
    private TextView mTCid;
    private TextView mTName;
    private TextView mTDepartment;
    private TextView mTTeacher;
    private TextView mTPoints;
    private TextView mTCreationDate;
    private TextView mTLoc;
    private String cid;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_course_info, container, false);
        mSubscriptions = new CompositeSubscription();
        mRetrofitRequests = new RetrofitRequests(this.getActivity());
        mServerResponse = new ServerResponse(view.findViewById(R.id.scroll));
        getData();
        initViews(view);
        return view;
    }

    private void initViews(View v) {
        mTCid = v.findViewById(R.id.tv_cid);
        mTName = v.findViewById(R.id.tv_name);
        mTDepartment = v.findViewById(R.id.tv_department);
        mTTeacher = v.findViewById(R.id.tv_teacher);
        mTPoints = v.findViewById(R.id.tv_points);
        mTCreationDate = v.findViewById(R.id.tv_creation_date);
        mTLoc = v.findViewById(R.id.tv_Loc);

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
        String points = Integer.toString(course.getPoints());
        mTPoints.setText(points);
        mTLoc.setText(course.getLocation());

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