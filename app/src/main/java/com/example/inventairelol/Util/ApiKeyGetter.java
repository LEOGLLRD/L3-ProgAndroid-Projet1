package com.example.inventairelol.Util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.example.inventairelol.Activities.Login;
import com.example.inventairelol.Activities.Register;
import com.example.inventairelol.R;

import java.io.BufferedReader;
import java.io.File;
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

            File file = context.getFileStreamPath(FILENAME);

            if (file == null || !file.exists()){
                //On créer une Alerte pour informer l'utilisateur
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.cannotReachAPI)
                        .setCancelable(false)
                        .setPositiveButton(R.string.understood, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(context, Login.class);
                                context.startActivity(intent);
                                ((Activity)context).finish();
                                Toast.makeText(context, R.string.understood, Toast.LENGTH_SHORT).show();
                            }
                        });

                builder.setMessage(R.string.checkAPIKey);
                builder.show();

            }

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
