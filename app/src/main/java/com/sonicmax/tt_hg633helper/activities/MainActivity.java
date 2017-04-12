package com.sonicmax.tt_hg633helper.activities;

import android.content.Intent;
import android.os.Bundle;

import com.sonicmax.tt_hg633helper.R;
import com.sonicmax.tt_hg633helper.services.HeartbeatManager;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start HeartbeatManager service to ensure that session token remains valid
        Intent intent = new Intent(Intent.ACTION_SYNC, null, this, HeartbeatManager.class);
        startService(intent);
    }
}
