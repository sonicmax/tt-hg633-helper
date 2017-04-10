package com.sonicmax.tt_hg633helper.network;

import android.os.Handler;

import com.sonicmax.tt_hg633helper.loaders.ApiRequestHandler;

public class ApiPoller {
    private final ApiRequestHandler mApiRequestHandler;
    private final String mEndpoint;
    private final int mInterval;

    private boolean mShouldPoll = true;

    public ApiPoller(ApiRequestHandler apiRequestHandler, String endpoint, int interval) {
        mApiRequestHandler = apiRequestHandler;
        mEndpoint = endpoint;
        mInterval = interval;

        mApiRequestHandler.get(endpoint);
        pollApi();
    }

    public String getEndpoint() {
        return mEndpoint;
    }

    public void stopPolling() {
        mShouldPoll = false;
    }

    private void pollApi() {
        new Handler().postDelayed(new Runnable() {
            public void run() {

                if (mShouldPoll) {
                    mApiRequestHandler.get(mEndpoint);
                    pollApi();
                }

            }

        }, mInterval);
    }
}
