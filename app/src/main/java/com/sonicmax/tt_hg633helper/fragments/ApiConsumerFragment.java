package com.sonicmax.tt_hg633helper.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.sonicmax.tt_hg633helper.network.ApiPoller;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ApiConsumerFragment extends Fragment {
    public JSONObject mApiData;
    public List<ApiPoller> mApiPollers;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mApiData = new JSONObject();
        mApiPollers = new ArrayList<>();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        stopPolling();
    }

    public void stopPolling(String endpoint) {
        for (ApiPoller apiPoller: mApiPollers) {
            if (apiPoller.getEndpoint().equals(endpoint)) {
                apiPoller.stopPolling();
            }
        }
    }

    public void stopPolling() {
        for (ApiPoller apiPoller: mApiPollers) {
            apiPoller.stopPolling();
        }
    }}
