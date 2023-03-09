package com.example.inventairelol.Service;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
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
                    //Suppression du chargement
                    progressDialog.dismiss();

                    break;
                }
                //Si regsiter on appel la fonction register
                case "register": {


                    //On se connecte à la base de données
                    connect();
                    //Vérification si la connexion est valide
                    if (!isDbConnected()) {
                        //Suppression du chargement
                        progressDialog.dismiss();
                        return "Error : Not Connected";
                    }
                    //Récupération du mail, pseudo, et password
                    String mail = strings[1];
                    String pseudo = strings[2];
                    String password = strings[3];

                    //On vérifie que le password n'est pas vide
                    if (password.isEmpty()) {
                        //Suppression du chargement
                        progressDialog.dismiss();
                        return "Fail : Password Empty";
                    }
                    //On vérifie que le mail n'est pas vide
                    else if (mail.isEmpty()) {
                        //Suppression du chargement
                        progressDialog.dismiss();
                        return "Fail : Mail Empty";
                    }
                    //On vérifie que le pseudo n'est pas vide
                    else if (pseudo.isEmpty()) {
                        //Suppression du chargement
                        progressDialog.dismiss();
                        return "Fail : Pseudo Empty";
                    }
                    //On vérifie que le mail et le pseudo ne sont pas utilisés
                    else if (areMailOrPseudoUsed(mail, pseudo)[0] || areMailOrPseudoUsed(mail, pseudo)[1]) {
                        //Suppression du chargement
                        progressDialog.dismiss();
                        return "Fail : Mail or Pseudo already used";
                        //Pas d'échecs ni d'erreurs
                    } else {
                        register(pseudo, mail, password);
                        //Suppression du chargement
                        progressDialog.dismiss();
                        return "Success : Register Done";
                    }



                }
                //Si login appel de la méthode login
                case "login": {

                    connect();
                    if (!isDbConnected()) {
                        progressDialog.dismiss();
                        return "Error : Not Connected";
                    }
                    //Récupération du pseudo et du password
                    String pseudo = strings[1];
                    String password = strings[2];

                    //On vérifie que le password n'est pas vide
                    if (password.isEmpty()) {
                        //Suppression du chargement
                        progressDialog.dismiss();
                        return "Fail : Password Empty";
                    }
                    //On vérifie que le pseudo n'est pas vide
                    else if (pseudo.isEmpty()) {
                        //Suppression du chargement
                        progressDialog.dismiss();
                        return "Fail : Pseudo Empty";
                    }

                    //Aucuns des deux n'est vide
                    else {
                        progressDialog.dismiss();
                        if (login(pseudo, password)) {
                            return "Success : Login Done";
                        }
                        return "Fail : Login Failed";
                    }

                }
            }


        } catch (Exception e) {
            e.printStackTrace();
            return "Error : An Error Occured";
        }
        return "";
    }

    private boolean connect() {
        try {

            //Exécution de la connection dans un nouveau Thread
            Thread t = new Thread(() -> {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    Log.i("url", url);
                    connection = DriverManager.getConnection(url, user, pass);
                    Log.i("connected", String.valueOf(isDbConnected()));
                } catch (SQLException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
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
                return rs.getString(1);
            }

            //Si une erreur à lieu, on retourne un String null

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public boolean login(String pseudo, String password) {

        try {
            return checkHash(password, pseudo);

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    //Méthode pour l'enregistrement d'un nouvel utilisateur
    public int register(String pseudo, String mail, String password) {

        int res = 0;

        try {

            //On prépare la requête
            PreparedStatement stmt = connection.prepareStatement("insert into user (mail, pseudo, password) values (?,?,?)");
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
