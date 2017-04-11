package com.sonicmax.tt_hg633helper.charting;

import android.database.Cursor;

import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import com.sonicmax.tt_hg633helper.database.AppDataContract.WifiPerformanceEntry;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helps to display the response from deviceinfo endpoint in a chart by keeping track of data sets
 * for each device and performing some UI modifications to the LineChart.
 */

public class WifiDeviceChartHelper {
    private final String LAYER_2_INTERFACE = "Layer2Interface";
    private final String DEVICE_RATE = "AssociatedDeviceRate";
    private final String MAC_ADDRESS = "MACAddress";
    private final String RSSI = "Rssi";

    private final int HG_633_LAN_RATE = 100;
    private final int FULL_SIGNAL = 0;

    private final Map<String, List<Entry>> mDeviceRates;
    private final Map<String, List<Entry>> mDeviceSignals;

    private boolean mHasMultipleEntries = false;

    public WifiDeviceChartHelper() {
        mDeviceRates = new HashMap<>();
        mDeviceSignals = new HashMap<>();
    }

    public void getEntriesFromResponse(JSONObject device, long time) throws JSONException {
        String macAddress = device.getString(MAC_ADDRESS);

        if (mDeviceRates.get(macAddress) == null) {
            mDeviceRates.put(macAddress, new ArrayList<Entry>());
        }

        if (mDeviceSignals.get(macAddress) == null) {
            mDeviceSignals.put(macAddress, new ArrayList<Entry>());
        }

        int deviceRate = HG_633_LAN_RATE;
        int deviceSignal = FULL_SIGNAL;

        if (device.getString(LAYER_2_INTERFACE).contains("SSID")) {
            deviceRate = Integer.parseInt(device.getString(DEVICE_RATE).replace(" Mbps", ""));
            deviceSignal = device.getInt(RSSI);
        }

        mDeviceRates.get(macAddress).add(new Entry(time, deviceRate));
        mDeviceSignals.get(macAddress).add(new Entry(time, convertDbmToPercentage(deviceSignal)));
    }

    public void getEntryFromCursor(Cursor cursor) {
        String macAddress = cursor.getString(cursor.getColumnIndex(WifiPerformanceEntry.COLUMN_MAC_ADDRESS));
        long timestamp = cursor.getLong(cursor.getColumnIndex(WifiPerformanceEntry.COLUMN_TIMESTAMP));
        int deviceRate = Integer.parseInt(cursor.getString(cursor.getColumnIndex(WifiPerformanceEntry.COLUMN_DEVICE_RATE))
                .replace(" Mbps", ""));

        int rssi = cursor.getInt(cursor.getColumnIndex(WifiPerformanceEntry.COLUMN_DEVICE_RSSI));

        if (mDeviceRates.get(macAddress) == null) {
            mDeviceRates.put(macAddress, new ArrayList<Entry>());
        }

        if (mDeviceSignals.get(macAddress) == null) {
            mDeviceSignals.put(macAddress, new ArrayList<Entry>());
        }

        mDeviceRates.get(macAddress).add(new Entry(timestamp, deviceRate));
        mDeviceSignals.get(macAddress).add(new Entry(timestamp, convertDbmToPercentage(rssi)));
    }

    private int convertDbmToPercentage(int rssi) {
        if (rssi <= -100)
            return 0;
        else if (rssi >= -50)
            return 100;
        else
            return 2 * (rssi + 100);
    }

    public ILineDataSet getDeviceRateDataSet(JSONObject device) throws JSONException {
        String macAddress = device.getString(MAC_ADDRESS);

        LineDataSet rateDataSet = new LineDataSet(mDeviceRates.get(macAddress), "Device rate (Mbps)");
        int color = ColorTemplate.JOYFUL_COLORS[0];
        rateDataSet.setColor(color);
        rateDataSet.setDrawFilled(true);
        rateDataSet.setFillColor(color);
        rateDataSet.setFillAlpha(50);
        rateDataSet.setDrawValues(false);
        rateDataSet.setDrawCircles(false);

        return rateDataSet;
    }

    public ILineDataSet getSignalDataSet(JSONObject device) throws JSONException {
        String macAddress = device.getString(MAC_ADDRESS);

        LineDataSet signalDataSet = new LineDataSet(mDeviceSignals.get(macAddress), "Signal (%)");
        int color = ColorTemplate.JOYFUL_COLORS[1];
        signalDataSet.setColor(color);
        signalDataSet.setDrawFilled(true);
        signalDataSet.setFillColor(color);
        signalDataSet.setFillAlpha(50);
        signalDataSet.setDrawValues(false);
        signalDataSet.setDrawCircles(false);

        return signalDataSet;
    }
}
