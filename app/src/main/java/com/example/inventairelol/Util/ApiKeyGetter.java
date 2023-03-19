package com.example.inventairelol.Util;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class ApiKeyGetter {

    Context context;
    String apiKey;
    String FILENAME = "apiKey";

    public ApiKeyGetter(Context context) {
        this.context = context;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            fos.write(apiKey.getBytes(StandardCharsets.UTF_8));
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getApiKey() {
        try {
            FileInputStream inputStream = context.openFileInput(FILENAME);
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String line = null;
            StringBuilder stringBuilder =  new StringBuilder();

            while ((line = br.readLine()) != null){
                stringBuilder.append(line);
            }

            this.apiKey = stringBuilder.toString();
            inputStream.close();
            return apiKey;

        }catch (Exception e){
            e.printStackTrace();
        }
        return apiKey;
    }
}
