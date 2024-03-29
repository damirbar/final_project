package com.wizeup.android.course;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class CoursePagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private String cid;

    public CoursePagerAdapter(FragmentManager fm, int NumOfTabs, String _cid) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.cid = _cid;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
                CourseInfoFragment tab1 = new CourseInfoFragment();
                bundle.putString("cid", cid);
                tab1.setArguments(bundle);
                return tab1;
            case 1:
                CourseMessagesFragment tab2 = new CourseMessagesFragment();
                bundle.putString("cid", cid);
                tab2.setArguments(bundle);
                return tab2;
            case 2:
                SessionFeedFragment tab3 = new SessionFeedFragment();
                bundle.putString("cid", cid);
                tab3.setArguments(bundle);
                return tab3;
            case 3:
                CourseFilesFragment tab4 = new CourseFilesFragment();
                bundle.putString("cid", cid);
                tab4.setArguments(bundle);
                return tab4;

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return mNumOfTabs;
    }
}