package com.ariel.wizer.session;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class SessionPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private String sid;

    public SessionPagerAdapter(FragmentManager fm, int NumOfTabs, String _sid) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.sid = _sid;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
                SessionInfoFragment tab1 = new SessionInfoFragment();
                bundle.putString("sid", sid);
                tab1.setArguments(bundle);
                return tab1;
            case 1:
                SessionFeedFragment tab2 = new SessionFeedFragment();
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