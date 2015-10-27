package com.example.hospital;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

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
    private Button f_Dlist_btn;
    private TextView mid_input_txt;
    private TextView mid_txt;
    private TextView imei_txt;
    private TextView lastUpdate_txt;
    private TextView dayListLastUpdate_txt;

    private ProgressBar loading_aninmation;
    Spinner dListSpinner;



    private boolean hasLocalData=false;

    private MemberInfo memberInfo;
    private MemberDayList memberDayList;

    String id;
    String imei;
    String lastUpdate;
    String dayListLastUpdate;


    private void initViews() {
        confirm_btn = (Button) findViewById(R.id.confirm_btn);
        f_Dlist_btn = (Button) findViewById(R.id.f_Dlist_btn);

        mid_input_txt = (TextView) findViewById(R.id.mid_input);
        mid_txt = (TextView) findViewById(R.id.mid);
        imei_txt = (TextView) findViewById(R.id.imei);
        lastUpdate_txt = (TextView) findViewById(R.id.lastupdate);
        dayListLastUpdate_txt = (TextView) findViewById(R.id.dlist_lastupdate);

        loading_aninmation = (ProgressBar) findViewById(R.id.loading_animation);
        loading_aninmation.setVisibility(View.INVISIBLE);
        dListSpinner = (Spinner) findViewById(R.id.dlist_spinner);

    }

    private void setListeners() {
        confirm_btn.setOnClickListener(confirm);
        f_Dlist_btn.setOnClickListener(fDlist);
        dListSpinner.setOnItemSelectedListener(spinner);

    }

    private View.OnClickListener confirm = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String currentDateandTime = sdf.format(new Date());

            TelephonyManager mTelManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
            imei = mTelManager.getDeviceId(); //get imei
            String[] info = {mid_input_txt.getText().toString(), imei, currentDateandTime, dayListLastUpdate};
            Log.v(TAG, "Current Time: " + currentDateandTime);

            insertMemberInfo(info);
        }
    };

    private View.OnClickListener fDlist = new View.OnClickListener(){
        @Override
        public void onClick(View v) {
            new fetchData().execute();
        }
    };

    private AdapterView.OnItemSelectedListener spinner = new AdapterView.OnItemSelectedListener(){
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            Log.v(TAG,"spinner positin : "+position);
            memberDayList.setSpinnerSelected(position);

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

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
            new fetchData().execute();
        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }



    private void setAdapter() {
        memberInfo = new MemberInfo(this);
        memberInfo.open();

        memberDayList =  new  MemberDayList(this);
        memberDayList.open();

        ArrayAdapter adapter = setCategoryArray();
        dListSpinner.setAdapter(adapter);
    }

    private void insertMemberInfo(String[] data) {

        String[] insertData = new String[4];

        insertData[0] = data[0];//member id
        insertData[1] = data[1];//imei
        insertData[2] = data[2];//last update time
        insertData[3] = data[3];//daylist last update time

        memberInfo.delete(1);
        memberInfo.insert(insertData);

        getMemberInfo();

    }

    private void getMemberInfo() {
        Cursor cursor = memberInfo.getAll();
        int row_num = cursor.getCount();
        if (row_num != 0) {
            hasLocalData = true;
            cursor.moveToFirst();

            id = cursor.getString(1);
            imei = cursor.getString(2);
            lastUpdate = cursor.getString(3);
            dayListLastUpdate = cursor.getString(4);

            Log.v(TAG, "LastUpdate:" + lastUpdate);

            this.mid_txt.setText(id);
            this.mid_input_txt.setText(id);

            this.imei_txt.setText(imei);
            this.lastUpdate_txt.setText(lastUpdate);
            this.dayListLastUpdate_txt.setText(dayListLastUpdate);

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
        protected void onPreExecute(){
            super.onPreExecute();
            f_Dlist_btn.setEnabled(false);
            loading_aninmation.setVisibility(View.VISIBLE);

        }

        @Override
        protected String doInBackground(String... params) {

            HashMap<String, String> postDataParams = new HashMap<String, String>();

            postDataParams.put("id", id);
            postDataParams.put("imei", imei);

            String result = getInfo(postURL, postDataParams);
            try {

                memberDayList.deleteOldDAyList();

                JSONArray resultJSON = new JSONArray(result);

                for(int i=0; i<resultJSON.length(); i++)
                {
                    String[] tempDListData = new String[8];
                    tempDListData[0]=String.valueOf(i);
                    tempDListData[1]=resultJSON.getJSONObject(i).getString("division");
                    tempDListData[2]=resultJSON.getJSONObject(i).getString("doctor");
                    tempDListData[3]=resultJSON.getJSONObject(i).getString("year");
                    tempDListData[4]=resultJSON.getJSONObject(i).getString("month");
                    tempDListData[5]=resultJSON.getJSONObject(i).getString("day");
                    tempDListData[6]=resultJSON.getJSONObject(i).getString("time");
                    tempDListData[7]=resultJSON.getJSONObject(i).getString("num");
                    memberDayList.insert(tempDListData);
                }


            }catch(JSONException e){

            }
            Log.v(TAG, "result: "+result + "\nend");
            return result;
        }

        @Override
        protected void onPostExecute(String result)
        {
            try{
                if(Integer.parseInt(result)==-1)
                {
                    Toast.makeText(getApplication(), "您在資料庫中尚未建立資料", Toast.LENGTH_LONG).show();
                    mid_txt.setText("您尚未建立資料");
                }
                else if(Integer.parseInt(result)==-2)
                {
                    Toast.makeText(getApplication(), "您尚未綁定手機", Toast.LENGTH_LONG).show();
                }
            }catch(Exception e){
                e.printStackTrace();
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
            String currentDateandTime = sdf.format(new Date());

            if(checkNetworkStatus())
            {
                dayListLastUpdate = currentDateandTime;
                memberInfo.updateDlistTime(currentDateandTime);
                dayListLastUpdate_txt.setText(dayListLastUpdate);
                Log.v(TAG, "DlistLastUpdate:" + dayListLastUpdate);

                ArrayAdapter adapter = setCategoryArray();
                dListSpinner.setAdapter(adapter);
            }
            else
            {
                dayListLastUpdate_txt.setText(dayListLastUpdate + "\n(無法更新)");
                Toast.makeText(getApplication(), "目前沒有網路連線\n無法更新資料", Toast.LENGTH_LONG).show();
            }

            f_Dlist_btn.setEnabled(true);
            loading_aninmation.setVisibility(View.INVISIBLE);

        }

    }

    private ArrayAdapter setCategoryArray(){

        ArrayAdapter< String> adapter =new ArrayAdapter< String>( this,R.layout.my_pinner);
        adapter.setDropDownViewResource(R.layout.my_pinner);

        Cursor cursor = memberDayList.getAll();
        if(cursor.moveToFirst())
        {
            while(cursor.isAfterLast() == false)
            {
                String temp = "";

                temp += DivisionToString.translate(cursor.getInt(1)) + " - ";
                temp += cursor.getString(2);
                temp += cursor.getString(3) + "年";
                temp += cursor.getString(4) + "月";
                temp += cursor.getString(5) + "日 - ";
                temp += cursor.getString(7) + "號";

                adapter.add(temp);

                cursor.moveToNext();
            }
        }
//        adapter.add("兼職");
        return adapter;
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

    private boolean checkNetworkStatus()
    {
        ConnectivityManager cm;
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        Log.v(TAG, "isConnected: " + isConnected);
        return isConnected;
    }
}

