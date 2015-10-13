package com.example.hospital;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;


public class NFCReportActivity extends Activity {
    private static final String TAG = NFCReportActivity.class.getSimpleName();

    private Button confirm_btn;
    private TextView mid_input_txt;
    private TextView mid_txt;
    private TextView imei_txt;
    private TextView lastUpdate_txt;

    private boolean hasLocalData=false;

    String mid;
    String imei;
    String lastUpdate;

    private void initViews() {
        confirm_btn = (Button) findViewById(R.id.confirm_btn);
        mid_input_txt = (TextView) findViewById(R.id.mid_input);
        mid_txt = (TextView) findViewById(R.id.mid);
        imei_txt = (TextView) findViewById(R.id.imei);
        lastUpdate_txt = (TextView) findViewById(R.id.lastupdate);

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
            imei = mTelManager.getDeviceId(); //get imei

            String[] info = {mid_input_txt.getText().toString(), imei, currentDateandTime};

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
        if(hasLocalData)
        {

        }
       new fetchData().execute();
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
            hasLocalData = true;
            cursor.moveToFirst();

            long rowid = cursor.getLong(0);

            mid = cursor.getString(1);
            imei = cursor.getString(2);
            lastUpdate = cursor.getString(3);

            Log.v(TAG, "ROWID: " +rowid);
            Log.v(TAG, "MID: " + mid);
            Log.v(TAG, "IMEI:  " + imei);
            Log.v(TAG, "DateTime:" + lastUpdate);

            this.mid_txt.setText(mid);
            this.imei_txt.setText(imei);
            this.lastUpdate_txt.setText(lastUpdate);

//			currentCondition_txt.setText(cursor.getString(3));
//			humidity_txt.setText(cursor.getString(4));
//			currentTemperature_txt.setText(cursor.getString(5));
//			reliability_txt.setText(cursor.getDouble(6) + "%");
//			lastUpdate_txt.setText(cursor.getString(7));
        }
        else
        {
                        Toast.makeText(getApplication(), "尚未建立資料\n請先輸入身分證字號以建立資料", Toast.LENGTH_LONG).show();
        }
    }


    String postURL = "http://140.136.150.92/fetchDBInfo.php";

    private class fetchData extends AsyncTask<String, Void, String>
    {

        @Override
        protected String doInBackground(String... params) {

            HashMap<String, String> postDataParams = new HashMap<String, String>();

            postDataParams.put("id", "a123456789");
            postDataParams.put("imei", "123");


            String result = getInfo(postURL, postDataParams);
            Log.v(TAG, "result: "+result + "\nend");
            return null;
        }


    }

    private String getInfo(String requestURL,
                           HashMap<String, String> postDataParams)
    {
        URL url;//140.136.150.92/fetchDBInfo.php
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);


            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getPostDataString(postDataParams));

            writer.flush();
            writer.close();
            os.close();
            int responseCode=conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br=new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line=br.readLine()) != null) {
                    response+=line;
                }
            }
            else {
                response="";

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }

    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;
        for(Map.Entry<String, String> entry : params.entrySet()){
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
        }

        return result.toString();
    }
}

