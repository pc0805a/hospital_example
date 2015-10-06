package com.example.hospital;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import de.grundid.hcedemo.R;

public class NFCReportActivity extends Activity {
    private static final String TAG = NFCReportActivity.class.getSimpleName();

    private Button confirm_btn;
    private TextView mid_input;
    private TextView mid;
    private TextView imei;
    private TextView lastUpdate;

    private void initViews() {
        confirm_btn = (Button) findViewById(R.id.confirm_btn);
        mid_input = (TextView) findViewById(R.id.mid_input);
        mid = (TextView) findViewById(R.id.mid);
        imei = (TextView) findViewById(R.id.imei);
        lastUpdate = (TextView) findViewById(R.id.lastupdate);

    }

    private void setListeners() {
        confirm_btn.setOnClickListener(confirm);
    }

    private View.OnClickListener confirm = new View.OnClickListener(){
        @Override
        public void onClick(View v) {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String currentDateandTime = sdf.format(new Date());

            TelephonyManager mTelManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            //String imei = mTelManager.getDeviceId(); //get imei

            String[] info = {mid_input.getText().toString(), mTelManager.getDeviceId(), currentDateandTime};

            Log.v(TAG, "Current Time: " + currentDateandTime);

            insertMemberInfo(info);

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nfcreport);
        initViews();
        setListeners();
        setAdapter();
        getMemberInfo();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private MemberInfo meberInfo;
    private Cursor mCursor;

    private void setAdapter() {
        meberInfo = new  MemberInfo(this);
        meberInfo.open();
    }

    private void insertMemberInfo(String[] data) {

        String[] insertData = new String[3];

        insertData[0] = data[0];//member id
        insertData[1] = data[1];//imei
        insertData[2] = data[2];//last update time

        meberInfo.delete(1);
        meberInfo.insert(insertData);
        getMemberInfo();

    }

    private void getMemberInfo() {
        Cursor cursor = meberInfo.getAll();
        int row_num = cursor.getCount();
        if (row_num != 0) {
            cursor.moveToFirst();

            long rowid = cursor.getLong(0);

            String mid = cursor.getString(1);
            String imei = cursor.getString(2);
            String lastUpdate = cursor.getString(3);

            Log.v(TAG, "ROWID: " +rowid);
            Log.v(TAG, "MID: " + mid);
            Log.v(TAG, "IMEI:  " + imei);
            Log.v(TAG, "DateTime:" + lastUpdate);

//            Toast.makeText(getApplication(), "ROWID: " + rowid+
//                                             "\nMID: " + mid +
//                                             "\nIMEI:  " + imei +
//                                             "\nDateTime:" + lastUpdate
//                                            , Toast.LENGTH_LONG).show();



            this.mid.setText(mid);
            this.imei.setText(imei);
            this.lastUpdate.setText(lastUpdate);

//			currentCondition_txt.setText(cursor.getString(3));
//			humidity_txt.setText(cursor.getString(4));
//			currentTemperature_txt.setText(cursor.getString(5));
//			reliability_txt.setText(cursor.getDouble(6) + "%");
//			lastUpdate_txt.setText(cursor.getString(7));
        }
    }
}
