package com.sonicmax.tt_hg633helper.activities;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;

import com.sonicmax.tt_hg633helper.R;
import com.sonicmax.tt_hg633helper.adapters.ViewPagerAdapter;
import com.sonicmax.tt_hg633helper.ui.ProgressDialogHandler;

/**
 * Creates sliding tab layout to display devices connected to the router.
 */

public class HomeNetworkActivity extends BaseActivity {
    private final String LOG_TAG = this.getClass().getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home_network);

        ProgressDialogHandler.showDialog(this, "Loading...");

        // Prepare ViewPager and TabLayout to display device tabs
        ViewPager viewPager = (ViewPager) findViewById(R.id.home_network_viewpager);
        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), HomeNetworkActivity.this);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.home_network_tabs);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            tab.setCustomView(pagerAdapter.getTabView(i));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_ntwk, menu);
        return true;
    }
}