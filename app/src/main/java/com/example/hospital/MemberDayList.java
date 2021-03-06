package com.example.hospital;

/**
 * Created by User on 2015/10/14.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by User on 2015/5/22.
 */

public class MemberDayList {

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = "memberDayList.db";
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_MDAYLIST_TABLE = "memberDayList";
        public static final String DATABASE_SELECTED_TABLE = "selectedDayList";
        public static final String DATABASE_CREATE_MDAYLIST = "CREATE table memberDayList("
                + "_sn INTEGER PRIMARY KEY, "
                + "_division TEXT, "
                + "_doctor TEXT, "
                + "_year TEXT, "
                + "_month TEXT, "
                + "_day TEXT, "
                + "_time TEXT, "
                + "_num INTEGER" + "); ";
        public static final String DATABASE_CREATE_SELECTED = "CREATE table selectedDayList("
                + "_sn INTEGER PRIMARY KEY, "
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
            db.execSQL(DATABASE_CREATE_MDAYLIST);
            db.execSQL(DATABASE_CREATE_SELECTED);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_MDAYLIST_TABLE);
            db.execSQL("DROP TABLE IF EXISTS " + DATABASE_SELECTED_TABLE);
            onCreate(db);

        }
    }

    private static final String TAG = MemberInfo.class.getSimpleName();

    private Context mContext = null;
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    public static final String KEY_SN = "_sn";
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
        return db.query(DatabaseHelper.DATABASE_MDAYLIST_TABLE, strCol, null, null,
                null, null, KEY_DIVISION + " DESC");
    }

    public Long setSpinnerSelected(int sn) {
        String[] strCol = new String[]{KEY_SN ,KEY_DIVISION, KEY_DOCTOR, KEY_YEAR, KEY_MONTH, KEY_DAY, KEY_TIME, KEY_NUM};

        String whereClause = "_sn = ?";
        String[] whereArgs = new String[] {
                Integer.toString(sn)
        };

        Cursor cursor = db.query(DatabaseHelper.DATABASE_MDAYLIST_TABLE, strCol, whereClause, whereArgs, null, null, KEY_DIVISION + " DESC");

        cursor.moveToFirst();

        ContentValues args = new ContentValues();
        try {
            args.put(KEY_SN, cursor.getInt(0));
            args.put(KEY_DIVISION, cursor.getString(1));
            args.put(KEY_DOCTOR, cursor.getString(2));
            args.put(KEY_YEAR, cursor.getString(3));
            args.put(KEY_MONTH, cursor.getString(4));
            args.put(KEY_DAY, cursor.getString(5));
            args.put(KEY_TIME, cursor.getString(6));
            args.put(KEY_NUM, Integer.parseInt(cursor.getString(7)));

            db.execSQL("delete from " + DatabaseHelper.DATABASE_SELECTED_TABLE);
            return db.insert(DatabaseHelper.DATABASE_SELECTED_TABLE, null, args);
        }catch(CursorIndexOutOfBoundsException e)
        {
            e.printStackTrace();
            return null;
        }



    }

    public Cursor getSpinnerSelected()
    {
        String[] strCol = new String[]{KEY_SN ,KEY_DIVISION, KEY_DOCTOR, KEY_YEAR, KEY_MONTH, KEY_DAY, KEY_TIME, KEY_NUM};


        return db.query(DatabaseHelper.DATABASE_SELECTED_TABLE, strCol, null, null,
                null, null, KEY_DIVISION + " DESC");
    }

    public Long insert(String[] data) {

        ContentValues args = new ContentValues();
        args.put(KEY_SN, data[0]);
        args.put(KEY_DIVISION, data[1]);
        args.put(KEY_DOCTOR, data[2]);
        args.put(KEY_YEAR, data[3]);
        args.put(KEY_MONTH, data[4]);
        args.put(KEY_DAY, data[5]);
        args.put(KEY_TIME, data[6]);
        args.put(KEY_NUM, Integer.parseInt(data[7]));

        Log.v(TAG, "Insert DayList Done!!");
        return db.insert(DatabaseHelper.DATABASE_MDAYLIST_TABLE, null, args);

    }




    public void deleteOldDAyList() {
        db.execSQL("delete from " + DatabaseHelper.DATABASE_MDAYLIST_TABLE);
        Log.v(TAG, "Delete old DayList Done!!");
    }
}