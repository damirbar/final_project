package com.wizeup.android.session;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.wizeup.android.R;
import com.wizeup.android.model.CourseFile;

import java.util.ArrayList;
import java.util.List;

public class UploadCourseFilesAdapter extends ArrayAdapter<CourseFile> {
    private Context mContext;
    private List<CourseFile> filesList;


    public UploadCourseFilesAdapter(Activity context, ArrayList<CourseFile> list) {
        super(context, 0, list);
        mContext = context;
        filesList = list;
    }

    public List<CourseFile> getCoursesList() {
        return filesList;
    }

    public class MyHolder {
        TextView mFileName;
        TextView progressInfo;
        ProgressBar progressBar;
        ImageButton mCancel;


        public MyHolder(View v) {
            mFileName = v.findViewById(R.id.file_name);
            progressInfo = v.findViewById(R.id.textViewProgress);
            progressBar = v.findViewById(R.id.progressBar);
            mCancel = v.findViewById(R.id.cancel);
        }
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        MyHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.upload_item_file, parent, false);
            holder = new MyHolder(view);
            view.setTag(holder);
            holder.progressBar.setTag(holder);
            holder.progressInfo.setTag(holder);

        } else {
            holder = (MyHolder) view.getTag();
        }

        holder.mFileName.setText(filesList.get(position).getName());

        holder.progressBar.setIndeterminate(true);
        holder.progressBar.getIndeterminateDrawable().setColorFilter(
                Color.RED, PorterDuff.Mode.SRC_IN);

        holder.mCancel.setOnClickListener(v -> {
            ((ListView) parent).performItemClick(v, position, 0); // Let the event be handled in onItemClick()
        });



        return view;
    }


}


