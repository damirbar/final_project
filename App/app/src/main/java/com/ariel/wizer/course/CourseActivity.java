package com.ariel.wizer.course;

import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;

import com.ariel.wizer.R;

public class CourseActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ImageButton buttonBack;
    private String cid;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);
        if (!getData()) {
            finish();
        }
        initViews();

        final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
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
        buttonBack = (ImageButton) findViewById(R.id.image_Button_back);
        buttonBack.setOnClickListener(view -> finish());
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addTab(tabLayout.newTab().setText("Course"));
        tabLayout.addTab(tabLayout.newTab().setText("Files"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
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


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_session, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


}
