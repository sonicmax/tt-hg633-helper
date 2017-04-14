package com.sonicmax.tt_hg633helper.activities;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.sonicmax.tt_hg633helper.R;
import com.sonicmax.tt_hg633helper.services.HeartbeatManager;
import com.sonicmax.tt_hg633helper.utilities.SharedPreferenceManager;

import java.util.Date;

public class BaseActivity extends AppCompatActivity {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private final int OPEN = 1;
    private final int CLOSED = 2;
    private final int START = 3;

    private ServiceConnection mServiceConnection;
    private Messenger mMessenger;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkReferenceTimestamp();
        initServiceConnection();
        startHeartbeat();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!serviceIsRunning(HeartbeatManager.class)) {
            initServiceConnection();
            startHeartbeat();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (serviceIsRunning(HeartbeatManager.class)) {
            unbindFromServices();
        }
    }

    private void initServiceConnection() {
        mServiceConnection = new ServiceConnection() {
            public void onServiceConnected(ComponentName className, IBinder iBinder) {
                mMessenger = new Messenger(iBinder);
                sendStatusCodeToService(START);
                sendStatusCodeToService(OPEN);
            }

            public void onServiceDisconnected(ComponentName className) {}
        };
    }

    /**
     * Tell Service that Activity has been paused, and then call unbindService().
     * If this was the first instance of BaseActivity, then Service will destroy itself.
     */
    private void unbindFromServices() {
        sendStatusCodeToService(CLOSED);

        try {
            unbindService(mServiceConnection);
        } catch (IllegalArgumentException e) {
            Log.d(LOG_TAG, "Error unbinding from service", e);
        }
    }

    /**
     * Start HeartbeatManager (if it isn't already running) and bind it to BaseActivity
     */
    public void startHeartbeat() {
        if (!serviceIsRunning(HeartbeatManager.class)) {
            Intent intent = new Intent(Intent.ACTION_SYNC, null, this, HeartbeatManager.class);
            startService(intent);
        }

        bindService(new Intent(this, HeartbeatManager.class), mServiceConnection, Context.BIND_AUTO_CREATE);
    }

    private void sendStatusCodeToService(int code) {
        Bundle bundle = new Bundle();
        bundle.putInt("code", code);
        Message message = Message.obtain();
        message.setData(bundle);

        try {
            mMessenger.send(message);
        } catch (RemoteException e) {
            Log.e(LOG_TAG, "Error communicating with service", e);
        }
    }

    public boolean serviceIsRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks for existence of reference_timestamp in SharedPreferences and sets value if required.
     */
    private void checkReferenceTimestamp() {

        // Note: Required for MPAndroidChart library to work correctly with UNIX timestamps. We use
        // the result of (currentTime - referenceTime) as the x axis value, and account for this
        // in TimestampAxisValueFormatter when formatting the date string to display.

        if (SharedPreferenceManager.getLong(this, "reference_timestamp") == -1) {
            SharedPreferenceManager.putLong(this, "reference_timestamp", new Date().getTime());
        }
    }

    @Override
    public void onBackPressed() {
        // Going "backwards" in app slides screen from left to right.
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
