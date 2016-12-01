package com.google.research.ic.alogger;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by tsung on 2016/11/30.
 */

public class PHPUtilities implements URLConstants {

    private Context context;
    private List<String> mCookies;

    public PHPUtilities(Context context) {
        this.context = context;
    }

    private String initData(String key, String value) throws UnsupportedEncodingException {
        return URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
    }

    private String appendData(String data, String key, String value) throws UnsupportedEncodingException {
        return data + "&" + URLEncoder.encode(key, "UTF-8") + "=" + URLEncoder.encode(value, "UTF-8");
    }

    private String sendBuffer(String url, String data) throws Exception {
        URLConnection conn = new URL(url).openConnection();
        conn.setDoOutput(true);
        OutputStreamWriter wr = new OutputStreamWriter(
                conn.getOutputStream());
        wr.write(data);
        wr.flush();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream()));

        mCookies = conn.getHeaderFields().get("Set-Cookie");
        StringBuilder sb = new StringBuilder();
        String line;
        // Read Server Response
        while ((line = reader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    public String sendUserLog(String user, String log) throws Exception {
        String data = initData("user_name", user);
        data = appendData(data, "user_log", log);
        data = appendData(data, "time",  DateFormat.getDateTimeInstance().format(new Date()));
        Log.e("ALoggerService", DateFormat.getDateTimeInstance().format(new Date()));
        return sendBuffer(SERVER_ID + URL_SEND_USER_LOG, data);
    }
}
