package com.example.inventairelol.Service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.annotation.NonNull;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OnlineMYSQL extends AsyncTask<String, Void, String>{

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

    ///////////////////////////

    protected OnlineMYSQL(Parcel in) {
        url = in.readString();
        user = in.readString();
        pass = in.readString();
    }

    //////////////////////////////////

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
                    Log.i("connected ?", String.valueOf(isDbConnected()));
                    break;
                }
                //Si regsiter on appel la fonction register
                case "register": {

                    connect();
                    if (!isDbConnected()) {
                        return "Error : Not Connected";

                    }

                    String mail = strings[1];
                    String pseudo = strings[2];
                    String password = strings[3];

                    //On vérifie que le password n'est pas vide
                    if (password == "") {
                        return "Fail : Password Empty";
                    }
                    //On vérifie que le mail et le pseudo ne sont pas utilisés
                    if (areMailOrPseudoUsed(mail, pseudo)[0] || areMailOrPseudoUsed(mail, pseudo)[1]) {
                        return "Fail : Mail or Pseudo already used";
                    } else {
                        register(pseudo, mail, password);
                        return "Success : Register Done";
                    }



                }
                //Si login appel de la méthode login
                case "login": {
                    connect();
                    if (!isDbConnected()) {
                        break;
                    }
                    break;
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            return "false";
        }
        return String.valueOf(isDbConnected());
    }


    /////////////////////////////


    //Tente une connexion avec la base de données
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
            //Si le Thread prend plus de 10sec à se terminer on le kill
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


    //Méthode qui hash un mot de passe et le retourne
    public String hash(String password) {
        try {

            return BCrypt.hashpw(password, BCrypt.gensalt(12));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    //Méthode vérifiant si un mot de passe candidat correspond au mot de passe hashé d'un utilisateur
    public boolean checkHash(String candidate, String pseudo) {
        try {

            return BCrypt.checkpw(candidate, getHashedPasswordFromPseudo(pseudo));

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    //Méthode retournant le mot passe hashé d'un utilisateur en fonction du pseudo
    public String getHashedPasswordFromPseudo(String pseudo) {

        try {

            //On prépare la requête
            PreparedStatement stmt = connection.prepareStatement("select password from user where pseudo = ?");
            //On ajoute le pseudo à la requête
            stmt.setString(1, pseudo);
            //On execute la requête
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                return rs.getString(0);
            }

            //Si une erreur à lieu, on retourne un String null
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    //Méthode pour l'enregistrement d'un nouvel utilisateur
    public int register(String pseudo, String mail, String password) {

        int res = 0;

        try {

            //On prépare la requête
            PreparedStatement stmt = connection.prepareStatement("insert into user (mail, pseudo, password) values (?,?,?)");

            Log.i("mail", mail);
            Log.i("pseudo", pseudo);
            Log.i("pass", password);

            //On ajoute les paramètres
            stmt.setString(1, mail);
            stmt.setString(2, pseudo);
            stmt.setString(3, hash(password));
            //On execute la requête
            res = stmt.executeUpdate();

            //Si une erreur à lieu, on retourne un String null
        } catch (SQLException e) {
            e.printStackTrace();
            return res;
        } catch (Exception e) {
            e.printStackTrace();
            return res;
        }
        return res;

    }

    //Méthode retournant un tableau de 2 boolean :
    //le premier nous dis si le mail est déja utilisé
    //le deuxième nous dis si pseudo est déjà utilisé
    public boolean[] areMailOrPseudoUsed(String mail, String pseudo) {
        return new boolean[]{isMailUsed(mail), isPseudoUsed(pseudo)};
    }

    //Méthode retournant si le mail est déjà utilisé
    public boolean isMailUsed(String mail) {

        try {
            //On prépare la requête
            PreparedStatement stmt = connection.prepareStatement("select mail from user where mail = ?");
            //On ajoute le mail à la requête
            stmt.setString(1, mail);
            //On execute la requête
            ResultSet rs = stmt.executeQuery();
            //Si on a un résultat alors le mail est déjà utilisé
            //On retourne vrai
            if (rs.next()) return true;
                //Sinon faux
            else return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }

    }

    //Méthode retournant si le pseudo est déjà utilisé
    public boolean isPseudoUsed(String pseudo) {
        try {
            //On prépare la requête
            PreparedStatement stmt = connection.prepareStatement("select pseudo from user where pseudo = ?");
            //On ajoute le pseudo à la requête
            stmt.setString(1, pseudo);
            //On execute la requête
            ResultSet rs = stmt.executeQuery();
            //Si on a un résultat alors le pseudo est déjà utilisé
            //On retourne vrai
            if (rs.next()) return true;
                //Sinon faux
            else return false;
        } catch (Exception e) {
            e.printStackTrace();
            return true;
        }
    }


}
