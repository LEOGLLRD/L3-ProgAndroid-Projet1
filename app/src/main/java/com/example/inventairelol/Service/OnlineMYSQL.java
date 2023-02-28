package com.example.inventairelol.Service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OnlineMYSQL extends AsyncTask<String, Void, String> {

    ProgressDialog progressDialog;

    String url, user, pass;
    Connection connection;
    Context context;

    public OnlineMYSQL(Context context, String url, String user, String pass) {
        this.url = url;
        this.user = user;
        this.pass = pass;
        this.context = context;

    }

    @Override
    protected void onPreExecute() {

        progressDialog = ProgressDialog.show(context, "Chargement", "En attente de connexion avec le serveur");
        progressDialog.show();


    }

    @Override
    protected void onPostExecute(String s) {

        Log.i("In", "in");
        progressDialog.dismiss();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {

            String directiv = strings[0];
            Log.i("directiv", directiv);

            switch (directiv) {
                case "connect": {
                    connect();
                    break;
                }
                case "register": {
                    String mail = strings[1];
                    String pseudo = strings[2];
                    String password = strings[3];
                    break;


                }
                case "login": {
                    break;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }
        return String.valueOf(isDbConnected());
    }

    private boolean connect() {
        try {

            Class.forName("com.mysql.jdbc.Driver");
            Log.i("url", url);
            Log.i("user", user);
            Log.i("pass", pass);


            Thread t = new Thread(() -> {
                try {
                    connection = DriverManager.getConnection(url, user, pass);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            t.start();
            t.join(10000);


            Log.i("info", "succes");
            Log.i("conn", connection.toString());

            return true;


        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //Fonction permettant de vérifier si la connexion entre la base de données,
    //et l'application a bien été établi
    public boolean isDbConnected() {
        final String query = "SELECT 1";
        boolean isConnected = false;
        try {
            final PreparedStatement statement = connection.prepareStatement(query);
            isConnected = true;

        } catch (SQLException | NullPointerException e) {
            return false;
        }
        return isConnected;
    }


}
