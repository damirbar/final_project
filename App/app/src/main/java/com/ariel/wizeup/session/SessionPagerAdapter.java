package com.ariel.wizeup.session;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class SessionPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private String sid;
    private String admin;

    public SessionPagerAdapter(FragmentManager fm, int NumOfTabs, String _sid, String _admin) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.sid = _sid;
        this.admin = admin;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
                SessionInfoFragment tab1 = new SessionInfoFragment();
                bundle.putString("sid", sid);
                bundle.putString("admin", admin);
                tab1.setArguments(bundle);
                return tab1;
            case 1:
                SessionMessagesFragment tab2 = new SessionMessagesFragment();
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