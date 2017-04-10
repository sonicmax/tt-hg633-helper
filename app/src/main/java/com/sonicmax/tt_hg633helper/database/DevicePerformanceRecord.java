package com.sonicmax.tt_hg633helper.database;

/**
 * Contains device performance data obtained from HostInfo endpoint.
 */
public class DevicePerformanceRecord {
    private final long mTimestamp;
    private final String mMacAddress;
    private final int mRssi;
    private final String mDeviceRate;

    public DevicePerformanceRecord(long timestamp, String mac, int rssi, String deviceRate) {
        mTimestamp = timestamp;
        mMacAddress = mac;
        mRssi = rssi;
        mDeviceRate = deviceRate;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public String getMacAddress() {
        return mMacAddress;
    }

    public int getRssi() {
        return mRssi;
    }

    public String getDeviceRate() {
        return mDeviceRate;
    }
}
