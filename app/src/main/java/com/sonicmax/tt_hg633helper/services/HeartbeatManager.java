package com.sonicmax.tt_hg633helper.services;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.Process;
import android.util.Log;

import com.sonicmax.tt_hg633helper.network.ApiPathManager;
import com.sonicmax.tt_hg633helper.network.WebRequest;
import com.sonicmax.tt_hg633helper.utilities.JsonDataFilter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Makes regular requests to the triggerHeartbeat API (required for session token to remain valid).
 */
public class HeartbeatManager extends Service {
    private final String LOG_TAG = this.getClass().getSimpleName();

    // Codes for ServiceHandler
    private final int CLOSE = 1;
    private final int KEEP_OPEN = 2;

    // Error code for getIntervalFromResponse()
    private final int JSON_ERROR = -1;

    private String mHeartbeatEndpoint;
    private WebRequest mWebRequest;
    private Messenger mMessenger;
    private Looper mServiceLooper;
    private Handler mServiceHandler;
    private Handler mDelayedExitHandler;
    private boolean mShouldRun;
    private boolean mHasStarted = false;

    public HeartbeatManager() {
        super();
        final String HEARTBEAT = "heartbeat";
        String hostName = "http://192.168.1.1";
        mHeartbeatEndpoint = hostName + ApiPathManager.getFullPath(HEARTBEAT);
        mWebRequest = new WebRequest(this);
        mShouldRun = true;
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread(LOG_TAG, Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mDelayedExitHandler = new Handler();
        mServiceHandler = new Handler(mServiceLooper) {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.getData().getInt("code")) {
                    case CLOSE:
                        triggerDelayedStop();
                        break;

                    case KEEP_OPEN:
                        mDelayedExitHandler.removeCallbacksAndMessages(null);
                        break;
                }

                if (!mHasStarted) {
                    triggerHeartbeat();
                    mHasStarted = true;
                }
            }
        };

        mMessenger = new Messenger(mServiceHandler);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Message message = mServiceHandler.obtainMessage();
        mServiceHandler.sendMessage(message);

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mMessenger.getBinder();
    }

    private void triggerHeartbeat() {
        String response = mWebRequest.get(mHeartbeatEndpoint);

        if (response != null) {
            int interval = getIntervalFromResponse(response);

            if (interval == JSON_ERROR) {
                // Maybe the firmware was updated
                stopSelf();

            } else {

                if (mShouldRun) {
                    new Handler().postDelayed(new Runnable() {

                        public void run() {
                            triggerHeartbeat();
                        }

                    }, interval);
                }
            }
        }

        else {
            // Null response means session token was invalidated (or we lost connection)
            this.stopSelf();
        }
    }

    private void triggerDelayedStop() {
        final int TWO_SECONDS = 2000;

        mDelayedExitHandler.postDelayed(new Runnable() {

            public void run() {
                mShouldRun = false;
                stopSelf();
            }

        }, TWO_SECONDS);
    }

    /**
     * Removes JSON hijacking protection and returns interval from response.
     * @param response
     * @return
     */
    private int getIntervalFromResponse(String response) {
        final String INTERVAL = "interval";

        try {
            JSONObject json = JsonDataFilter.getDataAsJson(response);
            return json.getInt(INTERVAL);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error while parsing heartbeat response", e);
            return JSON_ERROR;
        }
    }
}
