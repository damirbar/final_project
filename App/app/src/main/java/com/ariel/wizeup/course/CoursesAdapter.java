package com.ariel.wizeup.course;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ariel.wizeup.R;
import com.ariel.wizeup.model.Course;

import java.util.ArrayList;
import java.util.List;

public class CoursesAdapter extends ArrayAdapter<Course> {
    private Context mContext;
    private List<Course> coursesList;

    public CoursesAdapter(Activity context, ArrayList<Course> list) {
        super(context, 0, list);
        mContext = context;
        coursesList = list;
    }

    public List<Course> getCoursesList() {
        return coursesList;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        Course currentCourse = coursesList.get(position);

        if (convertView == null) {

            listItem = LayoutInflater.from(mContext).inflate(R.layout.feed_course_item, parent, false);

        }
        TextView mCourseName = listItem.findViewById(R.id.course_name);
        TextView mTeacherName = listItem.findViewById(R.id.techer_name);

        mCourseName.setText(currentCourse.getName());
        String teacher = currentCourse.getTeacher_fname()+" "+currentCourse.getTeacher_lname();
        mTeacherName.setText(teacher);


        return listItem;
    }
}
