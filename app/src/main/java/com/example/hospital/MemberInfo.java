package com.example.hospital;

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

public class MemberInfo {

    private static class DatabaseHelper extends SQLiteOpenHelper {

        public static final String DATABASE_NAME = "memberInfo.db";
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_TABLE = "memberInfo";
        public static final String DATABASE_CREATE = "CREATE table memberInfo("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT, "
                + "_mid TEXT, "
                + "_imei TEXT, "
                + "_last_update TEXT, "
                + "_daylist_last_update TEXT" + " ); ";

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

    public static final String KEY_ROWID = "_rowid";
    public static final String KEY_MID = "_id";
    public static final String KEY_IMEI = "_imei";
    public static final String KEY_LASTUP = "_last_update";
    public static final String KEY_DLASTUP = "_daylist_last_update";

    public MemberInfo(Context context) {
        this.mContext = context;
    }

    public MemberInfo open() throws SQLException {
        dbHelper = new DatabaseHelper(mContext);
        db = dbHelper.getWritableDatabase();

        return this;
    }

    public void close() {
        dbHelper.close();
    }

    public Cursor getAll() {
        String[] strCol = new String[]{KEY_ROWID, KEY_MID, KEY_IMEI, KEY_LASTUP, KEY_DLASTUP};
        return db.query(DatabaseHelper.DATABASE_TABLE, strCol, null, null,
                null, null, KEY_ROWID + " DESC");
    }

    public Long insert(String[] data) {
        ContentValues args = new ContentValues();
        args.put(KEY_ROWID, 1);
        args.put(KEY_MID, data[0]);
        args.put(KEY_IMEI, data[1]);
        args.put(KEY_LASTUP, data[2]);
        args.put(KEY_DLASTUP, data[3]);

        Log.v(TAG, "Insert MemberInfo Done!!");

        return db.insert(DatabaseHelper.DATABASE_TABLE, null, args);

    }

    public int updateDlistTime(String data) {
        ContentValues args = new ContentValues();
        args.put(KEY_DLASTUP, data);

        Log.v(TAG, data);
        Log.v(TAG, "Update DListTime Done!!");

        return db.update(DatabaseHelper.DATABASE_TABLE, args, KEY_ROWID + " = " + 1, null);

    }

    public boolean delete(long rowId) {

        Log.v(TAG, "Delete Done!!");

        return db.delete(DatabaseHelper.DATABASE_TABLE,
                KEY_ROWID + "=" + rowId, null) > 0;

    }
}