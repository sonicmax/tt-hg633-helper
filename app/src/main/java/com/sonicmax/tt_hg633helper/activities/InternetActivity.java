package com.sonicmax.tt_hg633helper.activities;

import android.os.Bundle;
import android.view.Menu;

import com.sonicmax.tt_hg633helper.R;

public class InternetActivity extends BaseActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        // getMenuInflater().inflate(R.menu.menu_internet, menu);
        return true;
    }
}
