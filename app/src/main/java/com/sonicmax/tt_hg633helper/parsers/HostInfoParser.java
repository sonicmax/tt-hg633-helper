package com.sonicmax.tt_hg633helper.parsers;

import android.content.Context;
import android.util.Log;

import com.sonicmax.tt_hg633helper.database.DeviceDataProvider;
import com.sonicmax.tt_hg633helper.utilities.SharedPreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Provides various methods to manipulate the JSON response from HostInfo endpoint.
 */

public class HostInfoParser {
    private static String LOG_TAG = "HostInfoParser";
    private static String MAC_ADDRESS = "MACAddress";
    private static String RSSI = "Rssi";
    private static String DEVICE_RATE = "AssociatedDeviceRate";
    private static String LAYER_2_INTERFACE = "Layer2Interface";
    private static String HG633_LAN_RATE = "100 Mbps";

    public static JSONArray getLanDeviceInfo(JSONArray devices) throws JSONException {
        JSONArray lanDevices = new JSONArray();

        for (int i = 0; i < devices.length(); i++) {
            JSONObject device = devices.getJSONObject(i);
            if (device.getString(LAYER_2_INTERFACE).contains("LAN")) {
                lanDevices.put(device);
            }
        }

        return lanDevices;
    }

    public static JSONArray getWirelessDeviceInfo(JSONArray devices) throws JSONException {
        JSONArray wirelessDevices = new JSONArray();

        for (int i = 0; i < devices.length(); i++) {
            JSONObject device = devices.getJSONObject(i);
            if (device.getString(LAYER_2_INTERFACE).contains("SSID")) {
                wirelessDevices.put(device);
            }
        }

        return wirelessDevices;
    }

    public static List<JSONObject> convertToList(JSONArray devices) throws JSONException {
        List<JSONObject> deviceList = new ArrayList<>();

        for (int i = 0; i < devices.length(); i++) {
            deviceList.add(devices.getJSONObject(i));
        }

        return sortByLeaseTime(deviceList);
    }

    /**
     * Sorts HostInfo device info by lease time (highest first).
     * @param devices List of JSONObjects from HostInfo API
     * @return Sorted List
     * @throws JSONException
     */

    public static List<JSONObject> sortByLeaseTime(List<JSONObject> devices) throws JSONException {
        final String LEASETIME = "LeaseTime";

        Collections.sort(devices, new Comparator<JSONObject>(){

            @Override
            public int compare(JSONObject a, JSONObject b){

                try {
                    return b.getInt(LEASETIME) - a.getInt(LEASETIME);

                } catch (JSONException e) {
                    Log.e(LOG_TAG, "Error while comparing devices", e.getCause());
                    return 0;
                }
            }
        });

        return devices;
    }

    public static List<JSONObject> getActiveDevices(JSONArray devices) throws JSONException {
        List<JSONObject> activeDevices = new ArrayList<>();

        for (int i = 0; i < devices.length(); i++) {
            JSONObject device = devices.getJSONObject(i);
            if (device.getBoolean("Active")) {
                activeDevices.add(device);
            }
        }

        return activeDevices;
    }

    public static List<JSONObject> convertJSONArrayToList(JSONArray devices) throws JSONException {
        List<JSONObject> deviceList = new ArrayList<>();

        for (int i = 0; i < devices.length(); i++) {
            deviceList.add(devices.getJSONObject(i));
        }

        return deviceList;
    }

    public static void insertPerformanceRecordsToDatabase(Context context, JSONArray data) throws JSONException {
        List<JSONObject> devices = getActiveDevices(data);

        DeviceDataProvider dataProvider = new DeviceDataProvider(context);

        long timestamp = new Date().getTime() - SharedPreferenceManager.getLong(context, "reference_timestamp");

        for (JSONObject device : devices) {
            String macAddress = device.getString(MAC_ADDRESS);
            String deviceRate = HG633_LAN_RATE;
            int rssi = 0;

            if (device.getString(LAYER_2_INTERFACE).contains("SSID")) {
                deviceRate = device.getString(DEVICE_RATE);
                rssi = device.getInt(RSSI);
            }

            dataProvider.insertNewPerformanceRecord(timestamp, macAddress, rssi, deviceRate);
        }

        dataProvider.close();
    }
}
