package com.example.inventairelol.Util;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;


public class ApiLol {

    public static void main(String[] args) throws Exception {
        int id = 0;
        URL url = new URL("http://jsonplaceholder.typicode.com/posts/" + id);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
// Enable output for the connection.
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
        conn.setRequestProperty("Accept", "application/json");
// Set HTTP request method.
        conn.setRequestMethod("GET");
        Log.i("values","oui");


    }
}
