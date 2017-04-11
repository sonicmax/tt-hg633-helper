package com.sonicmax.tt_hg633helper.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sonicmax.tt_hg633helper.database.AppDataContract.DeviceEntry;
import com.sonicmax.tt_hg633helper.database.AppDataContract.WifiPerformanceEntry;
import com.sonicmax.tt_hg633helper.database.AppDataContract.LanPerformanceEntry;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "router.db";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String createDeviceTable = "CREATE TABLE " + DeviceEntry.TABLE_NAME + " (" +

                DeviceEntry._ID + " INTEGER PRIMARY KEY, " +
                DeviceEntry.COLUMN_TIMESTAMP + " INTEGER NOT NULL, " +
                DeviceEntry.COLUMN_MAC_ADDRESS + " TEXT NOT NULL, " +
                DeviceEntry.COLUMN_HOST_NAME + " TEXT NOT NULL, " +
                DeviceEntry.COLUMN_IP_ADDRESS + " TEXT NOT NULL, " +
                DeviceEntry.COLUMN_VENDOR_CLASS_ID + " TEXT NOT NULL, " +
                DeviceEntry.COLUMN_LAYER_2_INTERFACE + " REAL NOT NULL  );";

        String createWifiPerformanceTable = "CREATE TABLE " + WifiPerformanceEntry.TABLE_NAME + " (" +
                WifiPerformanceEntry._ID + " INTEGER PRIMARY KEY, " +
                WifiPerformanceEntry.COLUMN_TIMESTAMP + " INTEGER NOT NULL, " +
                WifiPerformanceEntry.COLUMN_MAC_ADDRESS + " TEXT NOT NULL, " +
                WifiPerformanceEntry.COLUMN_DEVICE_RSSI + " INTEGER NOT NULL, " +
                WifiPerformanceEntry.COLUMN_DEVICE_RATE + " TEXT NOT NULL  );";

        String createLanPerformanceTable = "CREATE TABLE " + LanPerformanceEntry.TABLE_NAME + " (" +
                LanPerformanceEntry._ID + " INTEGER PRIMARY KEY, " +
                LanPerformanceEntry.COLUMN_TIMESTAMP + " INTEGER NOT NULL, " +
                LanPerformanceEntry.COLUMN_PORT + " TEXT NOT NULL, " +
                LanPerformanceEntry.COLUMN_MAC_ADDRESS + " TEXT NOT NULL, " +
                LanPerformanceEntry.COLUMN_BYTES_SENT + " INTEGER NOT NULL, " +
                LanPerformanceEntry.COLUMN_BYTES_RECEIVED + " INTEGER NOT NULL  );";

        sqLiteDatabase.execSQL(createDeviceTable);
        sqLiteDatabase.execSQL(createWifiPerformanceTable);
        sqLiteDatabase.execSQL(createLanPerformanceTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onCreate(sqLiteDatabase);
    }
}