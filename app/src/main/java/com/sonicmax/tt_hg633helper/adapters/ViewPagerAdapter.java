package com.sonicmax.tt_hg633helper.adapters;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sonicmax.tt_hg633helper.R;
import com.sonicmax.tt_hg633helper.fragments.LanDeviceFragment;
import com.sonicmax.tt_hg633helper.fragments.WifiDeviceFragment;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final String[] tabTitles = { "WIFI", "LAN" };
    private final Fragment[] fragments = { new WifiDeviceFragment(), new LanDeviceFragment() };

    private final Context mContext;

    public ViewPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.mContext = context;
    }

    public View getTabView(int position) {
        View customTab = LayoutInflater.from(mContext).inflate(R.layout.custom_tab, null);
        TextView tabTitle = (TextView) customTab.findViewById(R.id.custom_tab_title);
        tabTitle.setText(tabTitles[position]);
        return customTab;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
