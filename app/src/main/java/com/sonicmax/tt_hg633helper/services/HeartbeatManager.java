package com.sonicmax.tt_hg633helper.services;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

import com.sonicmax.tt_hg633helper.network.ApiPathManager;
import com.sonicmax.tt_hg633helper.network.WebRequest;
import com.sonicmax.tt_hg633helper.utilities.JsonDataFilter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Makes regular requests to the heartbeat API (required for session token to remain valid).
 * TODO: Needs to be stopped when app is not running
 */
public class HeartbeatManager extends Service {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private final String HEARTBEAT = "heartbeat";
    private final String INTERVAL = "interval";
    private final int ERROR = -1;

    private final String mHostName;
    private final String mHeartbeatEndpoint;
    private final WebRequest mWebRequest;

    private Looper mServiceLooper;
    private Handler mServiceHandler;

    public HeartbeatManager() {
        super();

        mHostName = "http://192.168.1.1";
        mHeartbeatEndpoint = mHostName + ApiPathManager.getFullPath(HEARTBEAT);
        mWebRequest = new WebRequest(this);
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread(LOG_TAG, Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        // start the service using the background handler
        mServiceHandler = new Handler(mServiceLooper) {

            @Override
            public void handleMessage(Message msg) {
                heartbeat();
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();

        Message message = mServiceHandler.obtainMessage();
        mServiceHandler.sendMessage(message);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void heartbeat() {
        String response = mWebRequest.get(mHeartbeatEndpoint);

        if (response != null) {
            int interval = getIntervalFromResponse(response);

            if (interval == ERROR) {
                this.stopSelf();

            } else {
                new Handler().postDelayed(new Runnable() {

                    public void run() {
                        heartbeat();
                    }

                }, interval);
            }
        }

        else {
            // Null response means session token was invalidated (or we lost connection)
            this.stopSelf();
        }
    }

    /**
     * Removes JSON hijacking protection and returns interval from response.
     * @param response
     * @return
     */
    private int getIntervalFromResponse(String response) {
        try {
            JSONObject json = JsonDataFilter.getDataAsJson(response);
            return json.getInt(INTERVAL);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error while parsing heartbeat response", e);
            return ERROR;
        }
    }
}
