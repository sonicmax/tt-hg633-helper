package com.sonicmax.tt_hg633helper.loaders;

import android.util.Log;

import java.util.Random;

/**
 * Provides each endpoint with a unique number to use as id in AsyncTaskLoader
 */

public class LoaderIdProvider {
    private final static String LOG_TAG = "LoaderIdProvider";

    private final static String HEARTBEAT = "heartbeat";
    private final static int HEARTBEAT_ID = 0;

    private final static String WLANBASIC = "WlanBasic";
    private final static int WLANBASIC_ID = 1;

    private final static String DIAGNOSE_INTERNET = "diagnose_internet";
    private final static int DIAGNOSE_INTERNET_ID = 2;

    private final static String WIZARD_WIFI = "wizard_wifi";
    private final static int WIZARD_WIFI_ID = 3;

    private final static String DEVICE_COUNT = "device_count";
    private final static int DEVICE_COUNT_ID = 4;

    private final static String DIAGNOSE_LAN = "diagnose_lan";
    private final static int DIAGNOSE_LAN_ID = 5;

    private final static String HOST_INFO = "HostInfo";
    private final static int HOST_INFO_ID = 6;

    private final static String HOST_INFO_WIRELESS = "HostInfo?devicetype=wireless";
    private final static int HOST_INFO_WIRELESS_ID = 7;

    /**
     * Provides each endpoint with a unique ID (for use in AsyncTaskLoader).
     * @param endpoint Last path segment of endpoint
     * @return int
     */

    public static int getIdForEndpoint(String endpoint) {
        switch(endpoint) {
            case HEARTBEAT:
                return HEARTBEAT_ID;

            case WLANBASIC:
                return WLANBASIC_ID;

            case DIAGNOSE_INTERNET:
                return DIAGNOSE_INTERNET_ID;

            case WIZARD_WIFI:
                return WIZARD_WIFI_ID;

            case DEVICE_COUNT:
                return DEVICE_COUNT_ID;

            case DIAGNOSE_LAN:
                return DIAGNOSE_LAN_ID;

            case HOST_INFO:
                return HOST_INFO_ID;

            case HOST_INFO_WIRELESS:
                return HOST_INFO_WIRELESS_ID;

            default:
                Log.e(LOG_TAG, "No id to return for endpoint \"" + endpoint + "\"");
                return new Random().nextInt(-2 + 1 + 30) - 30;
        }
    }

    /**
     * Returns endpoint from ID provided by getIdForEndpoint()
     * @param id
     * @return Last path segment of endpoint
     */

    public static String getEndpointFromId(int id) {
        switch(id) {
            case HEARTBEAT_ID:
                return HEARTBEAT;

            case WLANBASIC_ID:
                return WLANBASIC;

            case DIAGNOSE_INTERNET_ID:
                return DIAGNOSE_INTERNET;

            case WIZARD_WIFI_ID:
                return WIZARD_WIFI;

            case DEVICE_COUNT_ID:
                return DEVICE_COUNT;

            case DIAGNOSE_LAN_ID:
                return DIAGNOSE_LAN;

            case HOST_INFO_ID:
                return HOST_INFO;

            case HOST_INFO_WIRELESS_ID:
                return HOST_INFO_WIRELESS;

            default:
                Log.e(LOG_TAG, "No endpoint to return for id \"" + id + "\"");
                return "";
        }
    }

}
