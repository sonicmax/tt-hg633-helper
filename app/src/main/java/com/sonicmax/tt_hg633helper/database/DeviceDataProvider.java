package com.sonicmax.tt_hg633helper.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.sonicmax.tt_hg633helper.database.AppDataContract.DeviceEntry;
import com.sonicmax.tt_hg633helper.database.AppDataContract.DevicePerformanceEntry;

/**
 * Provides various methods to aid with querying/inserting to device databases
 */

public class DeviceDataProvider {
    private final String LOG_TAG = DeviceDataProvider.class.getSimpleName();

    private final Context mContext;
    private final DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    public DeviceDataProvider(Context context) {
        this.mContext = context;
        mDbHelper = new DatabaseHelper(mContext);
    }

    public DeviceDataProvider open() throws SQLException {
        try {
            mDb = mDbHelper.getReadableDatabase();
        }
        catch (SQLException e) {
            Log.e(LOG_TAG, "Error opening database:", e);
        }
        return this;
    }

    public void close() {
        mDbHelper.close();
    }

    /**
     * Queries device database for all records and returns Cursor over the result set.
     * Results include all fields defined in AppDataContract.DeviceEntry
     * @return Cursor
     */
    public Cursor getAllRecords() {
        String orderBy = DeviceEntry.COLUMN_TIMESTAMP + " ASC";

        return mDb.query(DeviceEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                orderBy);
    }

    /**
     * Queries device performance database for all records and returns Cursor over the result set.
     * Results include timestamp, MAC address, RSSI, and device rate.
     *
     * @return Cursor
     */
    public Cursor getAllPerformanceRecords() {
        String[] columns = {DevicePerformanceEntry.COLUMN_TIMESTAMP, DevicePerformanceEntry.COLUMN_MAC_ADDRESS,
                DevicePerformanceEntry.COLUMN_DEVICE_RSSI, DevicePerformanceEntry.COLUMN_DEVICE_RATE};

        String orderBy = DevicePerformanceEntry.COLUMN_TIMESTAMP + " ASC";

        return mDb.query(DevicePerformanceEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                orderBy);
    }

    /**
     * Queries device database for performance records associated with given MAC address and
     * returns Cursor over the result set. Results include timestamp, RSSI and device rate.
     *
     * @param macAddress MAC address of device for query
     * @return Cursor
     */
    public Cursor getAllPerformanceRecordsForDevice(String macAddress) {
        String[] columns = {DevicePerformanceEntry.COLUMN_TIMESTAMP,
                DevicePerformanceEntry.COLUMN_DEVICE_RSSI, DevicePerformanceEntry.COLUMN_DEVICE_RATE};
        String where = DevicePerformanceEntry.COLUMN_MAC_ADDRESS + " = ?";
        String[] whereClause = {macAddress};
        String orderBy = DevicePerformanceEntry.COLUMN_TIMESTAMP + " ASC";

        return mDb.query(DevicePerformanceEntry.TABLE_NAME,
                columns,
                where,
                whereClause,
                null,
                null,
                orderBy);
    }

    public void insertNewPerformanceRecord(long timestamp, String macAddress, int rssi, String deviceRate) {
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DevicePerformanceEntry.COLUMN_TIMESTAMP, timestamp);
        values.put(DevicePerformanceEntry.COLUMN_MAC_ADDRESS, macAddress);
        values.put(DevicePerformanceEntry.COLUMN_DEVICE_RSSI, rssi);
        values.put(DevicePerformanceEntry.COLUMN_DEVICE_RATE, deviceRate);

        db.insert(DevicePerformanceEntry.TABLE_NAME, null, values);
        db.close();
    }
}
