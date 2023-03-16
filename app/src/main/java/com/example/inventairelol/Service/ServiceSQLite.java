package com.example.inventairelol.Service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class ServiceSQLite extends AsyncTask<String, Void, String> {

    Context context;
    ProgressDialog progressDialog;

    public ServiceSQLite(Context context){
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


    }

    @Override
    protected String doInBackground(String... strings) {
        try {



            return "";
        } catch (Exception e) {
            progressDialog.dismiss();
            return "";
        }
    }
}
