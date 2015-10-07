package com.example.hospital;

import android.content.ContentValues;

import android.util.Log;




import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by User on 2015/10/7.
 */
public class FetchDBInfo {
    private static final String TAG = FetchDBInfo.class.getSimpleName();

    private String addr = "140.136.150.92";
    private String id;
    private String imei;

    //flag 0 means get and 1 means post.(By default it is get.)
    public FetchDBInfo(String id ,String imei) {
        this.id = id;
        this.imei = imei;
    }

    public String getInfo () {

        try {

            String link = "http://" + addr + "/fetchDBInfo.php";

            URL url = new URL(link);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setReadTimeout(5000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);

            ContentValues params = new ContentValues();
            params.put("id", id);
            params.put("imei", imei);

            OutputStream os = conn.getOutputStream();
            BufferedWriter writer = new BufferedWriter(
                    new OutputStreamWriter(os, "UTF-8"));
            writer.write(getQuery(params));
            writer.flush();
            writer.close();
            os.close();

            Log.v(TAG, "result: testst");

            conn.connect();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return "";
    }

    private String getQuery(ContentValues params) throws UnsupportedEncodingException
    {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (Map.Entry<String, Object> entry : params.valueSet())
        {
            if (first)
                first = false;
            else
                result.append("&");

            result.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
        }


        Log.v(TAG, "result: " + result.toString());
        return result.toString();
    }
}

