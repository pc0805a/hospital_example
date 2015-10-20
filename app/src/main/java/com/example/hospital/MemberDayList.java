package com.example.hospital;

/**
 * Created by User on 2015/10/14.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by User on 2015/5/22.
 */
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MemberDayList {

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = "memberDayList.db";
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_TABLE = "memberDayList";
        public static final String DATABASE_CREATE = "CREATE table memberDayList("
                + "_sn INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "_division TEXT, "
                + "_doctor TEXT, "
                + "_year TEXT, "
                + "_month TEXT, "
                + "_day TEXT, "
                + "_time TEXT, "
                + "_num INTEGER" + "); ";

        public DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
            onCreate(db);

        }
    }

    private static final String TAG = MemberInfo.class.getSimpleName();

    private Context mContext = null;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public static final String KEY_SN = "_SN";
    public static final String KEY_DIVISION = "_division";
    public static final String KEY_DOCTOR = "_doctor";
    public static final String KEY_YEAR = "_year";
    public static final String KEY_MONTH = "_month";
    public static final String KEY_DAY = "_day";
    public static final String KEY_TIME= "_time";
    public static final String KEY_NUM = "_num";

    public MemberDayList(Context context) {
        this.mContext = context;
    }

    public MemberDayList open() throws SQLException {
        dbHelper = new DatabaseHelper(mContext);
        db = dbHelper.getWritableDatabase();

        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public Cursor getAll() {
        String[] strCol = new String[]{ KEY_SN ,KEY_DIVISION, KEY_DOCTOR, KEY_YEAR, KEY_MONTH, KEY_DAY, KEY_TIME, KEY_NUM};
        return db.query(DatabaseHelper.DATABASE_TABLE, strCol, null, null,
                null, null, KEY_DIVISION + " DESC");
    }

    public Cursor getSpecific(String division, String doctor, String year, String month, String day, String time, String num)
    {
        String[] strCol = new String[]{KEY_SN ,KEY_DIVISION, KEY_DOCTOR, KEY_YEAR, KEY_MONTH, KEY_DAY, KEY_TIME, KEY_NUM};

        String whereClause = "_division = ? AND _doctor = ? AND _year = ? AND _month = ? AND _day = ? AND _time = ? AND _num = ?";
        String[] whereArgs = new String[] {
                division,
                doctor,
                year,
                month,
                day,
                time,
                num
        };

        return db.query(DatabaseHelper.DATABASE_TABLE, strCol, null, whereArgs,
                null, null, KEY_DIVISION + " DESC");
    }

    public Long insert(String[] data) {

        ContentValues args = new ContentValues();
        args.put(KEY_DIVISION, data[0]);
        args.put(KEY_DOCTOR, data[1]);
        args.put(KEY_YEAR, data[2]);
        args.put(KEY_MONTH, data[3]);
        args.put(KEY_DAY, data[4]);
        args.put(KEY_TIME, data[5]);
        args.put(KEY_NUM, Integer.parseInt(data[6]));

        Log.v(TAG, "Insert DayList Done!!");

        return db.insert(DatabaseHelper.DATABASE_TABLE, null, args);

    }


    public void deleteOldDAyList() {
        db.execSQL("delete from " + DatabaseHelper.DATABASE_TABLE);
        Log.v(TAG, "Delete old DayList Done!!");
    }
}