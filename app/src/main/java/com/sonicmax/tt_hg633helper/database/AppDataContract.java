package com.sonicmax.tt_hg633helper.database;

import android.provider.BaseColumns;

public class AppDataContract {
    public static final class WifiPerformanceEntry implements BaseColumns {
        public static final String TABLE_NAME = "wifi_performance";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_MAC_ADDRESS = "mac";
        public static final String COLUMN_DEVICE_RATE = "rate";
        public static final String COLUMN_DEVICE_RSSI = "signal";
    }

    public static final class LanPerformanceEntry implements BaseColumns {
        public static final String TABLE_NAME = "lan_performance";
        public static final String COLUMN_PORT = "lan_port";
        public static final String COLUMN_MAC_ADDRESS = "mac";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_BYTES_SENT = "bytes_sent";
        public static final String COLUMN_BYTES_RECEIVED = "bytes_received";
    }

    public static final class DeviceEntry implements BaseColumns {
        public static final String TABLE_NAME = "device";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_MAC_ADDRESS = "mac";
        public static final String COLUMN_HOST_NAME = "hostname";
        public static final String COLUMN_IP_ADDRESS = "ip";
        public static final String COLUMN_VENDOR_CLASS_ID = "vendor";
        public static final String COLUMN_LAYER_2_INTERFACE = "l2i";
    }
}
