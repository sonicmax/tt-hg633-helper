package com.sonicmax.tt_hg633helper.database;

import android.provider.BaseColumns;

public class AppDataContract {
    public static final class DevicePerformanceEntry implements BaseColumns {
        public static final String TABLE_NAME = "device_performance";
        public static final String COLUMN_TIMESTAMP = "timestamp";
        public static final String COLUMN_MAC_ADDRESS = "mac";
        public static final String COLUMN_DEVICE_RATE = "rate";
        public static final String COLUMN_DEVICE_RSSI = "signal";
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
