package com.objective4.app.onlife.Controller;

import org.apache.http.client.ClientProtocolException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;


public class ConnectionController {
    private InputStream is = null;
    private JSONObject jObj = null;
    private String json = "";
    private URL url;

    // constructor
    public ConnectionController() {

    }

    // function get json from url
    // by making HTTP POST or GET mehtod
    public JSONObject makeHttpRequest(String file,
                                      HashMap params) throws Exception {

        HttpURLConnection urlConnection = null;
        // Making HTTP request
        try {
            // request method is POST
            url = new URL("http://104.236.74.55/onlife/" + file);
            urlConnection = (HttpURLConnection) url.openConnection();

            // Poner parametros para la conexion
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            OutputStream os = new BufferedOutputStream(urlConnection.getOutputStream());
            OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
            // Se manda al server con un write, convirtiendo el hashmap con valores a mandar a un string formateado.
            osw.write(getPostDataString(params));
            osw.flush();
            osw.close();
            is = new BufferedInputStream(urlConnection.getInputStream());

            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
                StringBuilder sb = new StringBuilder();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    sb.append(line + "\n");
                }
                is.close();
                json = sb.toString();
            } catch (Exception e) {
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            urlConnection.disconnect();
        }

        // try parse the string to a JSON object
        jObj = new JSONObject(json);

        // return JSON String
        return jObj;
    }
    private String getPostDataString(HashMap<String, String> params) throws UnsupportedEncodingException{
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