package com.wizeup.android.settings;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.wizeup.android.R;
import com.wizeup.android.model.Language;

import java.util.ArrayList;
import java.util.List;

public class LanguageAdapter extends ArrayAdapter<Language> {
    private Context mContext;
    private List<Language> langList;


    public LanguageAdapter(Activity context, ArrayList<Language> list) {
        super(context, 0, list);
        mContext = context;
        langList = list;
    }

    public class MyHolder {
        TextView mOrgName;
        TextView mName;


        public MyHolder(View v) {

            mOrgName = v.findViewById(R.id.orgNameTxt);
            mName = v.findViewById(R.id.nameTxt);

        }
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        Language currentLang = langList.get(position);


        MyHolder holder;
        if (view == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.lang_item, parent, false);
            holder = new MyHolder(view);
            view.setTag(holder);

        } else {
            holder = (MyHolder) view.getTag();

        }

        holder.mOrgName.setText(currentLang.getOriginalName());
        holder.mName.setText(currentLang.getName());




        return view;
    }
}

