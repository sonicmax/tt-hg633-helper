package com.sonicmax.tt_hg633helper.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.sonicmax.tt_hg633helper.R;
import com.sonicmax.tt_hg633helper.activities.HomeNetworkActivity;
import com.sonicmax.tt_hg633helper.activities.InternetActivity;
import com.sonicmax.tt_hg633helper.loaders.AccountManager;
import com.sonicmax.tt_hg633helper.loaders.ApiRequestHandler;
import com.sonicmax.tt_hg633helper.network.ApiPoller;
import com.sonicmax.tt_hg633helper.ui.ProgressDialogHandler;
import com.sonicmax.tt_hg633helper.utilities.CsrfHolder;
import com.sonicmax.tt_hg633helper.utilities.UptimeFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainFragment extends Fragment implements AccountManager.EventInterface, ApiRequestHandler.EventInterface {
    private final String LOG_TAG = this.getClass().getSimpleName();

    private final String WLAN_BASIC = "WlanBasic";
    private final String DIAGNOSE_INET = "diagnose_internet";
    private final String WIZARD_WIFI = "wizard_wifi";
    private final String DEVICE_COUNT = "device_count";

    private AccountManager mAccountManager;
    private ApiRequestHandler mApiRequestHandler;
    private JSONObject mApiData;
    private CsrfHolder mCsrfFields;
    private List<ApiPoller> mApiPollers;

    private TextView mUptimeView;
    private TextView mWifiDevicesView;
    private TextView mConnectedDevicesView;

    private Button mButtonInternet;
    private Button mButtonHomeNtwk;
    private Button mButtonSharing;
    private Button mButtonMaintain;
    private Button mButtonReset;
    private Button mButtonLogout;

    public MainFragment() {}

    ///////////////////////////////////////////////////////////////////////////
    // Fragment methods
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccountManager = new AccountManager(getContext(), this);
        mApiRequestHandler = new ApiRequestHandler(getContext(), this);
        mAccountManager.getCsrfFields();

        mApiPollers = new ArrayList<>();
        mApiPollers.add(new ApiPoller(mApiRequestHandler, WLAN_BASIC, 10000));
        mApiPollers.add(new ApiPoller(mApiRequestHandler, DIAGNOSE_INET, 10000));
        mApiPollers.add(new ApiPoller(mApiRequestHandler, WIZARD_WIFI, 10000));
        mApiPollers.add(new ApiPoller(mApiRequestHandler, DEVICE_COUNT, 5000));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        cacheViews(rootView);
        addListeners();
        return rootView;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        ProgressDialogHandler.dismissDialog();
        stopPolling();
    }

    ///////////////////////////////////////////////////////////////////////////
    // Interface methods
    ///////////////////////////////////////////////////////////////////////////

    @Override
    public void onLoadComplete(Object data, String endpoint) {
        if (data != null) {
            try {
                if (mApiData == null) {
                    mApiData = new JSONObject();
                }

                mApiData.put(endpoint, data);
                handleApiResponse(endpoint, data);

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error retrieving data from JSONObject", e);
                Log.d(LOG_TAG, "Endpoint: " + endpoint);

            } catch (NullPointerException e) {
                Log.e(LOG_TAG, "Data from ApiRequestHandler was null", e);
                Log.d(LOG_TAG, "Endpoint: " + endpoint);
            }

        } else {
            Log.d(LOG_TAG, "Stopping polling " + endpoint);
            stopPolling(endpoint);
        }
    }

    @Override
    public void onError(String endpoint) {
        Log.d(LOG_TAG, "Stopping polling " + endpoint);
        stopPolling(endpoint);
    }

    @Override
    public void onScrapeCsrf(CsrfHolder csrf) {
        mCsrfFields = csrf;
    }

    @Override
    public void onHaveIntent(Intent intent) {

    }

    ///////////////////////////////////////////////////////////////////////////
    // Helper methods
    ///////////////////////////////////////////////////////////////////////////

    private void stopPolling(String endpoint) {
        for (ApiPoller apiPoller: mApiPollers) {
            if (apiPoller.getEndpoint().equals(endpoint)) {
                apiPoller.stopPolling();
            }
        }
    }

    private void stopPolling() {
        for (ApiPoller apiPoller: mApiPollers) {
            apiPoller.stopPolling();
        }
    }

    private void cacheViews(View rootView) {
        mUptimeView = (TextView) rootView.findViewById(R.id.uptime_value);
        mWifiDevicesView = (TextView) rootView.findViewById(R.id.wifi_devices_value);
        mConnectedDevicesView = (TextView) rootView.findViewById(R.id.conn_devices_value);

        mButtonInternet = (Button) rootView.findViewById(R.id.menu_internet);
        mButtonHomeNtwk = (Button) rootView.findViewById(R.id.menu_home_ntwk);
        mButtonSharing = (Button) rootView.findViewById(R.id.menu_sharing);
        mButtonMaintain = (Button) rootView.findViewById(R.id.menu_maintain);
        mButtonReset = (Button) rootView.findViewById(R.id.menu_reboot);
        mButtonLogout = (Button) rootView.findViewById(R.id.menu_logout);
    }

    private void addListeners() {
        mButtonInternet.setOnClickListener(mClickHandler);
        mButtonHomeNtwk.setOnClickListener(mClickHandler);
        mButtonSharing.setOnClickListener(mClickHandler);
        mButtonMaintain.setOnClickListener(mClickHandler);
        mButtonReset.setOnClickListener(mClickHandler);
        mButtonLogout.setOnClickListener(mClickHandler);
    }

    private View.OnClickListener mClickHandler = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent;

            switch (view.getId()) {
                case R.id.menu_reboot:
                    rebootDevice();
                    break;

                case R.id.menu_internet:
                    intent = new Intent(getContext(), InternetActivity.class);
                    getContext().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                    break;

                case R.id.menu_home_ntwk:
                    intent = new Intent(getContext(), HomeNetworkActivity.class);
                    getContext().startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
                    break;

                default:
                    Log.d(LOG_TAG, "Click dispatched from view, but no handler exists. (" + view.getId() + ")");
                    break;
            }
        }
    };

    private void rebootDevice() {
        if (mCsrfFields != null) {
            try {
                JSONObject csrf = new JSONObject();
                csrf.put("csrf_param", mCsrfFields.getParam());
                csrf.put("csrf_token", mCsrfFields.getToken());

                JSONObject payload = new JSONObject();
                payload.put("csrf", csrf);

                ProgressDialogHandler.showDialog(getContext(), "Rebooting...");
                mApiRequestHandler.post("reboot.cgi", payload);

            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error creating payload for reboot.cgi", e);
                ProgressDialogHandler.dismissDialog();
            }
        }
    }

    private void handleApiResponse(String endpoint, Object data) throws JSONException {
        JSONObject json;
        JSONArray jsonArray;

        switch(endpoint) {
            case "reboot.cgi":
                // Poll heartbeat API until we get 404 response, and then redirect to login activity
                break;

            case "wizard_wifi":
                break;

            case "WlanBasic":
                break;

            case "diagnose_internet":
                json = (JSONObject) data;
                int uptime = json.getInt("Uptime");
                mUptimeView.setText(UptimeFormatter.getString(uptime));
                break;

            case "device_count":
                json = (JSONObject) data;
                int activeDeviceNumber = json.getInt("ActiveDeviceNumbers");
                int activeLanNumber = json.getInt("LanActiveNumber");
                mConnectedDevicesView.setText((Integer.toString(activeDeviceNumber)));
                mWifiDevicesView.setText((Integer.toString(activeDeviceNumber - activeLanNumber)));
                break;

            default:
                Log.d(LOG_TAG, "No corresponding case for endpoint \"" + endpoint + "\".");
                break;
        }
    }
}
