package com.sonicmax.tt_hg633helper.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.sonicmax.tt_hg633helper.database.AppDataContract.DeviceEntry;
import com.sonicmax.tt_hg633helper.database.AppDataContract.DevicePerformanceEntry;

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

        String createDevicePerformanceTable = "CREATE TABLE " + DevicePerformanceEntry.TABLE_NAME + " (" +
                DevicePerformanceEntry._ID + " INTEGER PRIMARY KEY, " +
                DevicePerformanceEntry.COLUMN_TIMESTAMP + " INTEGER NOT NULL, " +
                DevicePerformanceEntry.COLUMN_MAC_ADDRESS + " TEXT NOT NULL, " +
                DevicePerformanceEntry.COLUMN_DEVICE_RSSI + " INTEGER NOT NULL, " +
                DevicePerformanceEntry.COLUMN_DEVICE_RATE + " TEXT NOT NULL  );";

        sqLiteDatabase.execSQL(createDeviceTable);
        sqLiteDatabase.execSQL(createDevicePerformanceTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        onCreate(sqLiteDatabase);
    }
}