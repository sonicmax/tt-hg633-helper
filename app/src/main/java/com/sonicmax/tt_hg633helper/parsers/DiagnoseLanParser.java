package com.sonicmax.tt_hg633helper.parsers;

import android.content.Context;
import android.util.Log;

import com.sonicmax.tt_hg633helper.database.LanDeviceDataProvider;
import com.sonicmax.tt_hg633helper.database.WifiDeviceDataProvider;
import com.sonicmax.tt_hg633helper.utilities.SharedPreferenceManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * Provides various methods to manipulate the JSON response from HostInfo endpoint.
 */

public class DiagnoseLanParser {
    private static String LOG_TAG = "HostInfoParser";
    private static String MAC_ADDRESS = "MACAddress";
    private static final String STATUS = "Status";
    private static final String UP = "Up";
    private static final String HOSTS = "Hosts";
    private static final String NAME = "Name";
    private static final String BYTES_RECEIVED = "BytesReceived";
    private static final String BYTES_SENT = "BytesSent";

    public static List<JSONObject> convertToList(JSONArray ports) throws JSONException {
        List<JSONObject> portList = new ArrayList<>();

        for (int i = 0; i < ports.length(); i++) {
            portList.add(ports.getJSONObject(i));
        }

        return portList;
    }

    public static List<JSONObject> getActivePorts(JSONArray ports) throws JSONException {
        List<JSONObject> activePorts = new ArrayList<>();

        for (int i = 0; i < ports.length(); i++) {
            JSONObject device = ports.getJSONObject(i);
            if (device.getString(STATUS).equals(UP)) {
                activePorts.add(device);
            }
        }

        return activePorts;
    }

    public static void insertPerformanceRecordsToDatabase(Context context, JSONArray data) throws JSONException {
        List<JSONObject> activePorts = getActivePorts(data);

        LanDeviceDataProvider dataProvider = new LanDeviceDataProvider(context);

        long timestamp = new Date().getTime() - SharedPreferenceManager.getLong(context, "reference_timestamp");

        for (JSONObject port : activePorts) {
            String lanPort = port.getString(NAME);
            String macAddress = port.getJSONArray(HOSTS).getJSONObject(0).getString(MAC_ADDRESS);
            int sent = port.getInt(BYTES_SENT);
            int received = port.getInt(BYTES_RECEIVED);

            dataProvider.insertNewPerformanceRecord(timestamp, lanPort, macAddress, sent, received);
        }

        dataProvider.close();
    }
}
