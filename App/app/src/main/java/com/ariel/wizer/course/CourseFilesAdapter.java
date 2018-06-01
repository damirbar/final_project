package com.ariel.wizer.course;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.ariel.wizer.R;
import com.ariel.wizer.model.CourseFile;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CourseFilesAdapter extends ArrayAdapter<CourseFile> {
    private Context mContext;
    private List<CourseFile> filesList;

    public CourseFilesAdapter(Activity context, ArrayList<CourseFile> list) {
        super(context, 0, list);
        mContext = context;
        filesList = list;
    }

    public List<CourseFile> getCoursesList() {
        return filesList;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        CourseFile currentFile = filesList.get(position);

        if (convertView == null) {

            listItem = LayoutInflater.from(mContext).inflate(R.layout.search_item_file, parent, false);

        }
        TextView mFileName = (TextView) listItem.findViewById(R.id.file_name);
        TextView mFileDatee = (TextView) listItem.findViewById(R.id.creation_date);

        mFileName.setText(currentFile.getName());

        //Date
        Date date = currentFile.getCreation_date();
        if(date!=null){
            Format formatter = new SimpleDateFormat("dd/MM/yyyy");
            String s = formatter.format(date);
            mFileDatee.setText(s);
        }


        return listItem;
    }
}
