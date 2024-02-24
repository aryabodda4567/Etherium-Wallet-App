package com.android.cryptpay;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;


import com.android.cryptpay.eth.CreateAccountActivity;
import com.google.android.material.tabs.TabLayout;


public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TabLayout tabLayout = findViewById(R.id.tabLayout);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);


        //Get eth address
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext());
        String ethVal = getApplicationContext().getString(R.string.eth_address);
        String ethAddress = sharedPreferenceManager.getStringValue(ethVal, null);

        //Create account if not have one
        if (ethAddress == null) {

            startActivity(new Intent(getApplicationContext(), CreateAccountActivity.class));
            overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
            finish();
        }




        replaceFragment(new HomeFragment());
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Handle tab selection
                switch (tab.getPosition()) {
                    case 0:
                        // Handle Home tab selection
                        replaceFragment(new HomeFragment());
                        break;
                    case 1:
                        // Handle Balance tab selection
                        replaceFragment(new HistoryFragment());
                        break;
                    case 2:
                        // Handle History tab selection
                        replaceFragment(new SettingsFragment());
                        break;

                    default:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                // Do nothing on tab Un selection
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                // Do nothing on tab reselection
            }
        });
    }

    // Method to replace fragment
    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragmentContainer, fragment)
                .commit();
    }


}
