package com.example.inventairelol.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.Bundle;

import com.example.inventairelol.DataBase.SQLiteBDDHelper;
import com.example.inventairelol.R;
import com.example.inventairelol.Service.OnlineMYSQL;

import java.io.InputStream;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {

    SQLiteBDDHelper bddHelper;
    OnlineMYSQL onlineMYSQL;
    boolean isConnected;
    private static String hostname;
    private static String port;
    private static String database;
    private static String username;
    private static String password;
    private static String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {



            isConnected = false;

            //Récupération du fichier de configuration de la base de données
            Properties p = new Properties();
            AssetManager assetManager = getApplicationContext().getAssets();
            InputStream inputStream = assetManager.open("config.properties");
            p.load(inputStream);

            //Récupération des paramétres de configurations de la base de données via le fichier config
            this.hostname = p.getProperty("hostname");
            this.port = p.getProperty("port");
            this.database = p.getProperty("database");
            this.username = p.getProperty("username");
            this.password = p.getProperty("password");
            this.url = "jdbc:mysql://" + hostname + ":" + port + "/" + database;

            //Puis instanciation de la variable de connexion et connexion
            onlineMYSQL = new OnlineMYSQL(this, url, username, password);
            onlineMYSQL.execute("connect");





        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(R.layout.activity_main);
    }
}