package com.wizeup.android.course;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.wizeup.android.R;
import com.wizeup.android.utils.Constants;

public class CourseActivity extends AppCompatActivity {

    private TabLayout tabLayout;
    private String cid;
    private String userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        if (!getData()) {
            finish();
        }
        initViews();
        initSharedPreferences();


        final ViewPager viewPager = findViewById(R.id.pager);
        final CoursePagerAdapter adapter = new CoursePagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount(),cid);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void initViews() {
        ImageButton buttonBack = findViewById(R.id.image_Button_back);
        buttonBack.setOnClickListener(view -> finish());
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Course"));
        tabLayout.addTab(tabLayout.newTab().setText("Updates"));
        tabLayout.addTab(tabLayout.newTab().setText("Sessions"));
        tabLayout.addTab(tabLayout.newTab().setText("Files"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
    }

    private void initSharedPreferences() {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        userType = mSharedPreferences.getString(Constants.TYPE, "");
    }


    private boolean getData() {
        if (getIntent().getExtras() != null) {
            String _cid = getIntent().getExtras().getString("cid");
            if(_cid != null) {
                cid = _cid;
                return true;
            } else
                return false;
        }
        else
            return false;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(userType.equalsIgnoreCase("teacher")){
            getMenuInflater().inflate(R.menu.menu_course, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.add_user) {
            openAddUser();
            return true;

        }
        return super.onOptionsItemSelected(item);
    }


    private void openAddUser() {
        Intent intent = new Intent(this,AddUserActivity.class);
        intent.putExtra("cid", cid);
        startActivity(intent);
    }



}
