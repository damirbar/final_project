package com.ariel.wizer.course;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.ariel.wizer.session.SessionFeedFragment;
import com.ariel.wizer.session.SessionInfoFragment;

public class CoursePagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private String sid;

    public CoursePagerAdapter(FragmentManager fm, int NumOfTabs, String _sid) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.sid = _sid;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
                CourseInfoFragment tab1 = new CourseInfoFragment();
                bundle.putString("sid", sid);
                tab1.setArguments(bundle);
                return tab1;
            case 1:
                CourseFilesFragment tab2 = new CourseFilesFragment();
                bundle.putString("sid", sid);
                tab2.setArguments(bundle);
                return tab2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}