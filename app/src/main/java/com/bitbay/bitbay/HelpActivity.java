package com.bitbay.bitbay;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.app.Fragment;

//import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;


public class HelpActivity extends AppCompatActivity {
    private static final String TAG = HelpActivity.class.getSimpleName();
    private BottomNavigationView bottomNavigation;
    private Fragment fragment;
    private FragmentManager fragmentManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        bottomNavigation = (BottomNavigationView)findViewById(R.id.bottom_help_navigation);
        fragmentManager = getSupportFragmentManager();
        bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch (id){
                    case R.id.orders:
                        fragment = new OrdersFragment();
                        break;
                    case R.id.payments:
                        fragment = new PaymentFragment();
                        break;
                    case R.id.delivery:
                        fragment = new DeliveryFragment();
                        break;
                    case R.id.returns:
                        fragment = new ReturnsFragment();
                        break;
                }
                android.app.FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.help_fragment, fragment).commit();
                return true;
            }
        });
    }

}
