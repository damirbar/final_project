
package com.ariel.wizer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.ariel.wizer.session.ConnectSessionFragment;
import com.ariel.wizer.fragments.HomeFragment;
import com.ariel.wizer.fragments.ProfileFragment;
import com.ariel.wizer.fragments.SideMenuFragment;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;


public class NavBarActivity extends AppCompatActivity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_bar);

        BottomNavigationViewEx bottomNavigationView = (BottomNavigationViewEx) findViewById(R.id.navigation);

        bottomNavigationView.enableAnimation(false);
        bottomNavigationView.enableShiftingMode(false);
        bottomNavigationView.enableItemShiftingMode(false);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_item1:
                                selectedFragment = HomeFragment.newInstance();
                                break;
                            case R.id.action_item2:
                                selectedFragment = ConnectSessionFragment.newInstance();
                                break;
                            case R.id.action_item3:
                                selectedFragment = ProfileFragment.newInstance();
                                break;
                            case R.id.action_item4:
                                selectedFragment = SideMenuFragment.newInstance();
                                break;
                        }
                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.frame_layout, selectedFragment);
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, HomeFragment.newInstance());
        transaction.commit();

        //Used to select an item programmatically
        bottomNavigationView.getMenu().getItem(0).setChecked(true);
    }
}
