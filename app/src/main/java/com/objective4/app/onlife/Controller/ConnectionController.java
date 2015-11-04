package com.objective4.app.onlife.Controller;

import com.objective4.app.onlife.Activities.ActivityMain;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;


public class ConnectionController {
    private String json = "";

    // Get json from url
    public JSONObject makeHttpRequest(JSONObject params) throws Exception {

        HttpURLConnection urlConnection = null;

        // Making HTTP request
        try {
            // request method is POST
            URL url = new URL(ActivityMain.SERVER_URL);
            urlConnection = (HttpURLConnection) url.openConnection();

            // Connection parameters
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json");
            OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");

            // Se manda al server con un write, convirtiendo el hashmap con valores a mandar a un string formateado.
            osw.write(params.toString());//URLEncoder.encode(String.valueOf(params), "UTF-8"));
            //String val = URLEncoder.encode(String.valueOf(params),"UTF-8");
            osw.flush();
            osw.close();
            InputStream is = new BufferedInputStream(urlConnection.getInputStream());

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"), 8);
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                is.close();
                json = sb.toString();
            } catch (Exception ignored) {}

        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            assert urlConnection != null;
            urlConnection.disconnect();
        }

        // return JSON String
        return new JSONObject(json);
    }
}