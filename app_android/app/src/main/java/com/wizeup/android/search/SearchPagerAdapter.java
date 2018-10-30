package com.wizeup.android.search;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.wizeup.android.model.Searchable;

public class SearchPagerAdapter extends FragmentStatePagerAdapter {
    private int mNumOfTabs;
    private Searchable searchItems;

    public SearchPagerAdapter(FragmentManager fm, int NumOfTabs) {
        super(fm);
        this.mNumOfTabs = NumOfTabs;
        this.searchItems = new Searchable();
    }

    public Searchable getSearchItems() {
        return searchItems;
    }

    public void setSearchItems(Searchable searchItems) {
        this.searchItems = searchItems;
    }

    @Override
    public Fragment getItem(int position) {
        Bundle bundle = new Bundle();
        switch (position) {
            case 0:
                SearchCoursesFragment tab1 = new SearchCoursesFragment();
                bundle.putParcelableArrayList("courses", searchItems.getCourses());
                tab1.setArguments(bundle);
                return tab1;
            case 1:
                SearchFilesFragment tab2 = new SearchFilesFragment();
                bundle.putParcelableArrayList("files", searchItems.getFiles());
                tab2.setArguments(bundle);
                return tab2;
            case 2:
                SearchSessionsFragment tab3 = new SearchSessionsFragment();
                bundle.putParcelableArrayList("sessions", searchItems.getSessions());
                tab3.setArguments(bundle);
                return tab3;
            case 3:
                SearchUsersFragment tab4 = new SearchUsersFragment();
                bundle.putParcelableArrayList("users", searchItems.getUsers());
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

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}