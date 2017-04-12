package com.sonicmax.tt_hg633helper.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.sonicmax.tt_hg633helper.network.ApiPathManager;
import com.sonicmax.tt_hg633helper.network.WebRequest;
import com.sonicmax.tt_hg633helper.utilities.JsonDataFilter;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Makes regular requests to the heartbeat API (required for session token to remain valid).
 * Should be destroyed when app is not running
 */

public class HeartbeatManager extends IntentService {
    private final String LOG_TAG = this.getClass().getSimpleName();
    private final String HEARTBEAT = "heartbeat";
    private final String INTERVAL = "interval";
    private final int ERROR = -1;

    private final String mHostName;
    private final String mHeartbeatEndpoint;

    private boolean mShouldPoll = true;

    public HeartbeatManager() {
        super(HeartbeatManager.class.getName());
        mHostName = "http://192.168.1.1";
        mHeartbeatEndpoint = mHostName + ApiPathManager.getFullPath(HEARTBEAT);
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        heartbeat();
    }

    private void heartbeat() {
        String response = new WebRequest(this).get(mHeartbeatEndpoint);
        int interval = getIntervalFromResponse(response);

        if (interval == ERROR) {
            this.stopSelf();
        }

        else {
            new Handler().postDelayed(new Runnable() {
                public void run() {

                    if (mShouldPoll) {
                        heartbeat();
                    }

                }

            }, interval);
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
