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


    //Méthode appelée avant le processus long
    @Override
    protected void onPreExecute() {

        //Affichage d'un écran de chargement avant exécution du processus long
        progressDialog = ProgressDialog.show(context, "Chargement", "En attente de connexion avec le serveur");
        progressDialog.show();


    }

    //Méthode appelée après exécution du processus long
    @Override
    protected void onPostExecute(String s) {


        //Suppression du chargement
        progressDialog.dismiss();
    }

    @Override
    protected String doInBackground(String... strings) {
        try {

            //On récupère l'action à réaliser
            String directiv = strings[0];


            switch (directiv) {
                //Si "connect"
                case "connect": {
                    //On se connect à la BDD
                    connect();
                    break;
                }
                //Si regsiter on appel la fonction register
                case "register": {
                    String mail = strings[1];
                    String pseudo = strings[2];
                    String password = strings[3];
                    break;


                }
                //Si login appel de la méthode login
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

            //Récupération du driver
            Class.forName("com.mysql.jdbc.Driver");

            //Exécution de la connection dans un nouveau Thread
            Thread t = new Thread(() -> {
                try {
                    connection = DriverManager.getConnection(url, user, pass);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            });
            //Démarrage du Thread
            t.start();
            //Si le Thread prend plus de 10sec à se termniner on le kill
            t.join(10000);
            
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
