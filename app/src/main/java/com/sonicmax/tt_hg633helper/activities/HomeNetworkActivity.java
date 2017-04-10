package com.sonicmax.tt_hg633helper.activities;

import android.os.Bundle;
import android.view.Menu;

import com.sonicmax.tt_hg633helper.R;

public class HomeNetworkActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_network);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_home_ntwk, menu);
        return true;
    }
}